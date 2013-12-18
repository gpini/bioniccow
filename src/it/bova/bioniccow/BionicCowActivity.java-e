package it.bova.bioniccow;

import java.util.Date;
import java.util.List;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import it.bova.bioniccow.asyncoperations.DefaultMessageReceiver;
import it.bova.bioniccow.asyncoperations.MessageReceiver;
import it.bova.bioniccow.asyncoperations.sync.SyncHelper;
import it.bova.bioniccow.data.ApiSingleton;
import it.bova.bioniccow.data.Preferences;
import it.bova.bioniccow.data.Preferences.PrefParameter;
import it.bova.bioniccow.utilities.rtmobjects.ParcelableTask;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class BionicCowActivity extends MainActivity implements InterProcess {

	private View popup;

	private MessageReceiver messageReceiver;

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
	
	private boolean isCreated = false;

	/** Called when the activity is first created. */
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.messageReceiver = new MainMessageReceiver(this);
		
		//Action Bar
		setContentView(R.layout.main);
		ActionBar ab = this.getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		ab.setDisplayShowHomeEnabled(true);
		ab.setDisplayShowTitleEnabled(false);
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
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
				Preferences prefs = new Preferences(BionicCowActivity.this);
				prefs.putInteger(PrefParameter.LAST_NAVIGATION_OPTION, position);
				FragmentManager fm = BionicCowActivity.this.getSupportFragmentManager();
				//empties the BackStack
				if(fm.getBackStackEntryCount() > 0)
					fm.popBackStack();
				if(isCreated) {
					switch(position) {
					case LISTS :
						fm.beginTransaction()
						.replace(R.id.fragmentContainer, new TaskListFragment(), LIST_FRAGMENT)
						.commit();	
						return true;
					case LOCATIONS :
						fm.beginTransaction()
						.replace(R.id.fragmentContainer, new LocationFragment(), LOCATION_FRAGMENT)
						.commit();	
						return true;
					case TAGS :
						fm.beginTransaction()
						.replace(R.id.fragmentContainer, new TagFragment(), TAG_FRAGMENT)
						.commit();	
						return true;
					case FOLDERS:
						fm.beginTransaction()
						.replace(R.id.fragmentContainer, new FolderListFragment(), FOLDER_LIST_FRAGMENT)
						.commit();
						return true;
					case OVERVIEW :
						fm.beginTransaction()
						.replace(R.id.fragmentContainer, new TaskOverviewFragment(), OVERVIEW_FRAGMENT)					
						.commit();
						return true;
					case SPECIALS :
						fm.beginTransaction()
						.replace(R.id.fragmentContainer, new SpecialsFragment(), SPECIAL_FRAGMENT)					
						.commit();
						return true;
					default :
						return false;

					}
				}
				else return false;
			}
		};
		ab.setListNavigationCallbacks(navAdapter, navigationListener);
		int position = new Preferences(this).getInteger(PrefParameter.LAST_NAVIGATION_OPTION, LISTS);
		this.isCreated = true;
		ab.setSelectedNavigationItem(position);

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
				this.showVoteHintDialog();
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
					Preferences pref = new Preferences(BionicCowActivity.this);
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
							BionicCowActivity.this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
						else
							BionicCowActivity.this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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
				Fragment sf = this.getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
				if(sf != null && sf.getTag().equals(OVERVIEW_FRAGMENT))
					((TaskOverviewFragment) sf).refresh();
			}
			break;
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
			this.showEraseConfirmDialog();
			return true;
		case R.id.forget:
			this.showForgetConfirmDialog();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void showEraseConfirmDialog() {
	    DialogFragment newFragment = EraseConfirmDialogFragment.newInstance();
	    newFragment.show(this.getSupportFragmentManager(), "erase_confirm");
	}
	
	public void showForgetConfirmDialog() {
	    DialogFragment newFragment = ForgetConfirmDialogFragment.newInstance();
	    newFragment.show(this.getSupportFragmentManager(), "forget_confirm");
	}
	
	public void showVoteHintDialog() {
	    DialogFragment newFragment = VoteHintDialogFragment.newInstance();
	    newFragment.show(this.getSupportFragmentManager(), "vote_hint");
	}
		
	public static class EraseConfirmDialogFragment extends DialogFragment {

	    public static EraseConfirmDialogFragment newInstance() {
	    	EraseConfirmDialogFragment frag = new EraseConfirmDialogFragment();
	    	return frag;
	    }
	    
	    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
	    	String CONFIRM_ERASE = this.getResources().getString(R.string.confirmErase);
			String YES = this.getResources().getString(R.string.yes);
			String NO = this.getResources().getString(R.string.no);
			AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity())
			.setMessage(CONFIRM_ERASE)
			.setCancelable(false)
			.setPositiveButton(YES, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					new Preferences(EraseConfirmDialogFragment.this.getActivity()).putLong(PrefParameter.LAST_SYNCH, 0);
					String synch_reset_message = 
							EraseConfirmDialogFragment.this.getResources().getString(R.string.synch_reset_message);
					Toast.makeText(EraseConfirmDialogFragment.this.getActivity(), synch_reset_message, Toast.LENGTH_LONG).show();
					EraseConfirmDialogFragment.this.dismiss();
				}
			})
			.setNegativeButton(NO, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					EraseConfirmDialogFragment.this.dismiss();
				}
			});
			Dialog dialog = builder.create();
	    	return dialog;
	    }
	}
	
	public static class ForgetConfirmDialogFragment extends DialogFragment {

	    public static ForgetConfirmDialogFragment newInstance() {
	    	ForgetConfirmDialogFragment frag = new ForgetConfirmDialogFragment();
	    	return frag;
	    }
	    
	    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
	    	String CONFIRM_ERASE = this.getResources().getString(R.string.confirmForget);
			String YES = this.getResources().getString(R.string.yes);
			String NO = this.getResources().getString(R.string.no);
			AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity())
			.setMessage(CONFIRM_ERASE)
			.setCancelable(false)
			.setPositiveButton(YES, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					ApiSingleton.saveToken(ForgetConfirmDialogFragment.this.getActivity(), "");
					Preferences pref = new Preferences(ForgetConfirmDialogFragment.this.getActivity());
					pref.putLong(PrefParameter.LAST_SYNCH, 0L);
					pref.putBoolean(PrefParameter.FIRST_SYNC_DONE, false);
					pref.putBoolean(PrefParameter.FIRST_TOKEN_SET_DONE, false);
					((BionicCowActivity) ForgetConfirmDialogFragment.this.getActivity()).syncHelper.synchAll();
					ForgetConfirmDialogFragment.this.dismiss();
				}
			})
			.setNegativeButton(NO, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					ForgetConfirmDialogFragment.this.dismiss();
				}
			});
			Dialog dialog = builder.create();
	    	return dialog;
	    }
	}
	

	public static class VoteHintDialogFragment extends DialogFragment {

	    public static VoteHintDialogFragment newInstance() {
	    	VoteHintDialogFragment frag = new VoteHintDialogFragment();
	    	return frag;
	    }
	    
	    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
	    	new Preferences(this.getActivity()).putBoolean(PrefParameter.VOTE_REQUESTED, true);
	    	Dialog dialog = new AlertDialog.Builder(this.getActivity()).setTitle(R.string.voteTitle)
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
	    					VoteHintDialogFragment.this.dismiss();
	    				}
	    			})
	    			.setNegativeButton(R.string.voteNOK, new DialogInterface.OnClickListener() {
	    				public void onClick(DialogInterface dialog, int id) {
	    					VoteHintDialogFragment.this.dismiss();
	    				}
	    			})
	    			.create();
	    	return dialog;
	    }
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
			if(BionicCowActivity.this.popup.getVisibility() == View.VISIBLE) {
				BionicCowActivity.this.popup.setVisibility(View.GONE);
				Preferences pref = new Preferences(BionicCowActivity.this);
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
	
	private class MainMessageReceiver extends DefaultMessageReceiver {
	
		public MainMessageReceiver(SherlockFragmentActivity activity) {
			super(activity);
		}
		
		@Override protected void onTasklistsUpdated(Context context) {
			FragmentManager fm = BionicCowActivity.this.fm;
			Fragment headFragment = fm.findFragmentById(R.id.headerFragment);
			((HeaderFragment) headFragment).refresh();
			Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
			if(fragment != null && fragment.getTag() != null && fragment.getTag().equals(LIST_FRAGMENT)) 
				((TaskListFragment) fragment).refresh();
			if(fragment != null && fragment.getTag() != null && fragment.getTag().equals(FOLDER_FRAGMENT)) 
				((FolderFragment) fragment).refresh();
		}
			
		@Override protected void onLocationsUpdated(Context context) {
			FragmentManager fm = BionicCowActivity.this.fm;
			Fragment headFragment = fm.findFragmentById(R.id.headerFragment);
			((HeaderFragment) headFragment).refresh();
			Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
			if(fragment != null && fragment.getTag() != null && fragment.getTag().equals(LOCATION_FRAGMENT)) 
				((LocationFragment) fragment).refresh();
			if(fragment != null && fragment.getTag() != null && fragment.getTag().equals(FOLDER_FRAGMENT)) 
				((FolderFragment) fragment).refresh();
		}
		
		@Override protected void onFoldersUpdated(Context context) {
			FragmentManager fm = BionicCowActivity.this.fm;
			Fragment headFragment = fm.findFragmentById(R.id.headerFragment);
			((HeaderFragment) headFragment).refresh();
			Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
			if(fragment != null && fragment.getTag() != null && fragment.getTag().equals(FOLDER_FRAGMENT)) 
				((FolderFragment) fragment).refresh();
			if(fragment != null && fragment.getTag() != null && fragment.getTag().equals(FOLDER_LIST_FRAGMENT)) 
				((FolderListFragment) fragment).refresh();
		}
			
		@Override protected void onTaskChanged(Context context, List<String> changedId) {
			FragmentManager fm = BionicCowActivity.this.fm;
			Fragment headFragment = fm.findFragmentById(R.id.headerFragment);
			((HeaderFragment) headFragment).refresh();
			Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
			if(fragment != null && fragment.getTag() != null && fragment.getTag().equals(TAG_FRAGMENT)) 
				((TagFragment) fragment).refresh();
			if(fragment != null && fragment.getTag() != null && fragment.getTag().equals(FOLDER_FRAGMENT)) 
				((FolderFragment) fragment).refresh();
			if(fragment != null && fragment.getTag() != null && fragment.getTag().equals(OVERVIEW_FRAGMENT)) 
				((TaskOverviewFragment) fragment).refreshOnTaskChanged(changedId);
		}
			
		@Override protected void onTaskAdded(Context context, List<ParcelableTask> addedTasks) {
			FragmentManager fm = BionicCowActivity.this.fm;
			Fragment headFragment = fm.findFragmentById(R.id.headerFragment);
			((HeaderFragment) headFragment).refresh();
			Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
			if(fragment != null && fragment.getTag() != null && fragment.getTag().equals(TAG_FRAGMENT)) 
				((TagFragment) fragment).refresh();
			if(fragment != null && fragment.getTag() != null && fragment.getTag().equals(OVERVIEW_FRAGMENT)) 
				((TaskOverviewFragment) fragment).refreshOnTaskAdded(addedTasks);
		}
			
	}


}
