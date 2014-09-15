package com.gree.mobile.wf.taskinfo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gree.mobile.wf.taskinfo.xml.TaskInfoNode;
import com.gree.mobile.wf.taskinfo.xml.simple.GroupTableNode;
import com.gree.mobile.wf.taskinfo.xml.simple.ListTableNode;
import com.gree.mobile.wf.taskinfo.xml.simple.RowItemNode;
import com.gree.mobile.wf.taskinfo.xml.simple.SimpleViewNode;
import com.kingdee.bos.metadata.MetaDataLoaderFactory;
import com.kingdee.bos.metadata.entity.CardinalityType;
import com.kingdee.bos.metadata.entity.DataType;
import com.kingdee.bos.metadata.entity.EntityObjectInfo;
import com.kingdee.bos.metadata.entity.LinkPropertyInfo;
import com.kingdee.bos.metadata.entity.OwnPropertyInfo;
import com.kingdee.bos.metadata.entity.PropertyCollection;
import com.kingdee.bos.metadata.entity.PropertyInfo;
import com.kingdee.bos.metadata.entity.RelationshipInfo;
import com.kingdee.bos.util.BOSObjectType;

public class DefaultTaskInfoNoteBuilder implements ITaskInfoNoteBuilder {

	public TaskInfoNode build(BOSObjectType bosType){
		if(bosType==null){
			return null;
		}
		EntityObjectInfo eo = MetaDataLoaderFactory.getRemoteMetaDataLoader().getEntity(bosType);
		if(eo == null){
			throw new RuntimeException(String.format(
					"系统无法生成bosType=[%s]的单据详情模板文件:bosType对应的单据实体对象为空", bosType));
		}
		
		TaskInfoNode config = new TaskInfoNode();
		String bosTypeStr = bosType.toString();
		config.setBosType(bosTypeStr);
		config.setBosTypeName(eo.getAlias());
		
		SimpleViewNode viewNode = new SimpleViewNode();
		viewNode.setViewName("defaultView");
		config.addViewNode(viewNode);
		config.setDefaultView("defaultView");
		viewNode.setHiddenIfEmpty(false);
		GroupTableNode defaultTable = new GroupTableNode();
		viewNode.addTable(defaultTable);
		
		PropertyCollection propertyCol = eo.getInheritedNoDuplicatedPropertiesRuntime();
		
		for (int i = 0; i < propertyCol.size(); i++) {
			PropertyInfo pinfo = propertyCol.get(i);
			String alias = pinfo.getAlias();
			String propertyName = pinfo.getName();
			
			if(filterProp(bosTypeStr, propertyName)){
				continue;
			}
			if ("id".equalsIgnoreCase(propertyName)) {// 去除主键
				continue;
			}
			
			if (pinfo instanceof OwnPropertyInfo) {// 自有属性
				RowItemNode item = new RowItemNode();
				defaultTable.addItem(item);
				item.setPropName(propertyName);
				item.setTitle(alias);
				findDataType((OwnPropertyInfo)pinfo, item);
				continue;
			} 
			if (pinfo instanceof LinkPropertyInfo) {
				// 连接属性
				LinkPropertyInfo linkProInfo = (LinkPropertyInfo) pinfo;
				RelationshipInfo relationship = linkProInfo.getRelationship();
				CardinalityType cardType=relationship.getSupplierCardinality();
				EntityObjectInfo entryInfo = relationship.getSupplierObject();
				
				if (!(cardType.equals(CardinalityType.ZERO_TO_UNBOUNDED)
						|| cardType.equals(CardinalityType.ONE_TO_UNBOUNDED))){
					RowItemNode item = new RowItemNode();
					defaultTable.addItem(item);
					PropertyInfo nameProperty = entryInfo.getPropertyByName("name");
					if(nameProperty!=null){
						item.setPropName(propertyName+".name");
						if(nameProperty instanceof OwnPropertyInfo){
							findDataType((OwnPropertyInfo)nameProperty, item);
						}
					}else if(entryInfo.getObjectValueClass().equals("com.kingdee.eas.base.form.core.UserDefinedData.UserDefinedDataEntryInfo")){
						item.setPropName(propertyName);
					} else {
						item.setPropName(propertyName+".name");
					}
					item.setTitle(alias);
					
					continue;
				}
				// 1..n或者0..n才认为分录
				ListTableNode listTable = new ListTableNode();
				listTable.setPropName(propertyName);
				listTable.setTitle(alias);
				viewNode.addTable(listTable);
				
				PropertyCollection entryPropertyCol = entryInfo.getInheritedNoDuplicatedPropertiesRuntime();
				for (int j = 0; j < entryPropertyCol.size(); j++) {
					PropertyInfo entryPinfo = entryPropertyCol.get(j);
					// 去除主键和到单据体的连接
					String name = entryPinfo.getName();
					if("id".equalsIgnoreCase(name) || "parent".equalsIgnoreCase(name)){
						continue;
					}
					// 付款单
					if("40284E81".equals(bosTypeStr) && ("curProject".equalsIgnoreCase(name) || "productType".equalsIgnoreCase(name))){
						continue;
					}
					// 收款单
					if("FA44FD5B".equals(bosTypeStr) && "curProject".equalsIgnoreCase(name)){
						continue;
					}
					
					RowItemNode item = new RowItemNode();
					String pName = name;
					if(entryPinfo instanceof LinkPropertyInfo){
						LinkPropertyInfo linkEntry = (LinkPropertyInfo) entryPinfo;
						RelationshipInfo relationshipEntry = linkEntry.getRelationship();
						CardinalityType cardTypeEntry = relationshipEntry.getSupplierCardinality();
						// 默认分录中不会再有分录
						if (cardTypeEntry.equals(CardinalityType.ZERO_TO_UNBOUNDED)
								|| cardTypeEntry.equals(CardinalityType.ONE_TO_UNBOUNDED)){
							continue;
						}
						EntityObjectInfo supplierObject = relationshipEntry.getSupplierObject();
						PropertyInfo nameProperty = supplierObject.getPropertyByName("name");
						if(nameProperty!=null){
							pName += ".name";
							if(nameProperty instanceof OwnPropertyInfo){
								findDataType((OwnPropertyInfo)nameProperty, item);
							}
						}else if(!supplierObject.getObjectValueClass().equals("com.kingdee.eas.base.form.core.UserDefinedData.UserDefinedDataEntryInfo")){
							pName += ".name";
						}
					}
					if(entryPinfo instanceof OwnPropertyInfo){
						findDataType((OwnPropertyInfo)entryPinfo, item);
					}
					item.setPropName(pName);
					item.setTitle(entryPinfo.getAlias());
					listTable.addItem(item);
				}
			
			}
		}
		return config;
	}

