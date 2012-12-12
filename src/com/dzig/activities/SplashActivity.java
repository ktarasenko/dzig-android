package com.dzig.activities;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import com.dzig.api.request.user.GetUserRequest;
import com.dzig.api.response.user.UserResponse;
import com.dzig.api.task.GetUserTask;
import com.dzig.fragments.dialog.LoginDialogFragment;
import com.dzig.model.User;
import com.dzig.utils.UserPreferences;

import com.dzig.R;

import android.os.Bundle;
import android.content.Intent;

public class SplashActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        LoginDialogFragment newFragment = LoginDialogFragment.newInstance(true);
        ft.add(R.id.login_fragment, newFragment);
        ft.commit();

    }

}
