package com.gree.mobile.wf.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.gree.mobile.common.permission.UserContext;
import com.gree.mobile.common.permission.UserContextManager;
import com.gree.mobile.common.web.action.JsonActionSupport;
import com.gree.mobile.common.web.exception.JsonActionException;
import com.gree.mobile.wf.enums.AttachmentType;
import com.gree.mobile.wf.enums.ResponeStreamEnum;
import com.gree.mobile.wf.enums.TaskTypeEnum;
import com.kingdee.bos.BOSException;
import com.kingdee.bos.Context;
import com.kingdee.bos.dao.IObjectValue;
import com.kingdee.bos.dao.ormapping.ObjectUuidPK;
import com.kingdee.bos.framework.DynamicObjectFactory;
import com.kingdee.bos.framework.IDynamicObject;
import com.kingdee.bos.olap.util.ByteArrayInputStream;
import com.kingdee.bos.util.BOSUuid;
import com.kingdee.eas.base.attachment.AttachmentFactory;
import com.kingdee.eas.base.attachment.AttachmentInfo;
import com.kingdee.eas.base.attachment.IAttachment;
import com.kingdee.eas.base.attachment.ftp.AttachmentDownloadServer;
import com.kingdee.eas.base.fme.bo.DocumentBO;
import com.kingdee.eas.base.fme.service.FMEServiceFactory;
import com.kingdee.eas.base.fme.service.IFMEService;
import com.kingdee.eas.base.fme.service.ServiceResult;
import com.kingdee.eas.base.fme.util.FMEUtils;
import com.kingdee.eas.common.EASBizException;
import com.kingdee.eas.framework.CoreBaseInfo;

public class DownloadAttachmentAction extends JsonActionSupport {
	private static final Logger logger = Logger
			.getLogger(DownloadAttachmentAction.class);
	private String fkey;
	private String fname;
	private String fext;
	private int taskType; // 流程类型,1表示待办流程,2表示已办流程,3表示在办流程

	@Override
	public String execute() {
		if (TaskTypeEnum.getEnum(taskType) == null) {
			logger.error(String.format("taskType值[%s]不正确", taskType));
			this.error(String.format("taskType值[%s]不正确", taskType));
			return JSON;
		}
		try {
			downloadAttachment();
		} catch (JsonActionException e) {
			logger.error(e.getMessage(), e);
			this.error(e.getMessage());
			return JSON;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			this.error("下载失败");
			return JSON;
		}
		return NONE;
	}

	private void downloadAttachment() throws JsonActionException, Exception {
		String[] keyArr = fkey.split("\\!");
		String attachType = keyArr[0]; // 附件类型
		AttachmentType enum1 = AttachmentType.getEnum(attachType);
		if (enum1 == null) {
			throw new JsonActionException("附件key值不正确,附件类型不正确");
		}
		UserContext uc = UserContextManager.getUserContext(getSession());
		Context ctx = uc.getBosContext();
		switch (enum1) {
		case fileType_content: {
			download1(keyArr);
			break;
		}
		case fileType_easbase: {
			download2(keyArr, ctx);
			break;
		}
		case fileType_ftp: {
			download3(keyArr, ctx);
			break;
		}
		case fileType_office: {
			download4(keyArr, ctx);
			break;
		}
		}
	}

	private void download4(String[] keyArr, Context ctx)
			throws JsonActionException, BOSException, EASBizException,
			Exception {
		if (keyArr.length < 2) {
			throw new JsonActionException("fileType_office附件key值不正确");
		}
		String attachID = keyArr[1];
		com.kingdee.eas.base.form.extend.attachment.IAttachment facade = com.kingdee.eas.base.form.extend.attachment.AttachmentFactory
				.getRemoteInstanceWithObjectContext(ctx);
		com.kingdee.eas.base.form.extend.attachment.AttachmentInfo attachInfo = facade
				.getAttachmentInfo(new ObjectUuidPK(BOSUuid.read(attachID)));
		if (attachInfo == null) {
			throw new JsonActionException("fileType_office附件对象为空");
		}
		byte[] fileData = attachInfo.getFileData();
		if (fileData == null) {
			throw new JsonActionException("fileType_office附件数值为空");
		}
		outputBytes(getResponse(), fileData, attachInfo.getName(), attachInfo
				.getExtName(), "utf-8");
	}

