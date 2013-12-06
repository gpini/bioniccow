package it.bova.bioniccow.asyncoperations.rtmobjects;

import it.bova.bioniccow.data.database.TaskDatabase;
import it.bova.rtmapi.Location;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DBTagGetter extends AsyncTask<Void, Void, Set<String>>{

	private Context context;
	
	public DBTagGetter(Context context) {
		this.context = context;
	}

	@Override
	protected Set<String> doInBackground(Void... params) {
		try {
			TaskDatabase.open(this.context);
			return TaskDatabase.getDistinctTags();
		}catch(Exception e) {
			Log.d("DB error", e.getMessage());
			return new HashSet<String>();
		}
		finally {
			TaskDatabase.close();
		}
	}

}