package com.gree.mobile.common.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.log4j.Logger;
import org.apache.struts2.json.JSONUtil;
import org.springframework.util.Assert;

import com.gree.mobile.wf.config.LoggerConfig;


public class GreeRequestWrapper extends HttpServletRequestWrapper implements HttpServletRequest {

	private static final Logger logger =Logger.getLogger(GreeRequestWrapper.class);
	private String action;
	private String data;
	
	private boolean origin=true;
	
	public GreeRequestWrapper(HttpServletRequest request) {
		super(request);
	}
	
	public HttpServletRequestWrapper buildWrapper(){
		return new HttpServletRequestWrapper(this);
	}
	
	public String getAction() {
		return action;
	}

	public void explain() throws IOException, ServletException {
		BufferedReader reader = super.getReader();
		StringBuilder buffer = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        origin=false;
        if(buffer.length()==0){
        	if(LoggerConfig.isDebug(logger)){
        		logger.debug("request body is null, must be json string");
        	}
        	throw new ServletException("请求主体不是json串");
        }
        try{
        	String inputString = buffer.toString();
        	if(LoggerConfig.isDebug(logger)){
        		logger.debug("input json is : "+inputString);
        	}
			Object obj = JSONUtil.deserialize(inputString);
            if (obj instanceof Map) {
            	Map map = (Map) obj;
            	if(map.get("action")==null || map.get("data")==null){
            		throw new ServletException("请求主体值json结构不正确");
            	}
            	this.action = (String)map.get("action");
            	this.data = JSONUtil.serialize(map.get("data"));
            }else{
            	throw new ServletException("请求主体值json结构不正确");
            }
        }catch (Exception e) {
        	throw new ServletException("解析输入流失败", e);
        }
	}
	
	@Override
	public BufferedReader getReader() throws IOException {
		if(origin){
			return super.getReader();
		}
		BufferedReader bufferedReader = new BufferedReader(new StringReader(data));
		origin=true;
		return bufferedReader;
	}
	
	@Override
	public ServletInputStream getInputStream() throws IOException {
		if(origin){
			return super.getInputStream();
		}
		ServletInputStream input = new InnerRequestInputStream(new ByteArrayInputStream(data.getBytes("UTF-8")));
		origin=true;
		return input;
	}
	
	private static class InnerRequestInputStream extends ServletInputStream {
		private final InputStream target;
		public InnerRequestInputStream(InputStream sourceStream) {
			Assert.notNull(sourceStream, "Source InputStream must not be null");
			this.target = sourceStream;
		}
		@Override
		public int read() throws IOException {
			return target.read();
		}
		public void close() throws IOException {
			super.close();
			this.target.close();
		}
	}

}
