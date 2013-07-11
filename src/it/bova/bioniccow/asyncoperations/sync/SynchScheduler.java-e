package it.bova.bioniccow.asyncoperations.sync;

import it.bova.bioniccow.asyncoperations.ErrorCoded;
import it.bova.bioniccow.data.Preferences;
import it.bova.bioniccow.data.Preferences.PrefParameter;
import java.util.Date;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class SynchScheduler extends Service implements ErrorCoded{
	
	private SchedulerThread t;
	private Synchronizer synchronizer;
	//private Long lastSynch = null;
	private static final long START_DELAY = 5*1000; //milliseconds (5 seconds)
	private static final long REPEAT_TIME = 30*60*1000; //milliseconds (30 minutes)
	
	@Override public void onCreate() {
		super.onCreate();
		//Log.d("sched", "created");
		synchronizer = new Synchronizer(SynchScheduler.this);
		t = new SchedulerThread(new SchedulerHandler());
		t.start();
	}
	
	@Override public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override public int onStartCommand(Intent intent, int flags, int startId) {
		//Log.d("sched", "start command");
        return super.onStartCommand(intent, flags, startId);
    }

	@Override public void onDestroy() {
		super.onDestroy();
		//Log.d("sched", "destroyed");
		if(t != null && t.isAlive())
			t.cancel();
	}
	
	private class SchedulerHandler extends Handler {
		@Override public void handleMessage(Message msg) {
			synchronizer.sync(false, true, true, true);
		}
	}
	
	private class SchedulerThread extends Thread {
		
		private Handler handler;
		
		SchedulerThread(Handler handler) {
			this.handler = handler;
		}
		
		private boolean isCancelled = false;
		@Override public void run() {
			try {
				Thread.sleep(START_DELAY);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			Preferences pref = new Preferences(SynchScheduler.this);		
			while(!isCancelled) {
				try {
					long lastSynch = pref.getLong(PrefParameter.LAST_SYNCH, 0L);
					long now = new Date().getTime();
					long elapsed = now - lastSynch;
					//Log.d("Scheduler", "schedule");
					if(elapsed > REPEAT_TIME) {
						//Log.d("paolo","new scheduled synch");
						handler.sendEmptyMessage(1);	
						Thread.sleep(REPEAT_TIME);
					}
					else {
						//Log.d("paolo","synch already done");
						if(elapsed > 0 && elapsed <= REPEAT_TIME)
						Thread.sleep(REPEAT_TIME - elapsed);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		public void cancel() {this.isCancelled = true;}
	}


	

}
