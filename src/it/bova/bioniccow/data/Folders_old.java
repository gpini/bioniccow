package it.bova.bioniccow.data;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

public class Folders_old extends DataObservable<Map<String,Folder>> {
	
	public Folders_old(Context context) {
		super(context);
	}

	private static Map<String,Folder> folderMap;
	private static List<DataObserver<Map<String,Folder>>> observers = 
			new ArrayList<DataObserver<Map<String,Folder>>>();
	private static final String FOLDERS_FILENAME = "folders.dat";
	
	
	@Override
	Map<String,Folder> getData() {
		return folderMap;
	}

	@Override
	void setData(Map<String,Folder> folders) {
		if(folders != null) folderMap = folders;
	}

	@Override
	Map<String,Folder> emptyData() {
		return new HashMap<String,Folder>();

	}

	@Override List<DataObserver<Map<String,Folder>>> getObservers() {
		return observers;
	}
	
	@Override String getFileName() {
		return FOLDERS_FILENAME;
	}
	
}
