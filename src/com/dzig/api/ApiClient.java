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
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.Date;

public class ApiClient {

    private final String TAG = ApiClient.class.getSimpleName();

    private final String baseUrl;
    private final String clientVersion;
    private final DefaultHttpClient client;


    public ApiClient(Context context) {
        Logger.debug(TAG, "ApiClient init");
        client = new DefaultHttpClient();
        if (BuildConfig.DEBUG){
//            client.enableCurlLogging(TAG, Log.DEBUG);
        }
        baseUrl = context.getString(R.string.base_url);
        clientVersion = context.getString(R.string.client_version);
    }



    private HttpUriRequest createHttpRequest(BaseRequest<?> request) throws IOException {
        HttpUriRequest httpUriRequest = null;
        httpUriRequest = request.createHttpRequest(baseUrl);
        Logger.debug(TAG, "preparing request : " + httpUriRequest.getURI());
        if (httpUriRequest != null) {
            httpUriRequest.addHeader("Accept-Encoding", "gzip");
            httpUriRequest.addHeader("X-Client-Version", "1");

        }
        return httpUriRequest;
    }


    public <T extends BaseResponse> T execute(BaseRequest<T> request) {
        try {
            HttpUriRequest httpUriRequest = createHttpRequest(request);
            HttpResponse response = client.execute(httpUriRequest);
            request.parseResponse(response);
        } catch (IOException e) {
            Logger.error(TAG, "unable to execute request: " + e.getMessage(), e);
            return request.createErrorResponse("unable to execute request");
        }
        Logger.error(TAG, "This should newer happened " + request);
        return request.createErrorResponse("This should newer happened");
    }


}
