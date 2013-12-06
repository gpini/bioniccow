package it.bova.bioniccow;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.actionbarsherlock.app.SherlockFragment;

import it.bova.bioniccow.asyncoperations.rtmobjects.DBLocationsGetter;
import it.bova.bioniccow.asyncoperations.rtmobjects.DBTagGetter;
import it.bova.bioniccow.asyncoperations.rtmobjects.DBTaskListsGetter;
import it.bova.bioniccow.asyncoperations.rtmobjects.TaskAdder;
import it.bova.bioniccow.asyncoperations.sync.SyncHelper;
import it.bova.bioniccow.data.Folder;
import it.bova.bioniccow.data.Folders_old2;
import it.bova.bioniccow.data.Locations_old2;
import it.bova.bioniccow.data.Preferences;
import it.bova.bioniccow.data.Tags_old2;
import it.bova.bioniccow.data.Preferences.PrefParameter;
import it.bova.bioniccow.data.TaskLists_old2;
import it.bova.bioniccow.data.observers.FolderObserver;
import it.bova.bioniccow.data.observers.LocationObserver;
import it.bova.bioniccow.data.observers.TagObserver;
import it.bova.bioniccow.data.observers.TaskListObserver;
import it.bova.bioniccow.utilities.Label;
import it.bova.bioniccow.utilities.LabelAdapter;
import it.bova.bioniccow.utilities.LabelAutoCompleteTextView;
import it.bova.bioniccow.utilities.SimpleDatePickerDialog;
import it.bova.bioniccow.utilities.LabelAutoCompleteTextView.OnTextChangedListener;
import it.bova.bioniccow.utilities.rtmobjects.SmartDateFormat;
import it.bova.bioniccow.utilities.NetAvailabilityTask;
import it.bova.bioniccow.utilities.SpaceTokenizer;
import it.bova.rtmapi.Location;
import it.bova.rtmapi.TaskList;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HeaderFragment extends SherlockFragment implements InterProcess {

	private View defocusingView;
	private LabelAutoCompleteTextView quickTaskEditText;
	private View quickAddButton;
	boolean isAutocompleteOn = false;
	private LabelAdapter labelAdapter;
	private LabelAdapter emptyLabelAdapter;
	private View dueAndRepeatLayout;

	private SyncHelper syncHelper;
	private TextView syncInfo;

	private TaskLists_old2 tasklists;
	private TaskListObserver listObserver;
	private Map<String,TaskList> listMap;
	private Locations_old2 locations;
	private LocationObserver locationObserver;
	private Map<String,Location> locMap;
	private Tags_old2 tags;
	private TagObserver tagObserver;
	private Folders_old2 folders;
	private FolderObserver folderObserver;
	private List<Label> listLabels;
	private List<Label> locationLabels;
	private List<Label> tagLabels;
	private List<Label> folderLabels;

	//Resources
	private String lastSynchPhrase;

	@Override public void onCreate(Bundle savedInstanceState) {
		this.setRetainInstance(true);
		super.onCreate(savedInstanceState);
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_header, container, false);

		//views
		this.quickTaskEditText = (LabelAutoCompleteTextView) view.findViewById(R.id.quickTaskEditText1);
		this.quickAddButton = (ImageView) view.findViewById(R.id.quickAddButton);
		this.defocusingView = (View) view.findViewById(R.id.defocusingView);
		this.dueAndRepeatLayout = view.findViewById(R.id.dueAndRepeatLayout);
		this.syncInfo = (TextView) view.findViewById(R.id.syncInfo);

		this.labelAdapter = new LabelAdapter(this.getSherlockActivity(), new ArrayList<Label>(), R.layout.dropdown_labels);
		this.emptyLabelAdapter = new LabelAdapter(this.getSherlockActivity(), new ArrayList<Label>(), R.layout.dropdown_labels);

		this.quickAddButton.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) {
				HeaderFragment.this.addTask();
			}
		});

		this.quickTaskEditText.setTokenizer(new SpaceTokenizer());
		this.quickTaskEditText.setAdapter(this.emptyLabelAdapter);
		this.quickTaskEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					HeaderFragment.this.addTask();
					return true;	
				}
				return false;
			}
		});
		this.quickTaskEditText.setOnTextChangedListener(new OnTextChangedListener() {
			@Override protected void onTextChange(CharSequence text, int start, int before, int after) {
				String string = text.toString();
				boolean wasAutocompleteOn = isAutocompleteOn;
				if(string.matches("^\\s*\\S+\\s+.*"))
					isAutocompleteOn = true;
				else isAutocompleteOn = false;
				if(wasAutocompleteOn != isAutocompleteOn) {
					if(isAutocompleteOn) {
						dueAndRepeatLayout.setVisibility(View.VISIBLE);
						quickTaskEditText.setAdapter(labelAdapter);
					}
					else  {
						quickTaskEditText.setAdapter(emptyLabelAdapter);
						dueAndRepeatLayout.setVisibility(View.GONE);
					}
				}
			}
		});
		this.quickTaskEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus)
					HeaderFragment.this.quickAddButton.setVisibility(View.VISIBLE);
				else
					HeaderFragment.this.quickAddButton.setVisibility(View.GONE);
			}
		});

		Button dueButton = (Button) view.findViewById(R.id.dueButton);
		dueButton.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) {
				HeaderFragment.this.showDueDateDialog();
			}
		});
		Button repeatButton = (Button) view.findViewById(R.id.repeatButton);
		repeatButton.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) {
				HeaderFragment.this.showRepeatDialog();
			}
		});
		Button priorityButton = (Button) view.findViewById(R.id.priorityButton);
		priorityButton.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) {
				HeaderFragment.this.append(" !");
			}
		});

		//"Sveglia" le strutture
		this.folders = new Folders_old2(this.getSherlockActivity());	

		this.listLabels = new ArrayList<Label>();
		this.locationLabels = new ArrayList<Label>();
		this.tagLabels = new ArrayList<Label>();
		this.folderLabels = new ArrayList<Label>();

		//resources
		this.lastSynchPhrase = this.getResources().getString(R.string.last_synch);

		this.syncHelper = new SyncHelper(this.getSherlockActivity()) {
			@Override protected void onStopSynching() {
				DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
				Preferences pref = new Preferences(HeaderFragment.this.getSherlockActivity());
				Date lastSynch = new Date(pref.getLong(PrefParameter.LAST_SYNCH, 0));
				if(lastSynch.getTime() == 0L) {
					String never = HeaderFragment.this.getResources().getString(R.string.neverSynched);
					syncInfo.setText(lastSynchPhrase + never);
				}
				else
					syncInfo.setText(lastSynchPhrase + df.format(lastSynch));
			}
		};

		return view;


	}

	@Override public void onResume() {
		super.onResume();  
		this.syncHelper.attach();

		this.defocusingView.requestFocus();
		if(quickTaskEditText.hasFocus())
			this.showKeyboard();

		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
		Preferences pref = new Preferences(this.getSherlockActivity());
		Date lastSynch = new Date(pref.getLong(PrefParameter.LAST_SYNCH, 0));
		if(lastSynch.getTime() == 0L) {
			String never = HeaderFragment.this.getResources().getString(R.string.neverSynched);
			syncInfo.setText(lastSynchPhrase + never);
		}
		else
			syncInfo.setText(lastSynchPhrase + df.format(lastSynch));

		this.refresh();
			
		this.folderObserver = new FolderObserver() {
			@Override public void onDataChanged(List<Folder> folderList) {
				List<String> folderNameList = new ArrayList<String>();
				for(Folder folder : folderList)
					folderNameList.add(folder.getName());
				//Update AutocompleteTextView
				HeaderFragment.this.folderLabels = new ArrayList<Label>();
				for(Folder folder : folderList) {
					String rule = folder.getRule();
					List<String> tagElements = folder.getTagElements();
					for(String tag : tagElements) {
						String unruledTag = tag.substring(rule.length());
						HeaderFragment.this.folderLabels.add(new Label("#" + rule, unruledTag));
					}
					List<String> listElements = folder.getListElements();
					for(String listId : listElements) {
						TaskList tasklist = listMap.get(listId);
						if(tasklist == null || tasklist.isSmart()) continue;
						String list = tasklist.getName();
						String unruledList = list.substring(rule.length());
						HeaderFragment.this.folderLabels.add(new Label("#" + rule, unruledList));
					}
					List<String> locElements = folder.getLocationElements();
					for(String locId : locElements) {
						Location location = locMap.get(locId);
						if(location == null) continue;
						String loc = location.getName();
						String unruledLoc = loc.substring(rule.length());
						HeaderFragment.this.folderLabels.add(new Label("@" + rule, unruledLoc));
					}
				}
				HeaderFragment.this.reloadLabels();
			}
		};
		this.folders.addObserver(folderObserver);

		this.folders.retrieve();
		this.folders.notifyObservers();
		
		


	}

	@Override public void onPause() {
		super.onPause();
		this.syncHelper.detach();

		this.folders.removeObserver(folderObserver);
	}


	public void addTask() {
		NetAvailabilityTask nat = new NetAvailabilityTask(this.getSherlockActivity(), 2000);
		if(nat.isConnected()) {
			String taskName = this.quickTaskEditText.getText().toString();
			String OK = this.getResources().getString(R.string.task_added_OK);
			String NOK = this.getResources().getString(R.string.task_added_NOK);
			TaskAdder ta = new TaskAdder(OK, NOK, this.getSherlockActivity());
			ta.executeInBackground(taskName);
			Toast.makeText(this.getSherlockActivity(), R.string.adding, Toast.LENGTH_SHORT).show();
			//clear and unfocus editText
			
			this.quickTaskEditText.setText("");
		}
		else {
			Toast.makeText(this.getSherlockActivity(), R.string.connectionWarningQuickAdd, Toast.LENGTH_LONG).show();
		}
		this.hideKeyboard();
	}

	protected void reloadLabels() {
		List<Label> labels = new ArrayList<Label>();
		labels.add(new Label("", "!1"));
		labels.add(new Label("", "!2"));
		labels.add(new Label("", "!3"));
		labels.addAll(listLabels);
		labels.addAll(locationLabels);
		labels.addAll(tagLabels);
		labels.addAll(folderLabels);
		this.labelAdapter.reloadAndNotify(labels);

	}
	
	public void refresh() {
		//TODO Folder Update
		final DBTagGetter tg = new DBTagGetter(this.getSherlockActivity()) {
			@Override protected void onPostExecute(Set<String> tags) {
				HeaderFragment.this.tagLabels = new ArrayList<Label>();
				for(String tag : tags)
					HeaderFragment.this.tagLabels.add(new Label("#", tag));
				HeaderFragment.this.reloadLabels();
			}
		};
		final DBLocationsGetter lg = new DBLocationsGetter(this.getSherlockActivity()) {
			@Override protected void onPostExecute(List<Location> locations) {
				HeaderFragment.this.locationLabels = new ArrayList<Label>();
				for(Location loc : locations)
					HeaderFragment.this.locationLabels.add(new Label("@", loc.getName()));
				tg.execute();
			}
		};
		DBTaskListsGetter tlg = new DBTaskListsGetter(this.getSherlockActivity()) {
			@Override protected void onPostExecute(List<TaskList> tasklists) {
				HeaderFragment.this.listLabels = new ArrayList<Label>();
				for(TaskList list : tasklists)
					if(!list.isSmart())
						HeaderFragment.this.listLabels.add(new Label("#", list.getName()));
				lg.execute();
			}
		};	
		tlg.execute();
	}

	public void setText(String text) {
		this.quickTaskEditText.setText(text);
	}

	public void append(String text) {
		this.quickTaskEditText.append(text);
	}

	public void showKeyboard() {
		InputMethodManager imm = (InputMethodManager) this.getSherlockActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		//this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		//this.quickTaskEditText.requestFocus();
		imm.showSoftInputFromInputMethod(quickTaskEditText.getWindowToken(), 0);
	}

	public void hideKeyboard() {
		//hide keyboard, if present and remove focus
		InputMethodManager imm = (InputMethodManager) this.getSherlockActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(this.quickTaskEditText.getWindowToken(), 0);
		this.defocusingView.requestFocus();
	}

	private void showDueDateDialog() {
		DialogFragment newFragment = DueDateDialogFragment.newInstance();
		newFragment.show(getFragmentManager(), "due_dialog");
	}

	private void showRepeatDialog() {
		DialogFragment newFragment = RepeatDialogFragment.newInstance();
		newFragment.show(getFragmentManager(), "repeat_dialog");
	}

	public static class DueDateDialogFragment extends DialogFragment {

		private String[] dateFormatStrings;

		public static DueDateDialogFragment newInstance() {
			DueDateDialogFragment frag = new DueDateDialogFragment();
			return frag;
		}

		@Override public Dialog onCreateDialog(Bundle savedInstanceState) {
			this.dateFormatStrings = this.getResources().getStringArray(R.array.smart_date_format_labels);
			Calendar cal = Calendar.getInstance();
			SimpleDatePickerDialog sdpd = new SimpleDatePickerDialog(this.getActivity(),
					dateFormatStrings,
					new DatePickerDialog.OnDateSetListener() {
				public void onDateSet(DatePicker view, int year, int month, int day) {
					Calendar cal = Calendar.getInstance();
					cal.set(year, month, day);
					String smartDate = " ^" + SmartDateFormat.formatInEnglish(cal.getTime()) + " ";
					((MainActivity) DueDateDialogFragment.this.getActivity()).appendToHeaderText(smartDate);
					DueDateDialogFragment.this.dismiss();
				}
			},
			cal.get(Calendar.YEAR), 
			cal.get(Calendar.MONTH),
			cal.get(Calendar.DAY_OF_MONTH));
			return sdpd;
		}
		
		@Override public void onDismiss(DialogInterface di) {
			InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);	
		}
	}


	public static class RepeatDialogFragment extends DialogFragment {

		public static RepeatDialogFragment newInstance() {
			RepeatDialogFragment frag = new RepeatDialogFragment();
			return frag;
		}

		@Override public Dialog onCreateDialog(Bundle savedInstanceState) {
			String[] repeatHints = this.getResources().getStringArray(R.array.repeat_hints);
			final String[] repeatHintsInEnglish = this.getResources().getStringArray(R.array.repeat_hints_english);
			AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity())
			.setCancelable(true)
			.setAdapter(new ArrayAdapter<String>(this.getActivity(), R.layout.dropdown_simple, repeatHints),
					new  DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) {
					((MainActivity) RepeatDialogFragment.this.getActivity()).appendToHeaderText(" *" + repeatHintsInEnglish[which] + " ");
				}
			});
			Dialog dialog = builder.create();
			return dialog;
		}

	}


}
