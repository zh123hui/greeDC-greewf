package com.gree.mobile.wf.action.backup;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import org.apache.log4j.Logger;

import com.gree.mobile.common.permission.UserContext;
import com.gree.mobile.common.permission.UserContextManager;
import com.gree.mobile.common.web.PathUtils;
import com.gree.mobile.common.web.action.JsonActionSupport;
import com.gree.mobile.common.web.exception.JsonActionException;
import com.gree.mobile.wf.taskinfo.TaskInfoConfig;
import com.gree.mobile.wf.taskinfo.TaskInfoEvaluater;
import com.gree.mobile.wf.taskinfo.TaskInfoManager;
import com.gree.mobile.wf.taskinfo.TemplateManager;
import com.gree.mobile.wf.taskinfo.template.simple.SimpleView;
import com.kingdee.bos.Context;
import com.kingdee.bos.dao.IObjectValue;
import com.kingdee.bos.metadata.IMetaDataLoader;
import com.kingdee.bos.metadata.MetaDataLoaderFactory;
import com.kingdee.bos.metadata.entity.EntityObjectInfo;
import com.kingdee.bos.util.BOSObjectType;
import com.kingdee.bos.util.BOSUuid;
import com.kingdee.bos.workflow.ProcessInstInfo;
import com.kingdee.bos.workflow.define.ActivityCollection;
import com.kingdee.bos.workflow.define.ActivityDef;
import com.kingdee.bos.workflow.define.ActivityType;
import com.kingdee.bos.workflow.define.ExtendedAttributeCollection;
import com.kingdee.bos.workflow.define.ExtendedAttributeDef;
import com.kingdee.bos.workflow.define.ProcessDef;
import com.kingdee.bos.workflow.service.ormrpc.EnactmentServiceFactory;
import com.kingdee.bos.workflow.service.ormrpc.IEnactmentService;
import com.kingdee.util.StringUtils;
/**
 * @deprecated
 * 
 */
public abstract class AbstractTaskInfoAction extends JsonActionSupport{

	private Logger logger = Logger.getLogger(this.getClass());
	protected String docId;			//单据ID
	
	protected abstract void prevExecute() throws Exception;
	protected abstract String getFormId() throws Exception;
	
	@Override
	public final String execute() {
		if(StringUtils.isEmpty(docId)){
			this.error("单据ID不能为空");
			return JSON;
		}
		try{
			prevExecute();
			doExecute();
			return NONE;
		}catch (Exception e) {
			String message = e.getMessage();
			logger.error(message, e);
			if(e instanceof JsonActionException){
				this.error(message, ((JsonActionException) e).getResult());
			}else{
				this.error(message);
			}
			return JSON;
		}
//		return SUCCESS;
	}
	private void doExecute() throws Exception {
		BOSUuid billId = BOSUuid.read(docId);
		BOSObjectType bosType = billId.getType();
		if("C7CEA331".equals(bosType.toString())){
			throw new JsonActionException("无详情");
		}
		UserContext uc = UserContextManager.getUserContext(getSession());
		Context ctx = uc.getBosContext();
		IMetaDataLoader loader = MetaDataLoaderFactory.getLocalMetaDataLoader(ctx);
		EntityObjectInfo entityInfo = loader.getEntity(bosType);
		String eoiName = entityInfo.getName();
		String eoiPackage = entityInfo.getPackage();
		String eoiPath = eoiPackage.replaceAll("[.]", "/");
		String webRealPath = PathUtils.getWebRealPath("/wf/info");
		File file = new File(webRealPath, eoiPath+"/"+eoiName+".xml");
		TaskInfoManager manager = TaskInfoManager.getInstance();
		TaskInfoConfig taskInfoConfig = manager.getTaskInfoConfig(bosType, file.getAbsolutePath());
		IObjectValue docObject = manager.loadBillObject(ctx, docId, taskInfoConfig.getSelectorItems());
		
		String formId = getFormId();
		TaskInfoEvaluater evaluater = new TaskInfoEvaluater(ctx, formId);
//		TemplateViewData data = TemplateManager.evaluate(docObject, taskInfoConfig, evaluater);
//		TemplateManager.build(getResponse().getWriter(), data);
	}
	protected String getFormIdByProcinstId(String procInstId)
			throws Exception {
		if (procInstId == null) {
			return null;
		}
		IEnactmentService wfService = EnactmentServiceFactory.createRemoteEnactService();
		ProcessInstInfo processInstInfo = wfService.getProcessInstInfo(procInstId);
		ProcessDef processDef = wfService
				.getProcessDefByDefineHashValue(processInstInfo
						.getProcDefHashValue());
		ActivityCollection activityCollection = processDef
				.getActivitys(ActivityType.MANPOWER);
		for (int i = 0, len = activityCollection.size(); i < len; i++) {
			ActivityDef actDef = activityCollection.get(i);
			if (actDef != null) {
				String formId = findFormId(actDef);
				if(formId!=null){
					return formId;
				}
			}
		}
		return null;
	}
	
	protected String getFormIdByAssignId(String taskId) throws Exception {
		if (taskId == null) {
			return null;
		}
		IEnactmentService wfService = EnactmentServiceFactory.createRemoteEnactService();
		Map resultMap = wfService.getActivityDefAndActivityInstInfo(taskId);
		ActivityDef actDef = (ActivityDef) resultMap.get("ACTIVITYDEF");
		return findFormId(actDef);
	}
	
	private String findFormId(ActivityDef actDef) {
		ExtendedAttributeCollection extendedAttributes = actDef.getActivityHeader().getExtendedAttributes();
		ExtendedAttributeDef isWebAttr = extendedAttributes.get("isWebBillApprove");
		if (isWebAttr == null || "true".equalsIgnoreCase(isWebAttr.getValue())) {
			return null;
		}
		ExtendedAttributeDef urlAttr = extendedAttributes.get("WebBillApproveUrl");
		if (urlAttr == null || StringUtils.isEmpty(urlAttr.getValue())) {
			return null;
		}
		return findFormIdWithUrl(urlAttr.getValue());
	}

	private String findFormIdWithUrl(String url) {
		String formId = null;
		int temp1 = url.indexOf("?formID=");
		int temp2 = url.indexOf("&formID=");
		int index0 = temp1 == -1 ? temp2 : temp1;
		if (index0 == -1) {
			return null;
		}
		int index1 = url.indexOf("&", index0);
		if (index1 == -1) {
			formId = url.substring(index0 + 8);
		} else {
			formId = url.substring(index0 + 8, index1);
		}
		String decodeFormId = null;
		try {
			decodeFormId = URLDecoder.decode(formId, "utf-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(String.format("URLDecoder.decode 失败  fromId= %s", formId), e);
		}
		return decodeFormId;
	}
	
	public void setDocId(String docId) {
		this.docId = docId;
	}

}
