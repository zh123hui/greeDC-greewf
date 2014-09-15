package com.gree.mobile.common.web.action;

/**
 * 通用返回对象
 */
public class CommonResult {

	private String message;
	private Object data;
	private int result;
	private int totalCount;

	public CommonResult() {
		super();
	}
	public CommonResult(int result, String message, Object data, int totalCount) {
		super();
		this.message = message;
		this.data = data;
		this.result = result;
		this.totalCount = totalCount;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
}
