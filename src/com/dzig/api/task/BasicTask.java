package com.dzig.api.task;


import android.os.AsyncTask;
import com.dzig.DzigApplication;
import com.dzig.api.request.BaseRequest;
import com.dzig.api.response.BaseResponse;

public class BasicTask<Request extends BaseRequest<Response>, Response extends BaseResponse>
    extends AsyncTask<Request, Void, Response>{

    @Override
    protected Response doInBackground(Request... baseRequests) {
        return DzigApplication.getInstance().getClient().execute(baseRequests[0]);
    }

}
