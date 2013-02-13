package com.likya.tlossw.web.definitions;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.fileadapter.FileAdapterPropertiesDocument.FileAdapterProperties;
import com.likya.tlos.model.xmlbeans.fileadapter.FileModificationTimeDocument.FileModificationTime;
import com.likya.tlos.model.xmlbeans.fileadapter.FileTypeDocument.FileType;
import com.likya.tlos.model.xmlbeans.fileadapter.ProcessedFilesOperationTypeDocument.ProcessedFilesOperationType;
import com.likya.tlossw.web.utils.WebJobDefUtils;

@ManagedBean(name = "fileProcessPanelMBean")
@ViewScoped
public class FileProcessPanelMBean extends JobBaseBean implements Serializable {

	private static final Logger logger = Logger.getLogger(FileProcessPanelMBean.class);

	private static final long serialVersionUID = 681102878044121369L;

	private FileAdapterProperties fileProcessProperties;

	private Collection<SelectItem> fileAdapterTypeList = null;
	private String fileAdapterType;

	private Collection<SelectItem> textFileDetailOptions = null;
	private String textFileDetail;

	private String textFileSeparator;

	private boolean useXmlAccessType = false;
	private String xmlAccessType;

	private Collection<SelectItem> binaryFileDetailOptions = null;
	private String binaryFileDetail;

	private Collection<SelectItem> textFileOperationTypeList = null;
	private Collection<SelectItem> binaryFileOperationTypeList = null;
	private String fileOperationType;

	private Collection<SelectItem> processedFilesOperationTypeList = null;
	private String processedFilesOperationType;

	private boolean compress = false;
	private String compressProgramPath;
	private String compressProgramFileName;
	private String compressPassword;
	private String confirmCompressPassword;

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

	/* xmlFilesOperation */
	private String xmlNodeUsage;
	private String xPath;
	private String nodeValue;

	/* plainTextFilesOperation */
	private String startLineNumber;
	private String endLineNumber;
	private String plainTextUsage;
	private String plainTextValue;

	private boolean useFilter;
	private String xpathforFileFilter;
	private String plainTextFilterType;
	private String lineNumber;
	private String filterFrom;
	private String regexForFileFilter;
	private String stringSearchValue;
	private String stringSearchOccurance;

	private boolean useArchive = false;
	private String archiveDirectory;
	private String fileNamingConvention;

	public void dispose() {

	}

	@PostConstruct
	public void init() {
		initJobPanel();

		fillFileAdapterTypeList();
		fillTextFileDetailOptions();
		fillTextFileOperationTypeList();
		fillBinaryFileOperationTypeList();
		fillProcessedFilesOperationTypeList();
		fillFileTypeList();
		fillFileModificationTimeList();
	}

	public void fillTabs() {
		fillJobPanel();
		resetFileProcessProperties();
		fillFileProcessProperties();
	}

	private void resetFileProcessProperties() {
		fileOperationType = "";
		processedFilesOperationType = ProcessedFilesOperationType.NONE.toString();
		sourceDirectory = "";
		targetDirectory = "";
		sourceIsRemote = false;
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
		compressProgramPath = "";
		compressProgramFileName = "";
		compressPassword = "";
		confirmCompressPassword = "";
	}

	private void fillFileProcessProperties() {

	}

