package com.dzig.api.response;

import java.util.Date;

public class BaseResponse {

    private final int status;
    private final Date asOf;

    public BaseResponse(int status, Date asOf) {
        this.status = status;
        this.asOf = asOf;
    }

    public int getStatus() {
        return status;
    }

    public boolean isOk() {
        return status == 200;
    }

    public Date getAsOf() {
        return asOf;
    }
}
