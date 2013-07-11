package it.bova.bioniccow.asyncoperations.tasks;

import it.bova.bioniccow.data.ApiSingleton;
import it.bova.rtmapi.Priority;
import it.bova.rtmapi.RtmApi;
import it.bova.rtmapi.RtmApiException;
import it.bova.rtmapi.ServerException;
import it.bova.rtmapi.Task;

import java.io.IOException;
import java.util.List;

import android.content.Context;

public class PriorityChanger extends TaskEditor {

	public PriorityChanger(Context context, Task task, String newPriority) {
		super("priority", context, task, newPriority);
	}
		
	protected List<Task> inquire(Void... empty) throws ServerException, RtmApiException, IOException {
		Context c = this.getContext();
		RtmApi api = ApiSingleton.getApi(c);
		String timeline = ApiSingleton.getTimeline(c);
		String priority = "-";
		if(this.getParam().length > 0 && this.getParam()[0] != null)
			priority = (String) this.getParam()[0];
		Priority p = Priority.NONE;
		if(priority.equals("1")) p = Priority.HIGH;
		else if(priority.equals("2")) p = Priority.MEDIUM;
		else if(priority.equals("3")) p = Priority.LOW;
		else p = Priority.NONE;
		return api.tasksSetPriority(timeline,this.getTask(),p);
	}


}
