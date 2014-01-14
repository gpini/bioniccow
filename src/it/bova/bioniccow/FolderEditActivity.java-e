package it.bova.bioniccow;

import it.bova.bioniccow.asyncoperations.MessageSender;
import it.bova.bioniccow.asyncoperations.rtmobjects.DBFolderAdder;
import it.bova.bioniccow.asyncoperations.rtmobjects.DBFolderEditor;
import it.bova.bioniccow.data.Folder;
import it.bova.bioniccow.data.Folder.Applicability;
import it.bova.bioniccow.data.Preferences;
import it.bova.bioniccow.data.Preferences.PrefParameter;
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
	
	private static final int FOLDER_HELP_DIALOG = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.folder_edit);

		this.folderNameInput = (EditText) this.findViewById(R.id.name);
		this.ruleInput = (EditText) this.findViewById(R.id.rule);
		((RadioButton) this.findViewById(R.id.radio_tags)).setChecked(true);
		this.selectedApplicability = Applicability.TAGS;
		
		if(this.getIntent().hasExtra("folder")) { //load forms if action is EDIT
			Folder folder = (Folder) this.getIntent().getSerializableExtra("folder");
			String name = folder.getName();
			folderToBeEdited = folder;
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
		}
		
		folderNOKAdd1 = this.getResources().getString(R.string.folder_add_NOK1);
		folderNOKAdd2 = this.getResources().getString(R.string.folder_add_NOK2);
		
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
			//TextView tv = (TextView) inflater.inflate(R.layout.light_dialog, null); 
			//tv.setText(R.string.folderHelp);
        	String FOLDER_HELP_TEXT = this.getResources().getString(R.string.folderHelp);
			String FOLDER_HELP_TITLE = this.getResources().getString(R.string.howToTitle);
        	String OK = this.getResources().getString(R.string.proceed);
        	AlertDialog.Builder builder = new AlertDialog.Builder(this)
			.setTitle(FOLDER_HELP_TITLE)
    		.setMessage(FOLDER_HELP_TEXT)
    		//.setView(tv)
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
		int folderId = -1;
		if(folderToBeEdited != null) //EDIT MODE
			folderId = folderToBeEdited.getId();
		Folder newFolder = new Folder(folderId, name, rule, selectedApplicability);
		if(name.equals("") || rule.equals("")) {
			if(name.equals(""))
				Toast.makeText(this, this.folderNOKAdd1, Toast.LENGTH_SHORT).show();
			else if(rule.equals(""))
				Toast.makeText(this, this.folderNOKAdd2, Toast.LENGTH_SHORT).show();
			this.finish();
		}
		else {
			if(folderToBeEdited != null) {
				//EDIT MODE
				DBFolderEditor fe = new DBFolderEditor(FolderEditActivity.this) {
					@Override public void onPostExecute(Boolean result) {
						if(result)
							MessageSender.notifyFoldersUpdated(FolderEditActivity.this);
						FolderEditActivity.this.finish();
					}
				};
				fe.execute(newFolder);
			}
			else {
				//ADD MODE
				DBFolderAdder fa = new DBFolderAdder(FolderEditActivity.this) {
					@Override public void onPostExecute(Boolean result) {
						if(result)
							MessageSender.notifyFoldersUpdated(FolderEditActivity.this);
						FolderEditActivity.this.finish();
					}
				};
				fa.execute(newFolder);
			}
		}
	}
	
}
