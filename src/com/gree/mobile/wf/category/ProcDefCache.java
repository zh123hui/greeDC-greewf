package com.gree.mobile.wf.category;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;

import com.gree.mobile.wf.jdbc.EasDbUtil;
import com.kingdee.bos.Context;
import com.kingdee.jdbc.rowset.IRowSet;
import com.kingdee.util.db.SQLUtils;

public class ProcDefCache {

	private static ProcDefCache instance = new ProcDefCache();
	private long lastUpdate;
	private long refreshDelay = 24*60*60*1000l;
	Map<String,String> cacheMap = new ConcurrentHashMap<String, String>();
	private static String querySQL = "select FPROCINSTID,FPARENTPROCDEFID from T_WFR_ProcInst where FPARENTPROCDEFID IS NOT NULL ";
	
	public static ProcDefCache getCache(){
		return instance;
	}
	
	public String findParentProcDefID(String procInstID){
//		load();
		return cacheMap.get(procInstID);
	}
	
	public void load(Context ctx) {
		long now = System.currentTimeMillis();
		if(now < (lastUpdate+refreshDelay)){
			return;
		}
		IRowSet rowSet = null;
		try{
			rowSet = EasDbUtil.executeQuery(ctx, querySQL);
			if(rowSet.size()==0){
				SQLUtils.cleanup(rowSet);
				return ;
			}
			while(rowSet.next()){
				String procinstid = rowSet.getString("FPROCINSTID");
				String parentprocdefid = StringUtils.defaultString(rowSet.getString("FPARENTPROCDEFID"));
				cacheMap.put(procinstid, parentprocdefid);
			}	
		}catch (Exception e) {
			// TODO: handle exception
		}finally{
			SQLUtils.cleanup(rowSet);
		}
		
	}
	
	
}
