package com.dzig.api.request.coordinates;


import com.dzig.api.request.BaseRequest;
import com.dzig.api.response.coordinates.AddCoordinateResponse;
import com.dzig.model.Coordinate;
import org.json.JSONException;
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
     * @return  request object
     */
    public static AddCoordinateRequest newInstance(double lat, double lon, int accuracy){
       return (AddCoordinateRequest) new AddCoordinateRequest()
               .addParam("lat", lat)
               .addParam("lon", lon)
               .addParam("accuracy", accuracy);

    }

    @Override
    public AddCoordinateResponse parseResponse(JSONObject response) throws JSONException{
        return new AddCoordinateResponse(Coordinate.CREATOR.createFromJSON(response.getJSONObject("data")));
    }

    @Override
    public AddCoordinateResponse createErrorResponse(String message) {
        return new AddCoordinateResponse(message);
    }

    @Override
    public AddCoordinateResponse createErrorResponse(int statusCode, String message) {
        return new AddCoordinateResponse(statusCode, message);
    }
}
