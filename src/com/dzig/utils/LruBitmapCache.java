package com.dzig.utils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class LruBitmapCache<T> extends LruCache<T, Bitmap> {

	public LruBitmapCache(int sizeInBytes) {
		super(sizeInBytes);
	}
	
	@Override
	protected int sizeOf(T key, Bitmap value) {
		if (value == null){
			return 0;
		}
		return value.getWidth() * value.getHeight();
	}
}
