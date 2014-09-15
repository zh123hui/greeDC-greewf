package com.gree.mobile.wf.taskinfo.xml.grid;

import java.util.ArrayList;
import java.util.List;

import com.gree.mobile.wf.taskinfo.xml.ViewNode;

public class GridViewNode extends ViewNode {

	private List<CellNode> cellNodes=new ArrayList<CellNode>();
	private List<GridViewNode> subGridViews;
	private String propName;
	
	public String getPropName() {
		return propName;
	}

	public void setPropName(String propName) {
		this.propName = propName;
	}

	public void initSubGridViews(){
		subGridViews = new ArrayList<GridViewNode>();
	}
	public void addCellNode(CellNode node){
		cellNodes.add(node);
	}
	public void addSubGridView(GridViewNode node){
		subGridViews.add(node);
	}

	public List<CellNode> getCellNodes() {
		return cellNodes;
	}

	public List<GridViewNode> getSubGridViews() {
		return subGridViews;
	}

	
}
