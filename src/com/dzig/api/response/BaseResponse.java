package com.dzig.api.response;

import java.util.Date;

public class BaseResponse {

    protected final int status;
    protected final Date asOf;
    protected String errorMessage;

    public BaseResponse(int status, Date asOf) {
        this.status = status;
        this.asOf = asOf;
    }

    public BaseResponse(String errorMessage) {
        this(500, new Date(), errorMessage);
    }

    public BaseResponse(int status, Date asOf, String errorMessage) {
        this(status, asOf);
        this.errorMessage = errorMessage;
    }

    public int getStatus() {
        return status;
    }

    public boolean isOk() {
        return status < 300;
    }

    public Date getAsOf() {
        return asOf;
    }

    public String getErrorMessage(){
        return errorMessage;
    }

}
