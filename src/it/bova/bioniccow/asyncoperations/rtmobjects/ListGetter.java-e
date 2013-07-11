package it.bova.bioniccow.asyncoperations.rtmobjects;

import it.bova.bioniccow.asyncoperations.DefaultInquirer;
import it.bova.bioniccow.data.ApiSingleton;
import it.bova.rtmapi.RtmApiException;
import it.bova.rtmapi.ServerException;
import it.bova.rtmapi.TaskList;

import java.io.IOException;
import java.util.List;

import android.content.Context;

public class ListGetter extends DefaultInquirer<Void,List<TaskList>> {

	public ListGetter(String NOKPhrase, Context context) {
		super("",NOKPhrase, context);
	}
	
	@Override public List<TaskList> inquire(Void... empty) throws ServerException, RtmApiException, IOException {
		return ApiSingleton.getApi(this.getContext()).listsGetList();	
	}

}
