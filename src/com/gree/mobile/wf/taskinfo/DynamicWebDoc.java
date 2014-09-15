package com.gree.mobile.wf.taskinfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.kingdee.bos.Context;
import com.kingdee.bos.framework.ejb.EJBFactory;
import com.kingdee.bos.util.BOSUuid;
import com.kingdee.eas.base.form.core.UserDefinedData.UserDefinedDataEntryInfo;
import com.kingdee.util.db.SQLUtils;
import com.kingdee.util.enums.DoubleEnum;
import com.kingdee.util.enums.EnumUtils;
import com.kingdee.util.enums.FloatEnum;
import com.kingdee.util.enums.IntEnum;
import com.kingdee.util.enums.LongEnum;
import com.kingdee.util.enums.StringEnum;

/**
 * 动态web单据
 */
public class DynamicWebDoc {
	private static final Logger logger = Logger.getLogger(DynamicWebDoc.class);
	
	private Context ctx;
	private String formId;
	
	// 获取动态web单据中的枚举，辅助资料类型的属性
	private Map<String, Integer> enumPropMap = null;
	private Set<String> assistPropSet = null;

	
	public DynamicWebDoc(Context ctx, String formId) {
		super();
		if (ctx==null || formId==null) {
			throw new IllegalArgumentException("Context and formId is null");
		}
		this.ctx = ctx;
		this.formId = formId;
		init();
	}

	public Object tryFindValueInWebBill(String propName, Object obj) {
		// 是否为动态web单据的枚举属性
		Integer integer = enumPropMap.get(propName);
		if (integer != null) {
			// logger.info(String.format("属性[%s]是否为枚举?枚举值为[%s],原来的值为[%s]",
			// name, integer.intValue(), obj.toString()));
			int enumType = integer.intValue();
			return findEnumPropDisplayValue(enumType, obj);
		}
		if (assistPropSet.contains(propName)) {// 是否为动态web单据的辅助资料属性
//			logger.info("辅助资料属性: " + propName + " 值为: " + obj);
			return findAssistPropDisplayValue(obj);
		}
		return null;
	}
	private void init() {
		enumPropMap = getEnumProps();
		assistPropSet = getAssistPropSet();
	}
	/**
	 * 获取辅助资料的值
	 * 
	 * @param value
	 * @return
	 */
	private Object findAssistPropDisplayValue(Object value) {
		if (value == null) {
			return null;
		}
		String id = null;
		// logger.info("value instanceof UserDefinedDataEntryInfo : " + (value
		// instanceof UserDefinedDataEntryInfo));
		// ((com.kingdee.eas.base.form.core.UserDefinedData.UserDefinedDataEntryInfo)obj).get("datavalue")
		if (value instanceof UserDefinedDataEntryInfo) {
			UserDefinedDataEntryInfo udde = (UserDefinedDataEntryInfo) value;
			Object datavalue = udde.getDataValue();
			if (datavalue != null) {
				return datavalue;
			}
			BOSUuid bosuuid = udde.getId();
			if (bosuuid == null) {
				return null;
			}
			id = bosuuid.toString();
		} else if (value instanceof String) {
			id = (String) value;
		} else {
			return null;
		}

		Connection conn = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		String dispValue = null;
		try {
			logger.info("---------格式化辅助资料 assist,orignvalue=" + id);
			conn = EJBFactory.getConnection(ctx);
			ps1 = conn
					.prepareStatement("select FDataValue_l2 from t_bas_userdefineddataentry where FID=?");
			ps1.setString(1, id);
			rs1 = ps1.executeQuery();
			if (rs1.next()) {
				dispValue = rs1.getString("FDataValue_l2");
			}
			logger.info("---------格式化辅助资料,orignvalue=" + value + ",display="
					+ dispValue);
		} catch (Exception e1) {
			logger.error("error when read assist display Value", e1);
		} finally {
			SQLUtils.cleanup(rs1, ps1, conn);
		}
		return dispValue;
	}
	private Object findEnumPropDisplayValue(int enumType, Object propValue) {
		if (propValue == null || propValue.toString() == null || propValue.toString().trim().length() == 0) {
			return null;
		}
		Connection conn = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		StringBuffer sb = new StringBuffer();
		String strValue = propValue.toString();
		try {
			logger.info("查找枚举,enumType=" + enumType + ",orignvalue="
					+ propValue);
			conn = EJBFactory.getConnection(ctx);
			// 多选时，存储规则为1,2(逗号分割),与sql的in规则暗合。
			ps1 = conn
					.prepareStatement("select FDisplay_l2 from t_bas_formenumitem where FEnumid=? and FValue in ( "
							+ strValue + " ) ");
			ps1.setInt(1, enumType);
			rs1 = ps1.executeQuery();
			boolean found = false;
			while (rs1.next()) {
				found = true;
				sb.append(",");
				sb.append(rs1.getString("FDisplay_l2"));
			}
			if (!found) {
				ps1 = conn
						.prepareStatement("select FEnumClassName from t_bas_formenum where fid=?");
				ps1.setInt(1, enumType);
				rs1 = ps1.executeQuery();
				String className = null;
				while (rs1.next()) {
					className = rs1.getString("FEnumClassName");
					break;
				}
				if (!org.apache.commons.lang.StringUtils.isEmpty(className)) {
					// logger.info("找到枚举类: "+className);
					Class<?> forName = Class.forName(className, true,
							com.kingdee.util.enums.Enum.class.getClassLoader());
					com.kingdee.util.enums.Enum enum1 = null;
					if (IntEnum.class.isAssignableFrom(forName)) {
						enum1 = EnumUtils.getEnum(forName, Integer
								.parseInt(strValue));
					} else if (StringEnum.class.isAssignableFrom(forName)) {
						enum1 = EnumUtils.getEnum(forName, strValue);
					} else if (DoubleEnum.class.isAssignableFrom(forName)) {
						enum1 = EnumUtils.getEnum(forName, Double
								.parseDouble(strValue));
					} else if (LongEnum.class.isAssignableFrom(forName)) {
						enum1 = EnumUtils.getEnum(forName, Long
								.parseLong(strValue));
					} else if (FloatEnum.class.isAssignableFrom(forName)) {
						enum1 = EnumUtils.getEnum(forName, Float
								.parseFloat(strValue));
					}
					if (enum1 != null) {
						return enum1.getAlias();
					}
				}
			}
			logger.info("找到枚举值,enumType=" + enumType + ",orignvalue=" + propValue + ",display=" + sb);
		} catch (Exception e1) {
			logger.error("error when read enum value", e1);
		} finally {
			SQLUtils.cleanup(rs1, ps1, conn);
		}
		if (sb.length() > 1) {
			return sb.substring(1);// 截断第一个逗号
		} else {
			return null;
		}
	}
	
