package com.likya.tlossw.web.definitions;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.common.JobTypeDetailsDocument.JobTypeDetails;
import com.likya.tlos.model.xmlbeans.common.SpecialParametersDocument.SpecialParameters;
import com.likya.tlos.model.xmlbeans.processnode.AppendDocument.Append;
import com.likya.tlos.model.xmlbeans.processnode.DeleteDocument.Delete;
import com.likya.tlos.model.xmlbeans.processnode.FilterDocument.Filter;
import com.likya.tlos.model.xmlbeans.processnode.ProcessDocument.Process;
import com.likya.tlos.model.xmlbeans.processnode.ProcessDocument.Process.Source;
import com.likya.tlos.model.xmlbeans.processnode.ProcessNodeDocument.ProcessNode;
import com.likya.tlos.model.xmlbeans.processnode.ProcessNodesDocument.ProcessNodes;
import com.likya.tlos.model.xmlbeans.processnode.ReplaceDocument.Replace;
import com.likya.tlos.model.xmlbeans.processnode.ReplaceDocument.Replace.Type;
import com.likya.tlos.model.xmlbeans.processnode.SplitDocument.Split;
import com.likya.tlos.model.xmlbeans.processnode.TransformDocument.Transform;
import com.likya.tlos.model.xmlbeans.processnode.TransformDocument.Transform.With;
import com.likya.tlossw.web.utils.WebInputUtils;

@ManagedBean(name = "processNodePanelMBean")
@ViewScoped
public class ProcessNodePanelMBean extends JobBaseBean implements Serializable {

	private static final Logger logger = Logger.getLogger(ProcessNodePanelMBean.class);

	private static final long serialVersionUID = -4899233350101659362L;

	private ProcessNode processNode;

	private Collection<SelectItem> sourceTypeList = null;
	private String sourceType;

	private String processType;

	private String lineOrTagNumber;

	private String textOrTagToDelete;

	private String filterType;
	private String regexToFilter;
	private String stringFunctionToFilter;
	private String xpathToFilter;

	private String findValueOrTag;
	private String replaceValueOrTag;
	private String replaceType;

	private String splitType;
	private String wordToSplit;
	private String lineToSplit;
	private String tagToSplit;

	private String transformType;
	private String transformCommand;

	public static final String APPEND = "Append";
	public static final String DELETE = "Delete";
	public static final String FILTER = "Filter";
	public static final String REPLACE = "Replace";
	public static final String SPLIT = "Split";
	public static final String TRANSFORM = "Transform";

	public void dispose() {

	}

	@PostConstruct
	public void init() {
		initJobPanel();
		fillSourceTypeList();
	}

	public void fillTabs() {
		fillJobPanel();
		resetProcessNodeProperties();
		fillProcessNodeProperties();
	}

	private void resetProcessNodeProperties() {
		sourceType = "";
		processType = "";
		lineOrTagNumber = "";
		textOrTagToDelete = "";
		filterType = "";
		regexToFilter = "";
		stringFunctionToFilter = "";
		xpathToFilter = "";
		findValueOrTag = "";
		replaceValueOrTag = "";
		replaceType = "";
		splitType = "";
		wordToSplit = "";
		lineToSplit = "";
		tagToSplit = "";
		transformType = "";
		transformCommand = "";
	}

