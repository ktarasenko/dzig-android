package com.dzig.activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;

import com.dzig.R;
import com.dzig.model.Coordinate;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class CustomMapActivity extends MapActivity {
	public static final String ACTION_UPDATE_POINTS = "com.dzig.activities.CustomMapActivity.ACTION_UPDATE_POINTS";
	public static final String ACTION_GET_POINTS = "com.dzig.activities.CustomMapActivity.ACTION_GET_POINTS";
	/**
	 * Key for List<Coordinate> bundle extra, ACTION_UPDATE_POINTS event
	 */
	public static final String EXTRA_POINTS = "com.dzig.activities.CustomMapActivity.EXTRA_POINTS";
	private static final String TAG = "CustomMapActivity";
	
	
	private MapView mapView; 
	private MapOverlay mapOverlay;
	private MyLocationOverlay mapOverlay2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_view);
		
		mapView = (MapView) findViewById(R.id.mapview);
		mapOverlay = new MapOverlay(getResources().getDrawable(R.drawable.map_marker));
		mapOverlay2 = new MapOverlay2(this, mapView);
		mapView.getOverlays().add(mapOverlay2);
		mapView.getOverlays().add(mapOverlay);
	}
	
	@Override
	protected void onResume() {
		registerReceiver(receiver, new IntentFilter(ACTION_UPDATE_POINTS));
		sendBroadcast(new Intent(ACTION_GET_POINTS));
		mapOverlay2.enableCompass();
		mapOverlay2.enableMyLocation();
		super.onResume();
	}
	@Override
	protected void onPause() {
		super.onPause();
		mapOverlay2.disableCompass();
		mapOverlay2.disableMyLocation();
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
			
			String msg = dateFormat.format(coordinate.getDate()); //getResources().getString(R.string.last_seen_message, dateFormat.format(coordinate.getDate()));
			OverlayItem overlayitem = new OverlayItem(point, coordinate.getCreator(), msg);
			items.add(overlayitem);	
		}
		mapOverlay.setOverlays(items);
		mapView.invalidate();
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
		
		private Point pt = new Point();
		private Point pt2 = new Point();
		private Paint paint = new Paint();
		
		@Override
		public void draw(Canvas canvas, MapView map, boolean shadow) {
			super.draw(canvas, map, shadow);
			
			for (OverlayItem item : items) {
				drawPoint(canvas, map, item, shadow);
			}
		}
		private void drawPoint(Canvas canvas, MapView map, OverlayItem item, boolean shaddow){
			GeoPoint point = item.getPoint();
            Projection projection = map.getProjection();
	           
            projection.toPixels(point,pt);
            if (shaddow){
	            GeoPoint newGeos = new GeoPoint(point.getLatitudeE6()+(5000), point.getLongitudeE6()); // adjust your radius accordingly
	            
	            projection.toPixels(newGeos,pt2);
	            float circleRadius = Math.abs(pt2.y-pt.y);
	
	            paint.reset();
	            paint.setFlags(Paint.ANTI_ALIAS_FLAG);
	            paint.setColor(0x30000000);
	            paint.setStyle(Style.FILL_AND_STROKE);
	            canvas.drawCircle((float)pt.x, (float)pt.y, circleRadius, paint);
	
	            paint.setColor(0x99000000);
	            paint.setStyle(Style.STROKE);
	            canvas.drawCircle((float)pt.x, (float)pt.y, circleRadius, paint);
            } else {
	            paint.setColor(0xFFFF0000);
	            paint.setTextSize(25);
	            String text = item.getTitle();
	            float w = paint.measureText(text, 0, text.length());
	            canvas.drawText(text, pt.x - w/2, pt.y, paint);
	            
	            paint.setTextSize(12);
	            paint.setColor(0xFF0000FF);
	            String text2 = item.getSnippet();//getString(R.string.last_seen_message, item.getSnippet());
	            float w2 = paint.measureText(text2, 0, text2.length());
	            canvas.drawText(text2, pt.x - w2/2, pt.y+10, paint);
            }
//            Bitmap markerBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.pin);
//            canvas.drawBitmap(markerBitmap,pt.x,pt.y-markerBitmap.getHeight(),null);
		}
	}
	
	
	class MapOverlay2 extends MyLocationOverlay {

		public MapOverlay2(Context context, MapView map) {
			super(context, map);
		}
		
	}
}
