package com.dzig.api.response.coordinates;


import com.dzig.api.response.BaseResponse;
import com.dzig.model.Coordinate;

import java.util.Date;

public class AddCoordinateResponse extends BaseResponse {

    private Coordinate coordinate;



    public AddCoordinateResponse(String errorMessage) {
        super(errorMessage);
    }

    public AddCoordinateResponse(int status, String errorMessage) {
        super(status, errorMessage);
    }

    public AddCoordinateResponse(Coordinate coordinate){
        this.coordinate = coordinate;
    }
}
