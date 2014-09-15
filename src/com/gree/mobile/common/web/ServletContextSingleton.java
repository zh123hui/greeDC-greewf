package com.gree.mobile.common.web;

import javax.servlet.ServletContext;

public final class ServletContextSingleton {
	
	private ServletContext servletContext;

    private static ServletContextSingleton singleton;

    private ServletContextSingleton() {
    }

    public static ServletContextSingleton getInstance() {
        if (singleton == null) {
            singleton = new ServletContextSingleton();
        }
        return singleton;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }
    
    public void setServletContext(ServletContext context) {
        servletContext = context;
    }
	
}
