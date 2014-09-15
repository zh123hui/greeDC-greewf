package com.gree.mobile.wf.interceptor;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.gree.mobile.common.GreeMonitoredUserUtils;
import com.gree.mobile.common.permission.UserContext;
import com.gree.mobile.common.permission.UserContextManager;
import com.gree.mobile.wf.action.LoginAction;
import com.gree.mobile.wf.action.LogoutAction;
import com.kingdee.eas.base.usermonitor.UMRegistryInfo;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class ActionPostBeforeInterceptor extends AbstractInterceptor {
	private static final Logger logger = Logger.getLogger(ActionPostBeforeInterceptor.class);
	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		Object action = invocation.getAction();
		if(action instanceof LoginAction || action instanceof LogoutAction){
			return invocation.invoke();
		}
		HttpSession session = ServletActionContext.getRequest().getSession();
		UserContext uc = UserContextManager.getUserContext(session);
		if(uc==null){
			return "error";
		}
		// 两方会话都存在，则更新用户的最后操作时间，以免被监控面板中的，定时会话清理线程清理
		try {
			UMRegistryInfo monitoredUser = GreeMonitoredUserUtils.getUserInMonitorPanel(uc.getBosContext());
			monitoredUser.setLastOperateTime(new Date());
			GreeMonitoredUserUtils.updateUserInMonitorPanel(monitoredUser);
		} catch (Throwable e) {
			logger.error("无法更新用户监控面板时间", e);
		}
		
		return invocation.invoke();
	}
	
}
