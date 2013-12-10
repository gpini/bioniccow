package it.bova.bioniccow.asyncoperations.rtmobjects;

import it.bova.bioniccow.data.Folder;
import it.bova.bioniccow.data.database.TaskDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DBFolderAdder extends AsyncTask<Folder, Void, Boolean>{

	private Context context;
	
	public DBFolderAdder(Context context) {
		this.context = context;
	}

	@Override
	protected Boolean doInBackground(Folder... folders) {
		if(folders.length > 0) {
			Folder folder = folders[0];
			try {
				TaskDatabase.open(this.context);
				long id = TaskDatabase.putFolder(folder);
				if(id >= 0)
					return true;
				else
					return false;
			}catch(Exception e) {
				Log.d("DB error", e.getMessage());
				return false;
			}
			finally {
				TaskDatabase.close();
			}
		}
		else return false;
	}

}