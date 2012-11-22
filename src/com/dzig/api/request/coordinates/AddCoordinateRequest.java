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
    public static BaseRequest newInstance(double lat, double lon, int accuracy, String creatorId){
       return new AddCoordinateRequest()
               .addParam("lat", lat)
               .addParam("lon", lon)
               .addParam("accuracy", accuracy)
               .addParam("creator", creatorId);

    }

    @Override
    public AddCoordinateResponse parseResponse(int status,JSONObject response) throws JSONException{
        if (response != null && response.has("coordinate")){
           return new AddCoordinateResponse(status, new Date(),
                    Coordinate.CREATOR.createFromJSON(response.getJSONObject("coordinate")));
        }
        return new AddCoordinateResponse(status, new Date(), (Coordinate)null);
    }
}
