package com.likya.tlossw.web.live;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.component.datatable.DataTable;

import com.likya.tlossw.model.client.resource.ResourceInfoTypeClient;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.webclient.TEJmxMpClient;

@ManagedBean(name = "resourceMBean")
@ViewScoped
public class ResourceMBean extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = 6405656464925270466L;

	private ResourceInfoTypeClient resourceInfoTypeClient;

	private ArrayList<ResourceInfoTypeClient> resourceInfoList;

	private transient DataTable resourceDataTable;
	private List<ResourceInfoTypeClient> filteredResources;

	private ResourceInfoTypeClient selectedRow;
	private ResourceInfoTypeClient[] selectedRows;

	private boolean transformToLocalTime;

	public void fillResourceInfoList() {
		resourceInfoList = TEJmxMpClient.getResourceInfoTypeClientList(getWebAppUser());
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

	public ResourceInfoTypeClient getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(ResourceInfoTypeClient selectedRow) {
		this.selectedRow = selectedRow;
	}

	public ResourceInfoTypeClient[] getSelectedRows() {
		return selectedRows;
	}

	public void setSelectedRows(ResourceInfoTypeClient[] selectedRows) {
		this.selectedRows = selectedRows;
	}

	public boolean isTransformToLocalTime() {
		return transformToLocalTime;
	}

	public void setTransformToLocalTime(boolean transformToLocalTime) {
		this.transformToLocalTime = transformToLocalTime;
	}

}
