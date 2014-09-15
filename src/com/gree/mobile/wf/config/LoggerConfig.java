package com.gree.mobile.wf.config;

import org.apache.log4j.Logger;


public class LoggerConfig {

	public static boolean isDebug(Logger logger){
		return GreeWfCofingCenter.getInstance().getBoolean("logger.debug.enabled", false) || (logger!=null && logger.isDebugEnabled());
	}
	
}
