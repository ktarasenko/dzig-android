package com.dzig.api.request;


import android.net.Uri;
import com.dzig.api.ApiClient;
import com.dzig.api.ParseHelpers;
import com.dzig.api.response.BaseResponse;
import com.dzig.utils.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public abstract class BaseRequest<T extends  BaseResponse> {

    private static final String TAG = BaseRequest.class.getSimpleName();

    protected enum Method {
        GET, POST, PUT, DELETE
    }

    private final Method method;
    private final String url;

    private final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>(){
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (this.size() > 0)   {
                sb.append("?");
                for (NameValuePair pair : this){
                    sb.append(pair.getName())
                            .append("=")
                            .append(Uri.encode(pair.getValue(), "UTF-8"))
                            .append("&");
                }
            }
            return sb.toString();
        }
    };

    protected BaseRequest(Method method, String url){
        this.method = method;
        this.url = url;
    }


    public BaseRequest<T> addParam(String name, String value){
        params.add(new BasicNameValuePair(name, value));
        return this;
    }

    public BaseRequest<T> addParam(String name, double value){
        params.add(new BasicNameValuePair(name, String.valueOf(value)));
        return this;
    }

    public BaseRequest<T> addParam(String name, int value){
        params.add(new BasicNameValuePair(name, String.valueOf(value)));
        return this;
    }

    public BaseRequest<T> addParam(String name, Object value){
        params.add(new BasicNameValuePair(name, value.toString()));
        return this;
    }


    public HttpUriRequest createHttpRequest(String baseUrl) throws IOException {
        HttpUriRequest request = null;
        switch (method){
            case GET:
                request = new HttpGet(baseUrl + url + params.toString());
                break;
            case DELETE:
                request = new HttpDelete(baseUrl + url + params.toString());
                break;
            case PUT:
                request =  new HttpPut(baseUrl + url);
            case POST:
                request =  new HttpPost(baseUrl + url);
                break;
        }

        if (request instanceof HttpEntityEnclosingRequest){
            ((HttpEntityEnclosingRequest) request).setEntity(new UrlEncodedFormEntity(params));
        }

        return  request;
    }

    public T parseResponse(HttpResponse response) throws IOException{
        int statusCode = response.getStatusLine().getStatusCode();
        HttpEntity entity = response.getEntity();
        InputStreamReader reader = null;
        if (entity != null) {
            try {
                reader = new InputStreamReader(entity.getContent(), "UTF-8");
                StringBuilder sb = new StringBuilder();
                int l = -1;
                char[] buf = new char[1024];
                while ((l = reader.read(buf)) > -1){
                    sb.append(buf, 0, l);
                }
                Logger.debug(ApiClient.TAG, "Response received: " + sb);

                if (statusCode == 200) {
                    JSONObject obj = new JSONObject(sb.toString());
                    JSONObject meta = obj.getJSONObject("meta");
                    statusCode = meta.getInt("status");
                    String asOf = meta.getString("asOf");
                    T resp;
                    if (statusCode < 400){
                        resp =  parseResponse(obj);
                    } else {
                        resp =  parseErrorResponse(meta);
                    }
                    resp.setMeta(statusCode, asOf);
                    return resp;
                } else {
                    //something went wrong, grabbing the whole message
                    return createErrorResponse(statusCode, sb.toString());
                }
            } catch (JSONException jex){
                Logger.error(TAG, "Unable to parse json", jex);
            } finally {
                if (reader != null){
                    reader.close();
                }
            }
        }

        return createErrorResponse(statusCode, "Unable to read the output");
    }

    private T parseErrorResponse(JSONObject meta) {
        return createErrorResponse(ParseHelpers.parseErrorMessage(meta));
    }

    protected abstract T parseResponse(JSONObject response) throws JSONException;

    public abstract T createErrorResponse(String message);

    public abstract T createErrorResponse(int statusCode, String message);
}
