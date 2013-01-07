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
import com.dzig.activities.SettingsActivity;
import com.dzig.app.DzigApplication;
import com.dzig.model.Coordinate;
import com.dzig.model.CoordinatesDb;
import com.dzig.model.User;
import com.dzig.utils.Logger;
import com.dzig.utils.UserPreferences;


public class LocationService extends Service {

	private String TAG="LocationService";
	
	private ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
	private UserLocationHelper userLocationHelper;
	@Override
	public void onCreate() {
		Log.e(TAG, "onCreate");
		userLocationHelper = new UserLocationHelper(this);
	    super.onCreate();
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
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException ex){
            //do nothing
        }
		userLocationHelper.disableLocationUpdates();
		scheduler.shutdown();
		scheduler = null;	
		super.onDestroy();
	};

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (CustomMapActivity.ACTION_GET_POINTS.equals(intent.getAction())){
				forceUpdatePoints();
			}  else if (SettingsActivity.ACTION_SETTINGS_LOCATION_UPDATED.equals(intent.getAction())){
				userLocationHelper.updateSettings();
				userLocationHelper.requestLocationUpdates();
            }
			
		}
	};
	
	Runnable updatePointsRunable = new Runnable() {
		
		@Override
		public void run() {
			try{
				debugUpdateCoordinates();
				CoordinatesDb coordinatesDb = new CoordinatesDb(LocationService.this);
				coordinatesDb.update(coordinates);
				coordinatesDb.close();
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
		if (userLocationHelper != null && userLocationHelper.getCurrentLocation() != null) {
			Location location = userLocationHelper.getCurrentLocation();
			newCoordinates.set(0, new Coordinate("-1",
                    DzigApplication.userManager().getCurrentUser(),
                    new Date(),
                    location.getLatitude(),
                    location.getLongitude(),
                    location.getAccuracy()));
		}
		coordinates = newCoordinates;		
	}
}
