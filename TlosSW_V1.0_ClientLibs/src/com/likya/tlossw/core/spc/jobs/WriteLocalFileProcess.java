package com.likya.tlossw.core.spc.jobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.fileadapter.FileAdapterPropertiesDocument.FileAdapterProperties;
import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument.Parameter;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.core.spc.helpers.ParamList;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.utils.FileUtils;
import com.likya.tlossw.utils.GlobalRegistry;
import com.likya.tlossw.utils.ParsingUtils;
import com.likya.tlossw.utils.date.DateUtils;

public class WriteLocalFileProcess extends FileProcessExecuter {

	private static final long serialVersionUID = -1708700840073505453L;

	private Logger myLogger = Logger.getLogger(ReadLocalFileProcess.class);

	public WriteLocalFileProcess(GlobalRegistry globalRegistry, Logger globalLogger, JobRuntimeProperties jobRuntimeProperties) {
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

			insertNewLiveStateInfo(StateName.INT_RUNNING, SubstateName.INT_ON_RESOURCE, StatusName.INT_TIME_IN);

			try {

				String targetFileName = fileProcessProperties.getFilenameAndDirectory().getTargetFileName().getFilename();
				String targetDirectory = fileProcessProperties.getFilenameAndDirectory().getTargetDirectory().getPath();

				String targetFile = ParsingUtils.getConcatenatedPathAndFileName(targetDirectory, targetFileName);

				String fileContent = "";

		        String[] inputArray;
		        
				inputArray = getInputParameters(jobProperties);
				fileContent = inputArray[0];
				
				ParamList thisParam = new ParamList(WRITE_FILE_SOURCE, "STRING", "VARIABLE", fileContent);
				myParamList.add(thisParam);
				
//				if (jobProperties.getLocalParameters() != null && jobProperties.getLocalParameters().getInParam() != null) {
//
//					Parameter[] inParamList = jobProperties.getLocalParameters().getInParam().getParameterArray();
//					ArrayList<Parameter> dependencyList = new ArrayList<Parameter>(Arrays.asList(inParamList));
//
//					Iterator<Parameter> parameterIterator = dependencyList.iterator();
//
//					while (parameterIterator.hasNext()) {
//
//						Parameter parameter = parameterIterator.next();
//
//						if (parameter.getName().equals(FileProcessExecuter.READ_FILE_RESULT)) {
//							fileContent = parameter.getValueString();
//							break;
//
//						} else if (parameter.getName().equals(WebServiceExecuter.WS_RESULT)) {
//							fileContent = parameter.getValueString();
//							break;
//
//						} else if (parameter.getName().equals(ProcessNode.PN_RESULT)) {
//							fileContent = parameter.getValueString();
//							break;
//						} else if (parameter.getName().equals(JDBCPostgreSQLSentenceExecuter.DB_RESULT)) {
//						fileContent = parameter.getValueString();
//						    break;
//					    }
//					}
//				}

				boolean result = FileUtils.writeFile(targetFile, fileContent);

				insertNewLiveStateInfo(StateName.INT_FINISHED, SubstateName.INT_COMPLETED, (result == true ? StatusName.INT_SUCCESS : StatusName.INT_FAILED));

				try {
					getOutputFile().write(DateUtils.getCurrentTimeWithMilliseconds() + " Dosya yazma islemi tamamlandi." + System.getProperty("line.separator"));

				} catch (IOException e) {
					handleLogException(e, myLogger);
				}

			} catch (Exception e) {
				myLogger.error("Yerel Dosya Yazma hatasi !" + e.getMessage());

				try {
					getOutputFile().write(DateUtils.getCurrentTimeWithMilliseconds() + " Yerel Dosya Yazma hatasi !" + System.getProperty("line.separator"));
					getOutputFile().write(DateUtils.getCurrentTimeWithMilliseconds() + " " + e.getMessage() + System.getProperty("line.separator"));

					for (StackTraceElement element : e.getStackTrace()) {
						getOutputFile().write("\t" + element.toString() + System.getProperty("line.separator"));
					}

				} catch (IOException ioe) {
					ioe.printStackTrace();
				}

				insertNewLiveStateInfo(StateName.INT_FINISHED, SubstateName.INT_COMPLETED, StatusName.INT_FAILED, "Yerel Dosya Yazma hatasi !");
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
