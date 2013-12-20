package com.likya.tlossw.core.spc.jobs;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpAdapterPropertiesDocument.FtpAdapterProperties;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.utils.FtpUtils;
import com.likya.tlossw.utils.GlobalRegistry;
import com.likya.tlossw.utils.date.DateUtils;

public class FtpListRemoteFiles extends FtpExecutor {

	private static final long serialVersionUID = 303869626344651260L;

	private Logger myLogger = Logger.getLogger(FtpListRemoteFiles.class);

	public FtpListRemoteFiles(GlobalRegistry spaceWideRegistry, Logger globalLogger, JobRuntimeProperties jobRuntimeProperties) {
		super(spaceWideRegistry, globalLogger, jobRuntimeProperties);
	}

	@Override
	public void localRun() {

		initStartUp(myLogger);

		initializeFtpJob();

		JobProperties jobProperties = getJobRuntimeProperties().getJobProperties();
		FtpAdapterProperties ftpJobProperties = jobProperties.getBaseJobInfos().getJobTypeDetails().getSpecialParameters().getFtpAdapterProperties();

		while (true) {

			startWathcDogTimer();

			insertNewLiveStateInfo(StateName.INT_RUNNING, SubstateName.INT_ON_RESOURCE, StatusName.INT_TIME_IN);

			try {
				if (!checkLogin(myLogger)) {
					insertNewLiveStateInfo(StateName.INT_FINISHED, SubstateName.INT_COMPLETED, StatusName.INT_FAILED);

					getOutputFile().close();

					return;
				}

				String directory = ftpJobProperties.getFilenameAndDirectory().getSourceDirectory().getPath();

				FTPFile[] fileList = FtpUtils.listRemoteFiles(getFtpClient(), directory, getOutputFile(), myLogger);

				fileList.toString();

				insertNewLiveStateInfo(StateName.INT_FINISHED, SubstateName.INT_COMPLETED, StatusName.INT_SUCCESS);

			} catch (Exception e) {

				myLogger.error("Ftp hatasi !");

				try {
					getOutputFile().write(DateUtils.getCurrentTimeWithMilliseconds() + " Ftp hatasi !" + System.getProperty("line.separator"));
					getOutputFile().write(DateUtils.getCurrentTimeWithMilliseconds() + " " + e.getMessage() + System.getProperty("line.separator"));

					for (StackTraceElement element : e.getStackTrace()) {
						getOutputFile().write("\t" + element.toString() + System.getProperty("line.separator"));
					}

				} catch (IOException ioe) {
					ioe.printStackTrace();
				}

				insertNewLiveStateInfo(StateName.INT_FINISHED, SubstateName.INT_COMPLETED, StatusName.INT_FAILED, e.getMessage());

			}

			if (processJobResult(retryFlag, myLogger)) {
				retryFlag = false;
				continue;
			}

			break;
		}

		cleanUp(process, myLogger);

		try {
			getOutputFile().close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return;

	}

}