	/*
	 * private void fillFTPProperties() { JobTypeDetails jobTypeDetails =
	 * getJobProperties().getBaseJobInfos().getJobInfos().getJobTypeDetails();
	 * 
	 * if (jobTypeDetails.getSpecialParameters() != null &&
	 * jobTypeDetails.getSpecialParameters().getFtpAdapterProperties() != null)
	 * { ftpProperties =
	 * jobTypeDetails.getSpecialParameters().getFtpAdapterProperties();
	 * 
	 * adapterType = ftpProperties.getAdapterType().toString();
	 * 
	 * // operation Operation operation = ftpProperties.getOperation();
	 * operationType = operation.getOperationType().toString();
	 * processedFilesOperationType =
	 * operation.getProcessedFilesOperationType().toString();
	 * 
	 * if (operation.getPreOperation() != null &&
	 * operation.getPreOperation().getCompress() != null) { compress = true;
	 * 
	 * Compress compress = operation.getPreOperation().getCompress();
	 * compressProgramPath = compress.getPath(); compressProgramFileName =
	 * compress.getFilename(); }
	 * 
	 * if (operation.getPostOperation() != null &&
	 * operation.getPostOperation().getDecompress() != null) { decompress =
	 * true;
	 * 
	 * Decompress decompress = operation.getPostOperation().getDecompress();
	 * decompressProgramPath = decompress.getPath(); decompressProgramFileName =
	 * decompress.getFilename(); }
	 * 
	 * if (operation.getCompressedFilePassword() != null) { compressPassword =
	 * operation.getCompressedFilePassword().getUserPassword();
	 * confirmCompressPassword = compressPassword; }
	 * 
	 * // file properties FileProperties fileProperties =
	 * ftpProperties.getFileProperties();
	 * 
	 * fileType = fileProperties.getFileType().toString(); gelGec =
	 * fileProperties.getGelGec(); recursive = fileProperties.getRecursive();
	 * 
	 * if (!operationType.equals(OperationType.LIST_FILES.toString())) { if
	 * (fileProperties.getFileSize() != null) { useMaxFileSize = true;
	 * maxFileSize = fileProperties.getFileSize().toString(); }
	 * 
	 * if (fileProperties.getMinimumAge() != null) { useMinAge = true; minAge =
	 * fileProperties.getMinimumAge().toString(); }
	 * 
	 * fileModificationTime =
	 * fileProperties.getFileModificationTime().toString(); if
	 * (!fileModificationTime.equals(FileModificationTime.NONE.toString())) {
	 * modificationTimeFormat = fileProperties.getModificationTimeFormat(); } }
	 * 
	 * // file name and directory FilenameAndDirectory filenameAndDirectory =
	 * ftpProperties.getFilenameAndDirectory(); sourceDirectory =
	 * filenameAndDirectory.getSourceDirectory().getPath();
	 * 
	 * if (filenameAndDirectory.getSourceFileName() != null) {
	 * sourceFileNameType = FULLTEXT; sourceFileName =
	 * filenameAndDirectory.getSourceFileName().getFilename();
	 * 
	 * } else if (filenameAndDirectory.getExcludeFiles() != null) {
	 * sourceFileNameType = REGEX_WITH_EXCLUDE; excludeFiles =
	 * filenameAndDirectory.getExcludeFiles(); includeFiles =
	 * filenameAndDirectory.getIncludeFiles();
	 * 
	 * } else if (filenameAndDirectory.getIncludeFiles() != null) {
	 * sourceFileNameType = REGEX; includeFiles =
	 * filenameAndDirectory.getIncludeFiles();
	 * 
	 * } else if (filenameAndDirectory.getExcludeWildcard() != null) {
	 * sourceFileNameType = WILDCARD_WITH_EXCLUDE; excludeWildcard =
	 * filenameAndDirectory.getExcludeWildcard(); includeWildcard =
	 * filenameAndDirectory.getIncludeWildcard();
	 * 
	 * } else if (filenameAndDirectory.getIncludeWildcard() != null) {
	 * sourceFileNameType = WILDCARD; includeWildcard =
	 * filenameAndDirectory.getIncludeWildcard(); }
	 * 
	 * if (!operationType.equals(OperationType.LIST_FILES.toString())) {
	 * sourceIsRemote = filenameAndDirectory.getSourceIsRemote(); targetIsRemote
	 * = filenameAndDirectory.getTargetIsRemote();
	 * 
	 * targetDirectory = filenameAndDirectory.getTargetDirectory().getPath();
	 * targetFileName = filenameAndDirectory.getTargetFileName().getFilename();
	 * }
	 * 
	 * // archive properties ArchiveProperties archiveProperties =
	 * ftpProperties.getArchiveProperties(); useArchive =
	 * archiveProperties.getArchive();
	 * 
	 * if (useArchive) { archiveDirectory =
	 * archiveProperties.getArchiveDirectory().getPath(); fileNamingConvention =
	 * archiveProperties.getFileNamingConvention(); } } }
	 */

	public void insertJobAction(ActionEvent e) {
		fillJobProperties();
		fillFileProcessPropertyDetails();

		insertJobDefinition();
	}

	private void fillFileProcessPropertyDetails() {

	}

