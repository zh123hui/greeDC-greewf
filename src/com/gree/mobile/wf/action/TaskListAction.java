package com.gree.mobile.wf.action;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.gree.mobile.common.permission.UserContext;
import com.gree.mobile.common.permission.UserContextManager;
import com.gree.mobile.common.web.action.JsonAction;
import com.gree.mobile.common.web.exception.JsonActionException;
import com.gree.mobile.wf.BillNameManager;
import com.gree.mobile.wf.category.CategoryKey;
import com.gree.mobile.wf.category.TaskListManager;
import com.gree.mobile.wf.jdbc.EasDbUtil;
import com.gree.mobile.wf.vo.TaskCategoryVO;
import com.gree.mobile.wf.vo.TaskCategoryVO.TaskItem;
import com.kingdee.bos.dao.query.IQueryExecutor;
import com.kingdee.bos.dao.query.QueryExecutorFactory;
import com.kingdee.bos.metadata.IMetaDataPK;
import com.kingdee.bos.metadata.MetaDataPK;
import com.kingdee.bos.metadata.data.SortType;
import com.kingdee.bos.metadata.entity.EntityViewInfo;
import com.kingdee.bos.metadata.entity.FilterInfo;
import com.kingdee.bos.metadata.entity.FilterItemInfo;
import com.kingdee.bos.metadata.entity.SelectorItemCollection;
import com.kingdee.bos.metadata.entity.SelectorItemInfo;
import com.kingdee.bos.metadata.entity.SorterItemInfo;
import com.kingdee.bos.metadata.query.util.CompareType;
import com.kingdee.jdbc.rowset.IRowSet;
import com.kingdee.util.db.SQLUtils;

public class TaskListAction extends JsonAction{

