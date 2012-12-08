package com.dzig.api.request.user;


import com.dzig.api.request.BaseRequest;
import com.dzig.api.response.user.UserResponse;
import com.dzig.model.User;
import org.json.JSONException;
import org.json.JSONObject;

public class AuthRequest extends BaseRequest<UserResponse>{


    protected AuthRequest() {
        super(Method.POST, "auth");
    }



    public static AuthRequest newInstanceWebLogin(){
        return (AuthRequest)new AuthRequest().addParam("continueUrl", "login://success");
    }

    public static AuthRequest newInstanceTokenLogin(String token){
        return (AuthRequest)new AuthRequest().addParam("token", token);
    }

    public static AuthRequest newInstanceLogout(){
        return (AuthRequest)new AuthRequest().addParam("method", "logout");
    }


    @Override
    protected UserResponse parseResponse(JSONObject response) throws JSONException {
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
