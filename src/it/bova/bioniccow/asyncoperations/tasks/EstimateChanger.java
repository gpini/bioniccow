package it.bova.bioniccow.asyncoperations.tasks;

import it.bova.bioniccow.data.ApiSingleton;
import it.bova.rtmapi.RtmApi;
import it.bova.rtmapi.RtmApiException;
import it.bova.rtmapi.ServerException;
import it.bova.rtmapi.Task;

import java.io.IOException;
import java.util.List;

import android.content.Context;

public class EstimateChanger extends TaskEditor {

	public EstimateChanger(Context context, Task task, String estimate) {
		super("estimate", context, task, estimate);
	}
		
	protected List<Task> inquire(Void... empty) throws ServerException, RtmApiException, IOException {
		Context c = this.getContext();
		RtmApi api = ApiSingleton.getApi(c);
		String timeline = ApiSingleton.getTimeline(c);
		String estimate = "";
		if(this.getParam().length > 0 && this.getParam()[0] != null)
			estimate = (String) this.getParam()[0];
		return api.tasksSetEstimate(timeline,this.getTask(),estimate);
	}


}
