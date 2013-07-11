package it.bova.bioniccow.utilities.rtmobjects;

import it.bova.rtmapi.TaskList;

import java.util.Comparator;

public class TaskListComparator implements Comparator<TaskList> {
   public int compare(TaskList list1, TaskList list2) {
      if(list1.isSmart() & !list2.isSmart()) return 1;
      else if(!list1.isSmart() & list2.isSmart()) return -1;
	  //anticipare "Inbox" e "Sent"? A mano o forse ordinare per ID
      else return list1.getName().compareTo(list2.getName());
   }
}
