package com.gree.mobile.wf.vo;

public class DealRecordVO {
	private String actdefName;		//节点名称
	private String result;			//审批结果
	private String optValue;		//审批的决策项内容
	private String opinion;			//审批意见
	private String approver;		//审批人
	private String approveTime;		//审批时间
	public String getActdefName() {
		return actdefName;
	}
	public void setActdefName(String actdefName) {
		this.actdefName = actdefName;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getOptValue() {
		return optValue;
	}
	public void setOptValue(String optValue) {
		this.optValue = optValue;
	}
	public String getOpinion() {
		return opinion;
	}
	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}
	public String getApprover() {
		return approver;
	}
	public void setApprover(String approver) {
		this.approver = approver;
	}
	public String getApproveTime() {
		return approveTime;
	}
	public void setApproveTime(String approveTime) {
		this.approveTime = approveTime;
	}

}
