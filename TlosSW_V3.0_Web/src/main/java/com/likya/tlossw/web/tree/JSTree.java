package com.likya.tlossw.web.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
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
import com.likya.tlossw.web.utils.ConstantDefinitions;

@ManagedBean
@ViewScoped
public class JSTree extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = 995405464697116080L;

	private TreeNode root;

	private TreeNode selectedJS;

	private TreeNode selectedTreeNode;

	@PostConstruct
	public void initJSTree() {
		TlosProcessData tlosProcessData = getDbOperations().getTlosDataXml(ConstantDefinitions.JOB_DEFINITION_DATA);
		System.out.println("Tree has been loaded !!");

		System.out.println("Job Tree olusturuluyor ..");

		constructJSTree(tlosProcessData);

		// addMessage("jobTree", FacesMessage.SEVERITY_INFO,
		// "Job Tree olusturuldu !", null);
		System.out.println("Job Tree olusturuldu !");
	}

	public void constructJSTree(TlosProcessData tlosProcessData) {

		root = new DefaultTreeNode(ConstantDefinitions.TREE_ROOT, null);
		TreeNode scenarioRootNode = new DefaultTreeNode("scenario", resolveMessage("tlos.workspace.tree.scenario.root"), root);
		scenarioRootNode.setExpanded(true);
		setSelectedTreeNode(scenarioRootNode);

		if (tlosProcessData.getJobList() != null) {
			for (JobProperties jobProperties : tlosProcessData.getJobList().getJobPropertiesArray()) {
				addJobNode(jobProperties, selectedTreeNode);
			}
		}
		constructTree(tlosProcessData.getScenarioArray());
	}

	public void addJobNode(JobProperties jobProperties, TreeNode selectedNode) {
		@SuppressWarnings("unused")
		TreeNode jobNode = new DefaultTreeNode("job", jobProperties.getBaseJobInfos().getJsName() + " | " + jobProperties.getID(), selectedNode);
	}

	// Yeni tanimlanan isi agacta ilgili kisma ekliyor
	public void addJobNodeToScenarioPath(JobProperties jobProperties, String jobPathInScenario) {
		TreeNode selectedNode = root;

		StringTokenizer pathTokenizer = new StringTokenizer(jobPathInScenario, "/");

		while (pathTokenizer.hasMoreTokens()) {
			String scenarioName = pathTokenizer.nextToken();

			for (TreeNode node : selectedNode.getChildren()) {
				if (node.getData().equals(scenarioName)) {
					selectedNode = node;
					break;
				}
			}
		}

		addJobNode(jobProperties, selectedNode);
	}

	// silinen senaryoyu ağaçtan kaldırıyor
	public void removeScenarioSubtree(String scenarioPath) {
		TreeNode selectedNode = root;

		ArrayList<String> scenarioNameList = new ArrayList<>();
		StringTokenizer pathTokenizer = new StringTokenizer(scenarioPath, "/");
		while (pathTokenizer.hasMoreTokens()) {
			scenarioNameList.add(pathTokenizer.nextToken());
		}

		List<TreeNode> nodeList = selectedNode.getChildren().get(0).getChildren();
		deleteSubtree(scenarioNameList, 0, nodeList);
	}

	private void deleteSubtree(ArrayList<String> scenarioNameList, int index, List<TreeNode> nodeList) {
		boolean result = false;
		for (TreeNode node : nodeList) {

			String scenarioName = scenarioNameList.get(index);
			if (node.getData().equals(scenarioName)) {
				if (scenarioNameList.size() == index + 1) {
					result = true;
				} else {
					deleteSubtree(scenarioNameList, ++index, node.getChildren());
				}
			}

			if (result) {
				TreeNode parent = node.getParent();
				parent.getChildren().remove(node);

				break;
			}
		}

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
		TreeNode scenarioNode = new DefaultTreeNode("scenario", scenario.getBaseScenarioInfos().getJsName() + " | " + scenario.getID(), selectedTreeNode);

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
		// addMessage("jobTree", FacesMessage.SEVERITY_INFO,
		// event.getTreeNode().toString() + " expanded", null);
	}

	public void onNodeCollapse(NodeCollapseEvent event) {
		// addMessage("jobTree", FacesMessage.SEVERITY_INFO,
		// event.getTreeNode().toString() + " collapsed", null);
	}

	public void onNodeSelect(NodeSelectEvent event) {
		// addMessage("jobTree", FacesMessage.SEVERITY_INFO,
		// event.getTreeNode().toString() + " selected", null);
	}

	public void onNodeUnselect(NodeUnselectEvent event) {
		// addMessage("jobTree", FacesMessage.SEVERITY_INFO,
		// event.getTreeNode().toString() + " unselected", null);
	}

	public TreeNode getSelectedTreeNode() {
		return selectedTreeNode;
	}

	public void setSelectedTreeNode(TreeNode selectedTreeNode) {
		this.selectedTreeNode = selectedTreeNode;
	}

}
