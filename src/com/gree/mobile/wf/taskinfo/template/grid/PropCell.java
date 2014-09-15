package com.gree.mobile.wf.taskinfo.template.grid;


public class PropCell implements CellUI{

	private String title;
	private String value;
	private boolean twoLine;
	private String titleCss;
	
	public PropCell(String title, String value) {
		super();
		this.title = title;
		this.value = value;
	}
	
	public PropCell(String title, String value, boolean twoLine) {
		super();
		this.title = title;
		this.value = value;
		this.twoLine = twoLine;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public boolean isTwoLine() {
		return twoLine;
	}
	public void setTwoLine(boolean twoLine) {
		this.twoLine = twoLine;
	}

    public String getTitleCss() {
        return titleCss;
    }

    public void setTitleCss(String titleCss) {
        this.titleCss = titleCss;
    }
	
}
