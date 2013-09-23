package com.likya.tlossw.web.definitions;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.common.JobTypeDetailsDocument.JobTypeDetails;
import com.likya.tlos.model.xmlbeans.common.SpecialParametersDocument.SpecialParameters;
import com.likya.tlos.model.xmlbeans.ftpadapter.AdapterTypeDocument.AdapterType;
import com.likya.tlos.model.xmlbeans.ftpadapter.ArchiveDirectoryDocument.ArchiveDirectory;
import com.likya.tlos.model.xmlbeans.ftpadapter.ArchivePropertiesDocument.ArchiveProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.CompressDocument.Compress;
import com.likya.tlos.model.xmlbeans.ftpadapter.CompressedFilePasswordDocument.CompressedFilePassword;
import com.likya.tlos.model.xmlbeans.ftpadapter.DecompressDocument.Decompress;
import com.likya.tlos.model.xmlbeans.ftpadapter.FileModificationTimeDocument.FileModificationTime;
import com.likya.tlos.model.xmlbeans.ftpadapter.FilePropertiesDocument.FileProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.FileTypeDocument.FileType;
import com.likya.tlos.model.xmlbeans.ftpadapter.FilenameAndDirectoryDocument.FilenameAndDirectory;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpAdapterPropertiesDocument.FtpAdapterProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.OperationDocument.Operation;
import com.likya.tlos.model.xmlbeans.ftpadapter.OperationTypeDocument.OperationType;
import com.likya.tlos.model.xmlbeans.ftpadapter.PostOperationDocument.PostOperation;
import com.likya.tlos.model.xmlbeans.ftpadapter.PreOperationDocument.PreOperation;
import com.likya.tlos.model.xmlbeans.ftpadapter.ProcessedFilesOperationTypeDocument.ProcessedFilesOperationType;
import com.likya.tlos.model.xmlbeans.ftpadapter.SourceDirectoryDocument.SourceDirectory;
import com.likya.tlos.model.xmlbeans.ftpadapter.SourceFileNameDocument.SourceFileName;
import com.likya.tlos.model.xmlbeans.ftpadapter.TargetDirectoryDocument.TargetDirectory;
import com.likya.tlos.model.xmlbeans.ftpadapter.TargetFileNameDocument.TargetFileName;
import com.likya.tlossw.web.utils.WebInputUtils;

@ManagedBean(name = "ftpPanelMBean")
@ViewScoped
public class FTPPanelMBean extends JobBasePanelBean implements Serializable {

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

	public void dispose() {

	}

	public void init() {
		initJobPanel();

		fillAdapterTypeList();
		fillOperationTypeList();
		fillProcessedFilesOperationTypeList();
		fillFileTypeList();
		fillFileModificationTimeList();

		setFtpConnectionDefinitionList(WebInputUtils.fillFtpConnectionDefinitionList(getDbOperations().getFtpConnectionList()));
	}

	public void fillTabs() {
		fillJobPanel();
		resetFTPProperties();
		fillFTPProperties();
	}

	private void resetFTPProperties() {
		operationType = "";
		processedFilesOperationType = ProcessedFilesOperationType.NONE.toString();
		sourceDirectory = "";
		targetDirectory = "";
		sourceIsRemote = true;
		targetIsRemote = false;
		sourceFileNameType = "";
		sourceFileName = "";
		includeFiles = "";
		includeWildcard = "";
		excludeFiles = "";
		excludeWildcard = "";
		targetFileName = "";
		useMaxFileSize = false;
		maxFileSize = "";
		fileType = FileType.ASCII.toString();
		recursive = false;
		gelGec = false;
		useMinAge = false;
		minAge = "";
		fileModificationTime = FileModificationTime.NONE.toString();
		modificationTimeFormat = "";
		useArchive = false;
		archiveDirectory = "";
		fileNamingConvention = "";
		compress = false;
		decompress = false;
		compressProgramPath = "";
		compressProgramFileName = "";
		compressPassword = "";
		confirmCompressPassword = "";
		decompressProgramPath = "";
		decompressProgramFileName = "";
	}

