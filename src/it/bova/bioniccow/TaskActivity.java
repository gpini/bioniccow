package it.bova.bioniccow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionbarsherlock.app.SherlockActivity;

import it.bova.bioniccow.asyncoperations.DefaultMessageReceiver;
import it.bova.bioniccow.asyncoperations.rtmobjects.DBLocationsGetter;
import it.bova.bioniccow.asyncoperations.rtmobjects.DBNotLocatedTaskGetter;
import it.bova.bioniccow.asyncoperations.rtmobjects.DBNotTaggedTaskGetter;
import it.bova.bioniccow.asyncoperations.rtmobjects.DBPrioritizedTaskGetter;
import it.bova.bioniccow.asyncoperations.rtmobjects.DBRecentTaskGetter;
import it.bova.bioniccow.asyncoperations.rtmobjects.DBTaskGetterByLocation;
import it.bova.bioniccow.asyncoperations.rtmobjects.DBTaskGetterByTag;
import it.bova.bioniccow.asyncoperations.rtmobjects.DBTaskListsGetter;
import it.bova.bioniccow.asyncoperations.rtmobjects.TaskGetter;
import it.bova.bioniccow.asyncoperations.rtmobjects.DBTaskGetterByList;
import it.bova.bioniccow.asyncoperations.rtmobjects.TaskGetterByListId;
import it.bova.bioniccow.asyncoperations.tasks.MultipleTaskChanger;
import it.bova.bioniccow.asyncoperations.tasks.MultipleTaskDeleter;
import it.bova.bioniccow.asyncoperations.tasks.TaskCompleter;
import it.bova.bioniccow.asyncoperations.tasks.TaskDeleter;
import it.bova.bioniccow.asyncoperations.tasks.TaskPostponer;
import it.bova.bioniccow.asyncoperations.tasks.TaskUncompleter;
import it.bova.bioniccow.utilities.ImprovedArrayAdapter;
import it.bova.bioniccow.utilities.SmartClickListener;
import it.bova.bioniccow.utilities.rtmobjects.CheckableTask;
import it.bova.bioniccow.utilities.rtmobjects.ParcelableTask;
import it.bova.bioniccow.utilities.rtmobjects.SmartDateComparator;
import it.bova.bioniccow.utilities.rtmobjects.SmartDateFormat;
import it.bova.bioniccow.utilities.rtmobjects.TaskComparatorByCompletionDate;
import it.bova.bioniccow.utilities.rtmobjects.TaskComparator;
import it.bova.bioniccow.utilities.rtmobjects.TaskFormat;
import it.bova.rtmapi.Location;
import it.bova.rtmapi.Priority;
import it.bova.rtmapi.Task;
import it.bova.rtmapi.TaskList;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TaskActivity extends SyncableActivity {
	
	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		this.messageReceiver = new TaskActivityMessageReceiver(this);
		
		setContentView(R.layout.task);

		this.type = this.getIntent().getIntExtra(TYPE,0);	
		String name = this.getIntent().getStringExtra(NAME);

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
    			TaskActivity.this.retrieveTasks(type);
    		break;
    	case TASK_EDIT :
    		if(resultCode != RESULT_CANCELED)
    			TaskActivity.this.retrieveTasks(type);
    		break;
    	}
    }

	
	private class TaskActivityMessageReceiver extends DefaultMessageReceiver{
		public TaskActivityMessageReceiver(SherlockActivity activity) {
			super(activity);
		}
				
		@Override public void onTaskChanged(Context context, List<String> changedIds) {
			boolean areTheseTasksAffected = false;
			if(type == LIST) {
				boolean isSmart = TaskActivity.this.getIntent().getBooleanExtra("isSmart", true);
				if(isSmart) areTheseTasksAffected = true;
			}
			
			if(!areTheseTasksAffected && 
					TaskActivity.this.completedTasks != null &&
					TaskActivity.this.uncompletedTasks != null) {
				
				for(Task task : completedTasks) {
					int pos = Collections.binarySearch(changedIds, task.getId());
					if(pos >= 0) {
						areTheseTasksAffected = true;
						break;
					}
				}
				if(!areTheseTasksAffected) {
					for(Task task : uncompletedTasks) {
						int pos = Collections.binarySearch(changedIds, task.getId());
						if(pos >= 0) {
							areTheseTasksAffected = true;
							break;
						}
					}
				}
			}
			
			//Log.d("affected", "" + areTheseTasksAffected);
			if(areTheseTasksAffected) TaskActivity.this.retrieveTasks(type);
		}
		
		@Override public void onTaskAdded(Context context, List<ParcelableTask> tasks){
			boolean areTheseTasksAffected = false;
			String idOrName = TaskActivity.this.getIntent().getStringExtra(IDENTIFIER);
			switch(type) {
			case(LIST) : {
				boolean isSmart = TaskActivity.this.getIntent().getBooleanExtra("isSmart", true);
				if(isSmart) areTheseTasksAffected = true;
				else {
					for(Task task : tasks)
						if(task.getListId().equals(idOrName))
							areTheseTasksAffected = true;
				}
			}
			break;
			case(LOCATION) :
				for(Task task : tasks)
					if(task.getLocationId().equals(idOrName))
						areTheseTasksAffected = true;
			break;
			case(TAG) : {
				for(Task task : tasks) {
					String[] tags = task.getTags();
					for(String tag : tags) {
						if(tag.equals(idOrName)) {
							areTheseTasksAffected = true;
							break;
						}
					}
				}
			}
			break;
			case(NO_TAG) :
				for(Task task : tasks)
					if(task.getTags().length == 0)
						areTheseTasksAffected = true;
			break;
			case(NO_LOCATION) :
				for(Task task : tasks)
					if(!task.getLocationId().equals(null) && !task.getLocationId().equals(""))
						areTheseTasksAffected = true;
			break;
			case(RECENTLY_COMPLETED) :
				for(Task task : tasks)
					if(task.getCompleted() != null)
						areTheseTasksAffected = true;
			break;
			case(WITH_PRIORITY) :
				for(Task task : tasks)
					if(task.getPriority() != Priority.NONE)
						areTheseTasksAffected = true;
			break;
			}
			if(areTheseTasksAffected) TaskActivity.this.retrieveTasks(type);
		}
	}
	

	

}
