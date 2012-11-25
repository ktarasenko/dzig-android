package com.dzig.fragments;

import com.dzig.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class LoginFragment extends Fragment{
	public interface LoginFragmentContainer {
		void performLogin(String username, String password);
	}
	
	private LoginFragmentContainer fragmentContainer;
	
	@Override
	public void onDetach() {
		super.onDetach();
		fragmentContainer = null;
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		fragmentContainer = (LoginFragmentContainer) activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_login, container);
		final EditText login = (EditText)view.findViewById(R.id.login);
		final EditText password = (EditText)view.findViewById(R.id.password);
		view.findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (fragmentContainer != null){
					fragmentContainer.performLogin(login.getText().toString(), password.getText().toString());
				}
				
			}
		});
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
}
