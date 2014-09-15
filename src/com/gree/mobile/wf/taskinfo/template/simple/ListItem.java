package com.gree.mobile.wf.taskinfo.template.simple;

import java.util.ArrayList;
import java.util.List;

public class ListItem{

	private String title;
	private List<GroupItem> items=new ArrayList<GroupItem>();
	
	public ListItem(String title) {
		super();
		this.title = title;
	}

	public void addGroupItem(GroupItem item){
		items.add(item);
	}
	
	public List<GroupItem> getItems() {
		return items;
	}

	public String getTitle() {
		return title;
	}

}
