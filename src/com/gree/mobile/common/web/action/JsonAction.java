package com.gree.mobile.common.web.action;

import org.apache.log4j.Logger;
import org.apache.struts2.json.JSONUtil;

import com.gree.mobile.common.web.exception.IllegalParameterException;
import com.gree.mobile.common.web.exception.JsonActionException;


public abstract class JsonAction extends JsonActionSupport {

	public abstract Object doExecute() throws Exception;
	
	@Override
	public final String execute() {
		Logger logger = Logger.getLogger(this.getClass());
		try{
			this.success(doExecute());
		} catch (Throwable e) {
			String msg = e.getMessage();
			logger.error(msg, e);
			if(e instanceof NullPointerException){
				this.error("空指针错误");
			}else if(e instanceof JsonActionException){
				this.error(msg, ((JsonActionException)e).getResult());
			} else if(e instanceof Exception){
				this.error(msg);
			}else{
				this.error("服务器错误: "+msg);
			}
		}
		if(logger.isDebugEnabled()){
			try{
				logger.debug("respone : "+JSONUtil.serialize(getCommonResult()));
			}catch (Throwable t) {
			}
		}
		return JSON;
	}

}
