package it.bova.bioniccow.data.database;

import android.content.Context;

public class WriteableTaskDB extends TaskDatabase {

	private static DBHelper dBHelper;

	private static int openedDBs = 0;

	public static synchronized void getHelperSingleton(Context context) {
		if(context == null)
			throw new IllegalArgumentException("Context must be provided");
		if(dBHelper == null) 
			dBHelper = new DBHelper(context.getApplicationContext());
	}
	
	@Override public void open(Context context) {
		getHelperSingleton(context);
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
