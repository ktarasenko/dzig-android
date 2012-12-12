package com.dzig.api.request.user;


import com.dzig.api.request.BaseRequest;
import com.dzig.api.response.user.UserResponse;
import com.dzig.model.User;
import org.json.JSONException;
import org.json.JSONObject;

@Deprecated
/**
 * Totally corrupted
 */
public class AuthRequest extends BaseRequest<UserResponse>{


    protected AuthRequest(boolean tokenAuth) {
        super(Method.GET, tokenAuth ?"../../_ah/login" : "auth");  //FIXME: this line stinks
    }

    public static AuthRequest newInstanceWebLogin(){
        return (AuthRequest)new AuthRequest(false).addParam("continueUrl", "login://success");
    }

    public static AuthRequest newInstanceTokenLogin(String token){
        return (AuthRequest)new AuthRequest(true).addParam("auth", token).addParam("continue", "http://google.com");
    }

    public static AuthRequest newInstanceLogout(){
        return (AuthRequest)new AuthRequest(false).addParam("method", "logout");
    }


    @Override
    protected UserResponse parseResponse(JSONObject response) throws JSONException {
//        grab session cookie
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
