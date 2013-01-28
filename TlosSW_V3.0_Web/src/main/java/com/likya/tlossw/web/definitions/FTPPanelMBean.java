package com.likya.tlossw.web.definitions;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.primefaces.event.FlowEvent;

import com.likya.tlos.model.xmlbeans.common.JobTypeDetailsDocument.JobTypeDetails;
import com.likya.tlos.model.xmlbeans.ftpadapter.CompressDocument.Compress;
import com.likya.tlos.model.xmlbeans.ftpadapter.DecompressDocument.Decompress;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpAdapterPropertiesDocument.FtpAdapterProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.OperationDocument.Operation;
import com.likya.tlossw.web.utils.WebJobDefUtils;

@ManagedBean(name = "ftpPanelMBean")
@ViewScoped
public class FTPPanelMBean extends JobBaseBean implements Serializable {

	private static final Logger logger = Logger.getLogger(FTPPanelMBean.class);

	private static final long serialVersionUID = 1911439607648366142L;

	private FtpAdapterProperties ftpProperties;

	private Collection<SelectItem> adapterTypeList = null;
	private String adapterType;

	private Collection<SelectItem> operationTypeList = null;
	private String operationType;

	private Collection<SelectItem> processedFilesOperationTypeList = null;
	private String processedFilesOperationType;

	private boolean compress = false;
	private String compressProgramPath;
	private String compressProgramFileName;
	private String compressPassword;
	private String confirmCompressPassword;

	private boolean decompress = false;
	private String decompressProgramPath;
	private String decompressProgramFileName;

	private String sourceDirectory;
	private String targetDirectory;
	private boolean sourceIsRemote;
	private boolean targetIsRemote;

	private String sourceFileNameType;
	private String sourceFileName;
	private String includeFiles;
	private String excludeFiles;
	private String includeWildcard;
	private String excludeWildcard;

	private String targetFileName;

	private boolean useMaxFileSize = false;
	private String maxFileSize;
	private Collection<SelectItem> fileTypeList = null;
	private String fileType;
	private boolean recursive = false;
	private boolean gelGec = true;
	private boolean useMinAge = false;
	private String minAge;
	private Collection<SelectItem> fileModificationTimeList = null;
	private String fileModificationTime;
	private String modificationTimeFormat;

	private boolean useArchive = false;
	private String archiveDirectory;
	private String fileNamingConvention;

	private Collection<SelectItem> ftpConnectionDefinitionList = null;

	private boolean skip;

	public void dispose() {

	}

	@PostConstruct
	public void init() {
		initJobPanel();

		fillAdapterTypeList();
		fillOperationTypeList();
		fillProcessedFilesOperationTypeList();
		fillFileTypeList();
		fillFileModificationTimeList();

		setFtpConnectionDefinitionList(WebJobDefUtils.fillFtpConnectionDefinitionList(getDbOperations().getFtpConnectionList()));

		// webServiceDefinition = "";
		// selectedWebService = null;

		// TODO kullanici giris ekrani yapildiktan sonra
		// giris yapan kullanicinin id'si alinacak
		// int userId = 1;
		//
		// webServiceList =
		// getDbOperations().getWebServiceListForActiveUser(userId);
		// setWebServiceDefinitionList(WebJobDefUtils.fillWebServiceDefinitionList(webServiceList));
	}

	public void fillTabs() {
		fillJobPanel();
		fillFTPProperties();
	}

	private void fillFTPProperties() {
		JobTypeDetails jobTypeDetails = getJobProperties().getBaseJobInfos().getJobInfos().getJobTypeDetails();

		if (jobTypeDetails.getSpecialParameters() != null && jobTypeDetails.getSpecialParameters().getFtpAdapterProperties() != null) {
			ftpProperties = jobTypeDetails.getSpecialParameters().getFtpAdapterProperties();

			Operation operation = ftpProperties.getOperation();
			operationType = operation.getOperationType().toString();
			processedFilesOperationType = operation.getProcessedFilesOperationType().toString();
			
			if (operation.getPreOperation() != null && operation.getPreOperation().getCompress() != null) {
				compress = true;
				compressPassword = operation.getCompressedFilePassword().getUserPassword();
				confirmCompressPassword = compressPassword;
				
				Compress compress = operation.getPreOperation().getCompress(); 
				compressProgramPath = compress.getPath();
				compressProgramFileName = compress.getFilename();
			}
			
			if (operation.getPostOperation() != null) {
				decompress = true;
				compressPassword = operation.getCompressedFilePassword().getUserPassword();
				confirmCompressPassword = compressPassword;
				
				
			}
		}
	}

