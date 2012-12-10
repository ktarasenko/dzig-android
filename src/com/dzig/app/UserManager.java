package com.dzig.app;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import com.dzig.model.User;
import com.dzig.utils.Logger;
import com.dzig.utils.UserPreferences;

public class UserManager {

    private static final String TAG = UserManager.class.getSimpleName();

    private final AccountManager accountManager;
    private User currentUser;
    private String lastAccountName;


    public UserManager(Context context) {
        this.accountManager = AccountManager.get(context);
        this.lastAccountName = DzigApplication.userPreferences().getString(UserPreferences.PREF_LAST_USER);
    }


    /**
     * Gets all Google accounts registered in account manager
     * @return  array of accounts
     */
    public Account[] getAccounts(){
       return accountManager.getAccountsByType("com.google");
    }

    /**
     * Searches for last account user succeed to login
     * @return account that user logged in last time, null otherwise
     */
    public Account getLastUsedAccount(){
        if (lastAccountName != null){
            for (Account acc : getAccounts()){
                if (lastAccountName.equalsIgnoreCase(acc.name)){
                    return acc;
                }
            }
        }
        return null;
    }


    /**
     * Updates auth token. Performs network operation
     * NOT USE IN UI THREAD
     * @param activity
     * @param account
     * @param invalidateToken
     * @return
     */
    public synchronized String updateToken(Activity activity, Account account, boolean invalidateToken) {
        Logger.debug(TAG, "Updating auth toke for user " + account.name);
        String authToken = "null";
        try {
            AccountManagerFuture<Bundle> accountManagerFuture = accountManager.getAuthToken(account, "ah", null, activity, null, null);
            Bundle authTokenBundle = accountManagerFuture.getResult();
            authToken = authTokenBundle.getString(AccountManager.KEY_AUTHTOKEN).toString();
            if(invalidateToken) {
                accountManager.invalidateAuthToken("com.google", authToken);
                authToken = updateToken(activity, account, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return authToken;
    }


    public synchronized User getCurrentUser() {
        return currentUser;
    }

    public synchronized void updateCurrentUser(User user) {
       currentUser = user;
       lastAccountName = user.getEmail();
       DzigApplication.userPreferences().putString(UserPreferences.PREF_LAST_USER, lastAccountName);
    }
}
