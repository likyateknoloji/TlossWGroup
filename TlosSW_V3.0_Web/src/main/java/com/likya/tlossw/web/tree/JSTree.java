package com.likya.tlossw.web.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
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
import com.likya.tlossw.model.DocMetaDataHolder;
import com.likya.tlossw.model.tree.WsJobNode;
import com.likya.tlossw.model.tree.WsNode;
import com.likya.tlossw.model.tree.WsScenarioNode;
import com.likya.tlossw.utils.CommonConstantDefinitions;
import com.likya.tlossw.web.TreeBaseBean;
import com.likya.tlossw.web.appmng.TraceBean;
import com.likya.tlossw.web.live.ScenarioMBean;
import com.likya.tlossw.web.utils.ConstantDefinitions;

@ManagedBean
@ViewScoped
public class JSTree extends TreeBaseBean implements Serializable {

	private static final long serialVersionUID = 995405464697116080L;

	@ManagedProperty(value = "#{scenarioMBean}")
	private ScenarioMBean scenarioMBean;

	private TreeNode root;

	private TreeNode selectedJS;

	private TreeNode selectedTreeNode;

	@PostConstruct
	public void initJSTree() {

		/*
		 * 
		 * String scopeId2 = getPassedParameter().get(CommonConstantDefinitions.EXIST_SCOPEID2);
		 * if (scopeId2 != null) {
		 * getSessionMediator().setScopeId2(Boolean.valueOf(scopeId2));
		 * }
		 * 
		 * getSessionMediator().setDocumentId2( CommonConstantDefinitions.EXIST_SJDATA );
		 * getSessionMediator().setDocumentScope( getSessionMediator().getDocumentId2(), getSessionMediator().getScopeId2() );
		 * 
		 * 
		 * long startTime = System.currentTimeMillis();
		 * 
		 * TlosProcessData tlosProcessData = getDbOperations().getTlosDataXml( getSessionMediator().getDocumentId2(), getWebAppUser().getId(),
		 * getDocumentScope(getSessionMediator().getDocumentId2()));
		 */

		long startTime = System.currentTimeMillis();

		TlosProcessData tlosProcessData = getTlosProcessData(CommonConstantDefinitions.EXIST_SJDATA);

		constructJSTree(tlosProcessData);

		// addMessage("jobTree", FacesMessage.SEVERITY_INFO,
		// "Job Tree olusturuldu !", null);
		System.out.println("Job Tree olusturuldu ! Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
	}

	public void constructJSTree(TlosProcessData tlosProcessData) {
		if (tlosProcessData != null) {
			WsScenarioNode rootNode = new WsScenarioNode();
			rootNode.setId(ConstantDefinitions.TREE_ROOTID);
			rootNode.setName(ConstantDefinitions.TREE_ROOT);

			root = new DefaultTreeNode(rootNode, null);

			WsScenarioNode wsScenarioNode = new WsScenarioNode();
			wsScenarioNode.setId(ConstantDefinitions.TREE_SCENARIOROOTID);
			wsScenarioNode.setName(resolveMessage("tlos.workspace.tree.scenario.root"));

			TreeNode scenarioRootNode = new DefaultTreeNode(ConstantDefinitions.TREE_SCENARIO, wsScenarioNode, root);
			scenarioRootNode.setExpanded(true);
			setSelectedTreeNode(scenarioRootNode);

			if (tlosProcessData.getJobList() != null) {
				for (JobProperties jobProperties : tlosProcessData.getJobList().getJobPropertiesArray()) {
					addJobNode(jobProperties, selectedTreeNode);
				}
			}
			constructTree(tlosProcessData.getScenarioArray());

		}
	}

	public void reconstructJSTree(String documentId) {

		TlosProcessData tlosProcessData = getDbOperations().getTlosDataXml(CommonConstantDefinitions.EXIST_SJDATA, getWebAppUser().getId(), getSessionMediator().getDocumentScope(CommonConstantDefinitions.EXIST_SJDATA));
		constructJSTree(tlosProcessData);
	}

	public void addJobNode(JobProperties jobProperties, TreeNode selectedNode) {

		WsJobNode wsJobNode = new WsJobNode();
		wsJobNode.setId(jobProperties.getID());
		wsJobNode.setJobType(jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getJobCommandType().intValue());
		wsJobNode.setName(jobProperties.getBaseJobInfos().getJsName());

		wsJobNode.setLeafIcon(getScenarioMBean().getJobIconsElement(jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getJobCommandType().toString()));

		wsJobNode.setLabelText(jobProperties.getBaseJobInfos().getJsName());

		new DefaultTreeNode(ConstantDefinitions.TREE_JOB, wsJobNode, selectedNode);

	}

	public void addJobNodeOld(JobProperties jobProperties, TreeNode selectedNode) {

		new DefaultTreeNode(ConstantDefinitions.TREE_JOB, jobProperties.getBaseJobInfos().getJsName() + "|" + jobProperties.getID(), selectedNode);

	}

	// Yeni tanimlanan isi agacta ilgili kisma ekliyor
	public void addJobNodeToScenarioPath(JobProperties jobProperties, String jobPathInScenario) {
		TreeNode selectedNode = root;

		StringTokenizer pathTokenizer = new StringTokenizer(jobPathInScenario, "/");

		while (pathTokenizer.hasMoreTokens()) {
			String scenarioId = pathTokenizer.nextToken();

			for (TreeNode node : selectedNode.getChildren()) {
				if (((WsNode) node.getData()).getId().equals(scenarioId)) {
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

		ArrayList<String> scenarioIdList = new ArrayList<>();
		StringTokenizer pathTokenizer = new StringTokenizer(scenarioPath, "/");

		// ilk gelen senaryo root oldugu icin onu listeye eklemiyor
		if (pathTokenizer.hasMoreTokens()) {
			pathTokenizer.nextToken();
		}

		while (pathTokenizer.hasMoreTokens()) {
			scenarioIdList.add(pathTokenizer.nextToken());
		}

		List<TreeNode> nodeList = selectedNode.getChildren().get(0).getChildren();
		deleteSubtree(scenarioIdList, 0, nodeList);
	}

	private void deleteSubtree(ArrayList<String> scenarioIdList, int index, List<TreeNode> nodeList) {
		boolean result = false;
		for (TreeNode node : nodeList) {

			String scenarioId = scenarioIdList.get(index);
			if (((WsNode) node.getData()).getId().equals(scenarioId)) {
				if (scenarioIdList.size() == index + 1) {
					result = true;
				} else {
					deleteSubtree(scenarioIdList, ++index, node.getChildren());
				}
			}

			if (result) {
				TreeNode parent = node.getParent();
				parent.getChildren().remove(node);

				break;
			}
		}
	}

	// silinen işi ağaçtan kaldırıyor
	public void removeJobNode(String jobPath, String jobId) {
		TreeNode selectedNode = root;

		ArrayList<String> scenarioIdList = new ArrayList<>();
		StringTokenizer pathTokenizer = new StringTokenizer(jobPath, "/");

		// ilk gelen senaryo root oldugu icin onu listeye eklemiyor
		if (pathTokenizer.hasMoreTokens()) {
			pathTokenizer.nextToken();
		}

		while (pathTokenizer.hasMoreTokens()) {
			scenarioIdList.add(pathTokenizer.nextToken());
		}

		List<TreeNode> nodeList = selectedNode.getChildren().get(0).getChildren();
		deleteJobNode(scenarioIdList, 0, nodeList, jobId);
	}

	private void deleteJobNode(ArrayList<String> scenarioIdList, int index, List<TreeNode> nodeList, String jobId) {
		boolean result = false;
		for (TreeNode node : nodeList) {

			// serbest islerde
			if (scenarioIdList.size() == 0) {
				if (((WsNode) node.getData()).getId().equals(jobId)) {
					TreeNode parent = node.getParent();
					parent.getChildren().remove(node);

					break;
				}

			} else {
				String scenarioId = scenarioIdList.get(index);

				if (((WsNode) node.getData()).getId().equals(scenarioId)) {
					if (scenarioIdList.size() == index + 1) {
						result = true;
					} else {
						deleteJobNode(scenarioIdList, ++index, node.getChildren(), jobId);
					}
				}

				if (result) {
					for (TreeNode jobNode : node.getChildren()) {
						if (((WsNode) jobNode.getData()).getId().equals(jobId)) {
							TreeNode parent = jobNode.getParent();
							parent.getChildren().remove(jobNode);

							break;
						}
					}

					break;
				}
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

		WsScenarioNode wsScenarioNode = new WsScenarioNode();

		wsScenarioNode.setName(scenario.getBaseScenarioInfos().getJsName());
		wsScenarioNode.setId(scenario.getID());

		TreeNode scenarioNode = new DefaultTreeNode(ConstantDefinitions.TREE_SCENARIO, wsScenarioNode, selectedTreeNode);

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

	public ScenarioMBean getScenarioMBean() {
		return scenarioMBean;
	}

	public void setScenarioMBean(ScenarioMBean scenarioMBean) {
		this.scenarioMBean = scenarioMBean;
	}

}
