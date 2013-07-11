package it.bova.bioniccow.utilities.rtmobjects;

import it.bova.rtmapi.Task;

import java.io.Serializable;


public class CheckableTask_old extends Task implements Serializable{
	
	private boolean isChecked;

	public CheckableTask_old(Task task, boolean isChecked) {
		super(task.getId(), task.getName(), task.getAdded(),
				task.getCompleted(), task.getDeleted(), task.getDue(), 
				task.getEstimate(), task.getHasDueTime(), task.getPostponed(),
				task.getPriority(), task.getTaskserieId(), task.getLocationId(),
				task.getListId(), task.getCreated(), task.getModified(),
				task.getNotes(), task.getRecurrence(), task.getParticipants(),
				task.getSource(), task.getTags(), task.getUrl());
		this.isChecked = isChecked;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	
//	@Override public boolean equals(Object o) {
//		if(o instanceof Task)
//			return this.getId().equals(((Task) o).getId());
//		return false;
//	} 

	
}
