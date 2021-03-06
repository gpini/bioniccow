package it.bova.bioniccow.asyncoperations.rtmobjects;

import it.bova.bioniccow.asyncoperations.MessageSender;
import it.bova.bioniccow.data.database.ReadableTaskDB;
import it.bova.bioniccow.data.database.TaskDatabase;
import it.bova.rtmapi.TaskList;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DBTaskListsGetter extends AsyncTask<Void, Void, List<TaskList>>{

	private Context context;
	
	public DBTaskListsGetter(Context context) {
		this.context = context;
	}

	@Override
	protected List<TaskList> doInBackground(Void... params) {
		TaskDatabase db = new ReadableTaskDB();
		try {
			db.open(this.context);
			return db.getTasklists();
		}catch(Exception e) {
			MessageSender.sendMessage(this.context, "DB error: " + e.getMessage());
			return new ArrayList<TaskList>();
		}
		finally {
			db.close();
		}
	}

}
