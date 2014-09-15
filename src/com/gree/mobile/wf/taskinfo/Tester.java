package com.gree.mobile.wf.taskinfo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.IOUtils;

import junit.framework.TestCase;

import com.gree.mobile.wf.taskinfo.template.grid.GridView;
import com.gree.mobile.wf.taskinfo.template.grid.GridViewWriter;
import com.gree.mobile.wf.taskinfo.template.grid.IteratorCell;
import com.gree.mobile.wf.taskinfo.template.grid.PropCell;
import com.gree.mobile.wf.taskinfo.template.grid.RowCell;
import com.gree.mobile.wf.taskinfo.template.simple.GroupItem;
import com.gree.mobile.wf.taskinfo.template.simple.ListItem;
import com.gree.mobile.wf.taskinfo.template.simple.PropItem;
import com.gree.mobile.wf.taskinfo.template.simple.SimpleView;
import com.gree.mobile.wf.taskinfo.xml.DefaultXmlFileStorer;
import com.gree.mobile.wf.taskinfo.xml.TaskInfoNode;
import com.gree.mobile.wf.taskinfo.xml.ViewNode;
import com.gree.mobile.wf.taskinfo.xml.DefaultXmlFileLoader;
import com.gree.mobile.wf.taskinfo.xml.grid.GridViewNode;
import com.gree.mobile.wf.taskinfo.xml.grid.IteratorCellNode;
import com.gree.mobile.wf.taskinfo.xml.grid.PropCellNode;
import com.gree.mobile.wf.taskinfo.xml.grid.RowCellNode;
import com.gree.mobile.wf.taskinfo.xml.simple.GroupTableNode;
import com.gree.mobile.wf.taskinfo.xml.simple.ListTableNode;
import com.gree.mobile.wf.taskinfo.xml.simple.RowItemNode;
import com.gree.mobile.wf.taskinfo.xml.simple.SimpleViewNode;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class Tester extends TestCase{

	private void build(Writer writer) throws Exception {
		InputStream in = null;
		try {
			Configuration config = new Configuration();
			config.setNumberFormat("#");
			config.setDefaultEncoding("utf-8");
			Map<String, Object> con = new HashMap<String, Object>();
			
			StringWriter stringWriter = new StringWriter();
			GridView gridView = getGridView(true);
			new GridViewWriter(stringWriter).writeGridView(gridView);
			con.put("content", stringWriter.toString());
			con.put("mainViewId", gridView.getViewId());
			in = TemplateManager.class.getClassLoader().getResourceAsStream("com/gree/mobile/wf/taskinfo/template/grid/billDetailTemplate.ftl");
			Template tpl = new Template("", new InputStreamReader(in, "utf-8"), config,"utf-8");
			tpl.process(con, writer);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}
	public void testOutput() throws Exception{
		FileWriter writer = new FileWriter(new File("c://testTemp1.html"));
		build(writer);
	}
	private static AtomicInteger counter = new AtomicInteger();
	public static GridView getGridView(boolean top){
		GridView gridView = new GridView();
		gridView.setViewId("viewId"+counter.getAndDecrement());
		gridView.setTitle("请假单");
		gridView.addCell(new PropCell("标题一", "值一"));
		
		RowCell rowCell = new RowCell();
		rowCell.addPropCell(new PropCell("标题一", "值一"));
		rowCell.addPropCell(new PropCell("标题二", "值二"));
		gridView.addCell(rowCell);
		
		gridView.addCell(new PropCell("标题二", "值二"));
		
		IteratorCell iteratorCell = new IteratorCell();
		ArrayList<PropCell> propCells = new ArrayList<PropCell>();
		propCells.add(new PropCell("标题一", "值一"));
		propCells.add(new PropCell("标题二", "值二"));
		propCells.add(new PropCell("标题三", "值三"));
		iteratorCell.addPropCells(propCells);
		
		propCells = new ArrayList<PropCell>();
		propCells.add(new PropCell("标题一", "值一"));
		propCells.add(new PropCell("标题二", "值二"));
		propCells.add(new PropCell("标题三", "值三"));
		iteratorCell.addPropCells(propCells);
		gridView.addCell(iteratorCell);
		
		if(top){
			GridView gridView2 = getGridView(false);
			gridView2.setTitle("明细一");
			gridView.addGridView(gridView2);
			gridView2 = getGridView(false);
			gridView2.setTitle("明细二");
			gridView.addGridView(gridView2);
		}
		return gridView;
	}
	public void testStorerAndLoader() throws Exception{
		TaskInfoNode taskInfoNode = new TaskInfoNode();
		taskInfoNode.setBosType("ABCDEF");
		taskInfoNode.setBosTypeName("请求申请单");
		taskInfoNode.setDefaultView("default");
		taskInfoNode.addViewNode(buildSimpleViewNode());
		taskInfoNode.addViewNode(buildGridViewNode(true));
		new DefaultXmlFileStorer().storerToXml(taskInfoNode, "c://test.xml");
		
		DefaultXmlFileLoader loader = new DefaultXmlFileLoader();
		TaskInfoNode load = loader.load(new File("c://test.xml"));
		
		new DefaultXmlFileStorer().storerToXml(load, "c://test2.xml");
	}

	private GridViewNode buildGridViewNode(boolean isTop) {
		GridViewNode gridViewNode = new GridViewNode();
		gridViewNode.setTitle("随机");
		gridViewNode.setViewName("随机");
		PropCellNode propCellNode = new PropCellNode();
		propCellNode.setTitle("标题一");
		propCellNode.setPropName("prop1");
		gridViewNode.addCellNode(propCellNode);

		propCellNode = new PropCellNode();
		propCellNode.setTitle("标题二");
		propCellNode.setPropName("prop2");
		gridViewNode.addCellNode(propCellNode);

		RowCellNode rowCellNode = new RowCellNode();
		propCellNode = new PropCellNode();
		propCellNode.setTitle("标题一");
		propCellNode.setPropName("prop1");
		rowCellNode.addPropCellNode(propCellNode);
		propCellNode = new PropCellNode();
		propCellNode.setTitle("标题二");
		propCellNode.setPropName("prop2");
		rowCellNode.addPropCellNode(propCellNode);
		gridViewNode.addCellNode(rowCellNode);
		
		propCellNode = new PropCellNode();
		propCellNode.setTitle("标题三");
		propCellNode.setPropName("prop3");
		gridViewNode.addCellNode(propCellNode);
		
		IteratorCellNode iteratorCellNode = new IteratorCellNode();
		propCellNode = new PropCellNode();
		propCellNode.setTitle("标题一");
		propCellNode.setPropName("prop1");
		iteratorCellNode.addPropCellNode(propCellNode);
		propCellNode = new PropCellNode();
		propCellNode.setTitle("标题二");
		propCellNode.setPropName("prop2");
		iteratorCellNode.addPropCellNode(propCellNode);
		gridViewNode.addCellNode(iteratorCellNode);
		
		if(isTop){
			gridViewNode.initSubGridViews();
			gridViewNode.addSubGridView(buildGridViewNode(false));
			gridViewNode.addSubGridView(buildGridViewNode(false));
		}
		
		return gridViewNode;
	}
	private SimpleViewNode buildSimpleViewNode() {
		SimpleViewNode simpleViewNode = new SimpleViewNode();
		simpleViewNode.setTitle("随机");
		simpleViewNode.setViewName("随机");
		GroupTableNode table = new GroupTableNode();
		RowItemNode item = new RowItemNode();
		item.setTitle("标题一");
		item.setPropName("prop1");
		table.addItem(item);
		item = new RowItemNode();
		item.setTitle("标题二");
		item.setPropName("prop2");
		table.addItem(item);
		item = new RowItemNode();
		item.setTitle("标题三");
		item.setPropName("prop3");
		table.addItem(item);
		simpleViewNode.addTable(table);
		
		ListTableNode listTableNode = new ListTableNode();
		listTableNode.setPropName("prop1");
		item = new RowItemNode();
		item.setTitle("标题一");
		item.setPropName("prop1");
		listTableNode.addItem(item);
		item = new RowItemNode();
		item.setTitle("标题二");
		item.setPropName("prop2");
		listTableNode.addItem(item);
		item = new RowItemNode();
		item.setTitle("标题三");
		item.setPropName("prop3");
		listTableNode.addItem(item);
		simpleViewNode.addTable(listTableNode);
		return simpleViewNode;
	}
	
	public void testTemp1() throws Exception {
		SimpleView templateViewData = new SimpleView();
		templateViewData.setBosTypeName("测试");
		GroupItem groupItem = new GroupItem("组一");
		groupItem.addItem(new PropItem("标题一", "值一"));
		groupItem.addItem(new PropItem("标题二", "值二"));
		groupItem.addItem(new PropItem("标题三", "值三"));
		templateViewData.addGroupItem(groupItem);
		
		ListItem listItem = new ListItem("分录");
		templateViewData.addListItem(listItem);
		GroupItem item = new GroupItem("分录组一");
		item.addItem(new PropItem("标题一", "值一"));
		item.addItem(new PropItem("标题二", "值二"));
		item.addItem(new PropItem("标题三", "值三"));
		listItem.addGroupItem(item);
		
		item = new GroupItem("分录组一");
		item.addItem(new PropItem("标题一", "值一"));
		item.addItem(new PropItem("标题二", "值二"));
		item.addItem(new PropItem("标题三", "值三"));
		listItem.addGroupItem(item);
		
		FileWriter fileWriter = new FileWriter(new File("c://testTemp1.html"));
		TemplateManager.write(fileWriter, templateViewData);
	}
	public void test() throws Exception{
		DefaultXmlFileLoader loader = new DefaultXmlFileLoader();
		TaskInfoNode load = loader.load(new File("G:/works/greemobile-20130917/webroot/config/test_docInfo_config.xml"));
		
		assertEquals("4A44F49F", load.getBosType());
		assertEquals("费用报销单", load.getBosTypeName());
		assertEquals("fuKuan", load.getDefaultView());
		
		List<ViewNode> viewNodes = load.getViewNodes();
		assertEquals(4, viewNodes.size());
		
		SimpleViewNode sv = (SimpleViewNode)viewNodes.get(0);
		assertEquals(2, sv.getTables().size());
		
		GridViewNode gv = (GridViewNode)viewNodes.get(1);
		assertEquals(5, gv.getCellNodes().size());
		
		gv = (GridViewNode)viewNodes.get(2);
		assertEquals(4, gv.getCellNodes().size());
		assertEquals(3, gv.getSubGridViews().size());
		
		gv = (GridViewNode)viewNodes.get(3);
		assertEquals(3, gv.getCellNodes().size());
		assertEquals(1, gv.getSubGridViews().size());
		
	}

}
