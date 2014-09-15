package com.gree.mobile.wf.action;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.gree.mobile.common.permission.UserContext;
import com.gree.mobile.common.permission.UserContextManager;
import com.gree.mobile.common.web.action.JsonAction;
import com.gree.mobile.common.web.exception.JsonActionException;
import com.gree.mobile.wf.push.PushDAO;

public class BindPushAction extends JsonAction{

	private static final Logger logger = Logger.getLogger(BindPushAction.class);
	
	private String app_id;			//应用ID
	private long channel_id=-1;		//通道标识
	private String user_id;			//用户标识

	
	@Override
	public Object doExecute() throws Exception {
		if(channel_id==-1){
			throw new JsonActionException("参数不完整,通道标识不能为空");
		}
		if(StringUtils.isEmpty(user_id)){
			throw new JsonActionException("参数不完整,用户标识不能为空");
		}
		UserContext uc = UserContextManager.getUserContext(getSession());
		String curPushToken = uc.getPushToken();
		String pushToken = new StringBuffer().append(channel_id).append("__").append(user_id).toString();
		if(curPushToken!=null){
			if(curPushToken.equals(pushToken) ){
				return null;
			}
			throw new JsonActionException("此登录用户已绑定推送令牌");
		}
		PushDAO.saveOrUpdate(uc.getBosContext(), pushToken, uc.getUserId());
		uc.setPushToken(pushToken);
		return null;
	}

	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}

	public void setChannel_id(long channel_id) {
		this.channel_id = channel_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

}
