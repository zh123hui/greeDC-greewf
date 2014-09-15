package com.gree.mobile.wf;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.kingdee.bos.metadata.IMetaDataLoader;
import com.kingdee.bos.metadata.MetaDataLoaderFactory;
import com.kingdee.bos.metadata.entity.EntityObjectInfo;
import com.kingdee.bos.util.BOSObjectType;
import com.kingdee.bos.util.BOSUuid;


public class BillNameManager {

	private static final Map<String, String> map = new ConcurrentHashMap<String, String>();
	
	/**
	 * 缓存bosType对应的单据名称
	 * @param bosType
	 * @param billName
	 */
	public static void put(BOSObjectType bosType, String billName){
		if(bosType==null || billName==null){
			return ;
		}
		map.put(bosType.toString(), billName);
	}
	/**
	 * 根据单据对象ID获取单据名称
	 * @param bizObjectId
	 * @return
	 */
	public static String getBillName(String bizObjectId) {
		if (bizObjectId == null) {
			return null;
		}
		BOSObjectType bosType = null;
		try {
			BOSUuid id = BOSUuid.read(bizObjectId);
			bosType = id.getType();
		} catch (Exception e) {
		}
		return getBillName(bosType);
	}
	/**
	 * 根据BOSType获取单据名称
	 * @param bosType
	 * @return
	 */
	public static String getBillName(BOSObjectType bosType){
		if(bosType == null){
			return null;
		}
		String type = bosType.toString();
		String billName = map.get(type);
		if(billName != null){
			return billName;
		}
		try {
			IMetaDataLoader remoteMetaDataLoader = MetaDataLoaderFactory.getRemoteMetaDataLoader();
			if (remoteMetaDataLoader != null) {
				EntityObjectInfo eo = remoteMetaDataLoader.getEntity(bosType);
				if (eo != null) {
					String alias = eo.getAlias();
					put(bosType, alias);
					return alias;
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
	
}
