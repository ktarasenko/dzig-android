package com.dzig.api;


import android.content.Context;
import android.net.http.AndroidHttpClient;
import com.dzig.R;
import com.dzig.api.request.BaseRequest;
import com.dzig.api.response.BaseResponse;
import com.dzig.api.response.ErrorResponse;
import com.dzig.utils.Logger;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;
import java.util.Date;

public class ApiClient {

    private final String TAG = ApiClient.class.getSimpleName();

    private final String baseUrl;
    private final String clientVersion;
    private final AndroidHttpClient client;


    public ApiClient(Context context) {
        client = AndroidHttpClient.newInstance("android");
        baseUrl = context.getString(R.string.base_url);
        clientVersion = context.getString(R.string.client_version);
    }



    private HttpUriRequest createHttpRequest(BaseRequest<? extends BaseResponse> request) throws IOException {
        HttpUriRequest httpUriRequest = null;
        httpUriRequest = request.createHttpRequest(baseUrl);
        if (httpUriRequest != null) {
            httpUriRequest.addHeader("Accept-Encoding", "gzip");
            httpUriRequest.addHeader("X-Client-Version", "1");

        }
        return httpUriRequest;
    }


    public BaseResponse execute(BaseRequest<? extends BaseResponse> request) {
        try {
            HttpUriRequest httpUriRequest = createHttpRequest(request);
//             client.execute(httpUriRequest);
            //parse response
        } catch (IOException e) {
            Logger.error(TAG, "unable to execute request", e);
            return new ErrorResponse("unable to execute request");
        }

        return new BaseResponse(200, new Date());
    }


}
