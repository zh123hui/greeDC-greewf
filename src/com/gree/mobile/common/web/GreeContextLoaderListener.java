package com.gree.mobile.common.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.log4j.Logger;

import com.kingdee.eas.cp.eip.sso.IEasAuthHandler;

public class GreeContextLoaderListener extends org.springframework.web.context.ContextLoaderListener {
    private static final Logger logger = Logger.getLogger(GreeContextLoaderListener.class);

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        ServletContextSingleton.getInstance().setServletContext(servletContext);
        super.contextInitialized(event);
        logger.info("准备初始化com.gree.mobile.sp.sso.EncryptPasswordAuthHandler");
        try {
            String clazz = "com.gree.mobile.sp.sso.EncryptPasswordAuthHandler";
            Class.forName(clazz, true, IEasAuthHandler.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            logger.error("未部署sso扩展包", e);
        } catch (Throwable t) {
            logger.error("execute EncryptPasswordAuthHandler.init() fail.", t);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        super.contextDestroyed(event);
    }

}
