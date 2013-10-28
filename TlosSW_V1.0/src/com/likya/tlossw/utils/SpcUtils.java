package com.likya.tlossw.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.common.JobCommandTypeDocument.JobCommandType;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.ScenarioDocument.Scenario;
import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument.TlosProcessData;
import com.likya.tlos.model.xmlbeans.dbconnections.DbConnectionProfileDocument.DbConnectionProfile;
import com.likya.tlos.model.xmlbeans.dbconnections.DbPropertiesDocument.DbProperties;
import com.likya.tlos.model.xmlbeans.dbjob.DbJobDefinitionDocument.DbJobDefinition;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpAdapterPropertiesDocument.FtpAdapterProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpPropertiesDocument.FtpProperties;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.core.cpc.model.SpcInfoType;
import com.likya.tlossw.core.spc.helpers.SortType;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.db.utils.DBUtils;
import com.likya.tlossw.exceptions.TlosException;
import com.likya.tlossw.exceptions.TlosFatalException;
import com.likya.tlossw.model.SpcLookupTable;
import com.likya.tlossw.model.path.TlosSWPathType;
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
	
	public static SpcLookupTable updateSpcLookupTable(String runId, TlosSWPathType tlosSWPathType, Logger myLogger) throws TlosException {

		TlosProcessData tlosProcessData = null;

		SpcLookupTable spcLookupTable = TlosSpaceWide.getSpaceWideRegistry().getRunLookupTable().get(runId).getSpcLookupTable();

		HashMap<String, SpcInfoType> table = spcLookupTable.getTable();

		/***/
		SpcInfoType spcInfoTypeOld = null;
		
		synchronized (table) {
			spcInfoTypeOld = table.remove(tlosSWPathType.getFullPath());
		}
		ArrayList<SortType> nonDailJobQueueIndex = spcInfoTypeOld.getSpcReferance().getNonDailyJobQueueIndex();
		HashMap<String, Job> jobQueueOld = spcInfoTypeOld.getSpcReferance().getJobQueue();
		Iterator<SortType> nonDailJobQueueIndexIterator = nonDailJobQueueIndex.iterator();
		/***/
		
		try {

			tlosProcessData = DBUtils.getTlosDailyData(new Long(tlosSWPathType.getId().getBaseId()).intValue(), Integer.parseInt(runId));

			Scenario myScenario = CpcUtils.getScenario(tlosProcessData);

			SpcInfoType spcInfoType = CpcUtils.prepareScenario(runId, tlosSWPathType, myScenario, myLogger);

			HashMap<String, Job> jobQueue = spcInfoType.getSpcReferance().getJobQueue();
			
			synchronized (table) {
				while (nonDailJobQueueIndexIterator.hasNext()) {
					SortType sortType = nonDailJobQueueIndexIterator.next();
					Job oldJob = jobQueueOld.get(sortType.getJobId());
					jobQueue.put(sortType.getJobId(), oldJob);
				}
			}

			synchronized (spcLookupTable) {
				table.put(tlosSWPathType.getFullPath(), spcInfoType);
			}

			myLogger.info("  > Senaryo yuklendi !");

		} catch (TlosFatalException e) {
			e.printStackTrace();
		}

		return spcLookupTable;
	}

		
}
