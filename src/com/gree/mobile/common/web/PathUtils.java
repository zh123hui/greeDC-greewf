package com.gree.mobile.common.web;

import javax.servlet.ServletContext;

import org.springframework.util.Assert;


public class PathUtils {

	public static String getConfigRoot() {
		return getWebRealPath("/config");
	}

	public static String getWebRealPath(String path){
		ServletContext servletContext = ServletContextSingleton.getInstance().getServletContext();
		return getWebRealPath(servletContext, path);//System.getProperty("eas.properties.dir");
	}

	public static String getWebRealPath(ServletContext servletContext, String path) {
		Assert.notNull(servletContext, "ServletContext must not be null");
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		String realPath = servletContext.getRealPath(path);
		if (realPath == null) {
			throw new RuntimeException(
					"ServletContext resource [" + path + "] cannot be resolved to absolute file path - " +
					"web application archive not expanded?");
		}
		return realPath;
	}
}
