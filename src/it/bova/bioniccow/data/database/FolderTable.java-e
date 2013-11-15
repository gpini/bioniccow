package it.bova.bioniccow.data.database;

import it.bova.bioniccow.data.Folder;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class FolderTable {
	
	// Database table
	public static final String TABLE_FOLDER = "folder";
	public static final String COLUMN_FOLDER_ID = "folderId";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_RULE = "rule";
	public static final String COLUMN_APPLICABILITY = "applicability";
	
	public static final String[] allColumns = { 
		COLUMN_NAME, COLUMN_RULE, COLUMN_APPLICABILITY,
		COLUMN_FOLDER_ID
		};
	
	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table if not exists "
			+ TABLE_FOLDER
			+ "("
			+ COLUMN_NAME + " text not null,"
			+ COLUMN_RULE + " text,"
			+ COLUMN_APPLICABILITY + " integer,"
			+ COLUMN_FOLDER_ID + " integer primary key autoincrement"
			+ ");";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(NoteTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion);
	}
	
	public static ContentValues values(Folder folder) {
		ContentValues values = new ContentValues();
		values.put(FolderTable.COLUMN_NAME, folder.getName());
		values.put(FolderTable.COLUMN_RULE, folder.getRule());
		values.put(FolderTable.COLUMN_APPLICABILITY, folder.getApplicability().ordinal());
		return values;
	}
	
}
