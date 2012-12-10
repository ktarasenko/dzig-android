package com.dzig.fragments.dialog;


import android.accounts.Account;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        String message = getArguments().getString(BUNDLE_MESSAGE, getString(R.string.dialog_progress_message));
        return new android.app.ProgressDialog.Builder(getActivity())
                .setMessage(message)
                .create();
    }

}
