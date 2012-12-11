package com.dzig.api.response.user;


import com.dzig.api.response.BaseResponse;
import com.dzig.model.User;

public class UserResponse extends BaseResponse{

    private User user;

    public UserResponse(String errorMessage) {
        super(errorMessage);
    }

    public UserResponse(int status, String errorMessage) {
        super(status, errorMessage);
    }

    public UserResponse(User user){
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
