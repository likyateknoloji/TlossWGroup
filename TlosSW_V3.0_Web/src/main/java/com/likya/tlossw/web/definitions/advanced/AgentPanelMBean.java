package com.likya.tlossw.web.definitions.advanced;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

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
import org.xmldb.api.base.XMLDBException;

import com.likya.tlos.model.xmlbeans.agent.AgentTypeDocument.AgentType;
import com.likya.tlos.model.xmlbeans.agent.OsTypeDocument.OsType;
import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.agent.UserStopRequestDocument.UserStopRequest;
import com.likya.tlos.model.xmlbeans.jsdl.OperatingSystemTypeEnumeration;
import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument.Parameter;
import com.likya.tlos.model.xmlbeans.parameters.PreValueDocument.PreValue;
import com.likya.tlos.model.xmlbeans.resourceextdefs.ResourceDocument.Resource;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.appmng.SessionMediator;
import com.likya.tlossw.web.db.DBOperations;

@ManagedBean(name = "agentPanelMBean")
@ViewScoped
public class AgentPanelMBean extends TlosSWBaseBean implements Serializable {

	@ManagedProperty(value = "#{dbOperations}")
	private DBOperations dbOperations;

//	@ManagedProperty(value = "#{param.selectedAgentName}")
	private String selectedAgentName;
//
//	@ManagedProperty(value = "#{param.insertCheck}")
	private String insertCheck;
//
//	@ManagedProperty(value = "#{param.iCheck}")
	private String iCheck;
	
	private static final long serialVersionUID = 1L;

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
	
	private boolean insertButton;

	private String paramName;
	private String paramDesc;
	private String paramType;
	private String paramPreValue;

	private String selectedParamName;

	private ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
	private transient DataTable parameterTable;

	private boolean renderUpdateParamButton = false;
	
	@PostConstruct
	public void init() {

		agent = SWAgent.Factory.newInstance();
		fillAgentTypeList();
		
		if (iCheck != null && iCheck.equals("insert")) insertButton = true;
		
		if (insertCheck != null) {
			
			if (insertCheck.equals("update")) {
				insertButton = false;
				agent = SWAgent.Factory.newInstance();
				
				try {
					agent = dbOperations.searchAgentByResource(selectedAgentName);
				} catch (XMLDBException e) {
					e.printStackTrace();
				}
	
				if (agent != null) {
					resource=agent.getResource().getStringValue().toString();
					
					osType = agent.getOsType().toString();
					agentType = agent.getAgentType().toString();
					jmxPassword2 = agent.getJmxPassword();
					durationForUnavailability=Integer.toString(agent.getDurationForUnavailability());
					jobTransferFailureTime =  Long.toString(agent.getJobTransferFailureTime());
					workspacePath = agent.getWorkspacePath();
				}
			}
			else {
				insertButton = true;
				
			}
		}
	}
 
	public String getAgentXML() {
		 
		QName qName = SWAgent.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String agentXML = agent.xmlText(xmlOptions);
		return agentXML;
	}
	
	public void insertAgentAction(ActionEvent e) {
		
		if(osType.equals("All")) {
			agent.setOsType(OsType.OTHER); // Default
		}
		
		if (dbOperations.insertAgent(getAgentXML())) {
			addMessage("yeniAgent", FacesMessage.SEVERITY_INFO, "tlos.success.agent.insert", null);
			resetAgentAction();
		} else {
			addMessage("yeniAgent",  FacesMessage.SEVERITY_ERROR, "tlos.error.agent.insert", null);
		}

	}

	public void updateAgentAction(ActionEvent e) {

		if (dbOperations.updateAgent(getAgentXML())) {
			addMessage("yeniAgent", FacesMessage.SEVERITY_INFO, "tlos.success.agent.update", null);
		} else {
			addMessage("yeniAgent", FacesMessage.SEVERITY_ERROR, "tlos.error.agent.update", null);
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
		String agentType = null;
		Collection<SelectItem> agentTypeList = new ArrayList<SelectItem>();

		for (int i = 0; i < AgentType.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			agentType = AgentType.Enum.table.forInt(i + 1).toString();
			item.setValue(agentType);
			item.setLabel(agentType);
			agentTypeList.add(item);
		}
		setAgentTypeList(agentTypeList);
	}
	
	public void addInputParameter() {
		if (paramName == null || paramName.equals("") || paramDesc == null || paramDesc.equals("") || paramPreValue == null || paramPreValue.equals("") || paramType == null || paramType.equals("")) {

			addMessage("addInputParam", FacesMessage.SEVERITY_ERROR, "tlos.workspace.pannel.job.paramValidationError", null);

			return;
		}

		Parameter parameter = Parameter.Factory.newInstance();
		parameter.setName(paramName);
		parameter.setDesc(paramDesc);

		PreValue preValue = PreValue.Factory.newInstance();
		preValue.setStringValue(paramPreValue);
		preValue.setType(new BigInteger(paramType));
		parameter.setPreValue(preValue);

		parameterList.add(parameter);

		resetInputParameterFields();
	}
	
	private void resetInputParameterFields() {
		paramName = "";
		paramDesc = "";
		paramPreValue = "";
		paramType = "";
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

	public boolean isInsertButton() {
		return insertButton;
	}

	public void setInsertButton(boolean insertButton) {
		this.insertButton = insertButton;
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

	public String getInsertCheck() {
		return insertCheck;
	}

	public void setInsertCheck(String insertCheck) {
		this.insertCheck = insertCheck;
	}

	public String getiCheck() {
		return iCheck;
	}

	public void setiCheck(String iCheck) {
		this.iCheck = iCheck;
	}

	public String getSelectedAgentName() {
		return selectedAgentName;
	}

	public void setSelectedAgentName(String selectedAgentName) {
		this.selectedAgentName = selectedAgentName;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamDesc() {
		return paramDesc;
	}

	public void setParamDesc(String paramDesc) {
		this.paramDesc = paramDesc;
	}

	public String getParamType() {
		return paramType;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	public String getParamPreValue() {
		return paramPreValue;
	}

	public void setParamPreValue(String paramPreValue) {
		this.paramPreValue = paramPreValue;
	}

	public String getSelectedParamName() {
		return selectedParamName;
	}

	public void setSelectedParamName(String selectedParamName) {
		this.selectedParamName = selectedParamName;
	}

	public ArrayList<Parameter> getParameterList() {
		return parameterList;
	}

	public void setParameterList(ArrayList<Parameter> parameterList) {
		this.parameterList = parameterList;
	}

	public DataTable getParameterTable() {
		return parameterTable;
	}

	public void setParameterTable(DataTable parameterTable) {
		this.parameterTable = parameterTable;
	}

	public boolean isRenderUpdateParamButton() {
		return renderUpdateParamButton;
	}

	public void setRenderUpdateParamButton(boolean renderUpdateParamButton) {
		this.renderUpdateParamButton = renderUpdateParamButton;
	}
	 
}
