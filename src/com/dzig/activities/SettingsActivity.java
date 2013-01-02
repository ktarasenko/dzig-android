package com.dzig.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import  com.dzig.R;
import android.preference.PreferenceActivity;

import java.util.List;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private final boolean TOO_OLD =   Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB;

    private static final String ACTION_PREFS_LOCATION = "com.dzig.PREFS_LOCATION";
    private static final String ACTION_PREFS_MOCK = "com.dzig.PREFS_MOCK";
    public static final String ACTION_SETTINGS_LOCATION_UPDATED = "com.dzig.SETTINGS_LOCATION_UPDATED";

    private boolean preferencesWasChanged;

    // Called only on Honeycomb and later
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.prefs_headers, target);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.preferencesWasChanged = false;

        final String action = getIntent().getAction();
        if (action != null) {
            if (ACTION_PREFS_LOCATION.equals(action)) {
                addPreferencesFromResource(R.xml.prefs_location);
                setTitle(R.string.prefs_category_location);
            } else if (ACTION_PREFS_MOCK.equals(action)) {
                addPreferencesFromResource(R.xml.prefs_mock);
                setTitle(R.string.prefs_category_mock);
            }
        } else {

            if (TOO_OLD) {
                // Load the legacy preferences headers
                addPreferencesFromResource(R.xml.prefs_legacy);
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (TOO_OLD) {
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (TOO_OLD){
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }
        if (preferencesWasChanged){
            sendBroadcast(new Intent(SettingsActivity.ACTION_SETTINGS_LOCATION_UPDATED));
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        preferencesWasChanged = true;
    }

    public static class LocationFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.prefs_location);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener((SharedPreferences.OnSharedPreferenceChangeListener) getActivity());
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener((SharedPreferences.OnSharedPreferenceChangeListener) getActivity());
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
