package com.gree.mobile.wf.action;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.gree.mobile.common.permission.UserContext;
import com.gree.mobile.common.permission.UserContextManager;
import com.gree.mobile.common.web.action.JsonAction;
import com.gree.mobile.common.web.exception.JsonActionException;
import com.kingdee.bos.ctrl.swing.StringUtils;
import com.kingdee.bos.workflow.participant.Person;
import com.kingdee.bos.workflow.service.ormrpc.EnactmentServiceFactory;
import com.kingdee.bos.workflow.service.ormrpc.IEnactmentService;
import com.kingdee.bos.workflow.util.ApplicationUtil;

public class DeliverTaskAction extends JsonAction{

	private String taskId;			//任务ID
	private String personId;		//转交接收者ID
	private String opinion;			//转交意见

	
	@Override
	public Object doExecute() throws Exception {
		if(StringUtils.isEmpty(personId)){
			throw new JsonActionException("转交人不能为空");
		}
		if(StringUtils.isEmpty(taskId)){
			throw new JsonActionException("任务ID不能为空");
		}
		if(StringUtils.isEmpty(opinion)){
			throw new JsonActionException("转交意见不能为空");
		}
		IEnactmentService wfService = EnactmentServiceFactory.createRemoteEnactService();
		UserContext uc = UserContextManager.getUserContext(getSession());
//		UserInfo currentUserInfo = uc.getUserInfo();
		if(personId.equals(uc.getPersonId())){
			throw new JsonActionException("不能转交给自己");
		}
		Person[] persons = wfService.getPersonByPersonID(personId);
		if(persons==null || persons.length==0 || persons[0]==null){
			throw new JsonActionException("转交人ID无效");
		}
		String toUserID = persons[0].getUserId();
//		persons[0].getUserName(locale);
		if(StringUtils.isEmpty(toUserID)){
			throw new Exception("转交人没有对应的登录用户,不能转交");
		}
		Locale[] locales = ApplicationUtil.getContextLocales(uc.getBosContext());
		Map<Locale, String> opinionMap = new HashMap<Locale, String>();
		for (int i = 0; i < locales.length; i++) {
			Locale local = locales[i];
			String data = opinion+ "\n--deliver to "+ persons[0].getUserName(local)+" by mobile client.";
			opinionMap.put(local, data);
		}
		wfService.forwardAssignment(taskId, toUserID, opinionMap);
		return null; 
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	
}