	private void fillFTPProperties() {
		JobTypeDetails jobTypeDetails = getJobProperties().getBaseJobInfos().getJobInfos().getJobTypeDetails();

		if (jobTypeDetails.getSpecialParameters() != null && jobTypeDetails.getSpecialParameters().getFtpAdapterProperties() != null) {
			ftpProperties = jobTypeDetails.getSpecialParameters().getFtpAdapterProperties();

			adapterType = ftpProperties.getAdapterType().toString();

			// operation
			Operation operation = ftpProperties.getOperation();
			operationType = operation.getOperationType().toString();
			processedFilesOperationType = operation.getProcessedFilesOperationType().toString();

			if (operation.getPreOperation() != null && operation.getPreOperation().getCompress() != null) {
				compress = true;

				Compress compress = operation.getPreOperation().getCompress();
				compressProgramPath = compress.getPath();
				compressProgramFileName = compress.getFilename();
			}

			if (operation.getPostOperation() != null && operation.getPostOperation().getDecompress() != null) {
				decompress = true;

				Decompress decompress = operation.getPostOperation().getDecompress();
				decompressProgramPath = decompress.getPath();
				decompressProgramFileName = decompress.getFilename();
			}

			if (operation.getCompressedFilePassword() != null) {
				compressPassword = operation.getCompressedFilePassword().getUserPassword();
				confirmCompressPassword = compressPassword;
			}

			// file properties
			FileProperties fileProperties = ftpProperties.getFileProperties();

			fileType = fileProperties.getFileType().toString();
			gelGec = fileProperties.getGelGec();
			recursive = fileProperties.getRecursive();

			if (!operationType.equals(OperationType.LIST_FILES.toString())) {
				if (fileProperties.getFileSize() != null) {
					useMaxFileSize = true;
					maxFileSize = fileProperties.getFileSize().toString();
				}

				if (fileProperties.getMinimumAge() != null) {
					useMinAge = true;
					minAge = fileProperties.getMinimumAge().toString();
				}

				fileModificationTime = fileProperties.getFileModificationTime().toString();
				if (!fileModificationTime.equals(FileModificationTime.NONE.toString())) {
					modificationTimeFormat = fileProperties.getModificationTimeFormat();
				}
			}

			// file name and directory
			FilenameAndDirectory filenameAndDirectory = ftpProperties.getFilenameAndDirectory();
			sourceDirectory = filenameAndDirectory.getSourceDirectory().getPath();

			if (filenameAndDirectory.getSourceFileName() != null) {
				sourceFileNameType = FULLTEXT;
				sourceFileName = filenameAndDirectory.getSourceFileName().getFilename();

			} else if (filenameAndDirectory.getExcludeFiles() != null) {
				sourceFileNameType = REGEX_WITH_EXCLUDE;
				excludeFiles = filenameAndDirectory.getExcludeFiles();
				includeFiles = filenameAndDirectory.getIncludeFiles();

			} else if (filenameAndDirectory.getIncludeFiles() != null) {
				sourceFileNameType = REGEX;
				includeFiles = filenameAndDirectory.getIncludeFiles();

			} else if (filenameAndDirectory.getExcludeWildcard() != null) {
				sourceFileNameType = WILDCARD_WITH_EXCLUDE;
				excludeWildcard = filenameAndDirectory.getExcludeWildcard();
				includeWildcard = filenameAndDirectory.getIncludeWildcard();

			} else if (filenameAndDirectory.getIncludeWildcard() != null) {
				sourceFileNameType = WILDCARD;
				includeWildcard = filenameAndDirectory.getIncludeWildcard();
			}

			if (!operationType.equals(OperationType.LIST_FILES.toString())) {
				sourceIsRemote = filenameAndDirectory.getSourceIsRemote();
				targetIsRemote = filenameAndDirectory.getTargetIsRemote();

				targetDirectory = filenameAndDirectory.getTargetDirectory().getPath();
				targetFileName = filenameAndDirectory.getTargetFileName().getFilename();
			}

			// archive properties
			ArchiveProperties archiveProperties = ftpProperties.getArchiveProperties();
			useArchive = archiveProperties.getArchive();

			if (useArchive) {
				archiveDirectory = archiveProperties.getArchiveDirectory().getPath();
				fileNamingConvention = archiveProperties.getFileNamingConvention();
			}
		}
	}

