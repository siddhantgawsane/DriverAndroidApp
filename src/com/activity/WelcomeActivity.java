package com.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.gauge.R;
import com.util.AndroidConstants;
import com.util.HttpView;
import com.util.StringHelper;


public class WelcomeActivity extends CommonActivity  {
	protected int _splashTime = 8000;
	public static String TAG = "WelcomeActivity",value="";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcomescreen);
		toast("Fetching Bluetooth Devices");
		
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	
			splashTread.start();
	
	}
	Thread splashTread = new Thread() {
		@Override
		public void run() {
			try {
				sleep(_splashTime);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
				// do nothing
			} finally {
				go(BluetoothDeviceActivity.class);
		
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case RESULT_SETTINGS:
			go(WelcomeActivity.class);
			
			
			break;

		}

	}

}
