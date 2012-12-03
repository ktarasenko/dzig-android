package com.dzig.location;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.dzig.activities.CustomMapActivity;
import com.dzig.model.Coordinate;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class LocationService extends Service {

	private ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
	private ScheduledExecutorService scheduler;
	
	public IBinder onBind(Intent arg0) {		
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		registerReceiver(broadcastReceiver, new IntentFilter(CustomMapActivity.ACTION_GET_POINTS));
		if (scheduler == null){
			scheduler = Executors.newScheduledThreadPool(1);
			scheduler.scheduleAtFixedRate(updatePointsRunable, 0, 1, TimeUnit.SECONDS);
		}
		return START_STICKY;
	}
	
	public void onDestroy() {
		unregisterReceiver(broadcastReceiver);
		scheduler.shutdown();
		scheduler = null;
	};

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (CustomMapActivity.ACTION_GET_POINTS.equals(intent.getAction())){
				forseUpdatePoints();
			}
			
		}
	};
	
	Runnable updatePointsRunable = new Runnable() {
		
		@Override
		public void run() {
			debugUpdateCoordinates();
			Intent intent = new Intent(CustomMapActivity.ACTION_UPDATE_POINTS);
			intent.putParcelableArrayListExtra(CustomMapActivity.EXTRA_POINTS, coordinates);
			sendBroadcast(intent);
		}
	};

	protected void forseUpdatePoints() {
		if (scheduler != null) 
			scheduler.execute(updatePointsRunable);		
	}
	
	//============================ DEBUG =================================
	private static final String[] debugNickNames = {"Jade", "Sub Zero", "Raiden", "Goro", "Lui Kang", "Cyrax", "Shang Tsung", "Kabal"};
	private void debugGenerateCoordinates(){
		Random random = new Random();
		for (int i = 0; i < debugNickNames.length; i++) {
			double lat = 50.422519 + random.nextDouble()/10;
			double lon = 30.50344 + random.nextDouble()/10;
			coordinates.add(new Coordinate(""+debugNickNames[i].hashCode(), debugNickNames[i], new Date(), lat, lon, 5));
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
		coordinates = newCoordinates;
	}
}
