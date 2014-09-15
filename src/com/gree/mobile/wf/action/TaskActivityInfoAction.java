package com.gree.mobile.wf.action;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.gree.mobile.common.web.action.JsonAction;
import com.gree.mobile.common.web.exception.JsonActionException;
import com.gree.mobile.wf.util.TaskActivityInfoUtil;
import com.gree.mobile.wf.vo.ActivityInfoVO;
import com.gree.mobile.wf.vo.ActivityInfoVO.Decision;
import com.gree.mobile.wf.vo.ActivityInfoVO.DecisionOption;
import com.kingdee.bos.workflow.service.ormrpc.EnactmentServiceFactory;
import com.kingdee.bos.workflow.service.ormrpc.IEnactmentService;
import com.kingdee.util.StringUtils;

public class TaskActivityInfoAction extends JsonAction {

	private static final Logger logger = Logger.getLogger(TaskActivityInfoAction.class);
	private String taskId;

	@Override
	public Object doExecute() throws Exception {
		if (StringUtils.isEmpty(taskId)) {
			throw new JsonActionException("流程任务ID不正确");
		}
		Object activityDef = null;
		int type = 0;
		try {
			IEnactmentService wfService = EnactmentServiceFactory
					.createRemoteEnactService();
			Map resultMap = wfService.getActivityDefAndActivityInstInfo(taskId);
			activityDef = resultMap.get("ACTIVITYDEF");
			type = wfService.getActTypeByAssignment(taskId);
		} catch (Exception e) {
			throw new JsonActionException("查找流程节点定义失败", e);
		}
		if(activityDef==null){
			throw new JsonActionException("查找流程节点定义失败");
		}
		if(type!=3 && type!=4){
			logger.info(String.format("不支持类型为[%s]流程", type));
			throw new JsonActionException("流程任务ID不正确");
		}
		ActivityInfoVO vo = new ActivityInfoVO();
		vo.setType(type);
		List<Decision> decisions = TaskActivityInfoUtil.findDecisions(activityDef, type);
		if(type==3){
			for(Decision d : decisions){
				List<DecisionOption> options = d.getOptions();
				if(options.size()==0){
					options.add(new DecisionOption("-1000", "无"));
				}
			}
		}
		vo.setDecisions(decisions);
		vo.setOtherOpers(TaskActivityInfoUtil.findOtherOper(activityDef, type));
		return vo;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

}
