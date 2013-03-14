package com.likya.tlossw.web.definitions;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.common.JobTypeDetailsDocument.JobTypeDetails;
import com.likya.tlos.model.xmlbeans.common.SpecialParametersDocument.SpecialParameters;
import com.likya.tlos.model.xmlbeans.listener.FileListenerDocument.FileListener;
import com.likya.tlos.model.xmlbeans.listener.FileNameTypeDocument.FileNameType;
import com.likya.tlos.model.xmlbeans.listener.PollingPropertiesDocument.PollingProperties;
import com.likya.tlos.model.xmlbeans.listener.PollingTypeDocument.PollingType;
import com.likya.tlos.model.xmlbeans.listener.ReadAndListFilePropertiesDocument.ReadAndListFileProperties;
import com.likya.tlossw.web.utils.WebListDefinitionUtils;

@ManagedBean(name = "fileListenerPanelMBean")
@ViewScoped
public class FileListenerPanelMBean extends JobBaseBean implements Serializable {

	private static final Logger logger = Logger.getLogger(FileListenerPanelMBean.class);

	private static final long serialVersionUID = 3539696634614815587L;

	private FileListener fileListener;

	private String fileNameType;

	private String triggerFile;
	private String triggerFileDirectory;

	private String includeFiles;
	private String excludeFiles;
	private String includeWildcard;
	private String excludeWildcard;

	private Collection<SelectItem> pollingTypeList = null;
	private String pollingType;
	private String pollingFrequency;

	private String repetationNumber;

	private boolean recursive = false;

	public void dispose() {

	}

	@PostConstruct
	public void init() {
		initJobPanel();
		fillPollingTypeList();
	}

	public void fillTabs() {
		fillJobPanel();
		resetFileListenerProperties();
		fillFileListenerProperties();
	}

	private void resetFileListenerProperties() {
		fileNameType = "";
		triggerFile = "";
		triggerFileDirectory = "";
		includeFiles = "";
		excludeFiles = "";
		includeWildcard = "";
		excludeWildcard = "";
		pollingType = PollingType.CREATE.toString();
		pollingFrequency = "";
		repetationNumber = "";
		recursive = false;
	}

	private void fillFileListenerProperties() {
		JobTypeDetails jobTypeDetails = getJobProperties().getBaseJobInfos().getJobInfos().getJobTypeDetails();
		if (jobTypeDetails.getSpecialParameters() != null && jobTypeDetails.getSpecialParameters().getFileListener() != null) {
			fileListener = jobTypeDetails.getSpecialParameters().getFileListener();

			ReadAndListFileProperties fileProperties = fileListener.getReadAndListFileProperties();

			if (fileListener.getFileNameType().equals(FileNameType.FULL_TEXT)) {
				fileNameType = FULLTEXT;
				triggerFile = fileListener.getPollingProperties().getTriggerFile();

			} else if (fileProperties.getExcludeFiles() != null) {
				fileNameType = REGEX_WITH_EXCLUDE;
				excludeFiles = fileProperties.getExcludeFiles();
				includeFiles = fileProperties.getIncludeFiles();

			} else if (fileProperties.getIncludeFiles() != null) {
				fileNameType = REGEX;
				includeFiles = fileProperties.getIncludeFiles();

			} else if (fileProperties.getExcludeWildcard() != null) {
				fileNameType = WILDCARD_WITH_EXCLUDE;
				excludeWildcard = fileProperties.getExcludeWildcard();
				includeWildcard = fileProperties.getIncludeWildcard();

			} else if (fileProperties.getIncludeWildcard() != null) {
				fileNameType = WILDCARD;
				includeWildcard = fileProperties.getIncludeWildcard();
			}

			recursive = fileProperties.getRecursive();
			repetationNumber = fileListener.getRepetationNumber() + "";

			PollingProperties pollingProperties = fileListener.getPollingProperties();
			triggerFileDirectory = pollingProperties.getTriggerFileDirectory();
			pollingType = pollingProperties.getPollingType().toString();
			pollingFrequency = pollingProperties.getPollingFrequency().toString();
		}
	}

	public void insertJobAction(ActionEvent e) {
		fillJobProperties();
		fillFileListenerPropertyDetails();

		insertJobDefinition();
	}

