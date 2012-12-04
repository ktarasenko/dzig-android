package com.dzig.fragments;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;

/**
 * This is a fragment that will be used during transition from activities to fragments.
 */
@SuppressWarnings("deprecation")
public abstract class ActivityHostFragment extends Fragment {
    

    private final static String ACTIVITY_TAG = "hosted";
    private static final String TAG = ActivityHostFragment.class.getSimpleName();
    private static final String KEY_STATE_BUNDLE = "localActivityManagerState";
	
	private LocalActivityManager mLocalActivityManager;

	protected abstract Class<? extends Activity> getActivityClass();
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Intent intent = new Intent(getActivity(), getActivityClass());
        
        final Window w = getLocalActivityManager().startActivity(ACTIVITY_TAG, intent);
        final View wd = w != null ? w.getDecorView() : null;
       
        if (wd != null) {
            ViewParent parent = wd.getParent();
            if(parent != null) {
                ViewGroup v = (ViewGroup)parent;
                v.removeView(wd);
            }
            
            wd.setVisibility(View.VISIBLE);
            wd.setFocusableInTouchMode(true);
            if(wd instanceof ViewGroup) {
                ((ViewGroup) wd).setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
            }
        }
        return wd;
    }

    /**
     * For accessing public methods of the hosted activity
     */
    public Activity getHostedActivity() {
		return getLocalActivityManager().getActivity(ACTIVITY_TAG);
	}
    
    protected LocalActivityManager getLocalActivityManager() {
        return mLocalActivityManager;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(): " + getClass().getSimpleName());
        
        Bundle state = null;
        if(savedInstanceState != null) {
            state = savedInstanceState.getBundle(KEY_STATE_BUNDLE);
        }
        
        mLocalActivityManager = new LocalActivityManager(getActivity(), true);
        mLocalActivityManager.dispatchCreate(state);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(KEY_STATE_BUNDLE, mLocalActivityManager.saveInstanceState());
    }
    
    @Override
    public void onResume() {
        super.onResume();
        log("onResume(): " + getClass().getSimpleName());
        mLocalActivityManager.dispatchResume();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        log("onPause(): " + getClass().getSimpleName());
        mLocalActivityManager.dispatchPause(getActivity().isFinishing());
    }    
    
    @Override
    public void onStop() {
        super.onStop();
        log("onStop(): " + getClass().getSimpleName());
        mLocalActivityManager.dispatchStop();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        log("onDestroy(): " + getClass().getSimpleName());
        mLocalActivityManager.dispatchDestroy(getActivity().isFinishing());
    }
    private void log(String message){
    	Log.d(TAG, message);
    }
}
