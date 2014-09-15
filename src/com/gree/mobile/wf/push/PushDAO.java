package com.gree.mobile.wf.push;

import java.sql.Timestamp;

import com.gree.mobile.wf.jdbc.EasDbUtil;
import com.kingdee.bos.BOSException;
import com.kingdee.bos.Context;

public class PushDAO {

	public static void saveOrUpdate(Context ctx, String pushToken, String userId) throws BOSException {
		String sql = "UPDATE T_GMOBILE_PUSH SET FLastUserID=? , FLastUpdateTime=? WHERE FPushToken=?";
		Timestamp now = new Timestamp(System.currentTimeMillis());
		int update = EasDbUtil.update(ctx, sql, new Object[]{userId, now, pushToken});
		if(update==0){
			sql = "INSERT INTO T_GMOBILE_PUSH (FPushToken, FLastUserID, FLastUpdateTime, FCreateTime) VALUES (?,?,?,?)";
			EasDbUtil.update(ctx, sql, new Object[]{pushToken, userId, now, now});
		}
	}
	
	public static void delete(Context ctx, String pushToken, String userId) throws BOSException {
		String sql = "DELETE FROM T_GMOBILE_PUSH WHERE FPushToken=? AND FLastUserID=?";
		EasDbUtil.update(ctx, sql, new Object[]{pushToken, userId});
	}
	
}
