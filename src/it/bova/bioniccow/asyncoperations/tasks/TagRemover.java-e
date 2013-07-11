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

public class TagRemover extends TaskEditor {

	public TagRemover(Context context, Task task, Object... tags) {
		super("tag", context, task, tags);
	}
		
	protected List<Task> inquire(Void... empty) throws ServerException, RtmApiException, IOException {
		Context c = this.getContext();
		RtmApi api = ApiSingleton.getApi(c);
		String timeline = ApiSingleton.getTimeline(c);
		String[] tagsToBeRemoved = new String[0];
		if(this.getParam() == null)
			tagsToBeRemoved = new String[0];
		else {
			List<String> tmpTags = new ArrayList<String>();
			for(int i = 0; i < this.getParam().length; i++){
				if(this.getParam()[i] != null)
					tmpTags.add((String) this.getParam()[i]);
			}
			tagsToBeRemoved = new String[tmpTags.size()];
			for(int i = 0; i < tmpTags.size(); i++)
				tagsToBeRemoved[i] = tmpTags.get(i);
		}
		return api.tasksRemoveTags(timeline, this.getTask(), tagsToBeRemoved);

	}


}
