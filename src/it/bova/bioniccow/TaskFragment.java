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
import com.actionbarsherlock.app.SherlockFragment;

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
import android.support.v4.app.DialogFragment;
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

public class TaskFragment extends SherlockFragment implements InterProcess {
	
	private RelativeLayout loadingBar;
	private ListView lv;
	private View footer;
	private Button footerButton;
	private View header;
	private RelativeLayout actionLayout;
	private boolean areCompletedShown = false;
	private Button completeButton;
	private TaskAdapter adapter;

	private Map<String,TaskList> listMap;
	private Map<String,Location> locMap;
	private ArrayList<CheckableTask> uncompletedTasks;
	private ArrayList<CheckableTask> completedTasks;
	private HashMap<String,CheckableTask> selectedTasks;
	
	private TaskGetter taskGetter;
	private int type;	
	private String identifier;
	private boolean isSmart;
	private String name;
	
	//phrases
	private String[] dateFormatStrings;
	private String task_NOK;
	private String SHOW_COMPLETED;
	private String HIDE_COMPLETED;
	private String BLUE;
	private String ORANGE;
	private String LIGHT_GREY;
	private String COMPLETE;
	private String UNCOMPLETE;
	private static String CONFIRM_DELETE;
	private static String YES;
	private static String NO;
	private static String OK1_delete;
	private static String OK2_delete;
	private static String NOK1_delete;
	private static String NOK2_delete;
	private String OK1_complete;
	private String OK2_complete;
	private String NOK1_complete;
	private String NOK2_complete;
	private String OK1_uncomplete;
	private String OK2_uncomplete;
	private String NOK1_uncomplete;
	private String NOK2_uncomplete;
	private String OK1_postpone;
	private String OK2_postpone;
	private String NOK1_postpone;
	private String NOK2_postpone;

	
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
		      Bundle savedInstanceState) {	  
		
		dateFormatStrings = this.getResources().getStringArray(R.array.smart_date_format_labels);
		task_NOK = this.getResources().getString(R.string.task_NOK);
		SHOW_COMPLETED = this.getResources().getString(R.string.show_completed);
		HIDE_COMPLETED = this.getResources().getString(R.string.hide_completed);
		BLUE = this.getResources().getString(R.color.blue);
		BLUE =  "#" + BLUE.substring(3, 9);
		ORANGE = this.getResources().getString(R.color.dark_orange);
		ORANGE =  "#" + ORANGE.substring(3, 9);	
		LIGHT_GREY = this.getResources().getString(R.color.light_grey);
		LIGHT_GREY =  "#" + LIGHT_GREY.substring(3, 9);		
		COMPLETE = this.getResources().getString(R.string.complete);
		UNCOMPLETE = this.getResources().getString(R.string.uncomplete);
		CONFIRM_DELETE = this.getResources().getString(R.string.confirmDelete);
		YES = this.getResources().getString(R.string.yes);
		NO = this.getResources().getString(R.string.no);
		OK1_delete = TaskFragment.this.getResources().getString(R.string.task_deleted_OK1);
		OK2_delete = TaskFragment.this.getResources().getString(R.string.task_deleted_OK2);
		NOK1_delete = TaskFragment.this.getResources().getString(R.string.task_deleted_NOK1);
		NOK2_delete = TaskFragment.this.getResources().getString(R.string.task_deleted_NOK2);
		OK1_complete = this.getResources().getString(R.string.task_completed_OK1);
		OK2_complete = this.getResources().getString(R.string.task_completed_OK2);
		NOK1_complete = this.getResources().getString(R.string.task_completed_NOK1);
		NOK2_complete = this.getResources().getString(R.string.task_completed_NOK2);
		OK1_uncomplete = this.getResources().getString(R.string.task_uncompleted_OK1);
		OK2_uncomplete = this.getResources().getString(R.string.task_uncompleted_OK2);
		NOK1_uncomplete = this.getResources().getString(R.string.task_uncompleted_NOK1);
		NOK2_uncomplete = this.getResources().getString(R.string.task_uncompleted_NOK2);
		OK1_postpone = this.getResources().getString(R.string.task_postponed_OK1);
		OK2_postpone = this.getResources().getString(R.string.task_postponed_OK2);
		NOK1_postpone = this.getResources().getString(R.string.task_postponed_NOK1);
		NOK2_postpone = this.getResources().getString(R.string.task_postponed_NOK2);
		
		this.selectedTasks = new HashMap<String,CheckableTask>();
		
		this.type = this.getArguments().getInt(TYPE,0);
		this.identifier = this.getArguments().getString(IDENTIFIER,0);
		this.name = this.getArguments().getString(NAME,0);
		this.isSmart = this.getArguments().getBool("isSmart",false);
		
		View view = inflater.inflate(R.layout.task_list,
		        container, false);
		this.loadingBar = (RelativeLayout) view.findViewById(R.id.loadingBar);
		this.actionLayout = (RelativeLayout) view.findViewById(R.id.actionLayout);
		this.completeButton = (Button) view.findViewById(R.id.completeButton);
		
		this.adapter = new TaskAdapter(this.getSherlockActivity(), new ArrayList<CheckableTask>());
		this.footer = inflater.inflate(R.layout.task_footer, null);
		this.footerButton = (Button) this.footer.findViewById(R.id.footerText);
		this.header = view.findViewById(R.id.header);
		this.lv = (ListView) view.findViewById(R.id.list);
		this.lv.addFooterView(this.footer);
		this.lv.setAdapter(this.adapter);		

		return view;
		
	}

	
	public void onResume() {
		super.onResume();	
		if(this.uncompletedTasks == null)
			this.refresh();
		if(this.taskGetter != null && this.taskGetter.isDoing())
			this.loadingBar.setVisibility(View.VISIBLE);
		else this.loadingBar.setVisibility(View.GONE);			
	}
	
	/*@Override public void onSaveInstanceState(Bundle savedInstanceState) {
		ArrayList<CheckableTask> tasks = null;
		if(completedTasks != null || uncompletedTasks != null) {
			tasks = new ArrayList<CheckableTask>();
			if(completedTasks != null)
				tasks.addAll(completedTasks);
			if(uncompletedTasks != null)
				tasks.addAll(uncompletedTasks);
		}
		savedInstanceState.putParcelableArrayList("tasks", tasks);
		savedInstanceState.putBoolean("completedShown", areCompletedShown);
		super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		ArrayList<CheckableTask> tasks = savedInstanceState.getParcelableArrayList("tasks");
		areCompletedShown = savedInstanceState.getBoolean("completedShown");
		if(tasks != null) {
			this.onTasksObtained(tasks);
			areCompletedShown = !areCompletedShown; //is like a pressure in the opposite condition
			this.footerButton.performClick();
		}
		//this.reloadActionButtons();
	}*/
	
    
	public static class DeleteDialogFragment extends DialogFragment {

	    public static DeleteDialogFragment newInstance() {
	    	DeleteDialogFragment frag = new DeleteDialogFragment();
	    	return frag;
	    }
		
	    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity())
			.setMessage(CONFIRM_DELETE)
			.setCancelable(false)
			.setPositiveButton(YES, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					final TaskFragment tf = ((TaskFragment) DeleteDialogFragment.this.getTargetFragment());
					MultipleTaskDeleter mtd = new MultipleTaskDeleter(OK1_delete, OK2_delete,
						NOK1_delete, NOK2_delete, DeleteDialogFragment.this.getActivity()){
						@Override protected void onPostExecute(HashMap<String, Task> changedTasks) {
							super.onPostExecute(changedTasks);
							tf.adapter.notifyDataSetChanged();
						}
					};
					for(CheckableTask task : tf.selectedTasks.values())
						mtd.add(new TaskDeleter(tf.getSherlockActivity(), task));
					Toast.makeText(tf.getSherlockActivity(), R.string.deleting, Toast.LENGTH_SHORT).show();
					tf.clearSelectedTasks();
					tf.reloadActionButtons();
					mtd.execute();
				}
			})
			.setNegativeButton(NO, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			return builder.create();
		}
	}
	
	public void onDismissPressed(View v) {
		TaskFragment.this.clearSelectedTasks();
		TaskFragment.this.reloadActionButtons();
	}
	
	public void onCompletePressed(View v) {
		MultipleTaskChanger mtCompleter = new MultipleTaskChanger(OK1_complete, OK2_complete,
		NOK1_complete, NOK2_complete, this.getSherlockActivity()) {
			@Override protected void onPostExecute(HashMap<String, Task> changedTasks) {
				super.onPostExecute(changedTasks);
				TaskFragment.this.adapter.notifyDataSetChanged();
			}
		};
		MultipleTaskChanger mtUncompleter = new MultipleTaskChanger(OK1_uncomplete, OK2_uncomplete,
			NOK1_uncomplete, NOK2_uncomplete, this.getSherlockActivity()) {
			@Override protected void onPostExecute(HashMap<String, Task> changedTasks) {
				super.onPostExecute(changedTasks);
				TaskFragment.this.adapter.notifyDataSetChanged();
			}
		};
		for(CheckableTask task : this.selectedTasks.values()) {
			if(task.getCompleted() == null)
				mtCompleter.add(new TaskCompleter(this.getSherlockActivity(), task));
			else
				mtUncompleter.add(new TaskUncompleter(this.getSherlockActivity(), task));
		}
		TaskFragment.this.clearSelectedTasks();
		TaskFragment.this.reloadActionButtons();
		if(mtCompleter.size() > 0) {
			Toast.makeText(TaskFragment.this.getSherlockActivity(), R.string.completing, Toast.LENGTH_SHORT).show();
			mtCompleter.execute();
		}
		if(mtUncompleter.size() > 0) {
			Toast.makeText(TaskFragment.this.getSherlockActivity(), R.string.uncompleting, Toast.LENGTH_SHORT).show();
			mtUncompleter.execute();
		}
			
	}
	
	public void onPostponePressed(View v) {
		MultipleTaskChanger mta = new MultipleTaskChanger(OK1_postpone, OK2_postpone,
				NOK1_postpone, NOK2_postpone, this.getSherlockActivity()) {
			@Override protected void onPostExecute(HashMap<String, Task> changedTasks) {
				super.onPostExecute(changedTasks);
				TaskFragment.this.adapter.notifyDataSetChanged();
			}
		};
		for(CheckableTask task : this.selectedTasks.values())
			mta.add(new TaskPostponer(this.getSherlockActivity(), task));
		TaskFragment.this.clearSelectedTasks();
		TaskFragment.this.reloadActionButtons();
		Toast.makeText(TaskFragment.this.getSherlockActivity(), R.string.postponing, Toast.LENGTH_SHORT).show();
		mta.execute();
	}
	
	public void onDeletePressed(View v) {
		DialogFragment newFragment = DeleteDialogFragment.newInstance();
		newFragment.setTargetFragment(this, 0);
	    newFragment.show(this.getFragmentManager(), "delete");
	}
	
	public void refresh() {
		this.retrieveTasks(this.type);
	}
	
	private void retrieveTasks(int type) {
		//recupero liste e location, poi aggiorno i task
		final int t = type;
		final DBTaskListsGetter tlg = new DBTaskListsGetter(TaskFragment.this.getSherlockActivity()) {
			@Override protected void onPostExecute(List<TaskList> tasklists) {
				TaskFragment.this.listMap = new HashMap<String,TaskList>();
				for(TaskList list : tasklists)
					TaskFragment.this.listMap.put(list.getId(), list);
				TaskFragment.this.getTasks(t);
			}
		};
		DBLocationsGetter lg = new DBLocationsGetter(TaskFragment.this.getSherlockActivity()) {
			@Override protected void onPostExecute(List<Location> locations) {
				TaskFragment.this.locMap = new HashMap<String,Location>();
				for(Location loc : locations)
					TaskFragment.this.locMap.put(loc.getId(), loc);
				tlg.execute();
			}
		};
		lg.execute();
		
	}
	
	private void getTasks(int type) {
		switch(type) {
		case LIST :
			String listId = this.identifier;
			boolean isSmart = this.isSmart;
			if(!isSmart) {
				this.taskGetter = new DBTaskGetterByList(this.task_NOK,this.getSherlockActivity()) {
					@Override public void onResultObtained(List<Task> tasks) {
						TaskFragment.this.onTasksObtained(tasks);
					}
					@Override public void onPreInquiry() {
						TaskFragment.this.loadingBar.setVisibility(View.VISIBLE);
					}
					@Override public void onPostInquiry() {
						TaskFragment.this.loadingBar.setVisibility(View.GONE);
					}
				};
				this.taskGetter.executeInBackground(listId);
			}
			else {
				this.taskGetter = new TaskGetterByListId(this.task_NOK,this.getSherlockActivity()) {
					@Override public void onResultObtained(List<Task> tasks) {
						TaskFragment.this.onTasksObtained(tasks);
					}
					@Override public void onPreInquiry() {
						TaskFragment.this.loadingBar.setVisibility(View.VISIBLE);
					}
					@Override public void onPostInquiry() {
						TaskFragment.this.loadingBar.setVisibility(View.GONE);
					}
				};
				this.taskGetter.executeInBackground(listId);
			}
			break;
		case LOCATION :
			String locId = this.identifier;
			this.taskGetter = new DBTaskGetterByLocation(this.task_NOK,this.getSherlockActivity()) {
				@Override public void onResultObtained(List<Task> tasks) {
					TaskFragment.this.onTasksObtained(tasks);
				}
				@Override public void onPreInquiry() {
					TaskFragment.this.loadingBar.setVisibility(View.VISIBLE);
				}
				@Override public void onPostInquiry() {
					TaskFragment.this.loadingBar.setVisibility(View.GONE);
				}
			};
			this.taskGetter.executeInBackground(locId);
			break;
		case TAG :
			String tag = this.name;
			this.taskGetter = new DBTaskGetterByTag(this.task_NOK,this.getSherlockActivity()) {
				@Override public void onResultObtained(List<Task> tasks) {
					TaskFragment.this.onTasksObtained(tasks);
				}
				@Override public void onPreInquiry() {
					TaskFragment.this.loadingBar.setVisibility(View.VISIBLE);
				}
				@Override public void onPostInquiry() {
					TaskFragment.this.loadingBar.setVisibility(View.GONE);
				}
			};
			this.taskGetter.executeInBackground(tag);
			break;
		case RECENTLY_COMPLETED :
			Calendar now = Calendar.getInstance();
			now.add(Calendar.MONTH, -1);
			this.taskGetter = new DBRecentTaskGetter(this.task_NOK,this.getSherlockActivity()) {
				@Override public void onResultObtained(List<Task> tasks) {
					TaskFragment.this.onTasksObtained(tasks);
				}
				@Override public void onPreInquiry() {
					TaskFragment.this.loadingBar.setVisibility(View.VISIBLE);
				}
				@Override public void onPostInquiry() {
					TaskFragment.this.loadingBar.setVisibility(View.GONE);
				}
			};
			this.taskGetter.executeInBackground("" + now.getTime().getTime());	
			break;
		case NO_TAG :
			this.taskGetter = new DBNotTaggedTaskGetter(this.task_NOK,this.getSherlockActivity()) {
				@Override public void onResultObtained(List<Task> tasks) {
					TaskFragment.this.onTasksObtained(tasks);
				}
				@Override public void onPreInquiry() {
					TaskFragment.this.loadingBar.setVisibility(View.VISIBLE);
				}
				@Override public void onPostInquiry() {
					TaskFragment.this.loadingBar.setVisibility(View.GONE);
				}
			};
			this.taskGetter.executeInBackground();	
			break;
		case NO_LOCATION :
			this.taskGetter = new DBNotLocatedTaskGetter(this.task_NOK,this.getSherlockActivity()) {
				@Override public void onResultObtained(List<Task> tasks) {
					TaskFragment.this.onTasksObtained(tasks);
				}
				@Override public void onPreInquiry() {
					TaskFragment.this.loadingBar.setVisibility(View.VISIBLE);
				}
				@Override public void onPostInquiry() {
					TaskFragment.this.loadingBar.setVisibility(View.GONE);
				}
			};
			this.taskGetter.executeInBackground();	
			break;
		case WITH_PRIORITY :
			this.taskGetter = new DBPrioritizedTaskGetter(this.task_NOK,this.getSherlockActivity()) {
				@Override public void onResultObtained(List<Task> tasks) {
					TaskFragment.this.onTasksObtained(tasks);
				}
				@Override public void onPreInquiry() {
					TaskFragment.this.loadingBar.setVisibility(View.VISIBLE);
				}
				@Override public void onPostInquiry() {
					TaskFragment.this.loadingBar.setVisibility(View.GONE);
				}
			};
			this.taskGetter.executeInBackground();	
			break;
		}
	}
	
	private void onTasksObtained(List<? extends Task> tasks) {
		if(this.type == RECENTLY_COMPLETED)
			Collections.sort(tasks, new TaskComparatorByCompletionDate());
		else
			Collections.sort(tasks, new TaskComparator());
		this.selectedTasks.clear();

		ArrayList<CheckableTask> uncompletedTasks = new ArrayList<CheckableTask>();
		ArrayList<CheckableTask> completedTasks = new ArrayList<CheckableTask>();
		for(Task task : tasks) {
			if(task.getCompleted() != null) {
				if(task instanceof CheckableTask) {
					CheckableTask checkable = (CheckableTask) task;
					//Log.d("compl", checkable.getName() + " - " + checkable.isChecked());
					completedTasks.add(new CheckableTask(checkable, checkable.isChecked()));
					if(checkable.isChecked())
						selectedTasks.put(checkable.getId(), checkable);
				}
				else
					completedTasks.add(new CheckableTask(task, false)); //to be modified
			}
			else {
				if(task instanceof CheckableTask) {
					CheckableTask checkable = (CheckableTask) task;
					//Log.d("uncompl", checkable.getName() + " - " + checkable.isChecked());
					uncompletedTasks.add(new CheckableTask(checkable, checkable.isChecked()));
					if(checkable.isChecked())
						selectedTasks.put(checkable.getId(), checkable);
				}
				else
					uncompletedTasks.add(new CheckableTask(task, false)); //to be modified
			}
		}
		this.uncompletedTasks = uncompletedTasks;
		this.completedTasks = completedTasks;

		this.adapter.reloadAndNotify(this.uncompletedTasks);
		
		//Log.d("selTasks", "" + this.selectedTasks);
		
		//pseudo-header management
		if(this.completedTasks.size() == 0 &&
				this.uncompletedTasks.size() == 0)
			this.header.setVisibility(View.VISIBLE);
		else
			this.header.setVisibility(View.GONE);
		
		//footer and (un)complete management
		if(type == RECENTLY_COMPLETED) {
			this.footerButton.performClick();
			footer.setVisibility(View.GONE);
		}
		else if(completedTasks.size() != 0) {
			footer.setVisibility(View.VISIBLE);
			Formatter f = new Formatter();
			f.format(SHOW_COMPLETED, completedTasks.size());
			footerButton.setText(f.toString());
			f.flush();
			f.close();
		}
		else {
			footer.setVisibility(View.GONE);
		}
		
		this.reloadActionButtons();
	}

	
	public void showOrHideCompleted(View v) {
		Button b = (Button) v;
		areCompletedShown = !areCompletedShown;
		if(areCompletedShown) {
			b.setText(HIDE_COMPLETED);
			this.adapter.addAll(completedTasks);
			this.adapter.notifyDataSetChanged();
			//this.reloadActionButtons();
		}
		else {
			Formatter f = new Formatter();
			f.format(SHOW_COMPLETED, completedTasks.size());
			b.setText(f.toString());
			f.flush();
			f.close();
			for(CheckableTask task : completedTasks) {
				task.setChecked(false);
				this.selectedTasks.remove(task.getId());
			}
			this.adapter.reloadAndNotify(uncompletedTasks);
			this.reloadActionButtons();
		}
		
	}
	
	private void clearSelectedTasks() {
		//clear selection
		this.selectedTasks.clear();
		for(CheckableTask task : completedTasks)
			task.setChecked(false);
		for(CheckableTask task : uncompletedTasks)
			task.setChecked(false);	
	}
	
	private void reloadActionButtons() {
		//Log.d("reload", "" + selectedTasks.size());
		if(this.selectedTasks.size() == 0) {
			this.completeButton.setVisibility(View.GONE);
			if(this.actionLayout.getVisibility() != View.GONE) {
				Animation anim = AnimationUtils.loadAnimation(this.getSherlockActivity(), R.anim.disappear);
				anim.setAnimationListener(new AnimationListener(){
					@Override public void onAnimationEnd(Animation anim) {
						TaskFragment.this.actionLayout.setVisibility(View.GONE);
					}
					@Override public void onAnimationRepeat(Animation anim) {
						// do nothing
					}
					@Override public void onAnimationStart(Animation anim) {
						TaskFragment.this.actionLayout.setVisibility(View.INVISIBLE);	
					}
					
				});	
				this.actionLayout.startAnimation(anim);
			}
		}
		else {
			boolean areAllCompleted = true;
			boolean areAllUncompleted = true;
			for(Task task : this.selectedTasks.values()) {
				if(task.getCompleted() != null) {
					areAllCompleted &= true;
					areAllUncompleted &= false;
				}
				else {
					areAllCompleted &= false;
					areAllUncompleted &= true;
				}
			}
			if(areAllCompleted) {
				this.completeButton.setText(UNCOMPLETE);
				this.completeButton.setVisibility(View.VISIBLE);
			}
			else if(areAllUncompleted) {
				this.completeButton.setText(COMPLETE);
				this.completeButton.setVisibility(View.VISIBLE);
			}
			else {
				this.completeButton.setVisibility(View.GONE);
			}
			//if(this.ab.isShowing()) this.ab.hide();
			if(this.actionLayout.getVisibility() != View.VISIBLE) {
				Animation anim = AnimationUtils.loadAnimation(this.getSherlockActivity(), R.anim.appear);
				this.actionLayout.startAnimation(anim);
				this.actionLayout.setVisibility(View.VISIBLE);
			}
		}
	}
	
	
	private class TaskAdapter extends ImprovedArrayAdapter<CheckableTask> {
		
		TaskAdapter(Context context, List<CheckableTask> tasks) {
			super(context, R.layout.task_row, tasks);
		}
		
		private class TaskViewHolder {
			public TextView im;
			public TextView head;
			//public TextView extras;
			public TextView lab;
			public TextView time;
			public ImageView repeatIcon;
			public ImageView estimateIcon;
			public ImageView urlIcon;
			public ImageView noteIcon;
			public ImageView contactIcon;
			public TextView noteCount;
			public CheckBox checkbox;
			public LinearLayout taskText;
		}
		
		@Override public View getView(int position, View convertView, ViewGroup parent) {
			CheckableTask task = this.getItem(position);
			if(convertView == null) {
				LayoutInflater inflater 
					= (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.task_row, null);  
				final TaskViewHolder taskHolder = new TaskViewHolder();
				taskHolder.im = (TextView) convertView.findViewById(R.id.priority);
				taskHolder.lab = (TextView) convertView.findViewById(R.id.taskLabels);
				taskHolder.time = (TextView) convertView.findViewById(R.id.taskTime);
				taskHolder.head = (TextView) convertView.findViewById(R.id.taskHeader);
				taskHolder.contactIcon = (ImageView) convertView.findViewById(R.id.contactIcon);
				taskHolder.urlIcon = (ImageView) convertView.findViewById(R.id.urlIcon);
				taskHolder.noteIcon = (ImageView) convertView.findViewById(R.id.noteIcon);
				taskHolder.repeatIcon = (ImageView) convertView.findViewById(R.id.repeatIcon);
				taskHolder.estimateIcon = (ImageView) convertView.findViewById(R.id.estimateIcon);
				taskHolder.noteCount = (TextView) convertView.findViewById(R.id.noteCount);
				//taskHolder.extras = (TextView) convertView.findViewById(R.id.taskExtras);
				taskHolder.checkbox = (CheckBox) convertView.findViewById(R.id.checkBox);
//				if(task.isChecked()) 
//					taskHolder.checkbox.setChecked(true);
//				else taskHolder.checkbox.setChecked(false);
				final int pos = position;
				taskHolder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override public void onCheckedChanged(CompoundButton checkBox, boolean isChecked) {
						CheckableTask tmpTask = (CheckableTask) taskHolder.checkbox.getTag();
						tmpTask.setChecked(checkBox.isChecked());
						TaskFragment.this.lv.setItemChecked(pos, isChecked);
						if(isChecked) {
							TaskFragment.this.selectedTasks.put(tmpTask.getId(), tmpTask);
							//Log.d("add change", tmpTask.getName() + " - " + selectedTasks.size());
						}
						else {
							TaskFragment.this.selectedTasks.remove(tmpTask.getId());
							//Log.d("remove change", tmpTask.getName() + " - " + selectedTasks.size());
						}
						TaskFragment.this.reloadActionButtons();
						//Toast.makeText(TaskActivity.this, "" + isChecked, Toast.LENGTH_SHORT).show();
					}
				});
				taskHolder.taskText = (LinearLayout) convertView.findViewById(R.id.taskText);
				convertView.setTag(taskHolder);
				taskHolder.checkbox.setTag(task);
			}
			else
				((TaskViewHolder) convertView.getTag()).checkbox.setTag(task);
			TaskViewHolder holder = (TaskViewHolder) convertView.getTag();
			TaskFormat tf = new TaskFormat(dateFormatStrings, BLUE, ORANGE);
			

			String header = task.getName();//tf.formatHeader(task, true);
			String labels = tf.formatLabels(task, 
					TaskFragment.this.listMap, TaskFragment.this.locMap, true);
			String date = tf.formatDate(task);
			//String extra = tf.formatExtras(task);
			if(task.getCompleted() != null) {
				holder.head.setPaintFlags(holder.head.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			//	header = /*"<font color=\"" + LIGHT_GREY + "\">*/"<I>" + header + "</I>"/*</font>*/;
			}
			else {
				holder.head.setPaintFlags( holder.head.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
			}
			if(task.getDue() != null) {
				Date due = task.getDue();
				Date now = new Date();
				if(SmartDateComparator.isToday(now, due))
					header = "<B>" + header + "</B>";
				else if(SmartDateComparator.isOverdue(now, due))
					header = "<B><U>" + header + "</U></B>";
			}
			holder.lab.setText(Html.fromHtml(labels));	
			holder.head.setText(Html.fromHtml(header));
			if(type == RECENTLY_COMPLETED) {
				String completedOn = TaskFragment.this.getResources().getString(R.string.completePhrase);
				date = completedOn + " " + 
				SmartDateFormat.format(dateFormatStrings, task.getCompleted());
				holder.estimateIcon.setVisibility(View.GONE);
				holder.repeatIcon.setVisibility(View.GONE);
			}
			else {
				if(task.getEstimateDetail().length > 0)
					holder.estimateIcon.setVisibility(View.VISIBLE);
				else holder.estimateIcon.setVisibility(View.GONE);
				if(task.getRecurrence() != null)
					holder.repeatIcon.setVisibility(View.VISIBLE);
				else holder.repeatIcon.setVisibility(View.GONE);
			}
			holder.time.setText(date);
			
			//holder.extras.setText(extra);
			if(task.getUrl() != null && !task.getUrl().equals(""))
				holder.urlIcon.setVisibility(View.VISIBLE);
			else holder.urlIcon.setVisibility(View.GONE);
			if(task.getParticipants().length > 0)
				holder.contactIcon.setVisibility(View.VISIBLE);
			else holder.contactIcon.setVisibility(View.GONE);
			if(task.getNotes().length > 0) {
				holder.noteIcon.setVisibility(View.VISIBLE);
				holder.noteCount.setVisibility(View.VISIBLE);
				holder.noteCount.setText("" + task.getNotes().length);
			}
			else {
				holder.noteIcon.setVisibility(View.GONE);
				holder.noteCount.setVisibility(View.GONE);
			}
			String filler = "<br>";
			if(!date.equals("")) {
				filler = "<br><br>";
				holder.time.setVisibility(View.VISIBLE);
			}
			else {
				holder.time.setVisibility(View.GONE);
			}
			holder.im.setText(Html.fromHtml(filler));
			if(task.getPriority() == Priority.HIGH) 
				holder.im.setBackgroundResource(R.color.priority_high);
			else if(task.getPriority() == Priority.MEDIUM)
				holder.im.setBackgroundResource(R.color.priority_medium);
			else if(task.getPriority() == Priority.LOW) 
				holder.im.setBackgroundResource(R.color.priority_low);
			else
				holder.im.setBackgroundResource(0);
			
			holder.checkbox.setChecked(task.isChecked());
//			if(task.isChecked()) {
//				TaskActivity.this.selectedTasks.add(task);
//				//Log.d("add", task.getName() + " - " + selectedTasks.size());
//			}
//			else {
//				TaskActivity.this.selectedTasks.remove(task);
//				//Log.d("remove", task.getName() + " - " + selectedTasks.size());
//			}
			//TaskActivity.this.reloadActionButtons();

			holder.taskText.setOnClickListener(new SmartClickListener<CheckableTask>(task) {
				@Override public void onClick(View v) {
					//Toast.makeText(TaskActivity.this, "ciao", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(TaskFragment.this.getSherlockActivity(),TaskEditActivity.class);
					CheckableTask task = this.get();
					intent.putExtra("task", (Parcelable) task);
					TaskFragment.this.startActivityForResult(intent, TASK_EDIT);
				}
			});
			return convertView;
		}
	}
	
	public boolean checkAddedTasks(int type, String idOrName, boolean isSmart, List<ParcelableTask> tasks) {
		boolean areTheseTasksAffected = false;
		switch(type) {
		case(LIST) : {
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
		return areTheseTasksAffected;
	}
	
	public boolean checkChangedTasks(int type, boolean isSmart, List<String> changedIds) {
		boolean areTheseTasksAffected = false;
		if(type == LIST) {
			if(isSmart) areTheseTasksAffected = true;
		}
		
		if(!areTheseTasksAffected && 
				this.completedTasks != null &&
				this.uncompletedTasks != null) {
			
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
		return areTheseTasksAffected;
	
	}

	

}
