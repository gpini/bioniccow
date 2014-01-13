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

public class TaskDatabase {

	private static DBHelper dBHelper;

	private static int openedDBs = 0;

	public static synchronized void getHelperSingleton(Context context) {
		if(context == null)
			throw new IllegalArgumentException("Context must be provided");
		if(dBHelper == null) 
			dBHelper = new DBHelper(context.getApplicationContext());
	}
	
	@Override public void open(Context context) {
		getHelperSingleton(context)
		openedDBs++;
		this.dB = dBHelper.getWritableDatabase();
		this.dB.execSQL("PRAGMA foreign_keys=ON;");
	}

	@Override public void close() {
		openedDBs--;
		if(openedDBs < 1) {
			if(dB.inTransaction())
				dB.endTransaction();
			if(dB != null)
				dB.close();
		}
	}
	
}
