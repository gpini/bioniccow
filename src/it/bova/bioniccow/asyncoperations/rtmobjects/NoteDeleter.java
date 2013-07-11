package it.bova.bioniccow.asyncoperations.rtmobjects;

import it.bova.bioniccow.asyncoperations.DefaultInquirer;
import it.bova.bioniccow.asyncoperations.MessageSender;
import it.bova.bioniccow.data.ApiSingleton;
import it.bova.bioniccow.data.database.TaskDatabase;
import it.bova.rtmapi.RtmApi;
import it.bova.rtmapi.RtmApiException;
import it.bova.rtmapi.ServerException;
import java.io.IOException;
import android.content.Context;

public class NoteDeleter extends DefaultInquirer<String,Boolean> {

	public NoteDeleter(String OKPhrase, String NOKPhrase, Context context) {
		super(OKPhrase,NOKPhrase, context);
	}

	@Override
	protected Boolean inquire(String... noteIds) throws IOException,
			ServerException, RtmApiException, Exception {
		Context c = this.getContext();
		RtmApi api = ApiSingleton.getApi(c);
		String timeline = ApiSingleton.getTimeline(c);
		boolean ok = false;
		if(noteIds.length > 0) ok = api.tasksDeleteNote(timeline, noteIds[0]);
		else ok = false;
		if(ok) {
			try {
				TaskDatabase.open(this.getContext());
				TaskDatabase.removeNote(noteIds[0]);
				MessageSender.notifyNoteDeleted(getContext(), noteIds[0]);
			}catch(Exception e) {
				//Log.d("changed error",e.getMessage());
			}
			finally {
				TaskDatabase.close();	
			}
		}
		return ok;		
	}
	
	@Override protected void onResultObtained(Boolean result) {
		MessageSender.sendMessage(this.getContext(), getOKPhrase());
	}

}
