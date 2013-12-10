package it.bova.bioniccow.asyncoperations.rtmobjects;

import it.bova.bioniccow.data.database.TaskDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DBFolderDeleter extends AsyncTask<Integer, Void, Boolean>{

	private Context context;
	
	public DBFolderDeleter(Context context) {
		this.context = context;
	}

	@Override
	protected Boolean doInBackground(Integer... params) {
		if(params.length > 0) {
			int folderId = params[0];
			try {
				TaskDatabase.open(this.context);
				long id = TaskDatabase.removeFolder(folderId);
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
