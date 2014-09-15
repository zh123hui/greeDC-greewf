package com.gree.mobile.wf;


public final class PersonCoditionFilter {

	/**
	 * 用户表过滤器
	 * @param userStr 用户表(t_pm_user)别名,可以为空,为空表示针对t_pm_user表
	 * @return
	 */
	public static final String mergeUserFilter(String userStr){
		String isDelete = "FIsDelete";
	    String isLocked = "FIsLocked";
	    String isForbidden = "FForbidden";
	    String effectiveDate = "FEffectiveDate";
	    String invalidationDate = "FInvalidationDate";
		
		if (userStr != null)
	    {
	      isDelete = userStr + "." + isDelete;
	      isLocked = userStr + "." + isLocked;
	      isForbidden = userStr + "." + isForbidden;
	      effectiveDate = userStr + "." + effectiveDate;
	      invalidationDate = userStr + "." + invalidationDate;
	    }
		
		StringBuffer sql = new StringBuffer();
//		String today = DateTimeUtils.format(new Date());
		sql.append(" ( ");
		sql.append(" "+isDelete+" = 0 ");
		sql.append(" and "+isLocked+" = 0 ");
		sql.append(" and "+isForbidden+" = 0 ");
//		sql.append(" and "+effectiveDate+" < '" + today + "' ");
//		sql.append(" and "+invalidationDate+" > '" + today + "' ");
		sql.append(" ) ");
		return sql.toString();
	}
	/**
	 * 职员类型表过滤器
	 * @param empTypeStr 职员类型表(T_HR_BDEmployeeType)别名,不可以为空
	 * @return
	 */
	public static final String mergeEmpTypeFilter(String empTypeStr){
		if(empTypeStr==null){
			return "";
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" ( ");
		sql.append(empTypeStr + ".FInService=1 ");
		sql.append(" or ");
		sql.append(empTypeStr + ".FInService=4 ");
		sql.append(" ) ");
		return sql.toString();
	}
	
	/**
	 * 获取一张表中数据只包括有效职员并过滤当前登录职员的子查询职员表,表结构跟职员表(T_BD_Person)一样
	 * @param currentPersonId 当前登录职员ID
	 * @return
	 */
	public static String getVailablePersonTableData(String currentPersonId){
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT DISTINCT ");
		sql.append(" pp.* ");
		sql.append(" FROM ");
		sql.append(" t_pm_user uu , ");
		sql.append(" T_BD_Person pp, ");
		sql.append(" T_HR_BDEmployeeType tt ");
		sql.append(" WHERE ");
		sql.append(" uu.FPersonId=pp.fid ");
		sql.append(" AND pp.FEmployeeTypeID=tt.FID ");
		
		if(currentPersonId!=null && currentPersonId.trim().length()>0){
			sql.append(" AND ");
			sql.append(" pp.fid <> '" + currentPersonId +"'");
		}
		
		sql.append(" AND ");
		sql.append(mergeUserFilter("uu"));
		
		sql.append(" AND ");
		sql.append(mergeEmpTypeFilter("tt"));
		
		sql.append(" AND pp.FDeletedStatus=1 ");
		
		return sql.toString();
	}
	
	/**
	 * 获取一张表中数据只包括有效职员的子查询职员表,表结构跟职员表(T_BD_Person)一样
	 * @return
	 */
	public static String getAvailablePersonTableData(){
		return getVailablePersonTableData((String)null);
	}
	
	public static void main(String[] args) {
//		System.out.println(mergeUserFilter("user"));;
//		
//		System.out.println(mergeEmpTypeFilter("EmpType"));;
//
//		System.out.println(getAvailablePersonTableData());;

		StringBuffer sql = new StringBuffer();
		sql.append("select DISTINCT p.fid personid, p.FName_L2 personName, c.FName_L2 positionName, d.FDisplayName_l2 orgDisplayName, p.FNumber findex")
		.append(" from  ")
		.append(" ( ")
		.append(PersonCoditionFilter.getVailablePersonTableData("cOg51gETEADgADsiwKgSZYDvfe0="))
		.append(" ) ")
		.append(" p ")
		.append(" inner join t_org_positionmember b on p.fid=b.fpersonid ")
		.append(" inner join t_org_position c on b.fpositionid=c.fid ")
		.append(" inner join t_org_admin d on c.FAdminOrgUnitId=d.fid ")
		.append(" where (p.FName_L2 like ? or p.FFullNamePingYin like ? or p.FSimpleNamePingYin like ?) ")
		.append(" order by p.FNumber");
		System.out.println(sql.toString());
		
//		System.out.println(sql.toString());
		//用户信息过滤代码
//		String isDelete = "isDelete";
//	    String isLocked = "isLocked";
//	    String isForbidden = "isForbidden";
//	    String effectiveDate = "effectiveDate";
//	    String invalidationDate = "invalidationDate";
//	    FilterInfo userFilter = new FilterInfo();
//	    userFilter.getFilterItems().add(new FilterItemInfo(isDelete, Boolean.FALSE));
//	    userFilter.getFilterItems().add(new FilterItemInfo(isLocked, Boolean.FALSE));
//	    userFilter.getFilterItems().add(new FilterItemInfo(isForbidden, Boolean.FALSE));
//	    Date date = new Date();
//	    userFilter.getFilterItems().add(new FilterItemInfo(effectiveDate, date, CompareType.LESS));
//	    userFilter.getFilterItems().add(new FilterItemInfo(invalidationDate, date, CompareType.GREATER));

		//职员类型过滤代码
//	    FilterInfo empTypeFilter = new FilterInfo();
//	    empTypeFilter.getFilterItems().add(new FilterItemInfo(empTypeStr + ".id", null));
//	    empTypeFilter.getFilterItems().add(new FilterItemInfo(empTypeStr + ".inService", new Integer(1)));
//	    empTypeFilter.getFilterItems().add(new FilterItemInfo(empTypeStr + ".inService", new Integer(4)));
//	    empTypeFilter.setMaskString("#0 or (#1 or #2)");
	}
}
