package com.gree.mobile.wf.taskinfo;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.gree.mobile.wf.taskinfo.template.View;
import com.gree.mobile.wf.taskinfo.template.grid.GridView;
import com.gree.mobile.wf.taskinfo.template.grid.GridViewWriter;
import com.gree.mobile.wf.taskinfo.template.grid.IteratorCell;
import com.gree.mobile.wf.taskinfo.template.grid.PropCell;
import com.gree.mobile.wf.taskinfo.template.grid.RowCell;
import com.gree.mobile.wf.taskinfo.template.simple.GroupItem;
import com.gree.mobile.wf.taskinfo.template.simple.ListItem;
import com.gree.mobile.wf.taskinfo.template.simple.PropItem;
import com.gree.mobile.wf.taskinfo.template.simple.SimpleView;
import com.gree.mobile.wf.taskinfo.xml.ViewNode;
import com.gree.mobile.wf.taskinfo.xml.grid.CellNode;
import com.gree.mobile.wf.taskinfo.xml.grid.GridViewNode;
import com.gree.mobile.wf.taskinfo.xml.grid.IteratorCellNode;
import com.gree.mobile.wf.taskinfo.xml.grid.PropCellNode;
import com.gree.mobile.wf.taskinfo.xml.grid.RowCellNode;
import com.gree.mobile.wf.taskinfo.xml.simple.AbstractTableNode;
import com.gree.mobile.wf.taskinfo.xml.simple.GroupTableNode;
import com.gree.mobile.wf.taskinfo.xml.simple.ListTableNode;
import com.gree.mobile.wf.taskinfo.xml.simple.RowItemNode;
import com.gree.mobile.wf.taskinfo.xml.simple.SimpleViewNode;
import com.kingdee.bos.dao.AbstractObjectCollection;
import com.kingdee.bos.dao.IObjectValue;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class TemplateManager {

	private static final Logger logger = Logger.getLogger(TemplateManager.class);
	
	public static void write(Writer writer, View view) throws Exception {
		if(view instanceof SimpleView){
			Map<String, Object> con = new HashMap<String, Object>();
			con.put("data", view);
			write(writer, con, "com/gree/mobile/wf/taskinfo/template/simple/temp.ftl");
			return ;
		}
		if(view instanceof GridView){
			GridView gridView = (GridView) view;
			StringWriter stringWriter = new StringWriter();
			new GridViewWriter(stringWriter).writeGridView(gridView);
			Map<String, Object> con = new HashMap<String, Object>();
			con.put("content", stringWriter.toString());
			con.put("mainViewId", gridView.getViewId());
			write(writer, con, "com/gree/mobile/wf/taskinfo/template/grid/temp.ftl");
			return ;
		}
		logger.info("没有输出");
	}
	
	private static void write(Writer writer, Map<String, Object> context, String classpathFileName) throws Exception {
		InputStream in = null;
		try {
			Configuration config = new Configuration();
			config.setNumberFormat("#");
			config.setDefaultEncoding("utf-8");
			in = TemplateManager.class.getClassLoader().getResourceAsStream(classpathFileName);
			Template tpl = new Template("", new InputStreamReader(in, "utf-8"), config,"utf-8");
			tpl.process(context, writer);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}
	
	public static View evaluate(Object docObject, ViewNode viewNode, TaskInfoEvaluater evaluater){
		if(viewNode instanceof SimpleViewNode){
			return buildSimpleView(docObject, evaluater, (SimpleViewNode)viewNode);
		}
		if(viewNode instanceof GridViewNode){
			return buildGridView(docObject, evaluater, (GridViewNode) viewNode);
		}
		return null;
	}

	private static SimpleView buildSimpleView(Object docObject, TaskInfoEvaluater evaluater, SimpleViewNode viewNode) {
		SimpleView view = new SimpleView();
		view.setBosTypeName(viewNode.getTitle());
		boolean hiddenIfEmpty = viewNode.isHiddenIfEmpty();
		List<AbstractTableNode> tables = viewNode.getTables();
		for(AbstractTableNode t : tables){
			String title = t.getTitle();
			if(t instanceof GroupTableNode){
				GroupItem groupItem = new GroupItem(title);
				List<RowItemNode> items = t.getItems();
				for(RowItemNode row : items){
					String displayValue = evaluater.getDisplayValue(docObject, row.getPropName(), row, true);
                    if (StringUtils.isEmpty(displayValue)) {
                        if (hiddenIfEmpty) {
                            displayValue=null;
                        }
                        displayValue="";
                    }else{
                        if(displayValue.equals(row.getNullValue())){
                            
                        }
                    }
					if(displayValue != null){
						groupItem.addItem(new PropItem(row.getTitle(), displayValue));
					}
				}
				view.addGroupItem(groupItem);
			}else if(t instanceof ListTableNode){
				ListItem listItem = new ListItem(title);
				view.addListItem(listItem);
				ListTableNode listTableNote = (ListTableNode) t;
				Object entryObj = evaluater.getLinkPropertyValue(docObject, listTableNote.getPropName());
				if(!(entryObj instanceof AbstractObjectCollection)){
					continue;
				}
				AbstractObjectCollection entryValueCol = (AbstractObjectCollection) entryObj;
				if(entryValueCol==null || entryValueCol.size()==0){
					continue;
				}
				List<RowItemNode> items = t.getItems();
				for (int j = 0, len=entryValueCol.size(); j < len; j++) {
					IObjectValue entryValue = entryValueCol.getObject(j);
					
					GroupItem groupItem = new GroupItem(title+(j+1));
					for(RowItemNode row : items){
						String displayValue = evaluater.getDisplayValue(entryValue, row.getPropName(), row, false);
						if (StringUtils.isEmpty(displayValue)) {
	                        if (hiddenIfEmpty) {
	                            displayValue=null;
	                        }
	                        displayValue="";
	                    }
						if(displayValue != null){
							groupItem.addItem(new PropItem(row.getTitle(), displayValue));
						}
					}
					listItem.addGroupItem(groupItem);
				}					
			}
		}
		return view;
	}

	private static GridView buildGridView(Object rootObject, TaskInfoEvaluater evaluater, GridViewNode gridViewNode) {
		GridView gridView = new GridView();
		gridView.setTitle(gridViewNode.getTitle());
		if(!StringUtils.isEmpty(gridViewNode.getPropName())){
			rootObject = evaluater.getOwnPropertyValue(rootObject, gridViewNode.getPropName());
			if(rootObject==null){
				return gridView;
			}
		}
		
		List<CellNode> cellNodes = gridViewNode.getCellNodes();
		for(CellNode cellNode : cellNodes){
			if(cellNode instanceof PropCellNode){
				gridView.addCell(buildPropCell(rootObject, evaluater, (PropCellNode) cellNode));
				continue;
			}
			if(cellNode instanceof RowCellNode){
				RowCell rowCell = new RowCell();
				gridView.addCell(rowCell);
				List<PropCellNode> propCellNodes = ((RowCellNode) cellNode).getPropCellNodes();
				for(PropCellNode pcn : propCellNodes){
					rowCell.addPropCell(buildPropCell(rootObject, evaluater, pcn));
				}
				continue;
			}
			if(cellNode instanceof IteratorCellNode){
				IteratorCellNode iteratorCellNode = (IteratorCellNode) cellNode;
				IteratorCell iteratorCell = new IteratorCell();
				
				String propName = iteratorCellNode.getPropName();
				Object listObject=rootObject;
				if(!StringUtils.isEmpty(propName)){
					listObject = evaluater.getOwnPropertyValue(rootObject, propName);
					if(listObject==null){
						continue;
					}
				}
				List<PropCellNode> propCellNodes = iteratorCellNode.getPropCellNodes();
				if(listObject instanceof AbstractObjectCollection){
					AbstractObjectCollection entryValueCol = (AbstractObjectCollection) listObject;
					if(entryValueCol==null || entryValueCol.size()==0){
						continue;
					}
					for (int j = 0, len=entryValueCol.size(); j < len; j++) {
						IObjectValue entryValue = entryValueCol.getObject(j);
						List<PropCell> list = new ArrayList<PropCell>();
						iteratorCell.addPropCells(list);
						for(PropCellNode pcn : propCellNodes){
							list.add(buildPropCell(entryValue, evaluater, pcn));
						}
					}
				}
				if(listObject instanceof Iterable){
					for (Iterator it = ((Iterable) listObject).iterator();it.hasNext();) {
						Object entryValue = it.next();
						List<PropCell> list = new ArrayList<PropCell>();
						iteratorCell.addPropCells(list);
						for(PropCellNode pcn : propCellNodes){
							list.add(buildPropCell(entryValue, evaluater, pcn));
						}
					}
				}
				gridView.addCell(iteratorCell);
				continue;
			}
		}
		List<GridViewNode> subGridViews = gridViewNode.getSubGridViews();
		if(!CollectionUtils.isEmpty(subGridViews)){
			for(GridViewNode gvn : subGridViews){
				GridView subGridView = buildGridView(rootObject, evaluater, gvn);
				if(subGridView!=null){
					gridView.addGridView(subGridView);
				}
			}
		}
		return gridView;
	}

	private static PropCell buildPropCell(Object docObject, TaskInfoEvaluater evaluater, PropCellNode pcn) {
		String propName = pcn.getPropName();
		String displayValue = evaluater.getDisplayValue(docObject, propName, pcn, false);
		if (displayValue==null) {
            displayValue="";
        }
		PropCell propCell = new PropCell(pcn.getTitle(), displayValue, pcn.isTwoLine());
		String titleBgColor = pcn.getTitleBgColor();
		if(!StringUtils.isEmpty(titleBgColor)){
		    propCell.setTitleCss("background-color: "+titleBgColor);
		}
        return propCell;
	}
	
	
}
