package com.dzig.api;


import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.util.Log;
import com.dzig.BuildConfig;
import com.dzig.DzigApplication;
import com.dzig.R;
import com.dzig.api.request.BaseRequest;
import com.dzig.api.request.user.AuthRequest;
import com.dzig.api.response.BaseResponse;
import com.dzig.api.response.user.UserResponse;
import com.dzig.utils.Logger;
import com.dzig.utils.UserPreferences;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.concurrent.*;

public class ApiClient {

    public final static String TAG = ApiClient.class.getSimpleName();

    private final String baseUrl;
    private final String clientVersion;
    private final AndroidHttpClient client;
    private final Semaphore lock;
    private String token;
    private static final int WAITING_TIMEOUT = 300;



    public ApiClient(Context context) {
        Logger.debug(TAG, "ApiClient init");
        client = AndroidHttpClient.newInstance("android");
        HttpClientParams.setRedirecting(client.getParams(), true);
        CookieStore cookieStore = new BasicCookieStore();
        HttpContext httpContext = new BasicHttpContext();
        httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        lock = new Semaphore(1);
        if (BuildConfig.DEBUG){
            client.enableCurlLogging(TAG, Log.DEBUG);
        }
        baseUrl = context.getString(R.string.base_url, context.getString(R.string.api_version));
        clientVersion = context.getString(R.string.client_version);
        token = UserPreferences.newInstance(context).getString("token");
    }


    public void updateToken(String token){
        this.token = token;
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
             T response = executeRequest(request);
            //check for unauthorized request
            if (response.getStatus() == 403){
                UserResponse authResponse = executeRequest(AuthRequest.newInstanceTokenLogin(token));
                if (authResponse.isOk()){
                    //TODO: update current user
                   response =  executeRequest(request);
                }
            }

            return response;

        } catch (InterruptedException iex){
            Logger.error(TAG, "thread was interupted: " + iex.getMessage(), iex);
        } catch (IOException e) {
            Logger.error(TAG, "unable to execute request: " + e.getMessage(), e);
        }
        return request.createErrorResponse("unable to execute request");
    }

    private <T extends BaseResponse> T executeRequest(BaseRequest<T> request)
                            throws InterruptedException, IOException {
        HttpUriRequest httpUriRequest = createHttpRequest(request);

        if (lock.tryAcquire(WAITING_TIMEOUT, TimeUnit.SECONDS)){
            Logger.debug(TAG, "lock aquired: ");
            HttpResponse response = client.execute(httpUriRequest);
            lock.release();
            Logger.debug(TAG, "lock released: ");
            return  request.parseResponse(response);
        } else {
            Logger.error(TAG, "lock not aquired:  timeout");
        }

        return request.createErrorResponse("unable to execute request");
    }
}
