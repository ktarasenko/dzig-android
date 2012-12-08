package com.dzig.api.response;


import java.util.ArrayList;
import java.util.Date;

public abstract class GetDataResponse<T> extends BaseResponse {

    public abstract T getData();

    public GetDataResponse(String errorMessage) {
        super(errorMessage);
    }

    public GetDataResponse(int status, String errorMessage) {
        super(status, errorMessage);
    }

    public GetDataResponse(){

    }

}
