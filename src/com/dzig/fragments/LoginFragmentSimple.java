package com.dzig.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dzig.R;

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

//            view.findViewById(R.id.login_incognito).setOnClickListener(activity);
        }  catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString()
                    + " must implement DialogListener");
        }
        return view;
    }

}
