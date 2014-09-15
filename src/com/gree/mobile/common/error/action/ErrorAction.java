package com.gree.mobile.common.error.action;

import com.gree.mobile.common.error.ErrorDispatcher;
import com.gree.mobile.common.error.ErrorResult;
import com.gree.mobile.common.error.ErrorResultEnum;
import com.gree.mobile.common.web.action.JsonActionSupport;


public class ErrorAction extends JsonActionSupport {

	@Override
	public String execute() {
		ErrorResult error = ErrorDispatcher.getResult(getRequest());
		if(error==null){
			error = ErrorResultEnum.UNKNOWN_ERROR.getResult();
		}
		this.error(error.getMessage(), error.getResult());
		return JSON;
	}
}
