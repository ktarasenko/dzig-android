package com.dzig.location;

import com.dzig.R;
import com.dzig.utils.Logger;
import com.dzig.utils.UserPreferences;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

class UserLocationHelper {
	protected static final String TAG = "UserLocationHelper";

	public static boolean USE_GPS_WHEN_ACTIVITY_VISIBLE = true;

	// The default search radius when searching for places nearby.
	public static int DEFAULT_RADIUS = 150;
	// The maximum distance the user should travel between location updates.
	private int MAX_DISTANCE;
	// The maximum time that should pass before the user gets a location update.
	private long MAX_TIME;

	protected Criteria criteria;
	protected ILastLocationFinder lastLocationFinder;
	protected LocationUpdateRequester locationUpdateRequester;
	protected PendingIntent locationListenerPendingIntent;
	private Location currentLocation;
	private LocationManager locationManager;
	
	private Context context;
	
	UserLocationHelper(Context context){
		this.context = context;
        updateSettings();

		locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		
		criteria = new Criteria();
	    if (USE_GPS_WHEN_ACTIVITY_VISIBLE)
	      criteria.setAccuracy(Criteria.ACCURACY_FINE);
	    else
	      criteria.setPowerRequirement(Criteria.POWER_LOW);
	    
	    Intent activeIntent = new Intent(context, LocationChangedReceiver.class);
	    locationListenerPendingIntent = PendingIntent.getBroadcast(context, 0, activeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	    
	    lastLocationFinder = PlatformSpecificImplementationFactory.getLastLocationFinder(context);
	    lastLocationFinder.setChangedLocationListener(oneShotLastLocationUpdateListener);
	    
	    locationUpdateRequester = PlatformSpecificImplementationFactory.getLocationUpdateRequester(locationManager);
	    getLocationAndUpdateIt(true);
	}
	
	void updateSettings() {
        UserPreferences prefs = UserPreferences.newInstance(context);
        MAX_DISTANCE = prefs.getInt(context.getString(R.string.prefs_location_distance_key), DEFAULT_RADIUS/2);
        MAX_TIME = prefs.getLong(context.getString(R.string.prefs_location_interval_key), AlarmManager.INTERVAL_FIFTEEN_MINUTES);
    }


    protected BroadcastReceiver locProviderDisabledReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			boolean providerDisabled = !intent.getBooleanExtra(LocationManager.KEY_PROVIDER_ENABLED, false);
			// Re-register the location listeners using the best available Location Provider.
			if (providerDisabled)
				requestLocationUpdates();
		}
	};
		  
	/**
	 * If the best Location Provider (usually GPS) is not available when we request location
	 * updates, this listener will be notified if / when it becomes available. It calls 
	 * requestLocationUpdates to re-register the location listeners using the better Location
	 * Provider.
	 */
	protected LocationListener bestInactiveLocationProviderListener = new LocationListener() {
		public void onLocationChanged(Location l) {}
		public void onProviderDisabled(String provider) {}
		public void onStatusChanged(String provider, int status, Bundle extras) {}
		public void onProviderEnabled(String provider) {
			// Re-register the location listeners using the better Location Provider.
			requestLocationUpdates();
		}
	};
		  
		  
		  
	protected LocationListener oneShotLastLocationUpdateListener = new LocationListener() {
		public void onLocationChanged(Location l) {			
			Log.d(TAG,"onLocationChanged");
			setCurrentLocation(l);
		}
		   
	    public void onProviderDisabled(String provider) {}
	    public void onStatusChanged(String provider, int status, Bundle extras) {}
	    public void onProviderEnabled(String provider) {}
	};
	
	protected void getLocationAndUpdateIt(boolean updateWhenLocationChanges) {
		// This isn't directly affecting the UI, so put it on a worker thread.
		AsyncTask<Void, Void, Void> findLastLocationTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				// Find the last known location, specifying a required accuracy of within the min distance between updates
				// and a required latency of the minimum time required between updates.

				Location lastKnownLocation = null;
                try{
                	lastKnownLocation = lastLocationFinder.getLastBestLocation(MAX_DISTANCE,
						System.currentTimeMillis()-MAX_TIME);
	            } catch (IllegalArgumentException iex){
	                Logger.error("Location", "Unable to find provider. Need to handle this more intelligent", iex);
	            }
				if (lastKnownLocation != null){				
					setCurrentLocation(lastKnownLocation);
		        } 
				else {
					Log.d(TAG,"Last known location: is null");
				}
				
		        return null;
		      }
		    };
		    
	    findLastLocationTask.execute();
		    
	    // If we have requested location updates, turn them on here.
	    toggleUpdatesWhenLocationChanges(updateWhenLocationChanges);
	}
	
	protected void toggleUpdatesWhenLocationChanges(boolean updateWhenLocationChanges) {		
		// Start or stop listening for location changes
		if (updateWhenLocationChanges){
			requestLocationUpdates();
        }
		else  {
			disableLocationUpdates();
        }
	}
	
	protected void requestLocationUpdates() {
	    // Normal updates while activity is visible.
        try {
	    locationUpdateRequester.requestLocationUpdates(MAX_TIME, MAX_DISTANCE, criteria, locationListenerPendingIntent);
	    
	    // Register a receiver that listens for when the provider I'm using has been disabled. 
	    IntentFilter intentFilter = new IntentFilter(LocationChangedReceiver.ACTIVE_LOCATION_UPDATE_PROVIDER_DISABLED);
	    context.registerReceiver(locProviderDisabledReceiver, intentFilter);

	    // Register a receiver that listens for when a better provider than I'm using becomes available.
	    String bestProvider = locationManager.getBestProvider(criteria, false);
	    String bestAvailableProvider = locationManager.getBestProvider(criteria, true);
	    if (bestProvider != null && !bestProvider.equals(bestAvailableProvider)) {
	      locationManager.requestLocationUpdates(bestProvider, 0, 0, bestInactiveLocationProviderListener, context.getMainLooper());
	    }
        } catch (IllegalArgumentException iex){
            Logger.error("Location", "Unable to find provider. Need to handle this more intelligent", iex);
        }
	  }
	  
	  /**
	   * Stop listening for location updates
	   */
	protected void disableLocationUpdates() {
        try {
        	context.unregisterReceiver(locProviderDisabledReceiver);
        } catch (IllegalArgumentException ex){
            //do nothing
        }
		locationManager.removeUpdates(locationListenerPendingIntent);
		locationManager.removeUpdates(bestInactiveLocationProviderListener);	    
		lastLocationFinder.cancel();
	}
	
	private synchronized void setCurrentLocation(Location l){
		currentLocation = l;
	}
	
	public Location getCurrentLocation() {
		return currentLocation;
	}
}
