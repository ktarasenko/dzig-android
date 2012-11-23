package com.dzig.api.response;

import java.util.Date;

public class BaseResponse {

    protected int status;
    protected Date asOf;
    protected String errorMessage;



    public BaseResponse(String errorMessage) {
        this(500, new Date(), errorMessage);
    }

    public BaseResponse(int status, Date asOf, String errorMessage) {
        setMeta(status, asOf);
        this.errorMessage = errorMessage;
    }

    public BaseResponse(){

    }

    public void setMeta(int status, Date date){
       this.status = status;
        this.asOf = date;
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
