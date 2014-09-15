package com.gree.mobile.wf.enums;

public enum ResponeStreamEnum {
	
	asf("asf","video/x-ms-asf"),
	avi("avi","video/x-msvideo"),
	bmp("bmp","image/bmp"),
	css("css","text/css"),
	doc("doc","application/msword"),
	gif("gif","image/gif"),
	htm("htm","text/html"),
	html("html","text/html"),
	ico("ico","image/x-icon"),
	jpe("jpe","image/jpeg"),
	jpeg("jpeg","image/jpeg"),
	jpg("jpg","image/jpeg"),
	png("png","image/png"),
	js("js","application/x-javascript"),
	mov("mov","video/quicktime"),
	movie("movie","video/x-sgi-movie"),
	mp3("mp3","audio/mpeg"),
	mpe("mpe","video/mpeg"),
	mpeg("mpeg","video/mpeg"),
	mpg("mpg","video/mpeg"),
	mpp("mpp","application/vnd.ms-project"),
	pdf("pdf","application/pdf"),
	ppt("ppt","application/vnd.ms-powerpoint"),
	swf("swf","application/x-shockwave-flash"),
	txt("txt","text/plain"),
	xls("xls","application/vnd.ms-excel"),
	java("java","text/plain"),
	zip("zip","application/zip"),
	rar("rar","application/zip"),
	docx("docx","application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
	pptx("pptx","application/vnd.openxmlformats-officedocument.presentationml.presentation"),
	xlsx("xlsx","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
	
	DEFAUL("default", "application/octet-stream");
	
	private String fileExt;
	private String mime;

	public static ResponeStreamEnum getEnum(String fileExt){
		if(fileExt == null || fileExt.trim().length()==0){
			return DEFAUL;
		}
		for(ResponeStreamEnum e : ResponeStreamEnum.values()){
			if(e.getFileExt().equals(fileExt)){
				return e;
			}
		}
		return DEFAUL;
	}
	
	private ResponeStreamEnum(String fileExt, String mime) {
		this.fileExt = fileExt;
		this.mime = mime;
	}

	public String getFileExt() {
		return fileExt;
	}

	public String getMime() {
		return mime;
	}
	
}
