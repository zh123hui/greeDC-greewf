package com.gree.mobile.wf.taskinfo;

import ognl.Ognl;

import com.kingdee.bos.dao.IObjectValue;

public class ValueUtil {

	public static Object findValue(String expr, Object root) {
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
}
