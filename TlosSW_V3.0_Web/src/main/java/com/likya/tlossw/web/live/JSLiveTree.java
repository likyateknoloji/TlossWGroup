package com.likya.tlossw.web.live;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlossw.model.client.spc.JobInfoTypeClient;
import com.likya.tlossw.model.client.spc.SpcInfoTypeClient;
import com.likya.tlossw.model.tree.GunlukIslerNode;
import com.likya.tlossw.model.tree.InstanceNode;
import com.likya.tlossw.model.tree.JobNode;
import com.likya.tlossw.model.tree.ScenarioNode;
import com.likya.tlossw.model.tree.TlosSpaceWideNode;
import com.likya.tlossw.utils.CommonConstantDefinitions;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.common.Security;
import com.likya.tlossw.web.utils.ConstantDefinitions;
import com.likya.tlossw.web.utils.DecorationUtils;
import com.likya.tlossw.webclient.TEJmxMpClient;

@ManagedBean(name = "jSLiveTree")
@ViewScoped
public class JSLiveTree extends TlosSWBaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2287729115524041857L;

	private TreeNode root;

	// private TreeNode calisanIsler;

	private TreeNode selectedJS;

	private TreeNode selectedTreeNode;

	private TlosSpaceWideNode tlosSpaceWideNode = null;

	private CacheBase liveTreeCache = null;

	private DefaultTreeNode dummyNode = new DefaultTreeNode(ConstantDefinitions.TREE_DUMMY, null);

	@ManagedProperty(value = "#{security}")
	private Security security;

	@PostConstruct
	public void initJSLiveTree() {
		
		if(getPassedParameter().get(CommonConstantDefinitions.EXIST_DOCID) != null) {
			getSessionMediator().getWebAppUser().setViewRoleId(getPassedParameter().get(CommonConstantDefinitions.EXIST_DOCID));
		}

		// tlosSpaceWideNode = new TlosSpaceWideNode();
		// tlosSpaceWideNode = TEJmxMpClient.getLiveTreeInfo(tlosSpaceWideNode);
		// //tlosSpaceWideNode.getGunlukIslerNode().getInstanceNodes().get(key);
		// TlosProcessData tlosProcessData = getDbOperations().getTlosDataXml("tlosSWData10.xml");
		// System.out.println("Tree has been loaded !!");
		//
		// System.out.println("Job Tree olusturuluyor ..");
		//
		constructJSTree();
		//
		// // addMessage("jobTree", FacesMessage.SEVERITY_INFO,
		// // "Job Tree olusturuldu !", null);
		// System.out.println("Job Tree olusturuldu !");

	}

	public void constructJSTree() {

		String testString = resolveMessage("tlos.workspace.pannel.job.timeManagement");
		System.out.println(testString);

		root = new DefaultTreeNode(ConstantDefinitions.TREE_ROOT, resolveMessage("root"), null);
		DefaultTreeNode calisanIsler = new DefaultTreeNode(ConstantDefinitions.TREE_CALISANISLER, resolveMessage("likya.agac.calisan.isler"), root);

		calisanIsler.getChildren().add(dummyNode);

		// TreeNode scenarioRootNode = new DefaultTreeNode("scenario", resolveMessage("tlos.workspace.tree.scenario.root"), root);
		// scenarioRootNode.setExpanded(true);
		// setSelectedTreeNode(scenarioRootNode);
		//
		// if (tlosProcessData.getJobList() != null) {
		// for (JobProperties jobProperties : tlosProcessData.getJobList().getJobPropertiesArray()) {
		// addJobNode(jobProperties, selectedTreeNode);
		// }
		// }

		// constructTree(tlosProcessData.getScenarioArray());

		constructInstanceNodes();

	}

	private void constructInstanceNodes() {
		// if (security.get("Instance").equals(Boolean.TRUE)) {

		// ArrayList<String> instanceIds = TEJmxMpClient.retrieveInstanceIds(new JmxUser());

		// constructInstanceNodes(new HashSet<String>(instanceIds));

		liveTreeCache = new CacheBase();
		tlosSpaceWideNode = new TlosSpaceWideNode();
		// }
	}

	private void constructInstanceNodes(Set<String> instanceIds) {

		Iterator<String> keyIterator = instanceIds.iterator();

		while (keyIterator.hasNext()) {
			String instanceId = keyIterator.next();
			addInstance(instanceId);
		}
	}

	public void addInstance(String instanceId) {
		DefaultTreeNode calisanIsler = (DefaultTreeNode) root.getChildren().get(0);
		DefaultTreeNode instanceNode = new DefaultTreeNode(ConstantDefinitions.TREE_INSTANCE, new InstanceNode(instanceId), calisanIsler);
		instanceNode.getChildren().add(dummyNode);
		instanceNode.setExpanded(false);
	}

	public void addJobNode(JobProperties jobProperties, TreeNode selectedNode) {
		@SuppressWarnings("unused")
		TreeNode jobNode = new DefaultTreeNode(ConstantDefinitions.TREE_JOB, jobProperties.getBaseJobInfos().getJsName() + " | " + jobProperties.getID(), selectedNode);
	}

	// public void constructTree(Scenario[] scenario) {
	// for (Scenario children : scenario) {
	// TreeNode scenarioNode = addScenario(children);
	//
	// if (children.getScenarioArray().length > 0) {
	// selectedTreeNode = scenarioNode;
	// constructTree(children.getScenarioArray());
	// }
	// }
	//
	// selectedTreeNode = selectedTreeNode.getParent();
	// }
	//
	// public TreeNode addScenario(Scenario scenario) {
	// TreeNode scenarioNode = new DefaultTreeNode(ConstantDefinitions.TREE_SCENARIO, scenario.getBaseScenarioInfos().getJsName() + " | " + scenario.getID(), selectedTreeNode);
	//
	// scenarioNode.setExpanded(true);
	//
	// if (scenario.getJobList() != null) {
	// for (JobProperties jobProperties : scenario.getJobList().getJobPropertiesArray()) {
	// addJobNode(jobProperties, scenarioNode);
	// }
	// } else {
	// scenarioNode.getChildren().add(dummyNode);
	// }
	//
	// return scenarioNode;
	// }

	public void treeAction(AjaxBehaviorEvent event) {
		// log.info("LiveNavigationTree2 : treeAction  Begin :" + Utils.getCurrentTimeWithMilliseconds());
		renderLiveTree();
		// log.info("LiveNavigationTree2 : treeAction  End :" + Utils.getCurrentTimeWithMilliseconds());
	}

	public void renderLiveTree() {

		// log.debug("LiveNavigationTree2 : renderLiveTree  Begin :" + Utils.getCurrentTimeWithMilliseconds());
		Object liveTree = null;

		if (liveTreeCache != null && tlosSpaceWideNode != null) {
			liveTree = liveTreeCache.get(((Object) tlosSpaceWideNode).hashCode());
		}

		if (liveTree != null) {
			tlosSpaceWideNode = (TlosSpaceWideNode) liveTree;
			// log.debug("LiveNavigationTree2 : renderLiveTree  CacheData :" + Utils.getCurrentTimeWithMilliseconds());
		} else {
			if (root.getChildCount() > 0) {
				DefaultTreeNode calisanIsler = (DefaultTreeNode) root.getChildren().get(0);
				TlosSpaceWideNode tlosSpaceWideInputNode = preparePreRenderLiveTreeData(calisanIsler);
				
				//sunucudan guncel is listelerini aliyor
				tlosSpaceWideNode = TEJmxMpClient.getLiveTreeInfo(getWebAppUser(), tlosSpaceWideInputNode);
				if (tlosSpaceWideNode == null) {
					System.out.println("tlosSpaceWideNode == null");
				}
				if (liveTreeCache == null) {
					System.out.println("liveTreeCache == null");
				}
				liveTreeCache.put(((Object) tlosSpaceWideNode).hashCode(), tlosSpaceWideNode);
				// log.debug("LiveNavigationTree2 : renderLiveTree  EngineData :" + Utils.getCurrentTimeWithMilliseconds());
			}
		}

		if (root.getChildCount() > 0) {
			DefaultTreeNode calisanIsler = (DefaultTreeNode) root.getChildren().get(0);
			prepareRenderLiveTree(calisanIsler, tlosSpaceWideNode);
		}

	}

	//agacin datasi alinip render icin hazirlaniyor
	private TlosSpaceWideNode preparePreRenderLiveTreeData(TreeNode calisanIslerNode) {

		TlosSpaceWideNode tlosSpaceWideNode = new TlosSpaceWideNode();
		GunlukIslerNode gunlukIslerNode = new GunlukIslerNode();
		tlosSpaceWideNode.setGunlukIslerNode(gunlukIslerNode);

		// Gelen senaryoNode Gunluk Isler

		// Gunluk Isler expanded
		if (calisanIslerNode.isExpanded()) {

			// Bunlar instance listesi
			int size = calisanIslerNode.getChildCount();

			for (int i = 0; i < size; i++) {

				// instance aliniyor
				TreeNode tmpInstanceFolder = calisanIslerNode.getChildren().get(i);

				tmpInstanceFolder.getChildren().remove(dummyNode);

				if (tmpInstanceFolder.isExpanded()) {

					String instanceId = ((InstanceNode) tmpInstanceFolder.getData()).getInstanceId();
					InstanceNode instanceNode = new InstanceNode(instanceId);

					// instance icindeki senaryo sayisina bakiyoruz
					int numberOfScenariosInInstance = tmpInstanceFolder.getChildCount();

					for (int j = 0; j < numberOfScenariosInInstance; j++) {

						// Her bir senaryoyu aliyoruz
						TreeNode tmpScenario = tmpInstanceFolder.getChildren().get(j);
						ScenarioNode expendedNode = preRenderLiveTreeRecursive(tmpScenario);

						if (expendedNode != null) {
							instanceNode.getScenarioNodeMap().put(expendedNode.getSpcInfoTypeClient().getSpcId(), expendedNode);
						}
					}
					gunlukIslerNode.getInstanceNodes().put(instanceId, instanceNode);
				}

			}
		}
		return tlosSpaceWideNode;
	}

	private ScenarioNode preRenderLiveTreeRecursive(TreeNode scenarioNode) {

		ScenarioNode myScenarioNode = null;

		if (scenarioNode.isExpanded()) {
			myScenarioNode = new ScenarioNode();
			myScenarioNode.setSpcInfoTypeClient(((ScenarioNode) scenarioNode.getData()).getSpcInfoTypeClient());

			for (int i = 0; i < scenarioNode.getChildCount(); i++) {
				DefaultTreeNode tmpScenario = (DefaultTreeNode) scenarioNode.getChildren().get(i);
				if (!(scenarioNode.getData() instanceof ScenarioNode)) {
					continue;
				}
				ScenarioNode expendedNode = preRenderLiveTreeRecursive(tmpScenario);
				if (expendedNode != null) {
					myScenarioNode.getScenarioNodes().add(expendedNode);
				}
			}
		}

		return myScenarioNode;
	}

	private TreeNode prepareRenderLiveTree(TreeNode calisanIsler, TlosSpaceWideNode tlosSpaceWideNode) {

		if (calisanIsler.getChildCount() > 0) {
			// treeRendered = true;
		}

		GunlukIslerNode serverGunlukIslerNode = tlosSpaceWideNode.getGunlukIslerNode();

		// 1. asama instance lar eklenecek
		calisanIsler.getChildren().clear();
		constructInstanceNodes(serverGunlukIslerNode.getInstanceNodes().keySet());

		if (calisanIsler.getChildren().size() == 0) {
			calisanIsler.getChildren().add(dummyNode);
		} else {
			Iterator<TreeNode> gunlukIslerIterator = calisanIsler.getChildren().iterator();

			while (gunlukIslerIterator.hasNext()) {
				DefaultTreeNode instanceTreeNode = (DefaultTreeNode) gunlukIslerIterator.next();

				String instanceId = ((InstanceNode) instanceTreeNode.getData()).getInstanceId();
				if (serverGunlukIslerNode.getInstanceNodes().get(instanceId).getScenarioNodeMap().size() > 0) {
					constructLiveTree(instanceTreeNode, serverGunlukIslerNode.getInstanceNodes().get(instanceId).getScenarioNodeMap());
					instanceTreeNode.setExpanded(true);
				}
			}
			// System.out.println("prepareRenderLiveTree . Gunluk isler hascode-->" + gunlukIsler.hashCode());
		}
		return calisanIsler;
	}

	private void constructLiveTree(TreeNode instanceNode, HashMap<String, ScenarioNode> serverScenarioNodes) {

		instanceNode.getChildren().clear();

		Iterator<String> keyIterator = serverScenarioNodes.keySet().iterator();

		while (keyIterator.hasNext()) {

			String scenarioId = keyIterator.next();
			
			ScenarioNode serverScenarioNode = serverScenarioNodes.get(scenarioId);
			
			SpcInfoTypeClient spcInfoTypeClient = new SpcInfoTypeClient(serverScenarioNode.getSpcInfoTypeClient());

			ScenarioNode scenarioNode = new ScenarioNode();

			scenarioNode.setId(spcInfoTypeClient.getJsId());
			scenarioNode.setName(spcInfoTypeClient.getJsName());

			if (spcInfoTypeClient.isSerbestFolder()) {
				scenarioNode.setId(resolveMessage("tlos.live.tree.free"));
			}
			if (spcInfoTypeClient.getJsName() == null) {
				scenarioNode.setId(spcInfoTypeClient.getSpcId());
			}

			scenarioNode.setInstanceId(((InstanceNode) instanceNode.getData()).getInstanceId());
			scenarioNode.setSpcInfoTypeClient(spcInfoTypeClient);

			TreeNode scenarioNodeTree = new DefaultTreeNode(ConstantDefinitions.TREE_SCENARIO, scenarioNode, instanceNode);
			scenarioNodeTree.getChildren().add(dummyNode);
			scenarioNodeTree.setExpanded(false);

			System.out.println();
			System.out.println("************************************************************");
			System.out.println();
			System.out.println("Working for scenario : " + spcInfoTypeClient.getJsName());
			
			if (serverScenarioNode.getScenarioNodes().size() > 0 || serverScenarioNode.getJobNodes().size() > 0) {
				System.out.println("Cleaning children of scenario : " + scenarioNode.getName());
				scenarioNodeTree.getChildren().clear();
				scenarioNodeTree.setExpanded(true);
				System.out.println("Recursing for scenario : " + spcInfoTypeClient.getJsName());
				System.out.println("Scenario : " + serverScenarioNode.getName() + " Job List Size : " + serverScenarioNode.getJobNodes().size() + " Scenario List Size " + serverScenarioNode.getScenarioNodes().size());
				renderLiveTreeRecursive(scenarioNodeTree, serverScenarioNode);
			}
		}
	}

	private void renderLiveTreeRecursive(TreeNode scenarioNode, ScenarioNode serverScenarioNode) {

		constructJobNodes(scenarioNode, serverScenarioNode.getJobNodes());

		for (ScenarioNode tmpScenarioNode : serverScenarioNode.getScenarioNodes()) {
			SpcInfoTypeClient spcInfoTypeClient = new SpcInfoTypeClient(tmpScenarioNode.getSpcInfoTypeClient());

			tmpScenarioNode.setName(spcInfoTypeClient.getJsName());
			tmpScenarioNode.setId(spcInfoTypeClient.getJsId());
			
			TreeNode scenarioNodeTree = new DefaultTreeNode(ConstantDefinitions.TREE_SCENARIO, tmpScenarioNode, scenarioNode);
			scenarioNodeTree.getChildren().add(dummyNode);
			scenarioNodeTree.setExpanded(false);

			if (tmpScenarioNode.getScenarioNodes().size() > 0 || tmpScenarioNode.getJobNodes().size() > 0) {
				scenarioNodeTree.getChildren().clear();
				scenarioNodeTree.setExpanded(true);
				renderLiveTreeRecursive(scenarioNodeTree, tmpScenarioNode);
			}

		}

	}

	public void constructJobNodes(TreeNode scenarioNode, ArrayList<JobNode> jobNodes) {

		Iterator<JobNode> jobNodeIterator = jobNodes.iterator();

		while (jobNodeIterator.hasNext()) {
			
			JobNode tmpJobNode = jobNodeIterator.next();
			JobInfoTypeClient jobInfoTypeClient = tmpJobNode.getJobInfoTypeClient();

			String jobText = jobInfoTypeClient.getJobKey();
			JobNode jobNode = new JobNode();
			jobNode.setId(tmpJobNode.getId());
			jobNode.setLabelText(jobText);
			
			// job.setLeafIcon(jobImageSetter(jobInfoTypeClient.getLiveStateInfo()));
			jobNode.setName(jobInfoTypeClient.getJobKey());
			if (jobInfoTypeClient.getLiveStateInfo() == null) {
				System.out.println("jobInfoTypeClient.getLiveStateInfo() == null");
			}
			jobNode.setLeafIcon(DecorationUtils.jobImageSetter(jobInfoTypeClient.getLiveStateInfo()));
			jobNode.setPath(jobInfoTypeClient.getTreePath());
			TreeNode scenarioNodeTree = new DefaultTreeNode(ConstantDefinitions.TREE_JOB, jobNode, scenarioNode);
			scenarioNodeTree.setExpanded(false);

		}

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
		event.getTreeNode().setExpanded(true);
		treeAction(event);
		// addMessage("jobTree", FacesMessage.SEVERITY_INFO,
		// event.getTreeNode().toString() + " expanded", null);
	}

	public void onNodeCollapse(NodeCollapseEvent event) {
		event.getTreeNode().setExpanded(false);
		treeAction(event);
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

	public Security getSecurity() {
		return security;
	}

	public void setSecurity(Security security) {
		this.security = security;
	}

	public TlosSpaceWideNode getTlosSpaceWideNode() {
		return tlosSpaceWideNode;
	}

}
