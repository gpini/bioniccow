package it.bova.bioniccow.data.database;

import it.bova.rtmapi.TaskList;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TaskListTable {
	
	// Database table
	public static final String TABLE_TASKLIST = "tasklist";
	//public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_ARCHIVED = "archived";
	public static final String COLUMN_DELETED = "deleted";
	public static final String COLUMN_LOCKED = "locked";
	public static final String COLUMN_TASKLIST_ID = "tasklistId";
	public static final String COLUMN_POSITION = "position";
	public static final String COLUMN_SMART = "smart";
	public static final String COLUMN_SORT_ORDER = "sort_order";
	
	public static final String[] allColumns = { 
		COLUMN_NAME, COLUMN_ARCHIVED, COLUMN_DELETED,
		COLUMN_LOCKED, COLUMN_TASKLIST_ID, COLUMN_POSITION,
		COLUMN_SMART, COLUMN_SORT_ORDER
		};
	
	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table if not exists "
			+ TABLE_TASKLIST
			+ "("
			//+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_NAME + " text not null,"
			+ COLUMN_ARCHIVED + " bool not null,"
			+ COLUMN_DELETED + " bool not null,"
			+ COLUMN_LOCKED + " bool not null,"
			+ COLUMN_TASKLIST_ID + " text primary key,"
			+ COLUMN_POSITION + " integer not null,"
			+ COLUMN_SMART + " bool not null,"
			+ COLUMN_SORT_ORDER + " integer not null"
			+ ");";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
		
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(NoteTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion);
	}
	
	public static ContentValues values(TaskList tasklist) {
		ContentValues values = new ContentValues();
		values.put(TaskListTable.COLUMN_TASKLIST_ID, tasklist.getId());
		values.put(TaskListTable.COLUMN_NAME, tasklist.getName());
		values.put(TaskListTable.COLUMN_ARCHIVED, tasklist.isArchived());
		values.put(TaskListTable.COLUMN_DELETED, tasklist.isDeleted());
		values.put(TaskListTable.COLUMN_LOCKED, tasklist.isLocked());
		values.put(TaskListTable.COLUMN_POSITION, tasklist.getPosition());
		values.put(TaskListTable.COLUMN_SMART, tasklist.isSmart());
		values.put(TaskListTable.COLUMN_SORT_ORDER, tasklist.getSortOrder());
		return values;
	}
		
}
