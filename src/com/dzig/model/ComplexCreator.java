package com.dzig.model;


import android.os.Parcelable;
import org.json.JSONObject;

public interface ComplexCreator<T> extends Parcelable.Creator<T>{

    public T createFromJSON(JSONObject in);
}
