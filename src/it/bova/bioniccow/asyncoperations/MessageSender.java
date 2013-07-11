package it.bova.bioniccow.asyncoperations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import it.bova.bioniccow.InterProcess;
import it.bova.bioniccow.utilities.rtmobjects.ParcelableTask;
import it.bova.rtmapi.Note;
import it.bova.rtmapi.Task;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MessageSender implements InterProcess {
	
	public static final int AUTH_ERROR = 1;
	public static final int ERROR = 2;
	public static final int TASK_CHANGED = 3;
	public static final int TASK_ADDED = 4;
	public static final int NOTE_ADDED = 5;
	public static final int NOTE_DELETED = 6;
	public static final int NOTE_EDITED = 7;
	
	public static void sendMessage(Context context, String text) {
		Intent intent = new Intent(ERROR_MESSENGER);
		intent.putExtra("code", ERROR);
		intent.putExtra("msg", text);
		context.sendBroadcast(intent);
	}
	
	public static void sendAuthErrorMessage(Context context) {
		Intent intent = new Intent(ERROR_MESSENGER);
		intent.putExtra("code", AUTH_ERROR);
		intent.putExtra("msg", "login issue");
		context.sendBroadcast(intent);
	}
	
	public static void notifyTaskChanged(Context context, ArrayList<String> changedIds) {
		Intent intent = new Intent(ERROR_MESSENGER);
		intent.putExtra("code", TASK_CHANGED);
		//Log.d("sync", "" + changedIds);
		if(changedIds == null)
			changedIds = new ArrayList<String>();
		Collections.sort(changedIds);
		intent.putStringArrayListExtra("changedIds", changedIds);
		//Log.d("sync", changedIds.toString());
		context.sendBroadcast(intent);
	}

	public static void notifyTaskAdded(Context context, ArrayList<ParcelableTask> addedTasks) {
		Intent intent = new Intent(ERROR_MESSENGER);
		intent.putExtra("code", TASK_ADDED);
		if(addedTasks != null)
			intent.putParcelableArrayListExtra("addedTasks", addedTasks);
		//Log.d("sent", "" + addedTask);
		context.sendBroadcast(intent);		
	}
	
	public static void notifyNoteAdded(Context context, Note addedNote, String taskId) {
		Intent intent = new Intent(ERROR_MESSENGER);
		intent.putExtra("code", NOTE_ADDED);
		if(addedNote != null) {
			intent.putExtra("addedNote", addedNote);
			intent.putExtra("id", taskId);
		}
		context.sendBroadcast(intent);		
	}
	
	public static void notifyNoteDeleted(Context context, String deletedNoteId) {
		Intent intent = new Intent(ERROR_MESSENGER);
		intent.putExtra("code", NOTE_DELETED);
		if(deletedNoteId != null)
			intent.putExtra("deletedNote", deletedNoteId);
		context.sendBroadcast(intent);		
	}
	
	public static void notifyNoteEdited(Context context, Note editedNote) {
		Intent intent = new Intent(ERROR_MESSENGER);
		intent.putExtra("code", NOTE_EDITED);
		if(editedNote != null)
			intent.putExtra("editedNote", editedNote);
		context.sendBroadcast(intent);		
	}
}
