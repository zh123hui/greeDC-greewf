package com.gree.mobile.wf.action.backup;

import com.gree.mobile.common.web.exception.JsonActionException;
import com.kingdee.util.StringUtils;
/**
 * @deprecated
 * 
 */
public class ProcInfoAction extends AbstractTaskInfoAction {
	private String taskId; // 任务ID

	@Override
	protected void prevExecute() throws Exception {
		if (StringUtils.isEmpty(taskId)) {
			throw new JsonActionException("参数流程ID不能为空");
		}
	}
	
	@Override
	protected String getFormId() throws Exception {
		return getFormIdByProcinstId(taskId);
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

}
