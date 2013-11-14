package it.bova.bioniccow.data.database;

import it.bova.rtmapi.Recurrence;
import it.bova.rtmapi.Task;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TaskTable {
	// Database table
	public static final String TABLE_TASK = "task";
	//public static final String COLUMN_ID = "_id";
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
			//+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_TASK_STATUS + " integer default 0,"
			+ COLUMN_TASK_ID + " text primary key,"
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
				+ oldVersion + " to " + newVersion);
	}
	
	public static ContentValues values(Task task) {
		ContentValues values = new ContentValues();
		values.put(TaskTable.COLUMN_TASK_ID, task.getId());
		values.put(TaskTable.COLUMN_ADDED, task.getAdded().getTime());
		if(task.getCompleted() != null)
			values.put(TaskTable.COLUMN_COMPLETED, task.getCompleted().getTime());
		if(task.getDeleted() != null)
			values.put(TaskTable.COLUMN_DELETED, task.getDeleted().getTime());
		if(task.getDue() != null)
			values.put(TaskTable.COLUMN_DUE, task.getDue().getTime());
		if(task.getEstimate() != null)
			values.put(TaskTable.COLUMN_ESTIMATE, task.getEstimate());
		values.put(TaskTable.COLUMN_HAS_DUE_TIME, boolToInt(task.getHasDueTime()));
		values.put(TaskTable.COLUMN_POSTPONED, task.getPostponed());
		values.put(TaskTable.COLUMN_PRIORITY, task.getPriority().ordinal());
		values.put(TaskTable.COLUMN_TASKSERIE_ID, task.getTaskserieId());
		values.put(TaskTable.COLUMN_NAME, task.getName());
		values.put(TaskTable.COLUMN_LIST_ID, task.getListId());
		values.put(TaskTable.COLUMN_LOCATION_ID, task.getLocationId());
		if(task.getCreated() != null)
			values.put(TaskTable.COLUMN_CREATED, task.getCreated().getTime());
		if(task.getModified() != null)
			values.put(TaskTable.COLUMN_MODIFIED, task.getModified().getTime());
		if(task.getRecurrence() != null) {
			Recurrence rec = task.getRecurrence();
			values.put(TaskTable.COLUMN_RECURRENCE_IS_EVERY, boolToInt(rec.isEvery()));
			values.put(TaskTable.COLUMN_RECURRENCE_INTERVAL, rec.getInterval());
			values.put(TaskTable.COLUMN_RECURRENCE_FREQUENCY, rec.getFrequency().ordinal());
			if(rec.hasOption()) {
				values.put(TaskTable.COLUMN_RECURRENCE_OPTION_TYPE, rec.getOption().ordinal());
				values.put(TaskTable.COLUMN_RECURRENCE_OPTION_VALUE, rec.getOptionValue());
			}
		}
		values.put(TaskTable.COLUMN_SOURCE, task.getSource());
		if(task.getUrl() != null)
			values.put(TaskTable.COLUMN_URL, task.getUrl());
		return values;
	}
	
	private static int boolToInt(boolean bool) {
		return bool == true ? 1 : 0;
	}	
}
