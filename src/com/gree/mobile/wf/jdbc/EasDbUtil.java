package com.gree.mobile.wf.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.gree.mobile.wf.config.EASLoginConfig;
import com.kingdee.bos.BOSException;
import com.kingdee.bos.Context;
import com.kingdee.bos.dao.ormapping.ObjectUuidPK;
import com.kingdee.bos.framework.ejb.EJBFactory;
import com.kingdee.eas.util.app.DbUtil;
import com.kingdee.jdbc.rowset.IRowSet;
import com.kingdee.jdbc.rowset.impl.JdbcRowSet;
import com.kingdee.util.db.SQLUtils;

public class EasDbUtil {
	private static Logger logger = Logger.getLogger(DbUtil.class);
	
	public static Context getContext4ConnectionByConfig(){
		String slnName = EASLoginConfig.getDefault().getSlnName();
		String dbCode = EASLoginConfig.getDefault().getDbCode();
		Locale locale = new Locale(EASLoginConfig.getDefault().getLocale());
		return new Context(new ObjectUuidPK(), slnName, dbCode, locale);
	}
	
	public static IRowSet executeQuery(Context ctx, String sql)
			throws BOSException {
		return DbUtil.executeQuery(ctx, sql);
	}

	public static IRowSet executeQuery(Context ctx, String sql, Object[] params)
			throws BOSException {
		return DbUtil.executeQuery(ctx, sql, params);
	}

	public static IRowSet executeQuery(Context ctx, String sql, int start,
			int rows) throws BOSException {
		return DbUtil.executeQuery(ctx, sql, start, rows);
	}

	public static void execute(Context ctx, String sql, Object[] params)
			throws BOSException {
		DbUtil.execute(ctx, sql, params);
	}
	public static void execute(Context ctx, String sql)
			throws BOSException {
		DbUtil.execute(ctx, sql);
	}
	public static int update(Context ctx, String sql, Object[] params)
			throws BOSException {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			conn = EJBFactory.getConnection(ctx);
		} catch (SQLException exc) {
			SQLUtils.cleanup(conn);
			throw new BOSException(DbUtil.CONFIG_EXCEPTION, exc);
		}

		try {
			ps = conn.prepareStatement(sql);
			if (params != null) {
				for (int i = 0; i < params.length; ++i) {
					ps.setObject(i + 1, params[i]);
				}
			}
			return ps.executeUpdate();
		} catch (SQLException exc) {
			StringBuffer sb = new StringBuffer("");
			if (params != null) {
				for (int i = 0; i < params.length; ++i) {
					sb.append("param ").append(i).append(" is:").append(
							params[i]);
				}
			}
			logger.error("sql is:" + sql + " param is:" + sb.toString());

			throw new BOSException("Sql execute exception : " + sql, exc);
		} finally {
			SQLUtils.cleanup(rs, ps, conn);
		}
	}

	public static int count(Context ctx, String sql, Object[] params)
			throws BOSException, SQLException {
		IRowSet countRowset=null;
		int count=-1;
		try{
			countRowset = executeQuery(ctx, sql, params);
			if(countRowset.size()==1){
				countRowset.next();
				count = countRowset.getInt(1);	
			}
		}finally {
			SQLUtils.cleanup(countRowset);
		}
		return count;
	}

	public static IRowSet executeQuery(Context ctx, String sql,
			Object[] params, int start, int rows) throws BOSException {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			conn = EJBFactory.getConnection(ctx);
		} catch (SQLException exc) {
			SQLUtils.cleanup(conn);
			throw new BOSException(DbUtil.CONFIG_EXCEPTION, exc);
		}

		try {
			ps = conn.prepareStatement(sql);
			if (params != null) {
				for (int i = 0; i < params.length; ++i) {
					ps.setObject(i + 1, params[i]);
				}
			}
			rs = ps.executeQuery();
			
			JdbcRowSet rowset = new JdbcRowSet();
			rowset.populate(rs, start, rows, true);
			JdbcRowSet localJdbcRowSet1 = rowset;
			return localJdbcRowSet1;
		} catch (SQLException exc) {
			StringBuffer sb = new StringBuffer("");
			if (params != null) {
				for (int i = 0; i < params.length; ++i) {
					sb.append("param ").append(i).append(" is:").append(
							params[i]);
				}
			}
			logger.error("sql is:" + sql + " param is:" + sb.toString());

			throw new BOSException("Sql execute exception : " + sql, exc);
		} finally {
			SQLUtils.cleanup(rs, ps, conn);
		}
	}
}
