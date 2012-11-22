package com.dzig.api;


import android.os.Parcelable;
import com.dzig.model.ComplexCreator;
import com.dzig.model.Coordinate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class ParseHelpers {
    public static Date parseDate(String date) {
        return new Date();
    }

    public static Coordinate parseCoordinate(JSONObject coordinate) {
        return null;
    }


    public <T> ArrayList<T> parseList(JSONArray array, ComplexCreator<T> creator) throws JSONException {
        if (array != null && array.length() > 0) {
            ArrayList<T> list =  new ArrayList<T>(array.length());
            for (int i = 0; i < array.length(); i++){
                list.add(creator.createFromJSON(array.getJSONObject(i)));
            }
        } return new ArrayList<T>(0);
    }

}
