package it.bova.bioniccow.asyncoperations;

import it.bova.bioniccow.InterProcess;
import android.content.Context;

public abstract class DefaultInquirer<Param, Result> extends Inquirer<Param, Result> implements InterProcess {

	private String NOKPhrase;
	private String OKPhrase;
	
	public DefaultInquirer(String OKPhrase, String NOKPhrase, Context context) {
		super(context);
		this.NOKPhrase = NOKPhrase;
		this.OKPhrase = OKPhrase;
	}

	public String getNOKPhrase() {
		return this.NOKPhrase;
	}
	
	public String getOKPhrase() {
		return this.OKPhrase;
	}
	
	
	@Override protected void onLoginIssue() {
		MessageSender.sendAuthErrorMessage(this.getContext());
	}
	
	@Override protected void onMissingInternet(String msg) {
		MessageSender.sendMessage(this.getContext(), getNOKPhrase() + msg);
	}
	
	@Override protected void onServerError(int code, String msg) {
		MessageSender.sendMessage(this.getContext(), getNOKPhrase() + "(" +
			code + ") " + msg);
	}

	@Override protected void onApiError(String msg) {
		MessageSender.sendMessage(this.getContext(), getNOKPhrase() + msg);
	}
	
	@Override protected void onGenericException(String msg) {
		MessageSender.sendMessage(this.getContext(), getNOKPhrase() + msg);
	}
		


}
