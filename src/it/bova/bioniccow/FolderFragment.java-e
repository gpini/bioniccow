package it.bova.bioniccow;

import it.bova.bioniccow.asyncoperations.rtmobjects.DBFolderGetter;
import it.bova.bioniccow.asyncoperations.rtmobjects.DBLocationsGetter;
import it.bova.bioniccow.asyncoperations.rtmobjects.DBTagGetter;
import it.bova.bioniccow.asyncoperations.rtmobjects.DBTaskListsGetter;
import it.bova.bioniccow.data.Folder;
import it.bova.bioniccow.utilities.ImprovedArrayAdapter;
import it.bova.bioniccow.utilities.SmartClickListener;
import it.bova.bioniccow.utilities.rtmobjects.LocationComparator;
import it.bova.bioniccow.utilities.rtmobjects.TaskListComparator;
import it.bova.rtmapi.Location;
import it.bova.rtmapi.TaskList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.actionbarsherlock.app.SherlockFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class FolderFragment extends SherlockFragment implements InterProcess {
	
	private GridView grid;
	private FolderAdapter adapter;

	private Folder folder;
	private Set<String> tagSet;
	private Map<String,TaskList> listMap;
	private Map<String,Location> locMap;
	
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
		      Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.folder_grid,
		        container, false);
		grid = (GridView) view.findViewById(R.id.gridView);
		adapter = new FolderAdapter(this.getSherlockActivity(), new ArrayList<FolderElement>());
		grid.setAdapter(adapter);
		
		folder = (Folder) this.getArguments().getSerializable("folder");
		TextView folderTitle = (TextView) view.findViewById(R.id.folderTitle);
		if(folder != null)
			folderTitle.setText(folder.getName());
		else
			folderTitle.setText(R.string.noFolder);
		
		return view;
		
		
	}
	
	public void onResume() {
		super.onResume();
		
		this.refresh();		

	}

	
	public void refresh() {
		final DBFolderGetter fg = new DBFolderGetter(this.getSherlockActivity()) {
			@Override protected void onPostExecute(List<Folder> folders) {
				if(folder != null) {
					Folder tmpFolder = null;
					for(Folder fold : folders) {
						if(fold.getName().equals(folder.getName()))
							tmpFolder = fold;
					}
					if(tmpFolder != null) {
						List<FolderElement> tmpElementList = new ArrayList<FolderElement>();
						Folder.Applicability applicability = tmpFolder.getApplicability();
						if(applicability == Folder.Applicability.TAGS || applicability == Folder.Applicability.EVERYTHING) {
							List<String> tagElements = tmpFolder.loadTagElements(tagSet);
							for(String tag : tagElements)
								tmpElementList.add(new FolderElement(tag,FolderElement.Type.TAG));
						}
						if(applicability == Folder.Applicability.LISTS || applicability == Folder.Applicability.EVERYTHING) {
							List<String> listElements = tmpFolder.loadListElements(listMap);
							for(String listId : listElements)
								tmpElementList.add(new FolderElement(listId,FolderElement.Type.LIST));
						}
						if(applicability == Folder.Applicability.LOCATIONS || applicability == Folder.Applicability.EVERYTHING) {
							List<String> locElements = tmpFolder.loadLocationElements(locMap);
							for(String locId : locElements)
								tmpElementList.add(new FolderElement(locId,FolderElement.Type.LOCATION));
						}
					    Collections.sort(tmpElementList, new FolderElementComparator());
					    FolderFragment.this.adapter.reloadAndNotify(tmpElementList);
					}
					else {
						Toast.makeText(FolderFragment.this.getSherlockActivity(), "Folder not available", Toast.LENGTH_SHORT).show();
						FolderFragment.this.adapter.reloadAndNotify(new ArrayList<FolderElement>());
					}
				}
				else { //NO FOLDER (Specials)
					Collection<Folder> folderColl = folders;
					Map<String,Location> tmpLocMap = new HashMap<String,Location>();
					tmpLocMap.putAll(locMap);
					Map<String,TaskList> tmpListMap = new HashMap<String,TaskList>();
					tmpListMap.putAll(listMap);
					Set<String> tmpTagSet = new TreeSet<String>();
					tmpTagSet.addAll(tagSet);
					List<String> folderTags;
					List<String> folderLists;
					List<String> folderLocations;
					for(Folder folder : folderColl) {
						switch(folder.getApplicability()) {
						case TAGS :
							folderTags = folder.loadTagElements(tagSet);
							for(String tag : folderTags)
								tmpTagSet.remove(tag);
							break;
						case LISTS :
							folderLists = folder.loadListElements(listMap);
							for(String tag : folderLists)
								tmpListMap.remove(tag);
							break;
						case LOCATIONS :
							folderLocations = folder.loadLocationElements(locMap);
							for(String tag : folderLocations)
								tmpLocMap.remove(tag);
							break;
						case EVERYTHING :
							folderTags = folder.loadTagElements(tagSet);
							for(String tag : folderTags)
								tmpTagSet.remove(tag);
							folderLists = folder.loadListElements(listMap);
							for(String tag : folderLists)
								tmpListMap.remove(tag);
							folderLocations = folder.loadLocationElements(locMap);
							for(String tag : folderLocations)
								tmpLocMap.remove(tag);
							break;
						}
					}
					List<FolderElement> elementsNotInFolders = new ArrayList<FolderElement>();
					for(String tag : tmpTagSet)
						elementsNotInFolders.add(new FolderElement(tag,FolderElement.Type.TAG));
				    for(TaskList tasklist : tmpListMap.values())
				    	elementsNotInFolders.add(new FolderElement(tasklist.getId(),FolderElement.Type.LIST));
				    for(Location location : tmpLocMap.values())
				    	elementsNotInFolders.add(new FolderElement(location.getId(),FolderElement.Type.LOCATION));
				    Collections.sort(elementsNotInFolders, new FolderElementComparator());
					FolderFragment.this.adapter.reloadAndNotify(elementsNotInFolders);
				}
			}
		};
		final DBTaskListsGetter tlg = new DBTaskListsGetter(this.getSherlockActivity()) {
			@Override protected void onPostExecute(List<TaskList> tasklists) {
				FolderFragment.this.listMap = new HashMap<String,TaskList>();
				for(TaskList list : tasklists)
					FolderFragment.this.listMap.put(list.getId(), list);
				fg.execute();
			}
		};
		final DBLocationsGetter lg = new DBLocationsGetter(this.getSherlockActivity()) {
			@Override protected void onPostExecute(List<Location> locations) {
				FolderFragment.this.locMap = new HashMap<String,Location>();
				for(Location loc : locations)
					FolderFragment.this.locMap.put(loc.getId(), loc);
				tlg.execute();
			}
		};
		final DBTagGetter tg = new DBTagGetter(this.getSherlockActivity()) {
			@Override protected void onPostExecute(Set<String> tags) {
				FolderFragment.this.tagSet = tags;
				lg.execute();
			}
		};
		tg.execute();
	}
	
	private static class FolderElement {
		public String nameOrId;
		public Type type;
		public enum Type {
			LIST,LOCATION,TAG;
		}
		public FolderElement(String nameOrId, Type type) {
			this.nameOrId = nameOrId;
			this.type = type;
		}
	}
	
	private class FolderAdapter extends ImprovedArrayAdapter<FolderElement> {
		FolderAdapter(Context context, List<FolderElement> folderElementList) {
			super(context, 0, folderElementList);
		}

		private class TagViewHolder {
			Button button;
		}
		
		@Override public View getView(int position, View convertView, ViewGroup parent) {
			FolderElement element = getItem(position);
			if(convertView == null) {
				LayoutInflater inflater 
					= (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.item_button, null); 
				TagViewHolder tagHolder = new TagViewHolder();
				tagHolder.button = (Button) convertView.findViewById(R.id.button);
				convertView.setTag(tagHolder);
			}
			TagViewHolder holder = (TagViewHolder) convertView.getTag();
			switch(element.type) {
			case TAG :
				//Log.d("tag", "" + element.nameOrId);
				holder.button.setText(element.nameOrId);
				holder.button.setBackgroundResource(R.drawable.selector_standard);
				holder.button.setTextAppearance(FolderFragment.this.getSherlockActivity(), R.style.tag);
				holder.button.setShadowLayer(1, 1, 1, R.color.black);
				break;
			case LIST : 
				holder.button.setText(listMap.get(element.nameOrId).getName());
				TaskList tasklist = listMap.get(element.nameOrId);
				if(tasklist.isSmart()) {
					holder.button.setBackgroundResource(R.drawable.selector_smartlist);
					holder.button.setTextAppearance(FolderFragment.this.getSherlockActivity(), R.style.smartlist);
					holder.button.setShadowLayer(1, 1, 1, R.color.black);
				}
				else {
					holder.button.setTextAppearance(FolderFragment.this.getSherlockActivity(), R.style.standard);
					holder.button.setBackgroundResource(R.drawable.selector_standard);
				}
				break;
			case LOCATION :
				//Log.d("loc", "" + element.nameOrId);
				holder.button.setText(locMap.get(element.nameOrId).getName());
				holder.button.setBackgroundResource(R.drawable.selector_standard);
				holder.button.setTextAppearance(FolderFragment.this.getSherlockActivity(), R.style.location);
				holder.button.setShadowLayer(1, 1, 1, R.color.black);
				break;
			}
			holder.button.setOnClickListener(new SmartClickListener<FolderElement>(element) {
				public void onClick(View v){
					FolderElement element = this.get();
					Intent intent = new Intent(FolderFragment.this.getSherlockActivity(),TaskActivity.class);
					switch(element.type) {
					case TAG : 
						intent.putExtra(TYPE, TAG);
						String text = element.nameOrId;
						intent.putExtra(IDENTIFIER, text);
						intent.putExtra(NAME, text);
						//intent.putExtra(FILTER, "tag:\"" + element.nameOrId + "\"");
						//Toast.makeText(FolderActivity.this, element.nameOrId, Toast.LENGTH_SHORT).show();
						break;
					case LIST : 
						intent.putExtra(TYPE, LIST);
						TaskList tasklist = listMap.get(element.nameOrId);
						if(tasklist != null) {
							intent.putExtra(IDENTIFIER, tasklist.getId());
							intent.putExtra(NAME, tasklist.getName());
							if(tasklist.isSmart())
								intent.putExtra("isSmart", true);
							else
								intent.putExtra("isSmart", false);
						}
						//intent.putExtra(FILTER, element.nameOrId);
						//Toast.makeText(FolderActivity.this, element.nameOrId, Toast.LENGTH_SHORT).show();
						break;
					case LOCATION : 
						intent.putExtra(TYPE, LOCATION);
						intent.putExtra(NAME, "" + locMap.get(element.nameOrId).getName());
						Location loc = locMap.get(element.nameOrId);
						if(loc != null) {
							intent.putExtra(IDENTIFIER, loc.getId());
							intent.putExtra(NAME, loc.getName());
						}
						//if(loc != null)
						//	intent.putExtra(FILTER, "location:\"" + loc.getName() + "\"");
						//else intent.putExtra(FILTER, "location:\" \"");
						//Toast.makeText(FolderActivity.this, element.nameOrId, Toast.LENGTH_SHORT).show();
						break;
					}
					
					FolderFragment.this.startActivity(intent);
					//Toast.makeText(TaskListActivity.this,text,Toast.LENGTH_SHORT).show();
				}
			});
			return convertView;
		}
	}
	
	
	private class FolderElementComparator implements Comparator<FolderElement> {

		@Override public int compare(FolderElement elem1, FolderElement elem2) {
			switch(elem1.type) {
			case TAG :
				if(elem2.type == FolderElement.Type.TAG)
					return elem1.nameOrId.compareToIgnoreCase(elem2.nameOrId);
				else return -1;
			case LIST :
				if(elem2.type == FolderElement.Type.TAG)
					return 1;
				else if(elem2.type == FolderElement.Type.LIST) {
					TaskListComparator comp1 = new TaskListComparator();
					TaskList list1 = FolderFragment.this.listMap.get(elem1.nameOrId);
					TaskList list2 = FolderFragment.this.listMap.get(elem2.nameOrId);
					//Log.d("ciao", "" + list1 + " " + list2);
					//Log.d("comp", "" + comp1.compare(list1, list2));
					if(list1 != null && list2 != null)
						return comp1.compare(list1, list2);
					else return 0;
				}
				else return -1;
			case LOCATION:
				if(elem2.type != FolderElement.Type.LOCATION)
					return 1;
				else {
					LocationComparator comp2 = new LocationComparator();
					Location loc1 = FolderFragment.this.locMap.get(elem1.nameOrId);
					Location loc2 = FolderFragment.this.locMap.get(elem2.nameOrId);
					if(loc1 != null && loc2 != null)
						return comp2.compare(loc1, loc2);
					else return 0;
				}				
			default : return 0;
			}
		}
	}
}
