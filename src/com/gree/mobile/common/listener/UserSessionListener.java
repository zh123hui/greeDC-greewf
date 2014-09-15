package com.gree.mobile.common.listener;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

import com.gree.mobile.common.GreeMonitoredUserUtils;
import com.gree.mobile.common.permission.UserContext;
import com.gree.mobile.common.permission.UserContextManager;
import com.gree.mobile.wf.config.LoggerConfig;
import com.kingdee.bos.Context;
import com.kingdee.bos.framework.session.SessionManagerUtil;
import com.kingdee.eas.base.usermonitor.UMRegistryInfo;

public class UserSessionListener implements HttpSessionListener {
	private static final Logger logger = Logger.getLogger(UserSessionListener.class);

	public void sessionCreated(HttpSessionEvent event) {
		if(LoggerConfig.isDebug(logger)){
			HttpSession session = event.getSession();
			if(session!=null){
				logger.debug("创建 session : sessionID="+session.getId());
			}
		}
	}

	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		if(session==null){
			return ;
		}
		UserContext uc = UserContextManager.getUserContext(session);
		if(uc==null){
			return ;
		}
		
		if(LoggerConfig.isDebug(logger)){
			long onlineTime = System.currentTimeMillis()-session.getCreationTime()/1000;
			String msg = "销毁用户[%s] session : sessionID=%s, 在线时间%s";
			logger.debug(String.format(msg, uc.getUserName(), session.getId(), onlineTime));
		}

		try{
			logger.info("销毁session");
			Context ctx = uc.getBosContext();
			if(ctx == null){
				return ;
			}
			UMRegistryInfo info = GreeMonitoredUserUtils.getUserInMonitorPanel(ctx);
			if (info != null) {
				SessionManagerUtil.removeSession(info.getId());
			}
		}catch (Exception e) {
			logger.error("can not delete user session in monitor panel!", e);
		}

	}

}
