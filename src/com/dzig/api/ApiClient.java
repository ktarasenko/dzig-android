package com.dzig.api;


import android.accounts.Account;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.util.Log;
import com.dzig.BuildConfig;
import com.dzig.api.request.user.GetUserRequest;
import com.dzig.app.DzigApplication;
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
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.concurrent.*;

public class ApiClient {

    public final static String TAG = ApiClient.class.getSimpleName();

    private final String baseUrl;
    private final String clientVersion;
    private final AndroidHttpClient client;
    private HttpContext httpContext;
    private final Semaphore lock;
    private static final int WAITING_TIMEOUT = 300;
    private final String baseUrlRoot;


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
        baseUrlRoot = "http://dzig-gae.appspot.com/";
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

    public void authenticate(String authToken){
         httpContext = AuthenticatedAppEngineContext.newInstance(baseUrlRoot, authToken);
    }

    public <T extends BaseResponse> T execute(BaseRequest<T> request) {
        try {
             T response = executeRequest(request);
            //check for unauthorized request
            if (response.getStatus() == 403 && !(request instanceof AuthRequest)){
                Account lastUser = DzigApplication.userManager().getLastUsedAccount();
                if (lastUser != null){
                    authenticate(DzigApplication.userManager().updateToken(null, lastUser, true));
                    UserResponse authResponse = executeRequest(GetUserRequest.newInstance());
                    if (authResponse.isOk()){
                        DzigApplication.userManager().updateCurrentUser(authResponse.getUser());
                        if (request instanceof GetUserRequest) {
                            response = (T)authResponse;
                        } else {
                            response =  executeRequest(request);
                        }
                    }
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
        if (DzigApplication.getInstance().isConnected()){
            if (lock.tryAcquire(WAITING_TIMEOUT, TimeUnit.SECONDS)){
                HttpResponse response = null;
                try {
                    Logger.debug(TAG, "lock aquired: ");
                    response = client.execute(httpUriRequest, httpContext);
                } finally {
                    lock.release();
                    Logger.debug(TAG, "lock released: ");
                }
                if (response !=null){
                    return  request.parseResponse(response);
                }
            } else {
                Logger.error(TAG, "lock not aquired:  timeout");
            }
        } else {
            return request.createErrorResponse(600, "No Internet Connection");
        }

        return request.createErrorResponse("unable to execute request");
    }
}
