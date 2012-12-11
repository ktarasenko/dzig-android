package com.dzig.activities;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.ProgressBar;
import com.dzig.api.request.user.GetUserRequest;
import com.dzig.api.response.user.UserResponse;
import com.dzig.api.task.BasicTask;
import com.dzig.api.task.GetUserTask;
import com.dzig.app.DzigApplication;
import com.dzig.fragments.dialog.ProgressDialog;
import com.dzig.model.User;
import com.dzig.utils.UserPreferences;

import com.dzig.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

public class SplashActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new LoadAndLaunch().execute(GetUserRequest.newInstance());
    }
    private class LoadAndLaunch extends GetUserTask {

    	@Override
    	protected void onPostExecute(UserResponse result) {
            super.onPostExecute(result);
    		final boolean showIntro = UserPreferences.newInstance(SplashActivity.this).isIntroRequired();
    		if (showIntro) {
    			startActivity(new Intent(SplashActivity.this, IntroActivity.class));
    		} else if (User.currentUser() != null){
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            } else {
    			startActivity(new Intent(SplashActivity.this, LoginActivity.class));
    		}
    		finish();
    	}
    }
}
