package com.dzig.fragments.dialog;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.dzig.R;

public class ProgressDialog extends DialogFragment {

    private static final String BUNDLE_MESSAGE = "message";

    public static final String TAG = ProgressDialog.class.getSimpleName();

    public static ProgressDialog newInstance(String message) {
        ProgressDialog fragment = new ProgressDialog();
        Bundle args = new Bundle();
        args.putString(BUNDLE_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    public static ProgressDialog newInstance() {
        return newInstance("");
    }

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        String message = getArguments().getString(BUNDLE_MESSAGE);
        if (message == null){
        	message = getString(R.string.dialog_progress_message);
        }
        return new android.app.ProgressDialog.Builder(getActivity())
                .setMessage(message)
                .create();
    }

}
