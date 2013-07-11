package it.bova.bioniccow.asyncoperations;

import it.bova.bioniccow.AuthenticationActivity;
import it.bova.bioniccow.InterProcess;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DefaultMessageReceiver extends MessageReceiver implements InterProcess{
	
	private Activity activity;
	
	public DefaultMessageReceiver(SherlockActivity activity) {
		this.activity = activity;
	}
	
	public DefaultMessageReceiver(SherlockFragmentActivity activity) {
		this.activity = activity;
	}
	
	@Override protected void onAuthError(Context context, String error) {
		this.activity.startActivityForResult(new Intent(activity, AuthenticationActivity.class),AUTHENTICATE);
	}
	@Override protected void onError(Context context, String error) {
		Toast.makeText(context, error, Toast.LENGTH_LONG).show();
	}
}
