package com.likya.tlossw.web.menu;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.ScenarioDocument.Scenario;
import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument.TlosProcessData;
import com.likya.tlossw.model.tree.WsJobNode;
import com.likya.tlossw.model.tree.WsScenarioNode;
import com.likya.tlossw.web.db.DBOperations;
import com.likya.tlossw.web.utils.ConstantDefinitions;

@ManagedBean
@ViewScoped
public class JobTemplatesTree implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8098932616833921105L;

	@ManagedProperty(value = "#{dbOperations}")
	private DBOperations dbOperations;
	private TreeNode root;

	private TreeNode selectedJS;

	private TreeNode selectedTreeNode;

	@PostConstruct
	public void initJSTree() {
		TlosProcessData tlosProcessData = dbOperations.getTlosTemplateDataXml();

		System.out.println("Job Template Tree olusturuluyor ..");

		constructJSTree(tlosProcessData);

//		addMessage("Job Template Tree olusturuldu !");
		System.out.println("Job Template Tree olusturuldu !");
	}

	public void constructJSTree(TlosProcessData tlosProcessData) {
		root = new DefaultTreeNode("Root", null);

		setSelectedTreeNode(root);

		if (tlosProcessData.getJobList() != null) {
			for (JobProperties jobProperties : tlosProcessData.getJobList().getJobPropertiesArray()) {
				addJobNode(jobProperties, selectedTreeNode);
			}
		}
		constructTree(tlosProcessData.getScenarioArray());
	}

	public void addJobNode(JobProperties jobProperties, TreeNode selectedNode) {
		
		WsJobNode wsJobNode = new WsJobNode();
		
		wsJobNode.setName(jobProperties.getBaseJobInfos().getJsName());
		
		new DefaultTreeNode(ConstantDefinitions.TREE_JOB, wsJobNode, selectedNode);
	}

	public void constructTree(Scenario[] scenario) {
		for (Scenario children : scenario) {
			TreeNode scenarioNode = addScenario(children);

			if (children.getScenarioArray().length > 0) {
				selectedTreeNode = scenarioNode;
				constructTree(children.getScenarioArray());
			}
		}
	}

	public TreeNode addScenario(Scenario scenario) {
		
		WsScenarioNode wsScenarioNode = new WsScenarioNode();
		
		wsScenarioNode.setName(scenario.getBaseScenarioInfos().getJsName());
		
		TreeNode scenarioNode = new DefaultTreeNode(ConstantDefinitions.TREE_JOBGROUP, wsScenarioNode, selectedTreeNode);

		scenarioNode.setExpanded(true);

		if (scenario.getJobList() != null) {
			for (JobProperties jobProperties : scenario.getJobList().getJobPropertiesArray()) {
				addJobNode(jobProperties, scenarioNode);
			}
		}

		return scenarioNode;
	}

	public void addMessage(String summary) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public DBOperations getDbOperations() {
		return dbOperations;
	}

	public void setDbOperations(DBOperations dbOperations) {
		this.dbOperations = dbOperations;
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public TreeNode getSelectedJS() {
		return selectedJS;
	}

	public void setSelectedJS(TreeNode selectedJS) {
		this.selectedJS = selectedJS;
	}

	public void onNodeExpand(NodeExpandEvent event) {
		/* FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Expanded", event.getTreeNode().toString());

		FacesContext.getCurrentInstance().addMessage(null, message); */
	}

	public void onNodeCollapse(NodeCollapseEvent event) {
		/* FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Collapsed", event.getTreeNode().toString());

		FacesContext.getCurrentInstance().addMessage(null, message); */
	}

	public void onNodeSelect(NodeSelectEvent event) {
		/* FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Selected", event.getTreeNode().toString());

		FacesContext.getCurrentInstance().addMessage(null, message); */
	}

	public void onNodeUnselect(NodeUnselectEvent event) {
		/* FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Unselected", event.getTreeNode().toString());

		FacesContext.getCurrentInstance().addMessage(null, message); */
	}

	public TreeNode getSelectedTreeNode() {
		return selectedTreeNode;
	}

	public void setSelectedTreeNode(TreeNode selectedTreeNode) {
		this.selectedTreeNode = selectedTreeNode;
	}

}