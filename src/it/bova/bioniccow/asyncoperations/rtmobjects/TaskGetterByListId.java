package it.bova.bioniccow.asyncoperations.rtmobjects;

import it.bova.bioniccow.data.ApiSingleton;
import it.bova.rtmapi.RtmApiException;
import it.bova.rtmapi.ServerException;
import it.bova.rtmapi.Task;

import java.io.IOException;
import java.util.List;

import android.content.Context;

public class TaskGetterByListId extends TaskGetter {

	public TaskGetterByListId(String NOKPhrase, Context context) {
		super(NOKPhrase, context);
	}
	
	@Override public List<Task> inquire(String... listIds) throws ServerException, RtmApiException, IOException {
		if(listIds.length < 1) return ApiSingleton.getApi(this.getContext()).tasksGetByListId("");;
		return ApiSingleton.getApi(this.getContext()).tasksGetByListId(listIds[0]);
	}

}
