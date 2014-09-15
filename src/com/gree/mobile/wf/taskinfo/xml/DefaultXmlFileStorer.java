package com.gree.mobile.wf.taskinfo.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

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

public class DefaultXmlFileStorer implements IXmlFileStorer{

	public void storerToXml(TaskInfoNode config, String fileName) throws Exception{
		Document doc = buildDocument(config);
		File file = new File(fileName);
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("utf-8");
		FileOutputStream output=null;
		try {
			output = new FileOutputStream(file);
			XMLWriter writer = new XMLWriter(output, format);
			writer.write(doc);
		} catch (Exception e) {
			throw new Exception("保存配置内容到XML文件失败", e);
		} finally{
			IOUtils.closeQuietly(output);
		}
	}
	
	private Document buildDocument(TaskInfoNode config){
		Document doc = DocumentHelper.createDocument();
		Element taskInfoEle = DocumentHelper.createElement("taskInfo");
		doc.setRootElement(taskInfoEle);
		taskInfoEle.addElement("bosType").setText(config.getBosType());
		taskInfoEle.addElement("bosTypeName").setText(config.getBosTypeName());
		List<ViewNode> viewNodes = config.getViewNodes();
		for(ViewNode vn : viewNodes){
			if(vn instanceof SimpleViewNode){
				SimpleViewNode viewNode = (SimpleViewNode)vn;
				Element viewEle = taskInfoEle.addElement("simpleView");
				viewEle.addAttribute("title", viewNode.getTitle());
				viewEle.addAttribute("viewName", viewNode.getViewName());
				viewEle.addAttribute("hiddenIfEmpty", String.valueOf(viewNode.isHiddenIfEmpty()));
				List<AbstractTableNode> tables = viewNode.getTables();
				for(AbstractTableNode t : tables){
					Element tableEle=null;
					if(t instanceof GroupTableNode){
						tableEle = viewEle.addElement("groupTable");
					}else if(t instanceof ListTableNode){
						tableEle = viewEle.addElement("listTable");
						tableEle.addAttribute("propName", ((ListTableNode) t).getPropName());
					}
					if(tableEle==null){
						continue;
					}
					tableEle.addAttribute("title", t.getTitle());
					List<RowItemNode> items = t.getItems();
					for(RowItemNode item : items){
						Element rowItemEle = tableEle.addElement("propItem");
						rowItemEle.addAttribute("title", item.getTitle());
						rowItemEle.addAttribute("propName", item.getPropName());
						rowItemEle.addAttribute("format", item.getFormat());
						rowItemEle.addAttribute("valueToText", item.getValueToText());
					}
				}
			}else if(vn instanceof GridViewNode){
				GridViewNode viewNode = (GridViewNode)vn;
				addGridViewEle(taskInfoEle, viewNode);
			}
		}
		return doc;
	}

	private void addGridViewEle(Element parentEle, GridViewNode viewNode) {
		Element viewEle = parentEle.addElement("gridView");
		viewEle.addAttribute("title", viewNode.getTitle());
		viewEle.addAttribute("viewName", viewNode.getViewName());
		
		List<CellNode> cellNodes = viewNode.getCellNodes();
		for(CellNode cellNode : cellNodes){
			if(cellNode instanceof PropCellNode){
				PropCellNode node = (PropCellNode) cellNode;
				addPropCellEle(viewEle, node);
			}else if(cellNode instanceof RowCellNode){
				Element rowCellEle = viewEle.addElement("rowCell");
				List<PropCellNode> propCellNodes = ((RowCellNode) cellNode).getPropCellNodes();
				for(PropCellNode node : propCellNodes){
					addPropCellEle(rowCellEle, node);
				}
			}else if(cellNode instanceof IteratorCellNode){
				IteratorCellNode icNode = (IteratorCellNode) cellNode;
				Element iteratorCellEle = viewEle.addElement("iteratorCell");
				iteratorCellEle.addAttribute("propName", icNode.getPropName());
				List<PropCellNode> propCellNodes = icNode.getPropCellNodes();
				for(PropCellNode node : propCellNodes){
					addPropCellEle(iteratorCellEle, node);
				}
			}
		}
		List<GridViewNode> subGridViews = viewNode.getSubGridViews();
		if(!CollectionUtils.isEmpty(subGridViews)){
			Element subGridViewGroupEle = viewEle.addElement("subGridViewGroup");
			for(GridViewNode gvn : subGridViews){
				addGridViewEle(subGridViewGroupEle, gvn);
			}
		}
	}

	private void addPropCellEle(Element viewEle, PropCellNode node) {
		Element rowItemEle = viewEle.addElement("propItem");
		rowItemEle.addAttribute("title", node.getTitle());
		rowItemEle.addAttribute("propName", node.getPropName());
		rowItemEle.addAttribute("format", node.getFormat());
		rowItemEle.addAttribute("valueToText", node.getValueToText());
	}
}
