package com.likya.tlossw.web.live;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

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

import com.likya.tlos.model.xmlbeans.agent.AgentTypeDocument.AgentType;
import com.likya.tlossw.model.jmx.JmxUser;
import com.likya.tlossw.model.tree.resource.MonitorAgentNode;
import com.likya.tlossw.model.tree.resource.ResourceListNode;
import com.likya.tlossw.model.tree.resource.ResourceNode;
import com.likya.tlossw.model.tree.resource.TlosAgentNode;
import com.likya.tlossw.model.tree.resource.TlosSWResourceNode;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.common.Security;
import com.likya.tlossw.web.utils.ConstantDefinitions;
import com.likya.tlossw.webclient.TEJmxMpClient;

@ManagedBean(name = "resourceLiveTree")
@ViewScoped
public class ResourceLiveTree extends TlosSWBaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2287729115524041857L;

	private TreeNode root;

	// private TreeNode calisanIsler;

	private TreeNode selectedJS;

	private TreeNode selectedTreeNode;

	private TlosSWResourceNode tlosSWResourceNode = null;

	private CacheBase liveTreeCache = null;

	public static final String NAGIOS_AGENT_NAME = "Kullanim Bilgisi";

	private DefaultTreeNode dummyNode = new DefaultTreeNode(ConstantDefinitions.TREE_DUMMY, null);

	@ManagedProperty(value = "#{security}")
	private Security security;

	@PostConstruct
	public void initResourceLiveTree() {

		// if (isInitiated)
		// return;
		//
		// isInitiated = true;
		// treeRendered = false;

		// kaynak listesi dugumu agaca ekleniyor
		// if (rootTreeNode != null &&
		// mediator.authorizeResource("AvailableResources")) {

		root = new DefaultTreeNode(ConstantDefinitions.TREE_ROOT, resolveMessage("root"), null);
		DefaultTreeNode kaynakListesi = new DefaultTreeNode(ConstantDefinitions.TREE_KAYNAKLISTESI, resolveMessage("likya.agac.kaynaklistesi"), root);

		kaynakListesi.getChildren().add(dummyNode);

		liveTreeCache = new CacheBase();
		tlosSWResourceNode = new TlosSWResourceNode();

		// }

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
	// TreeNode scenarioNode = new
	// DefaultTreeNode(ConstantDefinitions.TREE_SCENARIO,
	// scenario.getBaseScenarioInfos().getJsName() + " | " + scenario.getID(),
	// selectedTreeNode);
	//
	// scenarioNode.setExpanded(true);
	//
	// if (scenario.getJobList() != null) {
	// for (JobProperties jobProperties :
	// scenario.getJobList().getJobPropertiesArray()) {
	// addJobNode(jobProperties, scenarioNode);
	// }
	// } else {
	// scenarioNode.getChildren().add(dummyNode);
	// }
	//
	// return scenarioNode;
	// }

	public void treeAction(AjaxBehaviorEvent event) {
		// log.info("LiveNavigationTree2 : treeAction  Begin :" +
		// Utils.getCurrentTimeWithMilliseconds());
		renderLiveTree();
		// log.info("LiveNavigationTree2 : treeAction  End :" +
		// Utils.getCurrentTimeWithMilliseconds());
	}

	public void renderLiveTree() {

		Object liveTree = null;
		if (liveTreeCache != null && tlosSWResourceNode != null) {
			liveTree = liveTreeCache.get(((Object) tlosSWResourceNode).hashCode());
		}

		if (liveTree != null) {
			tlosSWResourceNode = (TlosSWResourceNode) liveTree;
		} else {
			if (root.getChildCount() > 0) {
				DefaultTreeNode kaynakListesi = (DefaultTreeNode) root.getChildren().get(0);
				TlosSWResourceNode tlosSpaceWideInputNode = preparePreRenderLiveTree(kaynakListesi);

				//sunucudan guncel makine listesini ve o makinelerdeki agent listelerini aliyor
				tlosSWResourceNode = TEJmxMpClient.getLiveResourceTreeInfo(new JmxUser(), tlosSpaceWideInputNode);
				liveTreeCache.put(((Object) tlosSWResourceNode).hashCode(), tlosSWResourceNode);
			}
		}

		if (root.getChildCount() > 0) {
			DefaultTreeNode kaynakListesi = (DefaultTreeNode) root.getChildren().get(0);
			// System.out.println(root.getChildCount());
			prepareRenderLiveTree(kaynakListesi, tlosSWResourceNode);
		}
	}

	//agacin datasi alinip render icin hazirlaniyor
	private TlosSWResourceNode preparePreRenderLiveTree(TreeNode kaynakListesiNode) {

		TlosSWResourceNode tlosSWResourceNode = new TlosSWResourceNode();
		ResourceListNode resourceListNode = new ResourceListNode();
		tlosSWResourceNode.setResourceListNode(resourceListNode);
		
		// Kaynak Listesi dugumu ekranda acilmissa buraya giriyor
		if (kaynakListesiNode.isExpanded()) {

			kaynakListesiNode.getChildren().remove(dummyNode);
			
			// Kaynak Listesi dugumunun altinda kac tane kaynak oldugu
			int size = kaynakListesiNode.getChildCount();

			for (int i = 0; i < size; i++) {

				// ilk kaynak aliniyor
				TreeNode resourceTreeNode = kaynakListesiNode.getChildren().get(i);
				resourceTreeNode.getChildren().remove(dummyNode);

				//ele alinan resourceNode ekranda expand edildiyse altindaki agentlarla ilgileniyor
				if (resourceTreeNode.isExpanded()) {

					ResourceNode resourceNode = ((ResourceNode) resourceTreeNode.getData());
					// String resourceName = resourceNode.getResourceInfoTypeClient().getResourceName();

					// kaynak icindeki agent sayisina bakiyoruz
					int numberOfAgentsInResource = resourceTreeNode.getChildCount();

					for (int j = 0; j < numberOfAgentsInResource; j++) {

						DefaultTreeNode tmpAgent = (DefaultTreeNode) resourceTreeNode.getChildren().get(j);

						//tlos/nagios ayrimina gore agentlar duzenleniyor
						if (tmpAgent.toString().equals(NAGIOS_AGENT_NAME)) {
							MonitorAgentNode expandedNode = preRenderLiveNagiosAgentTree(tmpAgent);

							if (expandedNode != null) {
								resourceNode.setMonitorAgentNode(expandedNode);
							}
						} else {
							TlosAgentNode expandedNode = preRenderLiveTlosAgentTree(tmpAgent);

							if (expandedNode != null) {
								resourceNode.getTlosAgentNodes().put(expandedNode.getTlosAgentInfoTypeClient().getAgentId(), expandedNode);
							}
						}
					}
					resourceListNode.getResourceNodes().add(resourceNode);
				}
			}
		}

		return tlosSWResourceNode;

	}
	
	private MonitorAgentNode preRenderLiveNagiosAgentTree(DefaultTreeNode nagiosAgentNode) {

		MonitorAgentNode myNagiosAgentNode = null;

		return myNagiosAgentNode;
	}
	
	private TlosAgentNode preRenderLiveTlosAgentTree(DefaultTreeNode tlosAgentNode) {

		TlosAgentNode myTlosAgentNode = null;
		
		return myTlosAgentNode;
	}

	private TreeNode prepareRenderLiveTree(TreeNode kaynakListesi, TlosSWResourceNode tlosSWResourceNode) {
		
		if(kaynakListesi.getChildCount() > 0) {
			// treeRendered = true;
		}

		ResourceListNode serverResourceListNode = tlosSWResourceNode.getResourceListNode();
		
		// 1. asama: kaynak listesine tanimli makineler ekleniyor
		kaynakListesi.getChildren().clear();
		constructResourceNodes(kaynakListesi, serverResourceListNode.getResourceNodes());

		if (kaynakListesi.getChildren().size() == 0) {
			kaynakListesi.getChildren().add(dummyNode);
		} else {
			
			Iterator<TreeNode> localResourceListIterator = kaynakListesi.getChildren().iterator();

			while (localResourceListIterator.hasNext()) {
				
				DefaultTreeNode resourceTreeNode = (DefaultTreeNode) localResourceListIterator.next();
				
				ResourceNode resourceNode = ((ResourceNode) resourceTreeNode.getData());
				
				String resourceName = resourceNode.getResourceInfoTypeClient().getResourceName();
				
				//ilgili makinenin agent nodelarini ekleyip agaci tamamliyor
				for(int i = 0; i < serverResourceListNode.getResourceNodes().size(); i++) {
					
					if (serverResourceListNode.getResourceNodes().get(i).getResourceInfoTypeClient().getResourceName().equals(resourceName)) {
						
						if(serverResourceListNode.getResourceNodes().get(i).getTlosAgentNodes().size() > 0) {
							constructLiveResourceTree(resourceTreeNode, serverResourceListNode.getResourceNodes().get(i));
							resourceTreeNode.setExpanded(true);
						} else {
							resourceTreeNode.getChildren().add(dummyNode);
							resourceTreeNode.setExpanded(false);
						}
					}
				}
			}
		}
		
		return kaynakListesi;
	}
	
	//sunucudan gelen makine bilgileri ekranda gosterilmek uzere set ediliyor
	private void constructResourceNodes(TreeNode localResourceList, ArrayList<ResourceNode> resourceNodeList) {

		Iterator<ResourceNode> resourceNodeIterator = resourceNodeList.iterator();

		while (resourceNodeIterator.hasNext()) {
			
			ResourceNode newResourceNode = resourceNodeIterator.next();

//			ResourceInfoTypeClient resourceInfoTypeClient = new ResourceInfoTypeClient(newResourceNode.getResourceInfoTypeClient());
			
//			LiveResourceNavigationContentBean resource = new LiveResourceNavigationContentBean();
			
//			ResourceFolderBean resourceFolder = new ResourceFolderBean();
			
//			resource.setNavigationSelection(navigationBean);
//			if(newResourceNode.getResourceInfoTypeClient().getIncludesServer()) {
//				resource.setText(newResourceNode.getResourceInfoTypeClient().getResourceName() + " (S)");
//				resourceFolder.setServer(true);
//			} else {
//				resource.setText(newResourceNode.getResourceInfoTypeClient().getResourceName());
//			}
//			resource.setMenuContentTitle("webmail.navigation.rootNode.title");
//			resource.setTemplateName("resourceViewPanel");
//			resource.setPageContent(true);
//			resource.setExpanded(false);
			
			/*
			if (!resourceInfoTypeClient.isActive()) {
				resource.setBranchContractedIcon("images/navigation_tree/tree_folder_closed_passive.gif");
				resource.setBranchExpandedIcon("images/navigation_tree/tree_folder_open_passive.gif");
			}
			*/
				
//			resourceFolder.setResourceName(newResourceNode.getResourceInfoTypeClient().getResourceName());
//			resourceFolder.setResourceInfoTypeClient(resourceInfoTypeClient);
//			resourceFolder.setResource(true);
//			resource.setResourceFolder(resourceFolder);
			
			TreeNode resourceNodeTree = new DefaultTreeNode(ConstantDefinitions.TREE_KAYNAK, newResourceNode, localResourceList);
			resourceNodeTree.setExpanded(false);
		}
	}

	//sunucudan alinan her makine icin bu metot cagiriliyor, lokaldeki agactaki ilgili makinenin altina sunucudan gelen agentlar ekleniyor
	private void constructLiveResourceTree(TreeNode resourceNode, ResourceNode serverResourceNode) {

		//Tlos agentlari alip agaca ekleyecek
		Iterator<Integer> keyIterator = serverResourceNode.getTlosAgentNodes().keySet().iterator();

		while (keyIterator.hasNext()) {
			Integer tlosAgentId = keyIterator.next();
			
			TlosAgentNode tlosAgentNode = serverResourceNode.getTlosAgentNodes().get(tlosAgentId);
			
			if(tlosAgentNode.getTlosAgentInfoTypeClient().getAgentType().toLowerCase().equals(AgentType.SERVER.toString().toLowerCase())) {
				tlosAgentNode.setLabelText("Sunucu");
			} else {
				tlosAgentNode.setLabelText("Agent" + serverResourceNode.getTlosAgentNodes().get(tlosAgentId).getTlosAgentInfoTypeClient().getAgentId());
			}
			
//			tlosAgent.setMenuContentTitle("webmail.navigation.rootNode.title");
//			tlosAgent.setTemplateName("tlosAgentViewPanel");
//			tlosAgent.setPageContent(true);
//			tlosAgent.setExpanded(false);
//			tlosAgent.setLeaf(true);
			
//			if (!tlosAgentInfoTypeClient.isOutJmxAvailable()) {
//				tlosAgent.setLeafIcon("images/navigation_tree/tree_node.gif");
//			}

//			ResourceFolderBean resourceFolder = new ResourceFolderBean();
//			resourceFolder.setResourceName(resourceNode.getResourceFolder().getResourceName());
//			resourceFolder.setResource(true);
//			resourceFolder.setTlosAgent(true);
//			resourceFolder.setTlosAgentInfoTypeClient(tlosAgentInfoTypeClient);
//			tlosAgent.setResourceFolder(resourceFolder);

			TreeNode tlosAgentNodeTree = new DefaultTreeNode(ConstantDefinitions.TREE_TLOSAGENT, tlosAgentNode, resourceNode);
			tlosAgentNodeTree.setExpanded(false);
		}
		
		MonitorAgentNode monitorAgentNode = serverResourceNode.getMonitorAgentNode();
		//gelen makine icinde nagios agent var mı diye kontrol ediliyor
		if(monitorAgentNode.getNagiosAgentInfoTypeClient().isNrpeAvailable()) {
			
//			NagiosAgentInfoTypeClient nagiosAgentInfoTypeClient = new NagiosAgentInfoTypeClient(serverResourceNode.getNagiosAgentNode().getNagiosAgentInfoTypeClient());
			
			//Nagios agenti agaca ekleyecek
			
			monitorAgentNode.setLabelText(NAGIOS_AGENT_NAME);
			
			/*
			LiveResourceNavigationContentBean nagiosAgent = new LiveResourceNavigationContentBean();
			nagiosAgent.setNavigationSelection(navigationBean);
			
			nagiosAgent.setMenuContentTitle("webmail.navigation.rootNode.title");
			nagiosAgent.setTemplateName("nagiosAgentViewPanel");
			nagiosAgent.setPageContent(true);
			nagiosAgent.setExpanded(false);
			nagiosAgent.setLeaf(true);
			
			if (!nagiosAgentInfoTypeClient.isNrpeAvailable()) {
				nagiosAgent.setLeafIcon("images/navigation_tree/tree_node.gif");
			}
			
			ResourceFolderBean resourceFolder = new ResourceFolderBean();
			resourceFolder.setResourceName(resourceNode.getResourceFolder().getResourceName());
//			resourceFolder.setResource(true);
			resourceFolder.setNagiosAgent(true);
			resourceFolder.setNagiosAgentInfoTypeClient(nagiosAgentInfoTypeClient);
			nagiosAgent.setResourceFolder(resourceFolder);
			*/
			
			TreeNode monitorAgentNodeTree = new DefaultTreeNode(ConstantDefinitions.TREE_MONITORAGENT, monitorAgentNode, resourceNode);
			monitorAgentNodeTree.setExpanded(false);
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

}
