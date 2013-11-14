package it.bova.bioniccow.data.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class NoteTable_old {
	
	// Database table
	public static final String TABLE_NOTE = "note";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_TEXT = "text";
	public static final String COLUMN_NOTE_ID = "noteId"; //o taskserie?
	public static final String COLUMN_TASK_ID = "taskId"; //o taskserie?
	public static final String COLUMN_CREATED = "created";
	public static final String COLUMN_MODIFIED = "modified";
	
	public static final String[] allColumns = { 
		COLUMN_TITLE, COLUMN_TEXT, COLUMN_NOTE_ID,
		COLUMN_TASK_ID, COLUMN_CREATED, COLUMN_MODIFIED
		};
	
	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_NOTE
			+ "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_TITLE + " text not null,"
			+ COLUMN_TEXT + " text not null,"
			+ COLUMN_NOTE_ID + " text not null,"
			+ COLUMN_TASK_ID + " text not null,"
			+ COLUMN_CREATED + " integer not null,"
			+ COLUMN_MODIFIED + " integer"
			+ ");";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(NoteTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE);
		onCreate(database);
	}
	
}
