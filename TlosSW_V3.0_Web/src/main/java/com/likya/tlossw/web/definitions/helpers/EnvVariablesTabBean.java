package com.likya.tlossw.web.definitions.helpers;

import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;

import org.apache.xmlbeans.XmlCursor;
import org.primefaces.component.datatable.DataTable;

import com.likya.tlos.model.xmlbeans.common.EntryDocument.Entry;
import com.likya.tlos.model.xmlbeans.common.EnvVariablesDocument.EnvVariables;
import com.likya.tlos.model.xmlbeans.common.SpecialParametersDocument.SpecialParameters;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;

public class EnvVariablesTabBean extends BaseTabBean {

	private static final long serialVersionUID = -7016007758343152710L;
	
	// envVariables
	private String envVariableName;
	private String envVariableValue;

	private String selectedEnvVariableName;

	private ArrayList<Entry> envVariableList = new ArrayList<Entry>();
	private transient DataTable envVariableTable;

	private boolean renderUpdateEnvVariableButton = false;

	public EnvVariablesTabBean() {
		super();
	}

	public void resetTab(boolean resetList) {
		envVariableName = "";
		envVariableValue = "";
		selectedEnvVariableName = "";

		if (resetList) {
			envVariableList = new ArrayList<Entry>();
		}
	}

	public void addEnvVariable() {

		if (envVariableName == null || envVariableName.equals("") || envVariableValue == null || envVariableValue.equals("")) {

			addMessage("addEnvVariable", FacesMessage.SEVERITY_ERROR, "tlos.validation.job.envVariable", null);

			return;
		}

		Entry entry = Entry.Factory.newInstance();
		entry.setKey(envVariableName);
		entry.setStringValue(envVariableValue);

		envVariableList.add(entry);

		resetTab(false);
	}

	public void updateEnvVariable() {

		for (int i = 0; i < envVariableList.size(); i++) {

			if (selectedEnvVariableName.equals(envVariableList.get(i).getKey())) {
				envVariableList.get(i).setKey(envVariableName);
				envVariableList.get(i).setStringValue(envVariableValue);

				break;
			}
		}

		resetTab(false);

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

	public void fillEnvVariables(Object refObject) {

		SpecialParameters specialParameters = ((JobProperties) refObject).getBaseJobInfos().getJobInfos().getJobTypeDetails().getSpecialParameters();
		if (specialParameters == null) {
			specialParameters = SpecialParameters.Factory.newInstance();
		}

		if (envVariableList.size() > 0) {
			EnvVariables envVariables = EnvVariables.Factory.newInstance();

			for (int i = 0; i < envVariableList.size(); i++) {
				Entry entry = envVariables.addNewEntry();
				entry.set(envVariableList.get(i));
			}

			specialParameters.setEnvVariables(envVariables);
			((JobProperties) refObject).getBaseJobInfos().getJobInfos().getJobTypeDetails().setSpecialParameters(specialParameters);

		} else if (specialParameters.getEnvVariables() != null) {
			XmlCursor xmlCursor = ((JobProperties) refObject).getBaseJobInfos().getJobInfos().getJobTypeDetails().getSpecialParameters().getEnvVariables().newCursor();
			xmlCursor.removeXml();
		}
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
