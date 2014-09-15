package com.gree.mobile.wf.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.gree.mobile.common.permission.UserContext;
import com.gree.mobile.common.permission.UserContextManager;
import com.gree.mobile.common.web.action.JsonAction;
import com.gree.mobile.common.web.exception.JsonActionException;
import com.gree.mobile.wf.WfNextActsPersonsUtils;
import com.gree.mobile.wf.util.TaskActivityInfoUtil;
import com.gree.mobile.wf.vo.ActivityInfoVO.Decision;
import com.gree.mobile.wf.vo.ActivityInfoVO.DecisionOption;
import com.kingdee.bos.Context;
import com.kingdee.bos.util.BOSUuid;
import com.kingdee.bos.workflow.ActivityInstInfo;
import com.kingdee.bos.workflow.AssignmentInfo;
import com.kingdee.bos.workflow.SubmitAssignResult;
import com.kingdee.bos.workflow.biz.agent.IMultiBillWorkAgent;
import com.kingdee.bos.workflow.exception.AlreadyInProcessQueueException;
import com.kingdee.bos.workflow.metas.WfAssignmentState;
import com.kingdee.bos.workflow.service.ormrpc.EnactmentServiceFactory;
import com.kingdee.bos.workflow.service.ormrpc.IEnactmentService;
import com.kingdee.eas.base.multiapprove.ApproveResult;
import com.kingdee.eas.base.multiapprove.ManualDecisionInfo;
import com.kingdee.eas.base.multiapprove.MultiApprove;
import com.kingdee.eas.base.multiapprove.MultiApproveInfo;
import com.kingdee.eas.base.multiapprove.MultiApproveStatusEnum;
import com.kingdee.eas.base.permission.UserInfo;
import com.kingdee.eas.common.SysContextConstant;

public class DealDocAction extends JsonAction {
	private static Logger logger = Logger.getLogger(DealDocAction.class);
	private String taskId; // (必填)任务ID
	private boolean agree; // 同意/不同意，传入true|false
	private String opinion; // 审批意见
	private String optKey; // 决策项的选项
	private String optValue; // 决策项的值

	@Override
	public Object doExecute() throws Exception {
		if (StringUtils.isEmpty(taskId)) {
			throw new JsonActionException("流程ID为空");
		}
		UserContext uc = UserContextManager.getUserContext(getSession());
		Context ctx = uc.getBosContext();
		try {
			IEnactmentService wfService = EnactmentServiceFactory
					.createRemoteEnactService();
			AssignmentInfo ai = wfService.getAssignmentById(taskId);
			String checkStr = checkAssignState(ctx, wfService, ai);
			if (checkStr != null) {
				throw new JsonActionException(checkStr);
			}
			int type = wfService.getActTypeByAssignment(taskId);
			switch (type) {
			case 3:
				approve(ctx, wfService, ai);
				break;
			case 4:
				decision(ctx, wfService, ai);
				break;
			}
		} catch (AlreadyInProcessQueueException e) {
			throw new JsonActionException("该待办事项已经审批，请刷新待办事项，勿重复审批", e);
		}
		return null;
	}

	private String checkAssignState(Context ctx, IEnactmentService wfService,
			AssignmentInfo info) {
		if (info == null || info.getBizObjectIds() == null) {
			return "获取任务信息失败";
		}
		try {
			String actInstId = info.getActInstId();
			ActivityInstInfo actInstInfo = wfService
					.getActivityInstByActInstId(actInstId);
			if ((actInstInfo != null)
					&& (((actInstInfo.getState()
							.equalsIgnoreCase("open.not_running.suspended")) || (actInstInfo
							.getState()
							.equalsIgnoreCase("open.not_running.blocked"))))) {
				return "流程不处于运行状态";
			}
		} catch (Exception ex) {
			logger.error("获取活动信息失败", ex);
			return "获取活动信息失败";
		}
		String rtn = null;
		WfAssignmentState taskState = info.getState();
		if (taskState.equals(WfAssignmentState.CANCELED)) {
			rtn = "任务处于取消状态";
		} else if (taskState.equals(WfAssignmentState.COMPLETED)) {
			rtn = "任务处于完成状态";
		} else if (taskState.equals(WfAssignmentState.REJECTED)) {
			rtn = "任务处于拒绝状态";
		}
		return rtn;
	}

