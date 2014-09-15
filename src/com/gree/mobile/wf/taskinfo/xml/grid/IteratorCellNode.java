package com.gree.mobile.wf.taskinfo.xml.grid;

import java.util.ArrayList;
import java.util.List;

public class IteratorCellNode implements CellNode {
	private String propName;
	private List<PropCellNode> propCellNodes=new ArrayList<PropCellNode>();
	
	public void addPropCellNode(PropCellNode note){
		if(propCellNodes.size()>=4){
			return ;
		}
		propCellNodes.add(note);
	}

	public List<PropCellNode> getPropCellNodes() {
		return propCellNodes;
	}

	public String getPropName() {
		return propName;
	}

	public void setPropName(String propName) {
		this.propName = propName;
	}
}
