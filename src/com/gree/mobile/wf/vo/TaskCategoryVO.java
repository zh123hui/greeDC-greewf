package com.gree.mobile.wf.vo;

import java.util.ArrayList;
import java.util.List;

public class TaskCategoryVO {

	private String category;
	private List<TaskCategoryVO.TaskItem> list=new ArrayList<TaskCategoryVO.TaskItem>();
	
	public static class TaskItem{
		private String taskId;
		private String msgId;		//消息id
		private boolean hasRead;	//消息状态, false代表未读，true代表已读
		private String docId;		//单据ID
		private String title;		//标题
		private String submittor;	//流程提交者
		private String docTypeName;	//单据类型名称
		private String receiveTime;	//接收时间,格式”MM-dd HH:mm”
		private int seq;			//序列号,用于排序,越小越前
		public String getTaskId() {
			return taskId;
		}
		public void setTaskId(String taskId) {
			this.taskId = taskId;
		}
		public String getMsgId() {
			return msgId;
		}
		public void setMsgId(String msgId) {
			this.msgId = msgId;
		}
		public boolean isHasRead() {
			return hasRead;
		}
		public void setHasRead(boolean hasRead) {
			this.hasRead = hasRead;
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
		public String getReceiveTime() {
			return receiveTime;
		}
		public void setReceiveTime(String receiveTime) {
			this.receiveTime = receiveTime;
		}
		public int getSeq() {
			return seq;
		}
		public void setSeq(int seq) {
			this.seq = seq;
		}
		
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<TaskItem> getList() {
		return list;
	}

	public void setList(List<TaskItem> list) {
		this.list = list;
	}
	
}
