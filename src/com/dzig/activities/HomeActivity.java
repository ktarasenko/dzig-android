package com.dzig.activities;

import com.dzig.R;
import com.dzig.location.LocationService;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class HomeActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);		
	}
	@Override
	protected void onResume() {
		super.onResume();
		startService(new Intent(this, LocationService.class));
	}
	
	@Override
	protected void onPause() {
		stopService(new Intent(this, LocationService.class));
		super.onPause();
	}
}
