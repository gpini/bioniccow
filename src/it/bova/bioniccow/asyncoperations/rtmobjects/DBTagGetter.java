package it.bova.bioniccow.asyncoperations.rtmobjects;

import it.bova.bioniccow.asyncoperations.MessageSender;
import it.bova.bioniccow.data.database.ReadableTaskDB;
import it.bova.bioniccow.data.database.TaskDatabase;
import java.util.HashSet;
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
		TaskDatabase db = new ReadableTaskDB();
		try {
			db.open(this.context);
			return db.getTags();
		}catch(Exception e) {
			MessageSender.sendMessage(this.context, "DB error: " + e.getMessage());
			return new HashSet<String>();
		}
		finally {
			db.close();
		}
	}

}
