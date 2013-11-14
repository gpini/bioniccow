package it.bova.bioniccow.asyncoperations.sync;

import it.bova.bioniccow.R;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.actionbarsherlock.view.Menu;

public class SyncHelper{

	private Activity activity;

	private Menu menu;

	private Synchronizer synchronizer;
	private SyncObserver syncObserver;

	private ServiceConnection conn;
	
	private SyncHelper() {
		this.conn = new ServiceConnection() {
			@Override public void onServiceConnected(ComponentName arg0, IBinder arg1) {
				// do nothing
			}
			@Override public void onServiceDisconnected(ComponentName arg0) {
				// do nothing
			}
		};
		this.syncObserver = new SyncObserver() {
			@Override public void onStartSynching() {
				SyncHelper.this.onStartSynching();
			}
			@Override public void onStopSynching() {
				SyncHelper.this.onStopSynching();
			}
		};
	}

	public SyncHelper(SherlockActivity activity) { //in onCreate
		this();
		this.activity = activity;
		this.synchronizer = new Synchronizer(activity);
		//this.ad = (AnimationDrawable) this.activity.getResources().getDrawable(R.drawable.synching);
	}

	public SyncHelper(SherlockFragmentActivity activity) { //in onCreate
		this();
		this.activity = activity;
		this.synchronizer = new Synchronizer(activity);
		//this.ad = (AnimationDrawable) this.activity.getResources().getDrawable(R.drawable.synching);
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	public void loadButtons() {
		//do nothing
	}


	public void attachToUI() {	
		synchronizer.addObserver(syncObserver);
		this.checkButtons();
	}

	public void attach() {	
		synchronizer.addObserver(syncObserver);
	}

	public void bindScheduler() { //in onCreate
		Intent intent = new Intent(this.activity, SynchScheduler.class);
		activity.bindService(intent, this.conn, Context.BIND_AUTO_CREATE);
	}

	public void unbindScheduler() { //in onDestroy
		activity.unbindService(this.conn);
	}

	public void detachFromUI() {
		this.synchronizer.removeObserver(syncObserver);
	}

	public void detach() {
		this.synchronizer.removeObserver(syncObserver);
	}

	public void synchAll() {
		this.synchronizer.sync(true, true, true, true);
	}

	protected void onStartSynching() {
		this.toProgressBar();
	}

	protected void onStopSynching() {
		this.toButton();
	}

	private void toProgressBar() {
		if(this.menu != null)
			if(this.menu.findItem(R.id.sync) != null) {
				this.menu.findItem(R.id.sync).setActionView(R.layout.progress);
				//				this.menu.findItem(R.id.sync).setIcon(this.ad);    
				//				this.ad.start();

			}
	}

	public void checkButtons() {
		if(Synchronizer.isSynching())
			this.toProgressBar();
		else 
			this.toButton();
	}

	private void toButton() {
		if(this.menu != null)
			if(this.menu.findItem(R.id.sync) != null)
				this.menu.findItem(R.id.sync).setActionView(null);
		//this.menu.findItem(R.id.sync).setIcon(R.drawable.synch);
	}
}
