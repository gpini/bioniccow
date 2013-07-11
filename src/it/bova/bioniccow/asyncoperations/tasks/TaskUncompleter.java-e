package it.bova.bioniccow.asyncoperations.tasks;

import it.bova.bioniccow.data.ApiSingleton;
import it.bova.rtmapi.RtmApi;
import it.bova.rtmapi.RtmApiException;
import it.bova.rtmapi.ServerException;
import it.bova.rtmapi.Task;

import java.io.IOException;
import java.util.List;

import android.content.Context;

public class TaskUncompleter extends TaskInquirer {

	public TaskUncompleter(Context context, Task task) {
		super(context, task);
	}

	@Override
	protected List<Task> inquire(Void... params) throws IOException,
			ServerException, RtmApiException, Exception {
		Context c = this.getContext();
		RtmApi api = ApiSingleton.getApi(c);
		String timeline = ApiSingleton.getTimeline(c);
		if(this.getTask().getCompleted() == null)
			return api.tasksComplete(timeline, this.getTask());
		else
			return api.tasksUncomplete(timeline, this.getTask());
	}

}
