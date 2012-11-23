package com.dzig.activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;

import com.dzig.R;
import com.dzig.model.Coordinate;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class CustomMapActivity extends MapActivity {
	public static final String ACTION_UPDATE_POINTS = "com.dzig.activities.CustomMapActivity.ACTION_UPDATE_POINTS";
	/**
	 * Key for List<Coordinate> bundle extra, ACTION_UPDATE_POINTS event
	 */
	public static final String EXTRA_POINTS = "com.dzig.activities.CustomMapActivity.EXTRA_POINTS";
	
	
	private MapView mapView; 
	private MapOverlay mapOverlay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_view);
		
		mapView = (MapView) findViewById(R.id.mapview);
		mapOverlay = new MapOverlay(getResources().getDrawable(R.drawable.map_marker));
		mapView.getOverlays().add(mapOverlay);
		
		new Test().execute(10);
	}
	
	@Override
	protected void onResume() {
		registerReceiver(receiver, new IntentFilter(ACTION_UPDATE_POINTS));
		super.onResume();
	}
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return true;
	}
	
	protected void setPoints(List<Coordinate> coordinates) {
		ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
		java.text.DateFormat dateFormat = DateFormat.getMediumDateFormat(this);
		for (Coordinate coordinate : coordinates) {
			
			GeoPoint point = new GeoPoint(toIntE6(coordinate.getLat()), toIntE6(coordinate.getLon()));
			
			String msg = getResources().getString(R.string.last_seen_message, dateFormat.format(coordinate.getDate()));
			OverlayItem overlayitem = new OverlayItem(point, coordinate.getCreator(), msg);
			items.add(overlayitem);	
		}
		mapOverlay.setOverlays(items);
		
	}
	
	private static int toIntE6(double value){
		return (int)(value * 1000000);
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			if (ACTION_UPDATE_POINTS.equals(intent.getAction())){
				Bundle bundle = intent.getExtras();
				final List<Coordinate> list = bundle.getParcelableArrayList(EXTRA_POINTS);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setPoints(list);		
					}
				});
				
			}			
		}
		
	};

	class MapOverlay extends ItemizedOverlay<OverlayItem>{

		private final ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

		public MapOverlay(Drawable defaultMarker) {
			super(boundCenterBottom(defaultMarker));
		}
		
		public void addOverlay(OverlayItem overlay) {
			items.add(overlay);
		    populate();
		}
		
		public void setOverlays(List<OverlayItem> overlays) {
			items.clear();
			items.addAll(overlays);
		    populate();
		}
		
		@Override
		protected OverlayItem createItem(int index) {
			return items.get(index);
		}

		@Override
		public int size() {
			return items.size();
		}
		
	}

	
	private class Test extends AsyncTask<Integer, Void, Void>{
		@Override
		protected Void doInBackground(Integer... params) {
			Intent intent = new Intent(ACTION_UPDATE_POINTS);
			Random random = new Random();
			
			ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
			for (int i = 0; i < params[0]; i++) {
				double lat = 50.422519 + random.nextDouble()/10;
				double lon = 30.50344 + random.nextDouble()/10;
				coordinates.add(new Coordinate(""+i, "" + (char)('a'+i), new Date(), lat, lon, 5));
			}
			
			intent.putParcelableArrayListExtra(EXTRA_POINTS, coordinates);
			sendBroadcast(intent);
			return null;
		}
	}
}
