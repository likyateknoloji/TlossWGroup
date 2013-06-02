package com.likya.tlossw.web.definitions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlOptions;
import org.primefaces.component.datatable.DataTable;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.agent.UserStopRequestDocument.UserStopRequest;
import com.likya.tlos.model.xmlbeans.jsdl.OperatingSystemTypeEnumeration;
import com.likya.tlos.model.xmlbeans.resourceextdefs.ResourceDocument.Resource;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.db.DBOperations;
import com.likya.tlossw.web.utils.WebInputUtils;

@ManagedBean(name = "agentSearchPanelMBean")
@ViewScoped
public class AgentSearchPanelMBean extends TlosSWBaseBean implements Serializable {

	@ManagedProperty(value = "#{dbOperations}")
	private DBOperations dbOperations;

	private static final long serialVersionUID = -7436267818850177642L;

	private SWAgent agent;

	private ArrayList<SWAgent> searchAgentList;
	private transient DataTable searchAgentTable;

	private List<SWAgent> filteredAgents;

	private String osType;

	private String resource;
	private Collection<SelectItem> resourceList = null;

	public void dispose() {
		resetAgentAction();
		agent = null;
		searchAgentTable = null;
	}

	@PostConstruct
	public void init() {
		resetAgentAction();
		setResourceList(WebInputUtils.fillResourceNameList(getDbOperations().getResources()));
	}

	public String getAgentXML() {
		QName qName = SWAgent.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String agentXML = agent.xmlText(xmlOptions);
		return agentXML;
	}

	public void searchAgentAction(ActionEvent e) {
		Resource resourceDef = Resource.Factory.newInstance();
		if (resource != null && !resource.equals("")) {
			resourceDef.setStringValue(resource);
		} else {
			resourceDef.setStringValue(null);
		}
		agent.setResource(resourceDef);

		if (osType != null && !osType.equals("")) {
			agent.setOsType(OperatingSystemTypeEnumeration.Enum.forString(osType));
		} else {
			agent.setOsType(null);
		}

		searchAgentList = dbOperations.searchAgent(getAgentXML());
		if (searchAgentList == null || searchAgentList.size() == 0) {
			addMessage("searchAgent", FacesMessage.SEVERITY_INFO, "tlos.info.search.noRecord", null);
		}
	}

	public void deleteAgentAction(ActionEvent e) {
		agent = (SWAgent) searchAgentTable.getRowData();
		resource = agent.getResource().toString();

		if (dbOperations.deleteAgent(getAgentXML())) {
			searchAgentList.remove(agent);
			agent = SWAgent.Factory.newInstance();
			addMessage("deleteAgent", FacesMessage.SEVERITY_INFO, "tlos.success.agent.delete", null);
		} else {
			addMessage("deleteAgent", FacesMessage.SEVERITY_ERROR, "tlos.error.agent.delete", null);
		}

	}

	public void resetAgentAction() {
		agent = SWAgent.Factory.newInstance();
		agent.setJmxPort(new Short("0"));
		agent.setNrpePort(new Short("0"));
		agent.setJmxUser("");
		agent.setJmxPassword("");
		agent.setDurationForUnavailability(new Integer(0));
		agent.setJobTransferFailureTime(new Long("0"));
		agent.setUserStopRequest(UserStopRequest.NULL);

		resource = null;
		osType = "";
		searchAgentList = new ArrayList<SWAgent>();
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public void setAgent(SWAgent agent) {
		this.agent = agent;
	}

	public SWAgent getAgent() {
		return agent;
	}

	public void setSearchAgentList(ArrayList<SWAgent> searchAgentList) {
		this.searchAgentList = searchAgentList;
	}

	public ArrayList<SWAgent> getSearchAgentList() {
		return searchAgentList;
	}

	public void setOsType(String osType) {
		this.osType = osType;
	}

	public String getOsType() {
		return osType;
	}

	public DBOperations getDbOperations() {
		return dbOperations;
	}

	public void setDbOperations(DBOperations dbOperations) {
		this.dbOperations = dbOperations;
	}

	public DataTable getSearchAgentTable() {
		return searchAgentTable;
	}

	public void setSearchAgentTable(DataTable searchAgentTable) {
		this.searchAgentTable = searchAgentTable;
	}

	public List<SWAgent> getFilteredAgents() {
		return filteredAgents;
	}

	public void setFilteredAgents(List<SWAgent> filteredAgents) {
		this.filteredAgents = filteredAgents;
	}

	public Collection<SelectItem> getResourceList() {
		return resourceList;
	}

	public void setResourceList(Collection<SelectItem> resourceList) {
		this.resourceList = resourceList;
	}

}
