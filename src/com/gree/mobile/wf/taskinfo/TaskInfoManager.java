package com.gree.mobile.wf.taskinfo;

import java.io.File;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.gree.mobile.wf.BillNameManager;
import com.gree.mobile.wf.taskinfo.xml.DefaultXmlFileLoader;
import com.gree.mobile.wf.taskinfo.xml.DefaultXmlFileStorer;
import com.gree.mobile.wf.taskinfo.xml.IXmlFileLoader;
import com.gree.mobile.wf.taskinfo.xml.IXmlFileStorer;
import com.gree.mobile.wf.taskinfo.xml.TaskInfoNode;
import com.kingdee.bos.Context;
import com.kingdee.bos.dao.IObjectValue;
import com.kingdee.bos.dao.ormapping.IORMappingDAO;
import com.kingdee.bos.dao.ormapping.ORMappingDAO;
import com.kingdee.bos.dao.ormapping.ObjectUuidPK;
import com.kingdee.bos.framework.DynamicObjectFactory;
import com.kingdee.bos.framework.IDynamicObject;
import com.kingdee.bos.framework.ejb.EJBFactory;
import com.kingdee.bos.metadata.entity.SelectorItemCollection;
import com.kingdee.bos.util.BOSObjectType;
import com.kingdee.bos.util.BOSUuid;
import com.kingdee.util.db.SQLUtils;

public class TaskInfoManager {

	private IXmlFileLoader loader = new DefaultXmlFileLoader();
	private ITaskInfoNoteBuilder builder = new DefaultTaskInfoNoteBuilder();
	private IXmlFileStorer storer = new DefaultXmlFileStorer();
	
	private static TaskInfoManager instance = new TaskInfoManager();
	public static TaskInfoManager getInstance(){
		return instance;
	} 
	
	public TaskInfoNode buildFromXmlFile(String fileName) throws Exception{
		return loader.load(new File(fileName));
	}
	public TaskInfoConfig getTaskInfoConfig(BOSObjectType bosType, String fileName) throws Exception{
		TaskInfoManager manager = getInstance();
		TaskInfoNode taskInfoNote;
		File file = new File(fileName);
		if(!file.exists()){
			taskInfoNote=manager.buildFromBosType(bosType);
			manager.saveToXML(taskInfoNote, fileName);
		}else{
			taskInfoNote = manager.buildFromXmlFile(fileName);
			BillNameManager.put(bosType, taskInfoNote.getBosTypeName());
		}
		return new TaskInfoConfig(taskInfoNote, fileName);
	}
	public TaskInfoNode buildFromBosType(BOSObjectType bosType){
		return builder.build(bosType);
	}
	
	public void saveToXML(TaskInfoNode config, String fileName) throws Exception{
		storer.storerToXml(config, fileName);
	}
	
	public IObjectValue loadBillObject(Context ctx, String docId, List<String> selectorItems) throws Exception {
		if (ctx == null) {
			return null;
		}
		if(CollectionUtils.isEmpty(selectorItems)){
			BOSUuid id = BOSUuid.read(docId);
			BOSObjectType type = id.getType();
			IDynamicObject dynamicObject = DynamicObjectFactory.getRemoteInstance();
			String sql = (new StringBuffer("where id = '")).append(id.toString()).append("'").toString();
			return dynamicObject.getValue(type, sql);
		}
		Object bak = ctx.get("disablePermissionForKScript");
		IORMappingDAO dao = null;
		IObjectValue model = null;
		Connection cn = null;
		try {
			ctx.put("disablePermissionForKScript", Boolean.TRUE);
			ObjectUuidPK pk = new ObjectUuidPK(docId);
			SelectorItemCollection sc = new SelectorItemCollection();
			for (Iterator it = selectorItems.iterator(); it.hasNext();) {
				sc.add((String) it.next());
			}
			cn = EJBFactory.getConnection(ctx);
			dao = ORMappingDAO.getInstance(pk.getObjectType(), ctx, cn);
			model = dao.getValue(pk, sc, true);
			return model;
		} finally {
			ctx.put("disablePermissionForKScript", bak);
			ctx.put("dataNotFoundException", "dataIsDisappeared");
			SQLUtils.cleanup(cn);
		}
	}
	
	
	
}
