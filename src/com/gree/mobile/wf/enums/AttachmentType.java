package com.gree.mobile.wf.enums;


public enum AttachmentType {
	
	/** 附件类型之单据的正文内容 */
	fileType_content("1"),
	/** 附件类型之单据的通过eas基础的附件控件上传的附件 */
	fileType_easbase("2"),
	/** 附件类型之单据的存储于FTP控件上的附件 */
	fileType_ftp("3"),
	/** 附件类型之单据的通过eas基础的Office控件内容 */
	fileType_office("4"),
	
	;
	private String value;
	
	AttachmentType(String value) {
		this.value = value;
	}
	
	public static AttachmentType getEnum(String value){
		if(value == null || value.trim().length()==0){
			return null;
		}
		for(AttachmentType e :AttachmentType.values()){
			if(e.getValue().equals(value)){
				return e;
			}
		}
		return null;
	}
	public String getValue() {
		return value;
	}
}