	public String onFlowProcess(FlowEvent event) {
		if (skip) {
			skip = false;
			return CONFIRM;

		} else {
			String newStep = event.getNewStep();

			return newStep;
		}
	}

	public void insertJobAction(ActionEvent e) {
		fillJobProperties();

		JobTypeDetails jobTypeDetails = getJobProperties().getBaseJobInfos().getJobInfos().getJobTypeDetails();

		insertJobDefinition();
	}

	private void fillAdapterTypeList() {
		if (adapterTypeList == null) {
			adapterTypeList = WebJobDefUtils.fillAdapterTypeList();
		}
	}

	private void fillOperationTypeList() {
		if (operationTypeList == null) {
			operationTypeList = WebJobDefUtils.fillOperationTypeList();
		}
	}

	private void fillProcessedFilesOperationTypeList() {
		if (processedFilesOperationTypeList == null) {
			processedFilesOperationTypeList = WebJobDefUtils.fillProcessedFilesOperationTypeList();
		}
	}

	private void fillFileTypeList() {
		if (fileTypeList == null) {
			fileTypeList = WebJobDefUtils.fillFileTypeList();
		}
	}

	private void fillFileModificationTimeList() {
		if (fileModificationTimeList == null) {
			fileModificationTimeList = WebJobDefUtils.fillFileModificationTimeList();
		}
	}

	public static Logger getLogger() {
		return logger;
	}

	public boolean isSkip() {
		return skip;
	}

	public void setSkip(boolean skip) {
		this.skip = skip;
	}

	public Collection<SelectItem> getAdapterTypeList() {
		return adapterTypeList;
	}

	public void setAdapterTypeList(Collection<SelectItem> adapterTypeList) {
		this.adapterTypeList = adapterTypeList;
	}

	public String getAdapterType() {
		return adapterType;
	}

	public void setAdapterType(String adapterType) {
		this.adapterType = adapterType;
	}

	public Collection<SelectItem> getOperationTypeList() {
		return operationTypeList;
	}

