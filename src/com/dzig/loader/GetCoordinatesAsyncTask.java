package com.dzig.loader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dzig.model.Coordinate;
import com.dzig.model.CoordinatesDb;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

public class GetCoordinatesAsyncTask extends AsyncTask<Void, Void, List<Coordinate>> {
	private final WeakReference<Context> contextRef;
	
	public GetCoordinatesAsyncTask(Context context){
		this.contextRef = new WeakReference<Context>(context);
	}
	
	@Override
	protected List<Coordinate> doInBackground(Void... params) {
		Context context = contextRef.get();
		if (context == null) return Collections.emptyList();
		CoordinatesDb coordinatesDb = new CoordinatesDb(context);
		Cursor cursor = coordinatesDb.getCoordinatesCursor();
		ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
		while(cursor.moveToNext()){
			coordinates.add(CoordinatesDb.parseCoordinate(cursor));
		}
		return coordinates;
	}
}
