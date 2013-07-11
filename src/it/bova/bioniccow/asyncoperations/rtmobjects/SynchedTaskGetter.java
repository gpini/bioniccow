package it.bova.bioniccow.asyncoperations.rtmobjects;

import it.bova.bioniccow.asyncoperations.DefaultInquirer;
import it.bova.bioniccow.data.ApiSingleton;
import it.bova.rtmapi.RtmApiException;
import it.bova.rtmapi.ServerException;
import it.bova.rtmapi.SynchedTasks;

import java.io.IOException;
import java.util.Date;

import android.content.Context;

public class SynchedTaskGetter extends DefaultInquirer<Date,SynchedTasks> {
	
	private Date lastSyncDate = new Date(0);
	
	protected Date getlastSyncDate() {
		//this returns a Date different from default (unix epoch beginning) after execute methos starts;
		return this.lastSyncDate;
	}

	public SynchedTaskGetter(String NOKPhrase, Context context) {
		super("",NOKPhrase, context);
	}
	
	@Override public SynchedTasks inquire(Date... lastSyncDates) throws ServerException, RtmApiException, IOException {
		if(lastSyncDates.length < 1)
			lastSyncDate = new Date(0);
		else
			lastSyncDate = lastSyncDates[0];	
		return ApiSingleton.getApi(this.getContext()).tasksGetSynchedList(lastSyncDate);
	}


}
