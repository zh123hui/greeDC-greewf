package com.gree.mobile.wf.taskinfo.template.grid;

import java.util.ArrayList;
import java.util.List;

public class RowCell implements CellUI {

	private List<PropCell> propCells=new ArrayList<PropCell>();

	
	public void setPropCells(List<PropCell> propCells) {
		this.propCells = propCells;
	}
	public List<PropCell> getPropCells() {
		return propCells;
	}
	public void addPropCell(PropCell propCell){
		if(propCells.size()>=2){
			return;
		}
		propCells.add(propCell);
	}
	
	
	
}
