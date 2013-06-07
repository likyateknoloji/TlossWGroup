package com.likya.tlossw.web.definitions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlOptions;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.likya.tlos.model.xmlbeans.common.AgentChoiceMethodDocument.AgentChoiceMethod;
import com.likya.tlos.model.xmlbeans.common.ChoiceType;
import com.likya.tlos.model.xmlbeans.common.SchedulingAlgorithmDocument.SchedulingAlgorithm;
import com.likya.tlos.model.xmlbeans.data.AdvancedScenarioInfosDocument.AdvancedScenarioInfos;
import com.likya.tlos.model.xmlbeans.data.BaseScenarioInfosDocument.BaseScenarioInfos;
import com.likya.tlos.model.xmlbeans.data.ConcurrencyManagementDocument.ConcurrencyManagement;
import com.likya.tlos.model.xmlbeans.data.JobListDocument.JobList;
import com.likya.tlos.model.xmlbeans.data.JsIsActiveDocument.JsIsActive;
import com.likya.tlos.model.xmlbeans.data.ScenarioDocument.Scenario;
import com.likya.tlos.model.xmlbeans.data.TimeManagementDocument.TimeManagement;
import com.likya.tlos.model.xmlbeans.state.Status;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.tree.JSTree;
import com.likya.tlossw.web.utils.ConstantDefinitions;
import com.likya.tlossw.web.utils.DefinitionUtils;

@ManagedBean(name = "scenarioDefinitionMBean")
@ViewScoped
public class ScenarioDefinitionMBean extends JobBaseBean implements Serializable {

	private static final long serialVersionUID = -8027723328721838174L;

	@ManagedProperty(value = "#{jSTree}")
	private JSTree jsTree;

	private String scenarioName;
	private String comment;

	private boolean useCalendarDef = false;

	private Collection<SelectItem> schedulingAlgorithmList = null;
	private String selectedSchedulingAlgorithm;

	private String treePath;

	@PostConstruct
	public void init() {
		setScenario(true);
		setJsInsertButton(true);
		initScenarioPanel();

		fillSchedulingAlgorithmList();
	}

	public void initScenarioPanel() {
		fillAllLists();

		setScenario(Scenario.Factory.newInstance());

		BaseScenarioInfos baseScenarioInfos = BaseScenarioInfos.Factory.newInstance();
		getScenario().setBaseScenarioInfos(baseScenarioInfos);

		JobList jobList = JobList.Factory.newInstance();
		getScenario().setJobList(jobList);

		TimeManagement timeManagement = TimeManagement.Factory.newInstance();
		getScenario().setTimeManagement(timeManagement);

		ConcurrencyManagement concurrencyManagement = ConcurrencyManagement.Factory.newInstance();
		getScenario().setConcurrencyManagement(concurrencyManagement);

		resetScenarioPanelInputs();
	}

	private void resetScenarioPanelInputs() {
		resetPanelInputs();

		scenarioName = "";
		comment = "";
		useCalendarDef = false;
		selectedSchedulingAlgorithm = SchedulingAlgorithm.FIRST_COME_FIRST_SERVED.toString();
	}

	private void fillSchedulingAlgorithmList() {
		String algorithm = null;
		schedulingAlgorithmList = new ArrayList<SelectItem>();

		for (int i = 0; i < SchedulingAlgorithm.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			algorithm = SchedulingAlgorithm.Enum.forInt(i + 1).toString();
			item.setValue(algorithm);
			item.setLabel(algorithm);
			schedulingAlgorithmList.add(item);
		}
	}

	// sağ tıkla yeni senaryo ekle seçilince buraya geliyor
	public void addNewScenario() {
		resetScenarioPanelInputs();

		TreeNode selectedScenario = getJsTree().getSelectedJS();
		setScenarioTreePath(selectedScenario);

		setJsInsertButton(true);
		setJsUpdateButton(false);
	}

	public void insertJsAction() {
		fillScenarioProperties();
		insertScenarioDefinition();
	}

	public void updateJsAction() {
		fillScenarioProperties();
		updateScenarioDefinition();
	}

	// ekrandan girilen degerler scenario icine dolduruluyor
	public void fillScenarioProperties() {
		fillBaseScenarioInfos();
		fillTimeManagement();
		// şimdilik senaryolar arası bağımlılık yok
		// fillDependencyDefinitions();
		fillStateInfos();
		fillConcurrencyManagement();
		fillAlarmPreference();
		fillLocalParameters();
		fillAdvancedScenarioInfos();
	}

	private void fillBaseScenarioInfos() {
		BaseScenarioInfos baseScenarioInfos = getScenario().getBaseScenarioInfos();

		baseScenarioInfos.setJsName(scenarioName);
		baseScenarioInfos.setComment(comment);

		if (isJsActive()) {
			baseScenarioInfos.setJsIsActive(JsIsActive.YES);
		} else {
			baseScenarioInfos.setJsIsActive(JsIsActive.NO);
		}

		if (useCalendarDef) {
			baseScenarioInfos.setCalendarId(Integer.parseInt(getJsCalendar()));
		}

		// TODO login ekrani olmadigi icin simdilik 1 id'li kullaniciyi
		// ayarladim
		baseScenarioInfos.setUserId(1);
	}

