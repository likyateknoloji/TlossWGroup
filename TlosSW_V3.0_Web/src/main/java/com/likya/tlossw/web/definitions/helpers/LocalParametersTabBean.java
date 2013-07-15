package com.likya.tlossw.web.definitions.helpers;

import java.math.BigInteger;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;

import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument.Parameter;
import com.likya.tlos.model.xmlbeans.parameters.PreValueDocument.PreValue;
import com.likya.tlossw.web.definitions.BaseJSPanelMBean;

public class LocalParametersTabBean {

	// localParameters
	private String paramName;
	private String paramDesc;
	private String paramType;
	private String paramPreValue;
	private String selectedParamName;

	private boolean renderUpdateParamButton = false;

	private ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
	
	private transient DataTable parameterTable;
	
	private BaseJSPanelMBean baseJSPanelMBean;
	
	public LocalParametersTabBean(BaseJSPanelMBean baseJSPanelMBean) {
		super();
		this.baseJSPanelMBean = baseJSPanelMBean;
	}

	private void resetTab() {
		paramName = "";
		paramDesc = "";
		paramPreValue = "";
		paramType = "";
	}

	public void addInputParameter() {
		
		if (paramName == null || paramName.equals("") || paramDesc == null || paramDesc.equals("") || paramPreValue == null || paramPreValue.equals("") || paramType == null || paramType.equals("")) {
			baseJSPanelMBean.addMessage("addInputParam", FacesMessage.SEVERITY_ERROR, "tlos.workspace.pannel.job.paramValidationError", null);
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

		resetTab();
	}

	public void editInputParamAction(ActionEvent e) {
		
		Parameter inParam = (Parameter) parameterTable.getRowData();

		paramName = new String(inParam.getName());
		paramDesc = new String(inParam.getDesc());
		paramPreValue = new String(inParam.getPreValue().getStringValue());
		paramType = new String(inParam.getPreValue().getType().toString());

		selectedParamName = paramName;

		renderUpdateParamButton = true;

		RequestContext context = RequestContext.getCurrentInstance();
		context.update("jobDefinitionForm:tabView:parametersPanel");
	}

	public void deleteInputParamAction(ActionEvent e) {
		
		int parameterIndex = parameterTable.getRowIndex();
		parameterList.remove(parameterIndex);

		renderUpdateParamButton = false;

		RequestContext context = RequestContext.getCurrentInstance();
		context.update("jobDefinitionForm:tabView:parametersPanel");
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

		resetTab();

		renderUpdateParamButton = false;
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

}
