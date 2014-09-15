package com.gree.mobile.wf.taskinfo.template.grid;

import java.util.ArrayList;
import java.util.List;

public class IteratorCell implements CellUI {

	private List<List<PropCell>> propCellList=new ArrayList<List<PropCell>>();

	public void addPropCells(List<PropCell> propCells){
		this.propCellList.add(propCells);
	}
	public List<List<PropCell>> getPropCellList() {
		return propCellList;
	}
	
}