	/*
	 * private void fillFTPPropertyDetails() { JobTypeDetails jobTypeDetails =
	 * getJobProperties().getBaseJobInfos().getJobInfos().getJobTypeDetails();
	 * SpecialParameters specialParameters;
	 * 
	 * // periyodik job alanlari doldurulurken bu alan olusturuldugu icin bu //
	 * kontrol yapiliyor if (jobTypeDetails.getSpecialParameters() == null) {
	 * specialParameters = SpecialParameters.Factory.newInstance(); } else {
	 * specialParameters = jobTypeDetails.getSpecialParameters(); }
	 * 
	 * ftpProperties.setAdapterType(AdapterType.Enum.forString(adapterType));
	 * 
	 * // operation Operation operation = Operation.Factory.newInstance();
	 * operation.setOperationType(OperationType.Enum.forString(operationType));
	 * operation
	 * .setProcessedFilesOperationType(ProcessedFilesOperationType.Enum.
	 * forString(processedFilesOperationType));
	 * 
	 * boolean passwordUsage = false;
	 * 
	 * if (compress) { Compress compress = Compress.Factory.newInstance();
	 * compress.setPath(compressProgramPath);
	 * compress.setFilename(compressProgramFileName);
	 * 
	 * PreOperation preOperation = PreOperation.Factory.newInstance();
	 * preOperation.setCompress(compress);
	 * 
	 * operation.setPreOperation(preOperation);
	 * 
	 * passwordUsage = true; }
	 * 
	 * if (decompress) { Decompress decompress =
	 * Decompress.Factory.newInstance();
	 * decompress.setPath(getDecompressProgramPath());
	 * decompress.setFilename(getDecompressProgramFileName());
	 * 
	 * PostOperation postOperation = PostOperation.Factory.newInstance();
	 * postOperation.setDecompress(decompress);
	 * 
	 * operation.setPostOperation(postOperation);
	 * 
	 * passwordUsage = true; }
	 * 
	 * if (passwordUsage) { CompressedFilePassword compressedFilePassword =
	 * CompressedFilePassword.Factory.newInstance();
	 * compressedFilePassword.setUserPassword(compressPassword);
	 * operation.setCompressedFilePassword(compressedFilePassword); }
	 * 
	 * ftpProperties.setOperation(operation);
	 * 
	 * // file properties FileProperties fileProperties =
	 * FileProperties.Factory.newInstance();
	 * fileProperties.setFileType(FileType.Enum.forString(fileType));
	 * fileProperties.setGelGec(gelGec); fileProperties.setRecursive(recursive);
	 * 
	 * if (!operationType.equals(OperationType.LIST_FILES.toString())) {
	 * 
	 * if (useMaxFileSize) { fileProperties.setFileSize(new
	 * BigInteger(maxFileSize)); }
	 * 
	 * if (useMinAge) { fileProperties.setMinimumAge(new BigInteger(minAge)); }
	 * 
	 * fileProperties.setFileModificationTime(FileModificationTime.Enum.forString
	 * (fileModificationTime)); if
	 * (!fileModificationTime.equals(FileModificationTime.NONE.toString())) {
	 * fileProperties.setModificationTimeFormat(modificationTimeFormat); } }
	 * ftpProperties.setFileProperties(fileProperties);
	 * 
	 * // file name and directory FilenameAndDirectory filenameAndDirectory =
	 * FilenameAndDirectory.Factory.newInstance(); SourceDirectory sourceDir =
	 * SourceDirectory.Factory.newInstance();
	 * sourceDir.setPath(sourceDirectory);
	 * filenameAndDirectory.setSourceDirectory(sourceDir);
	 * 
	 * if (sourceFileNameType.equals(FULLTEXT)) { SourceFileName sourceFile =
	 * SourceFileName.Factory.newInstance();
	 * sourceFile.setFilename(sourceFileName);
	 * filenameAndDirectory.setSourceFileName(sourceFile);
	 * 
	 * } else if (sourceFileNameType.equals(REGEX)) {
	 * filenameAndDirectory.setIncludeFiles(includeFiles);
	 * 
	 * } else if (sourceFileNameType.equals(REGEX_WITH_EXCLUDE)) {
	 * filenameAndDirectory.setIncludeFiles(includeFiles);
	 * filenameAndDirectory.setExcludeFiles(excludeFiles);
	 * 
	 * } else if (sourceFileNameType.equals(WILDCARD)) {
	 * filenameAndDirectory.setIncludeWildcard(includeWildcard);
	 * 
	 * } else if (sourceFileNameType.equals(WILDCARD_WITH_EXCLUDE)) {
	 * filenameAndDirectory.setIncludeWildcard(includeWildcard);
	 * filenameAndDirectory.setExcludeWildcard(excludeWildcard); }
	 * 
	 * if (!operationType.equals(OperationType.LIST_FILES.toString())) {
	 * filenameAndDirectory.setTargetIsRemote(targetIsRemote);
	 * filenameAndDirectory.setSourceIsRemote(sourceIsRemote);
	 * 
	 * TargetDirectory targetDir = TargetDirectory.Factory.newInstance();
	 * targetDir.setPath(targetDirectory);
	 * filenameAndDirectory.setTargetDirectory(targetDir);
	 * 
	 * TargetFileName targetFile = TargetFileName.Factory.newInstance();
	 * targetFile.setFilename(targetFileName);
	 * filenameAndDirectory.setTargetFileName(targetFile); } else {
	 * filenameAndDirectory.setSourceIsRemote(true); }
	 * ftpProperties.setFilenameAndDirectory(filenameAndDirectory);
	 * 
	 * // archive properties ArchiveProperties archiveProperties =
	 * ArchiveProperties.Factory.newInstance();
	 * archiveProperties.setArchive(useArchive);
	 * 
	 * if (useArchive) { ArchiveDirectory archiveDir =
	 * ArchiveDirectory.Factory.newInstance();
	 * archiveDir.setPath(archiveDirectory);
	 * archiveProperties.setArchiveDirectory(archiveDir);
	 * 
	 * archiveProperties.setFileNamingConvention(fileNamingConvention); }
	 * ftpProperties.setArchiveProperties(archiveProperties);
	 * 
	 * specialParameters.setFtpAdapterProperties(ftpProperties);
	 * jobTypeDetails.setSpecialParameters(specialParameters); }
	 */

