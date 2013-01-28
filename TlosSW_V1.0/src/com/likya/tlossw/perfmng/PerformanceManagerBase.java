package com.likya.tlossw.perfmng;

import com.likya.tlossw.utils.SpaceWideRegistry;

public abstract class PerformanceManagerBase implements Runnable {

	private static boolean isPermitted = true;

	private SpaceWideRegistry spaceWideRegistry;

	public PerformanceManagerBase(SpaceWideRegistry spaceWideRegistry) {
		this.spaceWideRegistry = spaceWideRegistry;
	}

	protected boolean executionPermission = true;

	transient private Thread executerThread;

	public void setExecutionPermission(boolean executionPermission) {
		synchronized (this) {
			this.executionPermission = executionPermission;
		}

	}

	public SpaceWideRegistry getSpaceWideRegistry() {
		return spaceWideRegistry;
	}

	public boolean isPermitted() {
		return isPermitted;
	}

	public void setPermitted(boolean isPermitted) {
		PerformanceManagerBase.isPermitted = isPermitted;
	}

	public Thread getExecuterThread() {
		return executerThread;
	}

	public void setExecuterThread(Thread executerThread) {
		this.executerThread = executerThread;
	}
}
