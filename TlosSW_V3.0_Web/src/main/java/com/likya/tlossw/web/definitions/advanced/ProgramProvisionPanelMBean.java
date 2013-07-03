package com.likya.tlossw.web.definitions.advanced;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlOptions;

import com.likya.tlos.model.xmlbeans.common.TypeOfTimeDocument.TypeOfTime;
import com.likya.tlos.model.xmlbeans.programprovision.EndDateDocument;
import com.likya.tlos.model.xmlbeans.programprovision.LicenseDocument.License;
import com.likya.tlos.model.xmlbeans.programprovision.NameDocument;
import com.likya.tlos.model.xmlbeans.programprovision.ResourcePoolDocument.ResourcePool;
import com.likya.tlos.model.xmlbeans.programprovision.StartDateDocument;
import com.likya.tlos.model.xmlbeans.programprovision.TypeDocument.Type;
import com.likya.tlos.model.xmlbeans.programprovision.VersionDocument;
import com.likya.tlos.model.xmlbeans.sla.ResourceDocument.Resource;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.utils.DefinitionUtils;
import com.likya.tlossw.web.utils.WebInputUtils;

@ManagedBean(name = "ppPanelMBean")
@RequestScoped
public class ProgramProvisionPanelMBean extends TlosSWBaseBean implements Serializable {

	@ManagedProperty(value = "#{param.selectedProvisionID}")
	private String selectedProvisionID;

	@ManagedProperty(value = "#{param.insertCheck}")
	private String insertCheck;

	@ManagedProperty(value = "#{param.iCheck}")
	private String iCheck;

	private static final long serialVersionUID = 1L;

	private String selectedTZone = "Europe/Istanbul";

	private Collection<SelectItem> tZList;

	private Collection<SelectItem> typeOfTimeList;
	private String selectedTypeOfTime;

	private License license;

	private Date startDate;
	private String startTime;
	private Date endDate;
	private String endTime;

	private String resourceName;

	private Collection<SelectItem> resourceNameList = null;
	private String[] selectedResourceList;

	private String licenseType;
	private String minUser;
	private String maxUser;

	private boolean insertButton;

	@PostConstruct
	public void init() {
		resetProvisionAction();

		setResourceNameList(WebInputUtils.fillResourceNameList(getDbOperations().getResources()));

		setTZList(WebInputUtils.fillTZList());
		setTypeOfTimeList(WebInputUtils.fillTypesOfTimeList());

		if (iCheck != null && iCheck.equals("insert"))
			insertButton = true;

		if (insertCheck != null) {

			if (insertCheck.equals("update")) {
				insertButton = false;

				license = getDbOperations().searchProvisionByID(selectedProvisionID);

				if (license != null) {
					fillPanelFromProvision();
				}

			} else {
				insertButton = true;
			}
		}
	}

	public String getLicenseXML() {
		QName qName = License.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String licenseXML = license.xmlText(xmlOptions);

		return licenseXML;
	}

	public void resetProvisionAction() {
		license = License.Factory.newInstance();

		NameDocument nameDocument = NameDocument.Factory.newInstance();
		license.setName(nameDocument.getName());

		VersionDocument versionDocument = VersionDocument.Factory.newInstance();
		license.setVersion(versionDocument.getVersion());

		StartDateDocument startDateDocument = StartDateDocument.Factory.newInstance();
		license.setStartDate(startDateDocument.getStartDate());

		EndDateDocument endDateDocument = EndDateDocument.Factory.newInstance();
		license.setEndDate(endDateDocument.getEndDate());

		ResourcePool resourcePool = ResourcePool.Factory.newInstance();
		license.setResourcePool(resourcePool);

		Type type = Type.Factory.newInstance();
		license.setType(type);

		setSelectedTZone(new String("Europe/Istanbul"));
		selectedTypeOfTime = new String("Actual");

		license.setTimeZone(selectedTZone);
		license.setTypeOfTime(TypeOfTime.Enum.forString(selectedTypeOfTime));
	}

	private void fillPanelFromProvision() {
		// startDate = license.getStartDate().getTime();
		// endDate = license.getEndDate().getTime();

		startDate = DefinitionUtils.dateToDate(license.getStartDate().getTime(), selectedTZone);
		endDate = DefinitionUtils.dateToDate(license.getEndDate().getTime(), selectedTZone);

		String timeOutputFormat = new String("HH:mm:ss");

		// startTime = DefinitionUtils.dateToStringTime(license.getStartDate().getTime());
		// endTime = DefinitionUtils.dateToStringTime(license.getEndDate().getTime());
		startTime = DefinitionUtils.calendarToStringTimeFormat(license.getStartDate(), selectedTZone, timeOutputFormat);
		endTime = DefinitionUtils.calendarToStringTimeFormat(license.getEndDate(), selectedTZone, timeOutputFormat);

		licenseType = license.getType().getStringValue();

		if (license.getType().getMaxUser() != null) {
			maxUser = license.getType().getMaxUser().toString();
		}

		if (license.getType().getMinUser() != null) {
			minUser = license.getType().getMinUser().toString();
		}

		selectedTZone = license.getTimeZone();
		if (license.getTypeOfTime() != null)
			selectedTypeOfTime = license.getTypeOfTime().toString();
		else
			selectedTypeOfTime = new String("Broadcast");

		fillResourcePool();
	}

