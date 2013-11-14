package it.bova.bioniccow.asyncoperations;

import it.bova.bioniccow.utilities.rtmobjects.ParcelableTask;
import it.bova.rtmapi.Note;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MessageReceiver extends BroadcastReceiver {
	@Override public final void onReceive(Context context, Intent intent) {
		int messageCode = intent.getIntExtra("code", 0);
		switch(messageCode) {
		case MessageSender.AUTH_ERROR :
			String msg = intent.getStringExtra("msg");
			onAuthError(context, msg);
			break;
		case MessageSender.TASK_CHANGED :
			List<String> changedIds = intent.getStringArrayListExtra("changedIds");
			if(changedIds == null) changedIds =	new ArrayList<String>();
			onTaskChanged(context, changedIds);
			break;
		case MessageSender.TASK_ADDED :
			List<ParcelableTask> addedTasks =
				intent.getParcelableArrayListExtra("addedTasks");
			//Log.d("received", "" + addedTask);
			if(addedTasks != null)
				onTaskAdded(context, addedTasks);
			break;
		case MessageSender.NOTE_ADDED :
			Note addedNote =
				(Note) intent.getSerializableExtra("addedNote");
			String taskId = intent.getStringExtra("id");
			if(addedNote != null && taskId != null)
				onNoteAdded(context, addedNote, taskId);
			break;
		case MessageSender.NOTE_DELETED :
			String deletedNoteId =
				intent.getStringExtra("deletedNote");
			if(deletedNoteId != null)
				onNoteDeleted(context, deletedNoteId);
			break;
		case MessageSender.NOTE_EDITED :
			Note editedNote =
				(Note) intent.getSerializableExtra("editedNote");
			if(editedNote != null)
				onNoteEdited(context, editedNote);
			break;
		case MessageSender.ERROR :
			String errorMsg = intent.getStringExtra("msg");
			onError(context, errorMsg);
			break;
		}
	}
	
	protected void onAuthError(Context context, String text) {}
	protected void onError(Context context, String text) {}
	protected void onTaskChanged(Context context, List<String> changedIds) {}
	protected void onTaskAdded(Context context, List<ParcelableTask> addedTasks) {}
	protected void onNoteAdded(Context context, Note addedNote, String taskId) {}
	protected void onNoteDeleted(Context context, String deletedNoteId) {}
	protected void onNoteEdited(Context context, Note editedNote) {}
}
