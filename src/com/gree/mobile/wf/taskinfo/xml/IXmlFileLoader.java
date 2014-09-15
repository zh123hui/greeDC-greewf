package com.gree.mobile.wf.taskinfo.xml;

import java.io.File;


public interface IXmlFileLoader {

	public TaskInfoNode load(File file) throws Exception;
}
