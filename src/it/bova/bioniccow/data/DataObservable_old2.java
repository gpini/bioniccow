package it.bova.bioniccow.data;


import java.io.IOException;
import java.util.List;

import android.content.Context;

public abstract class DataObservable_old2<T> {
	
	abstract List<DataObserver_old2<T>> getObservers();
	abstract String getFileName();
	abstract T getData();
	abstract void setData(T t);
	abstract T emptyData();
	
	private Context context;
	
	DataObservable_old2(Context context) {
		this.context = context;
	}
	
	public Context getContext() {
		return context;
	}
	
	public void addObserver(DataObserver_old2<T> dataObserver) {getObservers().add(dataObserver);}
	public void removeObserver(DataObserver_old2<T> dataObserver) {getObservers().remove(dataObserver);}
	public void removeObservers() {getObservers().clear();}
	public void notifyObservers() {
		for(DataObserver_old2<T> observer : getObservers()) {
			observer.onDataChanged(this.getData());
		}
	}
	
	void setDataAndNotify(T t) {
		this.setData(t);
		this.notifyObservers();
	}	
	
	public void saveAndNotify(T t) {
		String filename = getFileName();
			new Serializer<T>(filename,this.context).serialize(t); //synchronized over filename
			if(t != null) this.setDataAndNotify(t);
			else this.setDataAndNotify(this.emptyData());
	}
	
	public void save(T t) {
		String filename = getFileName();
			new Serializer<T>(filename,this.context).serialize(t); //synchronized over filename
			if(t != null) this.setData(t);
			else this.setData(this.emptyData());
	}
	
	public void notifyDataChanged(){
		this.notifyObservers();
	}
	
	Object retrieveRaw() {
		try {
			return new Serializer<Object>(getFileName(),this.context).deserialize();
		} catch (IOException e) {
			return null;
		}
	}
	
	public T retrieve() {
		String filename = getFileName();
		if(this.getData() == null) {
			T t;
			try {
				t = new Serializer<T>(filename,this.context).deserialize();
				if(t != null) this.setDataAndNotify(t);
				else this.setDataAndNotify(this.emptyData());
			} catch (IOException e) {
				this.setDataAndNotify(this.emptyData());
			}
		}
		return this.getData(); 
	}
}