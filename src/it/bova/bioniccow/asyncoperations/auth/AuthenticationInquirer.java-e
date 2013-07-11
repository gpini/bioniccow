package it.bova.bioniccow.asyncoperations.auth;

import it.bova.bioniccow.InterProcess;
import it.bova.bioniccow.asyncoperations.DefaultInquirer;
import com.actionbarsherlock.app.SherlockActivity;
import android.content.Intent;

public abstract class AuthenticationInquirer<Param,Result> extends DefaultInquirer<Param,Result> implements InterProcess {
	
	private static final int RESULT_CANCELED = 0;
	private SherlockActivity activity;

	public AuthenticationInquirer(String NOKPhrase, SherlockActivity activity) {
		super("",NOKPhrase, activity);
		this.activity = activity;
	}

	protected void onMissingInternet() {
		cancelActivity(getNOKPhrase() + "missing internet connection");
	}

	protected void onLoginIssue() {
		cancelActivity(getNOKPhrase() + "invalid token");
	}

	protected void onServerError(int code, String msg) {
		cancelActivity(getNOKPhrase() + "(" +
			code + ") " + msg);
	}

	protected void onApiError(String msg) {
		cancelActivity(getNOKPhrase() + msg);
	}

	protected void onGenericException(String msg) {
		cancelActivity(getNOKPhrase() + msg);
	}

	private void cancelActivity(String msg) {
		Intent info = new Intent();
		info.putExtra("msg", msg);
		activity.setResult(RESULT_CANCELED,info);
		activity.finish();
	}

}
