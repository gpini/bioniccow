package it.bova.bioniccow.utilities.rtmobjects;

import it.bova.rtmapi.Task;

import java.util.Date;

public class InboxTaskComparator extends TaskComparator {
	public int compare(Task task1, Task task2) {
      Date due1 = task1.getDue();
	  Date due2 = task2.getDue();
	  if(due1 == null && due2 != null) return 1;
	  else if(due1 != null && due2 == null) return -1;
	  else if(due1 != null && due2 != null) {
		int compareDues = due1.compareTo(due2);
		if(compareDues == 0) {
			int comparePriorities = comparePriorities(task1.getPriority(), task2.getPriority());
			if(comparePriorities == 0) return task1.getName().compareTo(task2.getName());
			else return comparePriorities;
		}
		else return compareDues;
	  }
	  else {
		int comparePriorities = comparePriorities(task1.getPriority(), task2.getPriority());
		if(comparePriorities == 0) return task1.getName().compareTo(task2.getName());
		else return comparePriorities;
	  }
   }
}
