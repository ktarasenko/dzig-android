package com.dzig.api.request.coordinates;


import com.dzig.api.request.BaseRequest;

public class GetCoordinatesRequest extends BaseRequest{

    protected GetCoordinatesRequest(BaseRequest.Method method, String url) {
        super(BaseRequest.Method.GET, "coordinates");
    }
}

