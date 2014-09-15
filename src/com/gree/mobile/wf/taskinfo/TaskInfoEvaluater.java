package com.gree.mobile.wf.taskinfo;

import ognl.Ognl;

import org.apache.commons.lang.StringUtils;

import com.gree.mobile.wf.taskinfo.xml.FormatAware;
import com.kingdee.bos.Context;
import com.kingdee.bos.dao.IObjectValue;

public class TaskInfoEvaluater {

	private DynamicWebDoc dynamicWebDoc;
	private TaskInfoValueFormatter formatter = new TaskInfoValueFormatter();
	
	public TaskInfoEvaluater() {
	    super();
	    dynamicWebDoc=null;
    }
	public TaskInfoEvaluater(Context ctx, String formId) {
		super();
		if( formId!=null){
			dynamicWebDoc= new DynamicWebDoc(ctx, formId);
		}
	}
	public boolean isFormBill() {
		return dynamicWebDoc!=null;
	}

	public Object getOwnPropertyValue(Object boValue, String name) {
		return getPropertyValue(boValue, name, true);
	}
	
	public Object getLinkPropertyValue(Object boValue, String name) {
		return getPropertyValue(boValue, name, false);
	}
	
	private Object getPropertyValue(Object boValue, String name, boolean isOwnProperty) {
		Object obj = findValue(name, boValue);
		if(obj == null){
			return null;
		}
		if(isOwnProperty && name.indexOf(".")==-1 && isFormBill()){
			Object newObj = dynamicWebDoc.tryFindValueInWebBill(name, obj);
			if(newObj!=null){
				return newObj;
			}
		}
		return obj;
	}
	
	private Object findValue(String expr, Object root) {
		if (expr == null || root == null) {
			return null;
		}
		Object value = null;
		try {
			value = Ognl.getValue(expr, root);
		} catch (Exception e) {
		}
		if (value != null) {
			return value;
		}
		if (!(root instanceof IObjectValue)) {
			return null;
		}
		IObjectValue iObjectValue = (IObjectValue) root;
		// 表达式是否为联级的,如: xxx.yyy
		int indexOf = expr.indexOf(".");
		if (indexOf == -1) {
			return iObjectValue.get(expr);
		}
		Object root2 = findValue(expr.substring(0, indexOf), iObjectValue);
		return findValue(expr.substring(indexOf + 1), root2);
	}
	
	public String getDisplayValue(Object boValue, String propName, FormatAware formatAware, boolean isOwnProperty) {
		Object propValue = getPropertyValue(boValue, propName, isOwnProperty);
		if(propValue==null){
			String defaultValue = formatAware.getDefaultValue();
			if(!StringUtils.isEmpty(defaultValue)){
			    return defaultValue;
			}
		}
		String displayValue = formatter.format(propValue, formatAware);
		if(displayValue!=null && displayValue.equals(formatAware.getNullValue())){
		    return null;
		}
		return displayValue;
	}
	
}
