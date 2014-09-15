package com.gree.mobile.test;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.gree.mobile.common.web.PathUtils;
import com.gree.mobile.wf.taskinfo.TaskInfoConfig;
import com.gree.mobile.wf.taskinfo.TaskInfoManager;
import com.kingdee.bos.metadata.MetaDataLoaderFactory;
import com.kingdee.bos.metadata.entity.EntityObjectInfo;
import com.kingdee.bos.util.BOSObjectType;

public class XmlTester {

	public static void proccess(HttpServletRequest req, HttpServletResponse resp) throws Exception{
		String bosTypeStr = req.getParameter("bosType");
		if(!StringUtils.isEmpty(bosTypeStr)){
			BOSObjectType bosType = BOSObjectType.create(bosTypeStr);
			EntityObjectInfo entityInfo = MetaDataLoaderFactory.getRemoteMetaDataLoader().getEntity(bosType);
			String eoiName = entityInfo.getName();
			String eoiPackage = entityInfo.getPackage();
			String eoiPath = eoiPackage.replaceAll("[.]", "/");
			String webRealPath = PathUtils.getWebRealPath("/wf/info");
			File file = new File(webRealPath, eoiPath+"/"+eoiName+".xml");
			TaskInfoManager manager = TaskInfoManager.getInstance();
			TaskInfoConfig taskInfoConfig = manager.getTaskInfoConfig(bosType, file.getAbsolutePath());
			
//			TaskInfoEvaluater evaluater = new TaskInfoEvaluater();
//			View view = TemplateManager.evaluate(docObject, taskInfoConfig.getViewNote(), evaluater);
//			TemplateManager.write(getResponse().getWriter(), view);
		}
	}
	
	
}
