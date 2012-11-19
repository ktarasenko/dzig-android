package com.dzig.api.request.coordinates;


import com.dzig.api.request.BaseRequest;


public class GetCoordinatesRequest extends BaseRequest{

    protected GetCoordinatesRequest() {
        super(BaseRequest.Method.GET, "coordinates");
    }

    /**
     * Requests for list of coordinates
     */
    public static BaseRequest newInstance(){
        return new GetCoordinatesRequest();
    }

    /**
     * Requests for list of coordinates
     * @param creatorId  filtered by creatorId
     */
    public static BaseRequest newInstance(String creatorId){
        return new GetCoordinatesRequest().addParam("creator", creatorId);
    }
}

