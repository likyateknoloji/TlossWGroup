package com.likya.tlossw.web.definitions.advanced;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlOptions;
import org.primefaces.component.datatable.DataTable;

import com.likya.tlos.model.xmlbeans.agent.AgentTypeDocument.AgentType;
import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.agent.UserStopRequestDocument.UserStopRequest;
import com.likya.tlos.model.xmlbeans.jsdl.OperatingSystemTypeEnumeration;
import com.likya.tlos.model.xmlbeans.resourceextdefs.ResourceDocument.Resource;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.db.DBOperations;

@ManagedBean(name = "agentSearchPanelMBean")
@ViewScoped
public class AgentSearchPanelMBean extends TlosSWBaseBean implements Serializable {

	@ManagedProperty(value = "#{dbOperations}")
	private DBOperations dbOperations;

	private static final long serialVersionUID = -7436267818850177642L;

	private String osType;
	private String resource;
 
    private String nrpePort;
    private String jmxPort;
    private String jmxUser;
    private String jmxPassword;
    private String jmxPassword2;
    private String durationForUnavailability;
    private String jobTransferFailureTime;
    private String workspacePath;
    
    private Collection<SelectItem> agentTypeList = null;
	private String agentType;   
	
	private String userStopRequest;
	
	private SWAgent agent;
	private ArrayList<SWAgent> searchAgentList;
	private transient DataTable searchAgentTable;
	
	private List<SWAgent> filteredAgents; 
	
	public void dispose() {
		resetAgentAction();
		agent = null;
		searchAgentTable = null;
	}

	@PostConstruct
	public void init() {
		resetAgentAction();
	 
	}
 
	public String getAgentXML() {
		 
		QName qName = SWAgent.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String agentXML = agent.xmlText(xmlOptions);
		return agentXML;
	}
	
	public void searchAgentAction(ActionEvent e) {
		searchAgentList = dbOperations.searchAgent(getAgentXML());
		if (searchAgentList == null || searchAgentList.size() == 0) {
			addMessage("searchAgent", FacesMessage.SEVERITY_INFO, "tlos.info.search.noRecord", null);
		}
	}
	
	public void editAgentAction(ActionEvent e) {
		agent = (SWAgent) searchAgentTable.getRowData();
		resource = agent.getResource().getStringValue().toString();
		 
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("agentPanel.xhtml");
		} catch (IOException e1) {
			e1.printStackTrace();
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
		resource = null;
		osType = "All";
	     
	    nrpePort=null;
	    jmxPort=null;
	    jmxUser=null;
	    jmxPassword=null;
	    jmxPassword2=null;
	    durationForUnavailability=null;
	    jobTransferFailureTime=null;
		
		searchAgentList = null;
	 
		agent = SWAgent.Factory.newInstance();
		
		Resource res = Resource.Factory.newInstance();
		agent.setResource(res);
		
		//agent.setAgentType(AgentType.Enum.forString(getAgentType()));
		
		agent.setJmxPort(new Short("0"));
		agent.setNrpePort(new Short("0"));
		agent.setJmxUser("");
		agent.setJmxPassword("");
		agent.setDurationForUnavailability(new Integer(0));
		agent.setJobTransferFailureTime(new Long("0"));
		
		agent.setUserStopRequest(UserStopRequest.NULL);
		
		fillAgentTypeList();
	}
	
	public void fillAgentTypeList() {
		if (getAgentTypeList() != null) {
			setAgentType("Seciniz");
			return;
		}
		String agentType = null;
		Collection<SelectItem> agentTypeList = new ArrayList<SelectItem>();
		SelectItem item = new SelectItem();
		item.setLabel("Seciniz");
		item.setValue("Seciniz");
		agentTypeList.add(item);

		for (int i = 0; i < AgentType.Enum.table.lastInt(); i++) {
			item = new SelectItem();
			agentType = AgentType.Enum.table.forInt(i + 1).toString();
			item.setValue(agentType);
			item.setLabel(agentType);
			agentTypeList.add(item);
		}
		setAgentTypeList(agentTypeList);
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
		
		if(resource != null && !resource.equals("")) {
			Resource res = Resource.Factory.newInstance();
			res.setStringValue(resource);
			
			agent.setResource(res);
		}
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
		
		if(!osType.equals("All")) {
			agent.setOsType(OperatingSystemTypeEnumeration.Enum.forString(osType));
		} else {
			agent.setOsType(null); // Default
		}
	}

	public String getOsType() {
		return osType;
	}

	public void setDurationForUnavailability(String durationForUnavailability) {
		this.durationForUnavailability = durationForUnavailability;
		
		if(durationForUnavailability != null && !durationForUnavailability.equals("")) {
			agent.setDurationForUnavailability(Integer.parseInt(durationForUnavailability));
		}
	}

	public String getDurationForUnavailability() {
		return durationForUnavailability;
	}

	public void setJobTransferFailureTime(String jobTransferFailureTime) {
		this.jobTransferFailureTime = jobTransferFailureTime;
		
		if(jobTransferFailureTime != null && !jobTransferFailureTime.equals("")) {
			agent.setJobTransferFailureTime(Long.parseLong(jobTransferFailureTime));
		}
	}

	public String getJobTransferFailureTime() {
		return jobTransferFailureTime;
	}

	public void setAgentType(String agentType) {
		this.agentType = agentType;
		
		if (!agentType.equals("Seciniz")) {
			agent.setAgentType(AgentType.Enum.forString(agentType));
		} else if (agentType.equals("Seciniz")) {
			agent.setAgentType(AgentType.AGENT); // default
		}
	}

	public String getAgentType() {
		return agentType;
	}

	public void setNrpePort(String nrpePort) {
		this.nrpePort = nrpePort;
		if(nrpePort != null && !nrpePort.equals("")) {
			agent.setNrpePort(Short.parseShort(nrpePort));
		}
	}

	public String getNrpePort() {
		return nrpePort;
	}

	public void setJmxPort(String jmxPort) {
		this.jmxPort = jmxPort;
		if(jmxPort != null && !jmxPort.equals("")) {
			agent.setJmxPort(Short.parseShort(jmxPort));
		}
	}

	public String getJmxPort() {
		return jmxPort;
	}

	public void setJmxUser(String jmxUser) {
		this.jmxUser = jmxUser;
	}

	public String getJmxUser() {
		return jmxUser;
	}

	public void setJmxPassword(String jmxPassword) {
		this.jmxPassword = jmxPassword;
	}

	public String getJmxPassword() {
		return jmxPassword;
	}

	public void setJmxPassword2(String jmxPassword2) {
		this.jmxPassword2 = jmxPassword2;
 
	}

	public String getJmxPassword2() {
		return jmxPassword2;
	}

	public void setAgentTypeList(Collection<SelectItem> agentTypeList) {
		this.agentTypeList = agentTypeList;
	}

	public Collection<SelectItem> getAgentTypeList() {
		return agentTypeList;
	}

	public void setUserStopRequest(String userStopRequest) {
		this.userStopRequest = userStopRequest;
	}

	public String getUserStopRequest() {
		return userStopRequest;
	}

	public void setWorkspacePath(String workspacePath) {
		this.workspacePath = workspacePath;
		
		if(workspacePath != null && !workspacePath.equals("")) {
			agent.setWorkspacePath(workspacePath);
		}
	}

	public String getWorkspacePath() {
		return workspacePath;
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

}
