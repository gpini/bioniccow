package it.bova.bioniccow;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import it.bova.bioniccow.asyncoperations.DefaultMessageReceiver;
import it.bova.bioniccow.asyncoperations.MessageReceiver;
import it.bova.bioniccow.asyncoperations.rtmobjects.TaskAdder;
import it.bova.bioniccow.asyncoperations.sync.SyncHelper;
import it.bova.bioniccow.data.ApiSingleton;
import it.bova.bioniccow.data.Folders;
import it.bova.bioniccow.data.Folder;
import it.bova.bioniccow.data.Locations;
import it.bova.bioniccow.data.Preferences;
import it.bova.bioniccow.data.Tags;
import it.bova.bioniccow.data.TaskLists;
import it.bova.bioniccow.data.Preferences.PrefParameter;
import it.bova.bioniccow.data.observers.FolderObserver;
import it.bova.bioniccow.data.observers.LocationObserver;
import it.bova.bioniccow.data.observers.TagObserver;
import it.bova.bioniccow.data.observers.TaskListObserver;
import it.bova.bioniccow.utilities.Label;
import it.bova.bioniccow.utilities.LabelAdapter;
import it.bova.bioniccow.utilities.LabelAutoCompleteTextView;
import it.bova.bioniccow.utilities.LabelAutoCompleteTextView.OnTextChangedListener;
import it.bova.bioniccow.utilities.NetAvailabilityTask;
import it.bova.bioniccow.utilities.SimpleDatePickerDialog;
import it.bova.bioniccow.utilities.SmartDialogInterfaceClickListener;
import it.bova.bioniccow.utilities.SmartFragmentManager_old;
import it.bova.bioniccow.utilities.SpaceTokenizer;
import it.bova.bioniccow.utilities.rtmobjects.SmartDateFormat;
import it.bova.rtmapi.Location;
import it.bova.rtmapi.TaskList;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class BionicCowActivity_old extends SherlockFragmentActivity implements InterProcess {

	private View popup;
	private View defocusingView;
	private LabelAutoCompleteTextView quickTaskEditText;
	private View quickAddButton;
	boolean isAutocompleteOn = false;
	private LabelAdapter labelAdapter;
	private LabelAdapter emptyLabelAdapter;
	private View dueAndRepeatLayout;

	private MessageReceiver messageReceiver = new DefaultMessageReceiver(this);

	private SyncHelper syncHelper;
	private TextView syncInfo;

	private TaskLists tasklists;
	private TaskListObserver listObserver;
	private Map<String,TaskList> listMap;
	private Locations locations;
	private LocationObserver locationObserver;
	private Map<String,Location> locMap;
	private Tags tags;
	private TagObserver tagObserver;
	private Folders folders;
	private FolderObserver folderObserver;
	private List<Label> listLabels;
	private List<Label> locationLabels;
	private List<Label> tagLabels;
	private List<Label> folderLabels;


	private SpinnerAdapter navAdapter;
	private OnNavigationListener navigationListener;
	private FragmentManager fm;
	private OnBackStackChangedListener backStackListener;
	private static final int LISTS = 0;
	private static final int LOCATIONS = 1;
	private static final int TAGS = 2;
	private static final int OVERVIEW = 3;
	private static final int FOLDERS = 4;
	private static final int SPECIALS = 5;

	//Resources
	private String lastSynchPhrase;
	private String[] dateFormatStrings;
	private String modifyFolder;

	private static final int SYNC_ERASE_CONFIRM = 2;
	private static final int DIALOG_VOTE_HINT = 3;
	private static final int DATE_DIALOG_ID = 4;
	private static final int REPEAT_DIALOG_ID = 5;
	private static final int DIALOG_EDIT_DELETE = 6;



	/** Called when the activity is first created. */
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		ActionBar ab = this.getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		ab.setDisplayShowHomeEnabled(true);
		ab.setDisplayShowTitleEnabled(false);
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		//Drawable actionbarBackground = this.getResources().getDrawable(R.drawable.action_bar_background);
		//ab.setBackgroundDrawable(actionbarBackground);

		//views
		this.popup = this.findViewById(R.id.popup);
		this.quickTaskEditText = (LabelAutoCompleteTextView) this.findViewById(R.id.quickTaskEditText1);
		this.quickAddButton = (ImageView) this.findViewById(R.id.quickAddButton);
		this.defocusingView = (View) this.findViewById(R.id.defocusingView);
		this.dueAndRepeatLayout = this.findViewById(R.id.dueAndRepeatLayout);

		this.labelAdapter = new LabelAdapter(this, new ArrayList<Label>(), R.layout.dropdown_labels);
		this.emptyLabelAdapter = new LabelAdapter(this, new ArrayList<Label>(), R.layout.dropdown_labels);

		this.quickTaskEditText.setTokenizer(new SpaceTokenizer());
		this.quickTaskEditText.setAdapter(this.emptyLabelAdapter);
		this.quickTaskEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					BionicCowActivity_old.this.addTask();
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
				if(hasFocus) BionicCowActivity_old.this.quickAddButton.setVisibility(View.VISIBLE);
				else BionicCowActivity_old.this.quickAddButton.setVisibility(View.GONE);
			}
		});

		//expandable list
		final String[] groupArray = new String[6];
		groupArray[OVERVIEW] = this.getResources().getString(R.string.overview);
		groupArray[LISTS] = this.getResources().getString(R.string.lists);
		groupArray[TAGS] = this.getResources().getString(R.string.tags);
		groupArray[FOLDERS] = this.getResources().getString(R.string.folders);
		groupArray[LOCATIONS] = this.getResources().getString(R.string.locations);
		groupArray[SPECIALS] = this.getResources().getString(R.string.specials);
		this.navAdapter = new NavigationAdapter(this, R.layout.navigation_spinner, groupArray);
		this.navigationListener = new ActionBar.OnNavigationListener() {
			@Override public boolean onNavigationItemSelected(int position, long itemId) {
				Preferences prefs = new Preferences(BionicCowActivity_old.this);
				prefs.putInteger(PrefParameter.LAST_NAVIGATION_OPTION, position);
				switch(position) {
				case LISTS :
					SmartFragmentManager_old.startFragment(BionicCowActivity_old.this, new TaskListFragment(),
							R.id.fragmentContainer, LIST_FRAGMENT);	
					return true;
				case LOCATIONS :
					SmartFragmentManager_old.startFragment(BionicCowActivity_old.this, new LocationFragment(),
							R.id.fragmentContainer, LOCATION_FRAGMENT);
					return true;
				case TAGS :
					SmartFragmentManager_old.startFragment(BionicCowActivity_old.this, new TagFragment(),
							R.id.fragmentContainer, TAG_FRAGMENT);
					return true;
				case FOLDERS:
					SmartFragmentManager_old.startFragment(BionicCowActivity_old.this, new FolderListFragment(),
							R.id.fragmentContainer, FOLDER_LIST_FRAGMENT);
					return true;
				case OVERVIEW :
					SmartFragmentManager_old.startFragment(BionicCowActivity_old.this, new TaskOverviewFragment(),
							R.id.fragmentContainer, OVERVIEW_FRAGMENT);
					return true;
				case SPECIALS :
					SmartFragmentManager_old.startFragment(BionicCowActivity_old.this, new SpecialsFragment(),
							R.id.fragmentContainer, SPECIAL_FRAGMENT);
					return true;
				default :
					return false;

				}
			}
		};
		ab.setListNavigationCallbacks(navAdapter, navigationListener);
		int position = new Preferences(this).getInteger(PrefParameter.LAST_NAVIGATION_OPTION, LISTS);
		ab.setSelectedNavigationItem(position);

		//"Sveglia" le strutture
		this.folders = new Folders(this);	
		this.tasklists = new TaskLists(this);
		this.locations = new Locations(this);
		this.tags = new Tags(this);
		this.listMap = new HashMap<String,TaskList>();
		this.locMap = new HashMap<String,Location>();

		this.listLabels = new ArrayList<Label>();
		this.locationLabels = new ArrayList<Label>();
		this.tagLabels = new ArrayList<Label>();
		this.folderLabels = new ArrayList<Label>();

		//resources
		this.dateFormatStrings = this.getResources().getStringArray(R.array.smart_date_format_labels);
		this.lastSynchPhrase = this.getResources().getString(R.string.last_synch);
		this.syncInfo = (TextView) this.findViewById(R.id.syncInfo);
		this.modifyFolder = this.getResources().getString(R.string.modify_folder);

		this.syncHelper = new SyncHelper(this) {
			@Override protected void onStopSynching() {
				super.onStopSynching();
				DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
				Date lastSynch = new Date(new Preferences(BionicCowActivity_old.this).getLong(PrefParameter.LAST_SYNCH, 0));
				if(lastSynch.getTime() == 0L) {
					String never = BionicCowActivity_old.this.getResources().getString(R.string.neverSynched);
					syncInfo.setText(lastSynchPhrase + never);
				}
				else
					syncInfo.setText(lastSynchPhrase + df.format(lastSynch));
			}
		};
		this.syncHelper.loadButtons();

		//Vote  hint
		boolean voteRequested = new Preferences(this).getBoolean(PrefParameter.VOTE_REQUESTED, false);
		if(!voteRequested) {
			long installationDate = new Preferences(this).getLong(PrefParameter.INSTALLATION_DATE, 0);
			Date now = new Date();
			if(installationDate == 0) {
				installationDate = now.getTime();
				new Preferences(this).putLong(PrefParameter.INSTALLATION_DATE, installationDate);	
			}
			if((now.getTime() - installationDate) > 7*24*60*60*1000) {
				this.showDialog(DIALOG_VOTE_HINT);
			}
		}

		//Navigation hint
		boolean navigationHintShowed = new Preferences(this).getBoolean(PrefParameter.NAVIGATION_HINT_SHOWED, false);
		if(!navigationHintShowed) {
			this.popup.setVisibility(View.VISIBLE);

			Button button = (Button) this.popup.findViewById(R.id.closePopup);
			button.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					popup.setVisibility(View.GONE);
					Preferences pref = new Preferences(BionicCowActivity_old.this);
					pref.putBoolean(PrefParameter.NAVIGATION_HINT_SHOWED, true);
				}
			});

		}

	}

	protected void onStart() {
		super.onStart();
		this.syncHelper.bindScheduler();
	}

	protected void onResume() {
		super.onResume();  
		this.registerReceiver(messageReceiver, new IntentFilter(ERROR_MESSENGER));
		this.syncHelper.attachToUI();

		this.fm = this.getSupportFragmentManager();
		this.backStackListener = new OnBackStackChangedListener() {
			@Override public void onBackStackChanged() {
				Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
				if(fragment != null) {
					String tag = fragment.getTag();
					if (tag != null) {
						if(tag.equals(FOLDER_FRAGMENT))
							BionicCowActivity_old.this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
						else
							BionicCowActivity_old.this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
					}
				}
			}	
		};
		this.fm.addOnBackStackChangedListener(this.backStackListener);

		this.defocusingView.requestFocus();

		if(quickTaskEditText.hasFocus()) {
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInputFromInputMethod(this.quickTaskEditText.getWindowToken(), 0);
		}

		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
		Date lastSynch = new Date(new Preferences(this).getLong(PrefParameter.LAST_SYNCH, 0));
		if(lastSynch.getTime() == 0L) {
			String never = BionicCowActivity_old.this.getResources().getString(R.string.neverSynched);
			syncInfo.setText(lastSynchPhrase + never);
		}
		else
			syncInfo.setText(lastSynchPhrase + df.format(lastSynch));

		this.listObserver = new TaskListObserver() {
			@Override public void onDataChanged(List<TaskList> lists) {
				BionicCowActivity_old.this.listMap = new HashMap<String,TaskList>();
				for(TaskList list : lists)
					BionicCowActivity_old.this.listMap.put(list.getId(), list);
				BionicCowActivity_old.this.listLabels = new ArrayList<Label>();
				for(TaskList list : lists)
					if(!list.isSmart())
						BionicCowActivity_old.this.listLabels.add(new Label("#", list.getName()));
				BionicCowActivity_old.this.reloadLabels();
			}	
		};
		this.tasklists.addObserver(listObserver);


		this.locationObserver = new LocationObserver() {
			public void onDataChanged(List<Location> locations) {
				BionicCowActivity_old.this.locMap = new HashMap<String,Location>();
				for(Location loc : locations)
					BionicCowActivity_old.this.locMap.put(loc.getId(), loc);
				BionicCowActivity_old.this.locationLabels = new ArrayList<Label>();
				for(Location loc : locMap.values())
					BionicCowActivity_old.this.locationLabels.add(new Label("@", loc.getName()));
				BionicCowActivity_old.this.reloadLabels();
			}	
		};
		this.locations.addObserver(locationObserver);

		this.tagObserver = new TagObserver() {
			public void onDataChanged(Set<String> tagSet) {
				BionicCowActivity_old.this.tagLabels = new ArrayList<Label>();
				for(String tag : tagSet)
					BionicCowActivity_old.this.tagLabels.add(new Label("#", tag));
				BionicCowActivity_old.this.reloadLabels();
			}	
		};
		this.tags.addObserver(tagObserver);

		this.folderObserver = new FolderObserver() {
			@Override public void onDataChanged(List<Folder> folderList) {
				List<String> folderNameList = new ArrayList<String>();
				//Set<String> folderNames = folderList;
				for(Folder folder/*Name*/ : folderList/*Names*/)
					folderNameList.add(folder.getName()/*Name*/);
				//Update AutocompleteTextView
				BionicCowActivity_old.this.folderLabels = new ArrayList<Label>();
				for(Folder folder : folderList) {
					String rule = folder.getRule();
					List<String> tagElements = folder.getTagElements();
					for(String tag : tagElements) {
						String unruledTag = tag.substring(rule.length());
						BionicCowActivity_old.this.folderLabels.add(new Label("#" + rule, unruledTag));
					}
					List<String> listElements = folder.getListElements();
					for(String listId : listElements) {
						TaskList tasklist = listMap.get(listId);
						if(tasklist == null || tasklist.isSmart()) continue;
						String list = tasklist.getName();
						String unruledList = list.substring(rule.length());
						BionicCowActivity_old.this.folderLabels.add(new Label("#" + rule, unruledList));
					}
					List<String> locElements = folder.getLocationElements();
					for(String locId : locElements) {
						Location location = locMap.get(locId);
						if(location == null) continue;
						String loc = location.getName();
						String unruledLoc = loc.substring(rule.length());
						BionicCowActivity_old.this.folderLabels.add(new Label("@" + rule, unruledLoc));
					}
				}
				BionicCowActivity_old.this.reloadLabels();
			}
		};
		this.folders.addObserver(folderObserver);

		this.tasklists.retrieve();
		this.tasklists.notifyObservers();
		this.locations.retrieve();
		this.locations.notifyObservers();
		this.tags.retrieve();
		this.tags.notifyObservers();
		this.folders.retrieve();
		this.folders.notifyObservers();

		Preferences pref = new Preferences(this);
		boolean firstSyncDone = pref.getBoolean(PrefParameter.FIRST_SYNC_DONE, false);
		if(!firstSyncDone) {
			boolean isTokenSet = pref.getBoolean(PrefParameter.FIRST_TOKEN_SET_DONE, false);
			if(isTokenSet)
				Toast.makeText(this, R.string.first_synch, Toast.LENGTH_LONG).show();
			this.syncHelper.synchAll(); 	
		}


	}

	public void onPause() {
		super.onPause();
		this.unregisterReceiver(messageReceiver);
		this.syncHelper.detachFromUI();

		this.fm.removeOnBackStackChangedListener(this.backStackListener);

		this.tasklists.removeObserver(listObserver);
		this.locations.removeObserver(locationObserver);
		this.tags.removeObserver(tagObserver);
		this.folders.removeObserver(folderObserver);
	}

	protected void onStop() {
		super.onStop();
		this.syncHelper.unbindScheduler();
	}

	@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case AUTHENTICATE :
			if(resultCode != RESULT_CANCELED) {
				SmartFragmentManager_old sfm = new SmartFragmentManager_old();
				Fragment sf = sfm.getAttachedFragment(this, R.id.fragmentContainer);
				if(sf != null && sf.getTag().equals(OVERVIEW_FRAGMENT))
					((TaskOverviewFragment) sf).refresh();
			}
			break;
		}
	}

	protected Dialog onCreateDialog(int id, Bundle bundle) {
		Dialog dialog;
		switch(id) {
		case SYNC_ERASE_CONFIRM :
			String CONFIRM_ERASE = BionicCowActivity_old.this.getResources().getString(R.string.confirmErase);
			String YES = BionicCowActivity_old.this.getResources().getString(R.string.yes);
			String NO = BionicCowActivity_old.this.getResources().getString(R.string.no);
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
			.setMessage(CONFIRM_ERASE)
			.setCancelable(false)
			.setPositiveButton(YES, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					new Preferences(BionicCowActivity_old.this).putLong(PrefParameter.LAST_SYNCH, 0);
					String synch_reset_message = 
							BionicCowActivity_old.this.getResources().getString(R.string.synch_reset_message);
					Toast.makeText(BionicCowActivity_old.this, synch_reset_message, Toast.LENGTH_LONG).show();
				}
			})
			.setNegativeButton(NO, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			dialog = builder.create();
			break;
		case DIALOG_EDIT_DELETE:
			String folderName = bundle.getString("folder");
			dialog = new AlertDialog.Builder(this).setTitle(modifyFolder).setCancelable(true)
					.setItems(R.array.edit_del_options, new SmartDialogInterfaceClickListener<String>(folderName) {
						@Override public void onClick(DialogInterface dialoginterface, int i) {
							String name = this.get();
							switch(i) {
							case 0 :
								Intent intent = new Intent(BionicCowActivity_old.this,FolderEditActivity.class);
								intent.putExtra("name", name);
								BionicCowActivity_old.this.startActivity(intent);
								break;
							case 1 :
								Map<String,Folder> folderMap = folders.retrieveAsMap();
								folderMap.remove(name);
								folders.saveAndNotifyAsList(folderMap);
								break;
							default :
								Toast.makeText(BionicCowActivity_old.this, "unknown action", Toast.LENGTH_SHORT).show();
								break;
							}
						}
					}).create();
			break;
		case DIALOG_VOTE_HINT:
			new Preferences(this).putBoolean(PrefParameter.VOTE_REQUESTED, true);
			dialog = new AlertDialog.Builder(this).setTitle(R.string.voteTitle)
					.setMessage(R.string.voteMessage).setCancelable(true)
					.setPositiveButton(R.string.voteOK, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							String uri = "market://details?id=it.bova.bioniccow";
							try{
								startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
							} catch (android.content.ActivityNotFoundException anfe) {
								uri = "http://play.google.com/store/apps/details?id=it.bova.bioniccow";
								startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
							}
							dialog.cancel();
						}
					})
					.setNegativeButton(R.string.voteNOK, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					})
					.create();
			break;
		case DATE_DIALOG_ID :
			Calendar cal = Calendar.getInstance();
			SimpleDatePickerDialog sdpd = new SimpleDatePickerDialog(this,
					dateFormatStrings,
					new DatePickerDialog.OnDateSetListener() {
				public void onDateSet(DatePicker view, int year, int month, int day) {
					Calendar cal = Calendar.getInstance();
					cal.set(year, month, day);
					String smartDate = " ^" + SmartDateFormat.formatInEnglish(cal.getTime()) + " ";
					quickTaskEditText.append(smartDate);
				}
			},
			cal.get(Calendar.YEAR), 
			cal.get(Calendar.MONTH),
			cal.get(Calendar.DAY_OF_MONTH));
			sdpd.setOnDismissListener(new OnDismissListener(){
				@Override public void onDismiss(DialogInterface dialog) {
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);				
				}
			});
			return sdpd;
		case REPEAT_DIALOG_ID :
			String[] repeatHints = this.getResources().getStringArray(R.array.repeat_hints);
			final String[] repeatHintsInEnglish = this.getResources().getStringArray(R.array.repeat_hints_english);
			AlertDialog.Builder builder2 = new AlertDialog.Builder(this)
			.setCancelable(true)
			.setAdapter(new ArrayAdapter<String>(this, R.layout.dropdown_simple, repeatHints),
					new  DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) {
					quickTaskEditText.append(" *" + repeatHintsInEnglish[which] + " ");
				}
			});
			dialog = builder2.create();
			dialog.setOnDismissListener(new OnDismissListener(){
				@Override public void onDismiss(DialogInterface dialog) {
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInputFromInputMethod(quickTaskEditText.getWindowToken(), 0);				
				}
			});
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	@Override public void onPrepareDialog(int id, final Dialog dialog, Bundle bundle) {
		switch(id) {
		case DIALOG_EDIT_DELETE:
			final String folderName = bundle.getString("folder");
			ListView lv = ((AlertDialog) dialog).getListView();
			lv.setOnItemClickListener(new OnItemClickListener(){
				@Override public void onItemClick(AdapterView<?> av, View v,
						int pos, long id) {
					switch(pos) {
					case 0 :
						Intent intent = new Intent(BionicCowActivity_old.this,FolderEditActivity.class);
						intent.putExtra("name", folderName);
						dialog.dismiss();
						BionicCowActivity_old.this.startActivity(intent);
						break;
					case 1 :
						Map<String,Folder> folderMap = folders.retrieveAsMap();
						folderMap.remove(folderName);
						folders.saveAndNotifyAsList(folderMap);
						dialog.dismiss();
						break;
					default :
						Toast.makeText(BionicCowActivity_old.this, "unknown action", Toast.LENGTH_SHORT).show();
						break;
					}					
				}
			});
		}

	}


	@Override public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mMenuInflater = this.getSupportMenuInflater();
		mMenuInflater.inflate(R.menu.main_action_bar, menu);
		this.syncHelper.setMenu(menu);
		this.syncHelper.checkButtons();
		return true;
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			FragmentManager fm = this.getSupportFragmentManager();
			fm.popBackStack();
			return true;
		case R.id.add:
			Intent intent = new Intent(this, TaskAddActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			this.startActivity(intent);
			return true;
		case R.id.sync:
			this.syncHelper.synchAll();
			return true;
		case R.id.reset:
			this.showDialog(SYNC_ERASE_CONFIRM, new Bundle());
			return true;
		case R.id.forget:
			ApiSingleton.saveToken(this, "");
			new Preferences(this).putLong(PrefParameter.LAST_SYNCH, 0L);
			this.syncHelper.synchAll();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void quickAdd(View v) {
		this.addTask();
	}

	private void addTask() {
		NetAvailabilityTask nat = new NetAvailabilityTask(this, 2000);
		if(nat.isConnected()) {
			String taskName = this.quickTaskEditText.getText().toString();
			String OK = this.getResources().getString(R.string.task_added_OK);
			String NOK = this.getResources().getString(R.string.task_added_NOK);
			TaskAdder ta = new TaskAdder(OK, NOK, this);
			ta.executeInBackground(taskName);
			//clear and unfocus editText
			this.quickTaskEditText.setText("");
		}
		else {
			Toast.makeText(this, R.string.connectionWarningQuickAdd, Toast.LENGTH_LONG).show();
		}
		//hide keyboard, if present and remove focus
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(this.quickTaskEditText.getWindowToken(), 0);
		this.defocusingView.requestFocus();
	}


	public void showDatePickerDialog(View v) {
		this.showDialog(DATE_DIALOG_ID);
	}

	public void showRepeatDialog(View v) {
		this.showDialog(REPEAT_DIALOG_ID);
	}

	public void quickAddPriority(View v) {
		this.quickTaskEditText.append(" !");
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

	private class NavigationAdapter extends ArrayAdapter<String> {

		public NavigationAdapter(Context context, int textViewResourceId,
				String[] objects) {
			super(context, textViewResourceId, objects);
		}

		private class Holder {
			public TextView tv;
			public ImageView image;
		}

		@Override public View getDropDownView(int position, View convertView, ViewGroup parent){
			if(BionicCowActivity_old.this.popup.getVisibility() == View.VISIBLE) {
				BionicCowActivity_old.this.popup.setVisibility(View.GONE);
				Preferences pref = new Preferences(BionicCowActivity_old.this);
				pref.putBoolean(PrefParameter.NAVIGATION_HINT_SHOWED, true);
			}
			if(convertView == null) {
				LayoutInflater inflater 
				= (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.navigation_dropdown, null);
				Holder navHolder = new Holder();
				navHolder.tv = (TextView) convertView.findViewById(R.id.text);
				navHolder.image = (ImageView) convertView.findViewById(R.id.image);
				convertView.setTag(navHolder);
			}
			Holder holder = (Holder) convertView.getTag();
			String string = this.getItem(position);
			holder.tv.setText(string);
			switch(position) {
			case OVERVIEW :
				holder.image.setBackgroundResource(R.drawable.overview);
				break;
			case LISTS :
				holder.image.setBackgroundResource(R.drawable.list);
				break;
			case TAGS :
				holder.image.setBackgroundResource(R.drawable.tags);
				break;
			case FOLDERS :
				holder.image.setBackgroundResource(R.drawable.folders);
				break;
			case LOCATIONS :
				holder.image.setBackgroundResource(R.drawable.locations);
				break;
			case SPECIALS :
				holder.image.setBackgroundResource(R.drawable.specials);
				break;
			}
			return convertView;
		}

	}


}
