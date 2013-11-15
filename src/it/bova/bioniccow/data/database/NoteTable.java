package it.bova.bioniccow.data.database;

import it.bova.rtmapi.Note;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class NoteTable {
	
	// Database table
	public static final String TABLE_NOTE = "note";
	//public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_TEXT = "text";
	public static final String COLUMN_NOTE_ID = "noteId"; //o taskserie?
	public static final String COLUMN_CREATED = "created";
	public static final String COLUMN_MODIFIED = "modified";
	
	public static final String[] allColumns = { 
		COLUMN_TITLE, COLUMN_TEXT, COLUMN_NOTE_ID,
		COLUMN_NOTE_ID, COLUMN_CREATED, COLUMN_MODIFIED
		};
	
	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table if not exists "
			+ TABLE_NOTE
			+ "("
			//+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_TITLE + " text not null,"
			+ COLUMN_TEXT + " text not null,"
			+ COLUMN_NOTE_ID + " text primary key,"
			+ COLUMN_CREATED + " integer not null,"
			+ COLUMN_MODIFIED + " integer"
			+ ");";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(NoteTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion);
	}
		
	public static ContentValues values(Note note) {
		ContentValues values = new ContentValues();
		values.put(NoteTable.COLUMN_NOTE_ID, note.getId());
		values.put(NoteTable.COLUMN_TITLE, note.getTitle());
		values.put(NoteTable.COLUMN_TEXT, note.getText());
		values.put(NoteTable.COLUMN_CREATED, note.getCreated().getTime());
		if(note.getModified() != null)
			values.put(NoteTable.COLUMN_MODIFIED, note.getModified().getTime());
		return values;
	}
	
}
