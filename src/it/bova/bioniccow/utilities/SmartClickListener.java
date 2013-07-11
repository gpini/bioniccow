package it.bova.bioniccow.utilities;

import android.view.View;

public abstract class SmartClickListener<T> implements View.OnClickListener {
	T object;
	public SmartClickListener(T object) {this.object = object;}
	public T get() {return this.object;}
	public void set(T object) {this.object = object;}

}
