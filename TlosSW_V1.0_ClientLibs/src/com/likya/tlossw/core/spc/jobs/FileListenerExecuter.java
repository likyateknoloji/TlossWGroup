package com.likya.tlossw.core.spc.jobs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.impl.DefaultFileMonitor;
import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.listener.FileListenerDocument.FileListener;
import com.likya.tlos.model.xmlbeans.listener.FileNameTypeDocument.FileNameType;
import com.likya.tlos.model.xmlbeans.listener.PollingTypeDocument.PollingType;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.core.spc.helpers.CustomFileListener;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.utils.GlobalRegistry;
import com.likya.tlossw.utils.LiveStateInfoUtils;
import com.likya.tlossw.utils.ParsingUtils;
import com.likya.tlossw.utils.date.DateUtils;

public class FileListenerExecuter extends FileJob {

	private static final long serialVersionUID = 244898058920367389L;

	private Logger myLogger = Logger.getLogger(FileListenerExecuter.class);

	private Writer outputFile = null;

	private boolean retryFlag = true;

	transient protected Process process;

	public FileListenerExecuter(GlobalRegistry globalRegistry, Logger globalLogger, JobRuntimeProperties jobRuntimeProperties) {
		super(globalRegistry, globalLogger, jobRuntimeProperties);
	}

	@Override
	public void localRun() {

		initStartUp(myLogger);

		JobProperties jobProperties = getJobRuntimeProperties().getJobProperties();

		FileListener fileListener = jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getSpecialParameters().getFileListener();

		while (true) {

			try {

				startWathcDogTimer();

				String logFilePath = jobProperties.getBaseJobInfos().getJobLogPath();
				String logFileName = jobProperties.getBaseJobInfos().getJobLogFile().substring(0, jobProperties.getBaseJobInfos().getJobLogFile().indexOf('.')) + "_" + DateUtils.getCurrentTimeForFileName() + jobProperties.getBaseJobInfos().getJobLogFile().substring(jobProperties.getBaseJobInfos().getJobLogFile().indexOf('.'), jobProperties.getBaseJobInfos().getJobLogFile().length());

				String logFile = ParsingUtils.getConcatenatedPathAndFileName(logFilePath, logFileName);

				LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_RUNNING, SubstateName.INT_ON_RESOURCE, StatusName.INT_TIME_IN);

				sendStatusChangeInfo();

				try {
					outputFile = new BufferedWriter(new FileWriter(logFile));

					outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Dosya dinleme isi baslatildi !" + System.getProperty("line.separator"));

				} catch (IOException e) {
					handleLogException(e, myLogger);
				}

				//TODO log dosyasinin ismine zaman damgasi verildigi icin bu ismi dailyScenarios.xml'de guncellemek gerekiyor
				//DBUtils.insertLogFileNameForJob(jobProperties, jobPath, logFileName);

				jobProperties.getBaseJobInfos().setJobLogFile(logFileName);

				String fileName = null, directory = null;

				FileObject listendir = null;

				try {
					FileSystemManager fsManager = VFS.getManager();

					directory = fileListener.getPollingProperties().getTriggerFileDirectory();

					if (directory.charAt(directory.length() - 1) == '/' || directory.charAt(directory.length() - 1) == '\\') {
						directory = directory.substring(0, directory.length() - 1);
					}

					fileName = fileListener.getPollingProperties().getTriggerFile();

					listendir = fsManager.resolveFile(directory);

				} catch (FileSystemException e) {
					handleException(e, myLogger);
				}

				CustomFileListener customFileListener = new CustomFileListener();

				DefaultFileMonitor fileMonitor = new DefaultFileMonitor(customFileListener);
				fileMonitor.setRecursive(false);
				fileMonitor.addFile(listendir);

				customFileListener.setFileListener(fileListener);
				customFileListener.setOutputFile(outputFile);

				if (fileListener.getPollingProperties().getPollingType() == PollingType.CREATE) {

					if (fileListener.getFileNameType() == FileNameType.FULL_TEXT) {

						File file = new File(directory + File.separator + fileName);

						if (file.exists()) {
							myLogger.info("\"" + directory + File.separator + fileName + "\" exists");

							try {
								outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + "\"" + directory + File.separator + fileName + "\" zaten mevcut" + System.getProperty("line.separator"));
							} catch (IOException e) {
								handleLogException(e, myLogger);
							}

							//dosya zaten varsa sadece loga yazdiriyor dosyanin create edilmesi eventini yine bekliyor
						}
					}
				}

				fileMonitor.start();

				try {
					outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Dosya dinleme aktiflestirildi !" + System.getProperty("line.separator"));
				} catch (IOException e) {
					handleLogException(e, myLogger);
				}

				boolean executionPermission = true;

				while (executionPermission) {

					try {
						if (customFileListener.repetationNumberExceeded()) {
							executionPermission = false;
							continue;
						} else {

							//bu kisimda zaman kontrolunde sorun var duzeltilecek
							//					//bu is icin bitis zamani tanimlandiysa ve bitis zamani gectiyse
							//					if(jobProperties.getTimeManagement().getJsPlannedTime() != null) {
							//						
							//						StopTime stopTime = jobProperties.getTimeManagement().getJsPlannedTime().getStopTime();
							//						
							//						if(stopTime != null && DateUtils.normalizeDate(stopTime.getTime()).before(Calendar.getInstance())) {
							//						
							//							//en az bir kere tetiklenmediyse false donuyor
							//							if(customFileListener.getRepetationNumber() == 0) {
							//								return false;
							//							}
							//						}
							//					}
						}

						long frequency = fileListener.getPollingProperties().getPollingFrequency().longValue();

						synchronized (fileMonitor) {
							fileMonitor.wait(frequency * 1000);
						}

					} catch (InterruptedException e) {
						handleException(e, myLogger);

						LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_FINISHED, SubstateName.INT_COMPLETED, StatusName.INT_FAILED);
						sendStatusChangeInfo();
					}
				}

				fileMonitor.stop();

				try {
					outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Dosya dinleme isi bitti. Tekrar sayisi : " + fileListener.getRepetationNumber());
					outputFile.close();

				} catch (IOException e) {
					handleLogException(e, myLogger);
				}

				LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_FINISHED, SubstateName.INT_COMPLETED, StatusName.INT_SUCCESS);
				sendStatusChangeInfo();

			} catch (Exception err) {
				handleException(err, myLogger);

				try {
					outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Dosya dinleme isinde hata !" + System.getProperty("line.separator"));
					outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " " + err.getMessage() + System.getProperty("line.separator"));

					for (StackTraceElement element : err.getStackTrace()) {
						outputFile.write("\t" + element.toString() + System.getProperty("line.separator"));
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
			outputFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Logger getMyLogger() {
		return myLogger;
	}

	public void setMyLogger(Logger myLogger) {
		this.myLogger = myLogger;
	}

	public Writer getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(Writer outputFile) {
		this.outputFile = outputFile;
	}

}
