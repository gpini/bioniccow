package it.bova.bioniccow.asyncoperations.rtmobjects;

import it.bova.bioniccow.asyncoperations.DefaultInquirer;
import it.bova.bioniccow.asyncoperations.MessageSender;
import it.bova.bioniccow.data.ApiSingleton;
import it.bova.bioniccow.data.database.TaskDatabase;
import it.bova.rtmapi.Note;
import it.bova.rtmapi.RtmApi;
import it.bova.rtmapi.RtmApiException;
import it.bova.rtmapi.ServerException;
import java.io.IOException;
import android.content.Context;

public class NoteModifier extends DefaultInquirer<String,Note> {

	public NoteModifier(String OKPhrase, String NOKPhrase, Context context) {
		super(OKPhrase,NOKPhrase, context);
	}

	@Override
	protected Note inquire(String... params) throws IOException,
			ServerException, RtmApiException, Exception {
		Context c = this.getContext();
		RtmApi api = ApiSingleton.getApi(c);
		String timeline = ApiSingleton.getTimeline(c);
		Note note;
		if(params.length > 2) note = api.tasksEditNote(timeline, params[0], params[1], params[2]);
		else note = api.tasksEditNote(timeline, "", "", "");
		try {
			TaskDatabase.open(this.getContext());
			TaskDatabase.updateNote(note);	
			MessageSender.notifyNoteEdited(getContext(), note);
		}catch(Exception e) {
			//Log.d("changed error",e.getMessage());
		}
		finally {
			TaskDatabase.close();
		}	
		return note;
	}
	
	@Override protected void onResultObtained(Note result) {
		MessageSender.sendMessage(this.getContext(), getOKPhrase());
	}

}
