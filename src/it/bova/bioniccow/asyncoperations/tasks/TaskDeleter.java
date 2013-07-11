package it.bova.bioniccow.asyncoperations.tasks;

import it.bova.bioniccow.data.ApiSingleton;
import it.bova.rtmapi.RtmApi;
import it.bova.rtmapi.RtmApiException;
import it.bova.rtmapi.ServerException;
import it.bova.rtmapi.Task;

import java.io.IOException;
import java.util.List;

import android.content.Context;

public class TaskDeleter extends TaskInquirer {

	public TaskDeleter(Context context, Task task) {
		super(context, task);
	}

	@Override protected List<Task> inquire(Void... params) throws IOException,
			ServerException, RtmApiException, Exception {
		Context c = this.getContext();
		RtmApi api = ApiSingleton.getApi(c);
		String timeline = ApiSingleton.getTimeline(c);
		return api.tasksDelete(timeline, this.getTask());
	}

}
