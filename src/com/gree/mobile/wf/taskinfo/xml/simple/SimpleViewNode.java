package com.gree.mobile.wf.taskinfo.xml.simple;

import java.util.ArrayList;
import java.util.List;

import com.gree.mobile.wf.taskinfo.xml.ViewNode;

public class SimpleViewNode extends ViewNode {

	private boolean hiddenIfEmpty=true;
	private List<AbstractTableNode> tables=new ArrayList<AbstractTableNode>();

	public List<AbstractTableNode> getTables() {
		return tables;
	}
	public void addTable(AbstractTableNode table) {
		this.tables.add(table);
	}
	public void setTables(List<AbstractTableNode> tables) {
		this.tables = tables;
	}
	public boolean isHiddenIfEmpty() {
		return hiddenIfEmpty;
	}
	public void setHiddenIfEmpty(boolean hiddenIfEmpty) {
		this.hiddenIfEmpty = hiddenIfEmpty;
	}
	
}
