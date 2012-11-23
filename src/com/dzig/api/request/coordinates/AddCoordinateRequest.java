package com.dzig.api.request.coordinates;


import com.dzig.api.request.BaseRequest;
import com.dzig.api.response.BaseResponse;
import com.dzig.api.response.coordinates.AddCoordinateResponse;
import com.dzig.model.Coordinate;
import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class AddCoordinateRequest extends BaseRequest<AddCoordinateResponse>{

    protected AddCoordinateRequest() {
        super(Method.POST, "coordinates");
    }

    /**
     *
     * @param lat - latitude
     * @param lon - longitude
     * @param accuracy - accuracy
     * @param creatorId - id of user that reporting the coordinate
     * @return  request object
     */
    public static AddCoordinateRequest newInstance(double lat, double lon, int accuracy, String creatorId){
       return (AddCoordinateRequest) new AddCoordinateRequest()
               .addParam("lat", lat)
               .addParam("lon", lon)
               .addParam("accuracy", accuracy)
               .addParam("creator", creatorId);

    }

    @Override
    public AddCoordinateResponse parseResponse(JSONObject response) throws JSONException{
        return new AddCoordinateResponse(Coordinate.CREATOR.createFromJSON(response.getJSONObject("coordinate")));
    }

    @Override
    public AddCoordinateResponse createErrorResponse(String message) {
        return new AddCoordinateResponse(message);
    }
}
