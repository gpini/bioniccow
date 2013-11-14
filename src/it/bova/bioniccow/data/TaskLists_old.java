package it.bova.bioniccow.data;

import it.bova.rtmapi.TaskList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

public class TaskLists_old extends DataObservable_old2<Map<String,TaskList>> {
	
	public TaskLists_old(Context context) {
		super(context);
	}

	private static Map<String,TaskList> listMap;
	private static List<DataObserver_old2<Map<String,TaskList>>> observers = 
			new ArrayList<DataObserver_old2<Map<String,TaskList>>>();
	private static final String LIST_FILENAME = "lists.dat";
	
	
	@Override Map<String, TaskList> getData() {
		return listMap;
	}

	@Override void setData(Map<String, TaskList> lists) {
		if(lists != null) listMap = lists;
	}

	@Override Map<String,TaskList> emptyData() {
		return new HashMap<String,TaskList>();
	}

	@Override List<DataObserver_old2<Map<String, TaskList>>> getObservers() {
		return observers;
	}
	
	@Override String getFileName() {
		return LIST_FILENAME;
	}
	
	@Override public String toString() {
		return "" + listMap + observers;
	}



}
