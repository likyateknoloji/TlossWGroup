package com.likya.tlossw.core.spc.jobs;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.fileadapter.FileAdapterPropertiesDocument.FileAdapterProperties;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.core.spc.helpers.ParamList;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.utils.FileUtils;
import com.likya.tlossw.utils.GlobalRegistry;
import com.likya.tlossw.utils.LiveStateInfoUtils;
import com.likya.tlossw.utils.ParsingUtils;
import com.likya.tlossw.utils.date.DateUtils;

public class ReadLocalFileProcess extends FileProcessExecuter {

	private static final long serialVersionUID = 2222391052144828990L;

	private Logger myLogger = Logger.getLogger(ReadLocalFileProcess.class);

	public ReadLocalFileProcess(GlobalRegistry globalRegistry, Logger globalLogger, JobRuntimeProperties jobRuntimeProperties) {
		super(globalRegistry, globalLogger, jobRuntimeProperties);
	}

	public void localRun() {

		initStartUp(myLogger);

		initializeFileProcessJob();

		JobProperties jobProperties = getJobRuntimeProperties().getJobProperties();
		FileAdapterProperties fileProcessProperties = jobProperties.getBaseJobInfos().getJobTypeDetails().getSpecialParameters().getFileAdapterProperties();

		ArrayList<ParamList> myParamList = new ArrayList<ParamList>();

		while (true) {

			startWathcDogTimer();

			LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_RUNNING, SubstateName.INT_ON_RESOURCE, StatusName.INT_TIME_IN);

			try {

				String sourceFileName = fileProcessProperties.getFilenameAndDirectory().getSourceFileName().getFilename();
				String sourceDirectory = fileProcessProperties.getFilenameAndDirectory().getSourceDirectory().getPath();

				String sourceFile = ParsingUtils.getConcatenatedPathAndFileName(sourceDirectory, sourceFileName);

				String fileContent = FileUtils.readFile(sourceFile).toString();

				ParamList thisParam = new ParamList(READ_FILE_RESULT, "STRING", "VARIABLE", fileContent);
				myParamList.add(thisParam);

				LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_FINISHED, SubstateName.INT_COMPLETED, StatusName.INT_SUCCESS);

				try {
					getOutputFile().write(DateUtils.getCurrentTimeWithMilliseconds() + " Dosya icerigi : " + System.getProperty("line.separator"));
					getOutputFile().write((String) myParamList.get(0).getParamRef() + System.getProperty("line.separator"));

				} catch (IOException e) {
					handleLogException(e, myLogger);
				}

			} catch (Exception e) {
				myLogger.error("Yerel Dosya Okuma hatasi !");

				try {
					getOutputFile().write(DateUtils.getCurrentTimeWithMilliseconds() + " Yerel Dosya Okuma hatasi !" + System.getProperty("line.separator"));
					getOutputFile().write(DateUtils.getCurrentTimeWithMilliseconds() + " " + e.getMessage() + System.getProperty("line.separator"));

					for (StackTraceElement element : e.getStackTrace()) {
						getOutputFile().write("\t" + element.toString() + System.getProperty("line.separator"));
					}

				} catch (IOException ioe) {
					ioe.printStackTrace();
				}

				insertNewLiveStateInfo(StateName.INT_FINISHED, SubstateName.INT_COMPLETED, StatusName.INT_FAILED, "Yerel Dosya Okuma hatasi !");
			}

			if (processJobResult(retryFlag, myLogger, myParamList)) {
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
