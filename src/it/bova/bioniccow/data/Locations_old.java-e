package it.bova.bioniccow.data;

import it.bova.rtmapi.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

public class Locations_old extends DataObservable<Map<String,Location>> {
	
	public Locations_old(Context context) {
		super(context);
	}

	private static Map<String,Location> locationMap;
	private static List<DataObserver<Map<String,Location>>> observers = 
			new ArrayList<DataObserver<Map<String,Location>>>();
	private static final String LOC_FILENAME = "locations.dat";
	
	
	@Override
	Map<String, Location> getData() {
		return locationMap;
	}

	@Override
	void setData(Map<String, Location> locations) {
		if(locations != null) locationMap = locations;
	}

	@Override
	Map<String,Location> emptyData() {
		return new HashMap<String,Location>();
	}

	@Override List<DataObserver<Map<String, Location>>> getObservers() {
		return observers;
	}
	
	@Override String getFileName() {
		return LOC_FILENAME;
	}



}
