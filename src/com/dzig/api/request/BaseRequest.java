package com.dzig.api.request;


import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;

public class BaseRequest {

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

}
