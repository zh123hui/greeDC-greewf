package com.gree.mobile.wf.taskinfo.xml.grid;

import java.util.ArrayList;
import java.util.List;

public class RowCellNode implements CellNode {

	private List<PropCellNode> propCellNodes=new ArrayList<PropCellNode>();
	
	public void addPropCellNode(PropCellNode note){
		if(propCellNodes.size()>=2){
			return ;
		}
		propCellNodes.add(note);
	}

	public List<PropCellNode> getPropCellNodes() {
		return propCellNodes;
	}
	
}
