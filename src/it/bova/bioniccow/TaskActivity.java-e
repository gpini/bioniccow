package it.bova.bioniccow;

import java.util.List;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import it.bova.bioniccow.asyncoperations.DefaultMessageReceiver;
import it.bova.bioniccow.utilities.rtmobjects.ParcelableTask;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class TaskActivity extends SyncableActivity {
	
	private int type;

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		this.messageReceiver = new TaskActivityMessageReceiver(this);
		
		setContentView(R.layout.task);
		this.type = this.getIntent().getIntExtra(TYPE,0);
		String identifier = this.getIntent().getStringExtra(IDENTIFIER);;
		String name = this.getIntent().getStringExtra(NAME);
		boolean isSmart = false;
		if(this.type == LIST)
			isSmart = this.getIntent().getBooleanExtra("isSmart",true);
		TaskFragment taskFragment = new TaskFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(TYPE, type);
		bundle.putBoolean("isSmart", isSmart);
		bundle.putString(IDENTIFIER, identifier);
		bundle.putString(NAME, name);
		taskFragment.setArguments(bundle);
		FragmentManager fm = this.getSupportFragmentManager();
		fm.beginTransaction()
			.add(R.id.taskContainer, taskFragment, TASK_FRAGMENT)
			.commit();

		//"Where am I" TextViews
		this.ab.setTitle(name);	
		switch(type) {
		case LIST : 
			this.ab.setSubtitle(R.string.list); break;
		case LOCATION : 
			this.ab.setSubtitle(R.string.location); break;
		case TAG :
			this.ab.setSubtitle(R.string.tag); break;
		case NO_TAG :
			String no_tag = this.getResources().getStringArray(R.array.specials)[0];
			this.ab.setTitle(no_tag);
			this.ab.setSubtitle(R.string.specials); break;
		case NO_LOCATION :
			String no_loc = this.getResources().getStringArray(R.array.specials)[1];
			this.ab.setTitle(no_loc);
			this.ab.setSubtitle(R.string.specials);break;
		case RECENTLY_COMPLETED :
			String rec_compl = this.getResources().getStringArray(R.array.specials)[2];
			this.ab.setTitle(rec_compl);
			this.ab.setSubtitle(R.string.specials);break;
		case WITH_PRIORITY :
			String with_prio = this.getResources().getStringArray(R.array.specials)[3];
			this.ab.setTitle(with_prio);
			this.ab.setSubtitle(R.string.specials);break;
		}

	}	
	
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch (requestCode) {
    	case AUTHENTICATE :
    		if(resultCode != RESULT_CANCELED)
    			TaskActivity.this.reloadTasks();
    		break;
    	case TASK_EDIT :
    		if(resultCode != RESULT_CANCELED)
    			TaskActivity.this.reloadTasks();
    		break;
    	}
    }
    
    @Override public void onAddActionPressed() {
		Intent intent = new Intent(this, TaskAddActivity.class);
		int type = this.getIntent().getIntExtra(TYPE, 0);
		intent.putExtra(TYPE, type);
		String id = this.getIntent().getStringExtra(IDENTIFIER);
		intent.putExtra(IDENTIFIER, id);
		this.startActivity(intent);
	}
    
    private void reloadTasks() {
    	FragmentManager fm = this.getSupportFragmentManager();
    	Fragment fragment = fm.findFragmentByTag(TASK_FRAGMENT);
		((TaskFragment) fragment).refresh();
    }

	
	private class TaskActivityMessageReceiver extends DefaultMessageReceiver{
		public TaskActivityMessageReceiver(SherlockFragmentActivity activity) {
			super(activity);
		}
				
		@Override public void onTaskChanged(Context context, List<String> changedIds) {
			boolean isSmart = false;
			if(type == LIST)
				isSmart = TaskActivity.this.getIntent().getBooleanExtra("isSmart", true);
			FragmentManager fm = TaskActivity.this.getSupportFragmentManager();
			Fragment fragment = fm.findFragmentByTag(TASK_FRAGMENT);
			boolean areTheseTasksAffected = ((TaskFragment) fragment).checkChangedTasks(type, isSmart, changedIds);

			if(areTheseTasksAffected) {
				TaskActivity.this.reloadTasks();
			}
		}
		
		@Override public void onTaskAdded(Context context, List<ParcelableTask> tasks){
			String idOrName = TaskActivity.this.getIntent().getStringExtra(IDENTIFIER);
			boolean isSmart = false;
			if(type == LIST)
				isSmart = TaskActivity.this.getIntent().getBooleanExtra("isSmart", true);
			FragmentManager fm = TaskActivity.this.getSupportFragmentManager();
			Fragment fragment = fm.findFragmentByTag(TASK_FRAGMENT);
			boolean areTheseTasksAffected = ((TaskFragment) fragment).checkAddedTasks(type, idOrName, isSmart, tasks);
			if(areTheseTasksAffected) {
				TaskActivity.this.reloadTasks();
			}
		}
	}
	

	

}
