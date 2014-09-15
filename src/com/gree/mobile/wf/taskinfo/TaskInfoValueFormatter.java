package com.gree.mobile.wf.taskinfo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.gree.mobile.wf.taskinfo.xml.FormatAware;
import com.kingdee.util.enums.Enum;

public class TaskInfoValueFormatter {
	
	public String format(Object propValue, FormatAware formatAware) {
		if (propValue == null) {
			return null;
		}
		if (propValue instanceof Enum){
			return ((Enum)propValue).getAlias();
		}
		if(propValue instanceof byte[]){
			return "请看附件";
		}
		// boolean
		if (propValue instanceof Boolean) {
			String key = ((Boolean) propValue).booleanValue() ? "true" : "false";
			String text = formatAware.findInTextMap(key);
			if(text!=null){
				return text;
			}
			return ((Boolean) propValue).booleanValue() ? "是" : "否";
		}
		String format = formatAware.getFormat();
		// 大多数是String类型，逻辑放在前
		if (propValue instanceof String) {
			String value = (String) propValue;
			Map<String, String> map = formatAware.getTextMap();
			if (map != null) {
				String[] arrSplit = value.split("\\],\\[");
				if (arrSplit != null && arrSplit.length >= 2) {
					value = arrSplit[1].replaceAll("[\\[\\]\"]", "");
				}
				String[] vs = value.split(",");
				for (int i = 0; i < vs.length; i++) {
					String vv = map.get(vs);
					if(vv!=null){
						vs[i] = vv;
					}
				}
				value = Arrays.toString(vs).replaceAll("\\[", "").replaceAll("\\]", "");
			}

			if (StringUtils.isEmpty(format)) {
				return (String) propValue;
			} else {
				try {
					return String.format(format, propValue);
				} catch (Exception e) {
					return (String) propValue;
				}
			}
		}
		// decimal
		if (propValue instanceof BigDecimal) {
			if (StringUtils.isEmpty(format)) {
				format = "#,##0.##";
			}
			try {
				return new DecimalFormat(format).format(propValue);
			} catch (Exception e) {
				return new DecimalFormat("#,##0.##").format(propValue);
			}
		}
		
		if (propValue instanceof java.util.Date) {
			// java.sql.Date
			String defaultFormat = "yyyy-MM-dd";
			// java.sql.Timestamp
			if (propValue instanceof Timestamp) {
				defaultFormat = "yyyy-MM-dd HH:mm:ss";
			}
			if (StringUtils.isEmpty(format)) {
				format = defaultFormat;
			}
			String value;
			try {
				value = new SimpleDateFormat(format).format(propValue);
			}catch (Exception e) {
				value = new SimpleDateFormat(defaultFormat).format(propValue);
			}
			return value;
		}
		return propValue.toString();
	}
}