	private void fillProcessNodeProperties() {
		JobTypeDetails jobTypeDetails = getJobProperties().getBaseJobInfos().getJobInfos().getJobTypeDetails();
		if (jobTypeDetails.getSpecialParameters() != null && jobTypeDetails.getSpecialParameters().getProcessNodes() != null && jobTypeDetails.getSpecialParameters().getProcessNodes().sizeOfProcessNodeArray() > 0) {
			ProcessNodes processNodes = jobTypeDetails.getSpecialParameters().getProcessNodes();
			processNode = processNodes.getProcessNodeArray(0);

			sourceType = processNode.getProcess().getSource().toString();

			if (processNode.getAppend() != null) {
				processType = APPEND;
				lineOrTagNumber = processNode.getAppend().getStringValue();

			} else if (processNode.getDelete() != null) {
				processType = DELETE;
				textOrTagToDelete = processNode.getDelete().getFind();

			} else if (processNode.getFilter() != null) {
				processType = FILTER;

				if (sourceType.equals(Source.TEXT.toString())) {
					filterType = processNode.getFilter().getType().toString();

					if (filterType.equals(Filter.Type.REGEX.toString())) {
						regexToFilter = processNode.getFilter().getStringValue();

					} else if (filterType.equals(Filter.Type.STRING_FUNCTION.toString())) {
						stringFunctionToFilter = processNode.getFilter().getStringValue();
					}
				} else if (sourceType.equals(Source.XML.toString())) {
					xpathToFilter = processNode.getFilter().getStringValue();
				}

			} else if (processNode.getReplace() != null) {
				processType = REPLACE;
				findValueOrTag = processNode.getReplace().getFind();
				replaceValueOrTag = processNode.getReplace().getStringValue();

				if (sourceType.equals(Source.XML.toString())) {
					replaceType = processNode.getReplace().getType().toString();
				}

			} else if (processNode.getSplit() != null) {
				processType = SPLIT;

				if (sourceType.equals(Source.TEXT.toString())) {
					splitType = processNode.getSplit().getType().toString();

					if (splitType.equals(Split.Type.WORD.toString())) {
						wordToSplit = processNode.getSplit().getStringValue();

					} else if (splitType.equals(Split.Type.LINE.toString())) {
						lineToSplit = processNode.getSplit().getStringValue();
					}
				} else if (sourceType.equals(Source.XML.toString())) {
					tagToSplit = processNode.getSplit().getStringValue();
				}

			} else if (processNode.getTransform() != null) {
				processType = TRANSFORM;
				transformType = processNode.getTransform().getWith().toString();

				if (transformType.equals(With.XSLT.toString())) {
					transformCommand = processNode.getTransform().getStringValue();
				}
			}
		}
	}

	public void insertJsAction() {
		if (validateTimeManagement()) {
			fillJobProperties();
			fillProcessNodePropertyDetails();
	
			insertJobDefinition();
		}
	}
	
	public void updateJsAction() {
		fillJobProperties();
		fillProcessNodePropertyDetails();

		updateJobDefinition();
	}

	private void fillProcessNodePropertyDetails() {
		JobTypeDetails jobTypeDetails = getJobProperties().getBaseJobInfos().getJobInfos().getJobTypeDetails();
		SpecialParameters specialParameters;

		// periyodik job alanlari doldurulurken bu alan olusturuldugu icin
		// bu kontrol yapiliyor
		if (jobTypeDetails.getSpecialParameters() == null) {
			specialParameters = SpecialParameters.Factory.newInstance();
		} else {
			specialParameters = jobTypeDetails.getSpecialParameters();
		}

		ProcessNodes processNodes = ProcessNodes.Factory.newInstance();
		ProcessNode processNode = processNodes.addNewProcessNode();
		Process process = Process.Factory.newInstance();

		if (processType.equals(APPEND)) {
			Append append = Append.Factory.newInstance();
			append.setStringValue(lineOrTagNumber);

			if (sourceType.equals(Source.TEXT.toString())) {
				append.setType(Append.Type.LINE);

			} else if (sourceType.equals(Source.XML.toString())) {
				append.setType(Append.Type.TAG);
			}

			processNode.setAppend(append);

			process.setSource(Source.Enum.forString(sourceType));
			processNode.setProcess(process);

		} else if (processType.equals(DELETE)) {
			Delete delete = Delete.Factory.newInstance();
			delete.setFind(textOrTagToDelete);

			processNode.setDelete(delete);

			process.setSource(Source.Enum.forString(sourceType));
			processNode.setProcess(process);

		} else if (processType.equals(FILTER)) {
			Filter filter = Filter.Factory.newInstance();

			if (sourceType.equals(Source.TEXT.toString())) {
				if (filterType.equals(Filter.Type.REGEX.toString())) {
					filter.setType(Filter.Type.REGEX);
					filter.setStringValue(regexToFilter);

				} else if (filterType.equals(Filter.Type.STRING_FUNCTION.toString())) {
					filter.setType(Filter.Type.STRING_FUNCTION);
					filter.setStringValue(stringFunctionToFilter);
				}
			} else if (sourceType.equals(Source.XML.toString())) {
				filter.setType(Filter.Type.XPATH);
				filter.setStringValue(xpathToFilter);
			}

			processNode.setFilter(filter);

			process.setSource(Source.Enum.forString(sourceType));
			processNode.setProcess(process);

		} else if (processType.equals(REPLACE)) {
			Replace replace = Replace.Factory.newInstance();
			replace.setFind(findValueOrTag);
			replace.setStringValue(replaceValueOrTag);

			if (sourceType.equals(Source.XML.toString())) {
				if (replaceType.equals(Type.TAG.toString())) {
					replace.setType(Type.TAG);
				} else if (replaceType.equals(Type.VALUE.toString())) {
					replace.setType(Type.VALUE);
				}
			}

			processNode.setReplace(replace);

			process.setSource(Source.Enum.forString(sourceType));
			processNode.setProcess(process);

		} else if (processType.equals(SPLIT)) {
			Split split = Split.Factory.newInstance();

			if (sourceType.equals(Source.TEXT.toString())) {
				if (splitType.equals(Split.Type.WORD.toString())) {
					split.setType(Split.Type.WORD);
					split.setStringValue(wordToSplit);

				} else if (splitType.equals(Split.Type.LINE.toString())) {
					split.setType(Split.Type.LINE);
					split.setStringValue(lineToSplit);
				}
			} else if (sourceType.equals(Source.XML.toString())) {
				split.setType(Split.Type.TAG);
				split.setStringValue(tagToSplit);
			}

			processNode.setSplit(split);

			process.setSource(Source.Enum.forString(sourceType));
			processNode.setProcess(process);

		} else if (processType.equals(TRANSFORM)) {
			Transform transform = Transform.Factory.newInstance();

			if (sourceType.equals(Source.XML.toString()) && getTransformType().equals(With.XSLT.toString())) {
				transform.setWith(With.XSLT);
				transform.setStringValue(transformCommand);
			}

			processNode.setTransform(transform);

			process.setSource(Source.Enum.forString(sourceType));
			processNode.setProcess(process);
		}

		specialParameters.setProcessNodes(processNodes);
		jobTypeDetails.setSpecialParameters(specialParameters);
	}

