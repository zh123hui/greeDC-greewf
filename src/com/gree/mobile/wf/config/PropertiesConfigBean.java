package com.gree.mobile.wf.config;

import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;


public class PropertiesConfigBean {
	
	private Configuration cfg;
	
	protected PropertiesConfiguration getConfiguration(){
		return ((PropertiesConfiguration)cfg);
	}
	
	protected PropertiesConfigBean(String fileName){
		this(fileName, null);
	}
	protected PropertiesConfigBean(String fileName, String encoding){
		this.cfg = buildConfiguration(fileName, encoding);
	}

	public static PropertiesConfigBean getConfiguration(String fileName, String encoding){
		return new PropertiesConfigBean(fileName, encoding);
	}
	
	
	public String getString(String key, boolean assertExist){
		String value = getString(key);
		if(value == null && assertExist){
			throw new GreeConfigException(String.format("配置文件[%s]未配置[%s]", getFileName(), key));
		}
		return value;
	}
	
	public String getStringWithExt(String key, String keyExt){
		if(keyExt == null){
			return getString(key);
		}
		String value = getString(key+"_"+keyExt);
		if(value == null){
			value = getString(key);
		}
		return value;
	}
	
	public String getFileName() {
		return getConfiguration().getFileName();
	}

	
	public boolean getBoolean(String key){
		return cfg.getBoolean(key);
	}
	public boolean getBoolean(String key, boolean defaultValue){
		return cfg.getBoolean(key, defaultValue);
	}
	
	public int getInt(String key){
		return cfg.getInt(key);
	}
	
	public String getString(String key){
		return cfg.getString(key);
	}
	
	public String[] getStringArray(String key){
		return cfg.getStringArray(key);
	}

	private Configuration buildConfiguration(String fileName, String encoding) {
		if(fileName == null){
			throw new GreeConfigException("文件路径不能为空!");
		}
		if(encoding == null){
			encoding = "UTF-8";
		}
		try {
			File file = new File(fileName);
			if(!file.exists()){
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			PropertiesConfiguration cfg = new PropertiesConfiguration(fileName);
			cfg.setEncoding(encoding);
			cfg.setReloadingStrategy(new FileChangedReloadingStrategy());
			return cfg;
		} catch (ConfigurationException e) {
			throw new GreeConfigException(String.format("读取配置文件[%s]失败!", fileName));
		} catch (IOException e) {
			throw new GreeConfigException(String.format("创建文件[%s]失败!", fileName));
		}
	}
	
}
