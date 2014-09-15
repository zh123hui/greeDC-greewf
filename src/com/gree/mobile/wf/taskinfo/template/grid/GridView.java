package com.gree.mobile.wf.taskinfo.template.grid;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;

import com.gree.mobile.wf.taskinfo.template.View;

public class GridView  implements View{

	private String title;
	private List<CellUI> cells = new ArrayList<CellUI>();
	private List<GridView> subGridView = new ArrayList<GridView>();
	private String viewId;
	private GridView parent;
	public void addCell(CellUI cellUI){
		cells.add(cellUI);
	}
	public void addGridView(GridView gridView){
		gridView.setParent(this);
		subGridView.add(gridView);
	}
	public List<CellUI> getCells() {
		return cells;
	}
	public void setCells(List<CellUI> cells) {
		this.cells = cells;
	}
	public List<GridView> getSubGridView() {
		return subGridView;
	}
	public void setSubGridView(List<GridView> subGridView) {
		this.subGridView = subGridView;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getViewId() {
		if(viewId==null){
			viewId=RandomStringUtils.random(4, "abcdefghijklmnopquvwxyz");
		}
		return viewId;
	}
	public void setViewId(String viewId) {
		this.viewId = viewId;
	}
	public GridView getParent() {
		return parent;
	}
	public void setParent(GridView parent) {
		this.parent = parent;
	}
	
}
