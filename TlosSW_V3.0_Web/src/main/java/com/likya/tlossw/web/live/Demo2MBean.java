package com.likya.tlossw.web.live;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.NodeSelectEvent;

import com.likya.tlossw.model.tree.resource.ResourceNode;
import com.likya.tlossw.model.tree.resource.TlosAgentNode;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.utils.ConstantDefinitions;

@ManagedBean(name = "demo2MBean")
@ViewScoped
public class Demo2MBean extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = -504537811128309503L;

	@ManagedProperty(value = "#{resourceMBean}")
	private ResourceMBean resourceMBean;

	@ManagedProperty(value = "#{tlosAgentMBean}")
	private TlosAgentMBean tlosAgentMBean;

	private String activeLivePanel = RESOURCE_PANEL;

	public final static String RESOURCELIST_PANEL = "21.xhtml";
	public final static String RESOURCE_PANEL = "22.xhtml";
	public final static String TLOSAGENT_PANEL = "23.xhtml";
	public final static String JOB_PANEL = "/inc/livePanels/jobLiveTree.xhtml";

	private boolean transformToLocalTime = false;

	@PostConstruct
	public void init() {
		getResourceMBean().setTransformToLocalTime(transformToLocalTime);
		getTlosAgentMBean().setTransformToLocalTime(transformToLocalTime);
	}

	public void onNodeSelect(NodeSelectEvent event) {

		String nodeType = event.getTreeNode().getType();
		if (nodeType.equals(ConstantDefinitions.TREE_KAYNAKLISTESI)) {
			getResourceMBean().fillResourceInfoList();

			activeLivePanel = RESOURCELIST_PANEL;
		} else if (nodeType.equals(ConstantDefinitions.TREE_KAYNAK)) {
			ResourceNode resourceNode = (ResourceNode) event.getTreeNode().getData();
			String resourceName = resourceNode.getResourceInfoTypeClient().getResourceName();
			getResourceMBean().fillAgentInfoList(resourceName);

			activeLivePanel = RESOURCE_PANEL;
		} else if (nodeType.equals(ConstantDefinitions.TREE_TLOSAGENT)) {
			TlosAgentNode tlosAgentNode = (TlosAgentNode) event.getTreeNode().getData();
			int agentId = tlosAgentNode.getTlosAgentInfoTypeClient().getAgentId();
			getTlosAgentMBean().initializeTlosAgentPanel(agentId);

			activeLivePanel = TLOSAGENT_PANEL;
		}
		
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(activeLivePanel + "?faces-redirect=true");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String getActiveLivePanel() {
		return activeLivePanel;
	}

	public void setActiveLivePanel(String activeLivePanel) {
		this.activeLivePanel = activeLivePanel;
	}

	public ResourceMBean getResourceMBean() {
		return resourceMBean;
	}

	public void setResourceMBean(ResourceMBean resourceMBean) {
		this.resourceMBean = resourceMBean;
	}

	public TlosAgentMBean getTlosAgentMBean() {
		return tlosAgentMBean;
	}

	public void setTlosAgentMBean(TlosAgentMBean tlosAgentMBean) {
		this.tlosAgentMBean = tlosAgentMBean;
	}

}
