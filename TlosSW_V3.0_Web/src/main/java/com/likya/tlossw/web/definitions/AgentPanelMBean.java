package com.likya.tlossw.web.definitions;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlOptions;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;

import com.likya.tlos.model.xmlbeans.agent.AgentTypeDocument.AgentType;
import com.likya.tlos.model.xmlbeans.agent.LocalsDocument.Locals;
import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.agent.UserStopRequestDocument.UserStopRequest;
import com.likya.tlos.model.xmlbeans.jsdl.OperatingSystemTypeEnumeration;
import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument.Parameter;
import com.likya.tlos.model.xmlbeans.parameters.PreValueDocument.PreValue;
import com.likya.tlos.model.xmlbeans.resourceextdefs.ResourceDocument.Resource;
import com.likya.tlossw.utils.CommonConstantDefinitions;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.appmng.SessionMediator;
import com.likya.tlossw.web.db.DBOperations;
import com.likya.tlossw.web.utils.WebInputUtils;

@ManagedBean(name = "agentPanelMBean")
@ViewScoped
public class AgentPanelMBean extends TlosSWBaseBean implements Serializable {

	@ManagedProperty(value = "#{dbOperations}")
	private DBOperations dbOperations;

	private String selectedAgentID;
	private String insertCheck;
	private String iCheck;

	private static final long serialVersionUID = 1L;

	private boolean showInputParameterPanelGrid;
	private String osType;

	private String resource;
	private Collection<SelectItem> resourceList = null;

	private String nrpePort;
	private String jmxTlsPort;
	private String jmxPassword2;
	private int durationForUnavailability;
	private long jobTransferFailureTime;
	private String workspacePath;

	private Collection<SelectItem> agentTypeList = null;
	private String agentType;

	private SWAgent agent;

	private boolean insertButton;

	// localParameters
	private String paramName;
	private String paramDesc;
	private short paramType;
	private String paramPreValue;
	private boolean paramPreValueTF;
	private String paramValue;
	private boolean paramActive;
	private BigInteger selectedParamId;
	private BigInteger paramId;
	private String ioName;
	private String jsId;

	private boolean ioType; // input; false -- output; true
	private boolean mapped = false;
	private String connectedId;
	private String paramPreValueTime;

	private String selectedParamName;

	private ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
	private transient DataTable parameterTable;

	private boolean renderUpdateParamButton = false;

	private boolean paramDetails;
	
