package it.bova.bioniccow.asyncoperations.tasks;

import it.bova.bioniccow.data.ApiSingleton;
import it.bova.rtmapi.RtmApi;
import it.bova.rtmapi.RtmApiException;
import it.bova.rtmapi.ServerException;
import it.bova.rtmapi.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class TagAdder extends TaskEditor {

	public TagAdder(Context context, Task task, Object... tags) {
		super("tag", context, task, tags);
	}
		
	protected List<Task> inquire(Void... empty) throws ServerException, RtmApiException, IOException {
		Context c = this.getContext();
		RtmApi api = ApiSingleton.getApi(c);
		String timeline = ApiSingleton.getTimeline(c);
		String[] tagsToBeAdded = new  String[0];
		if(this.getParam() == null)
			tagsToBeAdded = new String[0];
		else {
			List<String> tmpTags = new ArrayList<String>();
			for(int i = 0; i < this.getParam().length; i++) {
				if(this.getParam()[i] != null)
					tmpTags.add((String) this.getParam()[i]);
			}
			tagsToBeAdded = new String[tmpTags.size()];
			for(int i = 0; i < tmpTags.size(); i++)
				tagsToBeAdded[i] = tmpTags.get(i);
		}
		return api.tasksAddTags(timeline, this.getTask(), tagsToBeAdded);
	}


}
