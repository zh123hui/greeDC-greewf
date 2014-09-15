package com.gree.mobile.common.web.action;



public abstract class JsonActionSupport extends AbstractAction {

	public static final int DEFAULT_ERROR = -1;
	
	protected static final String JSON = "json";
	
	private final CommonResult commonResult = new CommonResult(1, "", null, -1);

	public CommonResult getCommonResult() {
		return commonResult;
	}

	protected void finish(int result, String message, int totalCount, Object data){
		commonResult.setMessage(message);
		commonResult.setData(data);
		commonResult.setResult(result);
		commonResult.setTotalCount(totalCount);
	}

	protected void error(String message, int result) {
		commonResult.setMessage(message);
		commonResult.setResult(result);
	}

	protected void error(String message) {
		this.error(message, DEFAULT_ERROR);
	}

	protected void setData(Object data){
		commonResult.setData(data);
	}
	
	protected void success() {
		success(commonResult.getData());
	}
	
	protected void success(Object data) {
		success(data, commonResult.getTotalCount());
	}
	
	protected void success(Object data, int totalCount) {
		finish(1, commonResult.getMessage(), totalCount, data);
	}

	public void setTotalCount(int totalCount) {
		commonResult.setTotalCount(totalCount);
	}

	@Override
	public String execute() {
		return JSON;
	}
	
}
