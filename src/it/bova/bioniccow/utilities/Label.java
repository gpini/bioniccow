package it.bova.bioniccow.utilities;

public class Label {
	private String rule;
	private String unruledTag;
	public Label(String rule, String unruledTag) {
		super();
		this.rule = rule;
		this.unruledTag = unruledTag;
	}
	public String getRule() {
		return rule;
	}
	public String getUnruledTag() {
		return unruledTag;
	}
	public String toString() {return this.unruledTag;}
	
}

