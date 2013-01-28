package com.likya.tlossw.core.spc.jobs;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.utils.GlobalRegistry;
import com.likya.tlossw.utils.LiveStateInfoUtils;

public class ExecuteAsProcess extends ExecuteOSComponent {

	private static final long serialVersionUID = 5418701682539917545L;

	Logger myLogger = Logger.getLogger(ExecuteInShell.class);

	private boolean retryFlag = true;

	public ExecuteAsProcess(GlobalRegistry globalRegistry, Logger globalLogger, JobRuntimeProperties jobRuntimeProperties) {
		super(globalRegistry, globalLogger, jobRuntimeProperties);
	}

	public void run() {

		initStartUp(myLogger);

		JobProperties jobProperties = getJobRuntimeProperties().getJobProperties();

		while (true) {

			try {

				startWathcDogTimer();

				String jobPath = jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getJobPath();
				String jobCommand = jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getJobCommand();

				LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_RUNNING, SubstateName.INT_ON_RESOURCE, StatusName.INT_TIME_IN);

				sendStatusChangeInfo();

				// TODO Burasý eksik kaldý, 3. parametre tamamlanmalý
				// Serkan Taþ 13.08.2012
				// Map<String, String> env = new HashMap<String, String>();
				// env.put("PGPASSWORD", password);

				startNativeProcess(jobPath, jobCommand, null, this.getClass().getName(), myLogger);

			} catch (Exception err) {
				handleException(err, myLogger);
			}

			sendStatusChangeInfo();

			if (processJobResult(retryFlag, myLogger)) {
				retryFlag = false;
				continue;
			}

			break;
		}

		cleanUp(getProcess(), myLogger);

	}

}
