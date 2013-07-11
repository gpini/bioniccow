package it.bova.bioniccow.utilities;

import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

public class ImprovedArrayAdapter<T> extends ArrayAdapter<T> {
	public ImprovedArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}
	public ImprovedArrayAdapter(Context context, int resource, int textViewResourceId) {
		super(context, resource, textViewResourceId);
	}
	public ImprovedArrayAdapter(Context context, int textViewResourceId, T[] objects) {
		super(context, textViewResourceId, objects);
	}
	public ImprovedArrayAdapter(Context context, int resource, int textViewResourceId, T[] objects) {
		super(context, resource, textViewResourceId, objects);
	}
	public ImprovedArrayAdapter(Context context, int textViewResourceId, List<T> objects) {
		super(context, textViewResourceId, objects);
	}
	public ImprovedArrayAdapter(Context context, int resource, int textViewResourceId, List<T> objects) {
		super(context, resource, textViewResourceId, objects);
	}
	
	public void addAll(T... objects) {
		//append objects at the end of the adapter
		for(T object : objects)
			this.add(object);
	}
	
	public void addAll(Collection<? extends T> collection) {
		//append objects at the end of the adapter
		for(T object : collection)
			this.add(object);
	}
	
	public void reload(T... objects) {
		//clear adapter and load the objects
		this.clear();
		for(T object : objects)
			this.add(object);
	}	
	
	public void reload(Collection<? extends T> collection) {
		//clear adapter and load the objects
		this.clear();
		for(T object : collection)
			this.add(object);
	}
	
	public void reloadAndNotify(Collection<? extends T> collection) {
		//clear adapter and load the objects, then notifies adapter
		this.clear();
		for(T object : collection)
			this.add(object);
		this.notifyDataSetChanged();
	}
	
	public void reloadAndNotify(T... objects) {
		//clear adapter and load the objects, then notifies adapter
		this.clear();
		for(T object : objects)
			this.add(object);
		this.notifyDataSetChanged();
	}
	
	
	
}
