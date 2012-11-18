package com.dzig.api.request;


public class BaseRequest {

    protected enum Method {
        GET, POST, PUT, DELETE
    }

    private final Method method;
    private final String url;

    protected BaseRequest(Method method, String url){
        this.method = method;
        this.url = url;
    }


}
