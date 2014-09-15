package com.gree.mobile.wf.action;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.gree.mobile.common.permission.UserContext;
import com.gree.mobile.common.permission.UserContextManager;
import com.gree.mobile.common.web.action.JsonAction;
import com.gree.mobile.wf.BillNameManager;
import com.gree.mobile.wf.jdbc.EasDbUtil;
import com.gree.mobile.wf.vo.MyProcListVO;
import com.kingdee.jdbc.rowset.IRowSet;

public class MyProcListAction extends JsonAction{

	@Override
	public Object doExecute()  throws Exception{
		List<MyProcListVO> list = new ArrayList<MyProcListVO>();
		
		UserContext uc = UserContextManager.getUserContext(getSession());
		String userId = uc.getUserId();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PROCINST.FPROCINSTID AS FPROCINSTID, ");
		sql.append(" PROCINST.FPROCDEFNAME_l2 AS FPROCDEFNAME, ");
		sql.append(" PROCINST.FCREATEDTIME AS FCREATEDTIME,");
		sql.append(" ASSIGN.FACTDEFNAME_L2 AS FACTDEFNAME,");
		sql.append(" ASSIGN.FSUBJECT_L2 AS FASSIGNSUBJECT,");
		sql.append(" ASSIGN.FBIZOBJID AS FBIZOBJID,");
		sql.append(" ASSIGN.FBIZFUNCTION AS FBIZFUNCTION,");
		sql.append(" ASSIGN.FBIZPACKAGE AS FBIZPACKAGE,");
		sql.append(" ASSIGN.FPERSONUSERNAME_L2 AS FPERSONUSERNAME,");
		sql.append(" ASSIGN.FPERSONEMPNAME_L2 AS FPERSONEMPNAME,");
		sql.append(" ASSIGN.FPERSONEMPID AS FPERSONEMPID,");
		sql.append(" ASSIGN.FPERSONUSERID AS FPERSONUSERID");
		sql.append(" FROM T_WFR_ProcInst AS PROCINST");
		sql.append(" INNER JOIN T_WFR_Assign AS ASSIGN ON ASSIGN.FPROCINSTID=PROCINST.FPROCINSTID ");
		sql.append(" WHERE (PROCINST.FPROCDEFTYPE = 'NORMAL' OR PROCINST.FPROCDEFTYPE = 'MICRO') AND ");
		sql.append(" PROCINST.FSTATE LIKE 'open%' ");
		sql.append(" AND PROCINST.FINITIATORID = ? ");
		sql.append(" ORDER BY FCREATEDTIME ASC");
		// fprocinsttopic_l2 流程实例主题
		Object[] params = new Object[]{userId};
		IRowSet executeQuery = EasDbUtil.executeQuery(uc.getBosContext(), sql.toString(), params);
		if(executeQuery == null || executeQuery.size()==0){
			return Collections.emptyList();
		}
		Map<String, MyProcListVO> cachemap = new HashMap<String, MyProcListVO>();
		Map<String, Integer> empnamecachemap = new HashMap<String, Integer>();
		Integer in = new Integer(1);
		SimpleDateFormat sdf=new SimpleDateFormat("MM-dd HH:mm");
		while(executeQuery.next()){
			String procInstId = StringUtils.defaultString(executeQuery.getString("FPROCINSTID"));
			MyProcListVO vo = cachemap.get(procInstId);
			String billId = StringUtils.defaultString(executeQuery.getString("FBIZOBJID"));
			String billName = BillNameManager.getBillName(billId);
			if (vo==null) {
				vo = new MyProcListVO();
				vo.setProcInstId(procInstId);
				Timestamp createdTime = executeQuery.getTimestamp("FCREATEDTIME");
				if(createdTime!=null){
					vo.setSubmitTime(sdf.format(createdTime));
				}else{
					vo.setSubmitTime("");
				}
				vo.setProcDefName(StringUtils.defaultString(executeQuery.getString("FPROCDEFNAME")));// 流程名称
				vo.setActDefName(StringUtils.defaultString(executeQuery.getString("FACTDEFNAME")));
				vo.setTitle(StringUtils.defaultString(executeQuery.getString("FASSIGNSUBJECT")));
				
				vo.setDocTypeName(billName);
				vo.setDocId(billId);
				String eukey = procInstId+"_"+executeQuery.getString("FPERSONEMPID");
				empnamecachemap.put(eukey, in);
				vo.setHandler(StringUtils.defaultString(executeQuery.getString("FPERSONEMPNAME")));
				list.add(vo);
				cachemap.put(procInstId, vo);
				continue;
			} else if ("com.kingdee.eas.base.multiapprove.client".equals(executeQuery.getString("FBIZPACKAGE"))&&
					"MultiApproveUIFunction".equals(executeQuery.getString("FBIZFUNCTION"))) {
				vo.setActDefName(StringUtils.defaultString(executeQuery.getString("FACTDEFNAME")));
				vo.setTitle(StringUtils.defaultString(executeQuery.getString("FASSIGNSUBJECT")));
				vo.setDocTypeName(billName);
				vo.setDocId(billId);
			}
			String eukey = procInstId+"_"+executeQuery.getString("FPERSONEMPID");
			if(empnamecachemap.get(eukey)!=null){
				continue;
			}
			empnamecachemap.put(eukey, in);
			String s = vo.getHandler();
			vo.setHandler(s+","+StringUtils.defaultString(executeQuery.getString("FPERSONEMPNAME")));
		}
		return list;
	}

}
