package it.bova.bioniccow;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.content.IntentFilter;


public class EditActivity extends StandardActivity {
	
	@Override public void onResume() {
		super.onResume();
		this.registerReceiver(messageReceiver, new IntentFilter(ERROR_MESSENGER));	

	}
	
	@Override public void onPause() {
		super.onPause();	
		this.unregisterReceiver(messageReceiver);
	
	}
	
	@Override public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mMenuInflater = getSupportMenuInflater();
		mMenuInflater.inflate(R.menu.edit_action_bar, menu);
		if(this.syncHelper != null) {
			this.syncHelper.setMenu(menu);
			this.syncHelper.checkButtons();
		}
		return true;
	}
	
    
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case android.R.id.home:
        		this.onUpActionPressed();
        		return true;
            case R.id.save:
            	this.onSaveActionPressed();
            	return true;
            case R.id.cancel:
            	this.onCancelActionPressed();
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	protected void onCancelActionPressed() {
		this.finish();
	}

	protected void onSaveActionPressed() {}
	
	protected void onUpActionPressed() {
		this.finish();
	}
	

}
