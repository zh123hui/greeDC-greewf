package com.gree.mobile.wf.action;

import java.util.Locale;

import org.apache.log4j.Logger;

import com.gree.mobile.common.permission.UserContext;
import com.gree.mobile.common.permission.UserContextManager;
import com.gree.mobile.common.web.IpUtil;
import com.gree.mobile.common.web.action.JsonAction;
import com.gree.mobile.common.web.exception.JsonActionException;
import com.gree.mobile.wf.BussinessOrgSwitcher;
import com.gree.mobile.wf.config.EASLoginConfig;
import com.kingdee.bos.BOSException;
import com.kingdee.bos.Context;
import com.kingdee.bos.framework.session.BOSLoginException;
import com.kingdee.bos.framework.session.LoginContext;
import com.kingdee.bos.framework.session.LoginModuleFactory;
import com.kingdee.bos.sql.DbType;
import com.kingdee.bos.util.CryptException;
import com.kingdee.bos.util.CryptoTean;
import com.kingdee.eas.cp.common.web.util.WebContextUtil;

public class LoginAction extends JsonAction{

    private static final Logger logger = Logger.getLogger(LoginAction.class);
	private String userName; 		//用户名
	private String password;		//密码,密码传输前要加密
	private String pushToken;		//设备推送令牌

	@Override
	public Object doExecute() throws Exception {
		Context bosCtx=null;
		try{
			bosCtx = doEASLogin(userName, password);
		}catch (BOSLoginException e) {
			throw new JsonActionException(String.format("用户名[%s]或密码不正确.", userName), e);
		} catch (BOSException e) {
			throw new JsonActionException("BOS登录异常", e);
		} catch (CryptException e) {
			throw new JsonActionException("CryptException", e);
		} catch (JsonActionException e) {
			throw e;
		} catch (Exception e) {
			throw new JsonActionException("登录异常", e);
		}
		
		UserContext uc = new UserContext();
		uc.setBosContext(bosCtx);
		uc.setUserName(userName);
//		uc.setPushToken(pushToken);
		UserContextManager.setUserContext(getSession(), uc);
		
		return null;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPushToken(String pushToken) {
		this.pushToken = pushToken;
	}

	private Context doEASLogin(String userName, String password) 
			throws CryptException, BOSException, BOSLoginException, Exception {
		EASLoginConfig cfg = EASLoginConfig.getDefault();
		int dbType = cfg.getDbType();
		String localeStr = cfg.getLocale();
		String authType = cfg.getAuthType();
		logger.info(String.format("准备登录,认证类型为[%s]", authType));
		String slnName = cfg.getSlnName();
		String dbCode = cfg.getDbCode();
		String encodePwd = CryptoTean.encrypt(userName, password);
		Locale locale = new Locale(localeStr);
		String clientIP = IpUtil.getClientIP(getRequest());
		LoginContext loginCtx = new LoginContext(userName, encodePwd, slnName, dbCode, locale, clientIP, clientIP);
		loginCtx.put("UserAuthPattern", authType);
		loginCtx.put("dbTypeCode", dbType);
		loginCtx.put("dbType", DbType.getName(dbType));
		loginCtx.put("ClientIP", clientIP);
		Context context = new Context(null, loginCtx.getSlnName(), loginCtx.getAis(), loginCtx.getLocale(), loginCtx.getContextID());
		loginCtx.setContextID(context.getContextID());
		WebContextUtil.initRpcConfig(context);
		context = LoginModuleFactory.getRemoteInstance().fullLogin(loginCtx);
		BussinessOrgSwitcher.switchForLogin(context);
		WebContextUtil.initRpcConfig(context);
		return context;
	}
	
}
