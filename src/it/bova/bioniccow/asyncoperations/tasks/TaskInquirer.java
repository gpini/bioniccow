package it.bova.bioniccow.asyncoperations.tasks;

import it.bova.bioniccow.asyncoperations.Inquirer;
import it.bova.rtmapi.Task;

import java.util.List;

import android.content.Context;

public abstract class TaskInquirer extends Inquirer<Void,List<Task>> {
	
	private Task task;
	
	public TaskInquirer(Context context, Task task) {
		super(context);
		this.task = task;
	}
	
	public Task getTask() {return this.task;}
	public void setTask(Task task) {this.task = task;}

}