	private void fillFileListenerPropertyDetails() {
		JobTypeDetails jobTypeDetails = getJobProperties().getBaseJobInfos().getJobInfos().getJobTypeDetails();
		SpecialParameters specialParameters;

		// periyodik job alanlari doldurulurken bu alan olusturuldugu icin
		// bu kontrol yapiliyor
		if (jobTypeDetails.getSpecialParameters() == null) {
			specialParameters = SpecialParameters.Factory.newInstance();
		} else {
			specialParameters = jobTypeDetails.getSpecialParameters();
		}

		PollingProperties pollingProperties = PollingProperties.Factory.newInstance();
		ReadAndListFileProperties fileProperties = ReadAndListFileProperties.Factory.newInstance();

		if (fileNameType.equals(FULLTEXT)) {
			fileListener.setFileNameType(FileNameType.FULL_TEXT);
			pollingProperties.setTriggerFile(triggerFile);

		} else {
			fileListener.setFileNameType(FileNameType.REGEX);

			if (fileNameType.equals(REGEX)) {
				fileProperties.setIncludeFiles(includeFiles);

			} else if (fileNameType.equals(REGEX_WITH_EXCLUDE)) {
				fileProperties.setIncludeFiles(includeFiles);
				fileProperties.setExcludeFiles(excludeFiles);

			} else if (fileNameType.equals(WILDCARD)) {
				fileProperties.setIncludeWildcard(includeWildcard);

			} else if (fileNameType.equals(WILDCARD_WITH_EXCLUDE)) {
				fileProperties.setIncludeWildcard(includeWildcard);
				fileProperties.setExcludeWildcard(excludeWildcard);
			}
		}

		fileProperties.setRecursive(recursive);
		fileListener.setReadAndListFileProperties(fileProperties);

		pollingProperties.setTriggerFileDirectory(triggerFileDirectory);
		pollingProperties.setPollingType(PollingType.Enum.forString(pollingType));
		pollingProperties.setPollingFrequency(new BigInteger(pollingFrequency));
		fileListener.setPollingProperties(pollingProperties);

		fileListener.setRepetationNumber(Integer.parseInt(repetationNumber));

		specialParameters.setFileListener(fileListener);
		jobTypeDetails.setSpecialParameters(specialParameters);
	}

	private void fillPollingTypeList() {
		if (pollingTypeList == null) {
			pollingTypeList = WebListDefinitionUtils.fillPollingTypeList();
		}
	}

	public static Logger getLogger() {
		return logger;
	}

	public boolean isRecursive() {
		return recursive;
	}

	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}

	public FileListener getFileListener() {
		return fileListener;
	}

	public void setFileListener(FileListener fileListener) {
		this.fileListener = fileListener;
	}

	public String getFileNameType() {
		return fileNameType;
	}

	public void setFileNameType(String fileNameType) {
		this.fileNameType = fileNameType;
	}

	public String getTriggerFile() {
		return triggerFile;
	}

	public void setTriggerFile(String triggerFile) {
		this.triggerFile = triggerFile;
	}

	public String getTriggerFileDirectory() {
		return triggerFileDirectory;
	}

	public void setTriggerFileDirectory(String triggerFileDirectory) {
		this.triggerFileDirectory = triggerFileDirectory;
	}

	public String getIncludeFiles() {
		return includeFiles;
	}

	public void setIncludeFiles(String includeFiles) {
		this.includeFiles = includeFiles;
	}

	public String getExcludeFiles() {
		return excludeFiles;
	}

	public void setExcludeFiles(String excludeFiles) {
		this.excludeFiles = excludeFiles;
	}

	public String getIncludeWildcard() {
		return includeWildcard;
	}

	public void setIncludeWildcard(String includeWildcard) {
		this.includeWildcard = includeWildcard;
	}

	public String getExcludeWildcard() {
		return excludeWildcard;
	}

	public void setExcludeWildcard(String excludeWildcard) {
		this.excludeWildcard = excludeWildcard;
	}

	public Collection<SelectItem> getPollingTypeList() {
		return pollingTypeList;
	}

	public void setPollingTypeList(Collection<SelectItem> pollingTypeList) {
		this.pollingTypeList = pollingTypeList;
	}

	public String getPollingType() {
		return pollingType;
	}

	public void setPollingType(String pollingType) {
		this.pollingType = pollingType;
	}

	public String getPollingFrequency() {
		return pollingFrequency;
	}

	public void setPollingFrequency(String pollingFrequency) {
		this.pollingFrequency = pollingFrequency;
	}

	public String getRepetationNumber() {
		return repetationNumber;
	}

	public void setRepetationNumber(String repetationNumber) {
		this.repetationNumber = repetationNumber;
	}
}
