package it.bova.bioniccow.asyncoperations.rtmobjects;

import it.bova.bioniccow.asyncoperations.MessageSender;
import it.bova.bioniccow.data.Folder;
import it.bova.bioniccow.data.database.TaskDatabase;
import it.bova.bioniccow.data.database.WriteableTaskDB;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DBFolderEditor extends AsyncTask<Folder, Void, Boolean>{

	private Context context;
	
	public DBFolderEditor(Context context) {
		this.context = context;
	}

	@Override
	protected Boolean doInBackground(Folder... folders) {
		if(folders.length > 0) {
			Folder folder = folders[0];
			TaskDatabase db = new WriteableTaskDB();
			try {
				db.open(this.context);
				long updatedRows = db.updateFolder(folder);
				if(updatedRows >= 0)
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
