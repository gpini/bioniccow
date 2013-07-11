package it.bova.bioniccow.data.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ContactTable {
	// Database table
	public static final String TABLE_CONTACT = "contact";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_FULLNAME = "fullname";
	public static final String COLUMN_USERNAME = "username";
	public static final String COLUMN_CONTACT_ID = "contactId";
	public static final String COLUMN_TASK_ID = "taskId"; //o taskserie?
	
	public static final String[] allColumns = { 
		COLUMN_FULLNAME, COLUMN_USERNAME, 
		COLUMN_CONTACT_ID, COLUMN_TASK_ID
		};
	
	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_CONTACT
			+ "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_FULLNAME + " text not null,"
			+ COLUMN_USERNAME + " text not null,"
			+ COLUMN_CONTACT_ID + " text not null,"
			+ COLUMN_TASK_ID + " text not null"
			+ ");";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(ContactTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT);
		onCreate(database);
	}
	
}
