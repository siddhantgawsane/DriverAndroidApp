package com.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gauge.Gauge;
import com.gauge.R;
import com.util.HttpView;
import com.util.OBDModel;
import com.util.StringHelper;

public class BluetoothClientActivity extends CommonActivity {
	// TextView out;

	// Gauge Code For Meter
	Gauge meter1;
	Gauge meter2;
	Gauge meter3;
	Timer timer;
	private static final int RESULT_SETTINGS = 1;
	public static boolean  met_speed=false ;
	public static boolean  met_temperature=false ;
	public static boolean  met_rpm=false ;
	public static String  sys_ip="";
	public static String  sys_port="";
	
	// Bluetooth Data Adapter
	private static final int REQUEST_ENABLE_BT = 1;
	private static BluetoothAdapter btAdapter = null;
	private BluetoothSocket btSocket = null;
	private InputStream inStream = null;
	LocationManager lm = null;
	LocationSensor ls = null;
	// Well known SPP UUID
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static final String TAG = "test for function";

	// Insert your server's MAC address 001F E2 DF55C3
	private static String address = "00:1F:E2:DF:55:C3";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		
		
		showUserSettings();
		
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		ls = new LocationSensor();
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1f,
				ls);

		
		
		// out = (TextView) findViewById(R.id.out);
		String deviceAddress = StringHelper.n2s(getIntent().getStringExtra("NAME"));
		timer = null;
		
		meter1 = (Gauge) findViewById(R.id.meter1);
		meter2 = (Gauge) findViewById(R.id.meter2);
		meter3 = (Gauge) findViewById(R.id.meter3);
		et1 = (TextView) findViewById(R.id.editText1);
		et2 = (TextView) findViewById(R.id.editText2);
		et3 = (TextView) findViewById(R.id.editText3);
	
		
				
		
		
		
		if (deviceAddress.length() > 0)
			address = deviceAddress;
		// out.append("\n...In onCreate()");
		((Button) findViewById(R.id.button1))
				.setOnClickListener(new View.OnClickListener() {

					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						try {
							btAdapter = BluetoothAdapter.getDefaultAdapter();
							CheckBTState();

							final String imei = getIMEI();
							if (imei != null) {

								if (timer == null)
									timer = new Timer();
								timer.schedule(new TimerTask() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										runOnUiThread(new Runnable() {

											public void run() {
												// TODO Auto-generated method
												// stub
												long id = System
														.currentTimeMillis();
												System.out
														.println("Timer Started "
																+ id
																+ " "
																+ new Date());

												getData(imei);
												System.out
														.println("Timer Ended"
																+ id + " "
																+ new Date());
											}
										});

									}
								}
								, 1000, 5000);

							} else {
								toast("Invalid IMEI!");
							}
						} catch (Exception e) {
							Log.e("ConnectTest2", e.getMessage());
							e.printStackTrace();
						}
					}
				});

	}

	@Override
	public void onStart() {
		super.onStart();
		// out.append("\n...In onStart()");
	}

	@Override
	public void onResume() {
		super.onResume();
		
		if(met_temperature){
		meter3.setVisibility(View.VISIBLE);
		System.out.println("Temp Set Visible");
		}else{
			meter3.setVisibility(View.GONE);
			System.out.println("Temp Set InVisible");
		}
		
		if(met_rpm){
		meter2.setVisibility(View.VISIBLE);
		System.out.println("RPM Set Visible");
		}else{
			meter2.setVisibility(View.GONE);
			System.out.println("RPM Set InVisible");
		}
		
		
		if(met_speed){
		meter1.setVisibility(View.VISIBLE);
		System.out.println("Speed Set Visible");
		}else if(!met_speed){
			meter1.setVisibility(View.GONE);
			System.out.println("RPM Set InVisible");
		}
		

	}

	

	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case RESULT_SETTINGS:
			showUserSettings();
			
			
			break;

		}

	}

	
	private void showUserSettings() {
		try{
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		met_speed= sharedPrefs.getBoolean("speed_gauge", false);
		met_rpm= sharedPrefs.getBoolean("rpm_gauge", false);
		met_temperature= sharedPrefs.getBoolean("temp_gauge", false);
		System.out.println("met_speed> " +met_speed+ " met_rpm> " +met_rpm+ " met_temperature> "+met_temperature);
		sys_ip=sharedPrefs.getString("ip", "null");
		sys_port=sharedPrefs.getString("port", "null");
		System.out.println("IP " +sys_ip+ " PORT " +sys_port);
		StringBuilder builder = new StringBuilder();
		/*builder.append("\n" + sharedPrefs.getBoolean("speed_gauge", false));
		builder.append("\n" + sharedPrefs.getBoolean("rpm_gauge", false));
		builder.append("\n" + sharedPrefs.getBoolean("temp_gauge", false));*/
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	public void getData(final String imei) {
		// out.append("\n...In onResume...\n...Attempting client connect...");
		Log.v("TAG", "Test UUID");
		// Set up a pointer to the remote node using it's address.

		// Two things are needed to make a connection:
		// A MAC address, which we got above.
		// A Service ID or UUID. In this case we are using the
		// UUID for SPP.
		try {
			BluetoothDevice device = btAdapter.getRemoteDevice(address);
			btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);

		} catch (IOException e) {
			Log.v("ConnectTest2", "Test UUID");
			AlertBox("Fatal Error", "In onResume() And Socket Create Failed: "
					+ e.getMessage() + ".");
		}

		// Discovery is resource intensive. Make sure it isn't going on
		// when you attempt to connect and pass your message.
		btAdapter.cancelDiscovery();
		Log.v("ConnectTest4", "Read Object");
		// Establish the connection. This will block until it connects.
		try {
			btSocket.connect();
			Log.v("ConnectTest3", "Read Object");
			// out.append("\n...Connection established and data link opened...");
		} catch (IOException e) {
			try {
				btSocket.close();
			} catch (IOException e2) {
				Log.v("ConnectTest3", "Read Object");
				AlertBox("Fatal Error",
						"In onResume() and unable to close socket during connection failure"
								+ e2.getMessage() + ".");
			}
		}

		// Create a data stream so we can talk to server.
		// out.append("\n...Sending message to server...");

		try {
			inStream = btSocket.getInputStream();
		} catch (IOException e) {
			AlertBox(
					"Fatal Error",
					"In onResume() and output stream creation failed:"
							+ e.getMessage() + ".");
		}

		// String message = "Hello from Android.\n";
		
		Object o;
		try {
			Log.v("ConnectTest2", "About to Read Object");
			System.out.println("About to Read Object");
			ObjectInputStream ois = new ObjectInputStream(inStream);
			try {
				System.out.println("Reading Object");
				o = ois.readObject();
				ois.close();
				
				
				System.out.println("Reading Object => " + o);
				// out.append(o.toString());
				if (o != null) {
					final OBDModel obd = (OBDModel) o;
					final double vss = obd.getVss();
					final double rpm = obd.getRpm();
					final double temp = obd.getTemp();
					System.out.println("Reading Object => " + o);
					System.out.println("vss " + vss);
					System.out.println("rpm => " + rpm);
					System.out.println("temp => " + temp);
					runOnUiThread(new Runnable() {
						
						public void run() {
							// TODO Auto-generated method stub
							meter1.setValue((float) vss);
							meter2.setValue((float) rpm / 100);
							meter3.setValue((float) temp);
							et1.setText((rpm * 10) + "");
							et2.setText(temp + "");
							et3.setText(vss + "");
							
						}
					});
					
				new Thread(){
					@Override
					public void run() {
						final double load_pct = obd.getLoad_pct();
						final double iat = obd.getIat();
						final double maf = obd.getMaf();
						final double throttlepos = obd.getThrottlepos();
						final double latsend = lat;
						final double lngsend = lng;
						sendData(load_pct, iat, maf, throttlepos, vss, rpm, temp,
								imei, latsend, lngsend);
						
					}
				}.start();
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (IOException e) {
			String msg = "In onResume() and an exception occurred during write: "
					+ e.getMessage();
			if (address.equals("00:00:00:00:00:00"))
				msg = msg
						+ ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address";
			msg = msg + ".\n\nCheck that the SPP UUID: " + MY_UUID.toString()
					+ " exists on server.\n\n";
			AlertBox("Fatal Error", msg);
		}
	}

	@Override
	public String getIMEI() {
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telephonyManager.getDeviceId();
		return imei;
	}

	public double lat = 0, lng = 0;

	class LocationSensor implements LocationListener {

		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			lat = location.getLatitude();
			lng = location.getLongitude();
			Log.v("ConnectTest2", "Got New Location " + lat + " " + lng);
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

	}

	
	public void fnVisible(View v) {
		System.out.println("Temp> " +met_temperature);
		System.out.println("Speed> " +met_speed);
		System.out.println("RPM> " +met_rpm);
		
		if(met_temperature){
			meter3.setVisibility(View.VISIBLE);
			System.out.println("Temp Set Visible");
			}else{
				meter3.setVisibility(View.GONE);
				System.out.println("Temp Set InVisible");
			}
			
			if(met_rpm){
			meter2.setVisibility(View.VISIBLE);
			System.out.println("RPM Set Visible");
			}else{
				meter2.setVisibility(View.GONE);
				System.out.println("RPM Set InVisible");
			}
			
			
			if(met_speed){
			meter1.setVisibility(View.VISIBLE);
			System.out.println("Speed Set Visible");
			}else if(!met_speed){
				meter1.setVisibility(View.GONE);
				System.out.println("RPM Set InVisible");
			}
		
		/*if(met_temperature)
		meter3.setVisibility(View.VISIBLE);
		else if(!met_temperature){
			meter3.setVisibility(View.GONE);
		}
		else if(met_rpm)
		meter2.setVisibility(View.VISIBLE);
		else if(!met_temperature){
			meter2.setVisibility(View.GONE);
		}
		else if(met_speed)
		meter1.setVisibility(View.VISIBLE);
		else if(!met_temperature){
			meter1.setVisibility(View.GONE);
		}*/
	}
	

	public void sendData(double load_pct, double iat, double maf,
			double throttlepos, double vss, double rpm, double temp,
			String imei, double latsend, double lngsend) {
		HashMap param = new HashMap();
		param.put("method", "send");
		param.put("iat", iat);
		param.put("maf", maf);
		param.put("throttlepos", throttlepos);
		param.put("load_pct", load_pct);
		param.put("vss", vss);
		param.put("rpm", rpm);
		param.put("temp", temp);
		param.put("imei", imei);
		param.put("latsend", latsend);
		param.put("lngsend", lngsend);
		HttpView.createURL(param);
	}

	@Override
	public void onPause() {
		super.onPause();

		// out.append("\n...In onPause()...");
		toast("On Pause");
		if (lm != null) {
			lm.removeUpdates(ls);
			//ls = null;
		}
		if (inStream != null) {
			try {
				inStream.close();
			} catch (IOException e) {
				AlertBox(
						"Fatal Error",
						"In onPause() and failed to flush output stream: "
								+ e.getMessage() + ".");
			}
		}

		try {
			if (btSocket != null)
				btSocket.close();
		} catch (IOException e2) {
			AlertBox("Fatal Error", "In onPause() and failed to close socket."
					+ e2.getMessage() + ".");
		}
		try {
			if (timer != null) {
				timer.cancel();
				timer = null;

			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	
	@Override
	public void onStop() {
		super.onStop();
		// out.append("\n...In onStop()...");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// out.append("\n...In onDestroy()...");
	}

	private void CheckBTState() {
		// Check for Bluetooth support and then check to make sure it is turned
		// on

		// Emulator doesn't support Bluetooth and will return null
		if (btAdapter == null) {
			AlertBox("Fatal Error", "Bluetooth Not supported. Aborting.");
		} else {
			if (btAdapter.isEnabled()) {
				// out.append("\n...Bluetooth Is Enabled...");
			} else {
				// Prompt user to turn on Bluetooth
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}
	}

	public void AlertBox(String title, String message) {
//		new AlertDialog.Builder(this).setTitle(title)
//				.setMessage(message + " Press OK to exit.")
//				.setPositiveButton("OK", new OnClickListener() {
//					public void onClick(DialogInterface arg0, int arg1) {
//						// finish();
//					}
//				}).show();
	}

	public void fnExit(View v) {
		finished();

	}

	TextView et1;
	TextView et2;
	TextView et3;
}