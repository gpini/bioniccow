package it.bova.bioniccow.asyncoperations.rtmobjects;

import it.bova.bioniccow.data.Folder;
import it.bova.bioniccow.data.database.TaskDatabase;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DBFolderGetter extends AsyncTask<Void, Void, List<Folder>>{

	private Context context;
	
	public DBFolderGetter(Context context) {
		this.context = context;
	}

	@Override
	protected List<Folder> doInBackground(Void... params) {
		try {
			TaskDatabase.open(this.context);
			return TaskDatabase.getFolders();
		}catch(Exception e) {
			Log.d("DB error", e.getMessage());
			return new ArrayList<Folder>();
		}
		finally {
			TaskDatabase.close();
		}
	}

}