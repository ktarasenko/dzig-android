package com.dzig.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;

import com.dzig.R;
import com.dzig.loader.GetCoordinatesAsyncTask;
import com.dzig.model.Coordinate;
import com.dzig.model.CoordinatesDb;
import com.dzig.utils.Logger;
import com.dzig.utils.UserIconHelper;
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
	private UserIconHelper userIconHelper;
	private AsyncTask<?,?,?> loader = 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_view);
		userIconHelper = new UserIconHelper(this.getAssets());
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
		ArrayList<CoordinateOverlayItem> items = new ArrayList<CoordinateOverlayItem>();
		java.text.DateFormat dateFormat = DateFormat.getMediumDateFormat(this);
		for (Coordinate coordinate : coordinates) {
			
			GeoPoint point = new GeoPoint(toIntE6(coordinate.getLat()), toIntE6(coordinate.getLon()));
			
			String msg = dateFormat.format(coordinate.getDate()); //getResources().getString(R.string.last_seen_message, dateFormat.format(coordinate.getDate()));
			CoordinateOverlayItem overlayitem = new CoordinateOverlayItem(point, coordinate.getCreator().toString(), msg, coordinate);
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
						Logger.debug(TAG, "setPoints "+list);
						setPoints(list);		
					}
				});
				
			}			
		}
		
	};
	
	static class CoordinateOverlayItem extends OverlayItem{
		final Coordinate coordinate;
		
		public CoordinateOverlayItem(GeoPoint point, String title, String message, Coordinate coordinate) {
			super(point, title, message);
			this.coordinate = coordinate;
		}
		
		public Coordinate getCoordinate() {
			return coordinate;
		}
	}

	class MapOverlay extends ItemizedOverlay<CoordinateOverlayItem>{

		private final ArrayList<CoordinateOverlayItem> items = new ArrayList<CoordinateOverlayItem>();

		public MapOverlay(Drawable defaultMarker) {
			super(boundCenter(defaultMarker));
			populate();
		}
		
		public void addOverlay(CoordinateOverlayItem overlay) {
			items.add(overlay);
		    populate();
		}
		
		public void setOverlays(List<CoordinateOverlayItem> overlays) {
			items.clear();
			items.addAll(overlays);
		    populate();
		    
		}
		
		@Override
		protected CoordinateOverlayItem createItem(int index) {
			return items.get(index);
		}

		@Override
		public int size() {
			return items.size();
		}
		
		private Point pt = new Point();
		private Paint paint = new Paint();
		private Matrix matrix = new Matrix();
		
		@Override
		public void draw(Canvas canvas, MapView map, boolean shadow) {
			for (CoordinateOverlayItem item : items) {
				drawPoint(canvas, map, item, shadow);
			}
		}
		
		private void drawPoint(Canvas canvas, MapView map, CoordinateOverlayItem item, boolean shaddow){
			GeoPoint point = item.getPoint();
            Projection projection = map.getProjection();
	           
            projection.toPixels(point,pt);
            if (shaddow){
	            float circleRadius = Math.abs(projection.metersToEquatorPixels((float)item.getCoordinate().getAccuracy()));
	
	            paint.reset();
	            paint.setFlags(Paint.ANTI_ALIAS_FLAG);
	            paint.setColor(0x30000000);
	            paint.setStyle(Style.FILL_AND_STROKE);
	            canvas.drawCircle((float)pt.x, (float)pt.y, circleRadius, paint);
	
	            paint.setColor(0x99000000);
	            paint.setStyle(Style.STROKE);
	            canvas.drawCircle((float)pt.x, (float)pt.y, circleRadius, paint);
            } else {
            	Bitmap bitmap = userIconHelper.getBitmap(item.coordinate);
            	if (bitmap != null){
            		matrix.reset();
            		float scale = Math.min(50F / bitmap.getWidth(), 50F / bitmap.getHeight());
            		matrix.postScale(scale, scale);
            		matrix.postTranslate(pt.x - bitmap.getWidth()*scale/2, pt.y - bitmap.getHeight()*scale/2);
            		canvas.drawBitmap(bitmap, matrix, null);
            	}
//	            paint.setColor(0xFFFF0000);
//	            paint.setTextSize(25);
//	            String text = item.getTitle();
//	            float w = paint.measureText(text, 0, text.length());
//	            canvas.drawText(text, pt.x - w/2, pt.y, paint);
//	            
//	            paint.setTextSize(12);
//	            paint.setColor(0xFF0000FF);
//	            String text2 = item.getSnippet();//getString(R.string.last_seen_message, item.getSnippet());
//	            float w2 = paint.measureText(text2, 0, text2.length());
//	            canvas.drawText(text2, pt.x - w2/2, pt.y+10, paint);
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
