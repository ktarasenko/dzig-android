package com.dzig.activities;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import  com.dzig.R;
import android.preference.PreferenceActivity;

import java.util.List;

public class SettingsActivity extends PreferenceActivity{
    private static final String ACTION_PREFS_LOCATION = "com.dzig.PREFS_LOCATION";
    private static final String ACTION_PREFS_MOCK = "com.dzig.PREFS_MOCK";


    // Called only on Honeycomb and later
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.prefs_headers, target);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String action = getIntent().getAction();

        if (action != null) {
            if (ACTION_PREFS_LOCATION.equals(action)) {
                addPreferencesFromResource(R.xml.prefs_location);
            } else if (ACTION_PREFS_MOCK.equals(action)) {
                addPreferencesFromResource(R.xml.prefs_mock);
            }
        } else {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                // Load the legacy preferences headers
                addPreferencesFromResource(R.xml.prefs_legacy);
            }

        }
    }

    public static class LocationFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.prefs_location);
        }
    }

    public static class MockFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.prefs_mock);
        }
    }


}
