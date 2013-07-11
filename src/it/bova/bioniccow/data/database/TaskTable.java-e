package it.bova.bioniccow.data.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TaskTable {
	// Database table
	public static final String TABLE_TASK = "task";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TASK_STATUS = "status";
	//task
	public static final String COLUMN_TASK_ID = "taskId";
	public static final String COLUMN_ADDED = "added";
	public static final String COLUMN_COMPLETED = "completed";
	public static final String COLUMN_DELETED = "deleted";
	public static final String COLUMN_DUE = "due";
	public static final String COLUMN_ESTIMATE = "estimate";
	public static final String COLUMN_HAS_DUE_TIME = "hasDueTime";
	public static final String COLUMN_POSTPONED = "postponed";
	public static final String COLUMN_PRIORITY = "priority";
	//taskserie
	public static final String COLUMN_TASKSERIE_ID = "taskserieId";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_LIST_ID = "listId";
	public static final String COLUMN_LOCATION_ID = "locationId";
	public static final String COLUMN_CREATED= "created";
	public static final String COLUMN_MODIFIED = "modified";
	public static final String COLUMN_RECURRENCE_IS_EVERY = "recurrenceIsEvery";
	public static final String COLUMN_RECURRENCE_FREQUENCY = "recurrenceFrequency";
	public static final String COLUMN_RECURRENCE_INTERVAL = "recurrenceInterval";
	public static final String COLUMN_RECURRENCE_OPTION_TYPE = "recurrenceOptionType";
	public static final String COLUMN_RECURRENCE_OPTION_VALUE = "recurrenceOptionValue";
	public static final String COLUMN_SOURCE = "source";
	public static final String COLUMN_URL = "url";
	
	public static final String[] allColumns = { COLUMN_TASK_STATUS, COLUMN_TASK_ID,
		COLUMN_ADDED, COLUMN_COMPLETED, COLUMN_DELETED, COLUMN_DUE, COLUMN_ESTIMATE,
		COLUMN_HAS_DUE_TIME, COLUMN_POSTPONED, COLUMN_PRIORITY,
		COLUMN_TASKSERIE_ID, COLUMN_NAME, COLUMN_LIST_ID, COLUMN_LOCATION_ID,
		COLUMN_CREATED, COLUMN_MODIFIED,
		COLUMN_RECURRENCE_IS_EVERY, COLUMN_RECURRENCE_FREQUENCY, COLUMN_RECURRENCE_INTERVAL,
		COLUMN_RECURRENCE_OPTION_TYPE, COLUMN_RECURRENCE_OPTION_VALUE,
		COLUMN_SOURCE, COLUMN_URL
	};

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_TASK
			+ "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_TASK_STATUS + " integer default 0,"
			+ COLUMN_TASK_ID + " text unique not null,"
			+ COLUMN_ADDED + " integer not null,"
			+ COLUMN_COMPLETED + " integer,"
			+ COLUMN_DELETED + " integer,"
			+ COLUMN_DUE + " integer,"
			+ COLUMN_ESTIMATE + " string,"
			+ COLUMN_HAS_DUE_TIME + " bool not null,"
			+ COLUMN_POSTPONED + " integer not null,"
			+ COLUMN_PRIORITY + " integer not null,"
			+ COLUMN_TASKSERIE_ID + " text not null,"
			+ COLUMN_NAME + " text not null,"
			+ COLUMN_LIST_ID + " text not null,"
			+ COLUMN_LOCATION_ID + " text not null,"
			+ COLUMN_CREATED + " integer,"
			+ COLUMN_MODIFIED + " integer,"
			+ COLUMN_RECURRENCE_IS_EVERY + " integer,"
			+ COLUMN_RECURRENCE_FREQUENCY + " integer,"
			+ COLUMN_RECURRENCE_INTERVAL + " integer,"
			+ COLUMN_RECURRENCE_OPTION_TYPE + " integer,"
			+ COLUMN_RECURRENCE_OPTION_VALUE + " text,"
			+ COLUMN_SOURCE + " text not null,"
			+ COLUMN_URL + " text"
			+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(NoteTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
		onCreate(database);
	}
}
