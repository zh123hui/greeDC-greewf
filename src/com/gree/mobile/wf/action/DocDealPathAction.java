package com.gree.mobile.wf.action;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.gree.mobile.common.web.action.JsonAction;
import com.gree.mobile.common.web.exception.JsonActionException;
import com.gree.mobile.wf.enums.TaskTypeEnum;
import com.gree.mobile.wf.vo.DealRecordVO;
import com.kingdee.bos.BOSException;
import com.kingdee.bos.dao.query.IQueryExecutor;
import com.kingdee.bos.dao.query.QueryExecutorFactory;
import com.kingdee.bos.metadata.IMetaDataPK;
import com.kingdee.bos.metadata.MetaDataPK;
import com.kingdee.bos.util.BOSUuid;
import com.kingdee.jdbc.rowset.IRowSet;
import com.kingdee.util.DateTimeUtils;
import com.kingdee.util.db.SQLUtils;

public class DocDealPathAction extends JsonAction{

	private String docId;		//单据ID
	private int taskType;			//流程类型,1表示待办流程,2表示已办流程,3表示在办流程
	
	@Override
	public Object doExecute() throws Exception {
		if(StringUtils.isEmpty(docId)){
			throw new JsonActionException("单据ID为空");
		}
		if(TaskTypeEnum.getEnum(taskType)==null){
			throw new JsonActionException(String.format("taskType值[%s]不正确", taskType));
		}
		BOSUuid billId = BOSUuid.read(docId);
		String oql = "where MULTIAPPROVE.BILLID = '"+billId.toString()+"' order by MultiApprove.createTime desc";
		IRowSet rowset = null;
		try {
			IMetaDataPK queryPK = MetaDataPK.create("com.kingdee.eas.base.multiapprove.app.MultiApproveQuery");
			IQueryExecutor exec = QueryExecutorFactory.getRemoteInstance(queryPK);
			exec.setObjectView(oql);
			exec.option().isAutoTranslateEnum = true;
			exec.option().isIgnorePermissionCheck = false;
			rowset = exec.executeQuery();
		} catch (BOSException e1) {
			throw new JsonActionException("获取审批路线失败", e1);
		}
		if(rowset == null || rowset.size()==0){
			SQLUtils.cleanup(rowset);
			return Collections.emptyList();
		}
		List<DealRecordVO> list = new ArrayList<DealRecordVO>();
		try {
			HashSet<String> set = new HashSet<String>();
			while(rowset.next()){
				String actDefName=StringUtils.defaultString(rowset.getString("AssignDetail.actdefName"));
				if(StringUtils.isEmpty(actDefName)){
					continue;
				}
				String createTime=StringUtils.defaultString(rowset.getString("MultiApprove.createTime"));
				if(set.add(actDefName+createTime)){
					DealRecordVO vo = new DealRecordVO();
					vo.setActdefName(actDefName);
					vo.setResult(StringUtils.defaultString(rowset.getString("MultiApprove.isPass")));
					vo.setOptValue(StringUtils.defaultString(rowset.getString("MultiApprove.handlerOpinion")));
					vo.setOpinion(StringUtils.defaultString(rowset.getString("MultiApprove.opinion")));
					vo.setApprover(StringUtils.defaultString(rowset.getString("personId.name")));
					Timestamp time = rowset.getTimestamp("MultiApprove.createTime");
					if (time != null){
						createTime = DateTimeUtils.format(new Date(time.getTime()));
					}
					vo.setApproveTime(createTime);
					list.add(vo);
				}
			}
		} catch (SQLException e) {
			throw new JsonActionException("获取审批路线失败", e);
		}finally{
			SQLUtils.cleanup(rowset);
		}

		return list;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public void setTaskType(int taskType) {
		this.taskType = taskType;
	}

}
