package com.dzig.activities;

import utils.UserPreferences;

import com.dzig.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

public class SplashActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new LoadAndLaunch().execute();
    }
    private class LoadAndLaunch extends AsyncTask<Void, Integer, Void>{
    	@Override
    	protected Void doInBackground(Void... params) {
    		// TODO	init all stuff here
    		try{
    			Thread.sleep(1000);
    		} catch (InterruptedException e){
    			// ignore
    		}
    		return null;
    	}
    	@Override
    	protected void onPostExecute(Void result) {
    		final boolean showIntro = UserPreferences.newInstance(SplashActivity.this).isIntroRequired();
    		if (showIntro) {
    			startActivity(new Intent(SplashActivity.this, IntroActivity.class));
    		} else {
    			startActivity(new Intent(SplashActivity.this, LoginActivity.class));
    		}
    		finish();
    	}
    }
}
