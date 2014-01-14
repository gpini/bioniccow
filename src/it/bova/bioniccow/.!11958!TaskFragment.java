package it.bova.bioniccow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.MenuItem;

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
import android.view.View.OnClickListener;
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
	private boolean areCompletedShown = false;
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
	
	public ActionMode actionMode;

	
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
		this.identifier = this.getArguments().getString(IDENTIFIER);
		this.name = this.getArguments().getString(NAME);
		this.isSmart = this.getArguments().getBoolean("isSmart",false);
		
		View view = inflater.inflate(R.layout.task_list,
		        container, false);
		this.loadingBar = (RelativeLayout) view.findViewById(R.id.loadingBar);
		
		this.adapter = new TaskAdapter(this.getSherlockActivity(), new ArrayList<CheckableTask>());
		this.footer = inflater.inflate(R.layout.task_footer, null);
		this.footerButton = (Button) this.footer.findViewById(R.id.footerText);
		this.footerButton.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) {
				TaskFragment.this.showOrHideCompleted(v);
			}
		});
		this.header = view.findViewById(R.id.header);
		this.lv = (ListView) view.findViewById(R.id.list);
		this.lv.addFooterView(this.footer);
		this.lv.setAdapter(this.adapter);	
		this.lv.setItemsCanFocus(false);
        this.lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		

		return view;
		
	}

	
	public void onResume() {
		super.onResume();	
		if(this.uncompletedTasks == null)
			this.retrieveTasks(type);
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
					tf.onDeleteConfimPressed();
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
	
	public void onCompletePressed() {
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
		this.clearSelectedTasks();
		if(mtCompleter.size() > 0) {
			Toast.makeText(this.getSherlockActivity(), R.string.completing, Toast.LENGTH_SHORT).show();
			mtCompleter.execute();
		}
		if(mtUncompleter.size() > 0) {
			Toast.makeText(this.getSherlockActivity(), R.string.uncompleting, Toast.LENGTH_SHORT).show();
			mtUncompleter.execute();
		}
			
	}
	
	public void onPostponePressed() {
		MultipleTaskChanger mta = new MultipleTaskChanger(OK1_postpone, OK2_postpone,
				NOK1_postpone, NOK2_postpone, this.getSherlockActivity()) {
			@Override protected void onPostExecute(HashMap<String, Task> changedTasks) {
				super.onPostExecute(changedTasks);
				TaskFragment.this.adapter.notifyDataSetChanged();
			}
		};
		for(CheckableTask task : this.selectedTasks.values())
			mta.add(new TaskPostponer(this.getSherlockActivity(), task));
		this.clearSelectedTasks();
		Toast.makeText(this.getSherlockActivity(), R.string.postponing, Toast.LENGTH_SHORT).show();
		mta.execute();
	}
	
	public void onDeletePressed() {
		DialogFragment newFragment = DeleteDialogFragment.newInstance();
		newFragment.setTargetFragment(this, 0);
	    newFragment.show(this.getFragmentManager(), "delete");
	}
	
	public void onDeleteConfimPressed() {
		MultipleTaskDeleter mtd = new MultipleTaskDeleter(OK1_delete, OK2_delete,
			NOK1_delete, NOK2_delete, this.getSherlockActivity()){
			@Override protected void onPostExecute(HashMap<String, Task> changedTasks) {
				super.onPostExecute(changedTasks);
				TaskFragment.this.adapter.notifyDataSetChanged();
			}
		};
		for(CheckableTask task : this.selectedTasks.values())
			mtd.add(new TaskDeleter(this.getSherlockActivity(), task));
		Toast.makeText(this.getSherlockActivity(), R.string.deleting, Toast.LENGTH_SHORT).show();
		this.clearSelectedTasks();
		mtd.execute();
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
	
	private void onTasksObtained(List<Task> tasks) {
		if(this.type == RECENTLY_COMPLETED)
			Collections.sort(tasks, new TaskComparatorByCompletionDate());
		else
			Collections.sort(tasks, new TaskComparator());
		Map<String, CheckableTask> oldSelectedTasks = new HashMap<String, CheckableTask>();
		oldSelectedTasks.putAll(this.selectedTasks);
		this.selectedTasks.clear();

		ArrayList<CheckableTask> uncompletedTasks = new ArrayList<CheckableTask>();
		ArrayList<CheckableTask> completedTasks = new ArrayList<CheckableTask>();
		for(Task task : tasks) {
			if(task.getCompleted() != null) {
				/*if(task instanceof CheckableTask) {
					CheckableTask checkable = (CheckableTask) task;
					//Log.d("compl", checkable.getName() + " - " + checkable.isChecked());
					completedTasks.add(new CheckableTask(checkable, checkable.isChecked()));
					if(checkable.isChecked())
						selectedTasks.put(checkable.getId(), checkable);
				}
				else {*/
