package com.dzig.api.response;


import java.util.Date;

public class ErrorResponse extends BaseResponse{

    private final String errorMessage;

    public ErrorResponse(String msg) {
        super(500, new Date());
        this.errorMessage = msg;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
