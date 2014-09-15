package com.gree.mobile.wf.category;

import java.io.File;

public class FileChangedReloadingStrategy {
//	private static Logger logger = Logger.getLogger(FileChangedReloadingStrategy.class);
	private long lastModified;
	private long lastChecked;
	private long lastLength;
	private long refreshDelay = 5000l;
	private boolean reloading;
	private File file;

	public FileChangedReloadingStrategy(File file) {
		this.file=file;
		this.reloading=true;
	}

	public boolean reloadingRequired() {
		if (!reloading) {
			long now = System.currentTimeMillis();
			if (now > lastChecked + refreshDelay ) {
				lastChecked = now;
				if (hasChanged()) {
					reloading = true;
				}
			}
		}
		return reloading;
	}

	private boolean hasChanged() {
		File file = getFile();
		if (file == null || !file.exists()) {
			return false;
		}
		return file.lastModified() > lastModified || lastLength!=file.length();
	}

	public void reloadingPerformed() {
		updateLastModified();
	}

	private void updateLastModified() {
		File file = getFile();
		if (file != null) {
			lastModified = file.lastModified();
			lastLength = file.length();
		}
		reloading = false;
	}
	
	public File getFile(){
		return file;
	}
}
