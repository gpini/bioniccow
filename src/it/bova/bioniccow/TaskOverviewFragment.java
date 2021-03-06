package it.bova.bioniccow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.bova.bioniccow.asyncoperations.rtmobjects.DBTaskListsGetter;
import it.bova.bioniccow.asyncoperations.rtmobjects.TaskGetter;
import it.bova.bioniccow.asyncoperations.rtmobjects.DBOverviewTaskGetter;
import it.bova.bioniccow.utilities.SmartClickListener;
import it.bova.bioniccow.utilities.rtmobjects.ParcelableTask;
import it.bova.bioniccow.utilities.rtmobjects.SmartDateComparator;
import it.bova.bioniccow.utilities.rtmobjects.TaskComparator;
import it.bova.bioniccow.utilities.rtmobjects.TaskFormat;
import it.bova.rtmapi.Priority;
import it.bova.rtmapi.Task;
import it.bova.rtmapi.TaskList;
import com.actionbarsherlock.app.SherlockFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TaskOverviewFragment extends SherlockFragment
	implements InterProcess, OnGroupClickListener {

	private ExpandableListView elv;
	private TaskOverviewAdapter adapter;


	private Map<String,TaskList> listMap;
	private List<ParcelableTask> tasks;
	
	private String[] groupList;
	private TaskGetter taskGetter;	

	//phrases
	private String[] dateFormatStrings;
	private String task_NOK;


	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
		      Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.task_overview,
		        container, false);

		//this.loadingBar = (RelativeLayout) view.findViewById(R.id.loadingBar);

		this.dateFormatStrings = this.getResources().getStringArray(R.array.smart_date_format_labels);
		this.task_NOK = this.getResources().getString(R.string.task_NOK);	
		
		this.groupList = this.getResources().getStringArray(R.array.overview_categories);
		this.adapter = new TaskOverviewAdapter(this.getSherlockActivity(), this.groupList, createChildList(null));
		this.elv = (ExpandableListView) view.findViewById(R.id.list);
		this.elv.setAdapter(this.adapter);	
		this.elv.setOnGroupClickListener(this);		
		
		return view;

	}
	
	@Override public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(savedInstanceState != null) {
			ArrayList<ParcelableTask> tasks = savedInstanceState.getParcelableArrayList("tasks");
			if(tasks != null)
				this.onTasksObtained(tasks);
		}
	}

	public void onResume() {		
		super.onResume();
		//if(this.tasks == null)
			this.refresh();
		
	}


	@Override public void onSaveInstanceState(Bundle savedInstanceState) {
		ArrayList<ParcelableTask> tasks = null;
		if(this.tasks != null) {
			tasks = new ArrayList<ParcelableTask>();
			for(Task task : this.tasks)
				tasks.add(new ParcelableTask(task));
		}
		savedInstanceState.putParcelableArrayList("tasks", tasks);
		super.onSaveInstanceState(savedInstanceState);
	}
	
	public void refreshOnTaskChanged(List<String> changedIds) {
		if(this.tasks != null) {
			boolean areTheseTasksAffected = false;
			for(Task task : tasks) {
				int pos = Collections.binarySearch(changedIds, task.getId());
				if(pos >= 0) {
					areTheseTasksAffected = true;
					break;
				}
			}
			if(areTheseTasksAffected)
				this.refresh();
		}
	}
	
	public void refreshOnTaskAdded(List<ParcelableTask> tasks){
		for(Task task : tasks) {
			if(task.getDue() != null) {
				this.refresh();
				break;
			}
		}
	}

	public void refresh() {
		DBTaskListsGetter tlg = new DBTaskListsGetter(TaskOverviewFragment.this.getSherlockActivity()) {
			@Override protected void onPostExecute(List<TaskList> tasklists) {
				TaskOverviewFragment.this.listMap = new HashMap<String,TaskList>();
				for(TaskList list : tasklists)
					TaskOverviewFragment.this.listMap.put(list.getId(), list);
				TaskOverviewFragment.this.retrieveTasks();
			}
		};
		tlg.execute();
	}
	
	
	private void retrieveTasks() {
		this.taskGetter = new DBOverviewTaskGetter(this.task_NOK,this.getSherlockActivity()) {
			@Override public void onResultObtained(List<Task> tasks) {
				List<ParcelableTask> taskList = new ArrayList<ParcelableTask>();
				for(Task task : tasks)
					taskList.add(new ParcelableTask(task));
				TaskOverviewFragment.this.onTasksObtained(taskList);
			}
			//@Override public void onPreInquiry() {
			//	TaskOverviewFragment.this.loadingBar.setVisibility(View.VISIBLE);
			//}
			//@Override public void onPostInquiry() {
			//	TaskOverviewFragment.this.loadingBar.setVisibility(View.GONE);
			//}
		};
		this.taskGetter.executeInBackground();	
	}

	private void onTasksObtained(List<ParcelableTask> tasks2) {
		this.tasks = tasks2;
		this.adapter.reloadList(createChildList(tasks2));
		this.adapter.notifyDataSetChanged();
		this.elv.expandGroup(1); //expand "today"
	}
	
	private List<List<Task>> createChildList(List<ParcelableTask> tasks2) {
		if(tasks2 != null)
			Collections.sort(tasks2, new TaskComparator());
		Date now = new Date();
		List<List<Task>> list = new ArrayList<List<Task>>();
		for(int i = 0; i < groupList.length; i++) {
			ArrayList<Task> childList = new ArrayList<Task>();
			if(tasks2 != null) {
				for(Task task : tasks2) {
					Date due = task.getDue();
					if(due != null) {
						switch(i) {
						case 0 : //overdue
							if(SmartDateComparator.isOverdue(now, due)) {
								//Log.d("overdue", "" + task);	
								childList.add(task);
							}
							break;
						case 1 : //today
							if(SmartDateComparator.isToday(now, due)) {
								//Log.d("today", "" + task);	
								childList.add(task);
							}
							break;
						case 2 : //this Week
							if(SmartDateComparator.isInNextDaysOfThisWeek(now, due)) {
								//Log.d("this week", "" + task);	
								childList.add(task);
							}
							break;
						case 3 : //future
							if(SmartDateComparator.isLaterThanThisWeek(now, due)) {
								//Log.d("future", "" + task);	
								childList.add(task);
							}
							break;
						}
					}
				}
			}
			list.add(i, childList);
		}
		return list;
	}


	private class TaskOverviewAdapter extends BaseExpandableListAdapter {

		private Context context;
		private List<List<Task>> taskList;
		private String[] groupTitles;

		TaskOverviewAdapter(Context context,
				String[] groupTitles,
				List<List<Task>> taskList) {
			this.context = context;
			this.taskList = taskList;
			this.groupTitles = groupTitles;
		}



		public Object getChild(int groupPosition, int childPosition) {
			return taskList.get(groupPosition).get(childPosition);
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public int getChildrenCount(int groupPosition) {
			return taskList.get(groupPosition).size();
		}
		
		private class TaskViewHolder {
			public TextView im;
			public TextView head;
			public TextView time;
			public ImageView repeatIcon;
			public ImageView estimateIcon;
			public LinearLayout taskText;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			Task task = taskList.get(groupPosition).get(childPosition);
			if(convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.overview_child_row, null);  
				final TaskViewHolder taskHolder = new TaskViewHolder();
				taskHolder.im = (TextView) convertView.findViewById(R.id.priority);
				taskHolder.time = (TextView) convertView.findViewById(R.id.taskTime);
				taskHolder.head = (TextView) convertView.findViewById(R.id.taskHeader);
				taskHolder.repeatIcon = (ImageView) convertView.findViewById(R.id.repeatIcon);
				taskHolder.estimateIcon = (ImageView) convertView.findViewById(R.id.estimateIcon);
				taskHolder.taskText = (LinearLayout) convertView.findViewById(R.id.taskText);
				convertView.setTag(taskHolder);
			}
			TaskViewHolder holder = (TaskViewHolder) convertView.getTag();
			TaskFormat tf = new TaskFormat(dateFormatStrings, "", "");
			String header = task.getName();
			if(task.getDue() != null) {
				Date due = task.getDue();
				Date now = new Date();
				if(SmartDateComparator.isToday(now, due))
					header = "<B>" + header + "</B>";
				else if(SmartDateComparator.isOverdue(now, due))
					header = "<B><U>" + header + "</U></B>";
			}
				
			String date = tf.formatDate(task);	
			String listId = task.getListId();
			String list = "-----";
			if(listMap.get(listId) != null)
				list = listMap.get(listId).getName();
			holder.head.setText(Html.fromHtml(header));
			holder.time.setText(list + " - " + date);
			if(task.getEstimateDetail().length > 0)
				holder.estimateIcon.setVisibility(View.VISIBLE);
			else holder.estimateIcon.setVisibility(View.GONE);
			if(task.getRecurrence() != null)
				holder.repeatIcon.setVisibility(View.VISIBLE);
			else holder.repeatIcon.setVisibility(View.GONE);
			String filler = "<br>";
			if(!date.equals(""))
				holder.time.setVisibility(View.VISIBLE);
			else
				holder.time.setVisibility(View.GONE);
			holder.im.setText(Html.fromHtml(filler));
			if(task.getPriority() == Priority.HIGH) 
				holder.im.setBackgroundResource(R.color.priority_high);
			else if(task.getPriority() == Priority.MEDIUM)
				holder.im.setBackgroundResource(R.color.priority_medium);
			else if(task.getPriority() == Priority.LOW) 
				holder.im.setBackgroundResource(R.color.priority_low);
			else
				holder.im.setBackgroundResource(0);
			holder.taskText.setOnClickListener(new SmartClickListener<String>(task.getListId()) {
				@Override public void onClick(View v) {
					String id = this.get();
					((BionicCowActivity) TaskOverviewFragment.this.getSherlockActivity()).openTaskFragment(LIST, id, listMap.get(id).getName(), false);
				}
			});
			
			return convertView;
		}
			

		public Object getGroup(int groupPosition) {
			return groupTitles[groupPosition];
		}

		public int getGroupCount() {
			return groupTitles.length;
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}
		
		class GroupViewHolder {
			public TextView tv;
			public ImageView indicator;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			if(convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.overview_group_row, null);
				GroupViewHolder groupHolder = new GroupViewHolder();
				groupHolder.tv = (TextView) convertView.findViewById(R.id.groupText);
				groupHolder.indicator = (ImageView) convertView.findViewById(R.id.indicator);
				convertView.setTag(groupHolder);
        	}
			GroupViewHolder holder = (GroupViewHolder) convertView.getTag(); 
			int num = taskList.get(groupPosition).size();
			holder.tv.setText(groupTitles[groupPosition] + " (" + num + ")");
			if(num < 1) holder.indicator.setVisibility(View.INVISIBLE);
			else {
				holder.indicator.setVisibility(View.VISIBLE);
				boolean expanded = TaskOverviewFragment.this.elv.isGroupExpanded(groupPosition);
				if(expanded)
					holder.indicator.setImageResource(R.drawable.collapse);
				else 
					holder.indicator.setImageResource(R.drawable.expand);
			}
			return convertView;
		}
		
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}
		
		public boolean hasStableIds() {
			return true;
		}
		
		public void reloadList(List<List<Task>> taskList) {
			this.taskList = taskList;
		}
		
		
	} 

	
	@Override public boolean onGroupClick(ExpandableListView elv, View view,
			int groupPosition, long id) {
		it.bova.bioniccow.TaskOverviewFragment.TaskOverviewAdapter.GroupViewHolder holder =
				(it.bova.bioniccow.TaskOverviewFragment.TaskOverviewAdapter.GroupViewHolder) view.getTag(); 
		if(elv.isGroupExpanded(groupPosition)) {
			elv.collapseGroup(groupPosition);
			holder.indicator.setImageResource(R.drawable.expand);
		}
		else {
			elv.expandGroup(groupPosition);
			holder.indicator.setImageResource(R.drawable.collapse);
		}
		return true;
	}
	
}
