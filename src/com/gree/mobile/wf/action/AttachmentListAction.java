package com.gree.mobile.wf.action;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.gree.mobile.common.permission.UserContext;
import com.gree.mobile.common.permission.UserContextManager;
import com.gree.mobile.common.web.action.JsonAction;
import com.gree.mobile.common.web.exception.JsonActionException;
import com.gree.mobile.wf.enums.AttachmentType;
import com.gree.mobile.wf.enums.TaskTypeEnum;
import com.gree.mobile.wf.jdbc.EasDbUtil;
import com.gree.mobile.wf.vo.AttachmentItem;
import com.kingdee.bos.BOSException;
import com.kingdee.bos.Context;
import com.kingdee.bos.dao.IObjectValue;
import com.kingdee.bos.dao.ormapping.ObjectUuidPK;
import com.kingdee.bos.framework.DynamicObjectFactory;
import com.kingdee.bos.framework.IDynamicObject;
import com.kingdee.bos.metadata.MetaDataLoaderFactory;
import com.kingdee.bos.metadata.data.SortType;
import com.kingdee.bos.metadata.entity.DataType;
import com.kingdee.bos.metadata.entity.EntityObjectInfo;
import com.kingdee.bos.metadata.entity.EntityViewInfo;
import com.kingdee.bos.metadata.entity.FilterInfo;
import com.kingdee.bos.metadata.entity.FilterItemInfo;
import com.kingdee.bos.metadata.entity.OwnPropertyInfo;
import com.kingdee.bos.metadata.entity.PropertyCollection;
import com.kingdee.bos.metadata.entity.PropertyInfo;
import com.kingdee.bos.metadata.entity.SelectorItemCollection;
import com.kingdee.bos.metadata.entity.SelectorItemInfo;
import com.kingdee.bos.metadata.entity.SorterItemCollection;
import com.kingdee.bos.metadata.entity.SorterItemInfo;
import com.kingdee.bos.metadata.query.util.CompareType;
import com.kingdee.bos.util.BOSObjectType;
import com.kingdee.bos.util.BOSUuid;
import com.kingdee.eas.base.attachment.AttachmentInfo;
import com.kingdee.eas.base.attachment.BoAttchAssoCollection;
import com.kingdee.eas.base.attachment.BoAttchAssoFactory;
import com.kingdee.eas.base.attachment.BoAttchAssoInfo;
import com.kingdee.eas.base.attachment.IBoAttchAsso;
import com.kingdee.eas.base.fme.service.FMEServiceFactory;
import com.kingdee.eas.base.fme.service.IFMEConsoleService;
import com.kingdee.eas.base.form.extend.attachment.AttachmentCollection;
import com.kingdee.eas.base.form.extend.attachment.AttachmentFactory;
import com.kingdee.eas.base.form.extend.attachment.IAttachment;
import com.kingdee.eas.common.EASBizException;
import com.kingdee.eas.cp.dm.ContentTypeEnum;
import com.kingdee.eas.cp.dm.DocumentFactory;
import com.kingdee.eas.cp.dm.DocumentInfo;
import com.kingdee.eas.cp.dm.IDocument;
import com.kingdee.eas.framework.CoreBaseInfo;
import com.kingdee.jdbc.rowset.IRowSet;
import com.kingdee.util.db.SQLUtils;

public class AttachmentListAction extends JsonAction {

	private static final Logger logger =Logger.getLogger(AttachmentListAction.class);
	
	private String docId;		//单据ID
	private String id;			//单据ID
	private int taskType;		//流程类型,1表示待办流程,2表示已办流程,3表示在办流程
	
	@Override
	public Object doExecute() throws Exception {
		if(StringUtils.isEmpty(docId)){
			throw new JsonActionException("docId不能为空");
		}
		if(TaskTypeEnum.getEnum(taskType)==null){
			throw new JsonActionException(String.format("taskType值[%s]不正确", taskType));
		}
		List<AttachmentItem> attachmentList = new ArrayList<AttachmentItem>();
		UserContext uc = UserContextManager.getUserContext(getSession());
		Context ctx = uc.getBosContext();
		//单据正文
		try {
			attach1(docId, attachmentList, ctx);
		} catch (Exception e) {
			logger.error("获取单据正文附件失败!", e);
		} 
		// eas基础的附件控件
		try{
			attach2(docId, attachmentList);
		}catch (Exception e) {
			logger.error("获取单据关联的附件失败!", e);
		}
		//FTP附件
		try {
			attach3(docId, attachmentList, ctx);
		} catch (Exception e) {
			logger.error("获取单据FTP附件失败!", e);
		}
		//OFFICE控件
		try {
			attach4(docId, attachmentList);
		} catch (Exception e) {
			logger.error("获取单据Office控件附件失败!", e);
		}
		return attachmentList;
	}

