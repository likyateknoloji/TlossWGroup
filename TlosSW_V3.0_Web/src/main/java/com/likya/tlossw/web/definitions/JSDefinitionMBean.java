package com.likya.tlossw.web.definitions;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;

import com.likya.tlos.model.xmlbeans.common.JobCommandTypeDocument.JobCommandType;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.ScenarioDocument.Scenario;
import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument;
import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument.TlosProcessData;
import com.likya.tlossw.model.tree.WsJobNode;
import com.likya.tlossw.model.tree.WsNode;
import com.likya.tlossw.utils.CommonConstantDefinitions;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.utils.ConstantDefinitions;
import com.likya.tlossw.webclient.TEJmxMpWorkSpaceClient;

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

	@ManagedProperty(value = "#{remoteShellPanelMBean}")
	private RemoteShellPanelMBean remoteShellPanelMBean;

	@ManagedProperty(value = "#{systemCommandPanelMBean}")
	private SystemCommandPanelMBean systemCommandPanelMBean;

	@ManagedProperty(value = "#{shellScriptPanelMBean}")
	private ShellScriptPanelMBean shellScriptPanelMBean;

	@ManagedProperty(value = "#{scenarioDefinitionMBean}")
	private ScenarioDefinitionMBean scenarioDefinitionMBean;

	private String jobDefCenterPanel = DEFAULT_DEF_PAGE;

	public final static String BATCH_PROCESS_PAGE = "/inc/definitionPanels/batchProcessJobDef.xhtml";
	public final static String WEB_SERVICE_PAGE = "/inc/definitionPanels/webServiceJobDef.xhtml";
	public final static String FTP_PAGE = "/inc/definitionPanels/ftpJobDef.xhtml";
	public final static String FILE_PROCESS_PAGE = "/inc/definitionPanels/fileProcessJobDef.xhtml";
	public final static String FILE_LISTENER_PAGE = "/inc/definitionPanels/fileListenerJobDef.xhtml";
	public final static String DB_JOBS_PAGE = "/inc/definitionPanels/dbJobDef.xhtml";
	public final static String PROCESS_NODE_PAGE = "/inc/definitionPanels/processNodeJobDef.xhtml";
	public final static String REMOTE_SHELL_PAGE = "/inc/definitionPanels/remoteJobDef.xhtml";
	public final static String SYSTEM_COMMAND_PAGE = "/inc/definitionPanels/systemCommandJobDef.xhtml";
	public final static String SHELL_SCRIPT_PAGE = "/inc/definitionPanels/shellScriptJobDef.xhtml";
	public final static String DEFAULT_DEF_PAGE = "/inc/definitionPanels/defaultJobDef.xhtml";

	public final static String SCENARIO_PAGE = "/inc/definitionPanels/scenarioDef.xhtml";
	
	public final static String DEFAULT_TEST_PAGE = "/inc/livePanels/testLiveJS.xhtml";

	public WsNode draggedTemplateName = new WsNode();
	public String draggedTemplatePath;

	public String selectedJSPath;
	public String selectedType;

	public WsNode draggedWsNodeForDependency = new WsNode();
	public String draggedJobPathForDependency;

	private JobProperties jobProperties;
	private Scenario scenario;

	private Object currentPanelMBeanRef;

	public void onNodeSelect(NodeSelectEvent event) {

		WsNode wsNode = (WsNode) event.getTreeNode().getData();

		if (wsNode != null && wsNode.getId() != null && wsNode.getId().equals(ConstantDefinitions.TREE_ROOT)) {
			return;
		}

		TreeNode treeNode = event.getTreeNode();

		if ((treeNode.getType() != null) && treeNode.getType().equalsIgnoreCase(ConstantDefinitions.TREE_SCENARIO)) {
			selectedType = new String(ConstantDefinitions.TREE_SCENARIO);
		} else if ((treeNode.getType() != null) && treeNode.getType().equalsIgnoreCase(ConstantDefinitions.TREE_JOB)) {
			selectedType = new String(ConstantDefinitions.TREE_JOB);
		} else {
			selectedType = new String(ConstantDefinitions.TREE_UNKNOWN);
		}

		selectedJSPath = "";

		while (!((WsNode)treeNode.getParent().getData()).getId().equals(ConstantDefinitions.TREE_ROOTID)) {
			selectedJSPath = ((WsNode)treeNode.getParent().getData()).getId() + "/" + selectedJSPath;
			treeNode = treeNode.getParent();
		}

		String jsId = wsNode.getId();

		if (selectedType.equalsIgnoreCase(ConstantDefinitions.TREE_JOB)) {

			boolean isInsert = false;

			jobProperties = null;

			if (Integer.parseInt(jsId) > 0) {
				jobProperties = getDbOperations().getJobFromId(getWebAppUser().getId(), getDocumentId(), jsId);
			} else {
				isInsert = true;
			}

			if (jobProperties != null && jobProperties.getBaseJobInfos() != null) {
				int jobType = jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getJobCommandType().intValue();

				initializeSelectedJobPanel(jobType, isInsert);
			}
		} else if (selectedType.equalsIgnoreCase(ConstantDefinitions.TREE_SCENARIO)) {
			scenario = null;
			scenario = getDbOperations().getScenarioFromId(getWebAppUser().getId(), getDocumentId(), jsId);

			if (scenario != null) {
				switchToScenarioPanel();
				getScenarioDefinitionMBean().setScenario(scenario);
				getScenarioDefinitionMBean().initializeScenarioPanel(false);
			}
		}
	}

	public void switchToScenarioPanel() {
		jobDefCenterPanel = JSDefinitionMBean.SCENARIO_PAGE;

		currentPanelMBeanRef = getScenarioDefinitionMBean();
	}
	
	public String switchToTestPage() throws InvalidInputException {
		
		getSessionMediator().getWebAppUser().setViewRoleId(CommonConstantDefinitions.EXIST_MYDATA);
		
		TlosProcessDataDocument tlosProcessDataDocument = TlosProcessDataDocument.Factory.newInstance();

		tlosProcessDataDocument.addNewTlosProcessData();
		
		TlosProcessData tlosProcessData = tlosProcessDataDocument.getTlosProcessData();
		
		tlosProcessData.addNewConcurrencyManagement().setInstanceId(getSessionMediator().getWebAppUser().getId() + "");
		
		tlosProcessData.addNewBaseScenarioInfos().setUserId(getSessionMediator().getWebAppUser().getId());
		
		tlosProcessData.addNewJobList();
		
		
		JobProperties jobProperties = null;
		try {
			jobProperties = JobPropertiesDocument.Factory.parse(getJobPropertiesXML()).getJobProperties();
		} catch (XmlException e) {
			e.printStackTrace();
		}

		tlosProcessData.getJobList().addNewJobProperties().set(jobProperties);
		
		if(tlosProcessData.validate()) {
			throw new InvalidInputException();
		}
		
		TEJmxMpWorkSpaceClient.addTestData(getSessionMediator().getWebAppUser(), tlosProcessDataDocument.toString());
		
		return DEFAULT_TEST_PAGE;
	}
	
	public String getJobPropertiesXML() {
		QName qName = JobProperties.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String jobPropertiesXML = jobProperties.xmlText(xmlOptions);

		return jobPropertiesXML;
	}

	public void handleDropAction(ActionEvent ae) {
		jobProperties = getDbOperations().getTemplateJobFromId(draggedTemplateName.getId());
		jobProperties.setID("0");

		int jobType = jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getJobCommandType().intValue();

		initializeJobPanel(jobType);
	}

	public void handleJobDropAction(ActionEvent ae) {

		((JobBaseBean) currentPanelMBeanRef).setDraggedWsJobNode(draggedWsNodeForDependency);
		((JobBaseBean) currentPanelMBeanRef).setDraggedJobPath(draggedJobPathForDependency);
	}

	private void setCurrentPanel(int jobType) {

		switch (jobType) {

		case JobCommandType.INT_SYSTEM_COMMAND:
			currentPanelMBeanRef = getSystemCommandPanelMBean();
			jobDefCenterPanel = SYSTEM_COMMAND_PAGE;
			break;

		case JobCommandType.INT_BATCH_PROCESS:
			currentPanelMBeanRef = getBatchProcessPanelMBean();
			jobDefCenterPanel = BATCH_PROCESS_PAGE;
			break;

		case JobCommandType.INT_SHELL_SCRIPT:
			currentPanelMBeanRef = getShellScriptPanelMBean();
			jobDefCenterPanel = SHELL_SCRIPT_PAGE;
			break;

		case JobCommandType.INT_SAP:
			break;

		case JobCommandType.INT_SAS:
			break;

		case JobCommandType.INT_ETL_TOOL_JOBS:
			break;

		case JobCommandType.INT_FTP:
			currentPanelMBeanRef = getFtpPanelMBean();
			jobDefCenterPanel = FTP_PAGE;
			break;

		case JobCommandType.INT_WEB_SERVICE:
			currentPanelMBeanRef = getWebServicePanelMBean();
			jobDefCenterPanel = WEB_SERVICE_PAGE;
			break;

		case JobCommandType.INT_DB_JOBS:
			currentPanelMBeanRef = getDbJobsPanelMBean();
			jobDefCenterPanel = DB_JOBS_PAGE;
			break;

		case JobCommandType.INT_FILE_LISTENER:
			currentPanelMBeanRef = getFileListenerPanelMBean();
			jobDefCenterPanel = FILE_LISTENER_PAGE;
			break;

		case JobCommandType.INT_PROCESS_NODE:
			currentPanelMBeanRef = getProcessNodePanelMBean();
			jobDefCenterPanel = PROCESS_NODE_PAGE;
			break;

		case JobCommandType.INT_FILE_PROCESS:
			currentPanelMBeanRef = getFileProcessPanelMBean();
			jobDefCenterPanel = FILE_PROCESS_PAGE;
			break;

		case JobCommandType.INT_REMOTE_SHELL:
			currentPanelMBeanRef = getRemoteShellPanelMBean();
			jobDefCenterPanel = REMOTE_SHELL_PAGE;
			break;

		default:
			break;
		}
	}

	private void initializeJobPanel(int jobType) {

		setCurrentPanel(jobType);

		if (jobProperties != null) {
			((JobBaseBean) currentPanelMBeanRef).setJobProperties(jobProperties);

			((JobBaseBean) currentPanelMBeanRef).setJsInsertButton(true);
			((JobBaseBean) currentPanelMBeanRef).setJsUpdateButton(false);
			((JobBaseBean) currentPanelMBeanRef).resetPanelInputs();
			((JobBaseBean) currentPanelMBeanRef).fillTabs();

			((JobBaseBean) currentPanelMBeanRef).setJobPathInScenario(draggedTemplatePath);
			((JobBaseBean) currentPanelMBeanRef).addJobNodeToScenarioPath();

			RequestContext context = RequestContext.getCurrentInstance();
			context.update("jsTreeForm");
			context.update("jobDefinitionForm");
		}
	}

	private void initializeSelectedJobPanel(int jobType, boolean insert) {

		setCurrentPanel(jobType);

		if (jobProperties != null) {
			((JobBaseBean) currentPanelMBeanRef).setJobProperties(jobProperties);

			((JobBaseBean) currentPanelMBeanRef).setJsInsertButton(insert);
			((JobBaseBean) currentPanelMBeanRef).setJsUpdateButton(!insert);
			((JobBaseBean) currentPanelMBeanRef).resetPanelInputs();
			((JobBaseBean) currentPanelMBeanRef).fillTabs();

			if (insert) {
				((JobBaseBean) currentPanelMBeanRef).setJobPathInScenario(draggedTemplatePath);
			} else {
				((JobBaseBean) currentPanelMBeanRef).setJobPathInScenario(selectedJSPath);
				((JobBaseBean) currentPanelMBeanRef).setJsName(jobProperties.getBaseJobInfos().getJsName());
			}

			RequestContext context = RequestContext.getCurrentInstance();
			context.update("jobDefinitionForm");
		}
	}

	public void cancelJsAction() {
		jobDefCenterPanel = DEFAULT_DEF_PAGE;

		if (((JobBaseBean) currentPanelMBeanRef).isJsInsertButton()) {
			((JobBaseBean) currentPanelMBeanRef).deleteJob();
		}
	}

	public void deleteScenarioAction() {
		if (getScenarioDefinitionMBean().deleteScenario()) {
			jobDefCenterPanel = DEFAULT_DEF_PAGE;
		}
	}

	public void deleteJobAction(ActionEvent actionEvent) {

		if (((JobBaseBean) currentPanelMBeanRef).deleteJob()) {
			jobDefCenterPanel = DEFAULT_DEF_PAGE;
		}
	}

	public String getJobDefCenterPanel() {
		return jobDefCenterPanel;
	}

	public void setJobDefCenterPanel(String jobDefCenterPanel) {
		this.jobDefCenterPanel = jobDefCenterPanel;
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

	public RemoteShellPanelMBean getRemoteShellPanelMBean() {
		return remoteShellPanelMBean;
	}

	public void setRemoteShellPanelMBean(RemoteShellPanelMBean remoteShellPanelMBean) {
		this.remoteShellPanelMBean = remoteShellPanelMBean;
	}

	public SystemCommandPanelMBean getSystemCommandPanelMBean() {
		return systemCommandPanelMBean;
	}

	public void setSystemCommandPanelMBean(SystemCommandPanelMBean systemCommandPanelMBean) {
		this.systemCommandPanelMBean = systemCommandPanelMBean;
	}

	public WsNode getDraggedWsNodeForDependency() {
		return draggedWsNodeForDependency;
	}

	public void setDraggedWsNodeForDependency(WsJobNode draggedWsNodeForDependency) {
		this.draggedWsNodeForDependency = draggedWsNodeForDependency;
	}

	public WsNode getDraggedTemplateName() {
		return draggedTemplateName;
	}

	public void setDraggedTemplateName(WsNode draggedTemplateName) {
		this.draggedTemplateName = draggedTemplateName;
	}

	public ShellScriptPanelMBean getShellScriptPanelMBean() {
		return shellScriptPanelMBean;
	}

	public void setShellScriptPanelMBean(ShellScriptPanelMBean shellScriptPanelMBean) {
		this.shellScriptPanelMBean = shellScriptPanelMBean;
	}

}
