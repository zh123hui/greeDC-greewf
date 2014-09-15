package com.gree.mobile.wf.action;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.gree.mobile.common.permission.UserContext;
import com.gree.mobile.common.permission.UserContextManager;
import com.gree.mobile.common.web.action.JsonAction;
import com.gree.mobile.common.web.exception.JsonActionException;
import com.gree.mobile.wf.BillNameManager;
import com.gree.mobile.wf.jdbc.EasDbUtil;
import com.gree.mobile.wf.vo.DealtTaskItem;
import com.kingdee.bos.Context;
import com.kingdee.eas.base.permission.UserInfo;
import com.kingdee.jdbc.rowset.IRowSet;
import com.kingdee.util.db.SQLUtils;

public class DealtTaskListAction extends JsonAction{
	private static final Logger logger =Logger.getLogger(DealtTaskListAction.class);
	private int from=1;
	private int to=-1;
	@Override
	public Object doExecute()  throws Exception{
		
		int max = 30;
		int num = 0;
		if(from<1){
			from=1;
		}
		if(to==-1){
			num = max;
		}else{
			num = to-from;
		}
		if(num<=0){
			num = max;
		}
		List<DealtTaskItem> list = new ArrayList<DealtTaskItem>();
		StringBuffer sql = new StringBuffer();
		sql.append("select a.FAssignId FAssignId,a.FSubject_L2 FTitle, a.FEndTime FEndTime,a.FState FState, a.FInitiatorName_L2 initiator, a.FBIZOBJID FBIZOBJID ,a.FActdefId FActdefId ,a.FPROCINSTID FPROCINSTID, a.FEndTime");
		sql.append(" from T_WFR_AssignDetail a ");
		sql.append(" where a.FPersonUserId=? ");
//		sql.append(" and a.FProcDefId not in (select FProcessId from t_3g_wf_excludedprocesses) ");
		sql.append(" and (a.FState=4 or a.FState=8 or a.FState=16) ");
		sql.append(" and a.FIsSendMsg = 1 ");
		sql.append(" order by a.FEndTime desc ");
		
		UserContext userContext = UserContextManager.getUserContext(getSession());
		UserInfo currentUserInfo = userContext.getUserInfo();
		Context ctx = userContext.getBosContext();
		String userId = currentUserInfo.getId().toString();
		IRowSet queryRowset = EasDbUtil.executeQuery(ctx, sql.toString(), new Object[]{userId}, (from-1), num);
		if(queryRowset==null || queryRowset.size()==0){
			SQLUtils.cleanup(queryRowset);
			setTotalCount(0);
			return Collections.emptyList();
		}
		SimpleDateFormat sdf=new SimpleDateFormat("MM-dd HH:mm");
		try{
			while(queryRowset.next()){
				DealtTaskItem itme=new DealtTaskItem();
				itme.setTaskId(StringUtils.defaultString(queryRowset.getString("FASSIGNID")));
				itme.setTitle(StringUtils.defaultString(queryRowset.getString("FTITLE")));
				itme.setSubmittor(StringUtils.defaultString(queryRowset.getString("INITIATOR")));
//				bean.setInitiatorId(getInitiatorId(StringUtils.defaultString(rowset.getString("FPROCINSTID"))));
				String billId = StringUtils.defaultString(queryRowset.getString("FBIZOBJID"));
				itme.setDocId(billId);
				itme.setDocTypeName(BillNameManager.getBillName(billId));
				Timestamp FEndTime = queryRowset.getTimestamp("FENDTIME");
				if(FEndTime != null){
					itme.setDealTime(sdf.format(FEndTime));
				}else{
					itme.setDealTime("");
				}
//				String actdefId = StringUtils.defaultString(queryRowset.getString("FACTDEFID"));
				int state = queryRowset.getInt("FSTATE");
				// 4:已取消,8:已拒绝,16:已完成
				itme.setState(state);
				list.add(itme);
			}
		}catch (Exception e) {
			throw new JsonActionException("查询已办列表失败", e);
		}finally{
			SQLUtils.cleanup(queryRowset);
		}
		int count = -1;
		try{
			sql = new StringBuffer();
			sql.append("select count(0)");
			sql.append(" from T_WFR_AssignDetail a ");
			sql.append(" where a.FPersonUserId=? ");
//			sql.append(" and a.FProcDefId not in (select FProcessId from t_3g_wf_excludedprocesses) ");
			sql.append(" and (a.FState=4 or a.FState=8 or a.FState=16) ");
			sql.append(" and a.FIsSendMsg = 1 ");
//			sql.append(" order by a.FEndTime desc ");
			count = EasDbUtil.count(ctx, sql.toString(), new Object[]{userId});
		}catch (Exception e) {
			logger.error("统计已办总数失败", e);
		}
		if(count!=-1){
			setTotalCount(count);
		}else{
			setTotalCount(list.size());
		}
		return list;
	}
//	private String getInitiatorId(String procInstID) {
//		CommonDao dao = new CommonDao();
//		Object[] params = new Object[1];
//		params[0] = procInstID;
//		String sql = "select FINITIATORID from T_WFR_ProcInst where FProcInstID=?";
//		try {
//			List<Map<String, Object>> rs = dao.query(sql, params);
//			for (Map<String, Object> c : rs) {
//				return String.valueOf(c.get("FINITIATORID"));
//			}
//		} catch (Exception e) {
//			logger.error("获取发起者id失败", e);
//		}
//		return "";
//	}
	public void setFrom(int from) {
		this.from = from;
	}
	public void setTo(int to) {
		this.to = to;
	}

}
