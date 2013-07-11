package it.bova.bioniccow.utilities;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

public class NetAvailabilityTask extends Handler implements Runnable {
	
	private boolean isCancelled = false;
	private long period;
	private Activity activity;
	
	public NetAvailabilityTask(SherlockActivity activity, long period) {
		this.activity = activity;
		this.period = period;
	}
	
	public NetAvailabilityTask(SherlockFragmentActivity activity, long period) {
		this.activity = activity;
		this.period = period;
	}
	
	public void setPeriod(long period) {this.period = period;}
	public long getPeriod() {return this.period;}
	
	@Override public void run() {
		while(!isCancelled) {
			if(this.isConnected()) this.sendEmptyMessage(1);
			else this.sendEmptyMessage(-1);
			try {
				Thread.sleep(this.period);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isConnected() {
		ConnectivityManager cm = 
				(ConnectivityManager) this.activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean connected = false;
		if (cm != null) {
			NetworkInfo[] netInfo = cm.getAllNetworkInfo();
			for (NetworkInfo ni : netInfo) {
				if ((ni.getTypeName().equalsIgnoreCase("WIFI")
						|| ni.getTypeName().equalsIgnoreCase("MOBILE"))
						&& ni.isConnected() && ni.isAvailable())
					connected = true;
			}
		}
		return connected;
	}
	
	public void cancel() {this.isCancelled = true;}
}
