package com.dzig.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.dzig.R;

public class LoginActivity extends FragmentActivity{


    private static final String TAG = LoginActivity.class.getSimpleName();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

	}

}
