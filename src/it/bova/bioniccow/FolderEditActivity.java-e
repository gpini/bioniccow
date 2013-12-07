package it.bova.bioniccow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.bova.bioniccow.data.Folder;
import it.bova.bioniccow.data.Folder.Applicability;
import it.bova.bioniccow.data.Folders_old2;
import it.bova.bioniccow.data.Locations_old2;
import it.bova.bioniccow.data.Preferences;
import it.bova.bioniccow.data.Preferences.PrefParameter;
import it.bova.bioniccow.data.Tags_old2;
import it.bova.bioniccow.data.TaskLists_old2;
import it.bova.rtmapi.Location;
import it.bova.rtmapi.TaskList;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class FolderEditActivity extends EditActivity {
	
	private EditText folderNameInput;
	private EditText ruleInput;
	
	private Folder.Applicability selectedApplicability; //ROTATION!!
	
	//Resource
	private String folderNOKAdd1;
	private String folderNOKAdd2;
	
	private Folder folderToBeEdited;
	private Tags_old2 tags;
	private TaskLists_old2 tasklists;
	private Locations_old2 locations;
	
	private boolean isEditMode = true;
	
	private static final int FOLDER_HELP_DIALOG = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.folder_edit);

		this.folderNameInput = (EditText) this.findViewById(R.id.name);
		this.ruleInput = (EditText) this.findViewById(R.id.rule);
		((RadioButton) this.findViewById(R.id.radio_tags)).setChecked(true);
		this.selectedApplicability = Applicability.TAGS;
		
		//load forms if action is EDIT
		if(this.getIntent().hasExtra("name")) {
			 String name = this.getIntent().getStringExtra("name");
			 folderToBeEdited = 
					 new Folders_old2(this).retrieveAsMap().get(name);
			 if(folderToBeEdited != null) {
				 folderNameInput.setText(name);
				 String rule = folderToBeEdited.getRule();
				 ruleInput.setText(rule);
				 Applicability appl = folderToBeEdited.getApplicability();
				 switch(appl) {
				 case TAGS : 
					 ((RadioButton) this.findViewById(R.id.radio_tags)).setChecked(true);
					 this.selectedApplicability = Applicability.TAGS;
					 break;
				 case LISTS : 
					 ((RadioButton) this.findViewById(R.id.radio_lists)).setChecked(true);
					 this.selectedApplicability = Applicability.LISTS;
					 break;
				 case LOCATIONS : 
					 ((RadioButton) this.findViewById(R.id.radio_locations)).setChecked(true);
					 this.selectedApplicability = Applicability.LOCATIONS;
					 break;
				 case EVERYTHING : 
					 ((RadioButton) this.findViewById(R.id.radio_everything)).setChecked(true);
					 this.selectedApplicability = Applicability.EVERYTHING;
					 break;
				 }
			 }
			 else
				 Toast.makeText(this, "folder not found", Toast.LENGTH_SHORT).show();
			//"Where am I" TextViews
			this.ab.setTitle(name);
			this.ab.setSubtitle(R.string.edit_folder);
		}
		else { //ADD mode
			this.setTitle(R.string.add_folder);
			this.isEditMode = false;
		}
		
		folderNOKAdd1 = this.getResources().getString(R.string.folder_add_NOK1);
		folderNOKAdd2 = this.getResources().getString(R.string.folder_add_NOK2);

		this.tags = new Tags_old2(this);
		this.tasklists = new TaskLists_old2(this);
		this.locations = new Locations_old2(this);
		
	}
	

	public void onResume() {
		super.onResume();
		
		Preferences pref = new Preferences(this);
		boolean folderHelpShown = 
			pref.getBoolean(PrefParameter.FOLDER_HELP_SHOWN, false);
		if(!folderHelpShown) {
			this.showDialog(FOLDER_HELP_DIALOG);
		}

	}
	
	public void onPause() {
		super.onPause();		
	}
	
	protected Dialog onCreateDialog(int id) {
        switch(id) {
        case FOLDER_HELP_DIALOG :
        	LayoutInflater inflater 
					= (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			TextView tv = (TextView) inflater.inflate(R.layout.light_dialog, null); 
			//tv.setText(R.string.folderHelp);
        	//String FOLDER_HELP_TEXT = this.getResources().getString(R.string.folderHelp);
			String FOLDER_HELP_TITLE = this.getResources().getString(R.string.howToTitle);
        	String OK = this.getResources().getString(R.string.proceed);
        	AlertDialog.Builder builder = new AlertDialog.Builder(this)
			.setTitle(FOLDER_HELP_TITLE)
    		//.setMessage(FOLDER_HELP_TEXT)
    		.setView(tv)
    		.setCancelable(false)
    		.setPositiveButton(OK, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
    				new Preferences(FolderEditActivity.this).putBoolean(PrefParameter.FOLDER_HELP_SHOWN, true);
					dialog.cancel();
    			}
    		});
        	return builder.create();
        default:
            return null;
        }
    }
	
	public void onApplicabilityClicked(View v) {
		RadioButton rb = (RadioButton) v;
		switch(rb.getId()) {
			case R.id.radio_tags :
				this.selectedApplicability = Applicability.TAGS;
				break;
			case R.id.radio_lists :
				this.selectedApplicability = Applicability.LISTS;
				break;
			case R.id.radio_locations :
				this.selectedApplicability = Applicability.LOCATIONS;
				break;
			case R.id.radio_everything :
				this.selectedApplicability = Applicability.EVERYTHING;
				break;
			default :
				this.selectedApplicability = Applicability.EVERYTHING;
				break;
		}
	}
	
	@Override public void onSaveActionPressed() {
		//save
		String name = this.folderNameInput.getText().toString();
		String rule = this.ruleInput.getText().toString();
		if(name.equals("") || rule.equals("")) {
			if(name.equals(""))
				Toast.makeText(this, this.folderNOKAdd1, Toast.LENGTH_SHORT).show();
			else if(rule.equals(""))
				Toast.makeText(this, this.folderNOKAdd2, Toast.LENGTH_SHORT).show();
			this.finish();
		}
		else {
			Folders_old2 folders = new Folders_old2(this);
			Map<String,Folder> folderMap = folders.retrieveAsMap();
			if(folderToBeEdited != null)
				folderMap.remove(folderToBeEdited.getName());
			Folder newFolder = new Folder(name, rule, selectedApplicability);
			List<String> tagElements = new ArrayList<String>();
			List<String> listElements = new ArrayList<String>();
			List<String> locationElements = new ArrayList<String>();
			switch(selectedApplicability) {
				case TAGS :
					Set<String> tagSet1 = this.tags.retrieve();
					tagElements = newFolder.loadTagElements(tagSet1);
					break;
				case LISTS :
					Map<String,TaskList> listMap1 = this.tasklists.retrieveAsMap();
					listElements = newFolder.loadListElements(listMap1);
					break;
				case LOCATIONS :
					Map<String,Location> locMap1 = this.locations.retrieveAsMap();
					locationElements = newFolder.loadLocationElements(locMap1);
					break;
				case EVERYTHING :
					Set<String> tagSet2 = this.tags.retrieve();
					tagElements = newFolder.loadTagElements(tagSet2);
					Map<String,TaskList> listMap2 = this.tasklists.retrieveAsMap();
					listElements = newFolder.loadListElements(listMap2);
					Map<String,Location> locMap2 = this.locations.retrieveAsMap();
					locationElements = newFolder.loadLocationElements(locMap2);
					break;
			}
			newFolder.setTagElements(tagElements);
			newFolder.setListElements(listElements);
			newFolder.setLocationElements(locationElements);
			folderMap.put(newFolder.getName(),newFolder);
			folders.saveAndNotifyAsList(folderMap);
			this.finish();
		}
	}
	
}
