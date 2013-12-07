package it.bova.bioniccow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.actionbarsherlock.app.SherlockFragment;

import it.bova.bioniccow.asyncoperations.rtmobjects.DBFolderGetter;
import it.bova.bioniccow.data.Folder;
import it.bova.bioniccow.data.Folders_old2;
import it.bova.bioniccow.utilities.ImprovedArrayAdapter;
import it.bova.bioniccow.utilities.SmartClickListener;
import it.bova.bioniccow.utilities.SmartDialogInterfaceClickListener;
import it.bova.bioniccow.utilities.SmartLongClickListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FolderListFragment extends SherlockFragment implements InterProcess{

	private ListView lv;
	private FolderAdapter adapter;

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.specials,
				container, false);

		this.adapter = new FolderAdapter(this.getSherlockActivity(), new ArrayList<Folder>());
		this.lv = (ListView) view.findViewById(R.id.list);
		LayoutInflater inflater2
		= (LayoutInflater) this.getSherlockActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View footer = inflater2.inflate(R.layout.folder_footer, null);
		Button b = (Button) footer.findViewById(R.id.footerText);
		b.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FolderListFragment.this.getSherlockActivity(), FolderEditActivity.class);
				FolderListFragment.this.startActivity(intent);
			}
		});
		this.lv.addFooterView(footer);
		this.lv.setAdapter(this.adapter);
		this.registerForContextMenu(lv);

		return view;

	}


	@Override public void onResume() {
		super.onResume();  
		this.refresh();
	}

	
	public void refresh() {
		DBFolderGetter fg = new DBFolderGetter(this.getSherlockActivity()) {
			@Override protected void onPostExecute(List<Folder> folders) {
				Collections.sort(folders, new FolderComparator());
				FolderListFragment.this.adapter.reloadAndNotify(folders);	
			}
		};
		fg.execute();
	}

	private class FolderAdapter extends ImprovedArrayAdapter<Folder> {

		private FolderAdapter(Context context, List<Folder> folders) {
			super(context, 0, folders);
		}		

		private class ViewHolder {
			public TextView tv;
		}

		@Override public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				LayoutInflater inflater 
				= (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.folder_row, null);
				ViewHolder childHolder = new ViewHolder();
				childHolder.tv = (TextView) convertView.findViewById(R.id.text);
				convertView.setTag(childHolder);
			}
			ViewHolder holder = (ViewHolder) convertView.getTag();
			Folder folder = this.getItem(position);
			if(folder != null) holder.tv.setText(folder.getName());
			else holder.tv.setText("-----");
			convertView.setOnClickListener(new SmartClickListener<String>(folder.getName()) {
				@Override public void onClick(View v) {
					Bundle bundle = new Bundle();
					bundle.putString("folder", this.get());
					FolderFragment fragment = new FolderFragment();
					fragment.setArguments(bundle);
					FolderListFragment.this.getActivity().getSupportFragmentManager()
						.beginTransaction()
						.addToBackStack(null)
						.replace(R.id.fragmentContainer, fragment, FOLDER_FRAGMENT)				
						.commit();
				}
			});
			convertView.setOnLongClickListener(new SmartLongClickListener<String>(folder.getName()) {
				@Override public boolean onLongClick(View v) {
					String folderName = this.get()[0];
					FolderListFragment.this.showEditDeleteDialog(folderName);
					return true;
				}
			});
			return convertView;
		}

	}
	
	void showEditDeleteDialog(String folderName) {
	    DialogFragment newFragment = EditDeleteDialogFragment.newInstance();
	    Bundle bundle = new Bundle();
		bundle.putString("folder", folderName);
		newFragment.setArguments(bundle);
	    newFragment.show(this.getFragmentManager(), "edit_delete");
	}
	
	public static class EditDeleteDialogFragment extends DialogFragment {

	    public static EditDeleteDialogFragment newInstance() {
	    	EditDeleteDialogFragment frag = new EditDeleteDialogFragment();
	    	return frag;
	    }
	    
	    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
	    	String modifyFolder = this.getResources().getString(R.string.modify_folder);
			String folderName = this.getArguments().getString("folder");
			final Dialog dialog = new AlertDialog.Builder(this.getActivity()).setTitle(modifyFolder).setCancelable(true)
					.setItems(R.array.edit_del_options, new SmartDialogInterfaceClickListener<String>(folderName) {
						@Override public void onClick(DialogInterface dialoginterface, int i) {
							String name = this.get();
							switch(i) {
							case 0 :
								Intent intent = new Intent(EditDeleteDialogFragment.this.getActivity(),FolderEditActivity.class);
								intent.putExtra("name", name);
								EditDeleteDialogFragment.this.dismiss();
								EditDeleteDialogFragment.this.getActivity().startActivity(intent);
								break;
							case 1 :
								Folders_old2 folders = new Folders_old2(EditDeleteDialogFragment.this.getActivity());
								Map<String,Folder> folderMap = folders.retrieveAsMap();
								folderMap.remove(name);
								folders.saveAndNotifyAsList(folderMap);
								EditDeleteDialogFragment.this.dismiss();
								break;
							default :
								Toast.makeText(EditDeleteDialogFragment.this.getActivity(), "unknown action", Toast.LENGTH_SHORT).show();
								break;
							}
						}
					}).create();
			return dialog;
	    	
	    }
	}
	
	private class FolderComparator implements Comparator<Folder> {
		@Override
		public int compare(Folder folder1, Folder folder2) {
			return folder1.getName().compareToIgnoreCase(folder2.getName());
		}
	}


}
