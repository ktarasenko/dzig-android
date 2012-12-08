package com.dzig.api;


import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.util.Log;
import com.dzig.BuildConfig;
import com.dzig.R;
import com.dzig.api.request.BaseRequest;
import com.dzig.api.response.BaseResponse;
import com.dzig.utils.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;
import java.util.concurrent.*;

public class ApiClient {

    public final static String TAG = ApiClient.class.getSimpleName();

    private final String baseUrl;
    private final String clientVersion;
    private final AndroidHttpClient client;
    private final Semaphore lock;
    private static final int WAITING_TIMEOUT = 300;


    public ApiClient(Context context) {
        Logger.debug(TAG, "ApiClient init");
        client = AndroidHttpClient.newInstance("android");
        lock = new Semaphore(1);
        if (BuildConfig.DEBUG){
            client.enableCurlLogging(TAG, Log.DEBUG);
        }
        baseUrl = context.getString(R.string.base_url, context.getString(R.string.api_version));
        clientVersion = context.getString(R.string.client_version);
    }



    private HttpUriRequest createHttpRequest(BaseRequest<?> request) throws IOException {
        HttpUriRequest httpUriRequest = null;
        httpUriRequest = request.createHttpRequest(baseUrl);
        Logger.debug(TAG, "preparing request : "  + httpUriRequest.getMethod() + " " + httpUriRequest.getURI());
        if (httpUriRequest != null) {
            httpUriRequest.addHeader("Accept-Encoding", "utf8");
            httpUriRequest.addHeader("X-Client-Version", "1");

        }
        return httpUriRequest;
    }


    public <T extends BaseResponse> T execute(BaseRequest<T> request) {
        try {
            HttpUriRequest httpUriRequest = createHttpRequest(request);
            if (lock.tryAcquire(WAITING_TIMEOUT, TimeUnit.SECONDS)){
                Logger.debug(TAG, "lock aquired: ");
                HttpResponse response = client.execute(httpUriRequest);
                lock.release();
                Logger.debug(TAG, "lock released: ");
                return request.parseResponse(response);
            } else {
                Logger.error(TAG, "lock not aquired:  timeout");
            }
        } catch (InterruptedException iex){
            Logger.error(TAG, "thread was interupted: " + iex.getMessage(), iex);
        } catch (IOException e) {
            Logger.error(TAG, "unable to execute request: " + e.getMessage(), e);
        }
        return request.createErrorResponse("unable to execute request");
    }
}
