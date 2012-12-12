package com.dzig.loader;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import com.dzig.app.DzigApplication;
import com.dzig.api.request.BaseRequest;
import com.dzig.api.response.GetDataResponse;

public class BasicLoader<Data, Request extends BaseRequest<Response>,
        Response extends GetDataResponse<Data>> extends AsyncTaskLoader<Data> {

    private final Request request;

    public BasicLoader(Context context, Request request) {
        super(context);
        this.request = request;
    }

    @Override
    public Data loadInBackground() {
        return DzigApplication.client().execute(request).getData();
    }
}
