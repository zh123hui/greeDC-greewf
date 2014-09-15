package com.gree.mobile.wf.vo;

import java.util.ArrayList;
import java.util.List;

public class PersonVO {

	private String personId;
	private String name;
	private List<PositionItem> positions=new ArrayList<PersonVO.PositionItem>();
	
	public PersonVO(String personId, String name) {
		super();
		this.personId = personId;
		this.name = name;
	}

	public static class PositionItem{
		private String position;
		private String organ;
		public PositionItem(String position, String organ) {
			super();
			this.position = position;
			this.organ = organ;
		}
		public String getPosition() {
			return position;
		}
		public void setPosition(String position) {
			this.position = position;
		}
		public String getOrgan() {
			return organ;
		}
		public void setOrgan(String organ) {
			this.organ = organ;
		}
	}

	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PositionItem> getPositions() {
		return positions;
	}

	public void setPositions(List<PositionItem> positions) {
		this.positions = positions;
	}
	
}
