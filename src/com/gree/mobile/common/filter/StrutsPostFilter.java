package com.gree.mobile.common.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter;

import com.gree.mobile.common.error.ErrorDispatcher;
import com.gree.mobile.common.error.ErrorResultEnum;
import com.opensymphony.xwork2.config.entities.ActionConfig;

/**
 * 执行struts filter 之前的
 * 
 */
public class StrutsPostFilter extends StrutsPrepareAndExecuteFilter{
	private static final Logger logger = Logger.getLogger(GreeDispatcherFilter.class);
	private Dispatcher dispatcher;
	
	@Override
	protected void postInit(Dispatcher dispatcher, FilterConfig filterConfig) {
		super.postInit(dispatcher, filterConfig);
		this.dispatcher = dispatcher;
	}
	
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		String action = findActionName(request);
		ActionConfig actionConfig = dispatcher.getConfigurationManager().getConfiguration().getRuntimeConfiguration().getActionConfig("/", action);
    	if(actionConfig==null){
    		ErrorDispatcher.goInFilter(request, resp, ErrorResultEnum.INVALID_REQUEST.getResult());
    		return;
    	}
		super.doFilter(request, resp, chain);
	}

	private String findActionName(HttpServletRequest request) {
		String servletPath = request.getServletPath();
		return servletPath.substring(1, servletPath.length()-7);
	}

}
