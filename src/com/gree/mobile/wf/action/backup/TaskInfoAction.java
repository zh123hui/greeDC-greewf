package com.gree.mobile.wf.action.backup;

import org.apache.log4j.Logger;

import com.gree.mobile.common.permission.UserContext;
import com.gree.mobile.common.permission.UserContextManager;
import com.gree.mobile.common.web.exception.JsonActionException;
import com.gree.mobile.wf.jdbc.EasDbUtil;
import com.kingdee.bos.Context;
import com.kingdee.util.StringUtils;

/**
 * @deprecated
 * 
 */
public class TaskInfoAction extends AbstractTaskInfoAction {
	private static final Logger logger = Logger.getLogger(TaskInfoAction.class);
	private String taskId; // 任务ID
	private String msgId;			//消息ID
	private boolean hasRead;		//消息是否已读
	
	@Override
	protected String getFormId() throws Exception {
		return getFormIdByAssignId(taskId);
	}
	
	@Override
	protected void prevExecute() throws Exception {
		if(StringUtils.isEmpty(taskId)){
			throw new JsonActionException("参数任务ID不能为空");
		}
		UserContext uc = UserContextManager.getUserContext(getSession());
		Context ctx = uc.getBosContext();
		if(!StringUtils.isEmpty(msgId) && !hasRead){
	        try {
	        	EasDbUtil.execute(ctx, "update T_BAS_AssignRead set FStatus=? where FID=?", new Object[]{"10", msgId});
			} catch (Exception e) {
				logger.error(String.format("消息[%s]设置状态失败.", msgId), e);
			} 
		}
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public void setHasRead(boolean hasRead) {
		this.hasRead = hasRead;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

}
