package com.gree.mobile.wf.action;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.gree.mobile.common.permission.UserContext;
import com.gree.mobile.common.permission.UserContextManager;
import com.gree.mobile.common.web.action.JsonAction;
import com.gree.mobile.common.web.exception.JsonActionException;
import com.gree.mobile.wf.jdbc.EasDbUtil;
import com.gree.mobile.wf.vo.PersonVO;
import com.kingdee.bos.Context;
import com.kingdee.jdbc.rowset.IRowSet;

public class SearchPersonAction extends JsonAction {

	private String word;

	@Override
	public Object doExecute() throws Exception {
		if(StringUtils.isEmpty(word)){
			return Collections.emptyList();
		}
		UserContext uc = UserContextManager.getUserContext(getSession());
		Context ctx = uc.getBosContext();
		String currentPersonId = uc.getPersonId();

		List<Object> params = new ArrayList<Object>();
		word = "%" + word + "%";
		StringBuffer sql = new StringBuffer();
		sql.append("select DISTINCT p.fid PERSONID, p.FName_L2 PERSONNAME, c.FName_L2 POSITIONNAME, d.FDisplayName_l2 ORGDISPLAYNAME, p.FNumber FNumber");
		sql.append(" from  ");
		sql.append(" ( ");
		
		// TODO : 过滤
		sql.append(" SELECT DISTINCT ");
		sql.append(" pp.* ");
		sql.append(" FROM ");
		sql.append(" t_pm_user uu , ");
		sql.append(" T_BD_Person pp, ");
		sql.append(" T_HR_BDEmployeeType tt ");
		sql.append(" WHERE ");
		sql.append(" uu.FPersonId=pp.fid ");
		sql.append(" AND pp.FEmployeeTypeID=tt.FID ");
		sql.append(" AND ");
		sql.append(" pp.fid <> ?");
		params.add(currentPersonId);
		sql.append(" AND ");
		sql.append(" ( ");
		sql.append(" uu.FIsDelete = 0 ");
		sql.append(" and uu.FIsLocked = 0 ");
		sql.append(" and uu.FForbidden = 0 ");
		sql.append(" and uu.FEffectiveDate < ? ");
		sql.append(" and uu.FInvalidationDate > ? ");
		
		Timestamp now = new Timestamp(System.currentTimeMillis());
		params.add(now);
		params.add(now);

		sql.append(" ) ");
		sql.append(" AND ");
		sql.append(" ( ");
		sql.append("tt.FInService=1 ");
		sql.append(" or ");
		sql.append("tt.FInService=4 ");
		sql.append(" ) ");
		
		sql.append(" AND pp.FDeletedStatus=1 ");
		
		sql.append(" ) ");
		sql.append(" p ");
		sql.append(" inner join t_org_positionmember b on p.fid=b.fpersonid ");
		sql.append(" inner join t_org_position c on b.fpositionid=c.fid ");
		sql.append(" inner join t_org_admin d on c.FAdminOrgUnitId=d.fid ");
		
		sql.append(" where (p.FName_L2 like ? or p.FFullNamePingYin like ? or p.FSimpleNamePingYin like ?) ");
		params.add(word);
		params.add(word);
		params.add(word);
		
		sql.append(" order by p.FNumber");
		IRowSet rowSet = EasDbUtil.executeQuery(ctx, sql.toString(), params.toArray(), 0, 50);
		if (rowSet.size()==0) {
			return Collections.emptyList();
		}
		List<PersonVO> list = new ArrayList<PersonVO>();
		//把重复的职员合并
		Map<String, PersonVO> cache = new HashMap<String, PersonVO>();

		int max = 30;
		int counter =0 ;
		try{
			while(rowSet.next()){
				String personid = rowSet.getString("PERSONID");
				String personName = rowSet.getString("PERSONNAME");
				String positionName = rowSet.getString("POSITIONNAME");
				String orgDisplayName = rowSet.getString("ORGDISPLAYNAME");
				PersonVO person = cache.get(personid);
				if (person == null) {
					person = new PersonVO( personid, personName);
					list.add(person);
					cache.put(personid, person);
				}
				person.getPositions().add(new PersonVO.PositionItem ( positionName, orgDisplayName ));
				counter++;
				if(counter>=max){
					break;
				}
			};
		}catch (Exception e) {
			throw new JsonActionException("搜索失败,请稍后再试", e);
		}
		return list;
	}

	public void setWord(String word) {
		this.word = word;
	}
	

}
