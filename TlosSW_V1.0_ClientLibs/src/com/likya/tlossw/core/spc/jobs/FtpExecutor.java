package com.likya.tlossw.core.spc.jobs;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpPropertiesDocument.FtpProperties;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.utils.GlobalRegistry;
import com.likya.tlossw.utils.ParsingUtils;
import com.likya.tlossw.utils.date.DateUtils;

public abstract class FtpExecutor extends FileJob {

	private static final long serialVersionUID = -5600370864917354128L;
	
	private Writer outputFile = null;
	private String ipAddress = null;
	
	private FTPClient ftpClient = null;
			
	private String userName = null;
	private String password = null;
	
	short port = 0;
	
	protected boolean retryFlag = true;

	transient protected Process process;
	
	public FtpExecutor(GlobalRegistry globalRegistry, Logger globalLogger, JobRuntimeProperties jobRuntimeProperties) {
		super(globalRegistry, globalLogger, jobRuntimeProperties);
	}
	
	public void initializeFtpJob() {
		JobProperties jobProperties = getJobRuntimeProperties().getJobProperties();

		String logFilePath = jobProperties.getBaseJobInfos().getJobLogPath();
		String logFileName = jobProperties.getBaseJobInfos().getJobLogFile().substring(0, jobProperties.getBaseJobInfos().getJobLogFile().indexOf('.')) + "_" + DateUtils.getCurrentTimeForFileName()
				+ jobProperties.getBaseJobInfos().getJobLogFile().substring(jobProperties.getBaseJobInfos().getJobLogFile().indexOf('.'), jobProperties.getBaseJobInfos().getJobLogFile().length());

		String logFile = ParsingUtils.getConcatenatedPathAndFileName(logFilePath, logFileName);

		try {
			setOutputFile(new BufferedWriter(new FileWriter(logFile)));

			getOutputFile().write(DateUtils.getCurrentTimeWithMilliseconds() + " Ftp isi baslatildi !" + System.getProperty("line.separator"));

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		// TODO Merve Özbey 01.08.2012
		// log dosyasinin ismine zaman damgasi verildigi icin bu ismi
		// dailyScenarios.xml'de guncellemek gerekiyor
		// DBUtils.insertLogFileNameForJob(jobProperties, jobPath, logFileName);
		// ayni ismi registrydeki jobproperties icinde de guncellemek gerekiyor
		
		jobProperties.getBaseJobInfos().setJobLogFile(logFileName);
		
		ftpClient = new FTPClient();
		
		FtpProperties ftpProperties = getJobRuntimeProperties().getFtpProperties();
		
		ipAddress = ftpProperties.getConnection().getIpAddress();
		port = ftpProperties.getConnection().getFtpPortNumber();
		
		userName = ftpProperties.getConnection().getUserName();
		password = ftpProperties.getConnection().getUserPassword();

	}
	
	public boolean checkLogin(Logger logger) throws Exception {
		
		logger.info("Ftp ile kaynaga baglaniliyor !");
		
		getOutputFile().write(DateUtils.getCurrentTimeWithMilliseconds() + " Ftp ile kaynaga baglaniliyor !" + System.getProperty( "line.separator" ));
		
		if(port == 0) {
			ftpClient.connect(ipAddress);
		} else {
			ftpClient.connect(ipAddress, port);
		}
		
		getOutputFile().write(DateUtils.getCurrentTimeWithMilliseconds() + " Ftp ile kaynaga baglanti gerceklestirildi !" + System.getProperty( "line.separator" ));
		
		boolean login = ftpClient.login(userName, password);
		
		if(!login) {
			getOutputFile().write(DateUtils.getCurrentTimeWithMilliseconds() + " Ftp sunucusuna giris yapilamadi" + System.getProperty("line.separator"));
			getOutputFile().write(DateUtils.getCurrentTimeWithMilliseconds() + " Ftp ReplyCode: " + getFtpClient().getReplyCode() + System.getProperty("line.separator"));
			getOutputFile().write(DateUtils.getCurrentTimeWithMilliseconds() + " Ftp ReplyString: " + getFtpClient().getReplyString());
		}
		
		return login;
	}
	
	public boolean logout() throws Exception {
		
		boolean logout = ftpClient.logout();
		
		if (logout) {
			getOutputFile().write(DateUtils.getCurrentTimeWithMilliseconds() + " Ftp sunucusundan cikis yapildi" + System.getProperty( "line.separator" ));
		} else {
			getOutputFile().write(DateUtils.getCurrentTimeWithMilliseconds() + " Ftp sunucusundan cikis hatali" + System.getProperty( "line.separator" ));
			getOutputFile().write(DateUtils.getCurrentTimeWithMilliseconds() + " Ftp ReplyCode: " + ftpClient.getReplyCode() + System.getProperty( "line.separator" ));
			getOutputFile().write(DateUtils.getCurrentTimeWithMilliseconds() + " Ftp ReplyString: " + ftpClient.getReplyString());
		}
		
		return logout;
	}

	public Writer getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(Writer outputFile) {
		this.outputFile = outputFile;
	}

	public FTPClient getFtpClient() {
		return ftpClient;
	}

	public void setFtpClient(FTPClient ftpClient) {
		this.ftpClient = ftpClient;
	}

}
