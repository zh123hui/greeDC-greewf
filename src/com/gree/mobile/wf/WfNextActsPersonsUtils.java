package com.gree.mobile.wf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.kingdee.bos.BOSException;
import com.kingdee.bos.Context;
import com.kingdee.bos.workflow.AssignmentInfo;
import com.kingdee.bos.workflow.define.ActivityDef;
import com.kingdee.bos.workflow.define.ManpowerActivityDef;
import com.kingdee.bos.workflow.define.ParticipantCollection;
import com.kingdee.bos.workflow.define.ParticipantDef;
import com.kingdee.bos.workflow.define.ProcessDef;
import com.kingdee.bos.workflow.define.extended.ApproveActivityDef;
import com.kingdee.bos.workflow.ext.AbstractWfNextPersonExt;
import com.kingdee.bos.workflow.ext.ConfigLoader;
import com.kingdee.bos.workflow.ext.IWfNextPersonExt;
import com.kingdee.bos.workflow.monitor.WfProcessDiagram;
import com.kingdee.bos.workflow.participant.ActivityPersonsInfo;
import com.kingdee.bos.workflow.participant.Person;
import com.kingdee.bos.workflow.service.ormrpc.EnactmentServiceFactory;
import com.kingdee.bos.workflow.service.ormrpc.IEnactmentService;
import com.kingdee.util.StringUtils;

public class WfNextActsPersonsUtils {
	IWfNextPersonExt ext;
	Context ctx;

	public WfNextActsPersonsUtils(Context ctx) {
		this.ctx = ctx;
	}

	// 保存下一步参与人
	public void saveNextActsPersons(List nextActs, String assignId) throws Exception {
		IEnactmentService svc = EnactmentServiceFactory.createEnactService(ctx);
		AssignmentInfo assign = svc.getAssignmentById(assignId);
		if (nextActs != null) {
			for (int i = 0; i < nextActs.size(); i++) {
				Map act = (Map) nextActs.get(i);
				String key = (String) act.get("actKey");
				Map persons = (Map) act.get("persons");
				ArrayList personIds = new ArrayList();
				Iterator it = persons.entrySet().iterator();
				while (it.hasNext()) {
					Entry person = (Entry) it.next();
					if (person.getValue() != null && !"".equals(person.getValue())) {
						personIds.add(person.getKey());
					}
				}
				svc.setProcessContext(assign.getProcInstId(), key, personIds);
			}
		}
	}

	private ArrayList getParticipantDefs(AssignmentInfo assign, ManpowerActivityDef manActDef) {
		ArrayList participants = new ArrayList();
		ParticipantCollection pc = manActDef.getParticipants();
		for (int i = 0; i < pc.size(); i++) {
			ParticipantDef p = pc.get(i);
			if (this.ext.isIgnore(p, assign.getAssignmentId(), null)) {
				continue;// 忽略
			}
			participants.add(p.getID());
		}
		return participants;
	}

	private List participantsToPersons(IEnactmentService svc, AssignmentInfo assign, ProcessDef def, ManpowerActivityDef manActDef, ArrayList participants) throws Exception {
		List persons;
		String hashCode = ((ProcessDef) manActDef.getContainer()).getHashValue();
		if (hashCode.equals(def.getHashValue())) {// 同一个流程
			persons = svc.getPersonsByParticipants(assign.getProcInstId(), manActDef.getID(), participants);
		} else {// 不是同一个流程
			persons = svc.getPersonsByDefParticipants(hashCode, manActDef.getID(), participants, ctx.getCaller().toString());
		}
		return persons;
	}

	private void appendAdditionalPersons(IEnactmentService svc, AssignmentInfo assign, ManpowerActivityDef manActDef, List persons) throws Exception {
		// 允许扩展添加参与人
		String[] addedPersons = ext.getAddedCandidates(manActDef, assign.getAssignmentId(), null);
		if (addedPersons != null) {
			for (int i = 0; i < addedPersons.length; i++) {
				Person[] tmp = svc.getPersonByPersonID(addedPersons[i]);
				for (int j = 0; j < tmp.length; j++) {
					persons.add(tmp[j]);
				}
			}
		}
	}

	private List estimateParticipants(IEnactmentService svc, AssignmentInfo assign, ProcessDef def, ManpowerActivityDef manActDef) throws Exception {

		ArrayList participants = getParticipantDefs(assign, manActDef);

		List persons = participantsToPersons(svc, assign, def, manActDef, participants);

		appendAdditionalPersons(svc, assign, manActDef, persons);

		return persons;
	}

	private List retrieveParticipants(IEnactmentService svc, List personIds) throws Exception {
		List persons;
		persons = new ArrayList();
		for (int i = 0; i < personIds.size(); i++) {
			String personId = (String) personIds.get(i);
			Person[] tmp = svc.getPersonByPersonID(personId);
			for (int j = 0; j < tmp.length; j++) {
				persons.add(tmp[j]);
			}
		}
		return persons;
	}