	private void fillFileAdapterTypeList() {
		if (fileAdapterTypeList == null) {
			fileAdapterTypeList = WebJobDefUtils.fillFileAdapterTypeList();
		}
	}

	private void fillTextFileDetailOptions() {
		if (textFileDetailOptions == null) {
			textFileDetailOptions = WebJobDefUtils.fillTextFileDetailOptions();
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

	private void fillTextFileOperationTypeList() {
		if (textFileOperationTypeList == null) {
			textFileOperationTypeList = WebJobDefUtils.fillTextFileOperationTypeList();
		}
	}

	private void fillBinaryFileOperationTypeList() {
		if (binaryFileOperationTypeList == null) {
			binaryFileOperationTypeList = WebJobDefUtils.fillBinaryFileOperationTypeList();
		}
	}

	public static Logger getLogger() {
		return logger;
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

	public boolean isCompress() {
		return compress;
	}

	public void setCompress(boolean compress) {
		this.compress = compress;
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

	public Collection<SelectItem> getFileAdapterTypeList() {
		return fileAdapterTypeList;
	}

	public void setFileAdapterTypeList(Collection<SelectItem> fileAdapterTypeList) {
		this.fileAdapterTypeList = fileAdapterTypeList;
	}

	public String getFileAdapterType() {
		return fileAdapterType;
	}

	public void setFileAdapterType(String fileAdapterType) {
		this.fileAdapterType = fileAdapterType;
	}

	public Collection<SelectItem> getTextFileDetailOptions() {
		return textFileDetailOptions;
	}

	public void setTextFileDetailOptions(Collection<SelectItem> textFileDetailOptions) {
		this.textFileDetailOptions = textFileDetailOptions;
	}

	public String getTextFileDetail() {
		return textFileDetail;
	}

	public void setTextFileDetail(String textFileDetail) {
		this.textFileDetail = textFileDetail;
	}

	public String getTextFileSeparator() {
		return textFileSeparator;
	}

	public void setTextFileSeparator(String textFileSeparator) {
		this.textFileSeparator = textFileSeparator;
	}

	public boolean isUseXmlAccessType() {
		return useXmlAccessType;
	}

	public void setUseXmlAccessType(boolean useXmlAccessType) {
		this.useXmlAccessType = useXmlAccessType;
	}

	public String getXmlAccessType() {
		return xmlAccessType;
	}

	public void setXmlAccessType(String xmlAccessType) {
		this.xmlAccessType = xmlAccessType;
	}

	public Collection<SelectItem> getBinaryFileDetailOptions() {
		return binaryFileDetailOptions;
	}

	public void setBinaryFileDetailOptions(Collection<SelectItem> binaryFileDetailOptions) {
		this.binaryFileDetailOptions = binaryFileDetailOptions;
	}

	public String getBinaryFileDetail() {
		return binaryFileDetail;
	}

	public void setBinaryFileDetail(String binaryFileDetail) {
		this.binaryFileDetail = binaryFileDetail;
	}

	public String getFileOperationType() {
		return fileOperationType;
	}

	public void setFileOperationType(String fileOperationType) {
		this.fileOperationType = fileOperationType;
	}

	public String getXmlNodeUsage() {
		return xmlNodeUsage;
	}

	public void setXmlNodeUsage(String xmlNodeUsage) {
		this.xmlNodeUsage = xmlNodeUsage;
	}

	public String getxPath() {
		return xPath;
	}

	public void setxPath(String xPath) {
		this.xPath = xPath;
	}

	public String getNodeValue() {
		return nodeValue;
	}

	public void setNodeValue(String nodeValue) {
		this.nodeValue = nodeValue;
	}

	public String getStartLineNumber() {
		return startLineNumber;
	}

	public void setStartLineNumber(String startLineNumber) {
		this.startLineNumber = startLineNumber;
	}

	public String getEndLineNumber() {
		return endLineNumber;
	}

	public void setEndLineNumber(String endLineNumber) {
		this.endLineNumber = endLineNumber;
	}

	public String getPlainTextUsage() {
		return plainTextUsage;
	}

	public void setPlainTextUsage(String plainTextUsage) {
		this.plainTextUsage = plainTextUsage;
	}

	public String getPlainTextValue() {
		return plainTextValue;
	}

	public void setPlainTextValue(String plainTextValue) {
		this.plainTextValue = plainTextValue;
	}

	public boolean isUseFilter() {
		return useFilter;
	}

	public void setUseFilter(boolean useFilter) {
		this.useFilter = useFilter;
	}

	public String getXpathforFileFilter() {
		return xpathforFileFilter;
	}

	public void setXpathforFileFilter(String xpathforFileFilter) {
		this.xpathforFileFilter = xpathforFileFilter;
	}

	public String getPlainTextFilterType() {
		return plainTextFilterType;
	}

	public void setPlainTextFilterType(String plainTextFilterType) {
		this.plainTextFilterType = plainTextFilterType;
	}

	public String getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getFilterFrom() {
		return filterFrom;
	}

	public void setFilterFrom(String filterFrom) {
		this.filterFrom = filterFrom;
	}

	public String getRegexForFileFilter() {
		return regexForFileFilter;
	}

	public void setRegexForFileFilter(String regexForFileFilter) {
		this.regexForFileFilter = regexForFileFilter;
	}

	public String getStringSearchValue() {
		return stringSearchValue;
	}

	public void setStringSearchValue(String stringSearchValue) {
		this.stringSearchValue = stringSearchValue;
	}

	public String getStringSearchOccurance() {
		return stringSearchOccurance;
	}

	public void setStringSearchOccurance(String stringSearchOccurance) {
		this.stringSearchOccurance = stringSearchOccurance;
	}

	public FileAdapterProperties getFileProcessProperties() {
		return fileProcessProperties;
	}

	public void setFileProcessProperties(FileAdapterProperties fileProcessProperties) {
		this.fileProcessProperties = fileProcessProperties;
	}

	public Collection<SelectItem> getTextFileOperationTypeList() {
		return textFileOperationTypeList;
	}

	public void setTextFileOperationTypeList(Collection<SelectItem> textFileOperationTypeList) {
		this.textFileOperationTypeList = textFileOperationTypeList;
	}

	public Collection<SelectItem> getBinaryFileOperationTypeList() {
		return binaryFileOperationTypeList;
	}

	public void setBinaryFileOperationTypeList(Collection<SelectItem> binaryFileOperationTypeList) {
		this.binaryFileOperationTypeList = binaryFileOperationTypeList;
	}

}
