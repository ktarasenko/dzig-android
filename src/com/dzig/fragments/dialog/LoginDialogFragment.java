package com.dzig.fragments.dialog;


import android.accounts.Account;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.dzig.R;
import com.dzig.activities.HomeActivity;
import com.dzig.activities.IntroActivity;
import com.dzig.api.request.user.GetUserRequest;
import com.dzig.api.response.user.UserResponse;
import com.dzig.api.task.BasicTask;
import com.dzig.app.DzigApplication;
import com.dzig.model.User;
import com.dzig.utils.UserPreferences;

public class LoginDialogFragment extends DialogFragment implements View.OnClickListener, ItemsListDialogListener<Account> {


    private static final String BUNDLE_LIST = "list";
    private static final String BUNDLE_LOAD_USER = "loadUser";
    public static final String TAG = LoginDialogFragment.class.getSimpleName();
    private View button;
    private View progress;
    private ItemsListDialogListener<Account> listener;



    public static LoginDialogFragment newInstance(Account[] accounts, ItemsListDialogListener<Account> listener) {
        LoginDialogFragment fragment = new LoginDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArray(BUNDLE_LIST, accounts);
        fragment.setArguments(args);
        fragment.setListener(listener);
        return fragment;
    }

    private void setListener(ItemsListDialogListener listener) {
        this.listener = listener;
    }
    
    
    public static LoginDialogFragment newInstance(boolean shouldLoadUser) {
        LoginDialogFragment fragment = new LoginDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(BUNDLE_LOAD_USER, shouldLoadUser);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments().containsKey(BUNDLE_LIST)) return super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_login_simple, container, false);
        button = v.findViewById(R.id.login_button);
        button.setOnClickListener(this);
        progress = v.findViewById(R.id.login_progress);
        if (getArguments().getBoolean(BUNDLE_LOAD_USER)){
            new LoadAndLaunch().execute(GetUserRequest.newInstance());
        }
        return v;
    }



    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        final Account[] accounts = (Account[]) getArguments().getParcelableArray(BUNDLE_LIST);
        final String[] values; values = new String[accounts.length];
        for (int i = 0; i < accounts.length; i++) {
            values[i] = accounts[i].name;
        }
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_account_title)
                .setItems(values, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                      listener.onItemSelected(accounts[which]);
                    }
                })
                .setNegativeButton(R.string.btn_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //do nothing
                            }
                        }
                )
                .create();
    }





    private void doAuthentication(Account account){
        new AsyncTask<Account, Void, UserResponse>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                if (progress != null){
                    button.setVisibility(View.GONE);
                    progress.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected UserResponse doInBackground(Account... account) {
                String authToken =  DzigApplication.userManager().updateToken(getActivity(),account[0], true);
                DzigApplication.client().authenticate(authToken);
                return DzigApplication.client().execute(GetUserRequest.newInstance());
            }

            @Override
            protected void onPostExecute(UserResponse userResponse) {
                if (userResponse.isOk()){
                    DzigApplication.userManager().updateCurrentUser(userResponse.getUser());
                }

                if (userResponse.isOk()){
                    onLoginSuccessful();
                } else {
                    if (progress != null){
                        button.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.GONE);
                    }
                    Toast.makeText(getActivity(), "Not authenticated ", Toast.LENGTH_SHORT).show();
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
             LoginDialogFragment.newInstance(accounts, this).show(getFragmentManager(), LoginDialogFragment.TAG);
        } else {
            doAuthentication(accounts[0]);
        }
    }

    private void doWebLogin() {
        Toast.makeText(getActivity(), "Web login not implemented yet", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_button:
                doLogin();
                break;
        }
    }

    private void onLoginSuccessful() {
        startActivity(new Intent(getActivity(), HomeActivity.class));
        getActivity().finish();
    }

    @Override
    public void onItemSelected(Account item) {
        doAuthentication(item);
    }



    private class LoadAndLaunch extends BasicTask<GetUserRequest, UserResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            button.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(UserResponse userResponse) {
            if (userResponse.isOk()){
                DzigApplication.userManager().updateCurrentUser(userResponse.getUser());
            }
            final boolean showIntro = UserPreferences.newInstance(getActivity()).isIntroRequired();
            if (showIntro) {
                startActivity(new Intent(getActivity(), IntroActivity.class));
            } else if (User.currentUser() != null){
                startActivity(new Intent(getActivity(), HomeActivity.class));
            } else {
                button.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                return;
            }
            getActivity().finish();
        }
    }
}
