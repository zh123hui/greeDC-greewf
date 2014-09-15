package com.gree.mobile.wf.enums;

public enum TaskTypeEnum {

	TODO(1),
	DONE(2),
	DOING(3);
	private int value;

	private TaskTypeEnum(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	public static TaskTypeEnum getEnum(int value){
		for(TaskTypeEnum e : TaskTypeEnum.values()){
			if(e.getValue()==value){
				return e;
			}
		}
		return null;
	}
	
}