	private List calcParticipants(IEnactmentService svc, AssignmentInfo assign, ProcessDef def, String key, ManpowerActivityDef manActDef) throws Exception {
		List personIds = (List) svc.getProcessContext(assign.getProcInstId(), key);
		List persons;
		if (personIds == null || personIds.size() == 0) {// 定义参与人
			persons = estimateParticipants(svc, assign, def, manActDef);
		} else {// 指定的参与人
			persons = retrieveParticipants(svc, personIds);
		}
		return persons;
	}

	private Map eliminateDuplicatedPersons(List persons) {
		Map candidates = new HashMap();
		for (int i = 0; i < persons.size(); i++) {
			Person p = (Person) persons.get(i);
			candidates.put(p.getEmployeeId(), p.getEmployeeName(ctx.getLocale()));
		}
		return candidates;
	}

	private String generateActName(ProcessDef def, ManpowerActivityDef manActDef) {
		ProcessDef current = ((ProcessDef) manActDef.getContainer());
		String actName;
		if (current == def) {
			actName = manActDef.getName(ctx.getLocale());
		} else {
			actName = manActDef.getName(ctx.getLocale()) + " - " + current.getName(ctx.getLocale());
		}
		return actName;
	}

	private HashMap assembleActAndPersonInfo(ProcessDef def, ManpowerActivityDef manActDef, String key, Map candidates) {
		HashMap nextAct = new HashMap();
		nextAct.put("persons", candidates);
		nextAct.put("count", String.valueOf(candidates.size()));
		nextAct.put("actKey", key);
		nextAct.put("actId", manActDef.getID());
		String actName = generateActName(def, manActDef);
		nextAct.put("actName", actName);
		nextAct.put("isAllPersonAsPerformer", Boolean.valueOf(manActDef.isAllPersonAsPerformer()));
		return nextAct;
	}

	private void calcAllNextParticipants(IEnactmentService svc, AssignmentInfo assign, ProcessDef def, ArrayList nextActs, HashMap[] acts) throws Exception {
		for (int k = 0; k < acts.length; k++) {
			ActivityDef actDef = (ActivityDef) acts[k].get("actDef");
			if (!(actDef instanceof ManpowerActivityDef)) {
				HashMap[] subActs = (HashMap[]) acts[k].get("subActivities");
				calcAllNextParticipants(svc, assign, def, nextActs, subActs);
				continue;
			}

			ManpowerActivityDef manActDef = (ManpowerActivityDef) actDef;
			if (ext.isIgnore(manActDef, assign.getAssignmentId(), null)) {// 忽略
				continue;
			}

			String key = com.kingdee.bos.workflow.participant.ParticipantUtils.createDesignatePerformerKey(manActDef);
			List persons = calcParticipants(svc, assign, def, key, manActDef);
			Map candidates = eliminateDuplicatedPersons(persons);
			HashMap nextAct = assembleActAndPersonInfo(def, manActDef, key, candidates);
			nextActs.add(nextAct);
		}
	}

	private void findNextActsAndPersons(IEnactmentService svc, AssignmentInfo assign, ProcessDef def, String associatedLine, ArrayList nextActs) throws Exception {

		initNextPersonExt(assign);

		HashMap[] acts = searchNextActs(assign, associatedLine);
		if (acts.length == 0) {
			return;
		}

		calcAllNextParticipants(svc, assign, def, nextActs, acts);
	}

	private void initNextPersonExt(AssignmentInfo assign) {
		String billId = assign.getBizObjectIds();
		this.ext = ConfigLoader.getConfig4Server().getWfNextPersonExt(ctx, billId);
		if (this.ext == null) {
			this.ext = new AbstractWfNextPersonExt(ctx);
		}
	}

	private HashMap[] searchNextActs(AssignmentInfo assign, String associatedLine) throws BOSException {
		WfProcessDiagram diag = new WfProcessDiagram(ctx, associatedLine);
		String[] actIds = new String[] { assign.getActInstId() };
		HashMap[] acts = diag.findNextManpownerActivities(actIds);
		return acts;
	}

	public List findPostParticipants(String assignId, String associatedLine) throws Exception {
		IEnactmentService svc = EnactmentServiceFactory.createEnactService(ctx);
		ArrayList nextActs = new ArrayList();
		AssignmentInfo assign = svc.getAssignmentById(assignId);
		ProcessDef def = svc.getProcessDefByDefineHashValue(assign.getProcDefHashValue());
		findNextActsAndPersons(svc, assign, def, associatedLine, nextActs);
		return nextActs;
	}

