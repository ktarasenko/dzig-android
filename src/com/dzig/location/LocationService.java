package com.dzig.location;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
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
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.dzig.R;
import com.dzig.activities.CustomMapActivity;
import com.dzig.activities.HomeActivity;
import com.dzig.model.Coordinate;
import com.dzig.model.User;
import com.dzig.utils.Logger;



public class LocationService extends Service {

	private String TAG="LocationService";
	
	public static boolean USE_GPS_WHEN_ACTIVITY_VISIBLE = true;
	
	// The default search radius when searching for places nearby.
	public static int DEFAULT_RADIUS = 150;
	// The maximum distance the user should travel between location updates. 
	public static int MAX_DISTANCE = DEFAULT_RADIUS/2;
	// The maximum time that should pass before the user gets a location update.
	public static long MAX_TIME = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
	
	protected Criteria criteria;
	protected ILastLocationFinder lastLocationFinder;
	protected LocationUpdateRequester locationUpdateRequester;
	protected PendingIntent locationListenerPendingIntent;
	private Coordinate currentCoordinate;
	private LocationManager locationManager;
	
	private ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
	
	@Override
	public void onCreate() {
		Log.e(TAG, "onCreate");
		
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		criteria = new Criteria();
	    if (LocationService.USE_GPS_WHEN_ACTIVITY_VISIBLE)
	      criteria.setAccuracy(Criteria.ACCURACY_FINE);
	    else
	      criteria.setPowerRequirement(Criteria.POWER_LOW);
	    
	    Intent activeIntent = new Intent(this, LocationChangedReceiver.class);
	    locationListenerPendingIntent = PendingIntent.getBroadcast(this, 0, activeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	    
	    lastLocationFinder = PlatformSpecificImplementationFactory.getLastLocationFinder(this);
	    lastLocationFinder.setChangedLocationListener(oneShotLastLocationUpdateListener);
	    
	    locationUpdateRequester = PlatformSpecificImplementationFactory.getLocationUpdateRequester(locationManager);
	    getLocationAndUpdateIt(true);
	    
	    super.onCreate();
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
			setCurrentLocation(new Coordinate(""+ new String("Me").hashCode(), 
					new User("Me","Me","Me","Me"),
					new Date(l.getTime()), 
					l.getLatitude(), 
					l.getLongitude(), 
					l.getAccuracy()));
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
                        lastLocationFinder.getLastBestLocation(LocationService.MAX_DISTANCE,
						System.currentTimeMillis()-LocationService.MAX_TIME);
	            } catch (IllegalArgumentException iex){
	                Logger.error("Location", "Unable to find provider. Need to handle this more intelligent", iex);
	            }
				if (lastKnownLocation != null){				
					setCurrentLocation(new Coordinate(""+ new String("Me").hashCode(),
                            new User("Me","Me","Me","Me"),
							new Date(lastKnownLocation.getTime()), 
							lastKnownLocation.getLatitude(), 
							lastKnownLocation.getLongitude(), 
							lastKnownLocation.getAccuracy()));
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
		if (updateWhenLocationChanges)
			requestLocationUpdates();
		else 
			disableLocationUpdates();
	}
	
	protected void requestLocationUpdates() {
	    // Normal updates while activity is visible.
        try {
	    locationUpdateRequester.requestLocationUpdates(LocationService.MAX_TIME, LocationService.MAX_DISTANCE, criteria, locationListenerPendingIntent);
	    
	    // Register a receiver that listens for when the provider I'm using has been disabled. 
	    IntentFilter intentFilter = new IntentFilter(LocationChangedReceiver.ACTIVE_LOCATION_UPDATE_PROVIDER_DISABLED);
	    registerReceiver(locProviderDisabledReceiver, intentFilter);

	    // Register a receiver that listens for when a better provider than I'm using becomes available.
	    String bestProvider = locationManager.getBestProvider(criteria, false);
	    String bestAvailableProvider = locationManager.getBestProvider(criteria, true);
	    if (bestProvider != null && !bestProvider.equals(bestAvailableProvider)) {
	      locationManager.requestLocationUpdates(bestProvider, 0, 0, bestInactiveLocationProviderListener, getMainLooper());
	    }
        } catch (IllegalArgumentException iex){
            Logger.error("Location", "Unable to find provider. Need to handle this more intelligent", iex);
        }
	  }
	  
	  /**
	   * Stop listening for location updates
	   */
	protected void disableLocationUpdates() {
		unregisterReceiver(locProviderDisabledReceiver);
		locationManager.removeUpdates(locationListenerPendingIntent);
		locationManager.removeUpdates(bestInactiveLocationProviderListener);	    
		lastLocationFinder.cancel();
	}

	private ScheduledExecutorService scheduler;
	private static final int notificationId = 1;
	
	public IBinder onBind(Intent arg0) {		
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		registerReceiver(broadcastReceiver, new IntentFilter(CustomMapActivity.ACTION_GET_POINTS));
		if (scheduler == null){
			scheduler = Executors.newScheduledThreadPool(1);
			scheduler.scheduleAtFixedRate(updatePointsRunable, 0, 5, TimeUnit.SECONDS);
		}
		
		Intent onClickIntent = new Intent(this, HomeActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		Notification notification = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Dzig!")
                .setContentText("Dzig service is running")
                .setContentIntent(pendingIntent)
                .build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		manager.notify(notificationId, notification);
		
		return START_STICKY;
	}
	
	public void onDestroy() {
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		manager.cancel(notificationId);
		
		unregisterReceiver(broadcastReceiver);
		disableLocationUpdates();
		scheduler.shutdown();
		scheduler = null;	
		super.onDestroy();
	};

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (CustomMapActivity.ACTION_GET_POINTS.equals(intent.getAction())){
				forceUpdatePoints();
			}
			
		}
	};
	
