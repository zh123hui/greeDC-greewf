package com.gree.mobile.wf.config;



public class EASLoginConfig {

	private GreeWfCofingCenter cfg;
	private String name;
	
	private static EASLoginConfig one = new EASLoginConfig(null);
	
	public static EASLoginConfig getDefault(){
		return one;
	}

	private EASLoginConfig(String name) {
		this.name = name;
		cfg = GreeWfCofingCenter.getInstance();
	}
	/**
	 * 解决方案名,默认为eas
	 * @return
	 */
	public String getSlnName() {
		return cfg.getConfiguration().getString("gree.server.slnName", "eas");
	}

	public String getDbCode() {
		return cfg.getString("gree.database.dbCode", true);
	}

	/**
	 * 语言,L2为简体中文
	 * @return
	 */
	public String getLocale() {
		return cfg.getConfiguration().getString("gree.server.locale", "L2");
	}

	public int getDbType() {
		String type = cfg.getString("gree.database.dbType", true);
		try{
			return Integer.parseInt(type);
		}catch (Exception e) {
			throw new GreeConfigException(String.format("配置文件[%s]配置项[gree.database.dbType]值不正确", cfg.getFileName()));
		}
	}

	/**
	 * 认证类型
	 * @return
	 */
	public String getAuthType() {
		return cfg.getConfiguration().getString("gree.login.authType", "");
	}
}
