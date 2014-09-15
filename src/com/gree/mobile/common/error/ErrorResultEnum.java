package com.gree.mobile.common.error;

public enum ErrorResultEnum {
	INVALID_JSON(new ErrorResult(5,"请求JSON格式错误")),
	SESSION_EXPIRED(new ErrorResult(10,"会话失效或者未登录")),
	INVALID_REQUEST(new ErrorResult(11,"无效请求")),
	UNKNOWN_ERROR(new ErrorResult(99,"未知错误"))
	;
	private ErrorResult result;

	ErrorResultEnum(ErrorResult result) {
		this.result = result;
	}

	public ErrorResult getResult() {
		return result;
	}
}
