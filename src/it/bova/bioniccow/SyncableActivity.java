package it.bova.bioniccow;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;


public class SyncableActivity extends StandardActivity {
	
	@Override public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
	}

	@Override public void onResume() {
		super.onResume();
		this.registerReceiver(messageReceiver, new IntentFilter(ERROR_MESSENGER));
		this.syncHelper.attachToUI();	

	}
	
	@Override public void onPause() {
		super.onPause();	
		this.unregisterReceiver(messageReceiver);
		this.syncHelper.detachFromUI();
	
	}
	
	@Override public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mMenuInflater = getSupportMenuInflater();
		mMenuInflater.inflate(R.menu.standard_action_bar, menu);
		if(this.syncHelper != null) {
			this.syncHelper.setMenu(menu);
			this.syncHelper.checkButtons();
		}
		return true;
	}
	
//	@Override public void onWindowFocusChanged(boolean hasFocus){
//		if(hasFocus)
//			this.syncHelper.loadButtons();
//	}
	
    
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case android.R.id.home:
        		this.onUpActionPressed();
        		return true;
            case R.id.add:
            	this.onAddActionPressed();
            	return true;
            case R.id.sync:
            	this.onSyncActionPressed();
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
	protected void onAddActionPressed() {
		Intent intent = new Intent(this, TaskAddActivity.class);
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	this.startActivity(intent);
	}

	protected void onSyncActionPressed() {
		if(this.syncHelper != null)
    		this.syncHelper.synchAll();
	}
	
	protected void onUpActionPressed() {
		this.finish();
	}
	

}
