package it.bova.bioniccow.data.database;

import it.bova.rtmapi.Contact;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TaskToContactTable {
	
	// Database table
	public static final String TABLE_TASK_TO_CONTACT = "task_to_contact";
	//public static final String COLUMN_ID = "_id";
	public static final String COLUMN_CONTACT_ID = "contactId";
	public static final String COLUMN_TASK_ID = "taskId"; //o taskserie?
	
	public static final String[] allColumns = { 
		COLUMN_CONTACT_ID, COLUMN_TASK_ID
	};
	
	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table if not exists "
			+ TABLE_TASK_TO_CONTACT
			+ "("
			//+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_CONTACT_ID + " text not null,"
			+ COLUMN_TASK_ID + " text not null,"
			+ "FOREIGN KEY (" + COLUMN_TASK_ID + ") REFERENCES " + TaskTable.TABLE_TASK + "(" + COLUMN_TASK_ID + ") ON DELETE CASCADE ON UPDATE CASCADE,"
			+ "FOREIGN KEY (" + COLUMN_CONTACT_ID + ") REFERENCES " + ContactTable.TABLE_CONTACT + "(" + COLUMN_CONTACT_ID + ") ON DELETE CASCADE ON UPDATE CASCADE,"
			+ "PRIMARY KEY (" + COLUMN_TASK_ID + ", " + COLUMN_CONTACT_ID + ") ON CONFLICT REPLACE"
			+ ");";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(NoteTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion);
	}
	
	public static ContentValues values(String taskId, Contact contact) {
		ContentValues values = new ContentValues();
		values.put(TaskToContactTable.COLUMN_TASK_ID, taskId);
		values.put(TaskToContactTable.COLUMN_CONTACT_ID, contact.getId());
		return values;
	}
	
}
