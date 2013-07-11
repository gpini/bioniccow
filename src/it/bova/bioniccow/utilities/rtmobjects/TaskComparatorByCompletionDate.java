package it.bova.bioniccow.utilities.rtmobjects;

import it.bova.rtmapi.Priority;
import it.bova.rtmapi.Task;

import java.util.Comparator;
import java.util.Date;

public class TaskComparatorByCompletionDate implements Comparator<Task> {
	public int compare(Task task1, Task task2) {
		if(task1.getCompleted() == null && task2.getCompleted() != null)
			return -1;
		if(task1.getCompleted() != null && task2.getCompleted() == null)
			return 1;
		else {
			Date compl1 = task1.getCompleted();
			Date compl2 = task2.getCompleted();
			if(compl1 == null && compl2 != null) return -1;
			else if(compl1 != null && compl2 == null) return 1;
			else if(compl1 != null && compl2 != null) {
				int compareComplDate = compl2.compareTo(compl1);
				if(compareComplDate == 0) {
					int comparePriorities = comparePriorities(task1.getPriority(), task2.getPriority());
					if(comparePriorities == 0)
						return task1.getName().compareTo(task2.getName());
						else return comparePriorities;			
				}
				else return compareComplDate;
			}
			else return task1.getName().compareTo(task2.getName());

		}
	}
	
   protected int comparePriorities(Priority p1, Priority p2) {
		return p1.getLevel() - p2.getLevel();
   }
}
