package it.bova.bioniccow.asyncoperations.rtmobjects;

import it.bova.bioniccow.data.database.TaskDatabase;
import it.bova.rtmapi.Location;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DBLocationsGetter extends AsyncTask<Void, Void, List<Location>>{

	private Context context;
	
	public DBLocationsGetter(Context context) {
		this.context = context;
	}

	@Override
	protected List<Location> doInBackground(Void... params) {
		try {
			TaskDatabase db = new ReadableTaskDB();
			db.open(this.context);
			return db.getLocations();
		}catch(Exception e) {
			Log.d("DB error", e.getMessage());
			return new ArrayList<Location>();
		}
		finally {
			db.close();
		}
	}

}
