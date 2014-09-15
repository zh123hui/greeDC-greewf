package com.gree.mobile.wf.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.CannotGetJdbcConnectionException;

import com.kingdee.util.db.SQLUtils;

public class EasDataSourceUtils {
	
	private static final ThreadLocal<Connection> conn = new ThreadLocal<Connection>();
	
	public static Connection getConnection(DataSource dataSource) throws CannotGetJdbcConnectionException {
		try {
			Connection connection = conn.get();
			if(connection!=null && !connection.isClosed() && connection.isValid(100)){
				return connection;
			}
			connection = dataSource.getConnection();
			conn.set(connection);
			return connection;
		}
		catch (SQLException ex) {
			throw new CannotGetJdbcConnectionException("Could not get JDBC Connection", ex);
		}
	}
	
	public static void releaseConnection() {
		Connection connection = conn.get();
		SQLUtils.cleanup(connection);
	}
}
