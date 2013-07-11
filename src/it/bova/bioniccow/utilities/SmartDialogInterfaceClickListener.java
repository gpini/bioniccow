package it.bova.bioniccow.utilities;

import android.content.DialogInterface;

public abstract class SmartDialogInterfaceClickListener<T> implements DialogInterface.OnClickListener {
	T object;
	public SmartDialogInterfaceClickListener(T object) {this.object = object;}
	public T get() {return this.object;}
	public void set(T object) {this.object = object;}

}
