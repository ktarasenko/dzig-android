package com.dzig.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Utils {
		
	private Utils(){
		throw new UnsupportedOperationException();
	}
	

	public static Bitmap loadBitmapFromAssets(AssetManager assetManager, String fileName, Bitmap defaultBitmap){
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
	
	public static String getLastSeenString(Date date, String longTimeAgoMessage){
		long delta = (System.currentTimeMillis() - date.getTime())/1000;
		final String result;
		if (delta < 0){
			result = "in future";
		} else if (delta < 60) {
			result = "just now";
		} else if (delta < 120) {
		    result = "one minute ago";
		} else if (delta < 3600) {
			result = String.format("%d minutes ago", (int)(delta/60));
		} else if (delta < 7200) {
			result = "one hour ago";      
		} else if (delta < 86400) {
			result = String.format("%d hours ago", (int) (delta/3600));
		} else if (delta < ( 86400 * 2 ) ) {
			result = "one day ago";       
		} else if (delta < ( 86400 * 7 ) ) {
			result = String.format("%d days ago", (int) (delta/86400));
		} else {
			result = longTimeAgoMessage;
		}
		return result;
	}
}
