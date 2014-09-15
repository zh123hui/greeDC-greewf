package com.gree.mobile.common.error;

public class ErrorResult {

	private int result;
	private String message;
	
	public ErrorResult(int result, String message) {
		super();
		this.result = result;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}
	
}
