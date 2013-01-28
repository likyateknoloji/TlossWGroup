package com.likya.tlossw.core.spc.jobs;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpAdapterPropertiesDocument.FtpAdapterProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.ProcessedFilesOperationTypeDocument.ProcessedFilesOperationType;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.utils.FileUtils;
import com.likya.tlossw.utils.FtpUtils;
import com.likya.tlossw.utils.GlobalRegistry;
import com.likya.tlossw.utils.LiveStateInfoUtils;
import com.likya.tlossw.utils.ParsingUtils;
import com.likya.tlossw.utils.date.DateUtils;

public class FtpPutFile extends FtpExecutor {

	private static final long serialVersionUID = 303869626344651260L;

	private Logger myLogger = Logger.getLogger(FtpPutFile.class);

	public FtpPutFile(GlobalRegistry spaceWideRegistry, Logger globalLogger, JobRuntimeProperties jobRuntimeProperties) {
		super(spaceWideRegistry, globalLogger, jobRuntimeProperties);
	}

	public void run() {

		initStartUp(myLogger);

		initializeFtpJob();
		
		JobProperties jobProperties = getJobRuntimeProperties().getJobProperties();
		FtpAdapterProperties ftpJobProperties = jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getSpecialParameters().getFtpAdapterProperties();

		while (true) {

			startWathcDogTimer();

			LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_RUNNING, SubstateName.INT_ON_RESOURCE, StatusName.INT_TIME_IN);
			sendStatusChangeInfo();

			try {
				boolean result = false;

				if (!checkLogin(myLogger)) {
					LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_FINISHED, SubstateName.INT_COMPLETED, StatusName.INT_FAILED);
					sendStatusChangeInfo();

					getOutputFile().close();

					return;
				}

				String sourceFileName = ftpJobProperties.getFilenameAndDirectory().getSourceFileName().getFilename();
				String targetFileName = ftpJobProperties.getFilenameAndDirectory().getTargetFileName().getFilename();

				String sourceDirectory = ftpJobProperties.getFilenameAndDirectory().getSourceDirectory().getPath();
				String targetDirectory = ftpJobProperties.getFilenameAndDirectory().getTargetDirectory().getPath();

				String sourceFile = ParsingUtils.getConcatenatedPathAndFileName(sourceDirectory, sourceFileName);
				String targetFile = ParsingUtils.getConcatenatedPathAndFileName(targetDirectory, targetFileName);

				if (ftpJobProperties.getOperation().getProcessedFilesOperationType() == ProcessedFilesOperationType.COPY) {
					result = FtpUtils.copyLocalFileToRemote(getFtpClient(), sourceFile, targetFile, getOutputFile(), myLogger);

				} else if (ftpJobProperties.getOperation().getProcessedFilesOperationType() == ProcessedFilesOperationType.MOVE) {
					result = FtpUtils.copyLocalFileToRemote(getFtpClient(), sourceFile, targetFile, getOutputFile(), myLogger);

					if (result) {
						result = FileUtils.deleteLocalFile(sourceFile, getOutputFile(), myLogger);
					}
				} else {
					myLogger.error("Write islemi icin processedFilesOperationType degeri Copy ya da Move olmalidir. Delete icin File Adapter kullanilabilir.");
					getOutputFile().write(DateUtils.getCurrentTimeWithMilliseconds() + " Write islemi icin processedFilesOperationType degeri Copy ya da Move olmalidir. Delete icin File Adapter kullanilabilir." + System.getProperty("line.separator"));

					result = false;
				}

				LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_FINISHED, SubstateName.INT_COMPLETED, (result == true ? StatusName.INT_SUCCESS : StatusName.INT_FAILED));
				sendStatusChangeInfo();
				
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

				LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_FINISHED, SubstateName.INT_COMPLETED, StatusName.INT_FAILED);
				sendStatusChangeInfo();
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
