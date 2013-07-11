package it.bova.bioniccow.data;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import android.content.Context;

public class Serializer<T> {
	
	private String fileName;
	private Context context;
	
	public Serializer(String fileName, Context context) {
		this.fileName = fileName;
		this.context = context;
	}
	
	public boolean serialize(T t) {
		//se non la scrivo pace!! Non lo notifico
		synchronized(fileName) {
			FileOutputStream fos = null;
			ObjectOutputStream oos = null;
			try {
				//Con MODE_PRIVATE sovrascrivo (non è append)
				fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
				oos = new ObjectOutputStream(fos);	
				oos.writeObject(t); // Save object
				oos.flush(); // Empty output buffer
				return true;
			} catch(Exception e) {
				return false;
			} finally {			
				try {
					fos.close();
					oos.close();
				} catch (IOException e) {} 	
			}
		}
	}
	public T deserialize() throws IOException {
		//se non riesco ad aprire, chiudere o leggere il file lo comunico tramite eccezione
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = context.openFileInput(fileName);
			ois = new ObjectInputStream(fis);
			@SuppressWarnings("unchecked")
			T t = (T) ois.readObject();
			return t;
		} catch (ClassNotFoundException e) {
			throw new IOException("Class not found");
		} finally {
			if(fis != null) fis.close();//?
			if(ois != null) ois.close();
		}

	}

}