	private void findDataType(OwnPropertyInfo pinfo, RowItemNode item) {
		if(pinfo==null){
			return ;
		}
		DataType dataType = pinfo.getDataType();
		item.setDataType(dataType.getName());
		item.setFormat(findDefaultFormat(dataType));
	}

	private String findDefaultFormat(DataType dataType) {
		if(dataType == null){
			return null;
		}
		if(dataType==DataType.DATE){
			return "yyyy-MM-dd";
		}else if(dataType==DataType.TIMESTAMP){
			return "yyyy-MM-dd HH:mm:ss";
		}else if(dataType==DataType.TIME){
			return "HH:mm:ss";
		}else if(dataType==DataType.DECIMAL){
			return "#,##0.##";
		}
		return null;
	}
	
	private static boolean filterProp(String bosTypeStr, String propName){
		List<String> list = filterProperty.get(bosTypeStr);
		if(list==null){
			return false;
		}
		for(String s : list){
			if(s.equalsIgnoreCase(propName)){
				return true;
			}
		}
		return false;
	}
	private static final Map<String, List<String>> filterProperty = new HashMap<String, List<String>>();
	static {
		// 付款单
		filterProperty.put("40284E81", Arrays.asList(new String[]{
				"curProject", 
				"fdcPayType", 
				"fdcPayeeName", 
				"deductMoneyType", 
				"actFdcPayeeName", 
				"fdcPayReqNumber", 
				"fdcPayReqID", 
				"productType", 
				"printCount"
		}));
		// 收款单
		filterProperty.put("FA44FD5B", Arrays.asList(new String[]{
				"curProject"
		}));
	}
}
