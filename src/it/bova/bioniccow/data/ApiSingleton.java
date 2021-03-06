package it.bova.bioniccow.data;

import android.content.Context;
import it.bova.bioniccow.data.Preferences.PrefParameter;
import it.bova.rtmapi.RtmApi;
import it.bova.rtmapi.RtmApiAuthenticator;

public class ApiSingleton {
	private static final String API_KEY = "REPLACE_ME";
	private static final String SHARED_SECRET = "REPLACE_ME";
	private static String token = "";
	private static String timeline = "";
	private static RtmApi api;
	private static RtmApiAuthenticator authenticator;
   
	public static synchronized RtmApi getApi(Context context) {
		if(token == null || token.equals("")) {
			token = new Preferences(context).getString(PrefParameter.TOKEN,"");
			api = new RtmApi(API_KEY, SHARED_SECRET, token);
		}
		if(api == null)
			api = new RtmApi(API_KEY, SHARED_SECRET, token);
		return api;
	}
   
    public static synchronized RtmApiAuthenticator getAuthenticator() {
		if(authenticator == null)
			authenticator = new RtmApiAuthenticator(API_KEY, SHARED_SECRET);
		return authenticator;
    }
   
   public static synchronized void saveToken(Context context, String tk) {
      synchronized(token) {
		if(tk == null) tk = "";
		new Preferences(context).putString(PrefParameter.TOKEN,tk);
		token = tk;
		//con la riga seguente aggiorno tutti i RIFERIMENTI gi� acquisiti
		api = new RtmApi(API_KEY, SHARED_SECRET, tk);
	  }
   }
   
   public static void saveTimeline(Context context, String tl) {
	  synchronized(timeline) {
		if(tl == null) tl = "";
		new Preferences(context).putString(PrefParameter.TIMELINE,tl);
		timeline = tl;
	  }
   }
   
   public static String getToken(Context context) {
      synchronized(token) {
		if(token == null || token.equals("")) {
			token = new Preferences(context).getString(PrefParameter.TOKEN,"");
			//se avevo memorizzato "" pace, verr� fuori un LOGIN_ISSUE
			//ad ogni modo lo controllo prima di salvare il token
		 
			//con la riga seguente aggiorno tutti i RIFERIMENTI gi� acquisiti
			api = new RtmApi(API_KEY, SHARED_SECRET, token);
		}
		return token;
	  }
   }
   
   public static String getTimeline(Context context) {
      synchronized(timeline) {
		if(timeline == null | timeline.equals(""))
			timeline = new Preferences(context).getString(PrefParameter.TIMELINE,"");
			//se avevo memorizzato "" pace, verr� fuori un errore
			//ad ogni modo lo controllo prima di salvare la timeline
		return timeline;
	  }
   }
   
}

