package com.likya.tlossw.web.definitions.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.likya.tlos.model.xmlbeans.data.OSystemDocument.OSystem;
import com.likya.tlos.model.xmlbeans.state.JobStatusListDocument.JobStatusList;
import com.likya.tlos.model.xmlbeans.state.ReturnCodeDocument.ReturnCode;
import com.likya.tlos.model.xmlbeans.state.ReturnCodeListDocument.ReturnCodeList;
import com.likya.tlos.model.xmlbeans.state.ReturnCodeListDocument.ReturnCodeList.OsType;
import com.likya.tlos.model.xmlbeans.state.ScenarioStatusListDocument.ScenarioStatusList;
import com.likya.tlos.model.xmlbeans.state.Status;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlossw.web.definitions.JSBasePanelMBean;
import com.likya.tlossw.web.utils.WebInputUtils;

public class StateInfosTabBean {

	@ManagedProperty(value = "#{jsBasePanelMBean.oSystemList}")
	private Collection<SelectItem> oSystemList;

	@ManagedProperty(value = "#{jsBasePanelMBean.oSystem}")
	private String oSystem;
	
	@ManagedProperty(value = "#{jsBasePanelMBean.jsUpdateButton}")
	private boolean jsUpdateButton;
	
	private Status jobStatus;

	private String jobStatusName;

	private Collection<SelectItem> jobStatusNameList = null;

	private List<SelectItem> manyJobStatusList;
	private String[] selectedJobStatusList;

	/* jsStatusPopup */
	private boolean statusDialogShow = false;

	private String osType;
	private ReturnCode returnCode;
	private List<SelectItem> manyReturnCodeList;

	private JSBasePanelMBean jsBasePanelMBean;

	public StateInfosTabBean(JSBasePanelMBean jsBasePanelMBean) {
		super();
		this.jsBasePanelMBean = jsBasePanelMBean;
	}

	public void resetTab() {
		returnCode = ReturnCode.Factory.newInstance();

		jobStatus = Status.Factory.newInstance();
		jobStatusName = "";
		manyJobStatusList = new ArrayList<SelectItem>();
	}

	public void initJobStatusPopup(ActionEvent e) {
		setStatusDialogShow(!checkDuplicateStateName());

		osType = OSystem.WINDOWS.toString();
		jobStatus = Status.Factory.newInstance();
		setReturnCode(ReturnCode.Factory.newInstance());
		manyReturnCodeList = new ArrayList<SelectItem>();

	}

	public boolean checkDuplicateStateName() {
		if (getManyJobStatusList() != null) {

			for (int i = 0; i < getManyJobStatusList().size(); i++) {

				if (getManyJobStatusList().get(i).getValue().equals(jobStatusName)) {
					jsBasePanelMBean.addMessage("addReturnCode", FacesMessage.SEVERITY_ERROR, "tlos.info.job.status.duplicate", null);

					return true;
				}
			}
		}

		return false;
	}

	public void updateJobStatusAction(Status[] statusArray) {

		for (Status jStatus : statusArray) {
			if (jobStatus.getStatusName().toString().equals(jStatus.getStatusName().toString())) {
				jStatus = WebInputUtils.cloneJobStatus(jobStatus);

				jsBasePanelMBean.addMessage("addReturnCode", FacesMessage.SEVERITY_INFO, "tlos.info.job.code.update", null);

				break;
			}
		}

		statusDialogShow = false;
	}

