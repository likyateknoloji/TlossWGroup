package com.likya.tlossw.web.definitions.helpers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import org.apache.xmlbeans.XmlCursor;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

import com.likya.tlos.model.xmlbeans.common.InParamDocument.InParam;
import com.likya.tlos.model.xmlbeans.common.LocalParametersDocument.LocalParameters;
import com.likya.tlos.model.xmlbeans.common.OutParamDocument.OutParam;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.ScenarioDocument.Scenario;
import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument.Parameter;
import com.likya.tlos.model.xmlbeans.parameters.PreValueDocument.PreValue;
import com.likya.tlossw.exceptions.TransformCodeCreateException;
import com.likya.tlossw.exceptions.UnresolvedDependencyException;
import com.likya.tlossw.utils.CommonConstantDefinitions;
import com.likya.tlossw.web.appmng.SessionMediator;

public class LocalParametersTabBean extends BaseTabBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9051763580102022983L;
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

	private String paramPreValueTime;

	private boolean ioType; // input; false -- output; true
	private boolean mapped = false;
	private String connectedId;

	private boolean fromUser;
	private boolean renderUpdateParamButton = false;

	private boolean showAddParameterGrid = false;

	private ArrayList<Parameter> parameterList = null;
	private Parameter selectedRow;

	private String selectedJob;
	private List<SelectItem> dependentJobParamList;

	private transient DataTable parameterTable;

	private HashMap<String, Parameter> depParamList = null;

	private HashMap<String, Parameter> selectedJobMapping = null;

	private String selectedItem; // +getter +setter
	private List<SelectItem> availableItems; // +getter (no setter necessary)

	private boolean paramDetails;

	public LocalParametersTabBean() {
		super();
		selectedJob = "0";
		connectedId = "0";
		paramDetails = false;
		paramPreValueTF = false;
	}

	public void resetTab(boolean resetList) {
		paramName = "";
		paramDesc = "";
		paramPreValue = "";
		paramValue = "";
		ioType = false; // input
		paramActive = false;
		mapped = false;
		paramDetails = false;
		paramPreValueTF = false;
		connectedId = "0";
		jsId = "";
		ioName = "";
		fromUser=false;
		
		if (resetList) {
			setParameterList(new ArrayList<Parameter>());
		}
	}

	public void fillDepJobListParameterList() {

		SelectItemGroup grup = null;
		SelectItem item = null;

		if (depParamList != null) {

			dependentJobParamList = new ArrayList<SelectItem>();
			Set<String> depJobList = new HashSet<String>();

			for (Parameter parameter : depParamList.values()) {
				if (parameter.getActive())
					depJobList.add(parameter.getJsId());
			}
			Object[] depJobListArray = depJobList.toArray();

			// SelectItemGroup grup[] = null;

			for (Object job : depJobListArray) {

				String ne = (String) job;

				grup = new SelectItemGroup(ne);

				SelectItem params[] = new SelectItem[depParamList.size()];
				int j = 0;
				for (Parameter parameter : depParamList.values()) {

					item = new SelectItem();
					item.setValue(parameter.getId().toString());
					item.setLabel(parameter.getName());

					if (parameter.getJsId() == grup.getLabel()) {
						params[j] = item;
						j++;
					}
				}
				grup.setSelectItems(params);

				dependentJobParamList.add(grup);
			}
		}
		System.out.println("ne");
	}

	public void addParameter() {

		if (paramName == null || paramName.equals("") || paramDesc == null || paramDesc.equals("")) {
			addMessage("addInputParam", FacesMessage.SEVERITY_ERROR, "tlos.workspace.pannel.job.paramValidationError", null);
			return;
		}

		ioName = "UserDefined";
		Parameter parameter = Parameter.Factory.newInstance();
		parameter.setName(paramName);
		parameter.setDesc(paramDesc);
		parameter.setIoType(ioType);
		parameter.setActive(paramActive);
		parameter.setFromUser(true);
		
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
		
//		if (paramPreValue.contains("$(")) {
//		} else {
//			parameter.setValueString(paramValue);
//		}

		parameter.setIoName(ioName);
		parameter.setMapped(mapped);

		if (connectedId != null)
			parameter.setConnectedId(new BigInteger(connectedId));
		if (jsId != null)
			parameter.setJsId(jsId);

		int max = 10000000;
		int min = 100001;

		Random rand = new Random();

		long id = rand.nextInt((max - min) + 1) + min;

		parameter.setId(new BigInteger(id + ""));
		
		parameterList.add(parameter);

		resetTab(false);
	}

	public void paramActivation(AjaxBehaviorEvent event) {

		Parameter ioParam = (Parameter) parameterTable.getRowData();

		paramName = new String(ioParam.getName());
		paramDesc = new String(ioParam.getDesc());
		paramPreValue = new String(ioParam.getPreValue().getStringValue());
		paramType = ioParam.getPreValue().getType();

		if (paramType == 2)
			paramValue = ioParam.getValueString();

		ioType = ioParam.getIoType();
		paramActive = ioParam.getActive();
		ioName = ioParam.getIoName();
		mapped = ioParam.getMapped();
		connectedId = (ioParam.getConnectedId() == null) ? null : ioParam.getConnectedId().toString();
        fromUser = ioParam.getFromUser();
		jsId = ioParam.getJsId();

		paramId = ioParam.getId();
		selectedParamId = ioParam.getId();

		renderUpdateParamButton = true;
	}

	public void editParamAction(ActionEvent e) {

		Parameter ioParam = (Parameter) parameterTable.getRowData();
		
		selectedParamId = ioParam.getId();
		
		selectedJob = ioParam.getConnectedId().toString();
		
		fillDepJobListParameterList();
		showAddParameterGrid = true;
		paramName = new String(ioParam.getName());
		paramDesc = new String(ioParam.getDesc());
		paramPreValue = new String(ioParam.getPreValue().getStringValue());

		if (ioParam.getPreValue().getType() == 2 && ioParam.getIoType())
			if (ioParam.getValueString() != null)
				paramValue = new String(ioParam.getValueString());

		paramType = ioParam.getPreValue().getType();

		if (paramType == 2)
			paramValue = ioParam.getValueString();

		ioType = ioParam.getIoType();
		paramActive = ioParam.getActive();
		ioName = ioParam.getIoName();
		mapped = ioParam.getMapped();
		fromUser = ioParam.getFromUser();
		
		connectedId = (ioParam.getConnectedId() == null) ? null : ioParam.getConnectedId().toString();
		jsId = ioParam.getJsId();

		paramId = ioParam.getId();


		renderUpdateParamButton = true;
	}

	public void deleteParamAction(ActionEvent e) {

		int parameterIndex = parameterTable.getRowIndex();
		Parameter ioParam = (Parameter) parameterTable.getRowData();

		parameterList.remove(parameterIndex);

		// parameterList.remove(selectedRow);

		renderUpdateParamButton = false;
	}

	public void updateParameter() {
		for (int i = 0; i < parameterList.size(); i++) {

			if (selectedParamId.equals(parameterList.get(i).getId())) {
				Parameter parameter = parameterList.get(i);
				
				parameter.setName(paramName);
				parameter.setDesc(paramDesc);

				PreValue preValue = PreValue.Factory.newInstance();
				preValue.setStringValue(paramPreValue);
				preValue.setType((short) paramType);
				
				parameter.setPreValue(preValue);
				parameter.setIoType(ioType);
				parameter.setActive(paramActive);
				parameter.setIoName(ioName);
				parameter.setFromUser(fromUser);
				
				if (connectedId != null)
					parameter.setConnectedId(new BigInteger(connectedId));
				
				parameter.setMapped(mapped);
				parameter.setJsId(jsId);

				if (paramType == 4) {
					preValue.setStringValue(paramPreValueTime);
				} else if (paramType == 5) {
					preValue.setStringValue(paramPreValue + "T" + paramPreValueTime);
				} else {
					preValue.setStringValue(paramPreValue);
				}

				parameter.setValueString(preValue.getStringValue());

				break;
			}
		}

		resetTab(false);

		renderUpdateParamButton = false;
	}

	public void fillLocalParameters(boolean isScenario, Object refObject) {

		int outSize = 0;
		int inSize = 0;

		for (int i = 0; i < parameterList.size(); i++) {
			if (parameterList.get(i).getActive()) {
				if (parameterList.get(i).getIoType()) {
					outSize++;
				} else {
					inSize++;
				}
			}
		}

		if (parameterList.size() > 0) {

			LocalParameters localParameters = LocalParameters.Factory.newInstance();

			if (inSize > 0) {
				InParam inParam = null;
				inParam = InParam.Factory.newInstance();
				localParameters.setInParam(inParam);
			}

			if (outSize > 0) {
				OutParam outParam = null;
				outParam = OutParam.Factory.newInstance();
				localParameters.setOutParam(outParam);
			}

			Parameter inParameter = null;
			Parameter outParameter = null;

			for (int i = 0; i < parameterList.size(); i++) {

				if (parameterList.get(i).getActive()) {
					BigInteger rasgeleIdSiniri = new BigInteger("100000");
					BigInteger defaultIdSiniri = new BigInteger("100");

					Parameter parameter = parameterList.get(i);
					if (parameter.getId().compareTo(rasgeleIdSiniri) > 0 || parameter.getId().compareTo(defaultIdSiniri) < 0) {
						parameter.setId(getParameterId());
					}

					if (parameter.getIoType()) {
						outParameter = localParameters.getOutParam().addNewParameter();
						outParameter.set(parameter);
					} else {
						inParameter = localParameters.getInParam().addNewParameter();
						inParameter.set(parameter);
					}
				}
			}
			if (isScenario) {
				((Scenario) refObject).setLocalParameters(localParameters);
			} else {
				((JobProperties) refObject).setLocalParameters(localParameters);
			}
		} else {
			if (isScenario) {
				Scenario scenario = ((Scenario) refObject);
				if (scenario.getLocalParameters() != null) {
					XmlCursor xmlCursor = scenario.getLocalParameters().newCursor();
					xmlCursor.removeXml();
				}
			} else {
				JobProperties jobProperties = ((JobProperties) refObject);
				if (jobProperties.getLocalParameters() != null) {
					XmlCursor xmlCursor = jobProperties.getLocalParameters().newCursor();
					xmlCursor.removeXml();
				}
			}
		}

	}

	public Parameter setParameter(String name, String desc, short type, String value, boolean ioType, String ioName) {

		Parameter parameter = Parameter.Factory.newInstance();
		parameter.setName(name);
		parameter.setDesc("desc");
		PreValue preValue = PreValue.Factory.newInstance();
		preValue.setType((short) type);
		preValue.setStringValue("");
		parameter.setPreValue(preValue);
		parameter.setValueString(value);

		parameter.setFromUser(false);
		parameter.setIoType(ioType);
		parameter.setActive(false);
		parameter.setActive(paramActive);
		parameter.setIoName(ioName);
		parameter.setMapped(mapped);
		parameter.setJsId(jsId);
		
		if (paramType == 2)
			parameter.setValueString(paramValue);
		parameter.setConnectedId(new BigInteger(connectedId));

		int max = 10000000;
		int min = 100001;

		Random rand = new Random();

		long id = rand.nextInt((max - min) + 1) + min;

		parameter.setId(new BigInteger(id + ""));

		return parameter;
	}

	public void fillLocalParametersTab(boolean isScenario, Object refObject) {

		parameterList = new ArrayList<Parameter>();
		LocalParameters localParameters = null;
		HashMap<String, Parameter> optionalList = new HashMap<String, Parameter>();

		depParamList = new HashMap<String, Parameter>();

		if (isScenario) {
			Scenario scenario = ((Scenario) refObject);
			if (scenario != null && scenario.getLocalParameters() != null) {
				localParameters = scenario.getLocalParameters();
			}
		} else {
			JobProperties jobProperties = ((JobProperties) refObject);
			// DependencyResolver dependencyResolver = new DependencyResolver();
			try {
				SessionMediator sessionMediator = (SessionMediator) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessionMediator");
				depParamList = DependencyResolver.handleDependency(jobProperties, jobProperties.getDependencyList(), sessionMediator);
			} catch (UnresolvedDependencyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformCodeCreateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (jobProperties != null) {

				Parameter parameter = null;

				// parameter = setParameter("JobResult", "desc", "2", "", false);
				// if (parameter != null)
				// optionalList.put("JobResult", parameter);

				parameter = setParameter("LogOutput", "Log output", (short) 2, "", true, "LogOutput");
				if (parameter != null)
					optionalList.put(parameter.getIoName(), parameter);

				parameter = setParameter("ErrorOutput", "Error output", (short) 2, "", true, "ErrorOutput");
				if (parameter != null)
					optionalList.put(parameter.getIoName(), parameter);
			}
			if (jobProperties.getLocalParameters() == null) {
				jobProperties.setLocalParameters(LocalParameters.Factory.newInstance());
			}
			localParameters = jobProperties.getLocalParameters();
		}

		if (localParameters != null) {

			if (localParameters.getInParam() != null) {
				InParam inParam = localParameters.getInParam();

				for (Parameter parameter : inParam.getParameterArray()) {

					// optionalList.put(parameter.getId(), parameter);

					if (parameter != null) {
						parameter.setIoType(false);
						if (parameter.getActive())
							parameterList.add(parameter);
					}
				}
			}

			if (localParameters.getOutParam() != null) {
				OutParam outParam = localParameters.getOutParam();

				for (Parameter parameter : outParam.getParameterArray()) {

					// optionalList.put(parameter.getId(), parameter);

					if (parameter != null) {
						parameter.setIoType(true);
						boolean isUserDefined = parameter.getIoName().contains("UserDefined");
						if (!parameter.getIoName().isEmpty() || isUserDefined) {
							String name;
							if(isUserDefined)
								name = parameter.getIoName() + parameter.getId() + "";
							else
								name = parameter.getIoName();
							optionalList.put(name, parameter);
						}
						else
							optionalList.put(parameter.getId() + "", parameter);
					}
				}
			}
			// Bagimliliktan dolayi gelebilecek parametreler

			// if (depParamList != null) {
			//
			// for (Parameter parameter : depParamList.values()) {
			// parameter.setIoType(false);
			// // optionalList.put(parameter.getId(), parameter);
			//
			// if (parameter != null) {
			// parameter.setIoType(false);
			// if (!parameter.getIoName().isEmpty())
			// optionalList.put(parameter.getIoName(), parameter);
			// else
			// optionalList.put(parameter.getId() + "", parameter);
			// }
			// }
			// }

			for (Object parameter : optionalList.values()) {
				parameterList.add((Parameter) parameter);
			}

		}

		fillDepJobListParameterList();

		// if (localParameters != null) {
		//
		// if (localParameters.getInParam() != null) {
		// InParam inParam = localParameters.getInParam();
		//
		// for (Parameter parameter : inParam.getParameterArray()) {
		// parameter.setIoType(false);
		// parameterList.add(parameter);
		// }
		// }
		//
		// if (localParameters.getOutParam() != null) {
		// OutParam outParam = localParameters.getOutParam();
		//
		// for (Parameter parameter : outParam.getParameterArray()) {
		// parameter.setIoType(true);
		// parameterList.add(parameter);
		// }
		// }
		//
		// if (localParameters.getInParam() == null && localParameters.getOutParam() == null) {
		// parameterList = new ArrayList<Parameter>();
		// }
		// }
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

	public void handleParamListChange(String row) {
		if (dependentJobParamList != null && !dependentJobParamList.equals("")) {
			Parameter selectedRowParam = (Parameter) parameterTable.getRowData();
			for (Object parameter : parameterList.toArray()) {
				Parameter element = (Parameter) parameter;
				if (element.getId().toString().equalsIgnoreCase(selectedRowParam.getId().toString())) {
					element.setConnectedId(new BigInteger(selectedJob));
					element.setMapped(true);
					if (selectedJobMapping == null) {
						selectedJobMapping = new HashMap<String, Parameter>();
					}
					selectedJobMapping.put(element.getId().toString(), element);
                    break;
					// parameter = new String(ioParam.getName());
					// paramDesc = new String(ioParam.getDesc());
					// paramPreValue = new String(ioParam.getPreValue().getStringValue());
					// paramType = new String(ioParam.getPreValue().getType().toString());
					// ioType = ioParam.getIoType();
					// paramActive = ioParam.getActive();
					// ioName = ioParam.getIoName();
					//
					// paramId = ioParam.getId();
					// selectedParamId = ioParam.getId();
				}
				// parameterList.add((Parameter) parameter);
			}
			fillDepJobListParameterList();
		} else {
			dependentJobParamList = new ArrayList<SelectItem>();
		    selectedJob = "0";
		}
	}

	public boolean isRenderUpdateParamButton() {
		return renderUpdateParamButton;
	}

	public void setRenderUpdateParamButton(boolean renderUpdateParamButton) {
		this.renderUpdateParamButton = renderUpdateParamButton;
	}

	public DataTable getParameterTable() {
		return parameterTable;
	}

	public void setParameterTable(DataTable parameterTable) {
		this.parameterTable = parameterTable;
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

	public Parameter getSelectedRow() {
		return selectedRow;
	}

	public void onRowSelect(SelectEvent event) {
		FacesMessage msg = new FacesMessage("Parameter Selected", ((Parameter) event.getObject()).getId().toString());

		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public void onRowUnselect(UnselectEvent event) {
		FacesMessage msg = new FacesMessage("Parameter Unselected", ((Parameter) event.getObject()).getId().toString());

		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public void setSelectedRow(Parameter selectedRow) {
		this.selectedRow = selectedRow;
	}

	public boolean isioType() {
		return ioType;
	}

	public void setioType(boolean ioType) {
		this.ioType = ioType;
	}

	public ArrayList<Parameter> getParameterList() {
		return parameterList;
	}

	public void setParameterList(ArrayList<Parameter> parameterList) {
		this.parameterList = parameterList;
	}

	public boolean isParamActive() {
		return paramActive;
	}

	public void setParamActive(boolean paramActive) {
		this.paramActive = paramActive;
	}

	public BigInteger getParamId() {
		return paramId;
	}

	public void setParamId(BigInteger paramId) {
		this.paramId = paramId;
	}

	public boolean isIoType() {
		return ioType;
	}

	public void setIoType(boolean ioType) {
		this.ioType = ioType;
	}

	public String getIoName() {
		return ioName;
	}

	public void setIoName(String ioName) {
		this.ioName = ioName;
	}

	public HashMap<String, Parameter> getDepParamList() {
		return depParamList;
	}

	public void setDepParamList(HashMap<String, Parameter> depParamList) {
		this.depParamList = depParamList;
	}

	public String getSelectedJob(Parameter item) {
		if (selectedJobMapping.containsKey(item.getId().toString())) {
			Parameter parameter = selectedJobMapping.get(item.getId().toString());
			selectedJob = parameter.getConnectedId().toString();
		} else
			selectedJob = "0";
		return selectedJob;
	}

	public String getSelectedJob(String id) {
		return selectedJob;
	}

	public void setSelectedJob(String selectedJob) {
		this.selectedJob = selectedJob;
	}

	public List<SelectItem> getDependentJobParamList() {
		return dependentJobParamList;
	}

	public String getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(String selectedItem) {
		this.selectedItem = selectedItem;
	}

	public List<SelectItem> getAvailableItems() {
		return availableItems;
	}

	public void setAvailableItems(List<SelectItem> availableItems) {
		this.availableItems = availableItems;
	}

	public boolean isMapped() {
		return mapped;
	}

	public void setMapped(boolean mapped) {
		this.mapped = mapped;
	}

	public String getConnectedId() {
		return connectedId;
	}

	public void setConnectedId(String connectedId) {
		this.connectedId = connectedId;
	}

	public HashMap<String, Parameter> getSelectedJobMapping() {
		return selectedJobMapping;
	}

	public void setSelectedJobMapping(HashMap<String, Parameter> selectedJobMapping) {
		this.selectedJobMapping = selectedJobMapping;
	}

	public String getSelectedJob() {
		return selectedJob;
	}

	public boolean isParamDetails() {
		if (paramId != null)
			paramDetails = paramId.equals(new BigInteger("0")) ? false : true;
		return paramDetails;
	}

	public void setParamDetails(boolean paramDetails) {
		this.paramDetails = paramDetails;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public boolean isParamPreValueTF() {
		paramPreValueTF = paramPreValue.isEmpty() ? false : true;
		return paramPreValueTF;
	}

	public void setParamPreValueTF(boolean paramPreValueTF) {
		this.paramPreValueTF = paramPreValueTF;
	}

	public String getJsId() {
		return jsId;
	}

	public void setJsId(String jsId) {
		this.jsId = jsId;
	}

	public String getParamPreValueTime() {
		return paramPreValueTime;
	}

	public void setParamPreValueTime(String paramPreValueTime) {
		this.paramPreValueTime = paramPreValueTime;
	}

	public boolean isShowAddParameterGrid() {
		return showAddParameterGrid;
	}

	public void setShowAddParameterGrid() {
		if (showAddParameterGrid)
			showAddParameterGrid = false;
		else
			showAddParameterGrid = true;
	}

	public boolean isFromUser() {
		return fromUser;
	}

	public void setFromUser(boolean fromUser) {
		this.fromUser = fromUser;
	}

	public boolean isParamValueNeedsTextArea(String param) {
		return param.length() > 20 ? true : false;
	}

}
