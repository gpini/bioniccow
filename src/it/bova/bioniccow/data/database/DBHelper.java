package it.bova.bioniccow.data.database;

import it.bova.bioniccow.data.Folder;
import it.bova.bioniccow.data.Serializer;
import it.bova.rtmapi.Location;
import it.bova.rtmapi.TaskList;

import java.io.IOException;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "tasks.db";
	private static final int DATABASE_VERSION = 2;
	private Context context;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	// Method is called during creation of the database
	@Override public void onCreate(SQLiteDatabase database) {
		TaskTable.onCreate(database);
		ContactTable.onCreate(database);
		TaskToContactTable.onCreate(database);
		NoteTable.onCreate(database);
		TaskToNoteTable.onCreate(database);
		TagTable.onCreate(database);
		TaskListTable.onCreate(database);
		LocationTable.onCreate(database);
		FolderTable.onCreate(database);
	}

	// Method is called during an upgrade of the database,
	// e.g. if you increase the database version
	@Override public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		if(oldVersion == 1) {
			//create new tasklist, location and folder tables
			database.execSQL("DROP TABLE IF EXISTS tasklist;");
			TaskListTable.onCreate(database);
			try {
				List<TaskList> tasklists = new Serializer<List<TaskList>>("lists2.dat", context).deserialize();
				if(tasklists != null) {
					for(TaskList tasklist : tasklists) {
						ContentValues tasklistValues = TaskListTable.values(tasklist);
						database.insert(TaskListTable.TABLE_TASKLIST, null, tasklistValues);
					}
				}
			} catch (IOException e) {
				//do nothing (empty list)
			} 
			database.execSQL("DROP TABLE IF EXISTS location;");
			LocationTable.onCreate(database);
			try {
				List<Location> locations = new Serializer<List<Location>>("locations2.dat", context).deserialize();
				if(locations != null) {
					for(Location location : locations) {
						ContentValues locationValues = LocationTable.values(location);
						database.insert(LocationTable.TABLE_LOCATION, null, locationValues);
					}
				}
			} catch (IOException e) {
				//do nothing (empty list)
			}
			database.execSQL("DROP TABLE IF EXISTS folder;");
			FolderTable.onCreate(database);
			try {
				List<Folder> folders = new Serializer<List<Folder>>("folders2.dat", context).deserialize();
				if(folders != null) {
					for(Folder folder : folders) {
						ContentValues folderValues = FolderTable.values(folder);
						database.insert(FolderTable.TABLE_FOLDER, null, folderValues);
					}
				}
			} catch (IOException e) {
				//do nothing (empty list)
			}
			//modify task table
			database.execSQL("CREATE TEMPORARY TABLE tmpTask AS SELECT * FROM task;");
			database.execSQL("DROP TABLE task;");
			TaskTable.onCreate(database);
			String newTaskFields = "status, added, completed, deleted, due, estimate, "
					+ "hasDueTime, postponed, priority, taskserieId, name, listId, locationId, "
					+ "created, modified, recurrenceIsEvery, recurrenceFrequency, recurrenceInterval, "
					+ "recurrenceOptionType, recurrenceOptionValue, source, url, taskId";
			database.execSQL("INSERT INTO task (" + newTaskFields + ") SELECT " + newTaskFields + " FROM tmpTask;");
			database.execSQL("DROP TABLE tmpTask;");
			//create and populate new contact tables
			database.setTransactionSuccessful();
			database.endTransaction();
			String contactPath = context.getDatabasePath("contacts.db").getPath();
			database.execSQL("ATTACH DATABASE '" + contactPath + "' AS dbc;");
			//database.beginTransaction();
			database.execSQL("DROP TABLE IF EXISTS task_to_contact;");
			database.execSQL("DROP TABLE IF EXISTS contact;");
			ContactTable.onCreate(database);
			TaskToContactTable.onCreate(database);
			database.execSQL("INSERT INTO task_to_contact (taskId, contactId) SELECT DISTINCT task.taskId, contactId FROM task JOIN dbc.contact ON task.taskId = dbc.contact.taskId;");
			database.execSQL("INSERT INTO contact (fullname, username, contactId) SELECT DISTINCT fullname, username, contactId FROM dbc.contact;");
			//create and populate new tag table
			String tagPath = context.getDatabasePath("tags.db").getPath();
			database.execSQL("ATTACH DATABASE '" + tagPath + "' AS dbt;");
			database.execSQL("DROP TABLE IF EXISTS tag;");
			TagTable.onCreate(database);
			database.execSQL("INSERT INTO tag (name, taskId) SELECT name, taskId FROM dbt.tag;");
			//create and populate new note tables
			String notePath = context.getDatabasePath("notes.db").getPath();
			database.execSQL("ATTACH DATABASE '" + notePath + "' AS dbn;");
			database.execSQL("DROP TABLE IF EXISTS task_to_note;");
			database.execSQL("DROP TABLE IF EXISTS note;");
			NoteTable.onCreate(database);
			TaskToNoteTable.onCreate(database);
			database.execSQL("INSERT INTO note (title, text, created, modified, noteId) SELECT title, text, created, modified, noteId FROM dbn.note;");
			database.execSQL("INSERT INTO task_to_note (task.taskId, notetId) SELECT taskId, notetId FROM task JOIN dbn.note ON task.taskId = dbn.note.taskId;");
			//clean unused db
			database.execSQL("DROP TABLE dbc.contact;");
			database.execSQL("DETACH DATABASE '" + contactPath + "';");
			database.execSQL("DROP TABLE dbn.note;");
			database.execSQL("DETACH DATABASE '" + notePath + "';");
			database.execSQL("DROP TABLE dbt.tag;");
			database.execSQL("DETACH DATABASE '" + tagPath + "';");
			
			
		}
	}
} 
