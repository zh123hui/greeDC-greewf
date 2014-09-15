package com.gree.mobile.common.permission;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

public class UserContextManager {

	private static final String USER_CONTEXT_KEY = UserContextManager.class.getName()+"__KEY__";
	
	public static UserContext getUserContext(HttpSession session) {
		if (session == null){
			return null;
		}
		return (UserContext) session.getAttribute(USER_CONTEXT_KEY);
	}

	public static UserContext getUserContextInAction() {
		return getUserContext(ServletActionContext.getRequest().getSession());
	}
	
	public static void setUserContext(HttpSession session, UserContext context) {
		if (session == null){
			return ;
		}
		if(context==null){
			session.removeAttribute(USER_CONTEXT_KEY);
			return;
		}
		session.setAttribute(USER_CONTEXT_KEY, context);
	}
}
