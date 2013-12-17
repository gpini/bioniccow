package it.bova.bioniccow.asyncoperations.sync;

import it.bova.bioniccow.R;
import it.bova.bioniccow.asyncoperations.ErrorCoded;
import it.bova.bioniccow.asyncoperations.InquiryAnswer;
import it.bova.bioniccow.asyncoperations.rtmobjects.ListGetter;
import it.bova.bioniccow.asyncoperations.rtmobjects.LocationGetter;
import it.bova.bioniccow.asyncoperations.rtmobjects.SynchedTaskGetter;
import it.bova.bioniccow.data.Preferences;
import it.bova.bioniccow.data.Preferences.PrefParameter;
import it.bova.bioniccow.data.database.TaskDatabase;
import it.bova.bioniccow.utilities.rtmobjects.ParcelableTask;
import it.bova.rtmapi.DeletedTask;
import it.bova.rtmapi.Location;
import it.bova.rtmapi.SynchedTasks;
import it.bova.rtmapi.Task;
import it.bova.rtmapi.TaskList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.SystemClock;

public class SynchService extends IntentService implements ErrorCoded{

	public static final String EXTRA_MESSENGER = "messenger";
	
	public static final int LISTS_SYNCHED = 101;
	public static final int LOCATIONS_SYNCHED = 102;
	public static final int TASKS_SYNCHED = 103;
	public static final int TAGS_SYNCHED = 104;
	public static final int FOLDERS_SYNCHED = 105;

	private static boolean synching = false;
	private boolean synchSuccess;
	
	private List<Task> newTasks = new ArrayList<Task>();
	private List<Task> changedTasks = new ArrayList<Task>();
	
	//resource
	private String sync_NOK;
	
	static boolean isSynching() {return synching;}

	public SynchService() {
		super("SynchService");
	}
	
	@Override public void onCreate() {
		synching = true;
		sync_NOK = this.getResources().getString(R.string.sync_NOK);
		super.onCreate();
		Synchronizer.onStartSync();
	}

	@Override protected void onHandleIntent(Intent intent) {
		//Log.d("ciao", "start");
				
		Bundle extras = intent.getExtras();  
		Messenger messenger = (Messenger) extras.get(EXTRA_MESSENGER);
		
		ArrayList<ParcelableTask> changedTasks
			= intent.getParcelableArrayListExtra("changedTasks");
		if(changedTasks != null) {
			List<ParcelableTask> tasks = this.updateChangedTasks(changedTasks);
			//Log.d("change", "" + tasks);
			if(tasks != null) {
				this.notifyTaskChanged(messenger, tasks, SynchService.TASKS_SYNCHED);
			}
		}
		
		ArrayList<ParcelableTask> deletedTasks
			= intent.getParcelableArrayListExtra("deletedTasks");
		if(deletedTasks != null) {
			List<ParcelableTask> tasks = this.updateDeletedTasks(deletedTasks);
			//Log.d("del", "" + tasks);
			if(tasks != null) {
				this.notifyTaskChanged(messenger, tasks, SynchService.TASKS_SYNCHED);
			}
		}

		ArrayList<ParcelableTask> addedTasks
			= intent.getParcelableArrayListExtra("addedTasks");
		//Log.d("added", "" + addedTasks);
		if(addedTasks != null) {
			List<ParcelableTask> tasks = this.updateAddedTasks(addedTasks);
			//Log.d("add", "" + task);
			if(tasks != null) {
				this.notifyTaskAdded(messenger, tasks, SynchService.TASKS_SYNCHED);
			}
		}

		
		boolean forceSynch = intent.getBooleanExtra("forceSync", false);
		
		//Lists
		if(intent.getBooleanExtra("syncLists", false)) {
			synchSuccess = this.syncLists(forceSynch, sync_NOK);
			if(synchSuccess)
				this.sendMessage(messenger, SynchService.LISTS_SYNCHED);
			SystemClock.sleep(1000);
		}
		
		//Locations
		if(intent.getBooleanExtra("syncLocations", false)) {
			synchSuccess = this.syncLocations(forceSynch, sync_NOK);
			if(synchSuccess) {
				new Preferences(this).putBoolean(PrefParameter.FIRST_SYNC_DONE, true);
				this.sendMessage(messenger, SynchService.LOCATIONS_SYNCHED);
			}
			SystemClock.sleep(1000);
		}
		
		
		//Task&Co
		if(intent.getBooleanExtra("syncTasksAndRelated", false)){
			SynchedTasks synchedTasks = this.syncTasksAndRelated(forceSynch, sync_NOK);
			//Log.d("sync", "" + synchedTasks);
			synchSuccess = (synchedTasks == null) ? false : true;
			if(synchSuccess) {
				this.notifyTaskSynched(messenger, this.newTasks, this.changedTasks,
						synchedTasks.getDeletedTasks(), SynchService.TASKS_SYNCHED);
				this.newTasks.clear();
				this.changedTasks.clear();
				//this.sendMessage(messenger, SynchService.TAGS_SYNCHED);
				//this.sendMessage(messenger, SynchService.FOLDERS_SYNCHED);
			}
		}
		
		//this.sendMessage(messenger, SynchService.SYNCHRONIZATION_STOP);
		//Log.d("ciao", "stop");
	}

