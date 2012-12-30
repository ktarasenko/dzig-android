package com.dzig.loader;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.util.LruCache;

public class CacheService extends Service {
	private LruCache<String, Bitmap> bitmapCache = new LruCache<String, Bitmap>(5000){
		protected int sizeOf(String key, Bitmap value) {
			return value.getWidth()*value.getHeight();
		};
	};
	
	public class CacheBinder extends Binder{
		public Bitmap getBitmap(String key) {
			return bitmapCache.get(key);
		}
		
		public void putBitmap(String key, Bitmap value) {
			bitmapCache.put(key, value);
		}
	}
	
	CacheBinder binder = new CacheBinder();
	
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

}