	private String checkPostActivityPersons(IEnactmentService svc, String actInstId, String currentUserId, String associationLine) throws Exception {
		Class<?>[] parameterTypes1 = { String.class, String.class, Boolean.TYPE, String.class };
		Object[] args = null;
		java.lang.reflect.Method method = null;
		try {
			method = svc.getClass().getMethod("getPostActivitysPersonsByActInstID", parameterTypes1);
			args = new Object[] { actInstId, currentUserId, Boolean.valueOf(true), associationLine };
		} catch (Throwable t) {
			Class<?>[] parameterTypes2 = { String.class, String.class, Boolean.TYPE };
			try {
				method = svc.getClass().getMethod("getPostActivitysPersonsByActInstID", parameterTypes2);
				args = new Object[] { actInstId, currentUserId, Boolean.valueOf(true) };
			} catch (Throwable t2) {
				method = null;
			}
		}
		if (method == null)
			return null;

		ActivityPersonsInfo[] allPostActPersons = (ActivityPersonsInfo[]) method.invoke(svc, args);
		StringBuffer bf = new StringBuffer();
		for (int i = 0; i < allPostActPersons.length; ++i) {
			ActivityPersonsInfo info = allPostActPersons[i];
			if (info.getPersons().length <= 0) {
				bf.append(info.getActDefName());
				bf.append(" -");
				bf.append(info.getProcDefName());
				bf.append(",");
			}
		}
		if (bf.toString().trim().length() > 0) {
			String mesg = bf.substring(0, bf.length() - 1);
			return mesg;
		}
		return null;
	}

	public List checkPostParticipants(String assignId, String decisionKey, Map a, boolean isAfterSetting) throws Exception {
		IEnactmentService svc = EnactmentServiceFactory.createEnactService(ctx);
		ArrayList nextActs = new ArrayList();
		if (a == null) {
			a = svc.getExtendedAttributesFromAssignment(assignId);
		}
		AssignmentInfo assign = svc.getAssignmentById(assignId);

		String decisions = StringUtils.cnulls(a.get("manualDecisionItems_l2"));
		if (StringUtils.isEmpty(decisions)) {// bos6.0以前
			decisions = StringUtils.cnulls(a.get("manualDecisionItems"));
		}
		String associatedLine = "";
		if (!StringUtils.isEmpty(decisions) && !StringUtils.isEmpty(decisionKey)) {
			String[] strs1 = decisions.split(":");
			for (int i = 0; i < strs1.length; i++) {
				String[] strs2 = strs1[i].split(";");
				if (strs2.length == 4 && decisionKey.equals(strs2[0])) {
					if (!StringUtils.isEmpty(strs2[3])) {
						associatedLine = strs2[3];
					}
					break;
				}
			}
		}

		boolean shouldCheckNextPartipants = Boolean.parseBoolean((String) a.get("nextPerson"));
		boolean alwaysSetNextPersons = Boolean.parseBoolean((String) a.get("alwaysSetNextPersons"));
		boolean tmpb = ConfigLoader.getConfig4Server().getBoolean("always.set.next.persons", assign.getBizObjectIds(), "false");
		if (tmpb)
			alwaysSetNextPersons = tmpb;

		ProcessDef pDef = svc.getProcessDefByDefineHashValue(assign.getProcDefHashValue());
		ActivityDef actDef = pDef.getActivityDef(assign.getActDefId());
		// boolean mustShowPostActivities = false;
		boolean canSpecifyNextPerformer = false;
		if (actDef instanceof ApproveActivityDef) {
			// mustShowPostActivities =
			// ((ApproveActivityDef)actDef).isMustShowParticipantsCheckList();
			canSpecifyNextPerformer = ((ApproveActivityDef) actDef).canSpecifyNextActivityPerformer();
		}

		if (alwaysSetNextPersons)
			canSpecifyNextPerformer = true;
		if (!canSpecifyNextPerformer)
			shouldCheckNextPartipants = false;
		if (alwaysSetNextPersons && !isAfterSetting) {// 总需指定
			findNextActsAndPersons(svc, assign, pDef, associatedLine, nextActs);
		} else if (shouldCheckNextPartipants) {// 检查
			//调用工作流接口, 只检查直接后续人工活动
			String msg = checkPostActivityPersons(svc, assign.getActInstId(), assign.getUserId(), associatedLine);
			if (!StringUtils.isEmpty(msg)) {
				ArrayList nextActs2 = new ArrayList();
				findNextActsAndPersons(svc, assign, pDef, associatedLine, nextActs2);
				for (int i = 0, len = nextActs2.size(); i < len; i++) {
					HashMap map = (HashMap) nextActs2.get(i);
					if (Integer.parseInt(StringUtils.cnulls(map.get("count"))) == 0) {
						nextActs.add(map);
					}
				}
			}
		}
		return nextActs;
	}
}
