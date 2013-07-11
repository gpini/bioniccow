package it.bova.bioniccow.asyncoperations.tasks;

import it.bova.bioniccow.data.ApiSingleton;
import it.bova.rtmapi.RtmApi;
import it.bova.rtmapi.RtmApiException;
import it.bova.rtmapi.ServerException;
import it.bova.rtmapi.Task;

import java.io.IOException;
import java.util.List;

import android.content.Context;

public class TaskListChanger extends TaskEditor {

	public TaskListChanger(Context context, Task task, Object... lists) {
		super("list", context, task, lists);
	}
		
	protected List<Task> inquire(Void... empty) throws ServerException, RtmApiException, IOException {
		Context c = this.getContext();
		RtmApi api = ApiSingleton.getApi(c);
		String timeline = ApiSingleton.getTimeline(c);
		String fromListId = "";
		String toListId = "";
		if(this.getParam().length > 1) {
			if(this.getParam()[0] != null) fromListId = (String) this.getParam()[0];
			if(this.getParam()[1] != null) toListId = (String) this.getParam()[1];
		}
		return api.tasksMoveTo(timeline, this.getTask().getId(),
				this.getTask().getTaskserieId(), fromListId, toListId);
	}


}
