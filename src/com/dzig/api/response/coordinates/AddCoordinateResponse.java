package com.dzig.api.response.coordinates;


import com.dzig.api.response.BaseResponse;
import com.dzig.model.Coordinate;

import java.util.Date;

public class AddCoordinateResponse extends BaseResponse {

    private Coordinate coordinate;

    public AddCoordinateResponse(int status, Date asOf) {
        super(status, asOf);
    }

    public AddCoordinateResponse(String errorMessage) {
        super(errorMessage);
    }

    public AddCoordinateResponse(int status, Date asOf, String errorMessage) {
        super(status, asOf, errorMessage);
    }

    public AddCoordinateResponse(int status,Date asOf,Coordinate coordinate){
        super(status, asOf);
        this.coordinate = coordinate;
    }
}
