package com.dzig.location;

import android.app.PendingIntent;
import android.location.Criteria;
import android.location.LocationManager;

public class GingerbreadLocationUpdateRequester extends LocationUpdateRequester{

	protected GingerbreadLocationUpdateRequester(LocationManager locationManager) {
		super(locationManager);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void requestLocationUpdates(long minTime, long minDistance, Criteria criteria, PendingIntent pendingIntent) {
		locationManager.requestLocationUpdates(minTime, minDistance, criteria, pendingIntent); 
	}
}
