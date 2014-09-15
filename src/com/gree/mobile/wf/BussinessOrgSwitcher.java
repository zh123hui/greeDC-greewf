package com.gree.mobile.wf;

import java.util.HashMap;
import java.util.List;

import com.kingdee.bos.BOSException;
import com.kingdee.bos.Context;
import com.kingdee.bos.framework.cache.CacheServiceFactory;
import com.kingdee.bos.framework.cache.config.CacheConfigManager;
import com.kingdee.bos.invokecounter.RPCInvokeCounter;
import com.kingdee.eas.base.permission.UserInfo;
import com.kingdee.eas.basedata.org.FullOrgUnitInfo;
import com.kingdee.eas.basedata.org.IOrgSwitchFacade;
import com.kingdee.eas.basedata.org.OrgSwitchFacadeFactory;
import com.kingdee.eas.basedata.org.OrgType;
import com.kingdee.eas.basedata.org.OrgUnitInfo;
import com.kingdee.eas.basedata.org.helper.OrgTypeRegister;
import com.kingdee.eas.common.EASBizException;
import com.kingdee.eas.util.app.ContextUtil;


public class BussinessOrgSwitcher {

	public static void switchForLogin(Context ctx) throws EASBizException, BOSException {
		UserInfo currentUserInfo = ContextUtil.getCurrentUserInfo(ctx);
		FullOrgUnitInfo defOrgUnit = currentUserInfo.getDefOrgUnit();
		if(defOrgUnit == null){
			return ;
		}
		String ouId = defOrgUnit.getId().toString();
		
		OrgTypeRegister.getInstance().initOrgTypeMgrExt();
		if (ctx == null) {
			return ;
	    }
	    HashMap map = (HashMap)ctx.get("OrgUnits");
	    if (map == null) {
	    	IOrgSwitchFacade iSwitch = OrgSwitchFacadeFactory.getRemoteInstance();
			map = iSwitch.getOrgs(ouId);
	    }
	    
	    List orgTypeList = OrgType.getEnumList();
		for (int i = 0; i < orgTypeList.size(); ++i) {
			OrgType curOrgType = (OrgType)orgTypeList.get(i);
			OrgUnitInfo orgUnitInfo = (OrgUnitInfo)map.get(curOrgType);
			if (orgUnitInfo != null) {
				ContextUtil.setCurrentOrgUnit(ctx, curOrgType, orgUnitInfo);
			}
		}
	    FullOrgUnitInfo ouInfo = (FullOrgUnitInfo)map.get("CurOU");
	    ContextUtil.setCurrentOrgUnit(ctx, ouInfo);
	    OrgUnitInfo fiInfo = (OrgUnitInfo)map.get(OrgType.Company);
	    if (fiInfo != null) {
	    	ctx.put("CompanyInfo", fiInfo);
	    	ctx.put("CurCompanyId", fiInfo.getId().toString());
	    }
	    
	    if (CacheConfigManager.getConfig() != null){
	    	try {
	    		CacheServiceFactory.getInstance().discardAll();
	    	} catch (Exception e) {
	    	}
	    }
	    RPCInvokeCounter.clearActionSet();
	}
	
}
