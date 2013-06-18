package com.likya.tlossw.web.definitions;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;

import com.likya.tlos.model.xmlbeans.common.JobCommandTypeDocument.JobCommandType;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.ScenarioDocument.Scenario;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.utils.ConstantDefinitions;
import com.likya.tlossw.web.utils.DefinitionUtils;

@ManagedBean(name = "jsDefinitionMBean")
@ViewScoped
public class JSDefinitionMBean extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = 1393726981346371091L;

	@ManagedProperty(value = "#{batchProcessPanelMBean}")
	private BatchProcessPanelMBean batchProcessPanelMBean;

	@ManagedProperty(value = "#{webServicePanelMBean}")
	private WebServicePanelMBean webServicePanelMBean;

	@ManagedProperty(value = "#{ftpPanelMBean}")
	private FTPPanelMBean ftpPanelMBean;

	@ManagedProperty(value = "#{fileProcessPanelMBean}")
	private FileProcessPanelMBean fileProcessPanelMBean;

	@ManagedProperty(value = "#{fileListenerPanelMBean}")
	private FileListenerPanelMBean fileListenerPanelMBean;

	@ManagedProperty(value = "#{dbJobsPanelMBean}")
	private DBJobsPanelMBean dbJobsPanelMBean;

	@ManagedProperty(value = "#{processNodePanelMBean}")
	private ProcessNodePanelMBean processNodePanelMBean;

	@ManagedProperty(value = "#{scenarioDefinitionMBean}")
	private ScenarioDefinitionMBean scenarioDefinitionMBean;

	private String jobDefCenterPanel = DEFAULT_DEF_PAGE;

	public final static String JOB_TEMPLATES_DATA = "tlosSWJobTemplates10.xml";
	public final static String JOB_DEFINITION_DATA = "tlosSWData10.xml";

	public final static String BATCH_PROCESS_PAGE = "/inc/definitionPanels/batchProcessJobDef.xhtml";
	public final static String WEB_SERVICE_PAGE = "/inc/definitionPanels/webServiceJobDef.xhtml";
	public final static String FTP_PAGE = "/inc/definitionPanels/ftpJobDef.xhtml";
	public final static String FILE_PROCESS_PAGE = "/inc/definitionPanels/fileProcessJobDef.xhtml";
	public final static String FILE_LISTENER_PAGE = "/inc/definitionPanels/fileListenerJobDef.xhtml";
	public final static String DB_JOBS_PAGE = "/inc/definitionPanels/dbJobDef.xhtml";
	public final static String PROCESS_NODE_PAGE = "/inc/definitionPanels/processNodeJobDef.xhtml";
	public final static String DEFAULT_DEF_PAGE = "/inc/definitionPanels/defaultJobDef.xhtml";

	public final static String SCENARIO_PAGE = "/inc/definitionPanels/scenarioDef.xhtml";

	public String draggedTemplateName;
	public String draggedTemplatePath;

	public String selectedJSPath;
	public String selectedType;

	public String draggedJobNameForDependency;
	public String draggedJobPathForDependency;

	private JobProperties jobProperties;
	private Scenario scenario;

	public void onNodeSelect(NodeSelectEvent event) {
		// addMessage("jobTree", FacesMessage.SEVERITY_INFO, event.getTreeNode().toString() + " selected", null);

		String selectedJS = event.getTreeNode().toString();

		if (selectedJS.equals(resolveMessage("tlos.workspace.tree.scenario.root"))) {
			return;
		}

		TreeNode treeNode = event.getTreeNode();

		if ((treeNode.getType() != null) && treeNode.getType().equalsIgnoreCase("scenario")) {
			selectedType = new String(ConstantDefinitions.TREE_SCENARIO);
		} else if ((treeNode.getType() != null) && treeNode.getType().equalsIgnoreCase("job")) {
			selectedType = new String(ConstantDefinitions.TREE_JOB);
		} else
			selectedType = new String("what?");

		selectedJSPath = "";

		while (!treeNode.getParent().toString().equals(ConstantDefinitions.TREE_ROOT)) {
			selectedJSPath = treeNode.getParent().toString() + "/" + selectedJSPath;
			treeNode = treeNode.getParent();
		}

		event.getTreeNode().getParent().getParent().toString();

		String jsId = DefinitionUtils.getXFromNameId(selectedJS, "Id");

		if (selectedType.equalsIgnoreCase(ConstantDefinitions.TREE_JOB)) {
			jobProperties = null;
			jobProperties = getDbOperations().getJobFromId(JOB_DEFINITION_DATA, jsId);

			if (jobProperties != null) {
				int jobType = jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getJobCommandType().intValue();

				initializeJobPanel(jobType, false);
			}
		} else if (selectedType.equalsIgnoreCase(ConstantDefinitions.TREE_SCENARIO)) {
			scenario = null;
			scenario = getDbOperations().getScenarioFromId(JOB_DEFINITION_DATA, jsId);

			if (scenario != null) {
				switchToScenarioPanel();
				getScenarioDefinitionMBean().setScenario(scenario);
				getScenarioDefinitionMBean().initializeScenarioPanel(false);
			}
		}
	}

	public void switchToScenarioPanel() {
		jobDefCenterPanel = JSDefinitionMBean.SCENARIO_PAGE;
	}

	public void handleDropAction(ActionEvent ae) {
		jobProperties = getDbOperations().getTemplateJobFromName(JOB_TEMPLATES_DATA, draggedTemplateName);

		int jobType = jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getJobCommandType().intValue();

		initializeJobPanel(jobType, true);
	}

	public void handleJobDropAction(ActionEvent ae) {
		if (jobDefCenterPanel.equals(BATCH_PROCESS_PAGE)) {
			getBatchProcessPanelMBean().setDraggedJobName(draggedJobNameForDependency);
			getBatchProcessPanelMBean().setDraggedJobPath(draggedJobPathForDependency);

		} else if (jobDefCenterPanel.equals(WEB_SERVICE_PAGE)) {
			getWebServicePanelMBean().setDraggedJobName(draggedJobNameForDependency);
			getWebServicePanelMBean().setDraggedJobPath(draggedJobPathForDependency);

		} else if (jobDefCenterPanel.equals(FTP_PAGE)) {
			getFtpPanelMBean().setDraggedJobName(draggedJobNameForDependency);
			getFtpPanelMBean().setDraggedJobPath(draggedJobPathForDependency);

		} else if (jobDefCenterPanel.equals(FILE_PROCESS_PAGE)) {
			getFileProcessPanelMBean().setDraggedJobName(draggedJobNameForDependency);
			getFileProcessPanelMBean().setDraggedJobPath(draggedJobPathForDependency);

		} else if (jobDefCenterPanel.equals(FILE_LISTENER_PAGE)) {
			getFileListenerPanelMBean().setDraggedJobName(draggedJobNameForDependency);
			getFileListenerPanelMBean().setDraggedJobPath(draggedJobPathForDependency);

		} else if (jobDefCenterPanel.equals(DB_JOBS_PAGE)) {
			getDbJobsPanelMBean().setDraggedJobName(draggedJobNameForDependency);
			getDbJobsPanelMBean().setDraggedJobPath(draggedJobPathForDependency);

		} else if (jobDefCenterPanel.equals(PROCESS_NODE_PAGE)) {
			getProcessNodePanelMBean().setDraggedJobName(draggedJobNameForDependency);
			getProcessNodePanelMBean().setDraggedJobPath(draggedJobPathForDependency);
		}
	}

	private void initializeJobPanel(int jobType, boolean insert) {
		switch (jobType) {

		case JobCommandType.INT_SYSTEM_COMMAND:

			break;

		case JobCommandType.INT_BATCH_PROCESS:
			if (jobProperties != null) {
				getBatchProcessPanelMBean().setJobProperties(jobProperties);
				getBatchProcessPanelMBean().setJsInsertButton(insert);
				getBatchProcessPanelMBean().setJsUpdateButton(!insert);
				getBatchProcessPanelMBean().resetPanelInputs();
				getBatchProcessPanelMBean().fillTabs();

				if (insert) {
					getBatchProcessPanelMBean().setJobPathInScenario(draggedTemplatePath);
				} else {
					getBatchProcessPanelMBean().setJobPathInScenario(selectedJSPath);
				}
			}

			jobDefCenterPanel = BATCH_PROCESS_PAGE;

			break;

		case JobCommandType.INT_SHELL_SCRIPT:

			break;

		case JobCommandType.INT_SAP:

			break;

		case JobCommandType.INT_SAS:

			break;

		case JobCommandType.INT_ETL_TOOL_JOBS:

			break;

		case JobCommandType.INT_FTP:
			if (jobProperties != null) {
				getFtpPanelMBean().setJobProperties(jobProperties);
				getFtpPanelMBean().setJsInsertButton(insert);
				getFtpPanelMBean().setJsUpdateButton(!insert);
				getFtpPanelMBean().resetPanelInputs();
				getFtpPanelMBean().fillTabs();

				if (insert) {
					getFtpPanelMBean().setJobPathInScenario(draggedTemplatePath);
				} else {
					getFtpPanelMBean().setJobPathInScenario(selectedJSPath);
				}
			}

			jobDefCenterPanel = FTP_PAGE;

			break;

		case JobCommandType.INT_WEB_SERVICE:
			if (jobProperties != null) {
				getWebServicePanelMBean().setJobProperties(jobProperties);
				getWebServicePanelMBean().setJsInsertButton(insert);
				getWebServicePanelMBean().setJsUpdateButton(!insert);
				getWebServicePanelMBean().resetPanelInputs();
				getWebServicePanelMBean().fillTabs();

				if (insert) {
					getWebServicePanelMBean().setJobPathInScenario(draggedTemplatePath);
				} else {
					getWebServicePanelMBean().setJobPathInScenario(selectedJSPath);
				}
			}

			jobDefCenterPanel = WEB_SERVICE_PAGE;

			break;

		case JobCommandType.INT_DB_JOBS:
			if (jobProperties != null) {
				getDbJobsPanelMBean().setJobProperties(jobProperties);
				getDbJobsPanelMBean().setJsInsertButton(insert);
				getDbJobsPanelMBean().setJsUpdateButton(!insert);
				getDbJobsPanelMBean().resetPanelInputs();
				getDbJobsPanelMBean().fillTabs();

				if (insert) {
					getDbJobsPanelMBean().setJobPathInScenario(draggedTemplatePath);
				} else {
					getDbJobsPanelMBean().setJobPathInScenario(selectedJSPath);
				}
			}

			jobDefCenterPanel = DB_JOBS_PAGE;

			break;

		case JobCommandType.INT_FILE_LISTENER:
			if (jobProperties != null) {
				getFileListenerPanelMBean().setJobProperties(jobProperties);
				getFileListenerPanelMBean().setJsInsertButton(insert);
				getFileListenerPanelMBean().setJsUpdateButton(!insert);
				getFileListenerPanelMBean().resetPanelInputs();
				getFileListenerPanelMBean().fillTabs();

				if (insert) {
					getFileListenerPanelMBean().setJobPathInScenario(draggedTemplatePath);
				} else {
					getFileListenerPanelMBean().setJobPathInScenario(selectedJSPath);
				}
			}

			jobDefCenterPanel = FILE_LISTENER_PAGE;

			break;

		case JobCommandType.INT_PROCESS_NODE:
			if (jobProperties != null) {
				getProcessNodePanelMBean().setJobProperties(jobProperties);
				getProcessNodePanelMBean().setJsInsertButton(insert);
				getProcessNodePanelMBean().setJsUpdateButton(!insert);
				getProcessNodePanelMBean().resetPanelInputs();
				getProcessNodePanelMBean().fillTabs();

				if (insert) {
					getProcessNodePanelMBean().setJobPathInScenario(draggedTemplatePath);
				} else {
					getProcessNodePanelMBean().setJobPathInScenario(selectedJSPath);
				}
			}

			jobDefCenterPanel = PROCESS_NODE_PAGE;

			break;

		case JobCommandType.INT_FILE_PROCESS:
			if (jobProperties != null) {
				getFileProcessPanelMBean().setJobProperties(jobProperties);
				getFileProcessPanelMBean().setJsInsertButton(insert);
				getFileProcessPanelMBean().setJsUpdateButton(!insert);
				getFileProcessPanelMBean().resetPanelInputs();
				getFileProcessPanelMBean().fillTabs();

				if (insert) {
					getFileProcessPanelMBean().setJobPathInScenario(draggedTemplatePath);
				} else {
					getFileProcessPanelMBean().setJobPathInScenario(selectedJSPath);
				}
			}

			jobDefCenterPanel = FILE_PROCESS_PAGE;

			break;

		default:
			break;
		}

		RequestContext context = RequestContext.getCurrentInstance();
		context.update("jobDefinitionForm");
	}

	public void cancelJsAction() {
		jobDefCenterPanel = DEFAULT_DEF_PAGE;
	}

	public void deleteScenarioAction() {
		if (getScenarioDefinitionMBean().deleteScenario()) {
			cancelJsAction();
		}
	}

	public void deleteJobAction() {
		boolean result = false;
		if (jobDefCenterPanel.equals(BATCH_PROCESS_PAGE)) {
			if (getBatchProcessPanelMBean().deleteJob()) {
				result = true;
			}
		} else if (jobDefCenterPanel.equals(WEB_SERVICE_PAGE)) {
			if (getWebServicePanelMBean().deleteJob()) {
				result = true;
			}
		} else if (jobDefCenterPanel.equals(FTP_PAGE)) {
			if (getFtpPanelMBean().deleteJob()) {
				result = true;
			}
		} else if (jobDefCenterPanel.equals(FILE_PROCESS_PAGE)) {
			if (getFileProcessPanelMBean().deleteJob()) {
				result = true;
			}
		} else if (jobDefCenterPanel.equals(FILE_LISTENER_PAGE)) {
			if (getFileListenerPanelMBean().deleteJob()) {
				result = true;
			}
		} else if (jobDefCenterPanel.equals(DB_JOBS_PAGE)) {
			if (getDbJobsPanelMBean().deleteJob()) {
				result = true;
			}
		} else if (jobDefCenterPanel.equals(PROCESS_NODE_PAGE)) {
			if (getProcessNodePanelMBean().deleteJob()) {
				result = true;
			}
		}

		if (result) {
			cancelJsAction();
		}
	}

	public String getJobDefCenterPanel() {
		return jobDefCenterPanel;
	}

	public void setJobDefCenterPanel(String jobDefCenterPanel) {
		this.jobDefCenterPanel = jobDefCenterPanel;
	}

	public String getDraggedTemplateName() {
		return draggedTemplateName;
	}

	public void setDraggedTemplateName(String draggedTemplateName) {
		this.draggedTemplateName = draggedTemplateName;
	}

	public JobProperties getJobProperties() {
		return jobProperties;
	}

	public void setJobProperties(JobProperties jobProperties) {
		this.jobProperties = jobProperties;
	}

	public BatchProcessPanelMBean getBatchProcessPanelMBean() {
		return batchProcessPanelMBean;
	}

	public void setBatchProcessPanelMBean(BatchProcessPanelMBean batchProcessPanelMBean) {
		this.batchProcessPanelMBean = batchProcessPanelMBean;
	}

	public String getDraggedTemplatePath() {
		return draggedTemplatePath;
	}

	public void setDraggedTemplatePath(String draggedTemplatePath) {
		this.draggedTemplatePath = draggedTemplatePath;
	}

	public String getDraggedJobNameForDependency() {
		return draggedJobNameForDependency;
	}

	public void setDraggedJobNameForDependency(String draggedJobNameForDependency) {
		this.draggedJobNameForDependency = draggedJobNameForDependency;
	}

	public String getDraggedJobPathForDependency() {
		return draggedJobPathForDependency;
	}

	public void setDraggedJobPathForDependency(String draggedJobPathForDependency) {
		this.draggedJobPathForDependency = draggedJobPathForDependency;
	}

	public WebServicePanelMBean getWebServicePanelMBean() {
		return webServicePanelMBean;
	}

	public void setWebServicePanelMBean(WebServicePanelMBean webServicePanelMBean) {
		this.webServicePanelMBean = webServicePanelMBean;
	}

	public FTPPanelMBean getFtpPanelMBean() {
		return ftpPanelMBean;
	}

	public void setFtpPanelMBean(FTPPanelMBean ftpPanelMBean) {
		this.ftpPanelMBean = ftpPanelMBean;
	}

	public FileProcessPanelMBean getFileProcessPanelMBean() {
		return fileProcessPanelMBean;
	}

	public void setFileProcessPanelMBean(FileProcessPanelMBean fileProcessPanelMBean) {
		this.fileProcessPanelMBean = fileProcessPanelMBean;
	}

	public FileListenerPanelMBean getFileListenerPanelMBean() {
		return fileListenerPanelMBean;
	}

	public void setFileListenerPanelMBean(FileListenerPanelMBean fileListenerPanelMBean) {
		this.fileListenerPanelMBean = fileListenerPanelMBean;
	}

	public DBJobsPanelMBean getDbJobsPanelMBean() {
		return dbJobsPanelMBean;
	}

	public void setDbJobsPanelMBean(DBJobsPanelMBean dbJobsPanelMBean) {
		this.dbJobsPanelMBean = dbJobsPanelMBean;
	}

	public ProcessNodePanelMBean getProcessNodePanelMBean() {
		return processNodePanelMBean;
	}

	public void setProcessNodePanelMBean(ProcessNodePanelMBean processNodePanelMBean) {
		this.processNodePanelMBean = processNodePanelMBean;
	}

	public String getSelectedType() {
		return selectedType;
	}

	public void setSelectedType(String selectedType) {
		this.selectedType = selectedType;
	}

	public Scenario getScenario() {
		return scenario;
	}

	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}

	public ScenarioDefinitionMBean getScenarioDefinitionMBean() {
		return scenarioDefinitionMBean;
	}

	public void setScenarioDefinitionMBean(ScenarioDefinitionMBean scenarioDefinitionMBean) {
		this.scenarioDefinitionMBean = scenarioDefinitionMBean;
	}

}