	private void download3(String[] keyArr, Context ctx)
			throws JsonActionException, Exception {
		if (keyArr.length < 3) {
			throw new JsonActionException("fileType_ftp附件key值不正确");
		}
		String bizObjectId = keyArr[1]; // 单据ID
		String docId = keyArr[2];
		IFMEService serviceInstance = FMEServiceFactory.getServiceInstance(ctx);
		DocumentBO document = serviceInstance.getDocumentByDocumentID(
				bizObjectId, docId);
		HttpServletRequest request = ServletActionContext.getRequest();
		ServiceResult result = serviceInstance.downloadFileInServer(
				bizObjectId, docId, request);
		// 从FTP下载文件
		// logger.info("从FTP下载文件结果: "+result.getResultMessge());
		if (result.isResult()) {
			String downloadFilePathName = result.getResultDetail();
			File file = new File(downloadFilePathName);
			if (file.exists()) {
				// FileInputStream fileInputStream = new FileInputStream(file);
				String documentName = document.getDisplayName();
				String documentExtName = FMEUtils.getFileExtName(documentName);
				outputFile(getResponse(), file, documentName, documentExtName);
			} else {
				throw new JsonActionException("下载附件到本地失败,文件不存在!");
			}
		} else {
			throw new JsonActionException("下载附件到本地失败");
		}
	}

	private void download2(String[] keyArr, Context ctx)
			throws JsonActionException, BOSException, EASBizException,
			UnsupportedEncodingException, Exception {
		if (keyArr.length < 2) {
			throw new JsonActionException("fileType_easbase附件key值不正确");
		}
		// eas 通用附件存储
		String attachID = keyArr[1]; // 附件ID
		BOSUuid id = BOSUuid.read(attachID);
		IAttachment iAttachment = AttachmentFactory.getRemoteInstance();
		AttachmentInfo attachInfo = (AttachmentInfo) iAttachment
				.getValue(new ObjectUuidPK(id));
		if (attachInfo == null) {
			throw new JsonActionException("fileType_easbase附件对象为空");
		}
		byte[] fileBitArr = attachInfo.getFile();
		if (fileBitArr == null) {
			try {
				Method m = AttachmentDownloadServer.class.getMethod(
						"getFileFromFtp", Context.class, String.class);
				AttachmentDownloadServer downServer = new AttachmentDownloadServer();
				Object invoke = m.invoke(downServer, ctx, attachID);
				if (invoke instanceof byte[]) {
					fileBitArr = (byte[]) invoke;
				}
			} catch (Exception e) {
				throw new JsonActionException("尝试从FTP下载附件失败", e);
			}
			if (fileBitArr == null) {
				throw new JsonActionException("fileType_easbase附件数值为空");
			}
		}
		String encoding = null;
		if ("html".equalsIgnoreCase(fext)) {
			encoding = "utf-8";
		}
		String name = attachInfo.getName();
		if ("url".equalsIgnoreCase(fext)) {
			encoding = "utf-8";
			String url = null;
			if (name.startsWith("http")) {
				url = name;
			} else {
				url = "http://" + name;
			}
			fileBitArr = ("<a href=" + url + " target=\"_blank\">" + name + "</a>")
					.getBytes("utf-8");
			fext = "html";
		}
		outputBytes(getResponse(), fileBitArr, name, fext, encoding);
	}

