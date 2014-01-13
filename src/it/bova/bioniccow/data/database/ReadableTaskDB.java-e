package it.bova.bioniccow.data.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import it.bova.bioniccow.data.Folder;
import it.bova.rtmapi.Contact;
import it.bova.rtmapi.Location;
import it.bova.rtmapi.Note;
import it.bova.rtmapi.Task;
import it.bova.rtmapi.TaskList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ReadableTaskDB extends TaskDatabase {

	@Override public void open(Context context) {
		if(dB != null)
			throw new IOException("DB must closed in order to open an other one");
		else {
			if(context == null)
				throw new IllegalArgumentException("Context must be provided");
			DBHelper dBHelper = new DBHelper(context.getApplicationContext());
			dB = dBHelper.getWritableDatabase();
			dB.execSQL("PRAGMA foreign_keys=ON;");
		}
	}

	@Override public void close() {
		if(dB.inTransaction())
			dB.endTransaction();
		if(dB != null) {
			dB.close();
			dB = null;
		}
	}
	

}
