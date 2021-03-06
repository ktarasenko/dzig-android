package com.dzig.activities;

import android.view.*;
import android.widget.Toast;
import com.dzig.R;
import com.dzig.actionbar.ActionBarActivity;
import com.dzig.location.LocationService;
import com.dzig.utils.Logger;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class HomeActivity extends ActionBarActivity {
	boolean isMultipane;
	View legendContainer;
	View openLegendButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		legendContainer = findViewById(R.id.legend_container);
		openLegendButton = findViewById(R.id.open_legend_button);
		isMultipane = legendContainer != null && openLegendButton != null;
		
		if (isMultipane) {
			openLegendButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					legendContainer.setVisibility(View.VISIBLE);
					openLegendButton.setVisibility(View.GONE);
				}
			});
		}
	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && isMultipane && legendContainer.getVisibility() == View.VISIBLE){
			legendContainer.setVisibility(View.GONE);
			openLegendButton.setVisibility(View.VISIBLE);
	        return true;
		}
		return super.dispatchKeyEvent(event);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		ComponentName serviceName = startService(new Intent(this, LocationService.class));
		Logger.info("HomeActivity", serviceName + " started");
	}
	
	@Override
	protected void onPause() {
		stopService(new Intent(this, LocationService.class));
		super.onPause();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        // Calling super after populating the menu is necessary here to ensure that the
        // action bar helpers have a chance to handle this event.
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
             case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
