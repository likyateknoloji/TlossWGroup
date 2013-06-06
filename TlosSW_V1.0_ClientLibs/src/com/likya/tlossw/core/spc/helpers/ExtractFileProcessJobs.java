package com.likya.tlossw.core.spc.helpers;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.fileadapter.FileAdapterPropertiesDocument.FileAdapterProperties;
import com.likya.tlos.model.xmlbeans.fileadapter.OperationTypeDocument.OperationType;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.core.spc.jobs.ReadLocalFileProcess;
import com.likya.tlossw.core.spc.jobs.WriteLocalFileProcess;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.utils.GlobalRegistry;
import com.likya.tlossw.utils.TypeUtils;

public class ExtractFileProcessJobs {

	public static Job evaluate(GlobalRegistry globalRegistry, JobRuntimeProperties jobRuntimeProperties, Job myJob, Logger gobalLogger) {

		FileAdapterProperties fileAdapterProperties = TypeUtils.resolveFileAdapterProperties(jobRuntimeProperties.getJobProperties());

		int fileProcessOperationType = fileAdapterProperties.getOperation().getOperationType().intValue();

		switch (fileProcessOperationType) {

		case OperationType.INT_READ_FILE:
			myJob = new ReadLocalFileProcess(globalRegistry, gobalLogger, jobRuntimeProperties);
			break;

		case OperationType.INT_WRITE_FILE:
			myJob = new WriteLocalFileProcess(globalRegistry, gobalLogger, jobRuntimeProperties);
			break;

		case OperationType.INT_LIST_FILES:
			// TODO
			break;

		case OperationType.INT_INSERT_RECORD:
			// TODO
			break;

		case OperationType.INT_UPDATE_RECORD:
			// TODO
			break;

		case OperationType.INT_DELETE_RECORD:
			// TODO
			break;

		default:
			break;
		}
		
		
		return myJob;

	}

}
