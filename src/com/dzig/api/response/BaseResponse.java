package com.dzig.api.response;

import java.util.Date;

/**
 * Base response entity
 * Status code 600 reserved for NO INTERNET CONNECTION ERROR
 */
public class BaseResponse {

    protected int status;
    protected String asOf;
    protected String errorMessage;


    public BaseResponse(String errorMessage) {
        this(500, errorMessage);
    }

    public BaseResponse(int status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public BaseResponse(){

    }

    public void setMeta(int status, String date){
       this.status = status;
        this.asOf = date;
    }

    public int getStatus() {
        return status;
    }

    public boolean isOk() {
        return status < 300;
    }

    public boolean isConnected() {
        return status != 600;
    }

    public String getAsOf() {
        return asOf;
    }

    public String getErrorMessage(){
        return errorMessage;
    }

}
