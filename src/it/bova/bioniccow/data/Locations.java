package it.bova.bioniccow.data;

import it.bova.rtmapi.Location;
import it.bova.rtmapi.TaskList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

public class Locations extends DataObservable<List<Location>> {
	
	public Locations(Context context) {
		super(context);
	}

	private static List<Location> locationList;
	private static List<DataObserver<List<Location>>> observers = 
			new ArrayList<DataObserver<List<Location>>>();
	private static final String LOC_FILENAME = "locations2.dat";
	
	
	@Override
	List<Location> getData() {
		return locationList;
	}

	@Override
	void setData(List<Location> locations) {
		if(locations != null) locationList = locations;
	}

	@Override
	List<Location> emptyData() {
		return new ArrayList<Location>();
	}

	@Override List<DataObserver<List<Location>>> getObservers() {
		return observers;
	}
	
	public Map<String,Location> retrieveAsMap() {
		return toMap(this.retrieve());
	}
	
	public void saveAndNotifyAsList(Map<String,Location> locMap) {
		List<Location> locList = toList(locMap);
		this.saveAndNotify(locList);
	}
	
	public void saveAsList(Map<String,Location> locMap) {
		List<Location> locList = toList(locMap);
		this.save(locList);
	}
	
	private Map<String,Location> toMap(List<Location> locList) {
		Map<String,Location> locMap = new HashMap<String,Location>();
		for(Location location : locList)
			locMap.put(location.getId(), location);
		return locMap;
	}
	
	private List<Location> toList(Map<String,Location> locMap) {
		List<Location> locList = new ArrayList<Location>();
		for(Location location : locMap.values())
			locList.add(location);
		return locList;
	}
	
	@Override String getFileName() {
		return LOC_FILENAME;
	}



}