	public void jobStatusEditAction(Status[] statusArray) {

		if (selectedJobStatusList == null || selectedJobStatusList.length == 0) {
			jsBasePanelMBean.addMessage("addReturnCode", FacesMessage.SEVERITY_ERROR, "tlos.info.job.status.choose", null);
			return;
		} else if (selectedJobStatusList.length > 1) {
			jsBasePanelMBean.addMessage("addReturnCode", FacesMessage.SEVERITY_ERROR, "tlos.info.job.status.choose.one", null);
			return;
		}

		for (Status status : statusArray) {
			if (status.getStatusName().toString().equals(selectedJobStatusList[0])) {
				jobStatus = WebInputUtils.cloneJobStatus(status);
				jobStatusName = selectedJobStatusList[0];

				break;
			}
		}

		manyReturnCodeList = new ArrayList<SelectItem>();

		for (int i = 0; i < jobStatus.getReturnCodeListArray().length; i++) {
			ReturnCodeList returnCodeList = jobStatus.getReturnCodeListArray(i);

			for (int j = 0; j < returnCodeList.getReturnCodeArray().length; j++) {
				ReturnCode returnCode = returnCodeList.getReturnCodeArray(j);

				SelectItem item = new SelectItem();
				item.setValue(returnCode.getCode());
				item.setLabel(returnCodeList.getOsType().toString() + " : " + returnCode.getCode() + " -> " + jobStatusName);

				manyReturnCodeList.add(item);
			}
		}

		osType = OSystem.WINDOWS.toString();
		returnCode = ReturnCode.Factory.newInstance();

		statusDialogShow = true;
	}

	public void addJReturnCodeAction(boolean isScenario, Object refObject) {

		// TODO donus kodu eklerken ayni is donus statusu icin ayni isletim
		// sistemi secilerek
		// ayni kod birden fazla tanimlanabiliyor
		// bu kontrol yapilip ayni kodun eklenmesi engellenecek

		// guncelleme icin acildiginda duplicate kontrolunu yapmiyor
		if (jobStatus.getStsId() == null || jobStatus.getStsId().equals("")) {
			if (!checkDuplicateStateName()) {
				statusDialogShow = true;
			} else {
				statusDialogShow = false;

				return;
			}
		}

		jobStatus.setStatusName(StatusName.Enum.forString(jobStatusName));

		ReturnCode tmpReturnCode = WebInputUtils.cloneReturnCode(getReturnCode());

		// girilen statu icin onceden kayit yapilmamissa gerekli bilesenler
		// olusturuluyor
		if (jobStatus.getReturnCodeListArray() == null || jobStatus.sizeOfReturnCodeListArray() == 0) {
			ReturnCodeList returnCodeList = jobStatus.addNewReturnCodeList();
			returnCodeList.setOsType(OsType.Enum.forString(osType));

			tmpReturnCode.setCdId("1");

			ReturnCode returnCode = (jobStatus.getReturnCodeListArray()[0]).addNewReturnCode();
			returnCode.set(tmpReturnCode);

			// girilen statu icin onceden kayit yapilmissa, girilen isletim
			// sistemi icin onceden kayit yapilmis mi diye kontrol ediyor
		} else {
			boolean osIsDefined = false;

			for (int j = 0; j < jobStatus.sizeOfReturnCodeListArray(); j++) {
				ReturnCodeList returnCodeList = jobStatus.getReturnCodeListArray()[j];

				if (returnCodeList.getOsType().toString().toLowerCase().equals(osType.toLowerCase())) {
					int lastElementIndex = returnCodeList.getReturnCodeArray().length - 1;

					String maxId = returnCodeList.getReturnCodeArray()[lastElementIndex].getCdId();

					tmpReturnCode.setCdId((Integer.parseInt(maxId) + 1) + "");

					returnCodeList.setOsType(OsType.Enum.forString(osType));

					ReturnCode returnCode = returnCodeList.addNewReturnCode();
					returnCode.set(tmpReturnCode);

					osIsDefined = true;
				}
			}

			// girilen isletim sistemi tanimlanmamissa gerekli bilesenler
			// olusturuluyor
			if (!osIsDefined) {
				ReturnCodeList returnCodeList = jobStatus.addNewReturnCodeList();
				returnCodeList.setOsType(OsType.Enum.forString(osType));

				tmpReturnCode.setCdId("1");

				ReturnCode returnCode = returnCodeList.addNewReturnCode();
				returnCode.set(tmpReturnCode);
			}

			if (isScenario) {
				ScenarioStatusList scenarioStatusList = (ScenarioStatusList) refObject;
				// hazirlanan status nesnesi scenariostatusList icine koyuluyor
				for (int i = 0; i < scenarioStatusList.sizeOfScenarioStatusArray(); i++) {
					if (scenarioStatusList.getScenarioStatusArray(i).getStatusName().equals(jobStatus.getStatusName())) {
						scenarioStatusList.getScenarioStatusArray(i).set(jobStatus);
					}
				}
			} else {
				JobStatusList jobStatusList = (JobStatusList) refObject;
				// hazirlanan job status nesnesi jobstatusList icine koyuluyor
				for (int i = 0; i < jobStatusList.sizeOfJobStatusArray(); i++) {
					if (jobStatusList.getJobStatusArray(i).getStatusName().equals(jobStatus.getStatusName())) {
						jobStatusList.getJobStatusArray(i).set(jobStatus);
					}
				}
			}
		}

		if (manyReturnCodeList == null) {
			manyReturnCodeList = new ArrayList<SelectItem>();
		}

		// islem yapilan job icin onceden herhangi bir statu tanimi yapilmis mi
		// diye kontrol ediyor, yapilmamissa job tanimindaki gerekli bilesenleri
		// ekliyor
		if (isScenario) {
			ScenarioStatusList scenarioStatusList = (ScenarioStatusList) refObject;
			if (scenarioStatusList == null || scenarioStatusList.sizeOfScenarioStatusArray() == 0) {
				scenarioStatusList = ScenarioStatusList.Factory.newInstance();

				Status status = scenarioStatusList.addNewScenarioStatus();
				status.set(jobStatus);
			}
		} else {
			JobStatusList jobStatusList = (JobStatusList) refObject;
			if (jobStatusList == null || jobStatusList.sizeOfJobStatusArray() == 0) {
				jobStatusList = JobStatusList.Factory.newInstance();

				Status status = jobStatusList.addNewJobStatus();
				status.set(jobStatus);
			}
		}

		SelectItem item = new SelectItem();
		item.setValue(tmpReturnCode.getCode());
		item.setLabel(osType + " : " + tmpReturnCode.getCode() + " -> " + jobStatusName);

		manyReturnCodeList.add(item);
	}

