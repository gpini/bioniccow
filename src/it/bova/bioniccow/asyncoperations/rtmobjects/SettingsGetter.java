package it.bova.bioniccow.asyncoperations.rtmobjects;

import it.bova.bioniccow.asyncoperations.DefaultInquirer;
import it.bova.bioniccow.data.ApiSingleton;
import it.bova.rtmapi.RtmApiException;
import it.bova.rtmapi.ServerException;
import it.bova.rtmapi.Settings;
import java.io.IOException;
import android.content.Context;

public class SettingsGetter extends DefaultInquirer<Void,Settings> {

	public SettingsGetter(String NOKPhrase, Context context) {
		super("",NOKPhrase, context);
	}
	
	@Override public Settings inquire(Void... empty) throws ServerException, RtmApiException, IOException {
		return ApiSingleton.getApi(this.getContext()).settingsGetList() ;	
	}
	
}
