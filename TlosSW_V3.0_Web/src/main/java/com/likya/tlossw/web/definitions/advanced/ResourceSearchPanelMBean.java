package com.likya.tlossw.web.definitions.advanced;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlOptions;
import org.ogf.schemas.rns.x2009.x12.rns.RNSEntryType;
import org.primefaces.component.datatable.DataTable;

import com.likya.tlos.model.xmlbeans.swresourcens.ResourceListType;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.TlosSWBaseBean;

@ManagedBean(name = "resourceSearchPanelMBean")
@RequestScoped
public class ResourceSearchPanelMBean extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = -8496956003387120301L;

	private RNSEntryType resource;

	private ArrayList<RNSEntryType> searchResourceList;
	private transient DataTable searchResourceTable;

	private String resourceName;

	private List<RNSEntryType> filteredResourceList;

	public void dispose() {
		resetResourceAction();
	}

	@PostConstruct
	public void init() {
		resetResourceAction();
	}

	public String getResourceXML() {
		// TODO RNSEntryType taniminda bir problem var sanirim,
		// burada kullanamadim.
		// Simdilik qname tanimini boyle yaptim.
		//QName qName = RNSEntryType.type.getName()OuterType().getDocumentElementName();
		QName qName = RNSEntryType.type.getName();
		//QName qName = new QName("http://schemas.ogf.org/rns/2009/12/rns", "RNSEntryType", "rns");
		// QName qName =
		// RNSEntryType.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String resourceXML = resource.xmlText(xmlOptions);

		return resourceXML;
	}

	public void resetResourceAction() {
		resource = RNSEntryType.Factory.newInstance();
		searchResourceList = new ArrayList<RNSEntryType>();
		searchResourceList = null;
		resourceName = "";
	}

	public void searchResourceAction(ActionEvent e) {
		if (resourceName != null && !resourceName.equals("")) {
			resource.setEntryName(resourceName);
		}

		ResourceListType resourceListType = getDbOperations().searchResource(getResourceXML());
		searchResourceList = new ArrayList<RNSEntryType>();

		for (RNSEntryType rnsEntryType : resourceListType.getResourceArray()) {
			searchResourceList.add(rnsEntryType);
		}

		if (searchResourceList == null || searchResourceList.size() == 0) {
			addMessage("searchResource", FacesMessage.SEVERITY_INFO, "tlos.info.search.noRecord", null);
		}
	}

	public void deleteResourceAction(ActionEvent e) {
		resource = (RNSEntryType) searchResourceTable.getRowData();

		if (getDbOperations().deleteResource(getResourceXML())) {
			searchResourceList.remove(resource);
			resource = RNSEntryType.Factory.newInstance();

			addMessage("searchResource", FacesMessage.SEVERITY_INFO, "tlos.success.resource.delete", null);
		} else {
			addMessage("searchResource", FacesMessage.SEVERITY_ERROR, "tlos.error.resource.delete", null);
		}
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public RNSEntryType getResource() {
		return resource;
	}

	public void setResource(RNSEntryType resource) {
		this.resource = resource;
	}

	public DataTable getSearchResourceTable() {
		return searchResourceTable;
	}

	public void setSearchResourceTable(DataTable searchResourceTable) {
		this.searchResourceTable = searchResourceTable;
	}

	public ArrayList<RNSEntryType> getSearchResourceList() {
		return searchResourceList;
	}

	public void setSearchResourceList(ArrayList<RNSEntryType> searchResourceList) {
		this.searchResourceList = searchResourceList;
	}

	public List<RNSEntryType> getFilteredResourceList() {
		return filteredResourceList;
	}

	public void setFilteredResourceList(List<RNSEntryType> filteredResourceList) {
		this.filteredResourceList = filteredResourceList;
	}

}