	private static final double M=1024*1024;
	private static final double K=1024;
	public static final String formatSize(int size){
		if(size/M>1){
			return new DecimalFormat("(#.#M)").format(size/M);
		}else if(size/K>1){
			return new DecimalFormat("(#K)").format(size/K);
		}else{
			return "("+size+")";
		}
	}
	private void attach1(String bizObjId, List<AttachmentItem> attachmentList,
			Context ctx) throws BOSException, SQLException, Exception {
		BOSUuid id = BOSUuid.read(bizObjId);
		BOSObjectType type = id.getType();
		EntityObjectInfo eo = MetaDataLoaderFactory.getRemoteMetaDataLoader().getEntity(type);
		String fullName = eo.getFullName();
		IDynamicObject dynamicObject = DynamicObjectFactory.getRemoteInstance();
		IObjectValue dataCol = dynamicObject.getValue(type, (new StringBuffer("where id = '")).append(
				id.toString()).append("'").toString());
		if (dataCol == null) {
			throw new JsonActionException("单据已被删除");
		}
		CoreBaseInfo baseInfo = (CoreBaseInfo) dataCol;
		PropertyCollection propertyCol = eo.getInheritedNoDuplicatedPropertiesRuntime();
		for (int i = 0; i < propertyCol.size(); i++) {
			PropertyInfo pinfo = propertyCol.get(i);
			if (!(pinfo instanceof OwnPropertyInfo)) {
				continue;
			}
			OwnPropertyInfo ownPropertyInfo = (OwnPropertyInfo) pinfo;
			DataType dataType = ownPropertyInfo.getDataType();
			if (!DataType.BYTEARRAY.equals(dataType)) {
				continue;
			}
			byte[] bytes = baseInfo.getBytes(pinfo.getName());
			if (bytes == null || bytes.length==0) {
				continue;
			}
			int size = bytes.length;
			AttachmentItem item = new AttachmentItem();
			item.setFkey(AttachmentType.fileType_content.getValue() + "!"+ baseInfo.getId().toString() + "!" + pinfo.getName());
			item.setFname(ownPropertyInfo.getAlias());
			String fileExt = getFileExt(pinfo.getName());
			if(fullName.equals("com.kingdee.eas.cp.ap.app.Document")){
				baseInfo.get("billTemplate.id");
				StringBuffer sql = new StringBuffer();
				sql.append(" SELECT t2.fdoctype FDOCTYPE FROM ");
				sql.append(" T_CP_Document t1, T_CP_BillTemplate t2 ");
				sql.append(" WHERE t1.FBillTemplateID= t2.fid and t1.fid=? ");
				IRowSet rowSet = EasDbUtil.executeQuery(ctx, sql.toString(), new Object[]{bizObjId});
				if(rowSet.size()>0){
					rowSet.first();
					String docType = rowSet.getString("FDOCTYPE");
					if("2".equals(docType) || "4".equals(docType)){
						logger.info("excel");
						fileExt = "xls";
					}else{
						fileExt = "doc";
					}
				}
				SQLUtils.cleanup(rowSet);
			}
			item.setFext(fileExt);
			item.setFlength(formatSize(size));
			attachmentList.add(item);
		}
	
	}
	private void attach4(String bizObjId, List<AttachmentItem> attachmentList)
			throws BOSException {
		AttachmentCollection attachmentCollection = getBillAttachmentCollection(bizObjId);
		if(attachmentCollection==null || attachmentCollection.size()==0){
			return ;
		}
		for (int i = 0; i < attachmentCollection.size(); i++) {
			com.kingdee.eas.base.form.extend.attachment.AttachmentInfo attachmentInfo = attachmentCollection.get(i);
			AttachmentItem item = new AttachmentItem();
			int size = attachmentInfo.getSizeInByte();
			item.setFlength(formatSize(size));
			item.setFname("office正文");
			item.setFext(formatString(attachmentInfo.getExtName()));
			item.setFkey(AttachmentType.fileType_office.getValue() + "!"+attachmentInfo.getId().toString());
			attachmentList.add(item);
		}
	}
	private void attach3(String bizObjId, List<AttachmentItem> attachmentList,
			Context ctx) throws BOSException {
		IFMEConsoleService consoleServiceInstance = FMEServiceFactory.getConsoleServiceInstance(ctx);
		List businessDocumentInfo = consoleServiceInstance.getBusinessDocumentInfo(bizObjId);
		if(businessDocumentInfo==null || businessDocumentInfo.size()==0){
			return;
		}
		for(Object o : businessDocumentInfo){
			if(o==null || !(o instanceof Map)){
				continue;
			}
			Map map = (Map) o;
			AttachmentItem item = new AttachmentItem();
			String docId = formatString(map.get("docId"));
			int size = Integer.parseInt(formatString(map.get("docSize")));
			item.setFlength(formatSize(size));
			String docName = formatString(map.get("docName"));
			int lastIndexOf = docName.lastIndexOf(".");
			if(lastIndexOf != -1){
				docName = docName.substring(0, lastIndexOf);
			}
			item.setFname(docName);
			item.setFext(formatString(map.get("extName")));
			item.setFkey(AttachmentType.fileType_ftp.getValue() + "!"+ bizObjId+"!"+docId);
			attachmentList.add(item);
		}
	}
	private void attach2(String bizObjId, List<AttachmentItem> attachmentList)
			throws BOSException, EASBizException {
		BoAttchAssoCollection attachCol = getAttacheCollectionByBizID(bizObjId);
		if (attachCol == null || attachCol.size() == 0) {
			return;
		}
		BoAttchAssoInfo info = null;
		for (int i = 0; i < attachCol.size(); i++) {
			info = attachCol.get(i);
			if (info == null) {
				continue;
			}
			AttachmentItem item = new AttachmentItem();
			AttachmentInfo attachment = info.getAttachment();
			int size=attachment.getSizeInByte();
			item.setFlength(formatSize(size));
			String fileExt = attachment.getSimpleName();
			if ("b".equals(fileExt) || fileExt.startsWith("DMdocBody_word")) {
				item.setFname("正文");
				fileExt = findFileExt(bizObjId);
			}  else {
				item.setFname(attachment.getName());
			}
			if(fileExt==null){
				fileExt="";
			}
			item.setFext(fileExt);
			item.setFkey(AttachmentType.fileType_easbase.getValue() + "!"+ attachment.getId().toString()+"!"+info.getBoID());
			attachmentList.add(item);
		}
	
	}
	private String formatString(Object obj){
		return obj==null ? "" : obj.toString();
	}
	private String findFileExt(String biz) throws BOSException,
			EASBizException {
		String fileExt = null;
		try{
			ObjectUuidPK objectUuidPK = new ObjectUuidPK(biz);
			IDocument remoteInstance = DocumentFactory.getRemoteInstance();
			DocumentInfo documentInfo = remoteInstance.getDocumentInfo(objectUuidPK);
			ContentTypeEnum contentType = documentInfo.getContentType();
			if(contentType == ContentTypeEnum.HTML){
				fileExt = "html";
			}
			if(contentType == ContentTypeEnum.WORD){
				fileExt = "doc";
			}
			if(contentType == ContentTypeEnum.EXCEL){
				fileExt = "xls";
			}
			if(contentType == ContentTypeEnum.PDF){
				fileExt = "pdf";
			}
		}catch (Exception e) {
		}
		
		if(fileExt == null){
			fileExt = "html";
		}
		return fileExt;
	}
	