	private void fillDependencyDefinitions() {
		// son durumda bagimlik tanimlanmamissa senaryo icindeki ilgili kismi
		// kaldiriyor
		if (getScenario().getDependencyList() != null && getScenario().getDependencyList().getItemArray().length == 0) {
			XmlCursor xmlCursor = getScenario().getDependencyList().newCursor();
			xmlCursor.removeXml();
		}
	}

	private void fillStateInfos() {
		// son durumda statu kodu tanimlanmamissa senaryo icindeki
		// ilgili kismi kaldiriyor
		if (getScenario().getScenarioStatusList() != null && getScenario().getScenarioStatusList().sizeOfScenarioStatusArray() == 0) {
			XmlCursor xmlCursor = getScenario().getScenarioStatusList().newCursor();
			xmlCursor.removeXml();
		}
	}

	private void fillAdvancedScenarioInfos() {
		AdvancedScenarioInfos advancedScenarioInfos = AdvancedScenarioInfos.Factory.newInstance();

		AgentChoiceMethod choiceMethod = AgentChoiceMethod.Factory.newInstance();
		choiceMethod.setStringValue(getAgentChoiceMethod());

		if (getAgentChoiceMethod().equals(ChoiceType.USER_MANDATORY_PREFERENCE.toString())) {
			choiceMethod.setAgentId(getSelectedAgent());
		}
		advancedScenarioInfos.setAgentChoiceMethod(choiceMethod);

		advancedScenarioInfos.setSchedulingAlgorithm(SchedulingAlgorithm.Enum.forString(selectedSchedulingAlgorithm));

		getScenario().setAdvancedScenarioInfos(advancedScenarioInfos);
	}

	public void insertScenarioDefinition() {
		if (!scenarioCheckUp() & getScenarioId()) {
			return;
		}

		if (getDbOperations().insertScenario(JSDefinitionMBean.JOB_DEFINITION_DATA, getScenarioXML(), treePath)) {
			TreeNode scenarioNode = new DefaultTreeNode("scenario", scenarioName + " | " + getScenario().getID(), getJsTree().getSelectedJS());
			scenarioNode.setExpanded(true);

			RequestContext context = RequestContext.getCurrentInstance();
			context.update("jsTreeForm:tree");

			addMessage("scenarioInsert", FacesMessage.SEVERITY_INFO, "tlos.success.scenario.insert", null);
		} else {
			addMessage("scenarioInsert", FacesMessage.SEVERITY_ERROR, "tlos.error.scenario.insert", null);
		}
	}

	public void updateScenarioDefinition() {
		if (getDbOperations().updateScenario(ConstantDefinitions.JOB_DEFINITION_DATA, treePath, getScenarioXML())) {
			addMessage("scenarioUpdate", FacesMessage.SEVERITY_INFO, "tlos.success.scenario.update", null);
		} else {
			addMessage("scenarioUpdate", FacesMessage.SEVERITY_ERROR, "tlos.error.scenario.update", null);
		}
	}

	public void initializeScenarioPanel(boolean insert) {
		TreeNode selectedScenario = getJsTree().getSelectedJS();
		setScenarioTreePath(selectedScenario);

		setJsInsertButton(insert);
		setJsUpdateButton(!insert);

		resetScenarioPanelInputs();
		fillScenarioPanel();
	}

	public void fillScenarioPanel() {
		fillBaseScenarioInfosTab();
		fillTimeManagementTab();
		// şimdilik senaryolar arası bağımlılık yok
		// fillDependencyDefinitionsTab();
		fillStateInfosTab();
		fillConcurrencyManagementTab();
		fillAlarmPreferenceTab();
		fillLocalParametersTab();
		fillAdvancedScenarioInfosTab();
	}

	private void fillBaseScenarioInfosTab() {
		if (getScenario() != null) {
			BaseScenarioInfos baseScenarioInfos = getScenario().getBaseScenarioInfos();

			scenarioName = baseScenarioInfos.getJsName();
			comment = baseScenarioInfos.getComment();

			if (baseScenarioInfos.getCalendarId() != 0) {
				useCalendarDef = true;
				setJsCalendar(baseScenarioInfos.getCalendarId() + "");
			} else {
				useCalendarDef = false;
			}

			if (baseScenarioInfos.getJsIsActive().equals(JsIsActive.YES)) {
				setJsActive(true);
			} else {
				setJsActive(false);
			}

		} else {
			System.out.println("scenario is NULL in fillBaseScenarioInfosTab !!");
		}
	}

