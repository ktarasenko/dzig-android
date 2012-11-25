package com.dzig.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

public class LoginActivity extends FragmentActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		TextView textView = new TextView(this);
//		textView.setText(doGetAccounts());
//		setContentView(textView);
		startActivity(new Intent(this, HomeActivity.class));
		finish();
	}
	
	private String doGetAccounts() {
		AccountManager am = AccountManager.get(this);

		Account[] accounts = am.getAccounts();//am.getAccountsByType("com.google");
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < accounts.length; i++) {
			builder.append(accounts[i].name+" "+accounts[i].type+"\n");
		}
		return builder.toString();
	}
}
