package it.bova.bioniccow.asyncoperations.tasks;

import android.content.Context;

import it.bova.rtmapi.Task;

public abstract class TaskEditor extends TaskInquirer {

	private String fieldName;
	private Object[] params;
	
	public TaskEditor(String fieldName, Context context, Task task, Object... newParams) {
		super(context, task);
		this.fieldName = fieldName;
		this.params = newParams;
	}
	
	public String getFieldName() {return this.fieldName;}
	public void setFieldName(String fieldName) {this.fieldName = fieldName;}
	
	public Object[] getParam() {return this.params;}
	public void setParam(Object... params) {this.params = params;}
	
}
