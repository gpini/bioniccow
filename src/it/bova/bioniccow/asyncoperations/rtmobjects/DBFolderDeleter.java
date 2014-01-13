package it.bova.bioniccow.asyncoperations.rtmobjects;

import it.bova.bioniccow.asyncoperations.MessageSender;
import it.bova.bioniccow.data.database.TaskDatabase;
import it.bova.bioniccow.data.database.WriteableTaskDB;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DBFolderDeleter extends AsyncTask<Integer, Void, Boolean>{

	private Context context;
	
	public DBFolderDeleter(Context context) {
		this.context = context;
	}
	
	public Context getContext() {
		return this.context;
	}

	@Override
	protected Boolean doInBackground(Integer... params) {
		if(params.length > 0) {
			int folderId = params[0];
			TaskDatabase db = new WriteableTaskDB();
			try {
				db.open(this.context);
				long id = db.removeFolder(folderId);
				if(id >= 0)
					return true;
				else
					return false;
			}catch(Exception e) {
				MessageSender.sendMessage(this.context, "DB error: " + e.getMessage());
				return false;
			}
			finally {
				db.close();
			}
		}
		else return false;
	}

}
