package it.bova.bioniccow.asyncoperations.auth;

import it.bova.bioniccow.data.ApiSingleton;
import it.bova.rtmapi.RtmApiException;
import it.bova.rtmapi.ServerException;
import it.bova.rtmapi.Token;

import java.io.IOException;

import com.actionbarsherlock.app.SherlockActivity;

public class TokenGetter extends AuthenticationInquirer<String,String> {

	public TokenGetter(String NOKPhrase, SherlockActivity activity) {
		super(NOKPhrase, activity);
	}
	
	@Override public String inquire(String... strings) throws ServerException, RtmApiException, IOException {
		String frob;
		if(strings.length < 1) frob = "";
		else frob = strings[0];
		Token token = ApiSingleton.getAuthenticator().authGetToken(frob);
		return token.getToken();
	}
}
