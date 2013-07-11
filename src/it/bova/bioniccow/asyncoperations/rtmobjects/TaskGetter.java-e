package it.bova.bioniccow.asyncoperations.rtmobjects;

import it.bova.bioniccow.asyncoperations.DefaultInquirer;
import it.bova.bioniccow.data.ApiSingleton;
import it.bova.rtmapi.RtmApiException;
import it.bova.rtmapi.ServerException;
import it.bova.rtmapi.Task;

import java.io.IOException;
import java.util.List;

import android.content.Context;

public class TaskGetter extends DefaultInquirer<String,List<Task>> {

	public TaskGetter(String NOKPhrase, Context context) {
		super("",NOKPhrase, context );
	}
	
	@Override public List<Task> inquire(String... filters) throws ServerException, RtmApiException, IOException {
		String filterString = "";
		for(String filter : filters)
			filterString += filter;
		return ApiSingleton.getApi(this.getContext()).tasksGetByFilter(filterString);
	}

}
