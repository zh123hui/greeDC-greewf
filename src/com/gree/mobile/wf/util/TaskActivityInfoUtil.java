package com.gree.mobile.wf.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.gree.mobile.common.web.exception.JsonActionException;
import com.gree.mobile.wf.vo.ActivityInfoVO;
import com.gree.mobile.wf.vo.ActivityInfoVO.Decision;
import com.gree.mobile.wf.vo.ActivityInfoVO.DecisionOption;
import com.gree.mobile.wf.vo.ActivityInfoVO.OtherOper;
import com.gree.mobile.wf.vo.ActivityInfoVO.OtherOperEnum;
import com.kingdee.bos.workflow.define.ActualParameterCollection;
import com.kingdee.bos.workflow.define.ActualParameterDef;
import com.kingdee.bos.workflow.define.DataFieldCollection;
import com.kingdee.bos.workflow.define.DataFieldDef;
import com.kingdee.bos.workflow.define.ExtendedAttributeCollection;
import com.kingdee.bos.workflow.define.ExtendedAttributeDef;
import com.kingdee.bos.workflow.define.ProcessDef;
import com.kingdee.bos.workflow.define.extended.ApproveActivityDef;
import com.kingdee.bos.workflow.define.extended.ManualDecisionActivityDef;
import com.kingdee.util.StringUtils;

public class TaskActivityInfoUtil {

	public static List<OtherOper> findOtherOper(Object activityDef, int type) throws JsonActionException {
		List<OtherOper> otherOpers = new ArrayList<ActivityInfoVO.OtherOper>();
		switch (type) {
		case 3:
			ApproveActivityDef approveActDef = (ApproveActivityDef) activityDef;
			if (approveActDef.getAllowAssignNextPerformer()) {
				otherOpers.add(OtherOperEnum.DELIVER.get());
			}
			break;
		case 4:
			ManualDecisionActivityDef mdActDef = (ManualDecisionActivityDef) activityDef;
			if (mdActDef.getAllowAssignNextPerformer()) {
				otherOpers.add(OtherOperEnum.DELIVER.get());
			}
			break;
		}
		return otherOpers;
	}

	public static List<Decision> findDecisions(Object activityDef, int type) {
		String decisions = null;
		switch (type) {
		case 3:
			ApproveActivityDef approveActDef = (ApproveActivityDef) activityDef;
			ExtendedAttributeCollection extAttrCol = approveActDef
					.getActivityHeader().getExtendedAttributes();
			decisions = approveActDef.getManualDecisionItems(new Locale("l2"));
			if (StringUtils.isEmpty(decisions)) {
				if (extAttrCol != null) {
					ExtendedAttributeDef extAttrDef = extAttrCol
							.get("manualDecisionItems");
					if (extAttrDef != null) {
						decisions = extAttrDef.getValue();
					}
				}
			}
			return parseApproveDecisionString(decisions);
		case 4:
			ManualDecisionActivityDef mdActDef = (ManualDecisionActivityDef) activityDef;

			ActualParameterCollection actualParamCol = mdActDef
					.getActualParameters();
			DataFieldCollection col = ((ProcessDef) mdActDef.getContainer())
					.getDataFields();
			for (int i = 0; i < actualParamCol.size(); i++) {
				ActualParameterDef def = actualParamCol.get(i);
				if (StringUtils.isEmpty(def.getExpr())) {
					continue;
				}
				if (def.getIndex() == 1) {
					DataFieldDef dataFieldDef = col.get(def.getExpr());
					decisions = dataFieldDef.getInitValue();
					break;
				}
			}
			return splitDecisionString(decisions);
		default :
			return null;
		}
	}
//	public static boolean existKeyAndValueInApproveActivity(Object activityDef, boolean agree, String key, String value) {
//		List<Decision> decisions = findDecisions(activityDef, 3);
//		if(decisions==null || decisions.size()==0){
//			return false;
//		}
//		int index = agree ? 0 : 1;
//		return existOption(decisions.get(index).getOptions(), key, value);
//	}
//	public static boolean existKeyAndValueInDecisionActivity(Object activityDef, String key, String value) {
//		List<Decision> decisions = findDecisions(activityDef, 4);
//		if(decisions==null || decisions.size()==0){
//			return false;
//		}
//		return existOption(decisions.get(0).getOptions(), key, value);
//	}
	public static boolean existOption(List<DecisionOption> options, String key, String value) {
		if(options==null ||options.size()==0){
			return false;
		}
		if(key==null || value==null){
			return false;
		}
		for(DecisionOption opt : options){
			if(opt==null){
				continue;
			}
			if(key.equalsIgnoreCase(opt.getOptKey()) && value.equalsIgnoreCase(opt.getOptValue())){
				return true;
			}
		}
		return false;
	}

	public static List<Decision> parseApproveDecisionString(String decisions) {
		List<Decision> list = new ArrayList<Decision>();
		Decision agree = new Decision("同意");
		Decision disAgree = new Decision("不同意");
		list.add(agree);
		list.add(disAgree);
		if (StringUtils.isEmpty(decisions)) {
			return list;
		}
		String[] arr1 = decisions.split(":");
		for (int i = 0; i < arr1.length; i++) {
			String[] arr2 = arr1[i].split(";");
			DecisionOption option = new DecisionOption(arr2[0], arr2[1]);
			if (arr2[2].equals("全部")) {
				agree.getOptions().add(option);
				disAgree.getOptions().add(option);
			} else if (arr2[2].equals("同意")) {
				agree.getOptions().add(option);
			} else if (arr2[2].equals("不同意")) {
				disAgree.getOptions().add(option);
			}
		}
		return list;
	}

	public static List<Decision> splitDecisionString(String decisions) {
		List<Decision> list = new ArrayList<Decision>();
		Decision decision = new Decision("决策");
		list.add(decision);
		if (StringUtils.isEmpty(decisions)) {
			return list;
		}
		String[] arr = decisions.split(";");
		for (int i = 0; i < arr.length; i++) {
			decision.getOptions().add(new DecisionOption(String.valueOf(i), arr[i]));
		}
		return list;
	}
}
