package com.likya.tlossw.web.definitions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.model.TreeNode;

import com.likya.tlos.model.xmlbeans.common.SchedulingAlgorithmDocument.SchedulingAlgorithm;
import com.likya.tlossw.web.tree.JSTree;

@ManagedBean(name = "scenarioDefinitionMBean")
@ViewScoped
public class ScenarioDefinitionMBean extends JobBaseBean implements Serializable {

	private static final long serialVersionUID = -8027723328721838174L;

	@ManagedProperty(value = "#{jSTree}")
	private JSTree jsTree;

	@ManagedProperty(value = "#{jsDefinitionMBean}")
	private JSDefinitionMBean jsDefinitionMBean;

	private String scenarioName;
	private String comment;

	private boolean active = true;

	private boolean useCalendarDef = false;

	private Collection<SelectItem> schedulingAlgorithmList = null;
	private String selectedSchedulingAlgorithm;

	@PostConstruct
	public void init() {
		setJobInsertButton(true);
		initJobPanel();

		fillSchedulingAlgorithmList();
	}

	private void fillSchedulingAlgorithmList() {
		selectedSchedulingAlgorithm = SchedulingAlgorithm.FIRST_COME_FIRST_SERVED.toString();

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

	public void addNewScenario() {
		TreeNode selectedScenario = getJsTree().getSelectedJS();

		getJsDefinitionMBean().setJobDefCenterPanel(JSDefinitionMBean.SCENARIO_PAGE);

		// addMessage("jobTree", FacesMessage.SEVERITY_INFO,
		// selectedScenario.toString() + " içinde tanýmlanacak", null);
	}

	public JSTree getJsTree() {
		return jsTree;
	}

	public void setJsTree(JSTree jsTree) {
		this.jsTree = jsTree;
	}

	public JSDefinitionMBean getJsDefinitionMBean() {
		return jsDefinitionMBean;
	}

	public void setJsDefinitionMBean(JSDefinitionMBean jsDefinitionMBean) {
		this.jsDefinitionMBean = jsDefinitionMBean;
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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
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
