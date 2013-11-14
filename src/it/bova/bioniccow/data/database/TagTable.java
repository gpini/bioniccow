package it.bova.bioniccow.data.database;

import it.bova.rtmapi.Task;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TagTable {
	// Database table
	public static final String TABLE_TAG = "tag";
	//public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_TASK_ID = "taskId"; //o taskserie?
	
	public static final String[] allColumns = { COLUMN_NAME, COLUMN_TASK_ID	};
	
	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_TAG
			+ "("
			//+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_NAME + " text not null,"
			+ COLUMN_TASK_ID + " text not null,"
			+ "FOREIGN KEY (" + COLUMN_TASK_ID + ") REFERENCES " + TaskTable.TABLE_TASK + "(" + COLUMN_TASK_ID + ") ON DELETE CASCADE ON UPDATE CASCADE,"
			+ "PRIMARY KEY (" + COLUMN_TASK_ID + ", " + COLUMN_NAME + ") ON CONFLICT REPLACE"
			+ ");";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(TagTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion);
	}
	
	public static ContentValues values(Task task, String tag) {
		ContentValues values = new ContentValues();
		values.put(TagTable.COLUMN_TASK_ID, task.getId());
		values.put(TagTable.COLUMN_NAME, tag);
		return values;
	}
	
	
}
