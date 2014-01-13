package it.bova.bioniccow.data.database;

import java.io.IOException;
import android.content.Context;

public class ReadableTaskDB extends TaskDatabase {

	@Override public void open(Context context) throws IOException {
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
