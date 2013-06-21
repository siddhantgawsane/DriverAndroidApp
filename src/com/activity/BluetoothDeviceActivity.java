package com.activity;


import java.util.Set;

import com.gauge.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;



public class BluetoothDeviceActivity extends Activity {
  TextView out;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.devices);



    // Getting the Bluetooth adapter
    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
//    out.append("\nAdapter: " + adapter);

    // Check for Bluetooth support in the first place 
    // Emulator doesn't support Bluetooth and will return null
    if(adapter==null) { 
//      out.append("\nBluetooth NOT supported. Aborting.");
      return;
    }
    
    // Starting the device discovery
//    out.append("\nStarting discovery...");
    adapter.startDiscovery();
//    out.append("\nDone with discovery...");

    // Listing paired devices
//    out.append("\nDevices Pared:");
    devices = adapter.getBondedDevices();
    String devi[]=new String[devices.size()];
    final String deviids[]=new String[devices.size()];
    int i=-1;
    for (BluetoothDevice device : devices) {
    	devi[++i]=device.getName();
    	deviids[i]=device.getAddress();
    }
    ListView lv=((ListView)findViewById(R.id.listView1));
    lv.setAdapter(new ArrayAdapter<String>(BluetoothDeviceActivity.this, android.R.layout.simple_list_item_1, devi));
    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    		Intent intent=new Intent(BluetoothDeviceActivity.this,BluetoothClientActivity.class);
    		intent.putExtra("NAME", deviids[(int) arg3]);
    		startActivity(intent);
    	}
	});
  }
  
  
  
  Set<BluetoothDevice> devices =null;
}