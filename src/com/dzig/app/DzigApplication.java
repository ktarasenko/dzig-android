package com.dzig.app;


import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.dzig.api.ApiClient;
import com.dzig.utils.Logger;
import com.dzig.utils.UserPreferences;

public class DzigApplication extends Application{

    private static DzigApplication instance;
    private static final String TAG = "DzigApplication";

    private ApiClient client;
    private ConnectivityManager connectivityManager;
    private UserPreferences userPreferences;
    private UserManager userManager;

    public DzigApplication(){
        Logger.debug(TAG, "DzigApplication init");
        instance = this;
    }

    public static DzigApplication getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
//        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT > 8) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                    .detectAll()
//                    .penaltyLog()
//                    .build());
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                    .detectLeakedSqlLiteObjects()
//                    .detectLeakedClosableObjects()
//                    .penaltyLog()
//                    .penaltyDeath()
//                    .build());
//        }
        super.onCreate();
        Context context = getApplicationContext();
        Logger.debug(TAG, "DzigApplication onCreate");
        client = new ApiClient(context);
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        userPreferences = UserPreferences.newInstance(context);
        userManager = new UserManager(context);

    }

    public static ApiClient client(){
         return getInstance().client;
    }

    public static boolean isConnected(){
        NetworkInfo nInfo = getInstance().connectivityManager.getActiveNetworkInfo();
        if (nInfo != null){
            return nInfo.isConnectedOrConnecting();
        }

        return false;
    }


    public static UserPreferences userPreferences() {
        return getInstance().userPreferences;
    }

    public static UserManager userManager() {
        return getInstance().userManager;
    }
}
