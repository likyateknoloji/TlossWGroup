package com.likya.tlossw.web.tree;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.ScenarioDocument.Scenario;
import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument.TlosProcessData;
import com.likya.tlossw.web.TlosSWBaseBean;

@ManagedBean
@ViewScoped
public class JSTree extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = 995405464697116080L;

	private TreeNode root;

	private TreeNode selectedJS;

	private TreeNode selectedTreeNode;

	@PostConstruct
	public void initJSTree() {
		TlosProcessData tlosProcessData = getDbOperations().getTlosDataXml("tlosSWData10.xml");
		System.out.println("Tree has been loaded !!");

		System.out.println("Job Tree olusturuluyor ..");

		constructJSTree(tlosProcessData);

		addMessage("jobTree", FacesMessage.SEVERITY_INFO, "Job Tree olusturuldu !", null);
		System.out.println("Job Tree olusturuldu !");
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
		@SuppressWarnings("unused")
		TreeNode jobNode = new DefaultTreeNode("job", jobProperties.getBaseJobInfos().getJsName()+" | "+jobProperties.getID(), selectedNode);
	}

	public void constructTree(Scenario[] scenario) {
		for (Scenario children : scenario) {
			TreeNode scenarioNode = addScenario(children);

			if (children.getScenarioArray().length > 0) {
				selectedTreeNode = scenarioNode;
				constructTree(children.getScenarioArray());
			}
		}

		selectedTreeNode = selectedTreeNode.getParent();
	}

	public TreeNode addScenario(Scenario scenario) {
		TreeNode scenarioNode = new DefaultTreeNode("scenario", scenario.getBaseScenarioInfos().getJsName()+" | "+scenario.getID(), selectedTreeNode);

		scenarioNode.setExpanded(true);

		if (scenario.getJobList() != null) {
			for (JobProperties jobProperties : scenario.getJobList().getJobPropertiesArray()) {
				addJobNode(jobProperties, scenarioNode);
			}
		}

		return scenarioNode;
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
		addMessage("jobTree", FacesMessage.SEVERITY_INFO, event.getTreeNode().toString() + " expanded", null);
	}

	public void onNodeCollapse(NodeCollapseEvent event) {
		addMessage("jobTree", FacesMessage.SEVERITY_INFO, event.getTreeNode().toString() + " collapsed", null);
	}

	public void onNodeSelect(NodeSelectEvent event) {
		addMessage("jobTree", FacesMessage.SEVERITY_INFO, event.getTreeNode().toString() + " selected", null);
	}

	public void onNodeUnselect(NodeUnselectEvent event) {
		addMessage("jobTree", FacesMessage.SEVERITY_INFO, event.getTreeNode().toString() + " unselected", null);
	}

	public TreeNode getSelectedTreeNode() {
		return selectedTreeNode;
	}

	public void setSelectedTreeNode(TreeNode selectedTreeNode) {
		this.selectedTreeNode = selectedTreeNode;
	}

}
