package com.likya.tlossw.web.live;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.component.datatable.DataTable;

import com.likya.tlos.model.xmlbeans.agent.AgentTypeDocument.AgentType;
import com.likya.tlos.model.xmlbeans.agent.OsTypeDocument.OsType;
import com.likya.tlossw.model.client.resource.ResourceInfoTypeClient;
import com.likya.tlossw.model.client.resource.TlosAgentInfoTypeClient;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.utils.ComboListUtils;
import com.likya.tlossw.web.utils.ConstantDefinitions;
import com.likya.tlossw.webclient.TEJmxMpClient;

@ManagedBean(name = "resourceMBean")
@ViewScoped
public class ResourceMBean extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = 6405656464925270466L;

	private ResourceInfoTypeClient resourceInfoTypeClient;

	private ArrayList<ResourceInfoTypeClient> resourceInfoList;

	private transient DataTable resourceDataTable;
	private List<ResourceInfoTypeClient> filteredResources;

	private ArrayList<TlosAgentInfoTypeClient> agentInfoList;

	private transient DataTable agentDataTable;
	private List<TlosAgentInfoTypeClient> filteredAgents;

	private ArrayList<String> oSList = new ArrayList<String>();

	private SelectItem[] oSSelectItem;

	private boolean transformToLocalTime;

	public void fillResourceInfoList() {
		resourceInfoList = TEJmxMpClient.getResourceInfoTypeClientList(getWebAppUser());

		oSList.add(OsType.WINDOWS.toString());
		oSList.add(OsType.UNIX.toString());
		oSSelectItem = ComboListUtils.createFilterOptions(oSList);
	}

	public void fillAgentInfoList(String resourceName) {
		agentInfoList = TEJmxMpClient.getTlosAgentInfoTypeClientList(getWebAppUser(), resourceName);

		for (TlosAgentInfoTypeClient agent : agentInfoList) {
			if (agent.getAgentType().toLowerCase().equals(AgentType.SERVER.toString().toLowerCase())) {
				agent.setAgentName(ConstantDefinitions.SERVER_NAME);
			} else {
				agent.setAgentName(ConstantDefinitions.AGENT_NAME + agent.getAgentId());
			}
		}
	}

	public ResourceInfoTypeClient getResourceInfoTypeClient() {
		return resourceInfoTypeClient;
	}

	public void setResourceInfoTypeClient(ResourceInfoTypeClient resourceInfoTypeClient) {
		this.resourceInfoTypeClient = resourceInfoTypeClient;
	}

	public ArrayList<ResourceInfoTypeClient> getResourceInfoList() {
		return resourceInfoList;
	}

	public void setResourceInfoList(ArrayList<ResourceInfoTypeClient> resourceInfoList) {
		this.resourceInfoList = resourceInfoList;
	}

	public DataTable getResourceDataTable() {
		return resourceDataTable;
	}

	public void setResourceDataTable(DataTable resourceDataTable) {
		this.resourceDataTable = resourceDataTable;
	}

	public List<ResourceInfoTypeClient> getFilteredResources() {
		return filteredResources;
	}

	public void setFilteredResources(List<ResourceInfoTypeClient> filteredResources) {
		this.filteredResources = filteredResources;
	}

	public boolean isTransformToLocalTime() {
		return transformToLocalTime;
	}

	public void setTransformToLocalTime(boolean transformToLocalTime) {
		this.transformToLocalTime = transformToLocalTime;
	}

	public List<TlosAgentInfoTypeClient> getFilteredAgents() {
		return filteredAgents;
	}

	public void setFilteredAgents(List<TlosAgentInfoTypeClient> filteredAgents) {
		this.filteredAgents = filteredAgents;
	}

	public DataTable getAgentDataTable() {
		return agentDataTable;
	}

	public void setAgentDataTable(DataTable agentDataTable) {
		this.agentDataTable = agentDataTable;
	}

	public ArrayList<TlosAgentInfoTypeClient> getAgentInfoList() {
		return agentInfoList;
	}

	public void setAgentInfoList(ArrayList<TlosAgentInfoTypeClient> agentInfoList) {
		this.agentInfoList = agentInfoList;
	}

	public ArrayList<String> getoSList() {
		return oSList;
	}

	public void setoSList(ArrayList<String> oSList) {
		this.oSList = oSList;
	}

	public SelectItem[] getosSelectItem() {
		return oSSelectItem;
	}

	public void setosSelectItem(SelectItem[] oSSelectItem) {
		this.oSSelectItem = oSSelectItem;
	}

}
