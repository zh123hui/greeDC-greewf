package com.gree.mobile.wf.taskinfo.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


public class TaskInfoNode {

	private String bosType;
	private String bosTypeName;
	private List<ViewNode> viewNodes=new ArrayList<ViewNode>();
	private String defaultView;
	private Map<String, ViewNode> mapViewNode = new HashMap<String, ViewNode>();
	
	public void addViewNode(ViewNode node){
		viewNodes.add(node);
		mapViewNode.put(node.getViewName(), node);
	}
	public List<ViewNode> getViewNodes() {
		return viewNodes;
	}
	public ViewNode getViewNode(String viewName) {
		ViewNode viewNode = null;
		if(!StringUtils.isEmpty(viewName)){
			viewNode = mapViewNode.get(viewName);
		}
		if(viewNode==null){
			viewNode = mapViewNode.get(defaultView);
		}
		if(viewNode==null){
			viewNode = viewNodes.get(0);
		}
		return viewNode;
	}
	public String getBosType() {
		return bosType;
	}
	public void setBosType(String bosType) {
		this.bosType = bosType;
	}
	public String getBosTypeName() {
		return bosTypeName;
	}
	public void setBosTypeName(String bosTypeName) {
		this.bosTypeName = bosTypeName;
	}
	public String getDefaultView() {
		return defaultView;
	}
	public void setDefaultView(String defaultView) {
		this.defaultView = defaultView;
	}
	
}