	private static final Logger logger =Logger.getLogger(TaskListAction.class);
	private static final CategoryKey OTHER=CategoryKey.newInstance("其他", Integer.MAX_VALUE);//其他分类
	@Override
	public Object doExecute()  throws Exception{
		UserContext userContext = UserContextManager.getUserContext(getSession());
		int max = 100;
		String userid = userContext.getUserId();
		IRowSet rowset = null;
		try {
			rowset = getAcceptedTask(userid, 1, max+150);
		} catch (Exception e) {
			throw new JsonActionException("获取待办工作流失败.", e); 
		}
		int size = rowset.size();
		if(size == 0){
			SQLUtils.cleanup(rowset);
			return java.util.Collections.emptyList();
		}
		try{
			// 创建service
//			IEnactmentService wfService = EnactmentServiceFactory.createRemoteEnactService();
			Map<CategoryKey, TaskCategoryVO> categoryMap=new HashMap<CategoryKey, TaskCategoryVO>();//key为分类名,value为待办列表
			
			SimpleDateFormat sdf=new SimpleDateFormat("MM-dd HH:mm");
			int counter = 0;
//			Map<String, String> billNameMap = new HashMap<String, String>();
			rowset.first();
			Map<String, String> parentprocdefids = getParentProcDefid(rowset);
			TaskListManager manager = TaskListManager.getManager();
			do {
				TaskItem item;
				CategoryKey categoryKey = null;
				try{	//增加catch，避免单条流程失败，导致整个列表不返回
					String procDefId = StringUtils.defaultString(rowset.getString("procdefID"));
					String parentProcDefId = parentprocdefids.get(StringUtils.defaultString(rowset.getString("procInstID")));
//					//排除这个流程
					if(manager.excludeProccess(procDefId) || manager.excludeProccess(parentProcDefId)){
						continue;
					}
					String taskId = StringUtils.defaultString(rowset.getString("AssignRead.assignID"));
					String initiatorName = StringUtils.defaultString(rowset.getString("sender"));
					String bizFunction = StringUtils.defaultString(rowset.getString("bizFunction"));
					
					if (!"MultiApproveUIFunction".equals(bizFunction) && !"ManualDecisionUIFunction".equals(bizFunction)) {
						continue;
					}
					
//					String actDefId = StringUtils.defaultString(rowset.getString("actdefID"));
					
					item = new TaskItem();
					item.setTaskId(taskId);
					item.setMsgId(StringUtils.defaultString(rowset.getString("id")));
					item.setTitle(StringUtils.defaultString(rowset.getString("title")));
					item.setSubmittor(initiatorName);
					String docId = StringUtils.defaultString(rowset.getString("bizObjID"));
					item.setDocTypeName(BillNameManager.getBillName(docId));
					item.setDocId(docId);
					Timestamp rTime = rowset.getTimestamp("receiveTime");
					if(rTime != null){
						item.setReceiveTime(sdf.format(rTime));
					}
					// 状态：0代表未读，10代表已读
					item.setHasRead(StringUtils.defaultString(rowset.getString("status")).equals("10"));
					item.setSeq(counter++);
					
					//处理分类
					//根据流程定义ID找分类
					categoryKey=manager.findCategoryKey(procDefId);
					if(categoryKey==null){
						//如果是子流程，根据他父流程的定义ID找分类
						categoryKey=manager.findCategoryKey(parentProcDefId);
					}
					
				}catch(Exception e){
					logger.error("获取流程失败",e);
					continue;
				}
				
				if(categoryKey==null){
					categoryKey=OTHER;
				}
				TaskCategoryVO categoryVO = categoryMap.get(categoryKey);
				if(categoryVO == null){
					categoryVO = new TaskCategoryVO();
					categoryVO.setCategory(categoryKey.getName());
					categoryMap.put(categoryKey, categoryVO);
				}
				categoryVO.getList().add(item);
				
				if(counter>=max){
					break;
				}
			} while (rowset.next());
			//将分类排好
			return manager.getSortList(categoryMap);
		} catch (Exception e) {
			throw new JsonActionException("获取待办工作流失败.", e);
		}finally{
			SQLUtils.cleanup(rowset);
		}
	}
	
	
	private IRowSet getAcceptedTask(String userid, int pageNumber, int pagesize) throws Exception {
		IMetaDataPK mainQueryPK = new MetaDataPK("com.kingdee.eas.base.message", "WFAssignQuery");
		EntityViewInfo entityViewInfo = new EntityViewInfo();
		SelectorItemCollection sic = new SelectorItemCollection();
		sic.add(new SelectorItemInfo("id"));
		sic.add(new SelectorItemInfo("AssignRead.assignID"));
		sic.add(new SelectorItemInfo("sender"));
		sic.add(new SelectorItemInfo("title"));
		sic.add(new SelectorItemInfo("receiveTime"));
		sic.add(new SelectorItemInfo("status"));
		sic.add(new SelectorItemInfo("procdefID"));
		sic.add(new SelectorItemInfo("procInstID"));
		sic.add(new SelectorItemInfo("bizPackage"));
		sic.add(new SelectorItemInfo("bizFunction"));
		sic.add(new SelectorItemInfo("bizOperation"));
		sic.add(new SelectorItemInfo("bizObjID"));
		sic.add(new SelectorItemInfo("actdefID"));
		
		FilterInfo filterInfo = new FilterInfo();
		FilterItemInfo filterItemInfo = new FilterItemInfo("type", new Integer(10));
		filterInfo.getFilterItems().add(filterItemInfo);

		filterItemInfo = new FilterItemInfo("sourceStatus", new Integer(1));
		filterInfo.getFilterItems().add(filterItemInfo);

		filterItemInfo = new FilterItemInfo("sourceStatus", new Integer(2));
		filterInfo.getFilterItems().add(filterItemInfo);

		filterItemInfo = new FilterItemInfo("receiver", userid, CompareType.EQUALS);
		filterInfo.getFilterItems().add(filterItemInfo);

//		filterItemInfo = new FilterItemInfo("bizPackage", "com.kingdee.eas.base.multiapprove.client", CompareType.EQUALS);
//		filterInfo.getFilterItems().add(filterItemInfo);

		filterItemInfo = new FilterItemInfo("bizFunction", "MultiApproveUIFunction", CompareType.EQUALS);
		filterInfo.getFilterItems().add(filterItemInfo);

		filterItemInfo = new FilterItemInfo("bizFunction", "ManualDecisionUIFunction", CompareType.EQUALS);
		filterInfo.getFilterItems().add(filterItemInfo);

//		filterInfo.setMaskString(" #0 AND ( #1 OR #2 ) AND #3 AND #4 AND ( #5 OR #6 ) ");
		filterInfo.setMaskString(" #0 AND ( #1 OR #2 ) AND #3 AND ( #4 OR #5 ) ");

		SorterItemInfo sorterItemInfo = null;
		sorterItemInfo = new SorterItemInfo("receiveTime");
		sorterItemInfo.setSortType(SortType.DESCEND);
		entityViewInfo.getSorter().add(sorterItemInfo);

		entityViewInfo.setSelector(sic);
		entityViewInfo.setFilter(filterInfo);
		
		IQueryExecutor exec = QueryExecutorFactory.getRemoteInstance(mainQueryPK);
		exec.setObjectView(entityViewInfo);
		IRowSet rowSet = exec.executeQuery((pageNumber - 1) * pagesize, pagesize);
		return rowSet;
	}
	
	private Map<String,String> getParentProcDefid(IRowSet rowSet) {
		Map<String,String> map = new HashMap<String,String>();
		IRowSet subRowSet=null;
		try {
			Object[] params = new Object[rowSet.size()];
			StringBuffer sql = new StringBuffer("select FPROCINSTID,FPARENTPROCDEFID from T_WFR_ProcInst where FPROCINSTID in (");
			int ii=0;
			do {
				if (ii==0) {
					sql.append("?");
				} else {
					sql.append(",?");
				}
				params[ii] = StringUtils.defaultString(rowSet.getString("procInstID"));
				ii++;
			}while (rowSet.next());
			
			sql.append(")");
			UserContext uc = UserContextManager.getUserContext(getSession());
			subRowSet = EasDbUtil.executeQuery(uc.getBosContext(), sql.toString(), params);
			while(subRowSet.next()){
				String procinstid = subRowSet.getString("FPROCINSTID");
				String parentprocdefid = StringUtils.defaultString(subRowSet.getString("FPARENTPROCDEFID"));
				map.put(procinstid, parentprocdefid);
			}
		} catch (Exception e) {
			logger.error("获取父流程定义失败",e);
		} finally {
			SQLUtils.cleanup(subRowSet);
			try {
				rowSet.first();
			} catch (SQLException e) {
				logger.error("rowSet.first()",e);
			}
		}
		return map;
	}

}
