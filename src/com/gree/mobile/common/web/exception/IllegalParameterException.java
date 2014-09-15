package com.gree.mobile.common.web.exception;


public class IllegalParameterException extends JsonActionException {

	private static final long serialVersionUID = 5134350186095579686L;

	private IllegalParameterException(int result, String message, Throwable e) {
		super(result, message, e);
	}

	private IllegalParameterException(int result, String message) {
		super(result, message);
	}

	private IllegalParameterException(int result) {
		super(result);
	}

	private IllegalParameterException(String message, Throwable e) {
		super(message, e);
	}

	private IllegalParameterException(String message) {
		super(message);
	}

	private IllegalParameterException(Throwable e) {
		super(e);
	}
	
}
