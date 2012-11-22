package com.dzig.api.response;


import java.util.ArrayList;
import java.util.Date;

public abstract class GetDataResponse<T> extends BaseResponse {

    public abstract T getData();

    public GetDataResponse(String errorMessage) {
        super(errorMessage);
    }

    public GetDataResponse(int status, Date asOf, String errorMessage) {
        super(status, asOf, errorMessage);
    }

    public GetDataResponse(){

    }

}
