package it.bova.bioniccow.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.content.Context;

public class Tags_old2 extends DataObservable_old2<Set<String>> {
	
	public Tags_old2(Context context) {
		super(context);
	}

	private static Set<String> tagSet;
	private static List<DataObserver_old2<Set<String>>> observers = 
			new ArrayList<DataObserver_old2<Set<String>>>();
	private static final String TAG_FILENAME = "tags.dat";
	
	
	@Override
	Set<String> getData() {
		return tagSet;
	}

	@Override
	void setData(Set<String> tags) {
		if(tags != null) tagSet = tags;
	}

	@Override
	Set<String> emptyData() {
		return new TreeSet<String>();
	}

	@Override List<DataObserver_old2<Set<String>>> getObservers() {
		return observers;
	}
	
	@Override String getFileName() {
		return TAG_FILENAME;
	}
	
}