package com.likya.tlossw.web.definitions.helpers;

import java.math.BigInteger;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;

import org.apache.xmlbeans.XmlCursor;
import org.primefaces.component.datatable.DataTable;

import com.likya.tlos.model.xmlbeans.common.InParamDocument.InParam;
import com.likya.tlos.model.xmlbeans.common.LocalParametersDocument.LocalParameters;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.ScenarioDocument.Scenario;
import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument.Parameter;
import com.likya.tlos.model.xmlbeans.parameters.PreValueDocument.PreValue;

public class LocalParametersTabBean extends BaseTabBean {

	private static final long serialVersionUID = -539345583609476380L;

	// localParameters
	private String paramName;
	private String paramDesc;
	private String paramType;
	private String paramPreValue;
	private String selectedParamName;

	private boolean renderUpdateParamButton = false;

	private ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
	private Parameter selectedRow;

	private transient DataTable parameterTable;

	public LocalParametersTabBean() {
		super();
	}

	public void resetTab(boolean resetList) {
		paramName = "";
		paramDesc = "";
		paramPreValue = "";
		paramType = "";

		if (resetList) {
			parameterList = new ArrayList<Parameter>();
		}
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

		resetTab(false);
	}

	public void editInputParamAction(ActionEvent e) {

		Parameter inParam = (Parameter) parameterTable.getRowData();

		paramName = new String(inParam.getName());
		paramDesc = new String(inParam.getDesc());
		paramPreValue = new String(inParam.getPreValue().getStringValue());
		paramType = new String(inParam.getPreValue().getType().toString());

		selectedParamName = paramName;

		renderUpdateParamButton = true;
	}

	public void deleteInputParamAction(ActionEvent e) {

		int parameterIndex = parameterTable.getRowIndex();
		parameterList.remove(parameterIndex);
		// parameterList.remove(selectedRow);

		renderUpdateParamButton = false;
	}

	public void updateInputParameter() {
		for (int i = 0; i < parameterList.size(); i++) {

			if (selectedParamName.equals(parameterList.get(i).getName())) {
				parameterList.get(i).setName(paramName);
				parameterList.get(i).setDesc(paramDesc);

				PreValue preValue = PreValue.Factory.newInstance();
				preValue.setStringValue(paramPreValue);
				preValue.setType(new BigInteger(paramType));
				parameterList.get(i).setPreValue(preValue);

				break;
			}
		}

		resetTab(false);

		renderUpdateParamButton = false;
	}

	public void fillLocalParameters(boolean isScenario, Object refObject) {

		if (parameterList.size() > 0) {
			LocalParameters localParameters = LocalParameters.Factory.newInstance();

			InParam inParam = InParam.Factory.newInstance();
			localParameters.setInParam(inParam);

			for (int i = 0; i < parameterList.size(); i++) {
				Parameter parameter = localParameters.getInParam().addNewParameter();
				parameter.set(parameterList.get(i));
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

	public void fillLocalParametersTab(boolean isScenario, Object refObject) {

		LocalParameters localParameters = null;

		if (isScenario) {
			Scenario scenario = ((Scenario) refObject);
			if (scenario != null && scenario.getLocalParameters() != null) {
				localParameters = scenario.getLocalParameters();
			}
		} else {
			JobProperties jobProperties = ((JobProperties) refObject);
			if (jobProperties != null && jobProperties.getLocalParameters() != null) {
				localParameters = jobProperties.getLocalParameters();
			}
		}

		if (localParameters != null) {
			if (localParameters != null && localParameters.getInParam() != null) {
				InParam inParam = localParameters.getInParam();

				for (Parameter parameter : inParam.getParameterArray()) {
					parameterList.add(parameter);
				}
			} else {
				parameterList = new ArrayList<Parameter>();
			}
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

	public ArrayList<Parameter> getParameterList() {
		return parameterList;
	}

	public void setParameterList(ArrayList<Parameter> parameterList) {
		this.parameterList = parameterList;
	}

	public Parameter getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(Parameter selectedRow) {
		this.selectedRow = selectedRow;
	}

}
