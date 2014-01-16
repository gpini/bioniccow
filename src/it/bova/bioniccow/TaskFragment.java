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
import android.util.Log;
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
	private TextView titleHeader;
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
		boolean isSinglePane = this.getArguments().getBoolean("isSinglePane",false);
		
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
		this.titleHeader = (TextView) view.findViewById(R.id.title_header);
		this.lv = (ListView) view.findViewById(R.id.list);
		this.lv.addFooterView(this.footer);
		this.lv.setAdapter(this.adapter);	
		this.lv.setItemsCanFocus(false);
        this.lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		if(!isSinglePane) {
			titleHeader.setVisibility(View.VISIBLE);
			String title = ""; 
			switch(type) {
			case LIST : 
				title = name;
				break;
			case LOCATION : 
				title = name;
				break;
			case TAG :
				title = name;
				break;
			case NO_TAG :
				title = this.getResources().getStringArray(R.array.specials)[0];
				break;
			case NO_LOCATION :
				title = this.getResources().getStringArray(R.array.specials)[1];
				break;
			case RECENTLY_COMPLETED :
				title = this.getResources().getStringArray(R.array.specials)[2];
				break;
			case WITH_PRIORITY :
				title = this.getResources().getStringArray(R.array.specials)[3];
				break;
			}
			titleHeader.setText(title);
		}

		return view;
		
	}

	
	public void onResume() {
		//Log.w("pippo","pippo "+ uncompletedTasks);
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
		//Map<String, CheckableTask> oldSelectedTasks = new HashMap<String, CheckableTask>();
		//oldSelectedTasks.putAll(this.selectedTasks);
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
				else {
					/*if(oldSelectedTasks.get(task.getId()) != null) //se era giˆ selezionato
						completedTasks.add(new CheckableTask(task, true));
					else*/
						completedTasks.add(new CheckableTask(task, false));
				}
					
			}
			else {
				if(task instanceof CheckableTask) {
					CheckableTask checkable = (CheckableTask) task;
					//Log.d("uncompl", checkable.getName() + " - " + checkable.isChecked());
					uncompletedTasks.add(new CheckableTask(checkable, checkable.isChecked()));
					if(checkable.isChecked())
						selectedTasks.put(checkable.getId(), checkable);
				}
				else {
					/*if(oldSelectedTasks.get(task.getId()) != null) //se era giˆ selezionato
						uncompletedTasks.add(new CheckableTask(task, true));
					else*/
						uncompletedTasks.add(new CheckableTask(task, false));
				}
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
		//CAB
		if (this.selectedTasks.size() > 0) {
			if(this.actionMode == null) {
				this.actionMode = this.getSherlockActivity().startActionMode(new ActionModeCallback());
			}
			this.actionMode.setTitle("" + this.selectedTasks.size());
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
			MenuItem completeItem = this.actionMode.getMenu().findItem(R.id.complete);
			if(areAllCompleted) {
				if(!completeItem.isVisible()) {
					completeItem.setVisible(true);
					completeItem.setEnabled(true);
				}
				if(completeItem.getTitle() != UNCOMPLETE)
					completeItem.setTitle(R.string.uncomplete);
			}
			else if(areAllUncompleted) {
				if(!completeItem.isVisible()) {
					completeItem.setVisible(true);
					completeItem.setEnabled(true);
				}
				if(completeItem.getTitle() != COMPLETE)
					completeItem.setTitle(R.string.complete);
			}
			else {
				if(completeItem.isVisible()) {
					completeItem.setVisible(false);
					completeItem.setEnabled(false);
				}
			}
		}
		// there are some selected items, start the actionMode
		if (TaskFragment.this.selectedTasks.size() == 0 && TaskFragment.this.actionMode != null)
			// there no selected items, finish the actionMode
			TaskFragment.this.actionMode.finish();
		
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
				taskHolder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override public void onCheckedChanged(CompoundButton checkBox, boolean isChecked) {
						CheckableTask tmpTask = (CheckableTask) taskHolder.checkbox.getTag();
						tmpTask.setChecked(checkBox.isChecked());
						//TaskFragment.this.lv.setItemChecked(pos, isChecked);
						if(isChecked) {
							TaskFragment.this.selectedTasks.put(tmpTask.getId(), tmpTask);
							//Log.d("add change", tmpTask.getName() + " - " + selectedTasks.size());
							/*if (TaskFragment.this.selectedTasks.size() > 0 && TaskFragment.this.actionMode == null)
								// there are some selected items, start the actionMode
								mActionMode = startActionMode(new ActionModeCallback());*/
						}
						else {
							TaskFragment.this.selectedTasks.remove(tmpTask.getId());
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
	
	public void refreshOnTaskAdded(List<ParcelableTask> tasks) {
		boolean areTheseTasksAffected = false;
		switch(type) {
		case(LIST) : {
			if(isSmart) areTheseTasksAffected = true;
			else {
				for(Task task : tasks)
					if(task.getListId().equals(identifier))
						areTheseTasksAffected = true;
			}
		}
		break;
		case(LOCATION) :
			for(Task task : tasks)
				if(task.getLocationId().equals(identifier))
					areTheseTasksAffected = true;
		break;
		case(TAG) : {
			for(Task task : tasks) {
				String[] tags = task.getTags();
				for(String tag : tags) {
					if(tag.equals(identifier)) {
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
		if(areTheseTasksAffected)
			this.refresh();
	}
	
	public void refreshOnTaskChanged(List<String> changedIds) {
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
		if(areTheseTasksAffected)
			this.refresh();
	
	}
	
	private class ActionModeCallback implements ActionMode.Callback {
		
		private boolean closedByAction = false;
 
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			if(!closedByAction) {
				TaskFragment.this.clearSelectedTasks();
				TaskFragment.this.adapter.notifyDataSetChanged();
			}
			TaskFragment.this.actionMode = null;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode,
				com.actionbarsherlock.view.Menu menu) {
			// inflate contextual menu  
			mode.getMenuInflater().inflate(R.menu.task_contextual_menu, menu);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode,
				com.actionbarsherlock.view.Menu menu) {
			//menu.getItem(0).setTitle(R.id.delete);
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode,
				com.actionbarsherlock.view.MenuItem item) {
			 switch (item.getItemId()) {
	            case R.id.complete:
	                TaskFragment.this.onCompletePressed();
	                closedByAction = true;
	                mode.finish(); // Action picked, so close the CAB
	                return true;
	            case R.id.postpone:
	            	TaskFragment.this.onPostponePressed();
	            	closedByAction = true;
	                mode.finish(); // Action picked, so close the CAB
	                return true;
	            case R.id.delete:
	            	TaskFragment.this.onDeletePressed();
	            	closedByAction = true;
	                mode.finish(); // Action picked, so close the CAB
	                return true;
	            default:
	            	mode.finish();
	                return false;
	        }
		}
	 
	}

	

}
