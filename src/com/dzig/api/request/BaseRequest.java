package com.dzig.api.request;


import android.util.Pair;

import java.util.ArrayList;

public class BaseRequest {

    protected enum Method {
        GET, POST, PUT, DELETE
    }

    private final Method method;
    private final String url;
    private final ArrayList<Pair<String, String>> params = new ArrayList<Pair<String, String>>();

    protected BaseRequest(Method method, String url){
        this.method = method;
        this.url = url;
    }


    protected void addParam(String name, String value){
        params.add(new Pair<String, String>(name, value));
    }

    protected void addParam(String name, double value){
        params.add(new Pair<String, String>(name, String.valueOf(value)));
    }

    protected void addParam(String name, int value){
        params.add(new Pair<String, String>(name, String.valueOf(value)));
    }

    protected void addParam(String name, Object value){
        params.add(new Pair<String, String>(name, value.toString()));
    }
}
