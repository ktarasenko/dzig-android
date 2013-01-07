package com.dzig.loader;

import java.util.ArrayList;
import java.util.List;

import com.dzig.model.Coordinate;
import com.dzig.model.CoordinatesDb;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

public class CoordinatesLoader extends AsyncTaskLoader<List<Coordinate>> {

	public CoordinatesLoader(Context context) {
		super(context);
	}

	@Override
	public List<Coordinate> loadInBackground() {
		CoordinatesDb coordinatesDb = new CoordinatesDb(getContext());
		Cursor cursor = coordinatesDb.getCoordinatesCursor();
		ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
		while(cursor.moveToNext()){
			coordinates.add(CoordinatesDb.parseCoordinate(cursor));
		}
		return coordinates;
	}


}
