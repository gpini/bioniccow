package it.bova.bioniccow.asyncoperations.tasks;

import it.bova.bioniccow.asyncoperations.ErrorCoded;
import it.bova.bioniccow.asyncoperations.InquiryAnswer;
import it.bova.bioniccow.asyncoperations.sync.Synchronizer;
import it.bova.rtmapi.Task;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import android.content.Context;
import android.os.AsyncTask;

public class MultipleTaskInquirer implements ErrorCoded {

	private Queue<TaskInquirer> taskInquirers;
	private Queue<TaskInquirer> servedTaskInquirers;
	private Queue<InquiryAnswer<List<Task>>> answers;
	private Context context;
	private String OKPhrase;
	private String NOKPhrase;
	private HashMap<String,Task> modifiedTasks;

	public MultipleTaskInquirer(String OKPhrase, String NOKPhrase, Context context) {
		this.context = context;
		this.OKPhrase = OKPhrase;
		this.NOKPhrase = NOKPhrase;
		taskInquirers = new LinkedList<TaskInquirer>();
		modifiedTasks = new HashMap<String,Task>();
		servedTaskInquirers = new LinkedList<TaskInquirer>(); 
		answers = new LinkedList<InquiryAnswer<List<Task>>>();
	}

	public void setContext(Context context) {this.context = context;}
	public Context getContext() {return this.context;}	
	public String getOKPhrase() {
		return OKPhrase;
	}
	public void setOKPhrase(String OKPhrase) {
		this.OKPhrase = OKPhrase;
	}
	public String getNOKPhrase() {
		return NOKPhrase;
	}
	public void setNOKPhrase(String NOKPhrase) {
		this.NOKPhrase = NOKPhrase;
	}
	
	//private HashMap<String,Task> getModifiedTasks() {return this.modifiedTasks;}

	public boolean add(TaskInquirer taskInquirer) {
		taskInquirers.offer(taskInquirer);
		return true;
	}
	public void remove(TaskInquirer inquirer) {taskInquirers.remove(inquirer);}
	public void clear() {taskInquirers.clear();}
	public int size() {return taskInquirers.size();}

	private AsyncTask<Void,Void,HashMap<String,Task>> asyncTask = 
			new AsyncTask<Void,Void,HashMap<String,Task>>() {
		@Override protected HashMap<String,Task> doInBackground(Void... empty) {
			//execute all changes
			while(!taskInquirers.isEmpty()) {
				TaskInquirer taskInquirer = taskInquirers.poll();
				answers.offer(taskInquirer.executeSynchronously());
				servedTaskInquirers.offer(taskInquirer);
			}

			//verify answers
			MultipleTaskInquirer.this.modifiedTasks.clear();
			while(!answers.isEmpty()) {
				InquiryAnswer<List<Task>> answer = answers.poll();
				TaskInquirer taskInquirer = servedTaskInquirers.poll();
				if(answer.getCode() == OK) {
					for(Task task : answer.getResult()) {
						modifiedTasks.put(task.getId(),task);
					}
				}
				MultipleTaskInquirer.this.onChangePerformed(taskInquirer, answer);
			}

			MultipleTaskInquirer.this.onChangesCompleted();

			taskInquirers.clear();
			servedTaskInquirers.clear();
			answers.clear();

			return modifiedTasks;


		}

		@Override public void onPreExecute() {
			MultipleTaskInquirer.this.onPreExecute();
		}
		@Override public void onPostExecute(HashMap<String,Task> modifiedTasks) {
			MultipleTaskInquirer.this.onPostExecute(modifiedTasks);
		}

	};
	
	public void execute() {this.asyncTask.execute();}
	
	protected void onPreExecute() {
		//executed in calling thread
	}
	
	protected void onPostExecute(HashMap<String,Task> changedTasks) {
		if(changedTasks.size() != 0) {
			Synchronizer synchronizer = new Synchronizer(this.getContext());
			synchronizer.syncChangedTasks(changedTasks.values());
		}
	}
	
	protected void onChangePerformed(TaskInquirer servedModifier, InquiryAnswer<List<Task>> answer) {
		//executed in background thread
	}
	
	protected void onChangesCompleted() {
		//executed in background thread
	}

	
}
