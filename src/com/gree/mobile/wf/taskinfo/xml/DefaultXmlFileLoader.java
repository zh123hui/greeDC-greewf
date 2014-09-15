package com.gree.mobile.wf.taskinfo.xml;

import java.io.File;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.xml.sax.Attributes;

import com.gree.mobile.wf.taskinfo.xml.grid.GridViewNode;
import com.gree.mobile.wf.taskinfo.xml.grid.IteratorCellNode;
import com.gree.mobile.wf.taskinfo.xml.grid.PropCellNode;
import com.gree.mobile.wf.taskinfo.xml.grid.RowCellNode;
import com.gree.mobile.wf.taskinfo.xml.simple.GroupTableNode;
import com.gree.mobile.wf.taskinfo.xml.simple.ListTableNode;
import com.gree.mobile.wf.taskinfo.xml.simple.RowItemNode;
import com.gree.mobile.wf.taskinfo.xml.simple.SimpleViewNode;

public class DefaultXmlFileLoader implements IXmlFileLoader{
	
	private Digester digester;
	
	public DefaultXmlFileLoader() {
		this.digester = createDigester();
	}
	
	public DefaultXmlFileLoader(Digester digester) {
		this.digester = digester;
	}

	public TaskInfoNode load(File file) throws Exception{
		if(digester==null){
			digester = createDigester();			
		}
		digester.clear();
		return (TaskInfoNode) digester.parse(file);
	}
	
	private Digester createDigester() {
		Digester digester = new Digester();
//		digester.setValidating(true);
		digester.setClassLoader(DefaultXmlFileLoader.class.getClassLoader());
		digester.addObjectCreate("taskInfo", TaskInfoNode.class);
		digester.addBeanPropertySetter("taskInfo/bosType");
		digester.addBeanPropertySetter("taskInfo/bosTypeName");
		digester.addBeanPropertySetter("taskInfo/defaultView");
		String prefix="";
		//simpleView
		prefix="taskInfo/simpleView";
		addOperate(digester, prefix, SimpleViewNode.class, "addViewNode");
		addOperate(digester, prefix+"/groupTable", GroupTableNode.class, "addTable");
		addOperate(digester, prefix+"/groupTable/propItem", RowItemNode.class, "addItem");
		addOperate(digester, prefix+"/listTable", ListTableNode.class, "addTable");
		addOperate(digester, prefix+"/listTable/propItem", RowItemNode.class, "addItem");
		
		// gridView
		prefix = "taskInfo/gridView";
		addOperate(digester, prefix, GridViewNode.class, "addViewNode");
		addOperate(digester, prefix+"/propItem", PropCellNode.class, "addCellNode");
		addOperate(digester, prefix+"/rowCell", RowCellNode.class, "addCellNode");
		addOperate(digester, prefix+"/rowCell/propItem", PropCellNode.class, "addPropCellNode");
		addOperate(digester, prefix+"/iteratorCell", IteratorCellNode.class, "addCellNode");
		addOperate(digester, prefix+"/iteratorCell/propItem", PropCellNode.class, "addPropCellNode");
//		digester.addCallMethod(prefix+"/subGridViewGroup", "initSubGridViews");
		digester.addRule(prefix+"/subGridViewGroup", new Rule() {
			@Override
			public void begin(Attributes attributes) throws Exception {
				GridViewNode gv = (GridViewNode)getDigester().peek();
				gv.initSubGridViews();
			}
		});
		
		prefix = prefix+"/subGridViewGroup/gridView";
		addOperate(digester, prefix, GridViewNode.class, "addSubGridView");
		addOperate(digester, prefix+"/propItem", PropCellNode.class, "addCellNode");
		addOperate(digester, prefix+"/rowCell", RowCellNode.class, "addCellNode");
		addOperate(digester, prefix+"/rowCell/propItem", PropCellNode.class, "addPropCellNode");
		addOperate(digester, prefix+"/iteratorCell", IteratorCellNode.class, "addCellNode");
		addOperate(digester, prefix+"/iteratorCell/propItem", PropCellNode.class, "addPropCellNode");
		
		return digester;
	}
	
	private void addOperate(Digester digester, String pattern, Class<?> clazz, String addSetNextMethodName){
		digester.addObjectCreate(pattern, clazz);
		digester.addSetProperties(pattern);
		digester.addSetNext(pattern, addSetNextMethodName);
	}

}
