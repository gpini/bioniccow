package it.bova.bioniccow.utilities;

import android.view.View;

public abstract class SmartLongClickListener<T> implements View.OnLongClickListener {
	T[] objects;
	public SmartLongClickListener(T... objects) {this.objects = objects;}
	public T[] get() {return this.objects;}
	public void set(T... objects) {this.objects = objects;}

}
