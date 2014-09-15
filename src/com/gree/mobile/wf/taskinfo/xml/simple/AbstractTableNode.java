package com.gree.mobile.wf.taskinfo.xml.simple;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTableNode {
	private String title;
	
	private List<RowItemNode> items=new ArrayList<RowItemNode>();
	
	public void addItem(RowItemNode item) {
		this.items.add(item);
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<RowItemNode> getItems() {
		return items;
	}
	public void setItems(List<RowItemNode> items) {
		this.items = items;
	}
	
}
