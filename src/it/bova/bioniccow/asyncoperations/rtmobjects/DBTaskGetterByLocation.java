package it.bova.bioniccow.asyncoperations.rtmobjects;

import it.bova.bioniccow.data.database.ReadableTaskDB;
import it.bova.bioniccow.data.database.TaskDatabase;
import it.bova.bioniccow.data.database.TaskDatabase.Mode;
import it.bova.rtmapi.RtmApiException;
import it.bova.rtmapi.ServerException;
import it.bova.rtmapi.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.util.Log;

public class DBTaskGetterByLocation extends TaskGetter {

	public DBTaskGetterByLocation(String NOKPhrase, Context context) {
		super(NOKPhrase, context);
	}
	
	@Override public List<Task> inquire(String... locIds) throws ServerException, RtmApiException, IOException {
		TaskDatabase db = new ReadableTaskDB();
		try {
			db.open(this.getContext());
			//if(listIds.length < 1) return new ArrayList<Task>();
			return db.get(Mode.BY_LOCATION, locIds[0]);
		}catch(Exception e) {
			Log.d("DB error", e.getMessage());
			return new ArrayList<Task>();
		}
		finally {
			db.close();
		}
	}

}
