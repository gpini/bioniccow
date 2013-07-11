package it.bova.bioniccow.utilities;

import it.bova.rtmapi.Task;

public class TaskCloner {
	
	public static Task clone(Task task) {
		Task cloned = new Task(
				task.getId(), task.getName(),
				task.getAdded(), task.getCompleted(), task.getDeleted(),
				task.getDue(), task.getEstimate(), task.getHasDueTime(),
				task.getPostponed(), task.getPriority(), task.getTaskserieId(),
				task.getLocationId(), task.getListId(), task.getCreated(), 
				task.getModified(), task.getNotes(), task.getRecurrence(), 
				task.getParticipants(), task.getSource(), task.getTags(), task.getUrl());
		return cloned;
	}

}
