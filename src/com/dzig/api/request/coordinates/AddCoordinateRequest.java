package com.dzig.api.request.coordinates;


import com.dzig.api.request.BaseRequest;

public class AddCoordinateRequest extends BaseRequest{

    protected AddCoordinateRequest(Method method, String url) {
        super(Method.POST, "coordinates");
    }
}
