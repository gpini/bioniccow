package it.bova.bioniccow.asyncoperations.tasks;

import it.bova.bioniccow.data.ApiSingleton;
import it.bova.rtmapi.RtmApi;
import it.bova.rtmapi.RtmApiException;
import it.bova.rtmapi.ServerException;
import it.bova.rtmapi.Task;

import java.io.IOException;
import java.util.List;

import android.content.Context;

public class LocationChanger extends TaskEditor {

	public LocationChanger(Context context, Task task, Object... ids) {
		super("location", context, task, ids);
	}
		
	protected List<Task> inquire(Void... empty) throws ServerException, RtmApiException, IOException {
		Context c = this.getContext();
		RtmApi api = ApiSingleton.getApi(c);
		String timeline = ApiSingleton.getTimeline(c);
		String listId = "";
		String locationId = "";
		if(this.getParam().length > 1) {
			if(this.getParam()[0] != null)
				listId = (String) this.getParam()[0];
			if(this.getParam()[1] != null)
				locationId = (String) this.getParam()[1];
		}
		if(locationId.equals("")) 
			return api.tasksUnsetLocation(timeline, this.getTask());
		else
			return api.tasksSetLocation(timeline, this.getTask().getId(),
				this.getTask().getTaskserieId(), listId, locationId);
	}


}
