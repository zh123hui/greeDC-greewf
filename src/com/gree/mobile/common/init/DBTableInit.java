package com.gree.mobile.common.init;

import com.gree.mobile.wf.jdbc.EasDbUtil;
import com.kingdee.bos.BOSException;

public class DBTableInit {

	public static void init() throws BOSException {
		StringBuffer sb = new StringBuffer();
		sb.append(" IF NOT EXISTS ");
		sb.append(" ( ");
		sb.append(" SELECT ");
		sb.append(" * ");
		sb.append(" FROM ");
		sb.append(" KSQL_USERTABLES ");
		sb.append(" WHERE ");
		sb.append(" KSQL_TABNAME ='T_GMOBILE_PUSH') ");
		sb.append(" CREATE TABLE ");
		sb.append(" T_GMOBILE_PUSH ");
		sb.append(" ( ");
		sb.append(" FPushToken VARCHAR(100) NOT NULL, ");
		sb.append(" FLastUserID VARCHAR(50), ");
		sb.append(" FLastUpdateTime DATETIME, ");
		sb.append(" FCreateTime DATETIME, ");
		sb.append(" CONSTRAINT PK_T_GMOBILE_PUSH PRIMARY KEY (FPushToken) ");
		sb.append(" ); ");
		EasDbUtil.execute(EasDbUtil.getContext4ConnectionByConfig(), sb.toString());
	}
	
}
