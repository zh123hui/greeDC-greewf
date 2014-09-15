package com.gree.mobile.common.web.exception;

import com.gree.mobile.common.web.action.JsonAction;


public class JsonActionException extends Exception {

	private static final long serialVersionUID = 5134350186095579686L;
	
	public static final int COMMON_ERROR = JsonAction.DEFAULT_ERROR;
	
	private int result;
	
	public int getResult() {
		return result;
	}

	public JsonActionException(int result, String message){
		super(message);
		this.result = result;
	}
	
	public JsonActionException(int result, String message, Throwable e){
		super(message, e);
		this.result = result;
	}	
	
	public JsonActionException(String message, Throwable e){
		this(COMMON_ERROR, message, e);
	}
	public JsonActionException(Throwable e){
		this(COMMON_ERROR, "", e);
	}
	
	public JsonActionException(int result){
		this(result, "");
	}
	
	public JsonActionException(String message) {
		this(COMMON_ERROR, message);
	}
}
