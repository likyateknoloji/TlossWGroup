package com.likya.tlossw.core.spc.helpers;

import com.likya.tlos.model.xmlbeans.common.JobCommandTypeDocument.JobCommandType;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.dbconnections.DbConnectionProfileDocument.DbConnectionProfile;
import com.likya.tlos.model.xmlbeans.dbconnections.DbPropertiesDocument.DbProperties;
import com.likya.tlos.model.xmlbeans.dbjob.DbJobDefinitionDocument.DbJobDefinition;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpAdapterPropertiesDocument.FtpAdapterProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpPropertiesDocument.FtpProperties;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.utils.TypeUtils;

public class SpcUtils {

	public static JobProperties getJobPropertiesWithSpecialParameters(JobRuntimeProperties jobRuntimeProperties) {

		JobProperties jobProperties = jobRuntimeProperties.getJobProperties();

		int jobType = jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getJobCommandType().intValue();

		switch (jobType) {
		case JobCommandType.INT_FTP:

			FtpProperties ftpProperties = jobRuntimeProperties.getFtpProperties();

			FtpAdapterProperties adapterProperties = TypeUtils.resolveFtpAdapterProperties(jobProperties);
			adapterProperties.getRemoteTransferProperties().setFtpProperties(ftpProperties);

			break;

		case JobCommandType.INT_DB_JOBS:
			// TODO db joblari ile ilgili ayarlama yapilacak

			DbJobDefinition dbJobDefinition = TypeUtils.resolveDbJobDefinition(jobProperties);

			DbProperties dbProperties = jobRuntimeProperties.getDbProperties();
			DbConnectionProfile dbConnectionProfile = jobRuntimeProperties.getDbConnectionProfile();
			dbJobDefinition.setDbProperties(dbProperties);
			dbJobDefinition.setDbConnectionProfile(dbConnectionProfile);

			break;

		default:
			break;
		}

		return jobProperties;
	}
}