	private void fillSourceTypeList() {
		if (sourceTypeList == null) {
			sourceTypeList = WebInputUtils.fillSourceTypeList();
		}
	}

	public static Logger getLogger() {
		return logger;
	}

	public Collection<SelectItem> getSourceTypeList() {
		return sourceTypeList;
	}

	public void setSourceTypeList(Collection<SelectItem> sourceTypeList) {
		this.sourceTypeList = sourceTypeList;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getProcessType() {
		return processType;
	}

	public void setProcessType(String processType) {
		this.processType = processType;
	}

	public String getLineOrTagNumber() {
		return lineOrTagNumber;
	}

	public void setLineOrTagNumber(String lineOrTagNumber) {
		this.lineOrTagNumber = lineOrTagNumber;
	}

	public String getTextOrTagToDelete() {
		return textOrTagToDelete;
	}

	public void setTextOrTagToDelete(String textOrTagToDelete) {
		this.textOrTagToDelete = textOrTagToDelete;
	}

	public String getFilterType() {
		return filterType;
	}

	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}

	public String getRegexToFilter() {
		return regexToFilter;
	}

	public void setRegexToFilter(String regexToFilter) {
		this.regexToFilter = regexToFilter;
	}

	public String getStringFunctionToFilter() {
		return stringFunctionToFilter;
	}

	public void setStringFunctionToFilter(String stringFunctionToFilter) {
		this.stringFunctionToFilter = stringFunctionToFilter;
	}

	public String getXpathToFilter() {
		return xpathToFilter;
	}

	public void setXpathToFilter(String xpathToFilter) {
		this.xpathToFilter = xpathToFilter;
	}

	public String getFindValueOrTag() {
		return findValueOrTag;
	}

	public void setFindValueOrTag(String findValueOrTag) {
		this.findValueOrTag = findValueOrTag;
	}

	public String getReplaceValueOrTag() {
		return replaceValueOrTag;
	}

	public void setReplaceValueOrTag(String replaceValueOrTag) {
		this.replaceValueOrTag = replaceValueOrTag;
	}

	public String getReplaceType() {
		return replaceType;
	}

	public void setReplaceType(String replaceType) {
		this.replaceType = replaceType;
	}

	public String getSplitType() {
		return splitType;
	}

	public void setSplitType(String splitType) {
		this.splitType = splitType;
	}

	public String getWordToSplit() {
		return wordToSplit;
	}

	public void setWordToSplit(String wordToSplit) {
		this.wordToSplit = wordToSplit;
	}

	public String getLineToSplit() {
		return lineToSplit;
	}

	public void setLineToSplit(String lineToSplit) {
		this.lineToSplit = lineToSplit;
	}

	public String getTagToSplit() {
		return tagToSplit;
	}

	public void setTagToSplit(String tagToSplit) {
		this.tagToSplit = tagToSplit;
	}

	public String getTransformType() {
		return transformType;
	}

	public void setTransformType(String transformType) {
		this.transformType = transformType;
	}

	public String getTransformCommand() {
		return transformCommand;
	}

	public void setTransformCommand(String transformCommand) {
		this.transformCommand = transformCommand;
	}

	public ProcessNode getProcessNode() {
		return processNode;
	}

	public void setProcessNode(ProcessNode processNode) {
		this.processNode = processNode;
	}

}
