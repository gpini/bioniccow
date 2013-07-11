package it.bova.bioniccow.asyncoperations;

import it.bova.bioniccow.R;
import it.bova.bioniccow.data.ApiSingleton;
import it.bova.rtmapi.RtmApiException;
import it.bova.rtmapi.ServerException;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;

public abstract class Inquirer<Param,Result> implements ErrorCoded {
	
	private Context context;
	private boolean doing = false;
	
	public Context getContext() {return this.context;}
	
	public boolean isDoing() {return this.doing;}
	
	public Inquirer(Context context) {this.context = context;}
	
	protected abstract Result inquire(Param... params) throws IOException, ServerException, RtmApiException, Exception;
	
	protected void onPreInquiry() {}
	protected void onPostInquiry() {
		//executed before onResultObtained and onXxxError() management
	}
	
	protected void onResultObtained(Result result) {}
	protected void onLoginIssue() {}
	protected void onServerError(int code, String msg) {
		//gestisce anche timelineError non recuperati
	}
	protected void onApiError(String msg) {}
	protected void onMissingInternet(String msg) {}
	protected void onGenericException(String msg) {}
	
	private void onTimelineError() throws ServerException, RtmApiException, IOException {	
		String timeline = ApiSingleton.getApi(context).timelinesCreate();
		ApiSingleton.saveTimeline(context, timeline);
	}
	
	private class SimpleAsyncTask extends AsyncTask<Param,Void,InquiryAnswer<Result>> {
	
		@Override protected InquiryAnswer<Result> doInBackground(Param... params) {
			return Inquirer.this.inquiryProcess(params);		
		}
		
		@Override protected void onPostExecute(InquiryAnswer<Result> answer) {
			Inquirer.this.postProcessingExecute(answer);
		}

		@Override protected void onPreExecute() {
			Inquirer.this.preProcessingExecute();
		}
		
	}
	
	public final InquiryAnswer<Result> inquiryProcess(Param... params) {
		InquiryAnswer<Result> answer = new InquiryAnswer<Result>();
		String internetNOK = this.context.getResources().getString(R.string.internet_NOK);
		String timelineNOK = this.context.getResources().getString(R.string.timeline_NOK);
		try {
			answer.setResult(this.inquire(params));
			answer.setResultCode(ErrorCoded.OK);
			answer.setResultMsg("OK");
			return answer;
		} catch(IOException e1) {
			answer.setResultCode(ErrorCoded.NO_INTERNET);
			answer.setResultMsg(internetNOK);
			return answer;
		} catch(RtmApiException e2) {
			answer.setResultCode(ErrorCoded.API_ERROR);
			answer.setResultMsg(e2.getMessage());
			return answer;
		} catch(ServerException e3) {
			if(e3.getCode() == TIMELINE_ERROR) {
				InquiryAnswer<Result> answer2 = new InquiryAnswer<Result>();
				boolean isTimelineErrorHandled = false;
				try {
					this.onTimelineError();
					isTimelineErrorHandled = true;
					answer2.setResult(this.inquire(params));
					answer2.setResultCode(ErrorCoded.OK);
					answer2.setResultMsg("OK");
					return answer2;
				} catch (ServerException exc1) {
					answer2.setResultCode(exc1.getCode());
					if(isTimelineErrorHandled) answer2.setResultMsg(exc1.getMessage());
					else answer2.setResultMsg(timelineNOK + exc1.getMessage());
					return answer2;
				} catch (RtmApiException exc2) {
					answer2.setResultCode(ErrorCoded.API_ERROR);
					if(isTimelineErrorHandled) answer2.setResultMsg(exc2.getMessage());
					else answer2.setResultMsg(timelineNOK + exc2.getMessage());
					return answer2;
				} catch (IOException exc3) {
					answer2.setResultCode(ErrorCoded.NO_INTERNET);
					if(isTimelineErrorHandled) answer2.setResultMsg(internetNOK);
					else answer2.setResultMsg(timelineNOK + " - " + internetNOK);
					return answer2;
				} catch (Exception exc4) {
					answer2.setResultCode(ErrorCoded.GENERIC_EXCEPTION);
					answer2.setResultMsg(exc4.getMessage());
					return answer2;
				} 
			}
			else {
				answer.setResultCode(e3.getCode());
				answer.setResultMsg(e3.getMessage());
				return answer;
				
			}
		} catch(Exception e4) {
			answer.setResultCode(ErrorCoded.GENERIC_EXCEPTION);
			answer.setResultMsg(e4.getMessage());
			return answer;
		}
		finally {
			this.doing = false;
		}
	}
		
	public final void preProcessingExecute() {
		//executes onPreInquiry();
		this.onPreInquiry();
	}
	
	public final void postProcessingExecute(InquiryAnswer<Result> answer) {
		//executes on PostInquiry and then onXxxError or onResultObtained
		this.onPostInquiry();
		if(answer.getCode() > 0) {
			if(answer.getCode() == LOGIN_ISSUE) Inquirer.this.onLoginIssue();
			else this.onServerError(answer.getCode(), answer.getMsg());
		}
		else {
			String unknownError = this.context.getResources().getString(R.string.unknown_error);
			switch(answer.getCode()) {
				case OK : this.onResultObtained(answer.getResult()); break;
				case NO_INTERNET : this.onMissingInternet(answer.getMsg()); break;
				case API_ERROR : this.onApiError(answer.getMsg()); break;
				case GENERIC_EXCEPTION : this.onGenericException(answer.getMsg()); break;
				default : this.onGenericException(unknownError); break;
			}
		}
	}
	
	public final void executeInBackground(Param... params) {
		//execute doIt() in a worker thread
		//executes automatically onPreInquiry e on PostInquiry in calling thread
		this.doing = true;
		SimpleAsyncTask task = new SimpleAsyncTask();
		task.execute(params);
	}

	public final InquiryAnswer<Result> executeSynchronously(Param... params) {
		//execute inquire() in the calling thread
		//does not execute onPreInquiry and on PostInquiry
		this.doing = true;
		InquiryAnswer<Result> answer = this.inquiryProcess(params);
		this.doing = false;
		return answer;
	}

	
	
}
