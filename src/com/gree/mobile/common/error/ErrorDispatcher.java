package com.gree.mobile.common.error;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ErrorDispatcher {

	private static final String DRROR_KEY = "__ErrorAction_KEY__";
	
	public static void goInFilter(ServletRequest req, ServletResponse resp, ErrorResult vo) throws ServletException, IOException{
		setResult(req, vo);
		RequestDispatcher requestDispatcher = req.getRequestDispatcher("/error.action");
        requestDispatcher.forward(req, resp);
	}
	public static String goInInterceptor(ServletRequest req, ServletResponse resp, ErrorResult vo) throws ServletException, IOException{
		setResult(req, vo);
		return "error";
	}
	
	private static final void setResult(ServletRequest req, ErrorResult vo){
		req.setAttribute(DRROR_KEY, vo);
	}
	
	public static final ErrorResult getResult(ServletRequest req){
		Object object = req.getAttribute(DRROR_KEY);
		if(object instanceof ErrorResult){
			return (ErrorResult)  object;
		}
		return null;
	}
}
