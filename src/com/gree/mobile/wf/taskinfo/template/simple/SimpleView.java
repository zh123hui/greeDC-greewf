package com.gree.mobile.wf.taskinfo.template.simple;

import java.util.ArrayList;
import java.util.List;

import com.gree.mobile.wf.taskinfo.template.View;

public class SimpleView implements View{

	private String bosTypeName;
	private List<GroupItem> groupItems=new ArrayList<GroupItem>();
	private List<ListItem> listItems=new ArrayList<ListItem>();
	
	public String getBosTypeName() {
		return bosTypeName;
	}
	public void setBosTypeName(String bosTypeName) {
		this.bosTypeName = bosTypeName;
	}
	public List<GroupItem> getGroupItems() {
		return groupItems;
	}
	public void addGroupItem(GroupItem groupItem) {
		this.groupItems.add(groupItem);
	}
	public void addListItem(ListItem listItem) {
		this.listItems.add(listItem);
	}
	public void setGroupItems(List<GroupItem> groupItems) {
		this.groupItems = groupItems;
	}
	public List<ListItem> getListItems() {
		return listItems;
	}
	public void setListItems(List<ListItem> listItems) {
		this.listItems = listItems;
	}
	
	
}
