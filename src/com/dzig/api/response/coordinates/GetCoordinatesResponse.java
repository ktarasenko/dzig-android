package com.dzig.api.response.coordinates;


import com.dzig.api.response.BaseResponse;
import com.dzig.api.response.GetDataResponse;
import com.dzig.model.Coordinate;

import java.util.ArrayList;
import java.util.Date;

public class GetCoordinatesResponse extends GetDataResponse<ArrayList<Coordinate>>{

    ArrayList<Coordinate> coordinatesList = new ArrayList<Coordinate>(10);



    public GetCoordinatesResponse(String errorMessage) {
        super(errorMessage);
    }

    public GetCoordinatesResponse(int status, Date asOf, String errorMessage) {
        super(status, asOf, errorMessage);
    }

    public GetCoordinatesResponse(ArrayList<Coordinate> list){
        coordinatesList.addAll(list);
    }

    @Override
    public ArrayList<Coordinate> getData() {
        return new ArrayList<Coordinate>(coordinatesList);
    }
}
