package com.dzig.activities;

import android.accounts.*;
import android.content.Intent;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import com.dzig.api.ApiClient;
import com.dzig.utils.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class LoginActivity extends FragmentActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        runAuthSnippet();
//		startActivity(new Intent(this, HomeActivity.class));
//		finish();
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
            HttpClient client = AndroidHttpClient.newInstance("android");
            HttpClientParams.setRedirecting(client.getParams(), true);
            CookieStore cookieStore = new BasicCookieStore();
            HttpContext httpContext = new BasicHttpContext();
            httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
            HttpResponse resp = client.execute(new HttpGet("http://dzig-gae.appspot.com/_ah/login?auth=" + Uri.encode(authToken) + "&continue=" + Uri.encode("http://dzig-gae.appspot.com/auth")), httpContext);

            Log.e(ApiClient.TAG,"http://dzig-gae.appspot.com/_ah/login?auth=" + Uri.encode(authToken) + "&continue=" + Uri.encode("http://dzig-gae.appspot.com/auth"));
            HttpEntity entity = resp.getEntity();
            InputStreamReader reader = null;
            if (entity != null) {
                try {
                    reader = new InputStreamReader(entity.getContent(), "UTF-8");
                    StringBuilder sb = new StringBuilder();
                    int l = -1;
                    char[] buf = new char[1024];
                    while ((l = reader.read(buf)) > -1){
                        sb.append(buf, 0, l);
                    }
                    Logger.debug(ApiClient.TAG, "Response received: " + sb);
                    authToken =   sb.toString();
                }finally {
                    if (reader != null){
                        reader.close();
                    }
                }
            }

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
