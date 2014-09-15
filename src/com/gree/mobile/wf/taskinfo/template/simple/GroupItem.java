package com.gree.mobile.wf.taskinfo.template.simple;

import java.util.ArrayList;
import java.util.List;

public class GroupItem {

	private String title;
	private List<PropItem> items=new ArrayList<PropItem>();
	
	public GroupItem(String title) {
		super();
		this.title = title;
	}
	public void addItem(PropItem item){
		items.add(item);
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<PropItem> getItems() {
		return items;
	}
	public void setItems(List<PropItem> items) {
		this.items = items;
	}
	
}
