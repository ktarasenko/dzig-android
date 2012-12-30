package com.dzig.location;

import android.location.Location;
import android.location.LocationListener;

public interface ILastLocationFinder {
	/**
	 * Returns the most accurate and timely previously detected location.
	 * Where the last result is beyond the specified maximum distance or 
	 * latency a one-off location update is returned via the {@link LocationListener}
	 * specified in {@link setChangedLocationListener}.
	 * @param minDistance Minimum distance before we require a location update.
	 * @param minTime Minimum time required between location updates.
	 * @return The most accurate and / or timely previously detected location.
	 */
	public Location getLastBestLocation(int minDistance, long minTime);
	  
	public void setChangedLocationListener(LocationListener l);

	public void cancel(); 
}
