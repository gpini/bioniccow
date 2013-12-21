package it.bova.bioniccow.asyncoperations.tasks;

import it.bova.bioniccow.asyncoperations.MessageSender;
import it.bova.bioniccow.asyncoperations.InquiryAnswer;
import it.bova.rtmapi.Task;

import java.util.Formatter;
import java.util.List;

import android.content.Context;

public class MultipleTaskChanger extends MultipleTaskInquirer {
	
	private boolean isLoginIssue = false;
	private int unsuccessfullModifications = 0;
	private String msg = "";
	private String OKMultiPhrase;
	private String NOKMultiPhrase;
	private int servedChangers = 0;
	

	public MultipleTaskChanger(String OKSinglePhrase, String OKMultiPhrase,
			String NOKSinglePhrase, String NOKMultiPhrase, Context context) {
		super(OKSinglePhrase, NOKSinglePhrase, context);
		this.NOKMultiPhrase = NOKMultiPhrase;
		this.OKMultiPhrase = OKMultiPhrase;

	}
	
	public String getOKMultiPhrase() {return OKMultiPhrase;}
	public String getNOKMultiPhrase() {	return NOKMultiPhrase;}

	 @Override protected void onChangePerformed(TaskInquirer servedModifier, InquiryAnswer<List<Task>> answer) {
		//executed in background thread
		Integer answerCode = answer.getCode();
		servedChangers++;
		if(answerCode != OK) {
			if(answerCode == LOGIN_ISSUE) isLoginIssue = true;
			this.unsuccessfullModifications++;
		}
		msg = answer.getMsg();
	}
	
	 @Override protected void onChangesCompleted() {
		//send messages
		if(this.isLoginIssue)
			MessageSender.sendAuthErrorMessage(this.getContext());
		else if(this.unsuccessfullModifications == 0) {
			if(servedChangers < 2)
				MessageSender.sendMessage(this.getContext(), this.getOKPhrase());
			else {
				Formatter formatter = new Formatter();
				String oKMulti = formatter.format(getOKMultiPhrase(), servedChangers).toString();
				formatter.close();
				MessageSender.sendMessage(this.getContext(), oKMulti);
			}
		}
		else if(this.unsuccessfullModifications == 1) {
			MessageSender.sendMessage(this.getContext(), this.getNOKPhrase() + " - " + msg);
		}
		else {
			Formatter formatter = new Formatter();
			String nOKMulti = formatter.format(getNOKMultiPhrase(), unsuccessfullModifications).toString();
			formatter.close();
			MessageSender.sendMessage(this.getContext(), nOKMulti + " - " + msg);
		}
		isLoginIssue = false;
	}







		
}
