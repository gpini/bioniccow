package it.bova.bioniccow.data.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import it.bova.rtmapi.Contact;
import it.bova.rtmapi.Frequency;
import it.bova.rtmapi.Location;
import it.bova.rtmapi.Note;
import it.bova.rtmapi.Priority;
import it.bova.rtmapi.Recurrence;
import it.bova.rtmapi.Recurrence.RecurrenceOption;
import it.bova.rtmapi.Task;
import it.bova.rtmapi.TaskList;

public class CursorHelper {

	public static Task cursorToTask(Cursor taskCursor,
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


	public static Map<String,List<Contact>> cursorToContactMap(String asContactString, String asT2CString, Cursor contactCursor) {
		Map<String,List<Contact>> contactMap = new HashMap<String,List<Contact>>(); 
		int contactIdIndex = contactCursor.getColumnIndex(asContactString + ContactTable.COLUMN_CONTACT_ID);
		int fullnameIndex = contactCursor.getColumnIndex(asContactString + ContactTable.COLUMN_FULLNAME);
		int usernameIndex = contactCursor.getColumnIndex(asContactString + ContactTable.COLUMN_USERNAME);
		int taskIdIndex = contactCursor.getColumnIndex(asT2CString + TaskToContactTable.COLUMN_TASK_ID);
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
	
	public static Map<String,List<String>> cursorToTagMap(String asTagString, Cursor tagCursor) {
		Map<String,List<String>> tagMap = new HashMap<String,List<String>>(); 
		int nameIndex = tagCursor.getColumnIndex(asTagString + TagTable.COLUMN_NAME);
		int taskIdIndex = tagCursor.getColumnIndex(asTagString + TagTable.COLUMN_TASK_ID);
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

	public static Map<String,List<Note>> cursorToNoteMap(String asNoteString, String asT2NString, Cursor noteCursor) {
		Map<String,List<Note>> noteMap = new HashMap<String,List<Note>>(); 
		int titleIndex = noteCursor.getColumnIndex(asNoteString + NoteTable.COLUMN_TITLE);
		int textIndex = noteCursor.getColumnIndex(asNoteString + NoteTable.COLUMN_TEXT);
		int createdIndex = noteCursor.getColumnIndex(asNoteString + NoteTable.COLUMN_CREATED);
		int modifiedIndex = noteCursor.getColumnIndex(asNoteString + NoteTable.COLUMN_MODIFIED);
		int noteIdIndex = noteCursor.getColumnIndex(asNoteString + NoteTable.COLUMN_NOTE_ID);
		int taskIdIndex = noteCursor.getColumnIndex(asT2NString + TaskToNoteTable.COLUMN_TASK_ID);
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
	
	public static TaskList cursorToTaskList(Cursor tasklistCursor) {
		int tasklistIdIndex = tasklistCursor.getColumnIndex(TaskListTable.COLUMN_TASKLIST_ID);
		int nameIndex = tasklistCursor.getColumnIndex(TaskListTable.COLUMN_NAME);
		int lockedIndex = tasklistCursor.getColumnIndex(TaskListTable.COLUMN_LOCKED);
		int archivedIndex = tasklistCursor.getColumnIndex(TaskListTable.COLUMN_ARCHIVED);
		int deletedIndex = tasklistCursor.getColumnIndex(TaskListTable.COLUMN_DELETED);
		int positionIndex = tasklistCursor.getColumnIndex(TaskListTable.COLUMN_POSITION);
		int smartIndex = tasklistCursor.getColumnIndex(TaskListTable.COLUMN_SMART);
		int sortOrderIndex = tasklistCursor.getColumnIndex(TaskListTable.COLUMN_SORT_ORDER);
		String tasklistId = tasklistCursor.getString(tasklistIdIndex);
		String name = tasklistCursor.getString(nameIndex);
		boolean locked = intToBool(tasklistCursor.getInt(lockedIndex));
		boolean archived = intToBool(tasklistCursor.getInt(archivedIndex));
		boolean deleted = intToBool(tasklistCursor.getInt(deletedIndex));
		int position = tasklistCursor.getInt(positionIndex);
		boolean smart = intToBool(tasklistCursor.getInt(smartIndex));
		int sortOrder = tasklistCursor.getInt(sortOrderIndex);
		return new TaskList(tasklistId, name, archived, deleted, locked, position, smart, sortOrder);
	}
	
	public static Location cursorToLocation(Cursor locationCursor) {
		int locationIdIndex = locationCursor.getColumnIndex(LocationTable.COLUMN_LOCATION_ID);
		int nameIndex = locationCursor.getColumnIndex(LocationTable.COLUMN_NAME);
		int addressIndex = locationCursor.getColumnIndex(LocationTable.COLUMN_ADDRESS);
		int viewableIndex = locationCursor.getColumnIndex(LocationTable.COLUMN_VIEWABLE);
		int latitudeIndex = locationCursor.getColumnIndex(LocationTable.COLUMN_LATITUDE);
		int longitudeIndex = locationCursor.getColumnIndex(LocationTable.COLUMN_LONGITUDE);
		int zoomIndex = locationCursor.getColumnIndex(LocationTable.COLUMN_ZOOM);
		String locationId = locationCursor.getString(locationIdIndex);
		String name = locationCursor.getString(nameIndex);
		String address = locationCursor.getString(addressIndex);
		boolean viewable = intToBool(locationCursor.getInt(viewableIndex));
		double lat = locationCursor.getDouble(latitudeIndex);
		double lon = locationCursor.getDouble(longitudeIndex);
		int zoom = locationCursor.getInt(zoomIndex);
		return new Location(address, locationId, lat, lon, name, viewable, zoom);
	}
	
	private static boolean intToBool(int i) {
		return i == 1 ? true : false;
	}

}
