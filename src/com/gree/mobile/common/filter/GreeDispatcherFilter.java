package com.gree.mobile.common.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.gree.mobile.common.error.ErrorDispatcher;
import com.gree.mobile.common.error.ErrorResult;
import com.gree.mobile.common.error.ErrorResultEnum;
import com.gree.mobile.wf.config.LoggerConfig;


public class GreeDispatcherFilter implements Filter {
	
	private static final Logger logger = Logger.getLogger(GreeDispatcherFilter.class);
	
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest sourceRequest = (HttpServletRequest) req;
		String servletPath = sourceRequest.getServletPath();
		if(servletPath.startsWith("/test/")){
			chain.doFilter(req, resp);
			return;
		}
		if(!servletPath.equalsIgnoreCase("/wf.html")){
			String msg = String.format("请求路径格式不正确, url=%s", servletPath);
			if(LoggerConfig.isDebug(logger)){
				logger.error(msg);
			}
			((HttpServletResponse) resp).sendError(HttpServletResponse.SC_NOT_FOUND, msg);
			return ;
		}
		
		String contentType = sourceRequest.getHeader("content-type");
        if (contentType != null) {
            int idx = contentType.indexOf(";");
            if (idx != -1){
            	contentType = contentType.substring(0, idx);
            }
        }
        if ((contentType == null) || !contentType.equalsIgnoreCase("application/json")) {
        	//无效请求,目前无非json请求.
            String msg = "请求ContentType不正确";
        	if(LoggerConfig.isDebug(logger)){
                logger.error(msg);
			}
        	((HttpServletResponse) resp).sendError(HttpServletResponse.SC_FORBIDDEN, msg);
        	return ;
        }
    	
    	GreeRequestWrapper greeRequest = new GreeRequestWrapper(sourceRequest);
    	HttpServletRequestWrapper requestWrapper = greeRequest.buildWrapper();
    	try{
    		greeRequest.explain();
    	}catch (Exception e) {
    		logger.error(e.getMessage(), e);
    		ErrorDispatcher.goInFilter(requestWrapper, resp, ErrorResultEnum.INVALID_JSON.getResult());
    		return;
		}
    	String action = greeRequest.getAction();
    	
    	if(StringUtils.isEmpty(action)){
    		if(LoggerConfig.isDebug(logger)){
				logger.error("请求体中action为空");
			}
    		ErrorDispatcher.goInFilter(requestWrapper, resp, new ErrorResult(-1, "action不能为空"));
    		return;
    	}
    	
    	requestWrapper.getRequestDispatcher("/"+action+".action").forward(requestWrapper, resp);
	}

	public void init(FilterConfig filterConfig) {
	}

	public void destroy() {
	}
}
