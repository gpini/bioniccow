package it.bova.bioniccow.data.database;

import it.bova.rtmapi.Location;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LocationTable {
	
	// Database table
	public static final String TABLE_LOCATION = "location";
	//public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_ADDRESS = "address";
	public static final String COLUMN_VIEWABLE = "viewable";
	public static final String COLUMN_LATITUDE = "latitude";
	public static final String COLUMN_LONGITUDE = "longitude";
	public static final String COLUMN_ZOOM = "zoom";
	public static final String COLUMN_LOCATION_ID = "location_id";

	
	public static final String[] allColumns = { 
		COLUMN_NAME, COLUMN_ADDRESS, COLUMN_VIEWABLE,
		COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_ZOOM,
		COLUMN_LOCATION_ID
		};
	
	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table if not exists "
			+ TABLE_LOCATION
			+ "("
			//+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_NAME + " text not null,"
			+ COLUMN_ADDRESS + " text,"
			+ COLUMN_VIEWABLE + " bool,"
			+ COLUMN_LATITUDE + " real,"
			+ COLUMN_LONGITUDE + " real,"
			+ COLUMN_ZOOM + " integer,"
			+ COLUMN_LOCATION_ID + " text primary key"
			+ ");";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(NoteTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion);
	}
	
	public static ContentValues values(Location location) {
		ContentValues values = new ContentValues();
		values.put(LocationTable.COLUMN_LOCATION_ID, location.getId());
		values.put(LocationTable.COLUMN_NAME, location.getName());
		values.put(LocationTable.COLUMN_ADDRESS, location.getAddress());
		values.put(LocationTable.COLUMN_VIEWABLE, location.isViewable());
		values.put(LocationTable.COLUMN_LATITUDE, location.getLatitude());
		values.put(LocationTable.COLUMN_LONGITUDE, location.getLongitude());
		values.put(LocationTable.COLUMN_ZOOM, location.getZoom());
		return values;
	}
	
}
