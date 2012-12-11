package com.dzig.api.task;


import com.dzig.api.request.user.GetUserRequest;
import com.dzig.api.response.user.UserResponse;
import com.dzig.app.DzigApplication;

public class GetUserTask extends BasicTask<GetUserRequest, UserResponse>{

    @Override
    protected void onPostExecute(UserResponse userResponse) {
        if (userResponse.isOk()){
            DzigApplication.userManager().updateCurrentUser(userResponse.getUser());
        }

    }
}
