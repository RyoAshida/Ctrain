package com.example.ctrain;

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
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {
	
	private final static String TAG = "MainActivity";
	private BluetoothAdapter btAdapter = null;
    private BroadcastReceiver btSearchReceiver = null;
    private final static int REQUEST_ENABLE_BT = 1111;
    
    private ActionBar actionBar;
    
    public static int btCount = 0;
    
    private Tab tab1;
    private Tab tab2;
    
	@SuppressLint("CommitTransaction")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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
            Toast.makeText(this, "Bluetooth is not supported", Toast.LENGTH_SHORT).show();
            finish();
        }
	}
	
	@Override
	protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        if (!btAdapter.isEnabled())
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                    REQUEST_ENABLE_BT);
        else
            setupBT();
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
                    actionBar.selectTab(tab2);
                    actionBar.selectTab(tab1);
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

}
