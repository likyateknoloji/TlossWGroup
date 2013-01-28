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

public class FtpGetFile extends FtpExecutor {

	private static final long serialVersionUID = 303869626344651260L;

	private Logger myLogger = Logger.getLogger(FtpGetFile.class);

	public FtpGetFile(GlobalRegistry globalRegistry, Logger globalLogger, JobRuntimeProperties jobRuntimeProperties) {
		super(globalRegistry, globalLogger, jobRuntimeProperties);
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
					result = FtpUtils.copyRemoteFileToLocal(getFtpClient(), sourceFile, targetFile, getOutputFile(), myLogger);

				} else if (ftpJobProperties.getOperation().getProcessedFilesOperationType() == ProcessedFilesOperationType.MOVE) {
					result = FtpUtils.copyRemoteFileToLocal(getFtpClient(), sourceFile, targetFile, getOutputFile(), myLogger);

					if (result) {
						result = FtpUtils.deleteRemoteFile(getFtpClient(), sourceFile, getOutputFile(), myLogger);
					}
				} else if (ftpJobProperties.getOperation().getProcessedFilesOperationType() == ProcessedFilesOperationType.DELETE) {
					result = FtpUtils.deleteRemoteFile(getFtpClient(), sourceFile, getOutputFile(), myLogger);
				} else {
					myLogger.error("Read islemi icin processedFilesOperationType degeri Copy, Move ya da Delete olmalidir");
					getOutputFile().write(DateUtils.getCurrentTimeWithMilliseconds() + " Read islemi icin processedFilesOperationType degeri Copy, Move ya da Delete olmalidir" + System.getProperty("line.separator"));
				}

				LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_FINISHED, SubstateName.INT_COMPLETED, (result == true ? StatusName.INT_SUCCESS : StatusName.INT_FAILED));
				sendStatusChangeInfo();

				//kopyalama islemi yapamadiginda yerel dizinde istenilen isimdeki dosyayi bos olarak olusturdugu icin hata olursa silecek
				if (!result) {
					FileUtils.deleteLocalFile(targetFile, getOutputFile(), myLogger);
				}

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
