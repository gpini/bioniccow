package it.bova.bioniccow.data.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import it.bova.bioniccow.utilities.rtmobjects.ParcelableTask;
import it.bova.rtmapi.Contact;
import it.bova.rtmapi.DeletedTask;
import it.bova.rtmapi.Frequency;
import it.bova.rtmapi.Note;
import it.bova.rtmapi.Priority;
import it.bova.rtmapi.Recurrence;
import it.bova.rtmapi.Recurrence.RecurrenceOption;
import it.bova.rtmapi.Task;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TaskDatabase {

	private static TaskDBHelper taskHelper;
	private static SQLiteDatabase taskDB;
	private static TagDBHelper tagHelper;
	private static SQLiteDatabase tagDB;
	private static ContactDBHelper contactHelper;
	private static SQLiteDatabase contactDB;
	private static NoteDBHelper noteHelper;
	private static SQLiteDatabase noteDB;

	private static int openedDBs = 0;


	public static synchronized void open(Context context) {
		if(context == null)
			throw new IllegalArgumentException("Context must be provided");
		if(taskHelper == null || tagHelper == null ||
				contactHelper == null || noteHelper == null) {
			taskHelper = new TaskDBHelper(context.getApplicationContext());
			tagHelper = new TagDBHelper(context.getApplicationContext());
			contactHelper = new ContactDBHelper(context.getApplicationContext());
			noteHelper = new NoteDBHelper(context.getApplicationContext());	
		}
		openedDBs++;
		taskDB = taskHelper.getWritableDatabase();
		tagDB = tagHelper.getWritableDatabase();
		contactDB = contactHelper.getWritableDatabase();
		noteDB = noteHelper.getWritableDatabase();
	}

	public static synchronized void close() {
		openedDBs--;
		if(openedDBs < 1) {
			if(taskDB.inTransaction())
				taskDB.endTransaction();
			if(tagDB.inTransaction())
				tagDB.endTransaction();
			if(contactDB.inTransaction())
				contactDB.endTransaction();
			if(noteDB.inTransaction())
				noteDB.endTransaction();
			if(taskDB != null)
				taskDB.close();
			if(tagDB!= null)
				tagDB.close();
			if(contactDB!= null)
				contactDB.close();
			if(noteDB!= null)
				noteDB.close();
		}
	}
	
	public static synchronized void beginTransaction() {
		checkOrThrow();
		taskDB.beginTransaction();
		tagDB.beginTransaction();
		contactDB.beginTransaction();
		noteDB.beginTransaction();
	}
	
	public static synchronized void endTransaction() {
		taskDB.setTransactionSuccessful();
		tagDB.setTransactionSuccessful();
		contactDB.setTransactionSuccessful();
		noteDB.setTransactionSuccessful();
		taskDB.endTransaction();
		tagDB.endTransaction();
		contactDB.endTransaction();
		noteDB.endTransaction();
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
			ContentValues taskValues = taskValues(task);
			long insertId = taskDB.insertWithOnConflict(TaskTable.TABLE_TASK, null,
					taskValues, SQLiteDatabase.CONFLICT_REPLACE);
			//insert TAGS
			tagDB.delete(TagTable.TABLE_TAG,
					TagTable.COLUMN_TASK_ID + "= ?",
					new String[]{task.getId()});
			for(String tag : task.getTags()) {
				ContentValues tagValues = tagValues(task, tag);
				tagDB.insert(TagTable.TABLE_TAG, null, tagValues);
			}
			//insert contacts
			contactDB.delete(ContactTable.TABLE_CONTACT,
					ContactTable.COLUMN_TASK_ID + "= ?",
					new String[]{task.getId()});
			for(Contact contact : task.getParticipants()) {
				ContentValues contactValues = contactValues(task, contact);
				contactDB.insert(ContactTable.TABLE_CONTACT, null, contactValues);
			}
			//insert notes
			noteDB.delete(NoteTable.TABLE_NOTE,
					NoteTable.COLUMN_TASK_ID + "= ?",
					new String[]{task.getId()});
			for(Note note : task.getNotes()) {
				ContentValues noteValues = noteValues(task, note);
				noteDB.insert(NoteTable.TABLE_NOTE, null, noteValues);
			}
			return insertId;
		}
		else return -1;
	}
	
	public static synchronized long insertNote(String taskId, Note note) {
		checkOrThrow();
		if(note != null) {
			ContentValues noteValues = noteValues(taskId, note);
			long insertId = noteDB.insert(NoteTable.TABLE_NOTE, null,
				noteValues);
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
			
		int updatedRows = noteDB.update(NoteTable.TABLE_NOTE,
				values, NoteTable.COLUMN_NOTE_ID + "= ?",
				new String[]{note.getId()});
		return updatedRows;
	}
	
	public static synchronized long removeNote(String noteId) {
		checkOrThrow();
		long deleteId = noteDB.delete(NoteTable.TABLE_NOTE,
				NoteTable.COLUMN_NOTE_ID + "= ?",
				new String[]{noteId});
		return deleteId;
	}

	public static synchronized long remove(String taskId) {
		checkOrThrow();
		long deleteId = taskDB.delete(TaskTable.TABLE_TASK,
				TaskTable.COLUMN_TASK_ID + "= ?",
				new String[]{taskId});
		//delete TAGS
		tagDB.delete(TagTable.TABLE_TAG,
				TagTable.COLUMN_TASK_ID + "= ?",
				new String[]{taskId});
		//delete contacts
		contactDB.delete(ContactTable.TABLE_CONTACT,
				ContactTable.COLUMN_TASK_ID + "= ?",
				new String[]{taskId});
		//delete notes
		noteDB.delete(NoteTable.TABLE_NOTE,
				NoteTable.COLUMN_TASK_ID + "= ?",
				new String[]{taskId});
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
		long deleteId = taskDB.delete(TaskTable.TABLE_TASK, null, null);
		tagDB.delete(TagTable.TABLE_TAG, null, null);
		contactDB.delete(ContactTable.TABLE_CONTACT, null, null);
		noteDB.delete(NoteTable.TABLE_NOTE, null, null);
		return deleteId;
	}
	
	public static synchronized long getLastId() {
		Cursor taskCursor = taskDB.query(TaskTable.TABLE_TASK,
				new String[]{TaskTable.COLUMN_ID},
				null, null, null, null,
				TaskTable.COLUMN_ID + " DESC",
				"1");
		int index = taskCursor.getColumnIndex(TaskTable.COLUMN_ID);
		taskCursor.moveToFirst();
		long lastId = taskCursor.getLong(index);
		taskCursor.close();
		return lastId;
	}
	
	public static synchronized long count() {
		String query =  "select count(*) from " + TaskTable.TABLE_TASK;
		Cursor countCursor = taskDB.rawQuery(query, null);
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
		String inClause = "()";
		switch(mode) {
		case BY_LIST :
			String listId = "";
			if(parameters.length > 0) {
				listId = parameters[0];
				taskCursor = taskDB.query(TaskTable.TABLE_TASK,
						TaskTable.allColumns,
						TaskTable.COLUMN_LIST_ID + " = ?",
						new String[]{listId},
						null, null, null);
				inClause = getInClause(taskCursor);
			}
			break;
		case BY_LOCATION :
			String locationId = "";
			if(parameters.length > 0) {
				locationId = parameters[0];
				taskCursor = taskDB.query(TaskTable.TABLE_TASK,
						TaskTable.allColumns,
						TaskTable.COLUMN_LOCATION_ID + " = ?",
						new String[]{locationId},
						null, null, null);
				inClause = getInClause(taskCursor);
			}
			break;
		case BY_TAG :
			String tag = "";
			if(parameters.length > 0) {
				tag = parameters[0];
				tagCursor = tagDB.query(TagTable.TABLE_TAG,
						TagTable.allColumns,
						TagTable.COLUMN_NAME + " = ?",
						new String[]{tag},
						null, null, null);
				inClause = getInClause(tagCursor);
			}
			break;
		case WITH_DUE_DATE :
			taskCursor = taskDB.query(TaskTable.TABLE_TASK,
					TaskTable.allColumns, 
					TaskTable.COLUMN_DUE + " IS NOT NULL AND " +
					TaskTable.COLUMN_COMPLETED + " IS NULL",
					null, null, null, null);
			inClause = getInClause(taskCursor);
			break;
		case WITH_PRIORITY :
			taskCursor = taskDB.query(TaskTable.TABLE_TASK,
					TaskTable.allColumns, 
					TaskTable.COLUMN_PRIORITY + " != 0",
					null, null, null, null);
			inClause = getInClause(taskCursor);
			break;
		case NOT_LOCATED :
			taskCursor = taskDB.query(TaskTable.TABLE_TASK,
					TaskTable.allColumns, 
					TaskTable.COLUMN_LOCATION_ID + " = ''",
					null, null, null, null);
			inClause = getInClause(taskCursor);
			break;
		case NOT_TAGGED :
			tagCursor = tagDB.query(TagTable.TABLE_TAG,
					TagTable.allColumns,
					null, null, null, null, null);
			String tmpInClause = getInClause(tagCursor);
			taskCursor = taskDB.query(TaskTable.TABLE_TASK,
					TaskTable.allColumns,
					TaskTable.COLUMN_TASK_ID + " NOT IN " + tmpInClause,
					null, null, null, null);
			inClause = getInClause(taskCursor);
			break;	
		case BY_COMPLETION_DATE :
			String date = "0";
			if(parameters.length > 0) {
				date = parameters[0];
				taskCursor = taskDB.query(TaskTable.TABLE_TASK,
						TaskTable.allColumns, 
						TaskTable.COLUMN_COMPLETED + " IS NOT NULL AND " +
						TaskTable.COLUMN_COMPLETED + " > " +  date,
						null, null, null, null);
				inClause = getInClause(taskCursor);
			}
			break;
		default : return new ArrayList<Task>();
		}
		
		
		if(inClause.equals("()"))
			return new ArrayList<Task>();

		if(taskCursor == null) {
			taskCursor = taskDB.query(TaskTable.TABLE_TASK,
					TaskTable.allColumns,
					TaskTable.COLUMN_TASK_ID + " IN " + inClause,
					null, null, null, null);
		}

		if(tagCursor == null) {	
			tagCursor = tagDB.query(TagTable.TABLE_TAG,
					TagTable.allColumns,
					TagTable.COLUMN_TASK_ID + " IN " + inClause,
					null, null, null, null);
		}

		if(contactCursor == null) {	
			contactCursor = contactDB.query(ContactTable.TABLE_CONTACT,
					ContactTable.allColumns,
					ContactTable.COLUMN_TASK_ID + " IN " + inClause,
					null, null, null, null);
		}

		if(noteCursor == null) {	
			noteCursor = noteDB.query(NoteTable.TABLE_NOTE,
					NoteTable.allColumns,
					NoteTable.COLUMN_TASK_ID + " IN " + inClause,
					null, null, null, null);
		}

		Map<String,List<String>> tagMap = cursorToTagMap(tagCursor);
		Map<String,List<Contact>> contactMap = cursorToContactMap(contactCursor);
		Map<String,List<Note>> noteMap = cursorToNoteMap(noteCursor);
		
		List<Task> tasks = new ArrayList<Task>();
		//Log.d("cursor", "" + taskCursor.getPosition());
		while(taskCursor.moveToNext()) {
			//Log.d("cursor", "" + taskCursor.getPosition());
			Task task = cursorToTask(taskCursor, tagMap, contactMap, noteMap);
			tasks.add(task);
		}

		taskCursor.close();
		tagCursor.close();
		noteCursor.close();
		contactCursor.close();

		return tasks;


	}

	private static String getInClause(Cursor cursor){
		//List<String> affectedIds = new ArrayList<String>();
		final String COLUMN_TASK_ID = "taskId";
		int taskIdIndex = cursor.getColumnIndex(COLUMN_TASK_ID);
		Set<String> affectedIds = new HashSet<String>();
		while(cursor.moveToNext()) {
			String id = cursor.getString(taskIdIndex);
			affectedIds.add(id);
		}
		StringBuilder inClauseArgs = new StringBuilder("(");
		Iterator<String> it = affectedIds.iterator();
		while(it.hasNext()) {
			String id = it.next();
			inClauseArgs.append(id);
			if(it.hasNext())
				inClauseArgs.append(",");
		}
		inClauseArgs.append(")");
		cursor.moveToPosition(-1); //funziona anche per le risposte vuote?
		return inClauseArgs.toString();

		//boolean tasksFound = true;
		//StringBuilder inClauseArgs = new StringBuilder("(");
		//for (Iterator<String> iterator = affectedIds.iterator(); iterator.hasNext();) 
		//	inClauseArgs.append(iterator.next() + (iterator.hasNext() ? "," : ""));
		//inClauseArgs.append(")");
	}


	public static synchronized Set<String> getDistingTags() {
		checkOrThrow();
		Set<String> tags = new TreeSet<String>();
		Cursor c = tagDB.query(true, TagTable.TABLE_TAG,
				new String[]{TagTable.COLUMN_NAME}, null, null, null, null, null, null);
		//Cursor c = tagDB.query(TagTable.TABLE_TAG,
		//		new String[]{TagTable.COLUMN_NAME}, null, null, null, null, null);		
		//String query = "SELECT DISTINCT " + TagTable.COLUMN_NAME + " FROM " + TagTable.TABLE_TAG;
		//Log.d("query", query);
		//Cursor c = tagDB.rawQuery(query, null);
		//Log.d("count", "" + c.getCount());
		//Log.d("column count", "" + c.getColumnCount());
		//Log.d("column names", "" + c.getColumnNames());
		//int i = 0;
		if(c.getCount() != 0) {
			int nameIndex = c.getColumnIndex(TagTable.COLUMN_NAME);
			while(c.moveToNext()) {
				String tag = c.getString(nameIndex);
				//Log.d("taskDB", ++i + " - " + tag);
				tags.add(tag);
			} 
		}
		c.close();
		return tags;

	}

	private static ContentValues taskValues(Task task) {
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

	private static ContentValues tagValues(Task task, String tag) {
		ContentValues values = new ContentValues();
		values.put(TagTable.COLUMN_TASK_ID, task.getId());
		values.put(TagTable.COLUMN_NAME, tag);
		return values;
	}

	private static ContentValues contactValues(Task task, Contact contact) {
		ContentValues values = new ContentValues();
		values.put(ContactTable.COLUMN_TASK_ID, task.getId());
		values.put(ContactTable.COLUMN_CONTACT_ID, contact.getId());
		values.put(ContactTable.COLUMN_FULLNAME, contact.getFullname());
		values.put(ContactTable.COLUMN_USERNAME, contact.getUsername());
		return values;
	}

	private static ContentValues noteValues(Task task, Note note) {
		return noteValues(task.getId(), note);
	}
	
	private static ContentValues noteValues(String taskId, Note note) {
		ContentValues values = new ContentValues();
		values.put(NoteTable.COLUMN_TASK_ID, taskId);
		values.put(NoteTable.COLUMN_NOTE_ID, note.getId());
		values.put(NoteTable.COLUMN_TITLE, note.getTitle());
		values.put(NoteTable.COLUMN_TEXT, note.getText());
		values.put(NoteTable.COLUMN_CREATED, note.getCreated().getTime());
		if(note.getModified() != null)
			values.put(NoteTable.COLUMN_MODIFIED, note.getModified().getTime());
		return values;
	}

	private static int boolToInt(boolean bool) {
		return bool == true ? 1 : 0;
	}	

	private static boolean intToBool(int i) {
		return i == 1 ? true : false;
	}

	private static void checkOrThrow() {
		if(taskHelper == null || tagHelper == null ||
				contactHelper == null || noteHelper == null)
			throw new IllegalArgumentException("DatabaseSingleton not set");
		if(taskDB == null || tagDB == null ||
				contactDB == null || noteDB == null)
			throw new IllegalArgumentException("Database not opened");
	}


	private static Task cursorToTask(Cursor taskCursor,
			Map<String,List<String>> tagMap,
			Map<String,List<Contact>> contactMap,
			Map<String,List<Note>> noteMap) {
		int taskIdIndex = taskCursor.getColumnIndex(TaskTable.COLUMN_TASK_ID);
		int addedIndex = taskCursor.getColumnIndex(TaskTable.COLUMN_ADDED);
		int completedIndex = taskCursor.getColumnIndex(TaskTable.COLUMN_COMPLETED);
		int deletedIndex = taskCursor.getColumnIndex(TaskTable.COLUMN_DELETED);
		int dueIndex = taskCursor.getColumnIndex(TaskTable.COLUMN_DUE);
		int estimateIndex = taskCursor.getColumnIndex(TaskTable.COLUMN_ESTIMATE);
		int hasDueTimeIndex = taskCursor.getColumnIndex(TaskTable.COLUMN_HAS_DUE_TIME);
		int postponedIndex = taskCursor.getColumnIndex(TaskTable.COLUMN_POSTPONED);
		int priorityIndex = taskCursor.getColumnIndex(TaskTable.COLUMN_PRIORITY);
		int taskserieIdIndex = taskCursor.getColumnIndex(TaskTable.COLUMN_TASKSERIE_ID);
		int nameIndex = taskCursor.getColumnIndex(TaskTable.COLUMN_NAME);
		int listIdIndex = taskCursor.getColumnIndex(TaskTable.COLUMN_LIST_ID);
		int locationIdIndex = taskCursor.getColumnIndex(TaskTable.COLUMN_LOCATION_ID);
		int createdIndex = taskCursor.getColumnIndex(TaskTable.COLUMN_CREATED);
		int modifiedIndex = taskCursor.getColumnIndex(TaskTable.COLUMN_MODIFIED);
		int isEveryIndex = taskCursor.getColumnIndex(TaskTable.COLUMN_RECURRENCE_IS_EVERY);
		int frequencyIndex = taskCursor.getColumnIndex(TaskTable.COLUMN_RECURRENCE_FREQUENCY);
		int intervalIndex = taskCursor.getColumnIndex(TaskTable.COLUMN_RECURRENCE_INTERVAL);
		int optionIndex = taskCursor.getColumnIndex(TaskTable.COLUMN_RECURRENCE_OPTION_TYPE);
		int optionValueIndex = taskCursor.getColumnIndex(TaskTable.COLUMN_RECURRENCE_OPTION_VALUE);
		int sourceIndex = taskCursor.getColumnIndex(TaskTable.COLUMN_SOURCE);
		int urlIndex = taskCursor.getColumnIndex(TaskTable.COLUMN_URL);
		String taskId = taskCursor.getString(taskIdIndex);
		long add = taskCursor.getLong(addedIndex);
		Date added = new Date(add); 
		Date completed = null;
		if(!taskCursor.isNull(completedIndex)) {
			long compl = taskCursor.getLong(completedIndex);
			completed = new Date(compl);
		}
		Date deleted = null;
		if(!taskCursor.isNull(deletedIndex)) {
			long del = taskCursor.getLong(deletedIndex);
			deleted = new Date(del);
		}
		Date due = null;
		if(!taskCursor.isNull(dueIndex)) {
			long d = taskCursor.getLong(dueIndex);
			due = new Date(d);
		}
		String estimate = "";
		if(!taskCursor.isNull(estimateIndex))
			estimate = taskCursor.getString(estimateIndex);
		boolean hasDueTime = intToBool(taskCursor.getInt(hasDueTimeIndex));
		int postponed = taskCursor.getInt(postponedIndex);
		int prioOrdinal = taskCursor.getInt(priorityIndex);
		Priority priority = Priority.values()[prioOrdinal];
		String taskserieId = taskCursor.getString(taskserieIdIndex); 
		String name = taskCursor.getString(nameIndex);
		String listId = taskCursor.getString(listIdIndex);
		String locationId = taskCursor.getString(locationIdIndex);
		Date created = null;
		if(!taskCursor.isNull(createdIndex)) {
			long c = taskCursor.getLong(createdIndex);
			created = new Date(c);
		}
		Date modified = null;
		if(taskCursor.isNull(modifiedIndex)) {
			long mod = taskCursor.getLong(modifiedIndex);
			modified = new Date(mod);
		}
		Recurrence rec = null;
		if(!taskCursor.isNull(isEveryIndex)) {
			boolean isEvery = intToBool(taskCursor.getInt(isEveryIndex));
			int interval = taskCursor.getInt(intervalIndex);
			int freqOrdinal = taskCursor.getInt(frequencyIndex);
			Frequency freq = Frequency.values()[freqOrdinal];
			RecurrenceOption optionType = null;
			String optionValue = null;
			if(!taskCursor.isNull(optionIndex)) {
				int typeOrdinal = taskCursor.getInt(optionIndex);
				optionType = RecurrenceOption.values()[typeOrdinal];
				optionValue = taskCursor.getString(optionValueIndex);
			}
			rec = new Recurrence(isEvery, interval, freq, optionType, optionValue);
		}
		String source = taskCursor.getString(sourceIndex);
		String url = null;
		if(!taskCursor.isNull(urlIndex))
			url = taskCursor.getString(urlIndex);
		//taskCursor.close();
		//tags
		List<String> tagList = tagMap.get(taskId);
		String[] tags = new String[0];
		if(tagList != null) {
			tags = new String[tagList.size()];
			for(int i = 0; i < tagList.size(); i++)
				tags[i] = tagList.get(i);
		}
		//contacts
		List<Contact> contactList = contactMap.get(taskId);
		Contact[] contacts = new Contact[0];
		if(contactList != null) {
			contacts = new Contact[contactList.size()];
			for(int i = 0; i < contactList.size(); i++)
				contacts[i] = contactList.get(i);
		}
		//notes
		List<Note> noteList = noteMap.get(taskId);
		Note[] notes = new Note[0];
		if(noteList != null) {
			notes = new Note[noteList.size()];
			for(int i = 0; i < noteList.size(); i++)
				notes[i] = noteList.get(i);
		}
		//return
		return new Task(taskId, name, added,
				completed, deleted, due,
				estimate, hasDueTime, postponed, priority,
				taskserieId, locationId, listId,
				created, modified, notes,
				rec, contacts, source, tags, url);
	}

	private static Map<String,List<String>> cursorToTagMap(Cursor tagCursor) {
		Map<String,List<String>> tagMap = new HashMap<String,List<String>>(); 
		int nameIndex = tagCursor.getColumnIndex(TagTable.COLUMN_NAME);
		int taskIdIndex = tagCursor.getColumnIndex(TagTable.COLUMN_TASK_ID);
		while(tagCursor.moveToNext()) {
			String taskId = tagCursor.getString(taskIdIndex);
			String tag = tagCursor.getString(nameIndex);
			if(!tagMap.containsKey(taskId))
				tagMap.put(taskId, new ArrayList<String>());
			tagMap.get(taskId).add(tag);
		}
		tagCursor.close();
		return tagMap;
	}

	private static Map<String,List<Contact>> cursorToContactMap(Cursor contactCursor) {
		Map<String,List<Contact>> contactMap = new HashMap<String,List<Contact>>(); 
		int contactIdIndex = contactCursor.getColumnIndex(ContactTable.COLUMN_CONTACT_ID);
		int fullnameIndex = contactCursor.getColumnIndex(ContactTable.COLUMN_FULLNAME);
		int usernameIndex = contactCursor.getColumnIndex(ContactTable.COLUMN_USERNAME);
		int taskIdIndex = contactCursor.getColumnIndex(ContactTable.COLUMN_TASK_ID);
		while(contactCursor.moveToNext()) {
			String taskId = contactCursor.getString(taskIdIndex);
			String contactId = contactCursor.getString(contactIdIndex);
			String fullname = contactCursor.getString(fullnameIndex);
			String username = contactCursor.getString(usernameIndex);
			Contact contact = new Contact(contactId, fullname, username);
			if(!contactMap.containsKey(taskId))
				contactMap.put(taskId, new ArrayList<Contact>());
			contactMap.get(taskId).add(contact);
		}
		contactCursor.close();
		return contactMap;
	}

	private static Map<String,List<Note>> cursorToNoteMap(Cursor noteCursor) {
		Map<String,List<Note>> noteMap = new HashMap<String,List<Note>>(); 
		int titleIndex = noteCursor.getColumnIndex(NoteTable.COLUMN_TITLE);
		int textIndex = noteCursor.getColumnIndex(NoteTable.COLUMN_TEXT);
		int createdIndex = noteCursor.getColumnIndex(NoteTable.COLUMN_CREATED);
		int modifiedIndex = noteCursor.getColumnIndex(NoteTable.COLUMN_MODIFIED);
		int noteIdIndex = noteCursor.getColumnIndex(NoteTable.COLUMN_NOTE_ID);
		int taskIdIndex = noteCursor.getColumnIndex(NoteTable.COLUMN_TASK_ID);
		while(noteCursor.moveToNext()) {
			String taskId = noteCursor.getString(taskIdIndex);
			String noteId = noteCursor.getString(noteIdIndex);
			String title = noteCursor.getString(titleIndex);
			String text = noteCursor.getString(textIndex);
			long crea = noteCursor.getLong(createdIndex);
			Date noteCreated = new Date(crea);
			Date noteModified = null;
			if(noteCursor.isNull(modifiedIndex));
			long mod = noteCursor.getLong(modifiedIndex);
			noteModified = new Date(mod);
			Note note = new Note(noteId, title, text, noteCreated, noteModified);
			if(!noteMap.containsKey(taskId))
				noteMap.put(taskId, new ArrayList<Note>());
			noteMap.get(taskId).add(note);
		}
		noteCursor.close();
		return noteMap;
	}

}
