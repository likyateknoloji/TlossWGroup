package com.likya.tlossw.utils;

import com.likya.tlossw.model.infobus.InfoType;

public interface InfoBus extends Runnable {
	public void addInfo(InfoType infoType);
	public void terminate(boolean forcedTerminate);
}
