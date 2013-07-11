package it.bova.bioniccow.asyncoperations.rtmobjects;

import it.bova.bioniccow.asyncoperations.DefaultInquirer;
import it.bova.bioniccow.asyncoperations.MessageSender;
import it.bova.bioniccow.data.ApiSingleton;
import it.bova.rtmapi.RtmApi;
import it.bova.rtmapi.RtmApiException;
import it.bova.rtmapi.ServerException;
import it.bova.rtmapi.TaskList;

import java.io.IOException;

import android.content.Context;

public class ListAdder extends DefaultInquirer<String,TaskList> {
	
	public ListAdder(String OKPhrase, String NOKPhrase, Context context) {
		super(OKPhrase,NOKPhrase, context );
	}
	
	@Override public TaskList inquire(String... names) throws RtmApiException, ServerException, IOException {
		Context c = this.getContext();
		RtmApi api = ApiSingleton.getApi(c);
		String timeline = ApiSingleton.getTimeline(c);
		if(names.length > 0) return api.listsAdd(timeline,names[0]);
		else return api.listsAdd(timeline,"");
	}
	
	@Override protected void onResultObtained(TaskList result) {
		MessageSender.sendMessage(this.getContext(), getOKPhrase() + result.getName());
	}
}
