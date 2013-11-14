package it.bova.bioniccow.asyncoperations.sync;


import it.bova.bioniccow.data.Folder;
import it.bova.bioniccow.data.Folders_old2;
import it.bova.bioniccow.data.Locations_old2;
import it.bova.bioniccow.data.Tags_old2;
import it.bova.bioniccow.data.TaskLists_old2;
import it.bova.bioniccow.data.database.TaskDatabase;
import it.bova.bioniccow.utilities.rtmobjects.ParcelableTask;
import it.bova.rtmapi.Location;
import it.bova.rtmapi.TaskList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import android.content.Context;

public abstract class TasksUpdater {
	
	private Context context;

	public TasksUpdater(Context context) {this.context = context;}
	
	public ArrayList<ParcelableTask> update(ArrayList<ParcelableTask> deletedTasks) {
		try {
			TaskDatabase.open(context);
			//update tasks with changed tasks (they will be overriden by next synch action)
			onUpdate(deletedTasks);

			//update tags
			Tags_old2 tags = new Tags_old2(context);
			Set<String> tagSet = TaskDatabase.getDistinctTags();
			//Log.d("(change) tags", "" + tagSet.size());
			tags.save(tagSet);

			//update folders
			Folders_old2 folders = new Folders_old2(context);
			Map<String,Folder> folderMap = folders.retrieveAsMap();
			Map<String, TaskList> listMap = new TaskLists_old2(context).retrieveAsMap();
			Map<String, Location> locMap = new Locations_old2(context).retrieveAsMap();
			Map<String,Folder> newFolderMap
				= TasksUpdater.updateFolders(folderMap, tagSet, listMap, locMap);
			folders.saveAsList(newFolderMap);
			return deletedTasks;
		}catch(Exception e) {
			//Log.d("DB error", e.getMessage());
			return null;
		}
		finally {
			TaskDatabase.close();
		}
	}

	protected abstract void onUpdate(List<ParcelableTask> updatedTasks);
	
	public static Map<String, Folder> updateFolders(Map<String,Folder> folders,
	Set<String> tagSet, Map<String,TaskList> listMap, Map<String,Location> locMap) {
		for(Folder folder : folders.values()) {
			switch(folder.getApplicability()) {
			case TAGS :
				List<String> tagElements = folder.loadTagElements(tagSet);
				folder.setTagElements(tagElements);
				break;
			case LISTS :
				folder.setListElements(folder.loadListElements(listMap));
				break;
			case LOCATIONS :
				folder.setLocationElements(folder.loadLocationElements(locMap));
				break;
			case EVERYTHING :
				folder.setTagElements(folder.loadTagElements(tagSet));
				folder.setListElements(folder.loadListElements(listMap));
				folder.setLocationElements(folder.loadLocationElements(locMap));
				break;
			}
		}
		return folders;
	}
}
