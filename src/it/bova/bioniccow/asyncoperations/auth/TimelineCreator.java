package it.bova.bioniccow.asyncoperations.auth;

import it.bova.bioniccow.asyncoperations.Inquirer;
import it.bova.bioniccow.data.ApiSingleton;
import it.bova.rtmapi.RtmApi;
import it.bova.rtmapi.RtmApiException;
import it.bova.rtmapi.ServerException;

import java.io.IOException;

import android.content.Context;

public class TimelineCreator extends Inquirer<Void,String>{
	
	public TimelineCreator(Context context) {
		super(context);
	}

	@Override public String inquire(Void... empty) throws RtmApiException, ServerException, IOException {
		RtmApi api = ApiSingleton.getApi(this.getContext());
		return api.timelinesCreate();
	}
	
	//non faccio nulla se non riesco ad ottenerla! la riprenderò appena possibile
	@Override protected void onResultObtained(String timeline) {
		ApiSingleton.saveTimeline(this.getContext(), timeline);
	}
	
}
