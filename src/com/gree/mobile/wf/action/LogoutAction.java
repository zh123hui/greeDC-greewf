package com.gree.mobile.wf.action;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.gree.mobile.common.permission.UserContext;
import com.gree.mobile.common.permission.UserContextManager;
import com.gree.mobile.common.web.action.JsonAction;
import com.gree.mobile.wf.push.PushDAO;
import com.kingdee.bos.Context;
import com.kingdee.bos.framework.session.LoginModuleFactory;

public class LogoutAction extends JsonAction{

	private static final Logger logger = Logger.getLogger(LogoutAction.class);
	
	@Override
	public Object doExecute() throws Exception {
		
		UserContext uc = UserContextManager.getUserContext(getSession());
		logoutEas(uc);
		HttpSession session = getSession();
		if (session != null) {
			session.invalidate();
		}
		return null;
	}

	private void logoutEas(UserContext uc) {
		if(uc == null){
			return;
		}
		Context bosContext = uc.getBosContext();
		if(bosContext==null){
			return;
		}
		String pushToken = uc.getPushToken();
		if(!StringUtils.isEmpty(pushToken)){
			String userId = uc.getUserId();
			try{
				PushDAO.delete(bosContext, pushToken, userId);
			}catch (Exception e) {
				logger.error(String.format("删除用户[%s]推送令牌[%s]失败", uc.getUserName(), pushToken), e);
			}
		}
		
		String contextID=null;
		if(bosContext!= null){
			contextID = bosContext.getContextID();
		}
		if(contextID!=null){
			try{
				LoginModuleFactory.getRemoteInstance().logout(contextID);
			}catch (Exception e) {
				logger.error("注销EAS失败", e);
			}
		}
	}

}
