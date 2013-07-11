package it.bova.bioniccow.asyncoperations.tasks;

import it.bova.bioniccow.data.ApiSingleton;
import it.bova.rtmapi.RtmApi;
import it.bova.rtmapi.RtmApiException;
import it.bova.rtmapi.ServerException;
import it.bova.rtmapi.Task;

import java.io.IOException;
import java.util.List;

import android.content.Context;

public class RecurrenceChanger extends TaskEditor {

	public RecurrenceChanger(Context context, Task task, String repeat) {
		super("recurrence", context, task, repeat);
	}
		
	protected List<Task> inquire(Void... empty) throws ServerException, RtmApiException, IOException {
		Context c = this.getContext();
		RtmApi api = ApiSingleton.getApi(c);
		String timeline = ApiSingleton.getTimeline(c);
		String recurrence = "";
		if(this.getParam().length > 0 && this.getParam()[0] != null)
			recurrence = (String) this.getParam()[0];
		if(recurrence == null) recurrence = "";
		return api.tasksSetRecurrence(timeline, this.getTask(), recurrence);
	}


}
