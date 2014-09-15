package com.gree.mobile.wf.config;

import com.gree.mobile.common.web.PathUtils;


public class GreeWfCofingCenter extends PropertiesConfigBean {

	private static GreeWfCofingCenter instance = new GreeWfCofingCenter();
	
	public static GreeWfCofingCenter getInstance(){
		return instance;
	}

	private GreeWfCofingCenter() {
		super(PathUtils.getConfigRoot()+"/gree_wf_config.properties");
	}
	
}
