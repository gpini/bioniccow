package it.bova.bioniccow.asyncoperations.tasks;

import it.bova.bioniccow.data.ApiSingleton;
import it.bova.rtmapi.RtmApi;
import it.bova.rtmapi.RtmApiException;
import it.bova.rtmapi.ServerException;
import it.bova.rtmapi.Task;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import android.content.Context;

public class DueDateChanger extends TaskEditor {

	public DueDateChanger(Context context, Task task, Object... params) {
		super("due", context, task, params);
	}
		
	protected List<Task> inquire(Void... empty) throws ServerException, RtmApiException, IOException {
		Context c = this.getContext();
		RtmApi api = ApiSingleton.getApi(c);
		String timeline = ApiSingleton.getTimeline(c);
		Date due = null;
		boolean hasDueTime = false;
		if(this.getParam().length > 1) {
			try {
				if(this.getParam()[0] != null)
					due = (Date) this.getParam()[0];
				if(this.getParam()[1] != null)
					hasDueTime = (Boolean) this.getParam()[1];
			} catch(Exception e) {}
		}
		if(due == null) 
			return api.tasksSetDueDate(timeline, this.getTask(), "", false);
		else
			return api.tasksSetDueDate(timeline, this.getTask(), due, hasDueTime);
	}


}