	private void fillStateInfosTab() {
		if (getScenario() != null) {
			// durum tanimi yapildiysa alanlari dolduruyor
			if (getScenario().getScenarioStatusList() != null) {

				setManyJobStatusList(new ArrayList<SelectItem>());
				for (Status scenarioStatus : getScenario().getScenarioStatusList().getScenarioStatusArray()) {
					String statusName = scenarioStatus.getStatusName().toString();
					getManyJobStatusList().add(new SelectItem(statusName, statusName));
				}
			} else {
				setManyJobStatusList(null);
			}
		} else {
			System.out.println("scenario is NULL in fillStateInfosTab !!");
		}
	}

	public void fillConcurrencyManagementTab() {
		if (getScenario() != null) {
			setConcurrent(getScenario().getConcurrencyManagement().getConcurrent());
		} else {
			System.out.println("scenario is NULL in fillConcurrencyManagementTab !!");
		}
	}

	private void fillAdvancedScenarioInfosTab() {
		if (getScenario() != null) {
			if (getScenario().getAdvancedScenarioInfos() == null) {
				return;
			}

			AdvancedScenarioInfos advancedScenarioInfos = getScenario().getAdvancedScenarioInfos();

			// agent secme metodu
			if (advancedScenarioInfos.getAgentChoiceMethod() != null) {
				setAgentChoiceMethod(advancedScenarioInfos.getAgentChoiceMethod().getStringValue());

				if (getAgentChoiceMethod().equals(ChoiceType.USER_MANDATORY_PREFERENCE.toString())) {
					setSelectedAgent(advancedScenarioInfos.getAgentChoiceMethod().getAgentId());
				}
			}

			setSelectedSchedulingAlgorithm(advancedScenarioInfos.getSchedulingAlgorithm().toString());

		} else {
			System.out.println("scenario is NULL in fillAdvancedScenarioInfosTab !!");
		}
	}

	private boolean scenarioCheckUp() {
		String scenarioPath = treePath + "/dat:scenario/dat:baseScenarioInfos[com:jsName = '" + scenarioName + "']/..";

		Scenario scenarioDefinition = getDbOperations().getScenario(JSDefinitionMBean.JOB_DEFINITION_DATA, scenarioPath, scenarioName);

		if (scenarioDefinition != null && scenarioDefinition.getBaseScenarioInfos().getJsName().equals(scenarioName)) {
			addMessage("scenarioInsert", FacesMessage.SEVERITY_ERROR, "tlos.info.scenario.name.duplicate", null);
			return false;
		}

		return true;
	}

	private boolean getScenarioId() {
		int scenarioId = getDbOperations().getNextId(ConstantDefinitions.SCENARIO_ID);

		if (scenarioId < 0) {
			addMessage("scenarioInsert", FacesMessage.SEVERITY_ERROR, "tlos.error.scenario.getId", null);
			return false;
		}
		getScenario().setID(scenarioId + "");

		return true;
	}

	private String getScenarioXML() {
		QName qName = Scenario.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String scenarioXML = getScenario().xmlText(xmlOptions);

		return scenarioXML;
	}

	private void setScenarioTreePath(TreeNode scenarioNode) {
		String scenarioRoot = resolveMessage("tlos.workspace.tree.scenario.root");

		String path = "";
		if (!scenarioNode.getParent().getData().equals(ConstantDefinitions.TREE_ROOT)) {
			path = "/dat:scenario/dat:baseScenarioInfos[com:jsName = '" + DefinitionUtils.getXFromNameId(scenarioNode.getData().toString(), "Name") + "']/..";

			while (scenarioNode.getParent() != null && !scenarioNode.getParent().getData().equals(scenarioRoot)) {
				scenarioNode = scenarioNode.getParent();
				path = "/dat:scenario/dat:baseScenarioInfos[com:jsName = '" + DefinitionUtils.getXFromNameId(scenarioNode.getData().toString(), "Name") + "']/.." + path;
			}
		}

		path = "/dat:TlosProcessData" + path;

		treePath = path;
	}

	public JSTree getJsTree() {
		return jsTree;
	}

	public void setJsTree(JSTree jsTree) {
		this.jsTree = jsTree;
	}

	public String getScenarioName() {
		return scenarioName;
	}

	public void setScenarioName(String scenarioName) {
		this.scenarioName = scenarioName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isUseCalendarDef() {
		return useCalendarDef;
	}

	public void setUseCalendarDef(boolean useCalendarDef) {
		this.useCalendarDef = useCalendarDef;
	}

	public Collection<SelectItem> getSchedulingAlgorithmList() {
		return schedulingAlgorithmList;
	}

	public void setSchedulingAlgorithmList(Collection<SelectItem> schedulingAlgorithmList) {
		this.schedulingAlgorithmList = schedulingAlgorithmList;
	}

	public String getSelectedSchedulingAlgorithm() {
		return selectedSchedulingAlgorithm;
	}

	public void setSelectedSchedulingAlgorithm(String selectedSchedulingAlgorithm) {
		this.selectedSchedulingAlgorithm = selectedSchedulingAlgorithm;
	}

}
