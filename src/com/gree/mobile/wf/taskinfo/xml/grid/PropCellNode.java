package com.gree.mobile.wf.taskinfo.xml.grid;

import com.gree.mobile.wf.taskinfo.xml.FormatAware;

public class PropCellNode extends FormatAware implements CellNode {

	private String title;
	private String propName;
//	private String defaultValue;
	private String dataType;
	private boolean twoLine;//是否标题,内容各占一行
	private String titleBgColor;
	
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
	
//	public void setDefaultValue(String defaultValue) {
//		this.defaultValue = defaultValue;
//	}
//	public String getDefaultValue() {
//		return defaultValue;
//	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getDataType() {
		return dataType;
	}
	public boolean isTwoLine() {
		return twoLine;
	}
	public void setTwoLine(boolean twoLine) {
		this.twoLine = twoLine;
	}
    public String getTitleBgColor() {
        return titleBgColor;
    }
    public void setTitleBgColor(String titleBgColor) {
        this.titleBgColor = titleBgColor;
    }
	
}
