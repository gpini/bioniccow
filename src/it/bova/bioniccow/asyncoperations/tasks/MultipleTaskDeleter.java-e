package it.bova.bioniccow.asyncoperations.tasks;

import it.bova.bioniccow.asyncoperations.sync.Synchronizer;
import it.bova.rtmapi.Task;

import java.util.HashMap;
import android.content.Context;

public class MultipleTaskDeleter extends MultipleTaskChanger {

	public MultipleTaskDeleter(String OKSinglePhrase, String OKMultiPhrase,
			String NOKSinglePhrase, String NOKMultiPhrase, Context context) {
		super(OKSinglePhrase, OKMultiPhrase, NOKSinglePhrase, NOKMultiPhrase, context);
	}
	
	@Override public boolean add(TaskInquirer inquirer) {
		if(!(inquirer instanceof TaskDeleter)) return false;
		else {
			super.add(inquirer);
			return true;
		}
	}
	
	@Override protected void onPostExecute(HashMap<String,Task> deletedTasks) {
		if(deletedTasks.size() != 0) {
			Synchronizer synchronizer = new Synchronizer(this.getContext());
			synchronizer.syncDeletedTasks(deletedTasks.values());
		}
	}

}
