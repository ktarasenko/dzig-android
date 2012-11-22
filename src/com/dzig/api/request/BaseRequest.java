package com.dzig.api.request;


import android.util.JsonReader;
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

import java.io.BufferedReader;
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
                            .append(pair.getValue())
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


    public BaseRequest addParam(String name, String value){
        params.add(new BasicNameValuePair(name, value));
        return this;
    }

    public BaseRequest addParam(String name, double value){
        params.add(new BasicNameValuePair(name, String.valueOf(value)));
        return this;
    }

    public BaseRequest addParam(String name, int value){
        params.add(new BasicNameValuePair(name, String.valueOf(value)));
        return this;
    }

    public BaseRequest addParam(String name, Object value){
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
        final int statusCode = response.getStatusLine().getStatusCode();
        HttpEntity entity = response.getEntity();
        BufferedReader reader = null;
        if (entity != null) {
            try {
                reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                while (reader.ready()){
                    sb.append(reader.readLine());
                }
                JSONObject obj = new JSONObject(sb.toString());

                if (statusCode < 400) {
                    parseResponse(statusCode, obj);
                } else {
                    parseErrorResponse(statusCode, obj);
                }
            } catch (JSONException jex){
                Logger.error(TAG, "Unable to parse json", jex);
            } finally {
                if (reader != null){
                    reader.close();
                }
            }
        }

        return createErrorResponse("Unable to parse the output");
    }

    private T parseErrorResponse(int statusCode, JSONObject obj) {
        return (T) new BaseResponse(statusCode, ParseHelpers.parseDate(obj.optString("asOf")),  obj.optString("errorMessage", "Unknown error"));
    }

    public abstract T parseResponse(int status, JSONObject response);

    public T createErrorResponse(String message){
       return  (T) new BaseResponse(message);
    }
}
