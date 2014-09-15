package com.gree.mobile.wf.taskinfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.gree.mobile.wf.taskinfo.xml.TaskInfoNode;
import com.gree.mobile.wf.taskinfo.xml.ViewNode;
import com.gree.mobile.wf.taskinfo.xml.grid.CellNode;
import com.gree.mobile.wf.taskinfo.xml.grid.GridViewNode;
import com.gree.mobile.wf.taskinfo.xml.grid.IteratorCellNode;
import com.gree.mobile.wf.taskinfo.xml.grid.PropCellNode;
import com.gree.mobile.wf.taskinfo.xml.grid.RowCellNode;
import com.gree.mobile.wf.taskinfo.xml.simple.AbstractTableNode;
import com.gree.mobile.wf.taskinfo.xml.simple.ListTableNode;
import com.gree.mobile.wf.taskinfo.xml.simple.RowItemNode;
import com.gree.mobile.wf.taskinfo.xml.simple.SimpleViewNode;

public class TaskInfoConfig {

	private String fileName;
	private List<String> selectorItems;
	private ViewNode viewNote;
	
	public TaskInfoConfig(TaskInfoNode taskInfoNote, String fileName) {
		super();
		if(taskInfoNote==null || StringUtils.isEmpty(fileName)){
			throw new RuntimeException("参数不能为空");
		}
		this.fileName = fileName;
		this.viewNote = taskInfoNote.getViewNode("");
		if(StringUtils.isEmpty(this.viewNote.getTitle())){
			this.viewNote.setTitle(taskInfoNote.getBosTypeName());
		}
		this.selectorItems = findSelectorItems(viewNote);
	}
	
	public List<String> getSelectorItems() {
		return selectorItems;
	}

	private List<String> findSelectorItems(ViewNode view) {
		if(view==null){
			return Collections.emptyList();
		}
		if(view instanceof SimpleViewNode){
			List<AbstractTableNode> tables = ((SimpleViewNode)view).getTables();
			if(tables==null || tables.size()==0){
				return Collections.emptyList();
			}
			List<String> fields = new ArrayList<String>();
			for(AbstractTableNode t : tables){
				String prefix = null;
				List<RowItemNode> items = t.getItems();
				if(CollectionUtils.isEmpty(items)){
					continue;
				}
				if(t instanceof ListTableNode){
					prefix = ((ListTableNode)t).getPropName();
				}
				for(RowItemNode item : items){
					fields.add(getFullPropName(prefix, item.getPropName()));
				}
			}
			return fields;
		}
		if(view instanceof GridViewNode){
			GridViewNode gridView = (GridViewNode) view;
			List<String> fields = new ArrayList<String>();
			fields.addAll(findFields(gridView.getCellNodes(), null));
			List<GridViewNode> subGridViews = gridView.getSubGridViews();
			for(GridViewNode gvn : subGridViews){
				fields.addAll(findFields(gvn.getCellNodes(), gvn.getPropName()));
			}
			return fields;
		}
		return Collections.emptyList();
	}

	private List<String> findFields(List<CellNode> cellNodes, String prefix) {
		if(CollectionUtils.isEmpty(cellNodes)){
			return Collections.emptyList();
		}
		List<String> fields = new ArrayList<String>();
		for(CellNode cellNode : cellNodes){
			List<PropCellNode> propCellNodes = null;
			if(cellNode instanceof IteratorCellNode){
				prefix = getFullPropName(prefix, ((IteratorCellNode) cellNode).getPropName());
				propCellNodes = ((IteratorCellNode) cellNode).getPropCellNodes();
			}else if(cellNode instanceof RowCellNode){
				propCellNodes = ((RowCellNode) cellNode).getPropCellNodes();
			}else if(cellNode instanceof PropCellNode){
				propCellNodes = Collections.singletonList((PropCellNode) cellNode);
			}
			for(PropCellNode prop : propCellNodes){
				fields.add(getFullPropName(prefix, prop.getPropName()));
			}
		}
		return fields;
	}
	
	private String getFullPropName(String prefix, String propName){
		if (StringUtils.isEmpty(prefix)) {
			return propName;
		}
		return prefix + "." + propName;
	}
	
	public ViewNode getViewNote() {
		return viewNote;
	}

	public String getFileName() {
		return fileName;
	}

}
