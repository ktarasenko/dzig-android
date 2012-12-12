package com.dzig.api.request.coordinates;


import com.dzig.api.ParseHelpers;
import com.dzig.api.request.BaseRequest;
import com.dzig.api.response.coordinates.GetCoordinatesResponse;
import com.dzig.model.Coordinate;
import org.json.JSONException;
import org.json.JSONObject;


public class GetCoordinatesRequest extends BaseRequest<GetCoordinatesResponse>{

    protected GetCoordinatesRequest() {
        super(BaseRequest.Method.GET, "coordinate");
    }

    /**
     * Requests for list of coordinates
     */
    public static GetCoordinatesRequest newInstance(){
        return new GetCoordinatesRequest();
    }

    /**
     * Requests for list of coordinates
     * @param creatorId  filtered by creatorId
     */
    public static GetCoordinatesRequest newInstance(String creatorId){
        return (GetCoordinatesRequest)new GetCoordinatesRequest().addParam("creatorId", creatorId);
    }

    @Override
    public GetCoordinatesResponse parseResponse(JSONObject response) throws JSONException{
        return new GetCoordinatesResponse(ParseHelpers.parseList(response.getJSONArray("data"), Coordinate.CREATOR));
    }

    @Override
    public GetCoordinatesResponse createErrorResponse(String message) {
        return new GetCoordinatesResponse(message);
    }

    @Override
    public GetCoordinatesResponse createErrorResponse(int statusCode, String message) {
        return new GetCoordinatesResponse(statusCode, message);
    }
}

