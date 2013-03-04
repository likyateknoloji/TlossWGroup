package com.likya.tlossw.web.definitions;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.TreeNode;

import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.tree.JSTree;

@ManagedBean(name = "scenarioDefinitionMBean")
@ViewScoped
public class ScenarioDefinitionMBean extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = -8027723328721838174L;

	@ManagedProperty(value = "#{jSTree}")
	private JSTree jsTree;

	@ManagedProperty(value = "#{jsDefinitionMBean}")
	private JSDefinitionMBean jsDefinitionMBean;

	public void addNewScenario() {
		TreeNode selectedScenario = getJsTree().getSelectedJS();

		getJsDefinitionMBean().setJobDefCenterPanel(JSDefinitionMBean.SCENARIO_PAGE);

		addMessage("jobTree", FacesMessage.SEVERITY_INFO, selectedScenario.toString() + " içinde tanýmlanacak", null);
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

}
