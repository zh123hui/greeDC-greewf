package com.gree.mobile.wf.taskinfo.xml;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class FormatAware {

	private String format; 
	private String valueToText;
	private String nullValue;
	private String defaultValue;
	private Map<String, String> textMap;
	public String getNullValue() {
		return nullValue;
	}
	public void setNullValue(String nullValue) {
		this.nullValue = nullValue;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	/**
	 * 
	 * @param valueToText v:t,v:t
	 */
	public void setValueToText(String valueToText) {
		this.valueToText = valueToText;
		createTextMap(valueToText);
	}
	
	public String getValueToText() {
		return valueToText;
	}
	public String findInTextMap(String key){
		if(textMap==null){
			return null;
		}
		return textMap.get(key);
	}
	public Map<String, String> getTextMap(){
		return textMap;
	}
	private void createTextMap(String valueToText) {
		if(!StringUtils.isEmpty(valueToText)){
			textMap = new HashMap<String, String>();
			String[] arr = valueToText.split("\\,");
			for(String s : arr){
				String[] kv = s.split("\\:");
				if(kv.length!=2){
					continue;
				}
				textMap.put(kv[1], kv[2]);
			}
		}
	}
    public String getDefaultValue() {
        return defaultValue;
    }
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
