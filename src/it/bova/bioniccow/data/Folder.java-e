package it.bova.bioniccow.data;

import it.bova.rtmapi.Location;
import it.bova.rtmapi.TaskList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Folder implements Serializable{
	
	private String rule = "";
	private String name = "";
	private Applicability applicability = Applicability.EVERYTHING;
	private List<String> tagElements;
	private List<String> listElements;
	private List<String> locationElements;
	
	
	public enum Applicability {
		LISTS,TAGS,LOCATIONS,EVERYTHING;
	}
		
	public Folder(String name, String rule,
			Applicability applicability) {
		this.setRule(rule);
		this.name = name;
		this.applicability = applicability;
		this.tagElements = new ArrayList<String>();
		this.listElements = new ArrayList<String>();
		this.locationElements = new ArrayList<String>();
	}
	
	
	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule; //.replace(".", "\\.");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Applicability getApplicability() {
		return applicability;
	}

	public void setApplicability(Applicability applicability) {
		this.applicability = applicability;
	}

	public List<String> getTagElements() {
		return tagElements;
	}

	public void setTagElements(List<String> tagElements) {
		//Collections.sort(tagElements);
		this.tagElements = tagElements;
	}
	
	public List<String> getListElements() {
		return listElements;
	}

	public void setListElements(List<String> listElements) {
		//Collections.sort(listElements);
		this.listElements = listElements;
	}
	
	public List<String> getLocationElements() {
		return locationElements;
	}

	public void setLocationElements(List<String> locationElements) {
		//Collections.sort(locationElements);
		this.locationElements = locationElements;
	}
	
	public List<String> loadTagElements(Set<String> tagSet) {
		List<String> tags = new ArrayList<String>();
		String prefixPattern = /*"^" + */this.rule;
		for(String tag : tagSet) {
			if(findRule(prefixPattern, tag))
				tags.add(tag);
		}
		return tags;
	}
	
	public List<String> loadListElements(Map<String,TaskList> listMap) {
		List<String> lists = new ArrayList<String>();
		String prefixPattern = /*"^" + */this.rule;
		for(TaskList taskList : listMap.values()) {
			if(findRule(prefixPattern, taskList.getName()))
				lists.add(taskList.getId());
		}
		return lists;
	}
	
	public List<String> loadLocationElements(Map<String,Location> locationMap) {
		List<String> locations = new ArrayList<String>();
		String prefixPattern = this.rule;
		for(Location location : locationMap.values()) {
			if(findRule(prefixPattern, location.getName()))
				locations.add(location.getId());
		}
		return locations;
	}
	
	private boolean findRule(String prefixPattern, String label) {
		boolean IsPrefixFound = false;
		for(int i = 0; i < prefixPattern.length(); i++) {
			if(prefixPattern.charAt(i) == label.charAt(i)) {
				IsPrefixFound = true;
			}
			else {
				IsPrefixFound = false;
				break;
			}
		}
		return IsPrefixFound;
	}
	
	@Override public String toString() {
		return this.rule + " " + this.name + "(" + this.applicability + ")";
	}
	
}
