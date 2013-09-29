package com.likya.tlossw.model;

import java.io.Serializable;

public class WebSpaceWideRegistery implements Serializable {

	private static final long serialVersionUID = 6058162636775782038L;

	private boolean waitConfirmOfGUI = false;
	private boolean isFirstTime = false;
	private boolean isPersistent = false;
	private int instanceCount = 0;
	
	private DocMetaDataHolder docMetaDataInfo;
	
	public WebSpaceWideRegistery() {
		this.docMetaDataInfo = new DocMetaDataHolder();
	}

	public boolean getWaitConfirmOfGUI() {
		return waitConfirmOfGUI;
	}

	public void setWaitConfirmOfGUI(boolean waitConfirmOfGUI) {
		this.waitConfirmOfGUI = waitConfirmOfGUI;
	}

	public boolean getFirstTime() {
		return isFirstTime;
	}

	public void setFirstTime(boolean isFirstTime) {
		this.isFirstTime = isFirstTime;
	}

	public boolean getPersistent() {
		return isPersistent;
	}

	public void setPersistent(boolean isPersistent) {
		this.isPersistent = isPersistent;
	}

	public int getInstanceCount() {
		return instanceCount;
	}

	public void setInstanceCount(int instanceCount) {
		this.instanceCount = instanceCount;
	}

	public DocMetaDataHolder getDocMetaDataInfo() {
		return docMetaDataInfo;
	}

	public void setDocMetaDataInfo(DocMetaDataHolder docMetaDataInfo) {
		this.docMetaDataInfo = docMetaDataInfo;
	}


}
