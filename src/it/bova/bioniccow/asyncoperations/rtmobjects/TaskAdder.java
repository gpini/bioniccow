package it.bova.bioniccow.asyncoperations.rtmobjects;

import it.bova.bioniccow.asyncoperations.DefaultInquirer;
import it.bova.bioniccow.asyncoperations.MessageSender;
import it.bova.bioniccow.asyncoperations.sync.Synchronizer;
import it.bova.bioniccow.data.ApiSingleton;
import it.bova.rtmapi.RtmApi;
import it.bova.rtmapi.RtmApiException;
import it.bova.rtmapi.ServerException;
import it.bova.rtmapi.Task;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import android.content.Context;

public class TaskAdder extends DefaultInquirer<String,Task> {
	
	public TaskAdder(String OKPhrase, String NOKPhrase, Context context) {
		super(OKPhrase, NOKPhrase, context );
	}
	public Task inquire(String... names) throws RtmApiException, ServerException, IOException {
		Context context = this.getContext();
		RtmApi api = ApiSingleton.getApi(context);
		String timeline = ApiSingleton.getTimeline(context);
		if(names.length > 0) return api.tasksAddSmartly(timeline,names[0]);
		else return api.tasksAddSmartly(timeline,"");
	}
	
	@Override protected void onResultObtained(Task result) {
		if(result != null) {
			Synchronizer synchronizer = new Synchronizer(this.getContext());
			List<Task> tasks = new ArrayList<Task>();
			tasks.add(result);
			synchronizer.syncAddedTasks(tasks);	
			Formatter formatter = new Formatter();
			String OK = formatter.format(getOKPhrase(), result.getName()).toString();
			formatter.close();
			MessageSender.sendMessage(this.getContext(), OK);
		}
	}
}
