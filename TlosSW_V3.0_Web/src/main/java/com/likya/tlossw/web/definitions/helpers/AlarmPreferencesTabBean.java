package com.likya.tlossw.web.definitions.helpers;

import java.util.Collection;
import java.util.Iterator;

import javax.faces.model.SelectItem;

import org.apache.xmlbeans.XmlCursor;

import com.likya.tlos.model.xmlbeans.data.AlarmPreferenceDocument.AlarmPreference;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.ScenarioDocument.Scenario;
import com.likya.tlossw.web.definitions.JSBasePanelMBean;

public class AlarmPreferencesTabBean {
	
	// alarmPreference
	private String[] selectedAlarmList;
	
	private JSBasePanelMBean jsBasePanelMBean;

	public AlarmPreferencesTabBean(JSBasePanelMBean jsBasePanelMBean) {
		super();
		this.jsBasePanelMBean = jsBasePanelMBean;
	}
	
	public void resetTab() {
		selectedAlarmList = null;
	}
	
	/**
	 * xsd yapsında da scenaryo ve job nesnelerinin ortak alanları bir üst nesene de tanımlı olsaydı
	 * isScenario bilgisnin geçmesine gerek olmayacak.
	 * 
	 * @date 14.07.2013
	 * @author serkan taş
	 * @param isScenario
	 * @param refObject
	 */
	public void fillAlarmPreference(boolean isScenario, Object refObject) {

		if (selectedAlarmList != null && selectedAlarmList.length > 0) {
			AlarmPreference alarmPreference = AlarmPreference.Factory.newInstance();

			for (int i = 0; i < selectedAlarmList.length; i++) {
				String selectedId = selectedAlarmList[i].toString();

				Iterator<SelectItem> alarmIterator = getAlarmList().iterator();

				while (alarmIterator.hasNext()) {
					SelectItem alarm = alarmIterator.next();

					if (alarm.getValue().equals(selectedId)) {
						alarmPreference.addNewAlarmId();
						alarmPreference.setAlarmIdArray(alarmPreference.sizeOfAlarmIdArray() - 1, Integer.parseInt(selectedId));
					}
				}
			}

			if (isScenario) {
				((Scenario) refObject).setAlarmPreference(alarmPreference);
			} else {
				((JobProperties) refObject).setAlarmPreference(alarmPreference);
			}
		} else {
			if (isScenario) {
				if (((Scenario) refObject).getAlarmPreference() != null) {
					XmlCursor xmlCursor = ((Scenario) refObject).getAlarmPreference().newCursor();
					xmlCursor.removeXml();
				}
			} else if (((JobProperties) refObject).getAlarmPreference() != null) {
				XmlCursor xmlCursor = ((JobProperties) refObject).getAlarmPreference().newCursor();
				xmlCursor.removeXml();
			}
		}
	}

	public void fillAlarmPreferenceTab(boolean isScenario, Object refObject) {

		AlarmPreference alarmPreference = null;

		if (isScenario) {

			Scenario scenario = ((Scenario) refObject);

			if (scenario != null && scenario.getAlarmPreference() != null) {
				alarmPreference = scenario.getAlarmPreference();
			}

		} else {

			JobProperties jobProperties = ((JobProperties) refObject);

			if (jobProperties != null && jobProperties.getAlarmPreference() != null) {
				alarmPreference = jobProperties.getAlarmPreference();
			}

		}

		if (alarmPreference != null && alarmPreference.sizeOfAlarmIdArray() > 0) {
			int length = alarmPreference.sizeOfAlarmIdArray();
			selectedAlarmList = new String[length];

			for (int i = 0; i < length; i++) {
				selectedAlarmList[i] = alarmPreference.getAlarmIdArray(i) + "";
			}
		}
	}

	public Collection<SelectItem> getAlarmList() {
		return jsBasePanelMBean.getAlarmList();
	}

	public String[] getSelectedAlarmList() {
		return selectedAlarmList;
	}

	public void setSelectedAlarmList(String[] selectedAlarmList) {
		this.selectedAlarmList = selectedAlarmList;
	}

}