	Runnable updatePointsRunable = new Runnable() {
		
		@Override
		public void run() {
			try{
				debugUpdateCoordinates();
				Intent intent = new Intent(CustomMapActivity.ACTION_UPDATE_POINTS);
				intent.putParcelableArrayListExtra(CustomMapActivity.EXTRA_POINTS, coordinates);
				sendBroadcast(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	protected void forceUpdatePoints() {
		if (scheduler != null) 
			scheduler.execute(updatePointsRunable);		
	}
	
	//============================ DEBUG =================================
	private static final String[] debugNickNames = {"User","Jade", "Sub Zero", "Raiden", "Goro", "Lui Kang", "Cyrax", "Shang Tsung", "Kabal"};
	private static final String[] debugUserPics =  {"A03", "B02",  "C01", 	   "E05",    "F04",  "G02",      "H01",   "I03",         "K05"};
	private void debugGenerateCoordinates(){
		Random random = new Random();
		for (int i = 0; i < debugNickNames.length; i++) {
			double lat = 50.422519 + random.nextDouble()/10;
			double lon = 30.50344 + random.nextDouble()/10;
            User u = new User(""+debugNickNames[i].hashCode(), debugNickNames[i]+"@gmail.com",  debugNickNames[i], debugUserPics[i]);
			coordinates.add(new Coordinate(""+debugNickNames[i].hashCode(), u, new Date(), lat, lon, 5 + random.nextInt(5000)));
		}
	}
	
	private void debugUpdateCoordinates(){
		if (coordinates.isEmpty()){
			debugGenerateCoordinates();
		}
		ArrayList<Coordinate> newCoordinates = new ArrayList<Coordinate>();
		Random random = new Random();
		
		for (Coordinate coordinate : coordinates) {
			double dlat = (-1 + random.nextDouble()*2)/1000;
			double dlon = (-1 + random.nextDouble()*2)/1000;
			newCoordinates.add(new Coordinate(coordinate.getId(), coordinate.getCreator(), new Date(), 
					coordinate.getLat()+dlat, coordinate.getLon()+dlon, coordinate.getAccuracy()));
		}
		if (currentCoordinate != null) {
			newCoordinates.set(0, currentCoordinate);
		}
		coordinates = newCoordinates;		
	}
	
	private synchronized void setCurrentLocation(Coordinate l){
		if (currentCoordinate != null) {
			currentCoordinate = l;
		}
		else {
			currentCoordinate = new Coordinate(l.getId(), l.getCreator(), l.getDate(), l.getLat(), l.getLon(), l.getAccuracy());
		}		
	}
}
