package it.bova.bioniccow.asyncoperations.tasks;

import it.bova.bioniccow.data.ApiSingleton;
import it.bova.rtmapi.RtmApi;
import it.bova.rtmapi.RtmApiException;
import it.bova.rtmapi.ServerException;
import it.bova.rtmapi.Task;

import java.io.IOException;
import java.util.List;

import android.content.Context;

public class NameChanger extends TaskEditor {

	public NameChanger(Context context, Task task, String newName) {
		super("name", context, task, newName);
	}
		
	protected List<Task> inquire(Void... empty) throws ServerException, RtmApiException, IOException {
		Context c = this.getContext();
		RtmApi api = ApiSingleton.getApi(c);
		String timeline = ApiSingleton.getTimeline(c);
		String name = "";
		if(this.getParam().length > 0 && this.getParam()[0] != null)
			name = (String) this.getParam()[0];
		return api.tasksSetName(timeline,this.getTask(),name);
	}


}
