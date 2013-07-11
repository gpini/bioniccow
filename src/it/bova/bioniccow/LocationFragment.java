package it.bova.bioniccow;

import it.bova.bioniccow.data.Locations;
import it.bova.bioniccow.data.observers.LocationObserver;
import it.bova.bioniccow.utilities.ImprovedArrayAdapter;
import it.bova.bioniccow.utilities.SmartClickListener;
import it.bova.bioniccow.utilities.rtmobjects.LocationComparator;
import it.bova.rtmapi.Location;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.actionbarsherlock.app.SherlockFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

public class LocationFragment extends SherlockFragment implements InterProcess{
	
	private GridView grid;
	private LocationAdapter adapter;
	
	private Locations locations;
	private LocationObserver locationObserver;
	
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
		      Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.grid,
		        container, false);
		grid = (GridView) view.findViewById(R.id.gridView);
		adapter = new LocationAdapter(this.getSherlockActivity(), new ArrayList<Location>());
		grid.setAdapter(adapter);
		
		//"sveglia" locations!!
		this.locations = new Locations(this.getSherlockActivity());	
		
		return view;
		
	}
	
	public void onResume() {
		super.onResume();
		
		this.locationObserver = new LocationObserver() {
			public void onDataChanged(List<Location> locations) {
				List<Location> tmpLocs = new ArrayList<Location>();
				tmpLocs.addAll(locations);
				Collections.sort(tmpLocs, new LocationComparator());
				LocationFragment.this.adapter.reloadAndNotify(tmpLocs);
			}
		};
		this.locations.addObserver(locationObserver);

		locations.retrieve();
		locations.notifyObservers();	

	}
	
	public void onPause() {
		super.onPause();	
		this.locations.removeObserver(this.locationObserver);
	
	}
	
	private class LocationAdapter extends ImprovedArrayAdapter<Location> {
		LocationAdapter(Context context, List<Location> locations) {
			super(context, 0, locations);
		}

		private class LocationViewHolder {
			Button button;
		}
		
		@Override public View getView(int position, View convertView, ViewGroup parent) {
			Location location = getItem(position);
			if(convertView == null) {
				LayoutInflater inflater 
					= (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.item_button, null); 
				LocationViewHolder locHolder = new LocationViewHolder();
				locHolder.button = (Button) convertView.findViewById(R.id.button);
				locHolder.button.setBackgroundResource(R.drawable.selector_standard);
				locHolder.button.setTextAppearance(LocationFragment.this.getSherlockActivity(), R.style.location);
				locHolder.button.setShadowLayer(1, 1, 1, R.color.black);
				convertView.setTag(locHolder);
			}
			LocationViewHolder holder = (LocationViewHolder) convertView.getTag();
			holder.button.setText(getItem(position).getName());
			holder.button.setOnClickListener(new SmartClickListener<Location>(location) {
				public void onClick(View v){
					Location loc = this.get();
					Intent intent = new Intent(LocationFragment.this.getSherlockActivity(),TaskActivity.class);
					intent.putExtra(TYPE, LOCATION);
					intent.putExtra(NAME, "" + loc.getName());
					intent.putExtra(FILTER, "location:\"" + loc.getName() + "\"");
					intent.putExtra(IDENTIFIER, loc.getId());
					LocationFragment.this.startActivity(intent);
					//Toast.makeText(TaskListActivity.this,text,Toast.LENGTH_SHORT).show();
				}
			});
			return convertView;
		}
	}
}
