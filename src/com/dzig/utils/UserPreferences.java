package com.dzig.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class UserPreferences {
	private final SharedPreferences preferences;
	
	private UserPreferences (Context context){
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	/**
	 * @param context
	 * @return an instance of UserPreferences
	 */
	public static UserPreferences newInstance(Context context){
		return new UserPreferences(context);
	}
	
	/**
	 * @return true if Intro screen sould be shown after app launch
	 */
	public boolean isIntroRequired(){
		return false;
	}
}
