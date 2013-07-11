package it.bova.bioniccow.asyncoperations.tasks;

import it.bova.bioniccow.asyncoperations.MessageSender;
import it.bova.bioniccow.asyncoperations.InquiryAnswer;
import it.bova.rtmapi.Task;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

public class MultipleTaskEditor extends MultipleTaskInquirer {

	private Map<String,List<String>> errorMap =	 new HashMap<String,List<String>>();
	private Task task;
	private boolean isLoginIssue = false;
	private int successfullEditors = 0;

	public MultipleTaskEditor(String OKPhrase, String NOKPhrase, Context context, Task task) {
		super(OKPhrase, NOKPhrase, context);
		this.task = task;
	}
	
	public Task getTask() {return this.task;}
	
	@Override public boolean add(TaskInquirer editor) {
		if(!(editor instanceof TaskEditor)) return false;
		if(this.getTask().getId() == editor.getTask().getId()) {
			super.add(editor);
			return true;
		}
		else return false;
	}

	protected void onChangePerformed(TaskInquirer servedEditor, InquiryAnswer<List<Task>> answer) {
		//executed in background thread
		Integer answerCode = answer.getCode();
		if(answerCode != OK) {
			if(answerCode == LOGIN_ISSUE) isLoginIssue = true;
			else {
				String msg = "";
				if(answerCode > 0) msg = "(" + answerCode + ") " + answer.getMsg();
				else msg = answer.getMsg();
				if(!this.errorMap.containsKey(msg))
					this.errorMap.put(msg, new ArrayList<String>());
				if(servedEditor instanceof TaskEditor)
					this.errorMap.get(msg).add(((TaskEditor) servedEditor).getFieldName());
			}
		}
		else 
			successfullEditors++;
	}
	
	protected void onChangesCompleted() {
		//send messages
		if(this.isLoginIssue)
			MessageSender.sendAuthErrorMessage(this.getContext());
		else if(errorMap.size() == 0) {
			if(successfullEditors > 0 ) {
				String OK = new Formatter().format(this.getOKPhrase(), task.getName()).toString();
				MessageSender.sendMessage(this.getContext(), OK); //task %s added
			}
		}
		else {
			StringBuilder sb =  new StringBuilder("");
			String NOK = new Formatter().format(this.getNOKPhrase(), task.getName()).toString();
			sb.append(NOK); //"Impossible to edit task %s:"
			for(String errMsg : this.errorMap.keySet()) {
				if(errorMap.get(errMsg).size() == 1)
					sb.append("\nfield ");
				else
					sb.append("\nfields ");
				for(String field : this.errorMap.get(errMsg))
					sb.append(field + " ");
				sb.append( "- " + errMsg);
			}
			MessageSender.sendMessage(this.getContext(), sb.toString());
		}
		isLoginIssue = false;
	}

	
	
}
