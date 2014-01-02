package com.likya.tlossw.core.spc.jobs;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.utils.GlobalRegistry;
import com.likya.tlossw.utils.ParsingUtils;
import com.likya.tlossw.utils.date.DateUtils;

public abstract class FileProcessExecuter extends FileJob {

	private static final long serialVersionUID = 5008454434661327597L;

	private Writer outputFile = null;
	
	protected boolean retryFlag = true;

	transient protected Process process;
	
	public final static String READ_FILE_RESULT = "output1";
	public final static String WRITE_FILE_SOURCE = "input1";
	
	public FileProcessExecuter(GlobalRegistry globalRegistry, Logger globalLogger, JobRuntimeProperties jobRuntimeProperties) {
		super(globalRegistry, globalLogger, jobRuntimeProperties);
	}
	
	public void initializeFileProcessJob() {
		JobProperties jobProperties = getJobRuntimeProperties().getJobProperties();

		String logFilePath = jobProperties.getBaseJobInfos().getJobLogPath();
		String logFileName = jobProperties.getBaseJobInfos().getJobLogFile().substring(0, jobProperties.getBaseJobInfos().getJobLogFile().indexOf('.')) + "_" + DateUtils.getCurrentTimeForFileName()
				+ jobProperties.getBaseJobInfos().getJobLogFile().substring(jobProperties.getBaseJobInfos().getJobLogFile().indexOf('.'), jobProperties.getBaseJobInfos().getJobLogFile().length());

		String logFile = ParsingUtils.getConcatenatedPathAndFileName(logFilePath, logFileName);

		try {
			setOutputFile(new BufferedWriter(new FileWriter(logFile)));

			getOutputFile().write(DateUtils.getCurrentTimeWithMilliseconds() + " Dosya Isleme isi baslatildi !" + System.getProperty("line.separator"));

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		// TODO Merve Özbey 01.08.2012
		// log dosyasinin ismine zaman damgasi verildigi icin bu ismi
		// dailyScenarios.xml'de guncellemek gerekiyor
		// DBUtils.insertLogFileNameForJob(jobProperties, jobPath, logFileName);
		// ayni ismi registrydeki jobproperties icinde de guncellemek gerekiyor
		
		jobProperties.getBaseJobInfos().setJobLogFile(logFileName);
	}
	
	public Writer getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(Writer outputFile) {
		this.outputFile = outputFile;
	}

}