	@Override public void onDestroy() {
		synching = false;
		super.onDestroy();
		Synchronizer.onStopSync();	
	}
	
	
	public boolean syncLists(boolean forceSync, String NOK_sync_phrase) {
		ListGetter lg = new ListGetter(NOK_sync_phrase, SynchService.this);
		InquiryAnswer<List<TaskList>> answer = lg.executeSynchronously();
		if(answer.getCode() == OK) {
			TaskDatabase.open(this);
			TaskDatabase.putTasklists(answer.getResult());
			TaskDatabase.close();
			return true;
		}
		else {
			if(forceSync) lg.postProcessingExecute(answer);
			return false;
		}
	}

	public boolean syncLocations(boolean forceSync, String NOK_sync_phrase) {
		LocationGetter lg = new LocationGetter(NOK_sync_phrase, SynchService.this);
		InquiryAnswer<List<Location>> answer = lg.executeSynchronously();
		if(answer.getCode() == OK) {
			TaskDatabase.open(this);
			TaskDatabase.putLocations(answer.getResult());
			TaskDatabase.close();
			return true;
		}
		else {
			if(forceSync) lg.postProcessingExecute(answer);
			return false;
		}
	}
	
	public SynchedTasks syncTasksAndRelated(boolean forceSync, String NOK_sync_phrase) {
		
		//retrieve last task map and last synch date
		Preferences p = new Preferences(SynchService.this);
		Date lastSynch = new Date(p.getLong(PrefParameter.LAST_SYNCH, 0L));
		//lastSynch = new Date(0);
		//Log.d("ciao", "last synch was " + lastSynch);
		//Tasks tasks = new Tasks(this);
		//Map<String,Task> taskMap = tasks.retrieve();
		//Log.d("ciao", "retrieved " + taskMap.size() + " tasks");
		boolean cleared = false;
		try {
			TaskDatabase.open(this);
			if(lastSynch.getTime() == 0L) {
				//Log.d("ciao", "clear");
				TaskDatabase.clearAll();
				cleared = true;
				//new Tags(this).save(new TreeSet<String>());
			}
			SynchedTaskGetter stg = new SynchedTaskGetter(NOK_sync_phrase, SynchService.this); 
			InquiryAnswer<SynchedTasks> answer = stg.executeSynchronously(lastSynch);
			

			if(answer.getCode() == OK) {
				Date now = new Date();
				SynchedTasks synchedTasks = answer.getResult();

				//Log.d("ciao", "new tasks: " + synchedTasks.getTasks().size());
				//Log.d("ciao", "deleted tasks: " + synchedTasks.getDeletedTasks().size());
				//Log.d("ciao", "synching tasks up to " + now);

				//cancel last synch so that if operation is stopped
				//synch operation will be performed as it was the first time
				p.putLong(PrefParameter.LAST_SYNCH, 0L);
//				for(Task task : synchedTasks.getTasks()) {
//					TagDatabase.(task);
//					//Log.d("insert","" + task.getTags());
//				}
				TaskDatabase.beginTransaction();
				long entries = TaskDatabase.count();
				long newEntries = entries;
				for(Task task : synchedTasks.getTasks()) {
					TaskDatabase.put(task);
					if(cleared)
						changedTasks.add(task);
					else {
						newEntries = TaskDatabase.count();
						if(newEntries > entries) newTasks.add(task);
						else changedTasks.add(task);
						entries = newEntries;
					}
				}
				TaskDatabase.endTransaction();
				TaskDatabase.removeUsingTransactions(synchedTasks.getDeletedTasks());
				p.putLong(PrefParameter.LAST_SYNCH, now.getTime());

				/*//Log.d("ciao", "synching tags");
				Set<String> tagSet = TaskDatabase.getDistinctTags();
				//Log.d("(synch) tags", "" + tagSet.size());
				new Tags_old2(this).save(tagSet);

				//Log.d("ciao", "synching folders");
				Map<String,Folder> folders = new Folders_old2(this).retrieveAsMap();
				if(listMap == null)
					listMap = new TaskLists_old2(this).retrieveAsMap();
				if(locMap == null)
					locMap = new Locations_old2(this).retrieveAsMap();
				folderMap = TasksUpdater.updateFolders(folders, tagSet, listMap, locMap);
				new Folders_old2(this).saveAsList(folderMap);*/

				return synchedTasks;
			}
			else {
				if(forceSync || answer.getCode() == LOGIN_ISSUE)
					stg.postProcessingExecute(answer);	
				return null;
			}
			
		}catch(Exception e) {
			//Log.d("DB error", e.getMessage());
			return null;
		}
		finally {
			TaskDatabase.close();
		}
			//Log.d("ciao", "last task synch" + lastSynch);



	}
	