	// 对于动态Web单据，需要处理在界面上定义的枚举
	// 该方法返回form上的枚举属性对应的自定义枚举类型
	private Map<String, Integer> getEnumProps() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		Connection conn = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		try {
//			logger.info("---------获取所有枚举属性,formId=" + formId);
			conn = EJBFactory.getConnection(ctx);
			ps1 = conn.prepareStatement("select FPropertyName,FEnumType from t_bas_formfield where FFormID=? and FEnumType is not null and FEnumType!=0");
			ps1.setString(1, formId);
			rs1 = ps1.executeQuery();
			while (rs1.next()) {
				String propName = rs1.getString("FPropertyName");
				int enumType = rs1.getInt("FEnumType");
//				logger.info("---------找到枚举,propName=" + propName
//						+ ", enumtype=" + enumType);
				map.put(propName, enumType);
			}
		} catch (Exception e1) {
			logger.error("error when read enum type", e1);
		} finally {
			SQLUtils.cleanup(rs1, ps1, conn);
		}
		if (map.size() == 0) {
			return null;
		}
		return map;
	}
	
	private Set<String> getAssistPropSet() {
		Set<String> set = new HashSet<String>();
		Connection conn = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		try {
			logger.info("---------获取所有辅助资料,formId=" + formId);
			conn = EJBFactory.getConnection(ctx);
			ps1 = conn
					.prepareStatement("select FPropertyName,FLink from t_bas_formfield where FFormID=? and FElementTypeID=30");
			ps1.setString(1, formId);
			rs1 = ps1.executeQuery();
			while (rs1.next()) {
				String propName = rs1.getString("FPropertyName");
				set.add(propName);
				logger.info("---------找到辅助资料,propName=" + propName
						+ " is assist prop");
			}
		} catch (Exception e1) {
			logger.error("error when read assist propName", e1);
		} finally {
			SQLUtils.cleanup(rs1, ps1, conn);
		}
		return set;
	}
	
}
