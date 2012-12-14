package com.dzig.location;

import com.dzig.utils.LocationConstants;

import android.content.Context;
import android.location.LocationManager;

public class PlatformSpecificImplementationFactory {
	 /**
	   * Create a new LastLocationFinder instance
	   * @param context Context
	   * @return LastLocationFinder
	   */
	  public static ILastLocationFinder getLastLocationFinder(Context context) {
	    return LocationConstants.SUPPORTS_GINGERBREAD ? new GingerbreadLastLocationFinder(context) : new LegacyLastLocationFinder(context);
	  }
	  /**
	   * Create a new LocationUpdateRequester
	   * @param locationManager Location Manager
	   * @return LocationUpdateRequester
	   */
	  public static LocationUpdateRequester getLocationUpdateRequester(LocationManager locationManager) {
	    return LocationConstants.SUPPORTS_GINGERBREAD ? new GingerbreadLocationUpdateRequester(locationManager) : new FroyoLocationUpdateRequester(locationManager);    
	  }
}