	@PostConstruct
	public void init() {
		resetAgentAction();
		fillAgentTypeList();

		paramDetails = false;
		ioType = false;
		paramValue = "";
		showInputParameterPanelGrid = true;
		setResourceList(WebInputUtils.fillResourceNameList(getDbOperations().getResources()));

		selectedAgentID = String.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("selectedAgentID"));
		insertCheck = String.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("insertCheck"));
		iCheck = String.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("iCheck"));

		if (iCheck != null && iCheck.equals("insert"))
			insertButton = true;

		if (insertCheck != null) {
			if (insertCheck.equals("update")) {
				insertButton = false;
				agent = SWAgent.Factory.newInstance();

				agent = dbOperations.searchAgentById(selectedAgentID);

				if (agent != null) {
					resource = agent.getResource().getStringValue().toString();

					osType = agent.getOsType().toString();
					agentType = agent.getAgentType().toString();

					nrpePort = agent.getNrpePort() + "";
					jmxTlsPort = agent.getJmxTlsPort() + "";
					jmxPassword2 = agent.getJmxPassword();
					setDurationForUnavailability(agent.getDurationForUnavailability());
					setJobTransferFailureTime(agent.getJobTransferFailureTime());
					workspacePath = agent.getWorkspacePath();

					if (agent.getLocals() != null && agent.getLocals().sizeOfParameterArray() > 0) {
						for (Parameter parameter : agent.getLocals().getParameterArray()) {
							parameterList.add(parameter);
						}
					}
				}
			} else {
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
		fillAgentProperties();

		if (!(checkDuplicate() && validate())) {
			return;
		}

		if (dbOperations.insertAgent(getAgentXML())) {
			addMessage("yeniAgent", FacesMessage.SEVERITY_INFO, "tlos.success.agent.insert", null);
			resetAgentAction();
		} else {
			addMessage("yeniAgent", FacesMessage.SEVERITY_ERROR, "tlos.error.agent.insert", null);
		}
	}

	private boolean validate() {
		if (nrpePort.equals(jmxTlsPort)) {
			addMessage("yeniAgent", FacesMessage.SEVERITY_WARN, "tlos.error.agent.insert.portDuplicate", null);
			return false;
		}

		return true;
	}

	private boolean checkDuplicate() {
		SWAgent duplicateAgent = getDbOperations().checkAgent(agent.getResource().getStringValue(), agent.getJmxTlsPort());
		if (duplicateAgent != null) {
			addMessage("yeniAgent", FacesMessage.SEVERITY_WARN, "tlos.error.agent.insert.duplicate", null);
			return false;
		}

		return true;
	}

	public void updateAgentAction(ActionEvent e) {
		fillAgentProperties();

		if (dbOperations.updateAgent(getAgentXML())) {
			addMessage("yeniAgent", FacesMessage.SEVERITY_INFO, "tlos.success.agent.update", null);
		} else {
			addMessage("yeniAgent", FacesMessage.SEVERITY_ERROR, "tlos.error.agent.update", null);
		}
	}

	private void fillAgentProperties() {
		Resource resourceDef = Resource.Factory.newInstance();
		resourceDef.setStringValue(resource);
		agent.setResource(resourceDef);
		agent.setNrpePort(Integer.parseInt(nrpePort));
		agent.setJmxTlsPort(Integer.parseInt(jmxTlsPort));
		agent.setOsType(OperatingSystemTypeEnumeration.Enum.forString(osType));
		agent.setAgentType(AgentType.Enum.forString(agentType));
		agent.setDurationForUnavailability(durationForUnavailability);
		agent.setJobTransferFailureTime(jobTransferFailureTime);
		agent.setWorkspacePath(workspacePath);

		setLocalParameters();

		// bu alanlar ekrandan doldurulmuyor
		// bos olarak kaydedilmesin diye bu sekilde doldurdum
		agent.setInJmxAvailable(false);
		agent.setOutJmxAvailable(false);
		agent.setJmxAvailable(false);
		agent.setUserStopRequest(UserStopRequest.NULL);
		agent.setNrpeAvailable(false);
		agent.setLastHeartBeatTime(0);
		agent.setLastJobTransfer(false);
	}

	public void resetAgentAction() {
		resource = null;
		osType = "";

		jmxPassword2 = null;
		paramDetails = false;
		durationForUnavailability = 900;
		jobTransferFailureTime = 0;

		parameterList = new ArrayList<Parameter>();

		agent = SWAgent.Factory.newInstance();
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

	private BigInteger getParameterId() {
		SessionMediator sessionMediator = (SessionMediator) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessionMediator");
		int parameterId = sessionMediator.getDbOperations().getNextId(CommonConstantDefinitions.PARAMETER_ID);
		if (parameterId < 0) {
			addMessage("insertParameter", FacesMessage.SEVERITY_WARN, "tlos.info.parameter.db.getId", null);
			return new BigInteger("0");
		}
		paramId = new BigInteger(parameterId + "");

		return paramId;
	}
	
	private void setLocalParameters() {
		if (parameterList != null && parameterList.size() > 0) {
			Locals locals = Locals.Factory.newInstance();

			for (Parameter parameter : parameterList) {
				Parameter newParam = locals.addNewParameter();
				
				BigInteger rasgeleIdSiniri = new BigInteger("100000");
				BigInteger defaultIdSiniri = new BigInteger("100");

				if (parameter.getId().compareTo(rasgeleIdSiniri) > 0 || parameter.getId().compareTo(defaultIdSiniri) < 0) {
					parameter.setId(getParameterId());
				}
				
				newParam.set(parameter);
			}
			agent.setLocals(locals);

		} else if (agent.getLocals() != null) {
			XmlCursor xmlCursor = agent.getLocals().newCursor();
			xmlCursor.removeXml();
		}
	}

	public void addInputParameter() {
		if (paramName == null || paramName.equals("") || paramDesc == null  ) {
			addMessage("addInputParam", FacesMessage.SEVERITY_ERROR, "tlos.workspace.pannel.job.paramValidationError", null);

			return;
		}
		paramActive = true;
		ioName="UserDefined";
		Parameter parameter = Parameter.Factory.newInstance();
		parameter.setName(paramName);
		parameter.setDesc(paramDesc);
		parameter.setIoType(ioType);
		parameter.setActive(paramActive);
		
		PreValue preValue = PreValue.Factory.newInstance();
		preValue.setType((short) paramType);
		
		if (paramType == 4) {
			preValue.setStringValue(paramPreValueTime);
		} else if (paramType == 5) {
			preValue.setStringValue(paramPreValue + "T" + paramPreValueTime);
		} else {
			preValue.setStringValue(paramPreValue);
		}

		parameter.setPreValue(preValue);
		parameter.setValueString(preValue.getStringValue());
		
		parameter.setIoName(ioName);
		parameter.setMapped(mapped);
		if(connectedId != null)
		   parameter.setConnectedId(new BigInteger(connectedId));
		if(jsId != null)
			parameter.setJsId(jsId);
		
		int max = 10000000;
		int min = 100001;

		Random rand = new Random();

		long id = rand.nextInt((max - min) + 1) + min;

		parameter.setId(new BigInteger(id + ""));
		
		parameterList.add(parameter);

		resetInputParameterFields();
	}
	
	private void resetInputParameterFields() {
		paramName = "";
		paramDesc = "";
		paramPreValue = "";
		paramPreValueTime = "";
	}

	public void deleteInputParamAction(ActionEvent e) {
		int parameterIndex = parameterTable.getRowIndex();
		parameterList.remove(parameterIndex);

		renderUpdateParamButton = false;

		RequestContext context = RequestContext.getCurrentInstance();
		context.update("agent:parametersPanel");
	}

	public void editInputParamAction(ActionEvent e) {
		Parameter inParam = (Parameter) parameterTable.getRowData();

		paramName = new String(inParam.getName());
		paramDesc = new String(inParam.getDesc());

		short type = inParam.getPreValue().getType();
		paramType = type;

		String paramValue = inParam.getPreValue().getStringValue();
		if (type == 4) {
			paramPreValueTime = paramValue;
		} else if (type == 5) {
			StringTokenizer tokenizer = new StringTokenizer(paramValue, "T");

			if (tokenizer.hasMoreTokens()) {
				paramPreValue = tokenizer.nextToken();
			}
			if (tokenizer.hasMoreTokens()) {
				paramPreValueTime = tokenizer.nextToken();
			}
		} else {
			paramPreValue = paramValue;
		}

		this.paramValue=paramPreValue;
		selectedParamName = paramName;
		paramActive = inParam.getActive();
		selectedParamId = inParam.getId();
		renderUpdateParamButton = true;

		RequestContext context = RequestContext.getCurrentInstance();
		context.update("agentForm:parametersPanel");
	}

	public void updateInputParameter() {
		for (int i = 0; i < parameterList.size(); i++) {

			if (selectedParamId.equals(parameterList.get(i).getId())) {
				parameterList.get(i).setName(paramName);
				parameterList.get(i).setDesc(paramDesc);

				PreValue preValue = PreValue.Factory.newInstance();
				preValue.setStringValue(paramPreValue);
				preValue.setType((short) paramType);
				parameterList.get(i).setPreValue(preValue);
				parameterList.get(i).setIoType(ioType);
				parameterList.get(i).setActive(paramActive);
				parameterList.get(i).setIoName(ioName);
				
				parameterList.get(i).setMapped(mapped);
				
				parameterList.get(i).setActive(paramActive);
				
				if(connectedId != null)
					parameterList.get(i).setConnectedId(new BigInteger(connectedId));
				if(jsId != null)
						parameterList.get(i).setJsId(jsId);
					
				if (paramType == 4) {
					preValue.setStringValue(paramPreValueTime);
				} else if (paramType == 5) {
					preValue.setStringValue(paramPreValue + "T" + paramPreValueTime);
				} else {
					preValue.setStringValue(paramPreValue);
				}
				
				parameterList.get(i).setValueString(preValue.getStringValue());
				
				break;
			}
		}
		resetInputParameterFields();

		renderUpdateParamButton = false;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
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

	public void setOsType(String osType) {
		this.osType = osType;
	}

	public String getOsType() {
		return osType;
	}

	public String getAgentType() {
		return agentType;
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

	public void setWorkspacePath(String workspacePath) {
		this.workspacePath = workspacePath;
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

	public short getParamType() {
		return paramType;
	}

	public void setParamType(short paramType) {
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

	public String getSelectedAgentID() {
		return selectedAgentID;
	}

	public void setSelectedAgentID(String selectedAgentID) {
		this.selectedAgentID = selectedAgentID;
	}

	public int getDurationForUnavailability() {
		return durationForUnavailability;
	}

	public void setDurationForUnavailability(int durationForUnavailability) {
		this.durationForUnavailability = durationForUnavailability;
	}

	public long getJobTransferFailureTime() {
		return jobTransferFailureTime;
	}

	public void setJobTransferFailureTime(long jobTransferFailureTime) {
		this.jobTransferFailureTime = jobTransferFailureTime;
	}

	public void setAgentType(String agentType) {
		this.agentType = agentType;
	}

	public Collection<SelectItem> getResourceList() {
		return resourceList;
	}

	public void setResourceList(Collection<SelectItem> resourceList) {
		this.resourceList = resourceList;
	}

	public String getNrpePort() {
		return nrpePort;
	}

	public void setNrpePort(String nrpePort) {
		this.nrpePort = nrpePort;
	}

	public String getJmxTlsPort() {
		return jmxTlsPort;
	}

	public void setJmxTlsPort(String jmxTlsPort) {
		this.jmxTlsPort = jmxTlsPort;
	}

	public String getParamPreValueTime() {
		return paramPreValueTime;
	}

	public void setParamPreValueTime(String paramPreValueTime) {
		this.paramPreValueTime = paramPreValueTime;
	}

	public boolean isParamDetails() {
		if (paramId != null)
			paramDetails = paramId.equals(new BigInteger("0")) ? false : true;
		return paramDetails;
	}

	public void setParamDetails(boolean paramDetails) {
		this.paramDetails = paramDetails;
	}
	
	public BigInteger getParamId() {
		return paramId;
	}

	public void setParamId(BigInteger paramId) {
		this.paramId = paramId;
	}

	public boolean isShowInputParameterPanelGrid() {
		return showInputParameterPanelGrid;
	}

	public void setShowInputParameterPanelGrid(boolean showInputParameterPanelGrid) {
		this.showInputParameterPanelGrid = showInputParameterPanelGrid;
	}

	public boolean isIoType() {
		return ioType;
	}

	public void setIoType(boolean ioType) {
		this.ioType = ioType;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public boolean isParamActive() {
		return paramActive;
	}

	public void setParamActive(boolean paramActive) {
		this.paramActive = paramActive;
	}

	public String getConnectedId() {
		return connectedId;
	}

	public void setConnectedId(String connectedId) {
		this.connectedId = connectedId;
	}

	public String getJsId() {
		return jsId;
	}

	public void setJsId(String jsId) {
		this.jsId = jsId;
	}

	public BigInteger getSelectedParamId() {
		return selectedParamId;
	}

	public void setSelectedParamId(BigInteger selectedParamId) {
		this.selectedParamId = selectedParamId;
	}
}
