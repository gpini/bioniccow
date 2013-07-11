package it.bova.bioniccow.utilities.rtmobjects;

import it.bova.rtmapi.Priority;
import it.bova.rtmapi.Task;

import java.util.Comparator;
import java.util.Date;

public class TaskComparator implements Comparator<Task> {
	public int compare(Task task1, Task task2) {
		if(task1.getCompleted() == null && task2.getCompleted() != null)
			return -1;
		if(task1.getCompleted() != null && task2.getCompleted() == null)
			return 1;
		else {
			int comparePriorities = comparePriorities(task1.getPriority(), task2.getPriority());
			if(comparePriorities == 0) {
				Date due1 = task1.getDue();
				Date due2 = task2.getDue();
				if(due1 == null && due2 != null) return 1;
				else if(due1 != null && due2 == null) return -1;
				else if(due1 != null && due2 != null) {
					int compareDues = due1.compareTo(due2);
					if(compareDues == 0)
						return task1.getName().compareTo(task2.getName());
					else return compareDues;
				}
				else return task1.getName().compareTo(task2.getName());
			}
			else return comparePriorities;
		}
	}
	
   protected int comparePriorities(Priority p1, Priority p2) {
		return p1.getLevel() - p2.getLevel();
   }
}
