package com.dzig.location;

import android.app.PendingIntent;
import android.location.Criteria;
import android.location.LocationManager;

public class FroyoLocationUpdateRequester extends LocationUpdateRequester{

	protected FroyoLocationUpdateRequester(LocationManager locationManager) {
		super(locationManager);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void requestLocationUpdates(long minTime, long minDistance, Criteria criteria, PendingIntent pendingIntent) {
	    // Froyo introduced the Passive Location Provider, which receives updates whenever a 3rd party app 
	    // receives location updates.
	    locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, LocationService.MAX_TIME, LocationService.MAX_DISTANCE, pendingIntent);
	}

}