	private static enum KeyValueCheckEnum{
		FALSE(0),
		TRUE(1),
		EMPTYOPT(3)
		;
		private final int value;
		KeyValueCheckEnum(int value) {
			this.value = value;
		}
		public int getValue() {
			return value;
		}
		public static KeyValueCheckEnum format(boolean flag){
			return flag ? KeyValueCheckEnum.TRUE : KeyValueCheckEnum.FALSE;
		}
		
	}
	private KeyValueCheckEnum checkKeyAndValue(IEnactmentService wfService, int type)
			throws JsonActionException {
		Object activityDef = null;
		try {
			Map resultMap = wfService.getActivityDefAndActivityInstInfo(taskId);
			activityDef = resultMap.get("ACTIVITYDEF");
		} catch (Exception e) {
			throw new JsonActionException("查找流程节点定义失败", e);
		}
		if(activityDef==null){
			throw new JsonActionException("查找流程节点定义失败");
		}
		List<Decision> decisions = TaskActivityInfoUtil.findDecisions(activityDef, type);
		List<DecisionOption> options=null;
		switch (type) {
		case 3:
			int index = agree ? 0 : 1;
			options = decisions.get(index).getOptions();
			if(CollectionUtils.isEmpty(options)){
				return KeyValueCheckEnum.EMPTYOPT;
			}
			return KeyValueCheckEnum.format(TaskActivityInfoUtil.existOption(options, optKey, optValue));
		case 4:
			options = decisions.get(0).getOptions();
//			if(CollectionUtils.isEmpty(options)){
//				return KeyValueCheckEnum.EMPTYOPT;
//			}
			return KeyValueCheckEnum.format(TaskActivityInfoUtil.existOption(options, optKey, optValue));
		default:
			return KeyValueCheckEnum.FALSE;
		}
	}

	/**
	 * 根据流程ID审批该工作流(单条)
	 */
	private void approve(Context ctx, IEnactmentService wfService,
			AssignmentInfo ai) throws Exception {
		KeyValueCheckEnum enum1 = checkKeyAndValue(wfService, 3);
		if(enum1==KeyValueCheckEnum.FALSE){
			throw new JsonActionException("输入选择项错误");
		}
		if (StringUtils.isEmpty(opinion)) {
			opinion = agree ? "同意" : "不同意";
		}
		MultiApproveInfo info = new MultiApproveInfo();
		info.setAssignment(taskId);
		info.setBillId(BOSUuid.read(ai.getBizObjectIds()));
		info.setExtendedProperty("businuessObjectId", ai.getBizObjectIds());
		info.setExtendedProperty(IMultiBillWorkAgent.ASSIGNMENT_ID, taskId);
		info.setExtendedProperty(IMultiBillWorkAgent.IS_ADD_NEW,
				IMultiBillWorkAgent.IS_ADD_NEW);
		// 获取流程定义上的sms,mail设置
		Map extendedMap = wfService.getExtendedAttributesFromAssignment(taskId);;
		info.setIsMobelNotifyNext(Boolean.parseBoolean((String) extendedMap
				.get("isSendSMS")));
		info.setIsMailNotifyNext(Boolean.parseBoolean((String) extendedMap
				.get("isSendMail")));
		info.setOpinion(opinion);
		info.setIsPass(agree ? ApproveResult.PASS : ApproveResult.NOT_PASS);
		if(enum1==KeyValueCheckEnum.TRUE){
			try {
				info.setHandlerOpinion(Integer.parseInt(optKey));
			} catch (Exception e) {
				logger.error("把字符串[" + optKey + "]转换为整型数据时出错!", e);
				throw new JsonActionException(String.format("输入值optKey=%s不正确", optKey) , e);
			}
			info.setHandlerContent(optValue);
		}
		info.setStatus(MultiApproveStatusEnum.SUBMIT);

		// TODO :
		try{
			List rtn = new WfNextActsPersonsUtils(ctx).checkPostParticipants(taskId, optKey, extendedMap, false);
			if (rtn!=null && rtn.size() > 0) {
				throw new JsonActionException(3, "后续活动参与人为空,请在电脑上审批");
			}
		}catch (Exception e) {
			logger.error("查找下一步处理人失败", e);
		}
		
		MultiApprove multiApprove = new MultiApprove(ctx);
		multiApprove.submit(info);
	}

	
	private void decision(Context ctx, IEnactmentService wfService,
			AssignmentInfo assignment) throws Exception {
		if (StringUtils.isEmpty(optKey) || StringUtils.isEmpty(optValue)) {
			logger.error(String.format("输入参数decisionKey=[%s], decisionValue=[%s]", optKey, optValue));
			throw new JsonActionException("决策项值不正确");
		}
		KeyValueCheckEnum enum1 = checkKeyAndValue(wfService, 3);
		if(enum1==KeyValueCheckEnum.FALSE){
			throw new JsonActionException("输入决策项错误");
		}
		StringBuilder selectedIndices = new StringBuilder();
		StringBuilder selectedItems = new StringBuilder();
		selectedIndices.append(optKey);
		selectedItems.append(optValue);

		Map data = new HashMap();
		ManualDecisionInfo info = new ManualDecisionInfo();
		info.setSelectItemCount(1);
		info.setSelectedItems(selectedItems.toString());
		info.setSelectedIndices(selectedIndices.toString());
		info.setSelectItems(selectedItems.toString());
		String newId = BOSUuid.create(info.getBOSType()).toString();
		info.setId(BOSUuid.read(newId));
		data.put(newId, info);
		UserInfo userInfo = (UserInfo) ctx.get(SysContextConstant.USERINFO);
		String userid = userInfo.getId().toString();
		SubmitAssignResult result = wfService.submitAssignment(data, userid,
				assignment);
		if (!result.getStatus()) {
			throw new JsonActionException("决策失败");
		}
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public void setAgree(boolean agree) {
		this.agree = agree;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public void setOptKey(String optKey) {
		this.optKey = optKey;
	}

	public void setOptValue(String optValue) {
		this.optValue = optValue;
	}

}
