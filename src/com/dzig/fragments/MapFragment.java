package com.dzig.fragments;

import android.app.Activity;

import com.dzig.activities.CustomMapActivity;

public class MapFragment extends ActivityHostFragment {
	@Override
	protected Class<? extends Activity> getActivityClass() {
		return CustomMapActivity.class;
	}
}
