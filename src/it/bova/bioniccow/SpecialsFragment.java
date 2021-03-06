package it.bova.bioniccow;

import com.actionbarsherlock.app.SherlockFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SpecialsFragment extends SherlockFragment implements InterProcess, OnItemClickListener{
	
	private String[] specials;
	
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
		      Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.specials,
		        container, false);
		
		specials = this.getResources().getStringArray(R.array.specials);
		ListAdapter adapter = new ArrayAdapter<String>(this.getSherlockActivity(), R.layout.specials_row, R.id.text, specials);
		ListView lv = (ListView) view.findViewById(R.id.list);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);

		return view;

	}

	@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this.getSherlockActivity(),TaskActivity.class);
		switch(position) {
		case 0 : 
			((BionicCowActivity) SpecialsFragment.this.getSherlockActivity()).openTaskFragment(NO_TAG, "", "", false);
			break;
		case 1 :
			((BionicCowActivity) SpecialsFragment.this.getSherlockActivity()).openTaskFragment(NO_LOCATION, "", "", false);
			break;
		case 2 : 
			((BionicCowActivity) SpecialsFragment.this.getSherlockActivity()).openTaskFragment(RECENTLY_COMPLETED, "", "", false);
			break;
		case 3 :
			((BionicCowActivity) SpecialsFragment.this.getSherlockActivity()).openTaskFragment(WITH_PRIORITY, "", "", false);
			break;
		case 4 :
			FolderFragment fragment = new FolderFragment();
			Bundle bundle = new Bundle();
			bundle.putSerializable("folder", null);
			fragment.setArguments(bundle);
			this.getActivity().getSupportFragmentManager()
				.beginTransaction()
				.addToBackStack(null)	
				.replace(R.id.fragmentContainer, fragment, FOLDER_FRAGMENT)				
				.commit();
//			SmartFragmentManager.startFragmentToBackStack(SpecialsFragment.this.getSherlockActivity(),
//					fragment,
//					R.id.fragmentContainer, FOLDER_FRAGMENT);
			break;
		default : Toast.makeText(this.getSherlockActivity(), R.string.coming_soon, Toast.LENGTH_SHORT).show();
		}

	}




}
