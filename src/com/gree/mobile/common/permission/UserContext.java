package com.gree.mobile.common.permission;

import com.kingdee.bos.Context;
import com.kingdee.eas.base.permission.UserInfo;
import com.kingdee.eas.basedata.person.PersonInfo;
import com.kingdee.eas.util.app.ContextUtil;

public class UserContext implements java.io.Serializable {

	private static final long serialVersionUID = 2867064288450092545L;
	private String userName;		//登录帐号
	private String pushToken;
	private Context bosContext;
	
	public String getUserId(){
		UserInfo userInfo = getUserInfo();
		if(userInfo == null){
			return null;
		}
		return userInfo.getId().toString();
	}
	public String getPersonId(){
		PersonInfo personInfo = getPersonInfo();
		if(personInfo == null){
			return null;
		}
		return personInfo.getId().toString();
	}
	
	public PersonInfo getPersonInfo(){
		UserInfo userInfo = getUserInfo();
		if(userInfo == null){
			return null;
		}
		return userInfo.getPerson();
	}
	
	public UserInfo getUserInfo(){
		return ContextUtil.getCurrentUserInfo(bosContext);
	}
	
	public String getSysUserName(){
		return this.bosContext.getUserName();
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public void setBosContext(Context bosContext) {
		this.bosContext = bosContext;
	}
	public Context getBosContext() {
		return bosContext;
	}
	public String getPushToken() {
		return pushToken;
	}
	public void setPushToken(String pushToken) {
		this.pushToken = pushToken;
	}
	
}
