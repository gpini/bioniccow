package it.bova.bioniccow.data.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import it.bova.rtmapi.Contact;
import it.bova.rtmapi.DeletedTask;
import it.bova.rtmapi.Note;
import it.bova.rtmapi.Task;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TaskDatabase {

	private static DBHelper dBHelper;
	private static SQLiteDatabase dB;

	private static int openedDBs = 0;

	public static synchronized void open(Context context) {
		if(context == null)
			throw new IllegalArgumentException("Context must be provided");
		if(dBHelper == null) 
			dBHelper = new DBHelper(context.getApplicationContext());
		openedDBs++;
		dB = dBHelper.getWritableDatabase();
	}

	public static synchronized void close() {
		openedDBs--;
		if(openedDBs < 1) {
			if(dB.inTransaction())
				dB.endTransaction();
			if(dB != null)
				dB.close();
		}
	}
	
	public static synchronized void beginTransaction() {
		checkOrThrow();
		dB.beginTransaction();
	}
	
	public static synchronized void endTransaction() {
		dB.setTransactionSuccessful();
		dB.endTransaction();
	}

	public static synchronized void putUsingTransactions(List<? extends Task> updatedTasks) {
		beginTransaction();
		for(Task task : updatedTasks)
			put(task);
		endTransaction();
	}

	public static synchronized long putUsingTransactions(Task task) {
		beginTransaction();
		long id = put(task);
		endTransaction();
		return id;
	}

	public static synchronized long put(Task task) {
		checkOrThrow();
		if(task != null) {
			ContentValues taskValues = TaskTable.values(task);
			long insertId = dB.insertWithOnConflict(TaskTable.TABLE_TASK, null,
					taskValues, SQLiteDatabase.CONFLICT_REPLACE);
			//insert tags
			dB.delete(TagTable.TABLE_TAG,
					TagTable.COLUMN_TASK_ID + "= ?",
					new String[]{task.getId()});
			for(String tag : task.getTags()) {
				ContentValues tagValues = TagTable.values(task, tag);
				dB.insert(TagTable.TABLE_TAG, null, tagValues);
			}
			//insert contacts
			dB.delete(TaskToContactTable.TABLE_TASK_TO_CONTACT,
					TaskToContactTable.COLUMN_TASK_ID + "= ?",
					new String[]{task.getId()});
			for(Contact contact : task.getParticipants()) {
				ContentValues contactValues = ContactTable.values(contact);
				dB.insertWithOnConflict(ContactTable.TABLE_CONTACT, null,
						contactValues, SQLiteDatabase.CONFLICT_REPLACE);
				ContentValues taskToContactValues = TaskToContactTable.values(task, contact);
				dB.insert(TaskToContactTable.TABLE_TASK_TO_CONTACT, null, taskToContactValues);
			}
			//insert notes
			dB.delete(TaskToNoteTable.TABLE_TASK_TO_NOTE,
					TaskToNoteTable.COLUMN_TASK_ID + "= ?",
					new String[]{task.getId()});
			for(Note note : task.getNotes()) {
				ContentValues noteValues = NoteTable.values(note);
				dB.insertWithOnConflict(NoteTable.TABLE_NOTE, null,
						noteValues, SQLiteDatabase.CONFLICT_REPLACE);
				ContentValues taskToNoteValues = TaskToNoteTable.values(task, note);
				dB.insert(TaskToNoteTable.TABLE_TASK_TO_NOTE, null, taskToNoteValues);
			}
			return insertId;
		}
		else return -1;
	}
	
	public static synchronized long insertNote(String taskId, Note note) {
		checkOrThrow();
		if(note != null) {
			ContentValues noteValues = NoteTable.values(note);
			long insertId = dB.insertWithOnConflict(NoteTable.TABLE_NOTE, null,
					noteValues, SQLiteDatabase.CONFLICT_REPLACE);
			ContentValues taskToNoteValues = TaskToNoteTable.values(taskId, note);
			dB.insert(TaskToNoteTable.TABLE_TASK_TO_NOTE, null, taskToNoteValues);

			return insertId;
		}
		else return -1;
	}
	
	public static synchronized int updateNote(Note note) {
		ContentValues values = new ContentValues();
		values.put(NoteTable.COLUMN_TITLE, note.getTitle());
		values.put(NoteTable.COLUMN_TEXT, note.getText());
		values.put(NoteTable.COLUMN_CREATED, note.getCreated().getTime());
		if(note.getModified() != null)
			values.put(NoteTable.COLUMN_MODIFIED, note.getModified().getTime());
			
		int updatedRows = dB.update(NoteTable.TABLE_NOTE,
				values, NoteTable.COLUMN_NOTE_ID + "= ?",
				new String[]{note.getId()});
		return updatedRows;
	}
	
	public static synchronized long removeNote(String noteId) {
		checkOrThrow();
		long deleteId = dB.delete(NoteTable.TABLE_NOTE,
				NoteTable.COLUMN_NOTE_ID + "= ?",
				new String[]{noteId});
		//ON DELETE cancella anche la riga in TaskToNoteTable
		return deleteId;
	}

	public static synchronized long remove(String taskId) {
		checkOrThrow();
		long deleteId = dB.delete(TaskTable.TABLE_TASK,
				TaskTable.COLUMN_TASK_ID + "= ?",
				new String[]{taskId});
		//ON DELETE cancellano automaticamente in TaskToNoteTable e TaskToContactTable e TagTable
		//i contatti e le note rimangono perchè potrebbero essere puntati da altri
		return deleteId;
	}

	public static synchronized long remove(Task task) {
		checkOrThrow();
		if(task != null)
			return remove(task.getId());
		else return -1;
	}

	public static synchronized void removeUsingTransactions(List<? extends Object> deletedTasks) {
		beginTransaction();
		if(deletedTasks.size() > 0) {
			Object o = deletedTasks.get(0);
			if(o instanceof Task) {
				for(Object task : deletedTasks)
					remove(((Task) task).getId());
			}
			else if(o instanceof DeletedTask) {
				for(Object task : deletedTasks)
					remove(((DeletedTask) task).getId());
			}
		}
		endTransaction();
	}

	public static synchronized long clearAll() {
		checkOrThrow();
		long deleteId = dB.delete(TaskTable.TABLE_TASK, null, null);
		dB.delete(TagTable.TABLE_TAG, null, null);
		dB.delete(ContactTable.TABLE_CONTACT, null, null);
		dB.delete(NoteTable.TABLE_NOTE, null, null);
		dB.delete(TaskToContactTable.TABLE_TASK_TO_CONTACT, null, null);
		dB.delete(TaskToNoteTable.TABLE_TASK_TO_NOTE, null, null);
		return deleteId;
	}
	
	public static synchronized long count() {
		String query =  "select count(*) from " + TaskTable.TABLE_TASK;
		Cursor countCursor = dB.rawQuery(query, null);
		countCursor.moveToFirst();
		long count = countCursor.getLong(0);
		countCursor.close();
		return count;
	}

	public enum Mode {
		BY_LIST, BY_LOCATION, BY_TAG, BY_COMPLETION_DATE, WITH_DUE_DATE, WITH_PRIORITY, NOT_LOCATED, NOT_TAGGED;
	}

	public static synchronized List<Task> get(Mode mode, String... parameters) {
		checkOrThrow();
		Cursor taskCursor = null;
		Cursor tagCursor = null;
		Cursor contactCursor = null;
		Cursor noteCursor = null;
		String nestedSelect = null;
		switch(mode) {
		case BY_LIST :
			String listId = "";
			if(parameters.length > 0) {
				listId = parameters[0];
				nestedSelect = "SELECT * FROM " + TaskTable.TABLE_TASK
						+ " WHERE " + TaskTable.COLUMN_LIST_ID + " = " + listId;
				taskCursor = dB.rawQuery(nestedSelect, null);
			}
			break;
		case BY_LOCATION :
			String locationId = "";
			if(parameters.length > 0) {
				locationId = parameters[0];
				nestedSelect = "SELECT * FROM " + TaskTable.TABLE_TASK
						+ " WHERE " + TaskTable.COLUMN_LOCATION_ID + " = " + locationId;
				taskCursor = dB.rawQuery(nestedSelect, null);
			}
			break;
		case BY_TAG :
			String tag = "";
			if(parameters.length > 0) {
				tag = parameters[0];
				nestedSelect = "SELECT DISTINCT (" + columnsToString(TaskTable.allColumns) + ") FROM " 
						+ TaskTable.TABLE_TASK + " JOIN " +  TagTable.TABLE_TAG
						+ "AS T WHERE T." + TagTable.COLUMN_NAME + " = " + tag;
				taskCursor = dB.rawQuery(nestedSelect, null);
			}
			break;
		case WITH_DUE_DATE :
			nestedSelect = "SELECT * FROM " + TaskTable.TABLE_TASK
				+ " WHERE " + TaskTable.COLUMN_DUE + " IS NOT NULL AND " +
				TaskTable.COLUMN_COMPLETED + " IS NULL";
			taskCursor = dB.rawQuery(nestedSelect, null);
			break;
		case WITH_PRIORITY :
			nestedSelect = "SELECT * FROM " + TaskTable.TABLE_TASK
				+ " WHERE " + TaskTable.COLUMN_PRIORITY + " != 0" ;
			taskCursor = dB.rawQuery(nestedSelect, null);
			break;
		case NOT_LOCATED :
			nestedSelect = "SELECT * FROM " + TaskTable.TABLE_TASK
				+ " WHERE " + TaskTable.COLUMN_LOCATION_ID + " = ''";
			taskCursor = dB.rawQuery(nestedSelect, null);
			break;
		case NOT_TAGGED :
			nestedSelect = "SELECT DISTINCT (" + columnsToString(TaskTable.allColumns) + ") FROM " 
					+ TaskTable.TABLE_TASK + " LEFT JOIN " +  TagTable.TABLE_TAG
					+ "AS T WHERE T." + TagTable.COLUMN_NAME + " IS NULL";
			taskCursor = dB.rawQuery(nestedSelect, null);
			break;	
		case BY_COMPLETION_DATE :
			String date = "0";
			if(parameters.length > 0) {
				date = parameters[0];
				nestedSelect = "SELECT * FROM " + TaskTable.TABLE_TASK
					+ " WHERE " + TaskTable.COLUMN_COMPLETED + " IS NOT NULL AND " +
					TaskTable.COLUMN_COMPLETED + " > " +  date;
				taskCursor = dB.rawQuery(nestedSelect, null);
			}
			break;
		default : return new ArrayList<Task>();
		}
		
		
		if(nestedSelect == null)
			return new ArrayList<Task>();

		String tagQuery = "SELECT (" + columnsToString(TagTable.allColumns) + ")"
				+ " FROM " + TagTable.TABLE_TAG + " AS T"
				+ " JOIN (" + nestedSelect + ") AS N"
				+ " ON T." + TagTable.COLUMN_TASK_ID + " = N." + TaskTable.COLUMN_TASK_ID;
		tagCursor = dB.rawQuery(tagQuery, null);

		String contactQuery = "SELECT (" + columnsToString(TaskToContactTable.allColumns, ContactTable.allColumns) + ")"
				+ " FROM " + ContactTable.TABLE_CONTACT + " AS C"
				+ " JOIN " + TaskToContactTable.TABLE_TASK_TO_CONTACT  + " AS T2C"
				+ " ON T2C." + TaskToContactTable.COLUMN_CONTACT_ID + " = C." + ContactTable.COLUMN_CONTACT_ID
				+ " JOIN (" + nestedSelect +  ") AS N"
				+ " ON T2C." + TaskToContactTable.COLUMN_TASK_ID + " = N." + TaskTable.COLUMN_TASK_ID;
		contactCursor = dB.rawQuery(contactQuery, null);
		
		String noteQuery = "SELECT (" + columnsToString(TaskToNoteTable.allColumns, NoteTable.allColumns) + ")"
				+ " FROM " + NoteTable.TABLE_NOTE + " AS NT"
				+ " JOIN " + TaskToNoteTable.TABLE_TASK_TO_NOTE  + " AS N2C"
				+ " ON N2C." + TaskToNoteTable.COLUMN_NOTE_ID + " = NT." + NoteTable.COLUMN_NOTE_ID
				+ " JOIN (" + nestedSelect +  ") AS N"
				+ " ON N2C." + TaskToNoteTable.COLUMN_TASK_ID + " = N." + TaskTable.COLUMN_TASK_ID;
		noteCursor = dB.rawQuery(noteQuery, null);
		
		Map<String,List<String>> tagMap = CursorHelper.cursorToTagMap(tagCursor);
		Map<String,List<Contact>> contactMap = CursorHelper.cursorToContactMap(contactCursor);
		Map<String,List<Note>> noteMap = CursorHelper.cursorToNoteMap(noteCursor);
		
		List<Task> tasks = new ArrayList<Task>();
		//Log.d("cursor", "" + taskCursor.getPosition());
		while(taskCursor.moveToNext()) {
			//Log.d("cursor", "" + taskCursor.getPosition());
			Task task = CursorHelper.cursorToTask(taskCursor, tagMap, contactMap, noteMap);
			tasks.add(task);
		}

		taskCursor.close();
		tagCursor.close();
		noteCursor.close();
		contactCursor.close();

		return tasks;


	}
	
	private static String columnsToString(String[]...  stringArrays) {
		StringBuilder sb = new StringBuilder();
		int length1 = stringArrays.length;
		for(int i = 0; i < length1; i++) {
			String[] stringArray = stringArrays[i];
			int length2 = stringArrays.length;
			for(int j = 0; j < length2; j++) {
				String str = stringArray[j];
				sb.append(str);
				if(i != (length1 - 1) && j != (length2 - 1))
					sb.append(", ");
			}
		}
		return sb.toString();
		
	}


	public static synchronized Set<String> getDistinctTags() {
		checkOrThrow();
		Set<String> tags = new TreeSet<String>();
		Cursor c = dB.query(true, TagTable.TABLE_TAG,
				new String[]{TagTable.COLUMN_NAME}, null, null, null, null, null, null);
		if(c.getCount() != 0) {
			int nameIndex = c.getColumnIndex(TagTable.COLUMN_NAME);
			while(c.moveToNext()) {
				String tag = c.getString(nameIndex);
				tags.add(tag);
			} 
		}
		c.close();
		return tags;
	}

	private static void checkOrThrow() {
		if(dBHelper == null)
			throw new IllegalArgumentException("DatabaseSingleton not set");
		if(dB == null)
			throw new IllegalArgumentException("Database not opened");
	}


	

}
