package com.dzig.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.dzig.DzigApplication;
import com.dzig.R;
import com.dzig.activities.HomeActivity;
import com.dzig.activities.LoginActivity;
import com.dzig.api.request.user.AuthRequest;
import com.dzig.api.response.user.UserResponse;
import com.dzig.fragments.dialog.AccountSelectionDialog;
import com.dzig.fragments.dialog.DialogListener;
import com.dzig.model.User;
import com.dzig.utils.Logger;

public class LoginFragmentSimple extends Fragment {



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_simple, container);
        try {
            View.OnClickListener activity = (View.OnClickListener) getActivity();

            view.findViewById(R.id.login_button).setOnClickListener(activity);
            view.findViewById(R.id.login_incognito).setOnClickListener(activity);
        }  catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString()
                    + " must implement DialogListener");
        }
        return view;
    }

}
