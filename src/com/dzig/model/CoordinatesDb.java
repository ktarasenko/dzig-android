package com.dzig.model;

import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dzig.model.db.AColumn;
import com.dzig.model.db.AIndex;
import com.dzig.model.db.ATable;
import com.dzig.model.db.Table;
import com.dzig.model.db.Type;

public class CoordinatesDb extends SQLiteOpenHelper {
	private static final String DB_NAME = "data";
	private static final int DB_VERSION = 1;


	@ATable(name = "coordinates")
	static class Coords{
		@AColumn(type=Type.PRIMARY_KEY)
		public static final String ID = "id";
		
		@AColumn(type=Type.TEXT)
		public static final String UID = "uid";
		
		@AColumn(type=Type.TEXT)
		public static final String CREATOR_UID = "creator_uid";
		
		@AColumn(type=Type.INTEGER)
		public static final String DATE = "date";
		
		@AColumn(type=Type.REAL)
		public static final String LAT = "lat";
		
		@AColumn(type=Type.REAL)
		public static final String LON = "lon";
		
		@AColumn(type=Type.REAL)
		public static final String ACCURACY = "accuracy";
		
		
		@AIndex(columns="id")
		public static final String ID_INDEX = "id_index";
		
		@AIndex(columns="uid")
		public static final String UID_INDEX = "uid_index";
		
		@AIndex(columns="creator_uid")
		public static final String CREATOR_ID_INDEX = "creator_id_index";
	}
	
	@ATable(name="creator")
	static class Creator{
		@AColumn(type=Type.PRIMARY_KEY)
		public static final String ID = "id";
		
		@AColumn(type=Type.TEXT)
		public static final String UID = "uid";
		
		@AColumn(type=Type.TEXT)
		public static final String EMAIL = "email";
		
		@AColumn(type=Type.TEXT)
		public static final String NICKNAME = "nickName";
		
		@AColumn(type=Type.TEXT)
		public static final String AVATAR = "avatar";
		
		
		@AIndex(columns="id")
		public static final String ID_INDEX = "id_index";
		
		@AIndex(columns="uid")
		public static final String UID_INDEX = "uid_index";
	}
	
	private final Table coords = Table.newInstance(Coords.class);
	private final Table creators = Table.newInstance(Creator.class);
	
	public CoordinatesDb(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		creators.create(db);
		coords.create(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		coords.drop(db);
		creators.drop(db);
	}

	public void update(List<Coordinate> coordinates){
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			for (Coordinate coordinate : coordinates) {
				insertOrUpdateCoordinate(coordinate, db);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	public Cursor getCoordinatesCursor(){
		SQLiteDatabase db = getReadableDatabase();
		
		final String sql = "SELECT "+
				"c1."+Coords.UID+ //0
				", c1."+Coords.CREATOR_UID+ //1
				", c1."+Coords.DATE+ //2
				", c1."+Coords.LAT+ //3
				", c1."+Coords.LON+ //4
				", c1."+Coords.ACCURACY+ //5
				", c2."+Creator.EMAIL+ //6
				", c2."+Creator.NICKNAME+ //7
				", c2."+Creator.AVATAR+ //8
				", FROM "+coords.getTableName()+" as c1, "+creators.getTableName()+" as c2"+
				" WHERE c1."+Coords.CREATOR_UID+"=c2."+Creator.UID;
		return db.rawQuery(sql, null);
	}
	
	public static User parseUser(Cursor cursor){
		return new User(cursor.getString(1), cursor.getString(6), cursor.getString(7), cursor.getString(8));
	}
	
	public static Coordinate parseCoordinate(Cursor cursor){
		return new Coordinate(cursor.getString(0), parseUser(cursor), new Date(cursor.getLong(2)), cursor.getDouble(3), cursor.getDouble(4), cursor.getDouble(5));
	}
	
	private void insertOrUpdateCreator(User user, SQLiteDatabase db){
		ContentValues values = new ContentValues();
		values.put(Creator.EMAIL, user.getEmail());
		values.put(Creator.NICKNAME, user.getNickName());
		values.put(Creator.AVATAR, user.getAvatar());
		String table = creators.getTableName();
		
		if (db.update(table, values, Creator.UID+"=?", new String[]{user.getId()}) == 0) {
			values.put(Creator.UID, user.getId());
			db.insert(table, null, values);
		}
	}
	
	private void insertOrUpdateCoordinate(Coordinate coordinate, SQLiteDatabase db){
		insertOrUpdateCreator(coordinate.getCreator(), db);
		ContentValues values = new ContentValues();
		
		values.put(Coords.CREATOR_UID, coordinate.getCreator().getId());
		values.put(Coords.DATE, coordinate.getDate().getTime());
		values.put(Coords.LAT, coordinate.getLat());
		values.put(Coords.LON, coordinate.getLon());
		values.put(Coords.ACCURACY, coordinate.getAccuracy());
				
		String table =  coords.getTableName();
		if (db.update(table, values, Coords.UID+"=?", new String[]{coordinate.getId()}) == 0) {
			values.put(Coords.UID, coordinate.getId());
			db.insert(table, null, values);
		}
	}
}
