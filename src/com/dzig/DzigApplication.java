package com.dzig;


import android.app.Application;
import android.os.Build;
import android.os.StrictMode;
import com.dzig.api.ApiClient;
import com.dzig.api.request.coordinates.GetCoordinatesRequest;
import com.dzig.utils.Logger;

public class DzigApplication extends Application{

    private static DzigApplication instance;
    private static final String TAG = "DzigApplication";

    private ApiClient client;

    public DzigApplication(){
        super();
        Logger.debug(TAG, "DzigApplication init");
        instance = this;
    }

    public static DzigApplication getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT > 8) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
        super.onCreate();
        Logger.debug(TAG, "DzigApplication onCreate");
        client = new ApiClient(getApplicationContext());

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//               client.execute(GetCoordinatesRequest.newInstance());
//            }
//        }).start();

    }
}
