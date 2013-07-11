package it.bova.bioniccow.asyncoperations.auth;

import java.io.IOException;

import it.bova.bioniccow.data.ApiSingleton;
import it.bova.rtmapi.RtmApiException;
import it.bova.rtmapi.ServerException;
import com.actionbarsherlock.app.SherlockActivity;

public class FrobGetter extends AuthenticationInquirer<Void,String> {

	public FrobGetter(String NOKPhrase, SherlockActivity activity) {
		super(NOKPhrase, activity);
	}
	
	@Override public String inquire(Void... empty) throws ServerException, RtmApiException, IOException {
		String frob = ApiSingleton.getAuthenticator().authGetFrob();
		return frob;
	}
}
