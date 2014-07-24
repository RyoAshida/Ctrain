package com.example.ctrain;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements LocationListener,
															GooglePlayServicesClient.ConnectionCallbacks,
															GooglePlayServicesClient.OnConnectionFailedListener {
	
	private final static String TAG = "MainActivity";
	private BluetoothAdapter btAdapter = null;
    private BroadcastReceiver btSearchReceiver = null;
    private final static int REQUEST_ENABLE_BT = 1111;
    
    private ActionBar actionBar;
    
    public static int btCount = 0;
    public static double latitude = 0;
    public static double longitude = 0;
    public static String route = "Other";
    public final static int num = 2;
    public static boolean[][] gotten = new boolean[num][4];
    public static String Yamanote = "Yamanote", Chuo = "Chuo", Other = "Other";
    public static String[] lines = {Yamanote, Chuo};
    
    private Tab tab1;
    private Tab tab2;
    
    private LocationRequest locationRequest;
    private LocationClient locationClient;
    
    
    
    
    private Map<String, Integer> map = new HashMap<String, Integer>();
    
	@SuppressLint("CommitTransaction")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		map.clear();
		for (int i = 0; i < lines.length; i++) {
			map.put(lines[i], i);
			Arrays.fill(gotten[i], false);
		}
		
		//tab setting
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		tab1 = actionBar.newTab()
				.setText("混雑状況")
				.setTabListener(new TabListener<Tab1> (this, "tab1", Tab1.class));
		actionBar.addTab(tab1);
		tab2 = actionBar.newTab()
				.setText("実績")
				.setTabListener(new TabListener<Tab2> (this, "tab2", Tab2.class));
		actionBar.addTab(tab2);
		
		//Bluetooth setting
		BluetoothManager btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        if (btAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported", Toast.LENGTH_LONG).show();
            finish();
        }
        
        //GPS setting
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 5 sec.
        locationRequest.setFastestInterval(5 * 1000); // 1 sec.
        locationClient = new LocationClient(this, this, this);
        
        
        try {
			BufferedReader br = new BufferedReader(new InputStreamReader(openFileInput("achievements.txt")));
			String str = br.readLine();
			while (str != null) {
				int n = Integer.parseInt(br.readLine());
				gotten[map.get(str)][n] = true;
				str = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        locationClient.connect();
        if (!btAdapter.isEnabled())
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                    REQUEST_ENABLE_BT);
        else
            setupBT();
    }
	
	@Override
	protected void onStop() {
		locationClient.disconnect();
		try {
			PrintWriter pr = new PrintWriter(new OutputStreamWriter(openFileOutput("achievements.txt", Context.MODE_PRIVATE)));
			for (int i = 0; i < num; i++) {
				for (int j = 0; j < 4; j++) {
					if (gotten[i][j]) {
						pr.println(lines[i]);
						pr.println(j);
					}
				}
			}
			pr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		super.onStop();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
        if (!btAdapter.isDiscovering()) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        }
        else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
        case R.id.menu_scan:
        	btCount = 0;
            btAdapter.startDiscovery();
            break;
        case R.id.menu_stop:
            btAdapter.cancelDiscovery();
            break;
        }
        return true;
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
	 public void onActivityResult(int reqCode, int resCode, Intent data) {
		switch (reqCode) {
        case REQUEST_ENABLE_BT:
            if (resCode == Activity.RESULT_OK)
                setupBT();
            else {
                Toast.makeText(this, "Bluetooh must be turned on", Toast.LENGTH_SHORT)
                        .show();
                finish();
            }
            break;
        }
        super.onActivityResult(reqCode, resCode, data);
	 }
	 
	 private void setupBT() {
        btSearchReceiver = new BroadcastReceiver() {
            @SuppressLint("CommitTransaction")
			@Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.i(TAG, "onReceive: " + action);
                if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                    btCount++;
                    if (actionBar.getSelectedTab() == tab1) {
            	        actionBar.selectTab(tab2);
            	        actionBar.selectTab(tab1);
            		}
                }
                else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                    invalidateOptionsMenu();
                }
                else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                    invalidateOptionsMenu();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(btSearchReceiver, filter);
    }

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		locationClient.requestLocationUpdates(locationRequest, this);
	}

	@Override
	public void onDisconnected() {
		locationClient.removeLocationUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		Location loc = locationClient.getLastLocation();
		latitude = loc.getLatitude();
		longitude = loc.getLongitude();
		route = getLine(latitude, longitude);
		if (!route.equals(Other)) {
			int n = map.get(route);
			int m = rank(btCount);
			if (m >= 0) gotten[n][m] = true;
		}
		if (actionBar.getSelectedTab() == tab1) {
	        actionBar.selectTab(tab2);
	        actionBar.selectTab(tab1);
		}
	}
	
	private double[][] yamanote = {{35.70242641256646,35.6583641193756,139.69158321618613,139.71046596765098},
			{35.65892202195698,35.624882832734365,139.6960464119869,139.72728878259238},
			{35.62683629306901,35.61539391794844,139.71836239099082,139.74067836999473},
			{35.644415288174535,35.616231220424325,139.7344985604244,139.74857479333457,},
			{35.65027409396115,35.64274126474853,139.74136501550254,139.75853115319785},
			{35.680398307408005,35.64274126474853,139.7509780526119,139.77054744958457},
			{35.72026750497064,35.68095605601204,139.76333767175254,139.7839370369869},
			{35.74312056743117,35.7169226043571,139.75132137536582,139.78634029626426},
			{35.7397766264453,35.7269568849404,139.71012264489707,139.77466732263144},
			{35.734203079361905,35.695177328078394,139.69089657067832,139.71321254968223},
			//for demo titech
			{35.60848583808182,35.60164694927277,139.67785030602988,139.68651920556601}};
	private double[][] chuo = {{35.70521434650313,35.68569676173465,139.40284878015098,139.71321254968223},
			{35.68597561800473,35.67914335879439,139.69965130090293,139.73295360803183},
			{35.70451737215891,35.680398307407906,139.728662073608,139.74599987268027},
			{35.70437797655885,35.69392261194051,139.74308162927207,139.77758556603965},
			//for test home
			{35.536508671998746,35.53462285313818,140.26072651147422,140.26325851678428}};
	private String getLine(double lat, double lon) {
		for (int i = 0; i < yamanote.length; i++) {
			if (yamanote[i][1] < lat && lat < yamanote[i][0] && yamanote[i][2] < lon && lon < yamanote[i][3])
				return Yamanote;
		}
		for (int i = 0; i < chuo.length; i++) {
			if (chuo[i][1] < lat && lat < chuo[i][0] && chuo[i][2] < lon && lon < chuo[i][3])
				return Chuo;
		}
		return Other;
	}
	
	public static int rank(int n) {
		if (n < 1) return -1;
		if (n < 2) return 0;
		if (n < 5) return 1;
		if (n < 10) return 2;
		return 3;
	}

}
