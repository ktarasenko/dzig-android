package com.dzig.activities;

import android.accounts.*;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class LoginActivity extends FragmentActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//        runAuthSnippet();
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


    private void runAuthSnippet(){
        final TextView textView = new TextView(this);
        new AsyncTask<Void, Void, String>(){
            @Override
            protected String doInBackground(Void... voids) {
                return getAuthToken();
            }

            @Override
            protected void onPostExecute(String s) {
                textView.setText(s);
            }
        }.execute();

        setContentView(textView);
    }

    private String getAuthToken(){
        AccountManager am = AccountManager.get(this);
        Account[] mAccounts = am.getAccountsByType("com.google");

        AccountManagerFuture<Bundle> response =
                am.getAuthToken(mAccounts[0], "ah", null, this, null, null);


        Bundle authTokenBundle;
        String authToken = "not found";

        try {


            authTokenBundle = response.getResult();

            authToken = authTokenBundle.getString(AccountManager.KEY_AUTHTOKEN).toString();
            am.invalidateAuthToken("com.google", authToken);

            response =
                    am.getAuthToken(mAccounts[0], "ah", null, this, null, null);

            authTokenBundle = response.getResult();

            authToken = authTokenBundle.getString(AccountManager.KEY_AUTHTOKEN).toString();

            URLConnection conn= new URL("http://dzig-gae.appspot.com/_ah/login?auth="+ Uri.encode(authToken) + "&continue=" + Uri.encode("http://dzig-gae.appspot.com/auth")).openConnection();
            Log.e("api1", "http://dzig-gae.appspot.com/_ah/login?auth="+ Uri.encode(authToken) + "&continue=" + Uri.encode("http://dzig-gae.appspot.com/auth"));
            conn.getInputStream();
            authToken = conn.getURL().toString();


        } catch (OperationCanceledException e) {
            Log.e("api1", e.getMessage());
        } catch (AuthenticatorException e) {
            Log.e("api2", e.getMessage());
        } catch (Throwable e) {
            Log.e("api3", e.getMessage(), e);
        }
        return authToken;
    }
}
