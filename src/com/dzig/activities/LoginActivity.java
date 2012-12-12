package com.dzig.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;
import com.dzig.app.DzigApplication;
import com.dzig.R;
import com.dzig.api.request.user.AuthRequest;
import com.dzig.api.response.user.UserResponse;
import com.dzig.fragments.dialog.AccountSelectionDialog;
import com.dzig.fragments.dialog.DialogListener;
import com.dzig.utils.Logger;

public class LoginActivity extends FragmentActivity  implements View.OnClickListener, DialogListener {


    private static final String TAG = LoginActivity.class.getSimpleName();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

	}


    private void runAuthSnippet(Account account){
        new AsyncTask<Account, Void, UserResponse>(){
            @Override
            protected UserResponse doInBackground(Account... account) {
                String authToken =  DzigApplication.userManager().updateToken(LoginActivity.this,account[0], true);
                return DzigApplication.client().execute(AuthRequest.newInstanceTokenLogin(authToken));
            }

            @Override
            protected void onPostExecute(UserResponse userResponse) {
                Logger.debug(TAG, userResponse.getStatus() + " " + userResponse.getUser());
                if (userResponse.isOk()){
                    Toast.makeText(LoginActivity.this, "Authenticated as " + userResponse.getUser().getNickName(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Not authenticated " , Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(account);

    }


    private void doLogin() {
        Account[] accounts = DzigApplication.userManager().getAccounts();
        final int size = accounts.length;
        if (size == 0){
            doWebLogin();
        } else if (size > 1){
            AccountSelectionDialog.newInstance(accounts).show(getSupportFragmentManager(), AccountSelectionDialog.TAG);
        } else {
            runAuthSnippet(accounts[0]);
        }
    }

    private void doWebLogin() {
        Toast.makeText(this, "Web login not implemented yet", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        //DO nothing
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        //DO nothing
    }

    @Override
    public void onDialogItemClick(DialogFragment dialog, int which) {
        runAuthSnippet(((AccountSelectionDialog)dialog).getItem(which));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_button:
                doLogin();
                break;
            case R.id.login_incognito:
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                break;
        }
    }

}
