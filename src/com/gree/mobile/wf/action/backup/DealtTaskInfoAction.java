package com.gree.mobile.wf.action.backup;

import com.gree.mobile.common.web.exception.JsonActionException;
import com.kingdee.util.StringUtils;
/**
 * @deprecated
 * 
 */
public class DealtTaskInfoAction extends AbstractTaskInfoAction {

	private String taskId;			//任务ID
	
	@Override
	protected String getFormId() throws Exception {
		return getFormIdByAssignId(taskId);
	}

	@Override
	protected void prevExecute() throws Exception {
		if(StringUtils.isEmpty(taskId)){
			throw new JsonActionException("参数任务ID不能为空");
		}
	}
	
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

}
