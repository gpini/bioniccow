package it.bova.bioniccow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import it.bova.bioniccow.data.observers.LocationObserver;
import it.bova.bioniccow.data.observers.TaskListObserver;
import it.bova.bioniccow.asyncoperations.rtmobjects.TaskAdder;
import it.bova.bioniccow.utilities.rtmobjects.CheckableTask;
import it.bova.bioniccow.utilities.rtmobjects.LocationComparator;
import it.bova.bioniccow.utilities.rtmobjects.SmartAddFormat;
import it.bova.bioniccow.utilities.rtmobjects.TaskFormat;
import it.bova.bioniccow.utilities.rtmobjects.TaskListComparator;
import it.bova.rtmapi.Frequency;
import it.bova.rtmapi.Location;
import it.bova.rtmapi.Priority;
import it.bova.rtmapi.Recurrence;
import it.bova.rtmapi.TaskList;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class TaskAddActivity extends TaskDetailActivity{
	
	private String DEFAULT_LIST;

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = this.getIntent();
		this.taskToBeEdited = (CheckableTask) intent.getParcelableExtra("task");

		//remove undesirable views in Add mode
		this.findViewById(R.id.noteLayout).setVisibility(View.GONE);
		this.findViewById(R.id.actionLayout).setVisibility(View.GONE);	

		//"Where am I" TextViews
		this.ab.setTitle(R.string.add_task);
		
		//load resources
		this.DEFAULT_LIST = this.getResources().getString(R.string.defaultList);

	}

	public void onResume() {
		super.onResume();	

		//se si modificano da ora in poi aggiorno
		this.listObserver = new TaskListObserver() {
			@Override public void onDataChanged(List<TaskList> lists) {
				List<TaskList> tmpLists = new ArrayList<TaskList>();
				for(TaskList list : lists) {
					if(!list.isSmart()) tmpLists.add(list);
				}
				Collections.sort(tmpLists, new TaskListComparator());
				tmpLists.add(0, new TaskList("-", DEFAULT_LIST, false, false, false, 0, false, 0));
				TaskAddActivity.this.tasklistAdapter.reload(tmpLists);
				int selectedListPosition = 0;
				if(TaskAddActivity.this.selectedListId != null) {
					selectedListPosition =
							TaskAddActivity.this.tasklistAdapter.findPositionByRtmObjectId(selectedListId);
				}
				else {
					int type = TaskAddActivity.this.getIntent().getIntExtra(TYPE,0);
					String id = null;
					if(type == LIST)
						id = TaskAddActivity.this.getIntent().getStringExtra(IDENTIFIER);
					if(id != null)
						selectedListPosition =
							TaskAddActivity.this.tasklistAdapter.findPositionByRtmObjectId(id);
				}
				TaskAddActivity.this.tasklistAdapter.notifyDataSetChanged();
				if(selectedListPosition >= 0)
					TaskAddActivity.this.listSpinner.setSelection(selectedListPosition);
			}
		};
		this.tasklists.addObserver(this.listObserver);

		this.locationObserver = new LocationObserver() {
			@Override public void onDataChanged(List<Location> locations) {
				List<Location> tmpLocs = new ArrayList<Location>();
				tmpLocs.addAll(locations);
				Collections.sort(tmpLocs, new LocationComparator());
				tmpLocs.add(0, new Location("", "-", 0, 0, "-", false, 0));
				TaskAddActivity.this.locationAdapter.reload(tmpLocs);
				int selectedLocPosition = 0;
				if(TaskAddActivity.this.selectedLocationId != null)
					selectedLocPosition = 
						TaskAddActivity.this.locationAdapter.findPositionByRtmObjectId(selectedLocationId);
				TaskAddActivity.this.locationAdapter.notifyDataSetChanged();
				if(selectedLocPosition >= 0)
					TaskAddActivity.this.locationSpinner.setSelection(selectedLocPosition);
			}
		};
		this.locations.addObserver(this.locationObserver);

		tasklists.retrieve();
		tasklists.notifyObservers();
		locations.retrieve();
		locations.notifyObservers();

	}


	@Override public void onSaveActionPressed() {
		//save added task
		String OK = this.getResources().getString(R.string.task_added_OK);
		String NOK = this.getResources().getString(R.string.task_added_NOK);
		TaskAdder ta = new TaskAdder(OK, NOK, this);
		ta.executeInBackground(this.toSmartAddString());
		Toast.makeText(this, R.string.adding, Toast.LENGTH_SHORT).show();
		this.finish();
	}

	
    @Override protected void onPrepareDialog(int id, Dialog dialog, Bundle bundle) {
    	switch(id) {
    	case EDIT_RECURRENCE :
    		this.showUpdatedRecurrenceDialog();
    		break;
    	default : break;
    	}
    }

	private String toSmartAddString() {
		StringBuilder sb = new StringBuilder("");
		String name = this.nameInput.getText().toString();
		if(!name.equals("")) {
			sb.append(name);
			//priority
			int pos = this.prioritySpinner.getSelectedItemPosition();
			Priority priority = Priority.NONE;
			if(pos == 1) priority = Priority.LOW;
			if(pos == 2) priority = Priority.MEDIUM;
			if(pos == 3) priority = Priority.HIGH;
			sb.append(SmartAddFormat.formatPriority(priority));
			//list
			TaskList list = (TaskList) this.listSpinner.getSelectedItem();
			if(this.listSpinner.getSelectedItemPosition() != 0)
				sb.append(SmartAddFormat.formatTaskList(list.getName()));
			//location
			Location loc = (Location) this.locationSpinner.getSelectedItem();
			if(!loc.getName().equals("-"))
				sb.append(SmartAddFormat.formatLocation(loc.getName()));
			//date and time
			if(this.day != null && this.month != null && this.year != null)
				sb.append(SmartAddFormat.formatDate(this.day, this.month + 1, this.year));
			if(this.hour != null && this.minute != null)
				sb.append(SmartAddFormat.formatTime(this.hour, this.minute));
			//tags
			String tags = this.tagInput.getText().toString();
			sb.append(SmartAddFormat.formatTags(tags));
			//estimate
			float minutes = 0.0f;
			float hours = 0.0f;
			float days = 0.0f;
			String minuteString = this.estimateMinuteInput.getText().toString();
			if(!minuteString.equals("")) {
				try {
					minutes = Float.parseFloat(minuteString);
				} catch(NumberFormatException e) {
					minutes = 0.0f;
				}
			}
			String hourString = this.estimateHourInput.getText().toString();
			if(!hourString.equals("")) {
				try {
					hours = Float.parseFloat(hourString);
				} catch(NumberFormatException e) {
					hours = 0.0f;
				}
			}
			String dayString = this.estimateDayInput.getText().toString();
			if(!dayString.equals("")) {
				try {
					days = Float.parseFloat(dayString);
				} catch(NumberFormatException e) {
					days = 0.0f;
				}
			}
			sb.append(SmartAddFormat.formatEstimate(minutes, hours, days));
			//repeat
			Boolean isEvery;
			if(this.repeatSpinner1.getSelectedItemPosition() == 1) 
				isEvery = true;
			else if(this.repeatSpinner1.getSelectedItemPosition() == 2) 
				isEvery = false;
			else
				isEvery = null;
			int interval = this.repeatSpinner2.getSelectedItemPosition();
			Frequency freq;
			switch(this.repeatSpinner3.getSelectedItemPosition()) {
			case 1 : freq = Frequency.DAILY; break;
			case 2 : freq = Frequency.WEEKLY; break;
			case 3 : freq = Frequency.MONTHLY; break;
			case 4 : freq = Frequency.YEARLY; break;
			default : freq = null; break;
			}
			if(isEvery != null && interval != 0 && freq != null) {
				Recurrence rec = new Recurrence(isEvery, interval, freq, null, "");
				sb.append(SmartAddFormat.formatRepeat(rec));
				//repeat option
				//no sense to insert if a valid repetition rule is absent
				if(this.newRecurrenceOption != null && this.newRecurrenceString != null) {
					sb.append(" " + TaskFormat.formatRepeatOptionInEnglish(newRecurrenceOption, newRecurrenceString));
				}
			}
			//url
			sb.append(SmartAddFormat.formatUrl(this.urlInput.getText().toString()));
		}
		return sb.toString();
	}


}
