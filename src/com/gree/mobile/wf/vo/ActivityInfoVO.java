package com.gree.mobile.wf.vo;

import java.util.ArrayList;
import java.util.List;

public class ActivityInfoVO {

	private int type;
	private List<Decision> decisions=new ArrayList<ActivityInfoVO.Decision>();
	private List<OtherOper> otherOpers=new ArrayList<ActivityInfoVO.OtherOper>();
	
	public static class Decision{
		private String text;
		private List<DecisionOption> options=new ArrayList<ActivityInfoVO.DecisionOption>();
		
		public Decision(String text) {
			super();
			this.text = text;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public List<DecisionOption> getOptions() {
			return options;
		}
		public void setOptions(List<DecisionOption> options) {
			this.options = options;
		}
	}
	
	public static class DecisionOption{
		private String optKey;
		private String optValue;
		
		public DecisionOption(String optKey, String optValue) {
			super();
			this.optKey = optKey;
			this.optValue = optValue;
		}
		public String getOptKey() {
			return optKey;
		}
		public void setOptKey(String optKey) {
			this.optKey = optKey;
		}
		public String getOptValue() {
			return optValue;
		}
		public void setOptValue(String optValue) {
			this.optValue = optValue;
		}
	}
	
	public static class OtherOper{
		private String operKey;
		private String operText;
		
		public OtherOper(String operKey, String operText) {
			super();
			this.operKey = operKey;
			this.operText = operText;
		}
		public String getOperKey() {
			return operKey;
		}
		public void setOperKey(String operKey) {
			this.operKey = operKey;
		}
		public String getOperText() {
			return operText;
		}
		public void setOperText(String operText) {
			this.operText = operText;
		}
	}
	
	public static enum OtherOperEnum{
		/** 转交 */
		DELIVER(new OtherOper("01", "转交"));
		private OtherOper item;

		OtherOperEnum(OtherOper item) {
			this.item = item;
		}
		public OtherOper get(){
			return item;
		}
		
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List<Decision> getDecisions() {
		return decisions;
	}

	public void setDecisions(List<Decision> decisions) {
		this.decisions = decisions;
	}

	public void setOtherOpers(List<OtherOper> otherOpers) {
		this.otherOpers = otherOpers;
	}

	public List<OtherOper> getOtherOpers() {
		return otherOpers;
	}
	
}
