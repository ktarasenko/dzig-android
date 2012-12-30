package com.dzig.loader;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.support.v4.content.AsyncTaskLoader;

import com.dzig.utils.Utils;

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
		return Utils.loadBitmapFromAssets(assetManager, fileName, null);
	}

}
