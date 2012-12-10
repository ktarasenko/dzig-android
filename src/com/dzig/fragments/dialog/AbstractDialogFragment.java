package com.dzig.fragments.dialog;

import android.app.Activity;
import android.support.v4.app.DialogFragment;

public class AbstractDialogFragment extends DialogFragment {


    protected DialogListener listener;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (DialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement DialogListener");
        }
    }




    @Override
    public void onDetach() {
        super.onDetach();
        //paranoid mode. free the pointer on activity
        listener = null;
    }
}
