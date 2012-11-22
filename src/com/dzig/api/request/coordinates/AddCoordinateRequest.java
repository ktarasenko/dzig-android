package com.dzig.api.request.coordinates;


import com.dzig.api.request.BaseRequest;
import com.dzig.api.response.BaseResponse;
import com.dzig.api.response.coordinates.AddCoordinateResponse;
import org.apache.http.HttpResponse;
import org.json.JSONObject;

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
    public AddCoordinateResponse parseResponse(int status, JSONObject response) {
        return null;
    }
}