	private void download1(String[] keyArr) throws JsonActionException,
			BOSException, Exception {
		if (keyArr.length < 3) {
			throw new JsonActionException("fileType_content附件key值不正确");
		}
		// 附件存放在业务表中的某个字段
		String bizObjectId = keyArr[1]; // 单据ID
		String property = keyArr[2]; // 单据中存放附件字段的属性名
		BOSUuid id = BOSUuid.read(bizObjectId);
		IDynamicObject dynamicObject = DynamicObjectFactory.getRemoteInstance();
		IObjectValue dataCol = dynamicObject.getValue(id.getType(),
				(new StringBuffer("where id = '")).append(id.toString())
						.append("'").toString());
		// 单据对象
		if (!(dataCol instanceof CoreBaseInfo)) {
			throw new JsonActionException("找不到对应的单据对象");
		}
		CoreBaseInfo baseInfo = (CoreBaseInfo) dataCol;
		byte[] fileBitArr = baseInfo.getBytes(property);

		if (fileBitArr == null) {
			throw new JsonActionException("fileType_content附件内容为空");
		}
		if (fname == null || fname.equals(""))
			fname = "fileName";
		if (fext == null || fext.equals(""))
			fext = "fileExt";
		outputBytes(getResponse(), fileBitArr, fname, fext, "utf-8");
	}

	private String getMIMEfromFileExt(String fileExt) {
		return ResponeStreamEnum.getEnum(fileExt).getMime();
	}

	private String getTextCharset(byte[] fileBitArr) {
		byte utf8[] = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
		if (fileBitArr[0] == utf8[0] && fileBitArr[1] == utf8[1]
				&& fileBitArr[2] == utf8[2])
			return "UTF-8";
		else
			return "GBK";
	}

	private void outputBytes(HttpServletResponse resp, byte[] bytes,
			String fileName, String fileExt, String encoding) throws Exception {
		output(resp, new ByteArrayInputStream(bytes), fileName, fileExt, null);
	}

	private void outputFile(HttpServletResponse resp, File inputfile,
			String fileName, String fileExt) throws Exception {
		output(resp, new FileInputStream(inputfile), fileName, fileExt, null);
	}

	private void output(HttpServletResponse resp, InputStream input,
			String fileName, String fileExt, String encoding) throws Exception {
		String mime = getMIMEfromFileExt(fileExt);
		if (mime.startsWith("text")) {
			resp.addHeader("Content-Type", mime);
			try {
				byte[] headerBytes = null;
				if (encoding == null) {
					headerBytes = new byte[3];
					int read = input.read(headerBytes);
					if (read != -1) {
						encoding = getTextCharset(headerBytes);
					}
				}
				StringBuffer sb = new StringBuffer();
				sb.append("<html><head>");
				sb
						.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset="
								+ encoding + "\">");
				sb.append("</head><body>");
				InputStream extraInput = IOUtils.toInputStream(sb.toString(),
						encoding);

				OutputStream output = resp.getOutputStream();
				IOUtils.copy(extraInput, output);
				if (headerBytes != null) {
					IOUtils.copy(new ByteArrayInputStream(headerBytes), output);
				}
				IOUtils.copy(input, output);
				sb = new StringBuffer();
				sb.append("</body></html>");
				extraInput = IOUtils.toInputStream(sb.toString(), encoding);
				IOUtils.copy(extraInput, output);
			} finally {
				IOUtils.closeQuietly(input);
			}
			return;
		}

		String file = fileName + "." + fileExt;
		String fn = file;
		try {
			fn = new String(file.getBytes("utf-8"), "ISO-8859-1");
		} catch (UnsupportedEncodingException e1) {
			logger.error(e1.getMessage(), e1);
		}
		resp.addHeader("Content-disposition", "attachment;filename=" + fn);
		String contentType = null;

		if (mime.startsWith("text")) {
			contentType = mime + ";charset=utf-8";
		} else {
			contentType = mime;
		}
		resp.addHeader("Content-Type", contentType);
		resp.setContentType(contentType);
		try {
			IOUtils.copy(input, resp.getOutputStream());
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

	public void setFkey(String fkey) {
		this.fkey = fkey;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public void setFext(String fext) {
		this.fext = fext;
	}

	public void setTaskType(int taskType) {
		this.taskType = taskType;
	}
}
