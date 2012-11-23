package com.dzig.activities;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.dzig.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class CustomMapActivity extends MapActivity {
	private MapView mapView; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_view);
		mapView = (MapView) findViewById(R.id.mapview);
		MapOverlay mapOverlay = new MapOverlay(getResources().getDrawable(R.drawable.map_marker));
		mapView.getOverlays().add(mapOverlay);
		
		
		GeoPoint point = new GeoPoint(19240000,-99120000);
		OverlayItem overlayitem = new OverlayItem(point, "Hola, Mundo!", "I'm in Mexico City!");
		mapOverlay.addOverlay(overlayitem);
		
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	class MapOverlay extends ItemizedOverlay<OverlayItem>{

		private final ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

		public MapOverlay(Drawable defaultMarker) {
			super(boundCenterBottom(defaultMarker));
		}
		
		public void addOverlay(OverlayItem overlay) {
			items.add(overlay);
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
}
