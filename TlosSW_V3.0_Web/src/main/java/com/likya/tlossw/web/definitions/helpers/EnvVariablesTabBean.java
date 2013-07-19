package com.likya.tlossw.web.definitions.helpers;

import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;

import org.primefaces.component.datatable.DataTable;

import com.likya.tlos.model.xmlbeans.common.EntryDocument.Entry;
import com.likya.tlos.model.xmlbeans.common.EnvVariablesDocument.EnvVariables;
import com.likya.tlos.model.xmlbeans.common.SpecialParametersDocument.SpecialParameters;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlossw.web.definitions.JSBasePanelMBean;

public class EnvVariablesTabBean {

	// envVariables
	private String envVariableName;
	private String envVariableValue;

	private String selectedEnvVariableName;

	private ArrayList<Entry> envVariableList = new ArrayList<Entry>();
	private transient DataTable envVariableTable;

	private boolean renderUpdateEnvVariableButton = false;

	private JSBasePanelMBean baseJSPanelMBean;

	public EnvVariablesTabBean(JSBasePanelMBean baseJSPanelMBean) {
		super();
		this.baseJSPanelMBean = baseJSPanelMBean;
	}

	public void resetTab() {
		envVariableName = "";
		envVariableValue = "";
		selectedEnvVariableName = "";

		envVariableList = new ArrayList<Entry>();
	}

	public void addEnvVariable() {

		if (envVariableName == null || envVariableName.equals("") || envVariableValue == null || envVariableValue.equals("")) {

			baseJSPanelMBean.addMessage("addEnvVariable", FacesMessage.SEVERITY_ERROR, "tlos.workspace.pannel.job.envVariableValidationError", null);

			return;
		}

		Entry entry = Entry.Factory.newInstance();
		entry.setKey(envVariableName);
		entry.setStringValue(envVariableValue);

		envVariableList.add(entry);

		resetEnvVariableFields();
	}

	public void updateEnvVariable() {

		for (int i = 0; i < envVariableList.size(); i++) {

			if (selectedEnvVariableName.equals(envVariableList.get(i).getKey())) {
				envVariableList.get(i).setKey(envVariableName);
				envVariableList.get(i).setStringValue(envVariableValue);

				break;
			}
		}

		resetEnvVariableFields();

		setRenderUpdateEnvVariableButton(false);
	}

	public void editEnvVariableAction(ActionEvent e) {

		Entry entry = (Entry) envVariableTable.getRowData();
		envVariableName = new String(entry.getKey());
		envVariableValue = new String(entry.getStringValue());
		selectedEnvVariableName = envVariableName;

		setRenderUpdateEnvVariableButton(true);
	}

	public void deleteEnvVariableAction(ActionEvent e) {

		int envVarIndex = envVariableTable.getRowIndex();
		envVariableList.remove(envVarIndex);

		setRenderUpdateEnvVariableButton(false);
	}

	private void resetEnvVariableFields() {

		envVariableName = "";
		envVariableValue = "";
	}

	public void fillEnvVariables(Object refObject) {

		SpecialParameters specialParameters;
		if (((JobProperties) refObject).getBaseJobInfos().getJobInfos().getJobTypeDetails().getSpecialParameters() == null) {
			specialParameters = SpecialParameters.Factory.newInstance();
		} else {
			specialParameters = ((JobProperties) refObject).getBaseJobInfos().getJobInfos().getJobTypeDetails().getSpecialParameters();
		}

		EnvVariables envVariables = EnvVariables.Factory.newInstance();

		for (int i = 0; i < envVariableList.size(); i++) {
			Entry entry = envVariables.addNewEntry();
			entry.set(envVariableList.get(i));
		}

		specialParameters.setEnvVariables(envVariables);
		((JobProperties) refObject).getBaseJobInfos().getJobInfos().getJobTypeDetails().setSpecialParameters(specialParameters);
	}

	public void fillEnvVariablesTab(Object refObject) {

		EnvVariables envVariables = null;

		if (((JobProperties) refObject) != null) {
			SpecialParameters specialParameters = ((JobProperties) refObject).getBaseJobInfos().getJobInfos().getJobTypeDetails().getSpecialParameters();

			if (specialParameters != null && specialParameters.getEnvVariables() != null) {
				envVariables = specialParameters.getEnvVariables();
			} else {
				envVariableList = new ArrayList<Entry>();
			}
		}

		if (envVariables != null) {
			for (Entry entry : envVariables.getEntryArray()) {
				envVariableList.add(entry);
			}
		}
	}

	public String getEnvVariableName() {
		return envVariableName;
	}

	public void setEnvVariableName(String envVariableName) {
		this.envVariableName = envVariableName;
	}

	public String getEnvVariableValue() {
		return envVariableValue;
	}

	public void setEnvVariableValue(String envVariableValue) {
		this.envVariableValue = envVariableValue;
	}

	public String getSelectedEnvVariableName() {
		return selectedEnvVariableName;
	}

	public void setSelectedEnvVariableName(String selectedEnvVariableName) {
		this.selectedEnvVariableName = selectedEnvVariableName;
	}

	public ArrayList<Entry> getEnvVariableList() {
		return envVariableList;
	}

	public void setEnvVariableList(ArrayList<Entry> envVariableList) {
		this.envVariableList = envVariableList;
	}

	public DataTable getEnvVariableTable() {
		return envVariableTable;
	}

	public void setEnvVariableTable(DataTable envVariableTable) {
		this.envVariableTable = envVariableTable;
	}

	public boolean isRenderUpdateEnvVariableButton() {
		return renderUpdateEnvVariableButton;
	}

	public void setRenderUpdateEnvVariableButton(boolean renderUpdateEnvVariableButton) {
		this.renderUpdateEnvVariableButton = renderUpdateEnvVariableButton;
	}

}
