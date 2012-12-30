package com.dzig.loader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Pair;

import com.dzig.utils.Utils;

public class BitmapAssetCacheLoader<KEY> extends AsyncTaskLoader<List<Pair<KEY, Bitmap>>> {
	private AssetManager assetManager;
	private Collection<Pair<KEY, String>> keyPathPairs;
	private Bitmap defaultAsset;
	
	public BitmapAssetCacheLoader(Context context, Collection<Pair<KEY, String>> keyPathPairs) {
		this(context, keyPathPairs, null);
	}
	public BitmapAssetCacheLoader(Context context, Collection<Pair<KEY, String>> keyPathPairs, Bitmap defaultAsset) {
		super(context);
		assetManager = context.getAssets();
		this.defaultAsset = defaultAsset;
		this.keyPathPairs = keyPathPairs;
	}

	@Override
	public List<Pair<KEY, Bitmap>> loadInBackground() {
		ArrayList<Pair<KEY, Bitmap>> list = new ArrayList<Pair<KEY,Bitmap>>(keyPathPairs.size());
		for (Pair<KEY,String> pair : keyPathPairs) {
			list.add(new Pair<KEY, Bitmap>(pair.first,  Utils.loadBitmapFromAssets(assetManager, pair.second, defaultAsset)));
		}
		return list;
	}

}
