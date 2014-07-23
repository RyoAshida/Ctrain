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
				.setText("tab1!")
				.setTabListener(new TabListener<Tab1> (this, "tab1", Tab1.class));
		actionBar.addTab(tab1);
		tab2 = actionBar.newTab()
				.setText("tab2!!")
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
	
	private double[][] yamanote = {{35.6926678760627,35.65682986717963,139.695703089233,139.70840603112754},
			{35.54279441468406,35.51345673479633,140.2534309029537,140.2860465645748},
			{35.60911386999346,35.59976266291856,139.67991024255332,139.6876350045162}};
	private double[][] chuo = {};
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
