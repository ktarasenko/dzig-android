package com.dzig.fragments.dialog;


import android.accounts.Account;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import com.dzig.R;

public class AccountSelectionDialog extends AbstractDialogFragment{


    private static final String BUNDLE_LIST = "list";
    private Account[] accounts;
    public static final String TAG = AccountSelectionDialog.class.getSimpleName();

    public static AccountSelectionDialog newInstance(Account[] accounts) {
        AccountSelectionDialog fragment = new AccountSelectionDialog();
        Bundle args = new Bundle();
        args.putParcelableArray(BUNDLE_LIST, accounts);
        fragment.setArguments(args);
        return fragment;
    }

    public Account getItem(int which){
        return accounts[which];
    }

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        accounts = (Account[]) getArguments().getParcelableArray(BUNDLE_LIST);
        final String[] values; values = new String[accounts.length];
        for (int i = 0; i < accounts.length; i++) {
            values[i] = accounts[i].name;
        }
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_account_title)
                .setItems(values, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        listener.onDialogItemClick(AccountSelectionDialog.this, which);
                    }
                })
//                .setPositiveButton(R.string.btn_ok,
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int whichButton) {
//                                listener.onDialogPositiveClick(AccountSelectionDialog.this);
//                            }
//                        }
//                )
                .setNegativeButton(R.string.btn_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                listener.onDialogNegativeClick(AccountSelectionDialog.this);
                            }
                        }
                )
                .create();
    }


}
