package it.bova.bioniccow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import it.bova.bioniccow.asyncoperations.rtmobjects.DBLocationsGetter;
import it.bova.bioniccow.asyncoperations.rtmobjects.DBTaskListsGetter;
import it.bova.bioniccow.asyncoperations.rtmobjects.NoteAdder;
import it.bova.bioniccow.asyncoperations.rtmobjects.NoteDeleter;
import it.bova.bioniccow.asyncoperations.rtmobjects.NoteModifier;
import it.bova.bioniccow.asyncoperations.tasks.DueDateChanger;
import it.bova.bioniccow.asyncoperations.tasks.EstimateChanger;
import it.bova.bioniccow.asyncoperations.tasks.LocationChanger;
import it.bova.bioniccow.asyncoperations.tasks.MultipleTaskChanger;
import it.bova.bioniccow.asyncoperations.tasks.MultipleTaskDeleter;
import it.bova.bioniccow.asyncoperations.tasks.NameChanger;
import it.bova.bioniccow.asyncoperations.tasks.PriorityChanger;
import it.bova.bioniccow.asyncoperations.tasks.RecurrenceChanger;
import it.bova.bioniccow.asyncoperations.tasks.TagAdder;
import it.bova.bioniccow.asyncoperations.tasks.TagRemover;
import it.bova.bioniccow.asyncoperations.tasks.TaskCompleter;
import it.bova.bioniccow.asyncoperations.tasks.MultipleTaskEditor;
import it.bova.bioniccow.asyncoperations.tasks.TaskDeleter;
import it.bova.bioniccow.asyncoperations.tasks.TaskListChanger;
import it.bova.bioniccow.asyncoperations.tasks.TaskPostponer;
import it.bova.bioniccow.asyncoperations.tasks.TaskUncompleter;
import it.bova.bioniccow.asyncoperations.tasks.UrlChanger;
import it.bova.bioniccow.utilities.ImprovedDatePickerDialog;
import it.bova.bioniccow.utilities.ImprovedTimePickerDialog;
import it.bova.bioniccow.utilities.SmartClickListener;
import it.bova.bioniccow.utilities.SmartDialogInterfaceClickListener;
import it.bova.bioniccow.utilities.TaskCloner;
import it.bova.bioniccow.utilities.rtmobjects.LocationComparator;
import it.bova.bioniccow.utilities.rtmobjects.SmartAddFormat;
import it.bova.bioniccow.utilities.rtmobjects.SmartDateFormat;
import it.bova.bioniccow.utilities.rtmobjects.TaskFormat;
import it.bova.bioniccow.utilities.rtmobjects.CheckableTask;
import it.bova.bioniccow.utilities.rtmobjects.TaskListComparator;
import it.bova.rtmapi.Contact;
import it.bova.rtmapi.Estimate;
import it.bova.rtmapi.Frequency;
import it.bova.rtmapi.Location;
import it.bova.rtmapi.Note;
import it.bova.rtmapi.Priority;
import it.bova.rtmapi.Recurrence;
import it.bova.rtmapi.Task;
import it.bova.rtmapi.TaskList;
import com.actionbarsherlock.app.SherlockActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TaskEditActivity extends TaskDetailActivity {
	
	private TableLayout tableLayout;
	private EditText noteTitleEditText;
	private EditText noteTextEditText;
	private EditText noteTitleEditText2;
	private EditText noteTextEditText2;
	private Map<String,View> noteViewMap;
	private boolean isNoteChanged = false;
	
	//resources
	private String CONFIRM_DELETE;
	private String YES;
	private String NO;

	private static final int DIALOG_DELETE = 5;
	private static final int NOTE_ADD = 6;
	private static final int NOTE_EDIT = 7;

		   	
	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.messageReceiver = new TaskEditActivityMessageReceiver(this);
		
		Intent intent = this.getIntent();
		this.taskToBeEdited = (CheckableTask) intent.getParcelableExtra("task");
		if(this.taskToBeEdited == null) {
			this.finish();
		}
		
		//resurces
		CONFIRM_DELETE = this.getResources().getString(R.string.confirmDelete);
		YES = this.getResources().getString(R.string.yes);
		NO = this.getResources().getString(R.string.no);
		
		//adjust buttons
		this.findViewById(R.id.addNoteButton).setVisibility(View.VISIBLE);
		if(this.taskToBeEdited.getCompleted() == null)
			((Button)this.findViewById(R.id.completeButton)).setText(R.string.complete);
		else
			((Button)this.findViewById(R.id.completeButton)).setText(R.string.uncomplete);
		
		this.tableLayout = (TableLayout) this.findViewById(R.id.table);
		this.noteViewMap = new HashMap<String,View>();
		
		//fill forms with task data
		this.fillFormViews(taskToBeEdited);
		
		//Store recurrence option values
		Recurrence rec = this.taskToBeEdited.getRecurrence();
		if(rec != null) {
			newRecurrenceOption = rec.getOption();
			newRecurrenceString = rec.getOptionValue();
		}
		
		//"Where am I" TextViews
		this.ab.setTitle(taskToBeEdited.getName());
		this.ab.setSubtitle(R.string.edit_task);
		
	}

	public void onResume() {
		super.onResume();
		
		this.refreshTaskLists();
		this.refreshLocations();
		
	}
	
	public void onPause() {
		super.onPause();	
	}
	
	@Override public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putBoolean("isNoteChanged", isNoteChanged);
		this.getIntent().putExtra("task", (Parcelable) taskToBeEdited);
		super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		isNoteChanged = savedInstanceState.getBoolean("isNoteChanged", true);
	}
	
    
    protected Dialog onCreateDialog(int id, Bundle bundle) {
        Dialog dialog;
        switch(id) {
        case DATE_DIALOG:
			if(year == null || month == null || day == null) {
				Calendar cal = Calendar.getInstance();
				return new ImprovedDatePickerDialog(this, dateFormatStrings, dateSetListener,
						cal.get(Calendar.YEAR), 
						cal.get(Calendar.MONTH),
						cal.get(Calendar.DAY_OF_MONTH));
			}
			else	
				return new ImprovedDatePickerDialog(this, dateFormatStrings, dateSetListener,
						year, month, day);
		case TIME_DIALOG:
			if(hour == null || minute == null) 
				return new ImprovedTimePickerDialog(this, timeSetListener, 0, 0);
			else
				return new ImprovedTimePickerDialog(this, timeSetListener, hour, minute);
        case DIALOG_DELETE :
    		AlertDialog.Builder builder = new AlertDialog.Builder(this)
    		.setMessage(CONFIRM_DELETE)
    		.setCancelable(true)
    		.setPositiveButton(YES, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
    				String OK1 = TaskEditActivity.this.getResources().getString(R.string.task_deleted_OK1);
    				String OK2 = TaskEditActivity.this.getResources().getString(R.string.task_deleted_OK2);
    				String NOK1 = TaskEditActivity.this.getResources().getString(R.string.task_deleted_NOK1);
    				String NOK2 = TaskEditActivity.this.getResources().getString(R.string.task_deleted_NOK2);
    				MultipleTaskDeleter mtd = new MultipleTaskDeleter(OK1, OK2, NOK1, NOK2, TaskEditActivity.this);
    				mtd.add(new TaskDeleter(TaskEditActivity.this, TaskEditActivity.this.taskToBeEdited));
    				mtd.execute();
    				TaskEditActivity.this.finish();
    			}
    		})
    		.setNegativeButton(NO, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
    				dialog.cancel();
    			}
    		});
    		dialog = builder.create();
        	break;
        case NOTE_EDIT:
    		LayoutInflater inflater
    		= (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		View noteDialogView = inflater.inflate(R.layout.note_add_or_edit, null);
    		noteTitleEditText = (EditText) noteDialogView.findViewById(R.id.noteTitle);
    		noteTextEditText = (EditText) noteDialogView.findViewById(R.id.noteText);
    		String SAVE = TaskEditActivity.this.getResources().getString(R.string.save);
			String CANCEL = TaskEditActivity.this.getResources().getString(R.string.cancel);
        	AlertDialog.Builder builder3 = new AlertDialog.Builder(this)
    		.setTitle(R.string.editNote)
    		.setCancelable(true)
    		.setView(noteDialogView)
    		.setPositiveButton(SAVE, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			})
			.setNegativeButton(CANCEL, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
    		dialog = builder3.create();
    		break;
        case NOTE_ADD:
    		LayoutInflater inflater2
    		= (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		View noteDialogView2 = inflater2.inflate(R.layout.note_add_or_edit, null);
    		noteTitleEditText2 = (EditText) noteDialogView2.findViewById(R.id.noteTitle);
    		noteTextEditText2 = (EditText) noteDialogView2.findViewById(R.id.noteText);
    		String SAVE2 = TaskEditActivity.this.getResources().getString(R.string.save);
    		String CANCEL2 = TaskEditActivity.this.getResources().getString(R.string.cancel);
        	AlertDialog.Builder builder2 = new AlertDialog.Builder(this)
    		.setTitle(R.string.addNote)
    		.setCancelable(true)
    		.setView(noteDialogView2)
    		.setPositiveButton(SAVE2, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
    				String OK = TaskEditActivity.this.getResources().getString(R.string.note_added_OK);
    				String NOK = TaskEditActivity.this.getResources().getString(R.string.note_added_NOK);
					InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					mgr.hideSoftInputFromWindow(noteTitleEditText2.getWindowToken(), 0);
					mgr.hideSoftInputFromWindow(noteTextEditText2.getWindowToken(), 0);
    				NoteAdder na = new NoteAdder(OK, NOK, TaskEditActivity.this);
    				String[] params = new String[] {
    						taskToBeEdited.getId(),
    						taskToBeEdited.getTaskserieId(),
    						taskToBeEdited.getListId(),
    						noteTitleEditText2.getText().toString(),
    						noteTextEditText2.getText().toString()
    				};
    				na.executeInBackground(params);
    				dialog.cancel();
    			}
    		})
    		.setNegativeButton(CANCEL2, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
    				dialog.cancel();
    			}
    		});
    		dialog = builder2.create();
    		break;
        default:
            return super.onCreateDialog(id, bundle);
        }
        return dialog;
    }

    @Override protected void onPrepareDialog(int id, Dialog dialog, Bundle bundle) {
    	AlertDialog ad = (AlertDialog) dialog;
    	switch(id) {
    	case NOTE_EDIT : 
    		String noteId = bundle.getString("id");
    		noteTitleEditText.setText(bundle.getString("title"));
    		noteTextEditText.setText(bundle.getString("text"));
    		String SAVE = TaskEditActivity.this.getResources().getString(R.string.save);
    		String CANCEL = TaskEditActivity.this.getResources().getString(R.string.cancel);
    		ad.setButton(Dialog.BUTTON_POSITIVE, SAVE, new SmartDialogInterfaceClickListener<String>(noteId) {
    			@Override public void onClick(DialogInterface dialog, int id) {
    				InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    				mgr.hideSoftInputFromWindow(noteTitleEditText.getWindowToken(), 0);
    				mgr.hideSoftInputFromWindow(noteTextEditText.getWindowToken(), 0);
    				String OK = TaskEditActivity.this.getResources().getString(R.string.note_modified_OK);
    				String NOK = TaskEditActivity.this.getResources().getString(R.string.note_modified_NOK);
    				final String nId = this.get();
    				NoteModifier nm = new NoteModifier(OK, NOK, TaskEditActivity.this);
    				String[] params = new String[] {
    						nId,
    						noteTitleEditText.getText().toString(),
    						noteTextEditText.getText().toString()
    				};
					noteTitleEditText.setText("");
					noteTextEditText.setText("");
    				nm.executeInBackground(params);
					dialog.cancel();
    			}
    		});
    		ad.setButton(Dialog.BUTTON_NEGATIVE, CANCEL, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
    				InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    				mgr.hideSoftInputFromWindow(noteTitleEditText.getWindowToken(), 0);
    				mgr.hideSoftInputFromWindow(noteTextEditText.getWindowToken(), 0);
    				dialog.cancel();
    			}
    		});
    		break;
    	case NOTE_ADD :
    		noteTitleEditText2.setText("");
    		noteTextEditText2.setText("");
    		break;
    	case EDIT_RECURRENCE :
    		this.showUpdatedRecurrenceDialog();
    		break;
    	default : break;
    	}

    }

	@Override public void finish() {
		if(isNoteChanged)
			this.setResult(RESULT_OK);
		super.finish();
	}
    
	public void onSaveActionPressed() {
		//save edited task
		String taskEditOK = this.getResources().getString(R.string.task_modified_OK);
		String taskEditNOK = this.getResources().getString(R.string.task_modified_NOK);
		MultipleTaskEditor mte = new MultipleTaskEditor(taskEditOK, taskEditNOK, this, taskToBeEdited);
		this.loadTaskEditor(mte);
		Toast.makeText(this, R.string.editing, Toast.LENGTH_SHORT).show();
		TaskEditActivity.this.finish();
	}
	
	public void onCompletePressed(View v) {
		String OK1 = this.getResources().getString(R.string.task_completed_OK1);
		String OK2 = this.getResources().getString(R.string.task_completed_OK2);
		String NOK1 = this.getResources().getString(R.string.task_completed_NOK1);
		String NOK2 = this.getResources().getString(R.string.task_completed_NOK2);
		String OK3 = this.getResources().getString(R.string.task_uncompleted_OK1);
		String OK4 = this.getResources().getString(R.string.task_uncompleted_OK2);
		String NOK3 = this.getResources().getString(R.string.task_uncompleted_NOK1);
		String NOK4 = this.getResources().getString(R.string.task_uncompleted_NOK2);
		MultipleTaskChanger mtCompleter = new MultipleTaskChanger(OK1, OK2, NOK1, NOK2, this);
		MultipleTaskChanger mtUncompleter = new MultipleTaskChanger(OK3, OK4, NOK3, NOK4, this);
		if(this.taskToBeEdited.getCompleted() == null) {
			mtCompleter.add(new TaskCompleter(this, this.taskToBeEdited));
			mtCompleter.execute();
		}
		else  {
			mtUncompleter.add(new TaskUncompleter(this, this.taskToBeEdited));
			mtUncompleter.execute();
		}
		this.finish();
	}
	
	public void onPostponePressed(View v) {
		String OK1 = this.getResources().getString(R.string.task_postponed_OK1);
		String OK2 = this.getResources().getString(R.string.task_postponed_OK2);
		String NOK1 = this.getResources().getString(R.string.task_postponed_NOK1);
		String NOK2 = this.getResources().getString(R.string.task_postponed_NOK2);
		MultipleTaskChanger mta = new MultipleTaskChanger(OK1, OK2, NOK1, NOK2, this);
		mta.add(new TaskPostponer(this, this.taskToBeEdited));
		mta.execute();
		this.finish();
	}
	
	public void onDeletePressed(View v) {
		this.showDialog(DIALOG_DELETE);
	}
	
	public void onAddNotePressed(View v) {
		this.showDialog(NOTE_ADD);
	}
		
	private void fillFormViews(Task task) {
		//priority
		Priority priority = task.getPriority();
		if(priority == Priority.LOW)
			this.prioritySpinner.setSelection(1);
		else if(priority == Priority.MEDIUM)
			this.prioritySpinner.setSelection(2);
		else if(priority == Priority.HIGH)
			this.prioritySpinner.setSelection(3);
		else
			this.prioritySpinner.setSelection(0);
		//name
		this.nameInput.setText(task.getName());
		//completed status
		if(task.getCompleted() != null) {
			this.findViewById(R.id.completedLayout).setVisibility(View.VISIBLE);
			this.nameInput.setPaintFlags(this.nameInput.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			Date completionDate = task.getCompleted();
			((TextView) this.findViewById(R.id.completedTextView)).append(" " + SmartDateFormat.format(this.dateFormatStrings, completionDate));
		}
		//list - see observer behavior
		//locations - see observer behavior
		StringBuilder tagString = new StringBuilder("");
		for(String tag : task.getTags())
			tagString.append(tag + " ");
		this.tagInput.setText(tagString);
		//date&time
		Date dueDate = task.getDue();
		if(dueDate != null) {
			Calendar due = Calendar.getInstance();
			due.setTime(dueDate);
			int year = due.get(Calendar.YEAR);
			int month = due.get(Calendar.MONTH);
			int day = due.get(Calendar.DAY_OF_MONTH);
			this.dateSetListener.onDateSet(null, year, month, day);
			if(task.getHasDueTime()) {
				int minutes = due.get(Calendar.MINUTE);
				int hour = due.get(Calendar.HOUR_OF_DAY);
				this.timeSetListener.onTimeSet(null, hour, minutes);
			}
		}
		//estimate
		Estimate[] estimates = task.getEstimateDetail();
		for(Estimate estimate : estimates)
			switch(estimate.getUnit()) {
			case DAYS :
				this.estimateDayInput.setText("" + estimate.getQuantity());
				break;
			case HOURS :
				this.estimateHourInput.setText("" + estimate.getQuantity());
				break;
			case MINUTES :
				this.estimateMinuteInput.setText("" + estimate.getQuantity());
				break;
			}
		//repeat
		if(task.getRecurrence() != null) {
			Recurrence rec = task.getRecurrence();
			if(rec.isEvery()) this.repeatSpinner1.setSelection(1);
			else this.repeatSpinner1.setSelection(2);
			this.repeatSpinner2.setSelection(rec.getInterval());
			switch(rec.getFrequency()) {
			case DAILY :
				this.repeatSpinner3.setSelection(1);
				break;
			case WEEKLY :
				this.repeatSpinner3.setSelection(2);
				break;
			case MONTHLY :
				this.repeatSpinner3.setSelection(3);
				break;
			case YEARLY :
				this.repeatSpinner3.setSelection(4);
				break;
			default :
				this.repeatSpinner3.setSelection(0);
				break;
			}
			if(task.getRecurrence() != null) {
				if(task.getRecurrence().hasOption()) {
					TaskFormat tf = new TaskFormat(this.dateFormatStrings);
					String[] strings = this.getResources().getStringArray(R.array.repeat_option_strings);
					String[] weekdaysString = this.getResources().getStringArray(R.array.weekdays);
					String[] ordinals1 = this.getResources().getStringArray(R.array.ordinals1);
					String[] ordinals2 = this.getResources().getStringArray(R.array.ordinals2);
					String option = tf.formatRepeatOption(strings, 
							ordinals1, ordinals2, weekdaysString, task.getRecurrence());
					this.repeatOptionInput.setText(option);
				}
				else
					this.repeatOptionInput.setText("-");
				
			}
		}
		//url
		if(task.getUrl() != null) {
			this.urlInput.setText(task.getUrl());
		}
		//participants
		Contact[] contacts = taskToBeEdited.getParticipants();
		if(contacts.length != 0) {
			StringBuilder sb = new StringBuilder("");
			//sb.append(this.getResources().getString(R.string.sharedWith));
			for(int i = 0; i < contacts.length; i++) {
				Contact contact = contacts[i];
				if(i == 0) sb.append(contact.getFullname());
				else sb.append(", " + contact.getFullname());	
			}
			participantsEditText.append(sb.toString());
		}
		//NOTES
		for(Note note : this.taskToBeEdited.getNotes()) {
			fillNoteLayout(note);
		}

	}

	private void fillNoteLayout(Note note) {
		LayoutInflater inflater 
		= (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View noteView = inflater.inflate(R.layout.note_row, null);
		TextView titleTextView = (TextView) noteView.findViewById(R.id.noteTitle);
		titleTextView.append(note.getTitle());
		TextView textTextView = (TextView) noteView.findViewById(R.id.noteText);
		textTextView.append(note.getText());
		tableLayout.addView(noteView);
		noteViewMap.put(note.getId(), noteView);
		View noteBody = noteView.findViewById(R.id.noteBody);
		noteBody.setOnClickListener(new SmartClickListener<Note>(note) {
			@Override public void onClick(View v) {
				Note n = this.get();
				Bundle bundle = new Bundle();
				bundle.putString("title", n.getTitle());
				bundle.putString("text", n.getText());
				bundle.putString("id", n.getId());
				TaskEditActivity.this.showDialog(NOTE_EDIT, bundle);
				//Toast.makeText(TaskEditActivity.this, R.string.coming_soon, Toast.LENGTH_SHORT).show();
			}
		});
		View deleteButton = noteView.findViewById(R.id.deleteNoteIcon);
		deleteButton.setOnClickListener(new SmartClickListener<Note>(note) {
			@Override public void onClick(View v) {
				String OK = TaskEditActivity.this.getResources().getString(R.string.note_deleted_OK);
				String NOK = TaskEditActivity.this.getResources().getString(R.string.note_deleted_NOK);
				//final String id = this.get().getId();
				NoteDeleter nd = new NoteDeleter(OK, NOK, TaskEditActivity.this);
				nd.executeInBackground(this.get().getId()); 
				//Toast.makeText(TaskEditActivity.this, R.string.coming_soon, Toast.LENGTH_SHORT).show();
			}
		});
		
	}

	private void loadTaskEditor(MultipleTaskEditor multipleEditor) {
		//name
		String name = this.nameInput.getText().toString().trim();
		if(!name.equals(this.taskToBeEdited.getName()))
			multipleEditor.add(new NameChanger(this, taskToBeEdited, name));
		//priority
		int priorityPos = this.prioritySpinner.getSelectedItemPosition();
		Priority priority = this.taskToBeEdited.getPriority();
		Priority newPriority = Priority.NONE;
		if(priorityPos == 1) newPriority = Priority.LOW;
		if(priorityPos == 2) newPriority = Priority.MEDIUM;
		if(priorityPos == 3) newPriority = Priority.HIGH;
		if(!newPriority.equals(priority))
			multipleEditor.add(new PriorityChanger(this, taskToBeEdited, "" + newPriority.getLevel()));
		//list
		String listId = this.selectedListId;
		if(listId == null) listId = "";
		if(!listId.equals(this.taskToBeEdited.getListId())) {
			multipleEditor.add(new TaskListChanger(this, taskToBeEdited, 
					this.taskToBeEdited.getListId(), listId));
		}
		Task taskTBE2 = TaskCloner.clone(taskToBeEdited);
		if(listId != null && !listId.equals(""))
			taskTBE2.setListId(selectedListId);
		//location	
		String locationId = this.selectedLocationId;
		if(locationId == null || locationId.equals("-")) locationId = "";
		if(!locationId.equals(taskTBE2.getLocationId()))
			multipleEditor.add(new LocationChanger(this, taskTBE2, 
					listId, locationId));
		//tags
		String tmp = this.tagInput.getText().toString().trim();
		String[] insertedTags = new String[0];
		if(!tmp.equals(""))
			insertedTags = tmp.split("\\s+");
		List<String> tags = Arrays.asList(this.taskToBeEdited.getTags());
		Collections.sort(tags);
		List<String> newTags = Arrays.asList(insertedTags);
		Collections.sort(newTags);
		List<String> tagsToBeRemoved = new ArrayList<String>();
		List<String> tagsToBeAdded = new ArrayList<String>();
		for(String tag : newTags) {
			if(Collections.binarySearch(tags, tag) < 0)
				tagsToBeAdded.add(tag);
		}
		for(String tag : tags) {
			if(Collections.binarySearch(newTags, tag) < 0)
				tagsToBeRemoved.add(tag);
		}
		if(tagsToBeAdded.size() > 0)
			multipleEditor.add(new TagAdder(this, taskTBE2, tagsToBeAdded.toArray()));
		if(tagsToBeRemoved.size() > 0)
			multipleEditor.add(new TagRemover(this, taskTBE2, tagsToBeRemoved.toArray()));
		//date&time
		Calendar currentDate = Calendar.getInstance();
		currentDate.set(Calendar.MILLISECOND, 0);
		if(this.day != null && this.month != null && this.year != null) {
			if(this.hour != null && this.minute != null)
				currentDate.set(this.year, this.month, this.day, this.hour, this.minute, 0);
			else
				currentDate.set(this.year, this.month, this.day, 0, 0, 0);
		}
		else {
			if(this.hour != null && this.minute != null) {
				currentDate = Calendar.getInstance();
				currentDate.set(Calendar.HOUR_OF_DAY, this.hour);
				currentDate.set(Calendar.MINUTE, this.minute);
				currentDate.set(Calendar.SECOND, 0);
			}
			else currentDate = null;
		}
		boolean dueChanged = false;
		if(currentDate == null) {
			if(this.taskToBeEdited.getDue() != null) dueChanged = true;
		}
		else {
			if(!currentDate.getTime().equals(this.taskToBeEdited.getDue()))
				dueChanged = true;
		}
		if(dueChanged){
			boolean hasDueTime = false;
			if(this.hour != null && this.minute != null)
				hasDueTime = true;
			Date due = null;
			if(currentDate != null)
				due = currentDate.getTime();
			multipleEditor.add(new DueDateChanger(this, taskTBE2, due, hasDueTime));
		}
		//estimate
		Estimate[] estimates = this.taskToBeEdited.getEstimateDetail();
		float dayEstimOld = 0f;
		float hourEstimOld = 0f;
		float minEstimOld = 0f;
		for(Estimate estimate : estimates) {
			switch(estimate.getUnit()) {
			case DAYS :
				dayEstimOld = estimate.getQuantity();
				break;
			case HOURS :
				hourEstimOld = estimate.getQuantity();
				break;
			case MINUTES :
				minEstimOld = estimate.getQuantity();
				break;
			}
		}
		float dayEstimNew = 0f;
		float hourEstimNew = 0f;
		float minEstimNew = 0f;
		try {
			dayEstimNew = Float.parseFloat(this.estimateDayInput.getText().toString());
		} catch(Exception e) {}
		try {
			hourEstimNew = Float.parseFloat(this.estimateHourInput.getText().toString());
		} catch(Exception e) {}
		try {
			minEstimNew = Float.parseFloat(this.estimateMinuteInput.getText().toString());
		} catch(Exception e) {}
		boolean estimateChanged = false;
		if(dayEstimOld != dayEstimNew)
			estimateChanged = true;
		if(hourEstimOld != hourEstimNew)
			estimateChanged = true;
		if(minEstimOld != minEstimNew)
			estimateChanged = true;
		if(estimateChanged) {
			if(minEstimNew == 0 && hourEstimNew == 0f && dayEstimNew == 0)
				multipleEditor.add(new EstimateChanger(this, taskTBE2, ""));
			else {
				String estimateString = 
						SmartAddFormat.formatEstimate(minEstimNew, hourEstimNew, dayEstimNew);
				multipleEditor.add(new EstimateChanger(this, taskTBE2, estimateString));
			}
		}
		//repeat
		boolean repeatChanged = false;
		boolean repeatOptionChanged = false;
		Recurrence rec = this.taskToBeEdited.getRecurrence();
		if(rec != null) {
			if(rec.isEvery()
					&& this.repeatSpinner1.getSelectedItemPosition() != 1)
				repeatChanged = true;
			if(!repeatChanged && !rec.isEvery()
					&& this.repeatSpinner1.getSelectedItemPosition() != 2)
				repeatChanged = true;
			if(!repeatChanged && 
					rec.getInterval() != this.repeatSpinner2.getSelectedItemPosition())
				repeatChanged = true;
			if(!repeatChanged) {
				switch(rec.getFrequency()) {
				case DAILY :
					if(this.repeatSpinner3.getSelectedItemPosition() != 1)
						repeatChanged = true;
					break;
				case WEEKLY :
					if(this.repeatSpinner3.getSelectedItemPosition() != 2)
						repeatChanged = true;
					break;
				case MONTHLY :
					if(this.repeatSpinner3.getSelectedItemPosition() != 3)
						repeatChanged = true;
					break;
				case YEARLY :
					if(this.repeatSpinner3.getSelectedItemPosition() != 4)
						repeatChanged = true;
					break;
				}
			}
			
			if(rec.hasOption()) {
				if(rec.getOption() != newRecurrenceOption ||
					rec.getOptionValue() != newRecurrenceString)
					repeatOptionChanged = true;
			}
			else {
				if(newRecurrenceOption != null ||
					newRecurrenceString != null)
					repeatOptionChanged = true;
			}
		}
		else {
			if(this.repeatSpinner1.getSelectedItemPosition() != 0 ||
				this.repeatSpinner2.getSelectedItemPosition() != 0 ||
				this.repeatSpinner3.getSelectedItemPosition() != 0)
				repeatChanged = true;
				
			if(newRecurrenceOption != null && newRecurrenceString != null)
				repeatOptionChanged = true;			
		}
		
		StringBuilder repeatString = new StringBuilder("");
		if(repeatChanged) { //repeat changed
			if(this.repeatSpinner1.getSelectedItemPosition() != 0 &&
					this.repeatSpinner2.getSelectedItemPosition() != 0 &&
					this.repeatSpinner3.getSelectedItemPosition() != 0) { //changed to valid
				boolean every = this.repeatSpinner1.getSelectedItemPosition() == 1 ? 
						true : false;
				int interval = this.repeatSpinner2.getSelectedItemPosition();
				Frequency freq;
				switch(this.repeatSpinner3.getSelectedItemPosition()) {
				case 1 : freq = Frequency.DAILY; break;
				case 2 : freq = Frequency.WEEKLY; break;
				case 3 : freq = Frequency.MONTHLY; break;
				case 4 : freq = Frequency.YEARLY; break;
				default : freq = Frequency.YEARLY; break;
				}
				repeatString.append(TaskFormat.formatRepeatFrequencyInEnglish(freq, interval, every));
				if(newRecurrenceOption != null && newRecurrenceString != null)
					repeatString.append(" " + TaskFormat.formatRepeatOptionInEnglish(newRecurrenceOption, newRecurrenceString));
				else repeatString.append("");
				multipleEditor.add(new RecurrenceChanger(this, taskTBE2, repeatString.toString()));
			}
			else { //changed to invalid
				if(taskTBE2.getRecurrence() != null)
					//repeatString.append(TaskFormat.formatRepeatOptionInEnglish(taskTBE2.getRecurrence()));
					multipleEditor.add(new RecurrenceChanger(this, taskTBE2, repeatString.toString()));
			}	
		}
		else { //repeat not changed (option?)
			if(repeatOptionChanged && rec != null) {
				repeatString.append(TaskFormat.formatRepeatFrequencyInEnglish(rec));
				if(newRecurrenceOption != null && newRecurrenceString != null)
					repeatString.append(" " + TaskFormat.formatRepeatOptionInEnglish(newRecurrenceOption, newRecurrenceString));
				else repeatString.append("");
				multipleEditor.add(new RecurrenceChanger(this, taskTBE2, repeatString.toString()));
			}
		}
		//Toast.makeText(this, repeatString.toString(), Toast.LENGTH_LONG).show();

		//url
		String url = this.urlInput.getText().toString().trim();
		if(!url.equals(this.taskToBeEdited.getUrl()))
			multipleEditor.add(new UrlChanger(this, taskTBE2, SmartAddFormat.formatUrl(url)));
		multipleEditor.execute();
	}
	
	private class TaskEditActivityMessageReceiver extends DetailMessageReceiver{
	
		public TaskEditActivityMessageReceiver(SherlockActivity activity) {
			super(activity);
		}
		
		@Override public void onTaskChanged(Context context, List<String> changedIds) {
			super.onTaskChanged(context, changedIds);
			if(TaskEditActivity.this.taskToBeEdited != null) {
				for(String id : changedIds) {
					if(id.equals(TaskEditActivity.this.taskToBeEdited.getId())) {
						TaskEditActivity.this.setResult(RESULT_OK);
						TaskEditActivity.this.finish();
						Toast.makeText(TaskEditActivity.this, R.string.taskModified,
								Toast.LENGTH_SHORT).show();
						break;
					}
				}
			}
		}
		
		@Override protected void onNoteAdded(Context c, Note note, String taskId) {
			if(taskToBeEdited.getId().equals(taskId)) {
				isNoteChanged = true;
				fillNoteLayout(note);
				Note[] notes = taskToBeEdited.getNotes();
				int noteLength = notes.length;
				Note[] newNotes = new Note[noteLength + 1];
				for(int i = 0; i < noteLength; i++)
					newNotes[i] = notes[i];
				newNotes[noteLength] = note;
				taskToBeEdited.setNotes(newNotes);
			}
		}
		
		@Override protected void onNoteDeleted(Context c, String deletedNoteId) {
			View noteView = noteViewMap.get(deletedNoteId);
			if(noteView != null) {
				isNoteChanged = true;
				noteViewMap.remove(deletedNoteId);
				tableLayout.removeView(noteView);
				Note[] notes = taskToBeEdited.getNotes();
				int noteLength = notes.length;
				Note[] newNotes = new Note[noteLength - 1];
				for(int i = 0, j = 0; i < noteLength; i++) {
					if(!notes[i].getId().equals(deletedNoteId))
						newNotes[j++] = notes[i];
				}
				taskToBeEdited.setNotes(newNotes);
			}
		}
		
		@Override protected void onNoteEdited(Context c, Note note) {
			View noteView = noteViewMap.get(note.getId());
			if(noteView != null) {
				isNoteChanged = true;
				TextView titleView = (TextView) noteView.findViewById(R.id.noteTitle);
				titleView.setText(note.getTitle());
				TextView textView = (TextView) noteView.findViewById(R.id.noteText);
				textView.setText(note.getText());
				View noteBody = noteView.findViewById(R.id.noteBody);
				noteBody.setOnClickListener(new SmartClickListener<Note>(note) {
					@Override public void onClick(View v) {
						Note n = this.get();
						Bundle bundle = new Bundle();
						bundle.putString("title", n.getTitle());
						bundle.putString("text", n.getText());
						bundle.putString("id", n.getId());
						TaskEditActivity.this.showDialog(NOTE_EDIT, bundle);
					}
				});
				Note[] notes = taskToBeEdited.getNotes();
				int noteLength = notes.length;
				Note[] newNotes = new Note[noteLength];
				for(int i = 0; i < noteLength; i++)
					if(notes[i].getId().equals(note.getId()))
						newNotes[i] = note;
					else
						newNotes[i] = notes[i];
				taskToBeEdited.setNotes(newNotes);
			}	
		}
		
		@Override public void onTasklistsUpdated(Context context) {
			super.onTasklistsUpdated(context);
			TaskEditActivity.this.refreshTaskLists();
		}
		
		@Override public void onLocationsUpdated(Context context) {
			super.onLocationsUpdated(context);
			TaskEditActivity.this.refreshLocations();
		}

	}
	
	private void refreshTaskLists() {
		DBTaskListsGetter tlg = new DBTaskListsGetter(TaskEditActivity.this) {
			@Override protected void onPostExecute(List<TaskList> tasklists) {
				List<TaskList> tmpLists = new ArrayList<TaskList>();
				for(TaskList list : tasklists) {
					if(!list.isSmart()) tmpLists.add(list);
				}
				Collections.sort(tmpLists, new TaskListComparator());
				TaskEditActivity.this.tasklistAdapter.reload(tmpLists);
				int selectedListPosition = 0;
				if(TaskEditActivity.this.selectedListId != null)
					selectedListPosition =
						TaskEditActivity.this.tasklistAdapter.findPositionByRtmObjectId(selectedListId);
				else 
					selectedListPosition = 
						TaskEditActivity.this.tasklistAdapter.findPositionByRtmObjectId(taskToBeEdited.getListId());
				TaskEditActivity.this.tasklistAdapter.notifyDataSetChanged();
				if(selectedListPosition >= 0)
					TaskEditActivity.this.listSpinner.setSelection(selectedListPosition);
			}
		};
		tlg.execute();
	}
	
	private void refreshLocations() {
		DBLocationsGetter lg = new DBLocationsGetter(TaskEditActivity.this) {
			@Override protected void onPostExecute(List<Location> locations) {
				List<Location> tmpLocs = new ArrayList<Location>();
				tmpLocs.addAll(locations);
				Collections.sort(tmpLocs, new LocationComparator());
				tmpLocs.add(0, new Location("", "-", 0, 0, "-", false, 0));
				TaskEditActivity.this.locationAdapter.reload(tmpLocs);
				int selectedLocPosition = 0;
				if(TaskEditActivity.this.selectedLocationId != null)
					selectedLocPosition =
						TaskEditActivity.this.locationAdapter.findPositionByRtmObjectId(selectedLocationId);
				else 
					selectedLocPosition =
						TaskEditActivity.this.locationAdapter.findPositionByRtmObjectId(taskToBeEdited.getLocationId());
				TaskEditActivity.this.locationAdapter.notifyDataSetChanged();
				if(selectedLocPosition >= 0)
					TaskEditActivity.this.locationSpinner.setSelection(selectedLocPosition);
			}
		};
		lg.execute();
	}
	
}
