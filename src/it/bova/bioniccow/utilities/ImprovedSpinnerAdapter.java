package it.bova.bioniccow.utilities;

import it.bova.rtmapi.RtmObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ImprovedSpinnerAdapter<T extends RtmObject> extends BaseAdapter {
	
	private List<T> list = new ArrayList<T>();
	private Context context;

	public ImprovedSpinnerAdapter(Context context) {
		this.setContext(context);
	}
	
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void add(T object) {
		this.list.add(object);
	}
	
	public void clear() {
		this.list.clear();
	}
	
	public int getCount() {
		if(list!= null)
			return this.list.size();
		else return 0;
	}
	
	public T getItem(int position) {
		return this.list.get(position);
	}
	
	public long getItemId(int position) {
		return position;
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
	
	@Override public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater inflater 
				= (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(android.R.layout.simple_spinner_item, null);
		}
		TextView tv = (TextView) convertView;
		T object = this.getItem(position);
		tv.setText(object.toString());
		return tv;
	}	
	
	@Override public View getDropDownView(int position, View convertView, ViewGroup parent){
		if(convertView == null) {
			LayoutInflater inflater 
				= (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, null);
		}
		TextView tv = (TextView) convertView;
		T object = this.getItem(position);
		tv.setText(object.toString());
		return tv;
	}
	
	public int findPositionByRtmObjectId(String id) {
		int size = this.getCount();
		int selectedPosition = -1;
		if(size > 0) {
			for(int i = 0; i < size; i++) {
				if(this.getItem(i).getId().equals(id)) {
					selectedPosition = i;
					break;
				}
			}
		}
		return selectedPosition;
	}
	
	
}
