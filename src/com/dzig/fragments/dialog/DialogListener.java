package com.dzig.fragments.dialog;


import android.support.v4.app.DialogFragment;

public interface DialogListener {

    public void onDialogPositiveClick(DialogFragment dialog);
    public void onDialogNegativeClick(DialogFragment dialog);

    public void onDialogItemClick(DialogFragment dialog, int which);
}
