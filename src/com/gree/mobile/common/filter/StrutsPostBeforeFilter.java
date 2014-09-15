package com.gree.mobile.common.filter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.gree.mobile.common.GreeMonitoredUserUtils;
import com.gree.mobile.common.error.ErrorDispatcher;
import com.gree.mobile.common.error.ErrorResultEnum;
import com.gree.mobile.common.permission.UserContext;
import com.gree.mobile.common.permission.UserContextManager;
import com.gree.mobile.wf.config.EASLoginConfig;
import com.kingdee.bos.Context;
import com.kingdee.eas.base.usermonitor.UMRegistryInfo;
import com.kingdee.eas.cp.common.web.util.WebContextUtil;

/**
 * 执行struts filter 之前的
 * 
 */
public class StrutsPostBeforeFilter implements Filter{
	private static final Logger logger = Logger.getLogger(GreeDispatcherFilter.class);
	private static final Set<String> ignoreActions = new HashSet<String>();
	static {
		ignoreActions.add("login");
		ignoreActions.add("logout");
	}
	
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		String actionName = findActionName(request);
		if("error".equals(actionName)){
			chain.doFilter(request, resp);
			return;
		}
		//作登录检查
    	UserContext uc = UserContextManager.getUserContext(request.getSession());
    	if(uc==null){
    		if(ignoreActions.contains(actionName)){
    			chain.doFilter(request, resp);
    		}else{
    			ErrorDispatcher.goInFilter(request, resp, ErrorResultEnum.SESSION_EXPIRED.getResult());
    		}
    		return;
    	}
		Context bosContext = uc.getBosContext();
		UMRegistryInfo monitoredUser = null;
		try {
			monitoredUser = GreeMonitoredUserUtils.getUserInMonitorPanel(bosContext);
		} catch (Exception e) {
			logger.error("获取监控面板的用户信息失败", e);
		}
		if(monitoredUser==null){
			request.getSession().invalidate();
			ErrorDispatcher.goInFilter(request, resp, ErrorResultEnum.SESSION_EXPIRED.getResult());
			return;
		}
		
		if(!EASLoginConfig.getDefault().getSlnName().equals(bosContext.getSolution())
				|| !EASLoginConfig.getDefault().getDbCode().equals(bosContext.getAIS())){
			request.getSession().invalidate();
			ErrorDispatcher.goInFilter(request, resp, ErrorResultEnum.SESSION_EXPIRED.getResult());
			logger.info("数据库配置变更,请重新登录");
			return;
		};
		
    	WebContextUtil.initRpcConfig(bosContext);
		chain.doFilter(request, resp);
	}
	private String findActionName(HttpServletRequest request) {
		String servletPath = request.getServletPath();
		return servletPath.substring(1, servletPath.length()-7);
	}
	public void destroy() {
	}
	public void init(FilterConfig filterConfig) throws ServletException {
	}

}
