package it.bova.bioniccow;

import com.actionbarsherlock.app.SherlockActivity;

import it.bova.bioniccow.asyncoperations.ErrorCoded;
import it.bova.bioniccow.asyncoperations.auth.FrobGetter;
import it.bova.bioniccow.asyncoperations.auth.TimelineCreator;
import it.bova.bioniccow.asyncoperations.auth.TokenGetter;
import it.bova.bioniccow.asyncoperations.auth.UrlGetter;
import it.bova.bioniccow.data.ApiSingleton;
import it.bova.bioniccow.data.Preferences;
import it.bova.bioniccow.data.Preferences.PrefParameter;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.graphics.Bitmap;

public class AuthenticationActivity extends SherlockActivity implements ErrorCoded{
	
	private WebView wv;
	private ProgressBar progressBar;
	private String frob = null;
	private String url = null;
	private String token = null;
	
	//phrases
	private String NOK;
	private String CANCEL;
	private String STARTING_AUTH;
	private String FINISHING_AUTH;
	private String AUTH_ABORTED;
	
	private static final int DIALOG_AUTH_START = 1;
	private static final int DIALOG_AUTH_STOP = 2;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.auth);
		
		this.progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
		this.progressBar.setMax(100);
		this.progressBar.setProgressDrawable(this.getResources().getDrawable(R.drawable.progress_horizontal));
		this.getSupportActionBar().hide();
		
		//resources initialization
		NOK = this.getResources().getString(R.string.authentication_NOK);
		CANCEL = this.getResources().getString(R.string.cancel);
		STARTING_AUTH = this.getResources().getString(R.string.starting_authentication);
		FINISHING_AUTH = this.getResources().getString(R.string.finishing_authentication);
		AUTH_ABORTED = this.getResources().getString(R.string.authentication_aborted);
		
		wv = (WebView) this.findViewById(R.id.web_view);
		wv.getSettings().setJavaScriptEnabled(true);
		
		
		//Add progress bar
		wv.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                AuthenticationActivity.this.progressBar.setProgress(progress);
            }
        });
  
		
		/* Prevent WebView from Opening the Browser */
        wv.setWebViewClient(new WebViewClient() {
			@Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
			
			@Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
				if(AuthenticationActivity.this.progressBar.getVisibility() != View.VISIBLE) {
					AuthenticationActivity.this.progressBar.setVisibility(View.VISIBLE);
				}
			}
			@Override public void onPageFinished(WebView view, String url) {
				AuthenticationActivity.this.progressBar.setVisibility(View.GONE);
			}
		});
	}
	
	public void onResume() {
		super.onResume();
		if(frob == null) {
			FrobGetter fg = new FrobGetter(NOK,this) {
				@Override public void onPreInquiry() {
					AuthenticationActivity.this.showDialog(DIALOG_AUTH_START);
				}
				@Override public void onResultObtained(String frob) {
					AuthenticationActivity.this.frob = frob;	
					AuthenticationActivity.this.dismissDialogIfPresent(DIALOG_AUTH_START);
					AuthenticationActivity.this.getUrlAndLoadPage();
				}		
				
			};
			fg.executeInBackground();
		}
		else if(frob != null && url == null) {
			this.dismissDialogIfPresent(DIALOG_AUTH_START);
			this.getUrlAndLoadPage();
		}
		else {
			this.dismissDialogIfPresent(DIALOG_AUTH_START);
			this.loadPage();
		}

	}
	
    protected Dialog onCreateDialog(int id) {
        switch(id) {
        case DIALOG_AUTH_START :
        	return cancelableProgressDialog(STARTING_AUTH);
        case DIALOG_AUTH_STOP :
        	return cancelableProgressDialog(FINISHING_AUTH);
        default:
        	return null;
        }
    }
    
	
	@Override public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
    
    private void dismissDialogIfPresent(int id) {
    	try {
			AuthenticationActivity.this.dismissDialog(id);
		} catch(IllegalArgumentException e) {
			Log.d("rotation auth error", e.getMessage());
		}
    }

	public void done(View v) {
		this.getTokenAndFinish();
	}
	
	@Override public void onBackPressed() {
		this.getTokenAndFinish();
	}
	
	private void getUrlAndLoadPage() {
		UrlGetter ug = new UrlGetter(NOK,AuthenticationActivity.this) {
			@Override public void onResultObtained(String url) {
				AuthenticationActivity.this.url = url;
				AuthenticationActivity.this.loadPage();
			}
			@Override public void onPostInquiry() {
				AuthenticationActivity.this.dismissDialogIfPresent(DIALOG_AUTH_START);
			}
		};
		ug.executeInBackground(frob);
	}
	
	private void loadPage() {
		wv.loadUrl(url);
	}
	
	private void getTimelineAndFinish() {
		new Preferences(this).putBoolean(PrefParameter.FIRST_TOKEN_SET_DONE, true);
		new TimelineCreator(AuthenticationActivity.this).executeInBackground();
		this.setResult(RESULT_OK);
		this.finish();
	}
	
	public void getTokenAndFinish() {
		if(token == null) {
			TokenGetter tg = new TokenGetter(NOK,this) {
				@Override public void onPreInquiry() {
					AuthenticationActivity.this.showDialog(DIALOG_AUTH_STOP);
				}
				@Override public void onResultObtained(String token) {
					ApiSingleton.saveToken(AuthenticationActivity.this, token);
					new Preferences(AuthenticationActivity.this).putBoolean(PrefParameter.FIRST_TOKEN_SET_DONE, true);
					AuthenticationActivity.this.token = token;
					AuthenticationActivity.this.getTimelineAndFinish();
				}
				@Override public void onPostInquiry() {
					AuthenticationActivity.this.dismissDialogIfPresent(DIALOG_AUTH_STOP);
				}
			};
			tg.executeInBackground(frob);
		}
		else {
			AuthenticationActivity.this.dismissDialogIfPresent(DIALOG_AUTH_STOP);
			AuthenticationActivity.this.getTimelineAndFinish();
		}
	}
	
	private ProgressDialog cancelableProgressDialog(String text) {
		ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage(text);
		pd.setIndeterminate(true);
		pd.setCancelable(false);
		pd.setButton(ProgressDialog.BUTTON_POSITIVE, CANCEL , new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface arg0, int arg1) {
				Toast.makeText(AuthenticationActivity.this,
						AUTH_ABORTED, Toast.LENGTH_SHORT).show();
				AuthenticationActivity.this.finish();
			}
		});
		return pd;
	}


}
