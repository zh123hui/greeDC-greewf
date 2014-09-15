package com.gree.mobile.wf.vo;


public class DealtTaskItem {

	private String taskId;
	private String docId;		//单据ID
	private String title;		//标题
	private String submittor;	//流程提交者
	private String docTypeName;	//单据类型名称
	private String dealTime;	//接收时间,格式”MM-dd HH:mm”
	private int state;			//
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getDocId() {
		return docId;
	}
	public void setDocId(String docId) {
		this.docId = docId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubmittor() {
		return submittor;
	}
	public void setSubmittor(String submittor) {
		this.submittor = submittor;
	}
	public String getDocTypeName() {
		return docTypeName;
	}
	public void setDocTypeName(String docTypeName) {
		this.docTypeName = docTypeName;
	}
	public String getDealTime() {
		return dealTime;
	}
	public void setDealTime(String dealTime) {
		this.dealTime = dealTime;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	
}