	private AttachmentCollection getBillAttachmentCollection(String billId)
			throws BOSException {
		EntityViewInfo evi = new EntityViewInfo();
		FilterInfo filter = new FilterInfo();
		filter.getFilterItems().add(new FilterItemInfo("billId", billId, CompareType.EQUALS));
		evi.setFilter(filter);
		SelectorItemCollection selector = new SelectorItemCollection();
		selector.add(new SelectorItemInfo("name"));
		selector.add(new SelectorItemInfo("extName"));
		selector.add(new SelectorItemInfo("sizeInByte"));
		evi.setSelector(selector);
		AttachmentCollection attachmentCollection = null;
		try{
			IAttachment facade = AttachmentFactory.getRemoteInstance();
			attachmentCollection = facade.getAttachmentCollection(evi);
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return attachmentCollection;
	}
	
	
	private String getFileExt(String arg){
		String fileExt = "";
		if(arg.endsWith("Entity")){
			fileExt = arg.replace("Entity", "");
		}
		return fileExt;
	}
	
	private BoAttchAssoCollection getAttacheCollectionByBizID(String seleID) {
        IBoAttchAsso boAtt = null;
        BoAttchAssoCollection cols = null;
        EntityViewInfo ev = new EntityViewInfo();
        FilterInfo filter = new FilterInfo();
        filter.getFilterItems().add(new FilterItemInfo("boID", seleID, CompareType.EQUALS));

        filter.setMaskString(" #0 ");
        ev.setFilter(filter);

		SelectorItemCollection sic = new SelectorItemCollection();
		sic.add(new SelectorItemInfo("*"));
		sic.add(new SelectorItemInfo("attachment.id"));
		sic.add(new SelectorItemInfo("attachment.simpleName")); //文件后缀
		sic.add(new SelectorItemInfo("attachment.name")); //文件名称
		sic.add(new SelectorItemInfo("attachment.sizeInByte")); //文件大小
		ev.setSelector(sic);
		SorterItemCollection coll = new SorterItemCollection();
		SorterItemInfo sort = new SorterItemInfo("attachment.createTime");
        sort.setSortType(SortType.DESCEND);
        coll.add(sort);
		ev.setSorter(coll);
        try {
            boAtt = BoAttchAssoFactory.getRemoteInstance();
            cols = boAtt.getBoAttchAssoCollection(ev);
        } catch (BOSException e) {
            logger.error(e.getMessage(), e);
        }

        return cols;
    }
	
	public void setDocId(String docId) {
		this.docId = docId;
	}

	public void setTaskType(int taskType) {
		this.taskType = taskType;
	}
	

}