	public List<ParcelableTask> updateChangedTasks(ArrayList<ParcelableTask> changedTasks2) {
		TasksUpdater changeUpdater =  new TasksUpdater(this) {
			@Override protected void onUpdate(List<ParcelableTask> updatedTasks) {
				try {
					TaskDatabase.open(SynchService.this);
					TaskDatabase.putUsingTransactions(updatedTasks);	
				}catch(Exception e) {
					//Log.d("changed error",e.getMessage());
				}
				finally {
					TaskDatabase.close();
				}
			}
		};
		return changeUpdater.update(changedTasks2);
	
	}
	
	public List<ParcelableTask> updateDeletedTasks(ArrayList<ParcelableTask> deletedTasks) {
		TasksUpdater deleteUpdater =  new TasksUpdater(this) {
			@Override protected void onUpdate(List<ParcelableTask> updatedTasks) {
				try {
					TaskDatabase.open(SynchService.this);
					TaskDatabase.removeUsingTransactions(updatedTasks);	
				}catch(Exception e) {
					//Log.d("changed error",e.getMessage());
				}
				finally {
					TaskDatabase.close();
				}	
			}
		};
		return deleteUpdater.update(deletedTasks);
	
	}
	
	public List<ParcelableTask> updateAddedTasks(ArrayList<ParcelableTask> addedTasks) {
		TasksUpdater changeUpdater =  new TasksUpdater(this) {
			@Override protected void onUpdate(List<ParcelableTask> updatedTasks) {
				try {
					TaskDatabase.open(SynchService.this);
					TaskDatabase.putUsingTransactions(updatedTasks);	
				}catch(Exception e) {
					//Log.d("changed error",e.getMessage());
				}
				finally {
					TaskDatabase.close();
				}		
			}
		};
		return changeUpdater.update(addedTasks);
	
	}
	
	private void sendMessage(Messenger messenger, int message) {
		Message msg = Message.obtain();
		msg.arg1 = message;
		try {
			messenger.send(msg);
		} catch (android.os.RemoteException e1) {
			//Log.d(getClass().getName(), "Exception sending message", e1);
		}
	}
	
	private void notifyTaskSynched(Messenger messenger, List<Task> addedTasks,
			List<Task> changedTasks, List<DeletedTask> deletedTasks, int message) {
		Message msg = Message.obtain();
		msg.arg1 = message;
		ArrayList<String> changedIds = new ArrayList<String>();
		for(DeletedTask task : deletedTasks)
			changedIds.add(task.getId());
		for(Task task : changedTasks)
			changedIds.add(task.getId());
		Bundle bundle = new Bundle();
		bundle.putStringArrayList("changedIds", changedIds);
		ArrayList<Task> tasks = new ArrayList<Task>();
		tasks.addAll(addedTasks);
		bundle.putSerializable("addedTasks", tasks);
		msg.setData(bundle);
		try {
			messenger.send(msg);
		} catch (android.os.RemoteException e1) {
			//Log.d(getClass().getName(), "Exception sending message", e1);
		}
	}
	
	private void notifyTaskChanged(Messenger messenger, List<? extends Task> tasks, int message) {
		Message msg = Message.obtain();
		msg.arg1 = message;
		ArrayList<String> changedIds = new ArrayList<String>();
		for(Task task : tasks)
			changedIds.add(task.getId());
		Bundle bundle = new Bundle();
		bundle.putStringArrayList("changedIds", changedIds);
		msg.setData(bundle);
		try {
			messenger.send(msg);
		} catch (android.os.RemoteException e1) {
			//Log.d(getClass().getName(), "Exception sending message", e1);
		}
	}
	
	private void notifyTaskAdded(Messenger messenger, List<? extends Task> tasks2, int message) {
		Message msg = Message.obtain();
		msg.arg1 = message;
		Bundle bundle = new Bundle();
		ArrayList<ParcelableTask> tasks = new ArrayList<ParcelableTask>();
		for(Task task : tasks2)
			tasks.add(new ParcelableTask(task));
		bundle.putParcelableArrayList("addedTasks", tasks);
		msg.setData(bundle);
		try {
			messenger.send(msg);
		} catch (android.os.RemoteException e1) {
			//Log.d(getClass().getName(), "Exception sending message", e1);
		}
	}
	

}