	public void insertJsAction() {
		if (validateTimeManagement()) {
			fillJobProperties();
			fillJobPropertyDetails();
	
			insertJobDefinition();
		}
	}

	public void updateJsAction() {
		fillJobProperties();
		fillJobPropertyDetails();

		updateJobDefinition();
	}

	public void sendDeploymentRequest() {
		if (!isJsOverrideAndDeployDialog()) {
			fillJobProperties();
			fillJobPropertyDetails();
		}

		insertJobDeploymentRequest();
	}

	public void fillJobPropertyDetails() {
		JobTypeDetails jobTypeDetails = getJobProperties().getBaseJobInfos().getJobInfos().getJobTypeDetails();
		SpecialParameters specialParameters;

		// periyodik job alanlari doldurulurken bu alan olusturuldugu icin bu
		// kontrol yapiliyor
		if (jobTypeDetails.getSpecialParameters() == null) {
			specialParameters = SpecialParameters.Factory.newInstance();
		} else {
			specialParameters = jobTypeDetails.getSpecialParameters();
		}

		ftpProperties.setAdapterType(AdapterType.Enum.forString(adapterType));

		// operation
		Operation operation = Operation.Factory.newInstance();
		operation.setOperationType(OperationType.Enum.forString(operationType));
		operation.setProcessedFilesOperationType(ProcessedFilesOperationType.Enum.forString(processedFilesOperationType));

		boolean passwordUsage = false;

		if (compress) {
			Compress compress = Compress.Factory.newInstance();
			compress.setPath(compressProgramPath);
			compress.setFilename(compressProgramFileName);

			PreOperation preOperation = PreOperation.Factory.newInstance();
			preOperation.setCompress(compress);

			operation.setPreOperation(preOperation);

			passwordUsage = true;
		}

		if (decompress) {
			Decompress decompress = Decompress.Factory.newInstance();
			decompress.setPath(getDecompressProgramPath());
			decompress.setFilename(getDecompressProgramFileName());

			PostOperation postOperation = PostOperation.Factory.newInstance();
			postOperation.setDecompress(decompress);

			operation.setPostOperation(postOperation);

			passwordUsage = true;
		}

		if (passwordUsage) {
			CompressedFilePassword compressedFilePassword = CompressedFilePassword.Factory.newInstance();
			compressedFilePassword.setUserPassword(compressPassword);
			operation.setCompressedFilePassword(compressedFilePassword);
		}

		ftpProperties.setOperation(operation);

		// file properties
		FileProperties fileProperties = FileProperties.Factory.newInstance();
		fileProperties.setFileType(FileType.Enum.forString(fileType));
		fileProperties.setGelGec(gelGec);
		fileProperties.setRecursive(recursive);

		if (!operationType.equals(OperationType.LIST_FILES.toString())) {

			if (useMaxFileSize) {
				fileProperties.setFileSize(new BigInteger(maxFileSize));
			}

			if (useMinAge) {
				fileProperties.setMinimumAge(new BigInteger(minAge));
			}

			fileProperties.setFileModificationTime(FileModificationTime.Enum.forString(fileModificationTime));
			if (!fileModificationTime.equals(FileModificationTime.NONE.toString())) {
				fileProperties.setModificationTimeFormat(modificationTimeFormat);
			}
		}
		ftpProperties.setFileProperties(fileProperties);

		// file name and directory
		FilenameAndDirectory filenameAndDirectory = FilenameAndDirectory.Factory.newInstance();
		SourceDirectory sourceDir = SourceDirectory.Factory.newInstance();
		sourceDir.setPath(sourceDirectory);
		filenameAndDirectory.setSourceDirectory(sourceDir);

		if (sourceFileNameType.equals(FULLTEXT)) {
			SourceFileName sourceFile = SourceFileName.Factory.newInstance();
			sourceFile.setFilename(sourceFileName);
			filenameAndDirectory.setSourceFileName(sourceFile);

		} else if (sourceFileNameType.equals(REGEX)) {
			filenameAndDirectory.setIncludeFiles(includeFiles);

		} else if (sourceFileNameType.equals(REGEX_WITH_EXCLUDE)) {
			filenameAndDirectory.setIncludeFiles(includeFiles);
			filenameAndDirectory.setExcludeFiles(excludeFiles);

		} else if (sourceFileNameType.equals(WILDCARD)) {
			filenameAndDirectory.setIncludeWildcard(includeWildcard);

		} else if (sourceFileNameType.equals(WILDCARD_WITH_EXCLUDE)) {
			filenameAndDirectory.setIncludeWildcard(includeWildcard);
			filenameAndDirectory.setExcludeWildcard(excludeWildcard);
		}

		if (!operationType.equals(OperationType.LIST_FILES.toString())) {
			filenameAndDirectory.setTargetIsRemote(targetIsRemote);
			filenameAndDirectory.setSourceIsRemote(sourceIsRemote);

			TargetDirectory targetDir = TargetDirectory.Factory.newInstance();
			targetDir.setPath(targetDirectory);
			filenameAndDirectory.setTargetDirectory(targetDir);

			TargetFileName targetFile = TargetFileName.Factory.newInstance();
			targetFile.setFilename(targetFileName);
			filenameAndDirectory.setTargetFileName(targetFile);
		} else {
			filenameAndDirectory.setSourceIsRemote(true);
		}
		ftpProperties.setFilenameAndDirectory(filenameAndDirectory);

		// archive properties
		ArchiveProperties archiveProperties = ArchiveProperties.Factory.newInstance();
		archiveProperties.setArchive(useArchive);

		if (useArchive) {
			ArchiveDirectory archiveDir = ArchiveDirectory.Factory.newInstance();
			archiveDir.setPath(archiveDirectory);
			archiveProperties.setArchiveDirectory(archiveDir);

			archiveProperties.setFileNamingConvention(fileNamingConvention);
		}
		ftpProperties.setArchiveProperties(archiveProperties);

		specialParameters.setFtpAdapterProperties(ftpProperties);
		jobTypeDetails.setSpecialParameters(specialParameters);
	}

	public void switchSourceRemoteDir() {
		sourceIsRemote = !targetIsRemote;
	}

	public void switchTargetRemoteDir() {
		targetIsRemote = !sourceIsRemote;
	}

	private void fillAdapterTypeList() {
		if (adapterTypeList == null) {
			adapterTypeList = WebInputUtils.fillAdapterTypeList();
		}
	}

	private void fillOperationTypeList() {
		if (operationTypeList == null) {
			operationTypeList = WebInputUtils.fillOperationTypeList();
		}
	}

	private void fillProcessedFilesOperationTypeList() {
		if (processedFilesOperationTypeList == null) {
			processedFilesOperationTypeList = WebInputUtils.fillProcessedFilesOperationTypeList();
		}
	}

	private void fillFileTypeList() {
		if (fileTypeList == null) {
			fileTypeList = WebInputUtils.fillFileTypeList();
		}
	}

	private void fillFileModificationTimeList() {
		if (fileModificationTimeList == null) {
			fileModificationTimeList = WebInputUtils.fillFileModificationTimeList();
		}
	}

	public static Logger getLogger() {
		return logger;
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
