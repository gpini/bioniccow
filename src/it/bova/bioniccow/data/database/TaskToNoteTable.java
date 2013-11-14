package it.bova.bioniccow.data.database;

import it.bova.rtmapi.Note;
import it.bova.rtmapi.Task;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TaskToNoteTable {
	
	// Database table
	public static final String TABLE_TASK_TO_NOTE = "task_to_note";
	//public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NOTE_ID = "noteId"; //o taskserie?
	public static final String COLUMN_TASK_ID = "taskId"; //o taskserie?
	
	public static final String[] allColumns = { 
		COLUMN_NOTE_ID, COLUMN_TASK_ID
	};
	
	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_TASK_TO_NOTE
			+ "("
			//+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_NOTE_ID + " text not null,"
			+ COLUMN_TASK_ID + " text not null,"
			+ "FOREIGN KEY (" + COLUMN_TASK_ID + ") ON " + TaskTable.TABLE_TASK + "(" + COLUMN_TASK_ID + ") ON DELETE CASCADE ON UPDATE CASCADE,"
			+ "FOREIGN KEY (" + COLUMN_NOTE_ID + ") ON " + NoteTable.TABLE_NOTE + "(" + COLUMN_NOTE_ID + ") ON DELETE CASCADE ON UPDATE CASCADE,"
			+ "PRIMARY KEY (" + COLUMN_TASK_ID + ", " + COLUMN_NOTE_ID + ") ON CONFLICT REPLACE"
			+ ");";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(NoteTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion);
	}
	
	public static ContentValues values(Task task, Note note) {
		return values(task.getId(), note);
	}
	
	public static ContentValues values(String taskId, Note note) {
		ContentValues values = new ContentValues();
		values.put(TaskToNoteTable.COLUMN_TASK_ID, taskId);
		values.put(TaskToNoteTable.COLUMN_NOTE_ID, note.getId());
		return values;
	}
	
}
