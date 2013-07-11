package it.bova.bioniccow.data;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

public class Folders extends DataObservable<List<Folder>> {
	
	public Folders(Context context) {
		super(context);
	}

	private static List<Folder> folderList;
	private static List<DataObserver<List<Folder>>> observers = 
			new ArrayList<DataObserver<List<Folder>>>();
	private static final String OLD_FOLDERS_FILENAME = "folders.dat";
	private static final String FOLDERS_FILENAME = "folders2.dat";
	
	
	@Override
	List<Folder> getData() {
		return folderList;
	}

	@Override
	void setData(List<Folder> folders) {
		if(folders != null) folderList = folders;
	}

	@Override
	List<Folder> emptyData() {
		return new ArrayList<Folder>();

	}

	@Override List<DataObserver<List<Folder>>> getObservers() {
		return observers;
	}
	
	@Override String getFileName() {
		return FOLDERS_FILENAME;
	}
	
	public Map<String,Folder> retrieveAsMap() {
//		Object obj = this.retrieveRaw();
//		if(obj instanceof Map)
//			this.save(toList((Map<String,Folder>) obj));
		return toMap(this.retrieve());
	}
	
	@Override public List<Folder> retrieve() {
		FileInputStream fis = null;
		try {
			fis = this.getContext().openFileInput(OLD_FOLDERS_FILENAME);
			Map<String,Folder> folderMap = 
					new Serializer<Map<String,Folder>>(OLD_FOLDERS_FILENAME,
					this.getContext()).deserialize();
			this.saveAsList(folderMap);
			this.getContext().deleteFile(OLD_FOLDERS_FILENAME);
			return super.retrieve();
		} catch(FileNotFoundException fnfe) {
			return super.retrieve();
		} catch (IOException e) {
			this.setDataAndNotify(this.emptyData());
			return super.retrieve();
		} finally {
			if(fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					//do nothing
				}
		} 
	}
	
	public void saveAndNotifyAsList(Map<String,Folder> folderMap) {
		List<Folder> folderList = toList(folderMap);
		this.saveAndNotify(folderList);
	}
	
	public void saveAsList(Map<String,Folder> folderMap) {
		List<Folder> folderList = toList(folderMap);
		this.save(folderList);
	}
	
	private Map<String,Folder> toMap(List<Folder> folderList) {
		Map<String,Folder> folderMap = new HashMap<String,Folder>();
		for(Folder folder : folderList)
			folderMap.put(folder.getName(), folder);
		return folderMap;
	}
	
	private List<Folder> toList(Map<String,Folder> folderMap) {
		List<Folder> folderList = new ArrayList<Folder>();
		for(Folder folder : folderMap.values())
			folderList.add(folder);
		return folderList;
	}
	
}
