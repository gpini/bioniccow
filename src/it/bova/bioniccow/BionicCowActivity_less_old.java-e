package it.bova.bioniccow;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import it.bova.bioniccow.asyncoperations.DefaultMessageReceiver;
import it.bova.bioniccow.asyncoperations.MessageReceiver;
import it.bova.bioniccow.asyncoperations.sync.SyncHelper;
import it.bova.bioniccow.data.ApiSingleton;
import it.bova.bioniccow.data.Folder;
import it.bova.bioniccow.data.Folders_old2;
import it.bova.bioniccow.data.Preferences;
import it.bova.bioniccow.data.Preferences.PrefParameter;
import it.bova.bioniccow.utilities.SimpleDatePickerDialog;
import it.bova.bioniccow.utilities.SmartDialogInterfaceClickListener;
import it.bova.bioniccow.utilities.SmartFragmentManager_old;
import it.bova.bioniccow.utilities.rtmobjects.SmartDateFormat;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class BionicCowActivity_less_old extends SherlockFragmentActivity implements InterProcess {

	private View popup;

	private MessageReceiver messageReceiver = new DefaultMessageReceiver(this);

	private SyncHelper syncHelper;

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
	private String[] dateFormatStrings;
	private String modifyFolder;

	private static final int SYNC_ERASE_CONFIRM = 2;
	private static final int DIALOG_VOTE_HINT = 3;
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
				Preferences prefs = new Preferences(BionicCowActivity_less_old.this);
				prefs.putInteger(PrefParameter.LAST_NAVIGATION_OPTION, position);
				switch(position) {
				case LISTS :
					SmartFragmentManager_old.startFragment(BionicCowActivity_less_old.this, new TaskListFragment(),
							R.id.fragmentContainer, LIST_FRAGMENT);	
					return true;
				case LOCATIONS :
					SmartFragmentManager_old.startFragment(BionicCowActivity_less_old.this, new LocationFragment(),
							R.id.fragmentContainer, LOCATION_FRAGMENT);
					return true;
				case TAGS :
					SmartFragmentManager_old.startFragment(BionicCowActivity_less_old.this, new TagFragment(),
							R.id.fragmentContainer, TAG_FRAGMENT);
					return true;
				case FOLDERS:
					SmartFragmentManager_old.startFragment(BionicCowActivity_less_old.this, new FolderListFragment(),
							R.id.fragmentContainer, FOLDER_LIST_FRAGMENT);
					return true;
				case OVERVIEW :
					SmartFragmentManager_old.startFragment(BionicCowActivity_less_old.this, new TaskOverviewFragment(),
							R.id.fragmentContainer, OVERVIEW_FRAGMENT);
					return true;
				case SPECIALS :
					SmartFragmentManager_old.startFragment(BionicCowActivity_less_old.this, new SpecialsFragment(),
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

		//resources
		this.dateFormatStrings = this.getResources().getStringArray(R.array.smart_date_format_labels);
		this.modifyFolder = this.getResources().getString(R.string.modify_folder);

		this.syncHelper = new SyncHelper(this);
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
					Preferences pref = new Preferences(BionicCowActivity_less_old.this);
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
							BionicCowActivity_less_old.this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
						else
							BionicCowActivity_less_old.this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
					}
				}
			}	
		};
		this.fm.addOnBackStackChangedListener(this.backStackListener);

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
			String CONFIRM_ERASE = BionicCowActivity_less_old.this.getResources().getString(R.string.confirmErase);
			String YES = BionicCowActivity_less_old.this.getResources().getString(R.string.yes);
			String NO = BionicCowActivity_less_old.this.getResources().getString(R.string.no);
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
			.setMessage(CONFIRM_ERASE)
			.setCancelable(false)
			.setPositiveButton(YES, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					new Preferences(BionicCowActivity_less_old.this).putLong(PrefParameter.LAST_SYNCH, 0);
					String synch_reset_message = 
							BionicCowActivity_less_old.this.getResources().getString(R.string.synch_reset_message);
					Toast.makeText(BionicCowActivity_less_old.this, synch_reset_message, Toast.LENGTH_LONG).show();
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
								Intent intent = new Intent(BionicCowActivity_less_old.this,FolderEditActivity.class);
								intent.putExtra("name", name);
								BionicCowActivity_less_old.this.startActivity(intent);
								break;
							case 1 :
								Folders_old2 folders = new Folders_old2(BionicCowActivity_less_old.this);
								Map<String,Folder> folderMap = folders.retrieveAsMap();
								folderMap.remove(name);
								folders.saveAndNotifyAsList(folderMap);
								break;
							default :
								Toast.makeText(BionicCowActivity_less_old.this, "unknown action", Toast.LENGTH_SHORT).show();
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
						Intent intent = new Intent(BionicCowActivity_less_old.this,FolderEditActivity.class);
						intent.putExtra("name", folderName);
						dialog.dismiss();
						BionicCowActivity_less_old.this.startActivity(intent);
						break;
					case 1 :
						Folders_old2 folders = new Folders_old2(BionicCowActivity_less_old.this);
						Map<String,Folder> folderMap = folders.retrieveAsMap();
						folderMap.remove(folderName);
						folders.saveAndNotifyAsList(folderMap);
						dialog.dismiss();
						break;
					default :
						Toast.makeText(BionicCowActivity_less_old.this, "unknown action", Toast.LENGTH_SHORT).show();
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
	
	public void appendToHeaderText(String text) {
		HeaderFragment headerFragment = (HeaderFragment) this.getSupportFragmentManager().findFragmentById(R.id.headerFragment);
		if(headerFragment != null)
			headerFragment.append(text);
	}
	
	public void setHeaderText(String text) {
		HeaderFragment headerFragment = (HeaderFragment) this.getSupportFragmentManager().findFragmentById(R.id.headerFragment);
		if(headerFragment != null)
			headerFragment.setText(text);
	}
	
	public void showKeyboard() {
		HeaderFragment headerFragment = (HeaderFragment) this.getSupportFragmentManager().findFragmentById(R.id.headerFragment);
		if(headerFragment != null)
			headerFragment.showKeyboard();
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
			if(BionicCowActivity_less_old.this.popup.getVisibility() == View.VISIBLE) {
				BionicCowActivity_less_old.this.popup.setVisibility(View.GONE);
				Preferences pref = new Preferences(BionicCowActivity_less_old.this);
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
