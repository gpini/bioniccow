package it.bova.bioniccow;

import it.bova.bioniccow.asyncoperations.rtmobjects.DBTagGetter;
import it.bova.bioniccow.utilities.ImprovedArrayAdapter;
import it.bova.bioniccow.utilities.SmartClickListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.actionbarsherlock.app.SherlockFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

public class TagFragment extends SherlockFragment implements InterProcess{
	
	private GridView grid;
	private TagAdapter adapter;
	
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
		      Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.grid,
		        container, false);
		grid = (GridView) view.findViewById(R.id.gridView);
		adapter = new TagAdapter(this.getSherlockActivity(), new ArrayList<String>());
		grid.setAdapter(adapter);

		return view;

	}
	
	public void onResume() {
		super.onResume();
		this.refresh();

	}
	
	public void onPause() {
		super.onPause();	
	
	}
	
	public void refresh() {
		DBTagGetter tg = new DBTagGetter(this.getSherlockActivity()) {
			@Override protected void onPostExecute(Set<String> tags) {
				//Collections.sort(tags, new TaskListComparator());
				adapter.reloadAndNotify(tags);
			}
		};
		tg.execute();
	}
	
	private class TagAdapter extends ImprovedArrayAdapter<String> {
		TagAdapter(Context context, List<String> tagList) {
			super(context, 0, tagList);
		}

		private class TagViewHolder {
			Button button;
		}
		
		@Override public View getView(int position, View convertView, ViewGroup parent) {
			String tag = getItem(position);
			if(convertView == null) {
				LayoutInflater inflater 
					= (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.item_button, null); 
				TagViewHolder tagHolder = new TagViewHolder();
				tagHolder.button = (Button) convertView.findViewById(R.id.button);
				tagHolder.button.setBackgroundResource(R.drawable.selector_standard);
				tagHolder.button.setTextAppearance(TagFragment.this.getSherlockActivity(), R.style.tag);
				tagHolder.button.setShadowLayer(1, 1, 1, R.color.black);
				convertView.setTag(tagHolder);
			}
			TagViewHolder holder = (TagViewHolder) convertView.getTag();
			holder.button.setText(getItem(position));
			holder.button.setOnClickListener(new SmartClickListener<String>(tag) {
				public void onClick(View v){
					String text = this.get();
					((BionicCowActivity) TagFragment.this.getSherlockActivity()).openTaskFragment(TAG, text, text, false);
					//Toast.makeText(TaskListActivity.this,text,Toast.LENGTH_SHORT).show();
				}
			});
			return convertView;
		}
	}
}
