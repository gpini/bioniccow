package it.bova.bioniccow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import it.bova.bioniccow.data.TaskLists_old2;
import it.bova.bioniccow.data.observers.TaskListObserver;
import it.bova.bioniccow.utilities.ImprovedArrayAdapter;
import it.bova.bioniccow.utilities.SmartClickListener;
import it.bova.bioniccow.utilities.rtmobjects.TaskListComparator;
import it.bova.rtmapi.TaskList;
import com.actionbarsherlock.app.SherlockFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

public class TaskListFragment extends SherlockFragment implements InterProcess {
	
	private GridView grid;
	private TaskListAdapter adapter;
	
	private TaskLists_old2 tasklists;
	private TaskListObserver listObserver;
	
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
		      Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.grid, container, false);
		grid = (GridView) view.findViewById(R.id.gridView);
		adapter = new TaskListAdapter(this.getSherlockActivity(), new ArrayList<TaskList>());
		grid.setAdapter(adapter);
		
		//"sveglia" tasklists!!
		this.tasklists = new TaskLists_old2(this.getSherlockActivity());	
		
		return view;
		
	}
	
	public void onResume() {
		super.onResume();			
						
		this.listObserver = new TaskListObserver() {
			@Override public void onDataChanged(List<TaskList> lists) {
				//Toast.makeText(TaskListActivity.this, "size: " + taskMap.size() , Toast.LENGTH_SHORT).show();
				List<TaskList> tmpLists = new ArrayList<TaskList>();
				tmpLists.addAll(lists);
				for(TaskList list : tmpLists)
					if(list.isArchived()) lists.remove(list);
				Collections.sort(tmpLists, new TaskListComparator());
				TaskListFragment.this.adapter.reloadAndNotify(tmpLists);
			}
		};
		this.tasklists.addObserver(listObserver);

		
		tasklists.retrieve();
		tasklists.notifyObservers();	

	}
	
	public void onPause() {
		super.onPause();	
		this.tasklists.removeObserver(this.listObserver);
	
	}
	
	private class TaskListAdapter extends ImprovedArrayAdapter<TaskList> {
		TaskListAdapter(Context context, List<TaskList> tasklists) {
			super(context, 0, tasklists);
		}
		
		private class TaskListViewHolder {
			public Button button;
		}
		
		@Override public View getView(int position, View convertView, ViewGroup parent) {
			TaskList tasklist = getItem(position);
			//Log.d("ciao", "" + tasklist.getName() + " " + tasklist.isSmart());
			if(convertView == null) {
				LayoutInflater inflater 
					= (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				TaskListViewHolder listHolder = new TaskListViewHolder();
				convertView = inflater.inflate(R.layout.item_button, null);
				listHolder.button = (Button) convertView.findViewById(R.id.button);
				convertView.setTag(listHolder);
			}
			TaskListViewHolder holder = (TaskListViewHolder) convertView.getTag();
			if(tasklist.isSmart()) {
				holder.button.setBackgroundResource(R.drawable.selector_smartlist);
				holder.button.setTextAppearance(TaskListFragment.this.getSherlockActivity(), R.style.smartlist);
				holder.button.setShadowLayer(1, 1, 1, R.color.black);
			}
			else {
				holder.button.setBackgroundResource(R.drawable.selector_standard);
				holder.button.setTextAppearance(TaskListFragment.this.getSherlockActivity(), R.style.standard);
				holder.button.setShadowLayer(0, 0, 0, 0);
			}
			holder.button.setText(tasklist.getName());
			holder.button.setOnClickListener(new SmartClickListener<TaskList>(tasklist) {
				public void onClick(View v){
					String id = this.get().getId();
					Intent intent = new Intent(TaskListFragment.this.getSherlockActivity(),TaskActivity.class);
					intent.putExtra(TYPE, LIST);
					intent.putExtra(NAME, "" + this.get().getName());
					intent.putExtra(IDENTIFIER, id);
					if(this.get().isSmart())
						intent.putExtra("isSmart", true);
					else
						intent.putExtra("isSmart", false);
					TaskListFragment.this.startActivity(intent);
				}
			});
			return convertView;
		}
	}
}