	private void fillResourcePool() {
		if (license.getResourcePool().getResourceArray() != null) {

			selectedResourceList = new String[license.getResourcePool().getResourceArray().length];

			for (int i = 0; i < license.getResourcePool().getResourceArray().length; i++) {
				selectedResourceList[i] = license.getResourcePool().getResourceArray(i);
			}
		}
	}

	public void updateProvisionAction(ActionEvent e) {
		fillProvisionProperties();

		if (getDbOperations().updateProvision(getLicenseXML())) {
			addMessage("insertProgramProvision", FacesMessage.SEVERITY_INFO, "tlos.success.dbAccessDef.update", null);
		} else {
			addMessage("insertProgramProvision", FacesMessage.SEVERITY_ERROR, "tlos.error.dbConnection.update", null);
		}
	}

	public void insertProvisionAction(ActionEvent e) {
		fillProvisionProperties();

		if (getDbOperations().insertProvision(getLicenseXML())) {
			addMessage("insertProgramProvision", FacesMessage.SEVERITY_INFO, "tlos.success.provision.insert", null);
			resetProvisionAction();
		} else {
			addMessage("insertProgramProvision", FacesMessage.SEVERITY_ERROR, "tlos.error.provision.insert", null);
		}
	}

	private void fillProvisionProperties() {
		if (startDate != null && startTime != null) {
			license.setStartDate(DefinitionUtils.dateTimeToXmlDateTime(startDate, startTime, selectedTZone));
		}

		if (endDate != null && endTime != null) {
			license.setEndDate(DefinitionUtils.dateTimeToXmlDateTime(endDate, endTime, selectedTZone));
		}

		// makine listesindekileri license tanimindaki resourcePool kismina set
		// ediyor
		ResourcePool resourcePool = ResourcePool.Factory.newInstance();
		if (selectedResourceList != null) {
			for (int i = 0; i < selectedResourceList.length; i++) {
				Resource resource = Resource.Factory.newInstance();
				resource.setStringValue(selectedResourceList[i].toString());

				resourcePool.addNewResource();
				resourcePool.setResourceArray(i, resource.getStringValue());
			}
		}
		license.setResourcePool(resourcePool);

		license.getType().setStringValue(licenseType);

		if (maxUser != null && !maxUser.equals("")) {
			license.getType().setMaxUser(new BigInteger(maxUser));
		}

		if (minUser != null && !minUser.equals("")) {
			license.getType().setMinUser(new BigInteger(minUser));
		}

		license.setUserId(getSessionMediator().getJmxAppUser().getAppUser().getId());
	}

	public boolean isInsertButton() {
		return insertButton;
	}

	public void setInsertButton(boolean insertButton) {
		this.insertButton = insertButton;
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

	public String getSelectedProvisionID() {
		return selectedProvisionID;
	}

	public void setSelectedProvisionID(String selectedProvisionID) {
		this.selectedProvisionID = selectedProvisionID;
	}

	public License getLicense() {
		return license;
	}

	public void setLicense(License license) {
		this.license = license;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public Collection<SelectItem> getResourceNameList() {
		return resourceNameList;
	}

	public void setResourceNameList(Collection<SelectItem> resourceNameList) {
		this.resourceNameList = resourceNameList;
	}

	public String[] getSelectedResourceList() {
		return selectedResourceList;
	}

	public void setSelectedResourceList(String[] selectedResourceList) {
		this.selectedResourceList = selectedResourceList;
	}

	public String getLicenseType() {
		return licenseType;
	}

	public void setLicenseType(String licenseType) {
		this.licenseType = licenseType;
	}

	public String getMinUser() {
		return minUser;
	}

	public void setMinUser(String minUser) {
		this.minUser = minUser;
	}

	public String getMaxUser() {
		return maxUser;
	}

	public void setMaxUser(String maxUser) {
		this.maxUser = maxUser;
	}

	public String getSelectedTZone() {
		return selectedTZone;
	}

	public void setSelectedTZone(String selectedTZone) {
		this.selectedTZone = selectedTZone;
	}

	public Collection<SelectItem> getTZList() {
		return tZList;
	}

	public void setTZList(Collection<SelectItem> tZList) {
		this.tZList = tZList;
	}

	public Collection<SelectItem> getTypeOfTimeList() {
		return typeOfTimeList;
	}

	public void setTypeOfTimeList(Collection<SelectItem> typeOfTimeList) {
		this.typeOfTimeList = typeOfTimeList;
	}

	public String getSelectedTypeOfTime() {
		return selectedTypeOfTime;
	}

	public void setSelectedTypeOfTime(String selectedTypeOfTime) {
		this.selectedTypeOfTime = selectedTypeOfTime;
	}

}
