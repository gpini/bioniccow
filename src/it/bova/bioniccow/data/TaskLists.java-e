package it.bova.bioniccow.data;

import it.bova.rtmapi.TaskList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

public class TaskLists extends DataObservable<List<TaskList>> {
	
	public TaskLists(Context context) {
		super(context);
	}

	private static List<TaskList> listList;
	private static List<DataObserver<List<TaskList>>> observers = 
			new ArrayList<DataObserver<List<TaskList>>>();
	private static final String LIST_FILENAME = "lists2.dat";
	
	
	@Override List<TaskList> getData() {
		return listList;
	}

	@Override void setData(List<TaskList> lists) {
		if(lists != null) listList = lists;
	}

	@Override List<TaskList> emptyData() {
		return new ArrayList<TaskList>();
	}

	@Override List<DataObserver<List<TaskList>>> getObservers() {
		return observers;
	}
	
	@Override String getFileName() {
		return LIST_FILENAME;
	}
	
	public Map<String,TaskList> retrieveAsMap() {
		return toMap(this.retrieve());
	}
	
	public void saveAndNotifyAsList(Map<String,TaskList> listMap) {
		List<TaskList> listList = toList(listMap);
		this.saveAndNotify(listList);
	}
	
	public void saveAsList(Map<String,TaskList> listMap) {
		List<TaskList> listList = toList(listMap);
		this.save(listList);
	}
	
	private Map<String,TaskList> toMap(List<TaskList> listList) {
		Map<String,TaskList> listMap = new HashMap<String,TaskList>();
		for(TaskList tasklist : listList)
			listMap.put(tasklist.getId(), tasklist);
		return listMap;
	}
	
	private List<TaskList> toList(Map<String,TaskList> listMap) {
		List<TaskList> listList = new ArrayList<TaskList>();
		for(TaskList tasklist : listMap.values())
			listList.add(tasklist);
		return listList;
	}
	
	@Override public String toString() {
		return "" + listList + observers;
	}



}
