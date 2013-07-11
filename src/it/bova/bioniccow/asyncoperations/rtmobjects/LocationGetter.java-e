package it.bova.bioniccow.asyncoperations.rtmobjects;

import it.bova.bioniccow.asyncoperations.DefaultInquirer;
import it.bova.bioniccow.data.ApiSingleton;
import it.bova.rtmapi.RtmApiException;
import it.bova.rtmapi.ServerException;
import it.bova.rtmapi.Location;

import java.io.IOException;
import java.util.List;

import android.content.Context;

public class LocationGetter extends DefaultInquirer<Void,List<Location>> {

	public LocationGetter(String NOKPhrase, Context context) {
		super("",NOKPhrase, context);
	}
	
	@Override public List<Location> inquire(Void... empty) throws ServerException, RtmApiException, IOException {
		return ApiSingleton.getApi(this.getContext()).locationsGetList();	
	}

}
