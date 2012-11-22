package com.dzig.api.response.coordinates;


import com.dzig.api.response.BaseResponse;
import com.dzig.model.Coordinate;

import java.util.ArrayList;
import java.util.Date;

public class GetCoordinatesResponse extends BaseResponse{

    ArrayList<Coordinate> coordinatesList = new ArrayList<Coordinate>(10);

    public GetCoordinatesResponse(int status, Date asOf) {
        super(status, asOf);
    }

    public GetCoordinatesResponse(String errorMessage) {
        super(errorMessage);
    }

    public GetCoordinatesResponse(int status, Date asOf, String errorMessage) {
        super(status, asOf, errorMessage);
    }

    public ArrayList<Coordinate> getCoordinatesList() {
        return coordinatesList;
    }
}