	public void fillJobStatusList() {
		if (jobStatusNameList == null) {
			jobStatusNameList = WebInputUtils.fillJobStatusList();
		}
	}

	public List<SelectItem> getManyJobStatusList() {
		return manyJobStatusList;
	}

	public void setManyJobStatusList(List<SelectItem> manyJobStatusList) {
		this.manyJobStatusList = manyJobStatusList;
	}

	public String[] getSelectedJobStatusList() {
		return selectedJobStatusList;
	}

	public void setSelectedJobStatusList(String[] selectedJobStatusList) {
		this.selectedJobStatusList = selectedJobStatusList;
	}

	public JSBasePanelMBean getJsBasePanelMBean() {
		return jsBasePanelMBean;
	}

	public boolean isStatusDialogShow() {
		return statusDialogShow;
	}

	public void setStatusDialogShow(boolean statusDialogShow) {
		this.statusDialogShow = statusDialogShow;
	}

	public ReturnCode getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(ReturnCode returnCode) {
		this.returnCode = returnCode;
	}

	public List<SelectItem> getManyReturnCodeList() {
		return manyReturnCodeList;
	}

	public void setManyReturnCodeList(List<SelectItem> manyReturnCodeList) {
		this.manyReturnCodeList = manyReturnCodeList;
	}

	public Status getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(Status jobStatus) {
		this.jobStatus = jobStatus;
	}

	public String getJobStatusName() {
		return jobStatusName;
	}

	public void setJobStatusName(String jobStatusName) {
		this.jobStatusName = jobStatusName;
	}

	public Collection<SelectItem> getJobStatusNameList() {
		return jobStatusNameList;
	}

	public void setJobStatusNameList(Collection<SelectItem> jobStatusNameList) {
		this.jobStatusNameList = jobStatusNameList;
	}

	public Collection<SelectItem> getoSystemList() {
		return oSystemList;
	}

	public void setoSystemList(Collection<SelectItem> oSystemList) {
		this.oSystemList = oSystemList;
	}

	public String getoSystem() {
		return oSystem;
	}

	public void setoSystem(String oSystem) {
		this.oSystem = oSystem;
	}

	public String getOsType() {
		return osType;
	}

	public void setOsType(String osType) {
		this.osType = osType;
	}

	public boolean isJsUpdateButton() {
		return jsUpdateButton;
	}

	public void setJsUpdateButton(boolean jsUpdateButton) {
		this.jsUpdateButton = jsUpdateButton;
	}

}
