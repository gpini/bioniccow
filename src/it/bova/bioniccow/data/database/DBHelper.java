package it.bova.bioniccow.data.database;

import it.bova.bioniccow.data.Folder;
import it.bova.bioniccow.data.Preferences;
import it.bova.bioniccow.data.Preferences.PrefParameter;
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
		if(oldVersion <= 1) {
			Preferences prefs = new Preferences(this.context);
			
			//create new tasklist, location and folder tables
			
			//Tasklist
			boolean tasklistAlreadyDone = prefs.getBoolean(PrefParameter.TASKLIST_DB_UPGRADED, false);
			if(!tasklistAlreadyDone) {
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
				prefs.putBoolean(PrefParameter.TASKLIST_DB_UPGRADED, true);
				this.context.deleteFile("lists2.dat");
			}
			
			//Location
			boolean locationAlreadyDone = prefs.getBoolean(PrefParameter.LOCATION_DB_UPGRADED, false);
			if(!locationAlreadyDone) {
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
				prefs.putBoolean(PrefParameter.LOCATION_DB_UPGRADED, true);
				this.context.deleteFile("locations2.dat");
			}
			
			//Folder
			boolean folderAlreadyDone = prefs.getBoolean(PrefParameter.FOLDER_DB_UPGRADED, false);
			if(!folderAlreadyDone) {
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
				prefs.putBoolean(PrefParameter.FOLDER_DB_UPGRADED, true);
				this.context.deleteFile("folders2.dat");
			}
			
			//TASK
			boolean taskAlreadyDone = prefs.getBoolean(PrefParameter.TASK_DB_UPGRADED, false);
			if(!taskAlreadyDone) {
				database.execSQL("CREATE TEMPORARY TABLE tmpTask AS SELECT * FROM task;");
				database.execSQL("DROP TABLE task;");
				TaskTable.onCreate(database);
				String newTaskFields = "status, added, completed, deleted, due, estimate, "
						+ "hasDueTime, postponed, priority, taskserieId, name, listId, locationId, "
						+ "created, modified, recurrenceIsEvery, recurrenceFrequency, recurrenceInterval, "
						+ "recurrenceOptionType, recurrenceOptionValue, source, url, taskId";
				database.execSQL("INSERT INTO task (" + newTaskFields + ") SELECT " + newTaskFields + " FROM tmpTask;");
				database.execSQL("DROP TABLE tmpTask;");
				database.setTransactionSuccessful();
				database.endTransaction();
				prefs.putBoolean(PrefParameter.TASK_DB_UPGRADED, true);
			}
			
			//create and populate new contact tables
			String contactPath = context.getDatabasePath("contacts.db").getPath();
			boolean contactAlreadyDone = prefs.getBoolean(PrefParameter.CONTACT_DB_UPGRADED, false);
			if(!contactAlreadyDone) {
				database.execSQL("DROP TABLE IF EXISTS task_to_contact;");
				database.execSQL("DROP TABLE IF EXISTS contact;");
				database.execSQL("ATTACH DATABASE '" + contactPath + "' AS dbc;");
				//database.beginTransaction();
				ContactTable.onCreate(database);
				TaskToContactTable.onCreate(database);
				database.beginTransaction();
				database.execSQL("INSERT INTO task_to_contact (taskId, contactId) SELECT DISTINCT task.taskId, contactId FROM task JOIN dbc.contact ON task.taskId = dbc.contact.taskId;");
				database.execSQL("INSERT INTO contact (fullname, username, contactId) SELECT DISTINCT fullname, username, contactId FROM dbc.contact;");
				database.setTransactionSuccessful();
				database.endTransaction();
				this.context.deleteDatabase("contacts.db");
				prefs.putBoolean(PrefParameter.CONTACT_DB_UPGRADED, true);
				database.execSQL("DETACH DATABASE '" + contactPath + "';");
			}
			
			//create and populate new tag table
			String tagPath = context.getDatabasePath("tags.db").getPath();
			boolean tagAlreadyDone = prefs.getBoolean(PrefParameter.TAG_DB_UPGRADED, false);
			if(!tagAlreadyDone) {
				database.execSQL("DROP TABLE IF EXISTS tag;");
				database.execSQL("ATTACH DATABASE '" + tagPath + "' AS dbt;");
				TagTable.onCreate(database);
				database.execSQL("INSERT INTO tag (name, taskId) SELECT name, taskId FROM dbt.tag;");
				prefs.putBoolean(PrefParameter.TAG_DB_UPGRADED, true);
				database.execSQL("DETACH DATABASE '" + tagPath + "';");
				this.context.deleteDatabase("tags.db");
			}
			//create and populate new note tables
			String notePath = context.getDatabasePath("notes.db").getPath();
			boolean noteAlreadyDone = prefs.getBoolean(PrefParameter.NOTE_DB_UPGRADED, false);
			if(!noteAlreadyDone) {
				database.execSQL("DROP TABLE IF EXISTS task_to_note;");
				database.execSQL("DROP TABLE IF EXISTS note;");
				database.execSQL("ATTACH DATABASE '" + notePath + "' AS dbn;");
				NoteTable.onCreate(database);
				TaskToNoteTable.onCreate(database);
				database.beginTransaction();
				database.execSQL("INSERT INTO note (title, text, created, modified, noteId) SELECT DISTINCT title, text, created, modified, noteId FROM dbn.note;");
				database.execSQL("INSERT INTO task_to_note (taskId, noteId) SELECT DISTINCT taskId, noteId FROM dbn.note;");
				database.setTransactionSuccessful();
				database.endTransaction();
				prefs.putBoolean(PrefParameter.NOTE_DB_UPGRADED, true);
				database.execSQL("DETACH DATABASE '" + notePath + "';");
				this.context.deleteDatabase("notes.db");
			}
			
		}
	}
} 
