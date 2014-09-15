package com.gree.mobile.common;

import java.util.List;

import org.apache.log4j.Logger;

import com.kingdee.bos.Context;
import com.kingdee.eas.base.usermonitor.IUserMonitor;
import com.kingdee.eas.base.usermonitor.UMRegistryInfo;
import com.kingdee.eas.base.usermonitor.UserMonitorFactory;

public class GreeMonitoredUserUtils {
	private static final Logger logger = Logger.getLogger(GreeMonitoredUserUtils.class);
	
	// 更新监控用户的窗口信息
	public static void updateAppInfoOfMonitoredUser(Context ctx, String appClientId) {
		try {
			UMRegistryInfo monitoredUser = getUserInMonitorPanel(ctx);
			if (monitoredUser == null) {
				return;
			}
			monitoredUser.setOpenWindowTitle("移动工作流");
			updateUserInMonitorPanel(monitoredUser);
		} catch (Exception e) {
			logger.error("无法更新监控用户的窗口信息", e);
		}
	}

	public static void updateUserInMonitorPanel(UMRegistryInfo monitoredUser)
			throws Exception {
		IUserMonitor iUserMonitor = UserMonitorFactory.getRemoteInstance();
		iUserMonitor.updateLastOperate(monitoredUser);
	}

	public static UMRegistryInfo getUserInMonitorPanel(Context ctx)throws Exception {
		if (ctx == null) {
			return null;
		}
		IUserMonitor iUserMonitor = UserMonitorFactory.getRemoteInstance();
		List monitorUsers = iUserMonitor.findUsers(ctx.getAIS(), ctx.getUserName());
		for (Object o : monitorUsers) {
			UMRegistryInfo info = (UMRegistryInfo) o;
			if (info.getId().equals(ctx.getContextID())) {
				return info;
			}
		}
		return null;
	}
}
