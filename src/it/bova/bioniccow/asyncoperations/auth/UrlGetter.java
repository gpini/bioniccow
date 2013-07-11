package it.bova.bioniccow.asyncoperations.auth;

import java.io.IOException;

import it.bova.bioniccow.data.ApiSingleton;
import it.bova.rtmapi.Permission;
import it.bova.rtmapi.RtmApiException;
import it.bova.rtmapi.ServerException;
import com.actionbarsherlock.app.SherlockActivity;

public class UrlGetter extends AuthenticationInquirer<String,String> {

	public UrlGetter(String NOKPhrase, SherlockActivity activity) {
		super(NOKPhrase, activity);
	}
	
	@Override public String inquire(String... strings) throws ServerException, RtmApiException, IOException {
		String frob;
		if(strings.length < 1) frob = "";
		else frob = strings[0];
		String url = ApiSingleton.getAuthenticator().authGetDesktopUrl(Permission.DELETE, frob);
		return url;
	}
}
