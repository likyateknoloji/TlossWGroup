package com.likya.tlossw.core.spc.jobs;

import org.apache.log4j.Logger;

import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.utils.GlobalRegistry;

public abstract class SQLScriptExecuter extends ExecuteInShell {

	private static final long serialVersionUID = 4355239050595840659L;

	public SQLScriptExecuter(GlobalRegistry globalRegistry, Logger globalLogger, JobRuntimeProperties jobRuntimeProperties) {
		super(globalRegistry, globalLogger, jobRuntimeProperties);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void localRun() {
	}

}
