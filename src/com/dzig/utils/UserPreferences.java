package com.dzig.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.UUID;

public class UserPreferences {

    public static final String PREF_LAST_USER = "lastUser";

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
	 * @return true if Intro screen should be shown after app launch
	 */
	public boolean isIntroRequired(){
		return false;
	}

    public String getString(String key) {
        return preferences.getString(key, null);
    }

    public void putString(String key, String value){
        preferences.edit().putString(key, value).commit();
    }
}

