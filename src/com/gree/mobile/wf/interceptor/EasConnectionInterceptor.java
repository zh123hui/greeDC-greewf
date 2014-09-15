package com.gree.mobile.wf.interceptor;

import org.apache.log4j.Logger;

import com.gree.mobile.wf.jdbc.EasDataSourceUtils;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class EasConnectionInterceptor extends AbstractInterceptor {
	private static final Logger logger = Logger.getLogger(EasConnectionInterceptor.class);
	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		String invoke = invocation.invoke();
		try{
		    EasDataSourceUtils.releaseConnection();
		}catch (Exception e) {
		    logger.error("释放连接失败", e);
        }
		return invoke;
	}
	
}
