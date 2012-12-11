package com.dzig.api.request.user;

import com.dzig.api.request.BaseRequest;
import com.dzig.api.response.user.UserResponse;
import com.dzig.model.User;
import org.json.JSONException;
import org.json.JSONObject;

public class GetUserRequest extends BaseRequest<UserResponse>{


    protected GetUserRequest() {
        super(Method.GET, "user");
    }


    public static GetUserRequest newInstance(){
        return new GetUserRequest();
    }


    @Override
    public UserResponse parseResponse(JSONObject response) throws JSONException {
        return new UserResponse(User.CREATOR.createFromJSON(response.getJSONObject("data")));
    }

    @Override
    public UserResponse createErrorResponse(String message) {
        return new UserResponse(message);
    }

    @Override
    public UserResponse createErrorResponse(int statusCode, String message) {
        return new UserResponse(statusCode, message);
    }
}
