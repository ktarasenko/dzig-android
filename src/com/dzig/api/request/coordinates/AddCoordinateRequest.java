package com.dzig.api.request.coordinates;


import com.dzig.api.request.BaseRequest;

public class AddCoordinateRequest extends BaseRequest{

    protected AddCoordinateRequest() {
        super(Method.POST, "coordinates");
    }

    public static BaseRequest newInstance(double lat, double lon, int accuracy, String creatorId){
       return new AddCoordinateRequest()
               .addParam("lat", lat)
               .addParam("lon", lon)
               .addParam("accuracy", accuracy)
               .addParam("creator", creatorId);

    }
}
