package com.dzig.loader;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.AsyncTaskLoader;

public class BitmapAssetLoader extends AsyncTaskLoader<Bitmap> {
	private AssetManager assetManager;
	private String fileName;
	
	public BitmapAssetLoader(Context context, String fileName) {
		super(context);
		assetManager = context.getAssets();
		this.fileName = fileName;
	}

	@Override
	public Bitmap loadInBackground() {
		return loadFromAssets(assetManager, fileName, null);
	}

	static Bitmap loadFromAssets(AssetManager assetManager, String fileName, Bitmap defaultBitmap){
		InputStream inputStream = null;
		try{
			return BitmapFactory.decodeStream(inputStream = assetManager.open(fileName));
		} catch (IOException e) {
			return defaultBitmap;
		} finally {
			if (inputStream != null){
				try{
					inputStream.close();
				}catch(IOException e) {
					// ignore
				}
			}
		}
	}
}
