package it.bova.bioniccow.asyncoperations.rtmobjects;

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

public class DBNotLocatedTaskGetter extends TaskGetter {

	public DBNotLocatedTaskGetter(String NOKPhrase, Context context) {
		super(NOKPhrase, context);
	}
	
	@Override public List<Task> inquire(String... locIds) throws ServerException, RtmApiException, IOException {
		try {
			TaskDatabase db = new ReadableTaskDB();
			db.open(this.context);
			//if(listIds.length < 1) return new ArrayList<Task>();
			return db.get(Mode.NOT_LOCATED);
		}catch(Exception e) {
			Log.d("DB error", e.getMessage());
			return new ArrayList<Task>();
		}
		finally {
			db.close();
		}
	}

}
