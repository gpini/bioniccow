package it.bova.bioniccow.asyncoperations.sync;

import it.bova.bioniccow.asyncoperations.MessageSender;
import it.bova.bioniccow.data.Folders_old2;
import it.bova.bioniccow.data.Locations_old2;
import it.bova.bioniccow.data.Tags_old2;
import it.bova.bioniccow.data.TaskLists_old2;
import it.bova.bioniccow.utilities.rtmobjects.ParcelableTask;
import it.bova.rtmapi.Task;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;

public class Synchronizer {

	private Context context;
	
	public Synchronizer(Context context) {
		this.context = context;
	}
	
	private static List<SyncObserver> observers =
	new ArrayList<SyncObserver>();
	
	public void addObserver(SyncObserver observer) {
		observers.add(observer);
	}

	public void removeObserver(SyncObserver observer) {
		observers.remove(observer);
	}

	public void removeObservers() {
		observers.clear();
	}
	
	static synchronized void onStartSync() {
		for(SyncObserver observer : observers)
			observer.onStartSynching();
	}
	
	static synchronized void onStopSync() {
		for(SyncObserver observer : observers)
			observer.onStopSynching();
	}
	
	
	public static boolean isSynching() {
		return SynchService.isSynching();
	}
	
	public Context getContext() {return this.context;}

	
	public void sync(boolean forceSync, boolean syncTasksAndRelated,
			boolean syncLists, boolean syncLoc) {
		Handler handler = new SyncHandler();
		Intent intent = new Intent(context,SynchService.class);
		intent.putExtra("forceSync", forceSync);
		intent.putExtra("syncLists", syncLists);
		intent.putExtra("syncLocations", syncLoc);
		intent.putExtra("syncTasksAndRelated", syncTasksAndRelated);
    	//intent.putExtra("msg", "prova intent service!!!");
    	intent.putExtra(SynchService.EXTRA_MESSENGER, new Messenger(handler));
		context.startService(intent);
		
	}
	
	public void syncChangedTasks(Collection<Task> changedTasks) {
		Handler handler = new SyncHandler();
		Intent intent = new Intent(context,SynchService.class);
		ArrayList<ParcelableTask> tasks = new ArrayList<ParcelableTask>();
		for(Task task : changedTasks)
			tasks.add(new ParcelableTask(task));
		intent.putParcelableArrayListExtra("changedTasks", tasks);
		intent.putExtra(SynchService.EXTRA_MESSENGER, new Messenger(handler));
		context.startService(intent);
	}
	
	public void syncDeletedTasks(Collection<Task> deletedTasks) {
		Handler handler = new SyncHandler();
		Intent intent = new Intent(context,SynchService.class);
		ArrayList<ParcelableTask> tasks = new ArrayList<ParcelableTask>();
		for(Task task : deletedTasks)
			tasks.add(new ParcelableTask(task));
		intent.putParcelableArrayListExtra("deletedTasks", tasks);
		intent.putExtra(SynchService.EXTRA_MESSENGER, new Messenger(handler));
		context.startService(intent);
	}	
	
	public void syncAddedTasks(Collection<Task> addedTasks) {
		Handler handler = new SyncHandler();
		Intent intent = new Intent(context,SynchService.class);
		ArrayList<ParcelableTask> tasks = new ArrayList<ParcelableTask>();
		for(Task task : addedTasks)
			tasks.add(new ParcelableTask(task));
		intent.putParcelableArrayListExtra("addedTasks", tasks);
		intent.putExtra(SynchService.EXTRA_MESSENGER, new Messenger(handler));
		context.startService(intent);
		
	}

	private class SyncHandler extends Handler {
		@Override public void handleMessage(Message msg) {
			switch(msg.arg1) {
			case SynchService.LISTS_SYNCHED : 
				MessageSender.notifyTasklistsUpdated(context);
				break;
			case SynchService.LOCATIONS_SYNCHED : 
				MessageSender.notifyLocationsUpdated(context);
				break;
			case SynchService.TASKS_SYNCHED : 
				//Log.d("sync", "task synched");
				ArrayList<String> changedIds = msg.getData().getStringArrayList("changedIds");
				if(changedIds != null)
					MessageSender.notifyTaskChanged(context, changedIds);
				ArrayList<ParcelableTask> addedTasks = msg.getData().getParcelableArrayList("addedTasks");
				if(addedTasks != null) {
					MessageSender.notifyTaskAdded(context, addedTasks);
				}
				break;
			}
		}
	}

	

}
