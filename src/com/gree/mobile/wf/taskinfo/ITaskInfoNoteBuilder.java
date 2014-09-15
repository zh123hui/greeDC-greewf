package com.gree.mobile.wf.taskinfo;

import com.gree.mobile.wf.taskinfo.xml.TaskInfoNode;
import com.kingdee.bos.util.BOSObjectType;

public interface ITaskInfoNoteBuilder {

	public TaskInfoNode build(BOSObjectType bosType);
}
