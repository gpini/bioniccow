package it.bova.bioniccow;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import it.bova.bioniccow.asyncoperations.DefaultMessageReceiver;
import it.bova.bioniccow.asyncoperations.MessageReceiver;
import it.bova.bioniccow.asyncoperations.sync.SyncHelper;
import android.os.Bundle;
import android.view.View;


public class StandardActivity extends SherlockFragmentActivity implements InterProcess {
	
	protected SyncHelper syncHelper;
	protected View whereAmIView;
	//protected TextView tv1;
	//protected TextView tv2;
	protected ActionBar ab;
	
	protected MessageReceiver messageReceiver = new DefaultMessageReceiver(this);
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.syncHelper = new SyncHelper(this);
		this.syncHelper.loadButtons();
		
		this.ab = getSupportActionBar();
		this.ab.setDisplayHomeAsUpEnabled(true);
		this.ab.setDisplayShowHomeEnabled(true);
		this.ab.setDisplayShowTitleEnabled(true);	
	}
	
	@Override protected void onStart() {
    	super.onStart();
		this.syncHelper.bindScheduler();
    }
	
	@Override protected void onStop() {
    	super.onStop();
		this.syncHelper.unbindScheduler();
    }
	

}
