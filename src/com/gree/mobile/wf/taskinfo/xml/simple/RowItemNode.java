package com.gree.mobile.wf.taskinfo.xml.simple;

import com.gree.mobile.wf.taskinfo.xml.FormatAware;


public class RowItemNode extends FormatAware{

	private String title;
	private String propName;
	private String defaultValue;
	private String dataType;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPropName() {
		return propName;
	}
	public void setPropName(String propName) {
		this.propName = propName;
	}
	
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getDataType() {
		return dataType;
	}
	
}
