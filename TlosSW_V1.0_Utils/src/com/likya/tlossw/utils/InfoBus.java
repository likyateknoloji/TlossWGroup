package com.likya.tlossw.utils;

import com.likya.tlossw.infobus.helper.InfoType;

public interface InfoBus extends Runnable {
	public void addInfo(InfoType infoType);
	public void terminate(boolean forcedTerminate);
}
