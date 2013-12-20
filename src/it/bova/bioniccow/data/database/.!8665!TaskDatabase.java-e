package it.bova.bioniccow.data.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import it.bova.bioniccow.data.Folder;
import it.bova.rtmapi.Contact;
import it.bova.rtmapi.DeletedTask;
import it.bova.rtmapi.Location;
import it.bova.rtmapi.Note;
import it.bova.rtmapi.Task;
import it.bova.rtmapi.TaskList;
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
		dB.execSQL("PRAGMA foreign_keys=ON;");
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
			removeTags(task.getId());
			for(String tag : task.getTags()) {
				ContentValues tagValues = TagTable.values(task, tag);
				dB.insert(TagTable.TABLE_TAG, null, tagValues);
			}
			//insert contacts
			removeContacts(task.getId());
			for(Contact contact : task.getParticipants()) {
				insertContact(task.getId(), contact);
			}
			//insert notes
			removeNotes(task.getId());
			for(Note note : task.getNotes()) {
				insertNote(task.getId(), note);
			}
			return insertId;
		}
		else return -1;
	}
	
	public static synchronized long populate(Task task) {
		checkOrThrow();
		if(task != null) {
			ContentValues taskValues = TaskTable.values(task);
			long insertId = dB.insertWithOnConflict(TaskTable.TABLE_TASK, null,
					taskValues, SQLiteDatabase.CONFLICT_REPLACE);
			//insert tags
			for(String tag : task.getTags()) {
				ContentValues tagValues = TagTable.values(task, tag);
				dB.insert(TagTable.TABLE_TAG, null, tagValues);
			}
			//insert contacts
			for(Contact contact : task.getParticipants()) {
				insertContact(task.getId(), contact);
			}
			//insert notes
			for(Note note : task.getNotes()) {
				insertNote(task.getId(), note);
			}
			return insertId;
		}
		else return -1;
	}
	
	public static synchronized long insertContact(String taskId, Contact contact) {
		checkOrThrow();
		if(contact != null) {
			ContentValues contactValues = ContactTable.values(contact);
			long insertId = dB.insertWithOnConflict(ContactTable.TABLE_CONTACT, null,
					contactValues, SQLiteDatabase.CONFLICT_REPLACE);
			ContentValues taskToContactValues = TaskToContactTable.values(taskId, contact);
			dB.insert(TaskToContactTable.TABLE_TASK_TO_CONTACT, null, taskToContactValues);

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
		return dB.delete(NoteTable.TABLE_NOTE,
				NoteTable.COLUMN_NOTE_ID + "= ?",
				new String[]{noteId});
	}
	
	public static synchronized long removeNotes(String taskId) {
		checkOrThrow();
		return dB.delete(TaskToNoteTable.TABLE_TASK_TO_NOTE,
				TaskToNoteTable.COLUMN_TASK_ID + "= ?",
				new String[]{taskId});
	}
	
	public static synchronized long removeContacts(String taskId) {
		checkOrThrow();
		return dB.delete(TaskToContactTable.TABLE_TASK_TO_CONTACT,
				TaskToContactTable.COLUMN_TASK_ID + "= ?",
				new String[]{taskId});
	}
	
	public static synchronized long removeTags(String taskId) {
		checkOrThrow();
		return dB.delete(TagTable.TABLE_TAG,
				TagTable.COLUMN_TASK_ID + "= ?",
				new String[]{taskId});
	}
	
	public static synchronized void cleanNotes() {
		dB.execSQL("DELETE FROM note WHERE noteId IN ("
				+ "SELECT N.noteId AS noteId from note AS N "
				+ "LEFT JOIN task_to_note AS T2N ON T2N.noteId = N.noteId "
				+ "WHERE T2N.taskId IS NULL)");
	}
	
	public static synchronized void cleanContacts() {
		dB.execSQL("DELETE FROM contact WHERE contactId IN ("
				+ "SELECT C.contactId AS contactId from contact AS C "
				+ "LEFT JOIN task_to_contact AS T2C ON T2C.contactId = C.contactId "
				+ "WHERE T2C.taskId IS NULL)");
	}

	public static synchronized long remove(String taskId) {
		checkOrThrow();
		long deleteId = dB.delete(TaskTable.TABLE_TASK,
				TaskTable.COLUMN_TASK_ID + "= ?",
				new String[]{taskId});
		//ON DELETE cancellano automaticamente in TaskToNoteTable e TaskToContactTable e TagTable
