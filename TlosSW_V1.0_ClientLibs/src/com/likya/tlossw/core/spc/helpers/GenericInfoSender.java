package com.likya.tlossw.core.spc.helpers;

import com.likya.tlossw.core.spc.model.JobRuntimeProperties;

public interface GenericInfoSender {
	public void sendStatusChangeInfo(String messageId, JobRuntimeProperties jobRuntimeProperties);
	public void sendEndInfo(String messageId, JobRuntimeProperties jobRuntimeProperties);
}
