package com.dzig.utils;

import java.lang.ref.WeakReference;

import com.dzig.model.Coordinate;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

public class UserIconHelper {
	private Bitmap defaultMarker;
	private static WeakReference<LruBitmapCache<String>> markerCacheReference;
	private LruBitmapCache<String> markerCache;
	private final AssetManager assetManager;
	
	public UserIconHelper(AssetManager assetManager) {
		//highly unusual trick to allow cache to be shared, but reclaim resources if nobody uses it 
		synchronized (UserIconHelper.class) {
			markerCache = (markerCacheReference != null) ? markerCacheReference.get() : null;
			if (markerCache == null){
				markerCache = new LruBitmapCache<String>(2048);
				markerCacheReference = new WeakReference<LruBitmapCache<String>>(markerCache);
			}
		}
		this.assetManager = assetManager;
		defaultMarker = Utils.loadBitmapFromAssets(assetManager, getAssetName("no_picture"), null);
	}
	
	public Bitmap getBitmap(Coordinate coordinate){
		String markerPath = getAssetName(coordinate.getCreator().getAvatar());
		Bitmap result = markerCache.get(markerPath);
		if (result == null){
			result = Utils.loadBitmapFromAssets(assetManager, markerPath, null);
			if (result != null) {
				markerCache.put(markerPath, result);
			} else {
				result = defaultMarker;
			}
		}
		return result;
	}
	
	private String getAssetName(String shortName) {
		return "face_icons/"+shortName+".png";
	}
}
