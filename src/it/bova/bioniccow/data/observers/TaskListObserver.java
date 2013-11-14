package it.bova.bioniccow.data.observers;

import java.util.List;

import it.bova.bioniccow.data.DataObserver_old2;
import it.bova.rtmapi.TaskList;

public abstract class TaskListObserver extends DataObserver_old2<List<TaskList>> {
	protected abstract void onDataChanged(List<TaskList> lists);
}