	public void setOperationTypeList(Collection<SelectItem> operationTypeList) {
		this.operationTypeList = operationTypeList;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public Collection<SelectItem> getProcessedFilesOperationTypeList() {
		return processedFilesOperationTypeList;
	}

	public void setProcessedFilesOperationTypeList(Collection<SelectItem> processedFilesOperationTypeList) {
		this.processedFilesOperationTypeList = processedFilesOperationTypeList;
	}

	public String getProcessedFilesOperationType() {
		return processedFilesOperationType;
	}

	public void setProcessedFilesOperationType(String processedFilesOperationType) {
		this.processedFilesOperationType = processedFilesOperationType;
	}

	public String getCompressProgramPath() {
		return compressProgramPath;
	}

	public void setCompressProgramPath(String compressProgramPath) {
		this.compressProgramPath = compressProgramPath;
	}

	public String getCompressProgramFileName() {
		return compressProgramFileName;
	}

	public void setCompressProgramFileName(String compressProgramFileName) {
		this.compressProgramFileName = compressProgramFileName;
	}

	public String getCompressPassword() {
		return compressPassword;
	}

	public void setCompressPassword(String compressPassword) {
		this.compressPassword = compressPassword;
	}

	public String getConfirmCompressPassword() {
		return confirmCompressPassword;
	}

	public void setConfirmCompressPassword(String confirmCompressPassword) {
		this.confirmCompressPassword = confirmCompressPassword;
	}

	public String getDecompressProgramPath() {
		return decompressProgramPath;
	}

	public void setDecompressProgramPath(String decompressProgramPath) {
		this.decompressProgramPath = decompressProgramPath;
	}

	public String getDecompressProgramFileName() {
		return decompressProgramFileName;
	}

	public void setDecompressProgramFileName(String decompressProgramFileName) {
		this.decompressProgramFileName = decompressProgramFileName;
	}

	public String getSourceDirectory() {
		return sourceDirectory;
	}

	public void setSourceDirectory(String sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
	}

	public String getTargetDirectory() {
		return targetDirectory;
	}

	public void setTargetDirectory(String targetDirectory) {
		this.targetDirectory = targetDirectory;
	}

	public String getSourceFileNameType() {
		return sourceFileNameType;
	}

	public void setSourceFileNameType(String sourceFileNameType) {
		this.sourceFileNameType = sourceFileNameType;
	}

	public String getSourceFileName() {
		return sourceFileName;
	}

	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
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

	public String getTargetFileName() {
		return targetFileName;
	}

	public void setTargetFileName(String targetFileName) {
		this.targetFileName = targetFileName;
	}

	public String getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(String maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public Collection<SelectItem> getFileTypeList() {
		return fileTypeList;
	}

	public void setFileTypeList(Collection<SelectItem> fileTypeList) {
		this.fileTypeList = fileTypeList;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getMinAge() {
		return minAge;
	}

	public void setMinAge(String minAge) {
		this.minAge = minAge;
	}

	public Collection<SelectItem> getFileModificationTimeList() {
		return fileModificationTimeList;
	}

	public void setFileModificationTimeList(Collection<SelectItem> fileModificationTimeList) {
		this.fileModificationTimeList = fileModificationTimeList;
	}

	public String getFileModificationTime() {
		return fileModificationTime;
	}

	public void setFileModificationTime(String fileModificationTime) {
		this.fileModificationTime = fileModificationTime;
	}

	public String getModificationTimeFormat() {
		return modificationTimeFormat;
	}

	public void setModificationTimeFormat(String modificationTimeFormat) {
		this.modificationTimeFormat = modificationTimeFormat;
	}

	public String getArchiveDirectory() {
		return archiveDirectory;
	}

	public void setArchiveDirectory(String archiveDirectory) {
		this.archiveDirectory = archiveDirectory;
	}

	public String getFileNamingConvention() {
		return fileNamingConvention;
	}

	public void setFileNamingConvention(String fileNamingConvention) {
		this.fileNamingConvention = fileNamingConvention;
	}

	public Collection<SelectItem> getFtpConnectionDefinitionList() {
		return ftpConnectionDefinitionList;
	}

	public void setFtpConnectionDefinitionList(Collection<SelectItem> ftpConnectionDefinitionList) {
		this.ftpConnectionDefinitionList = ftpConnectionDefinitionList;
	}

	public boolean isCompress() {
		return compress;
	}

	public void setCompress(boolean compress) {
		this.compress = compress;
	}

	public boolean isDecompress() {
		return decompress;
	}

	public void setDecompress(boolean decompress) {
		this.decompress = decompress;
	}

	public boolean isGelGec() {
		return gelGec;
	}

	public void setGelGec(boolean gelGec) {
		this.gelGec = gelGec;
	}

	public boolean isRecursive() {
		return recursive;
	}

	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}

	public boolean isUseMaxFileSize() {
		return useMaxFileSize;
	}

	public void setUseMaxFileSize(boolean useMaxFileSize) {
		this.useMaxFileSize = useMaxFileSize;
	}

	public boolean isUseMinAge() {
		return useMinAge;
	}

	public void setUseMinAge(boolean useMinAge) {
		this.useMinAge = useMinAge;
	}

	public boolean isSourceIsRemote() {
		return sourceIsRemote;
	}

	public void setSourceIsRemote(boolean sourceIsRemote) {
		this.sourceIsRemote = sourceIsRemote;
	}

	public boolean isTargetIsRemote() {
		return targetIsRemote;
	}

	public void setTargetIsRemote(boolean targetIsRemote) {
		this.targetIsRemote = targetIsRemote;
	}

	public boolean isUseArchive() {
		return useArchive;
	}

	public void setUseArchive(boolean useArchive) {
		this.useArchive = useArchive;
	}

	public FtpAdapterProperties getFtpProperties() {
		return ftpProperties;
	}

	public void setFtpProperties(FtpAdapterProperties ftpProperties) {
		this.ftpProperties = ftpProperties;
	}

}
