package com.activity;

import com.gauge.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class CommonActivity extends Activity {

	public void refresh() {

	}

	
	public void toast(String message) {
		System.out.println(message);
		Toast t = Toast.makeText(CommonActivity.this, message, 1000);
		t.show();
	}

	public static boolean serviceRunning = false;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.mainmenu, menu);
		return true;
	}
	
	public void finished() {
		try {
			System.runFinalizersOnExit(true);
			finish();
			super.finish();
			super.onDestroy();
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			android.os.Process.killProcess(android.os.Process.myPid());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public  String getIMEI(){
		  TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	        String imei=telephonyManager.getDeviceId();
	        System.out.println("Device IMEI is "+imei);
	        return imei;
	        
	}
	
	public void go(Class c){
		Intent i=new Intent(getApplicationContext(),c);
		startActivity(i);
				
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.pref:
			Intent i = new Intent(this, QuickPrefsActivity.class);
			startActivityForResult(i, RESULT_SETTINGS);
			break;

		}

		return true;
	}
	public static final int RESULT_SETTINGS = 1;
}
