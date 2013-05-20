package com.likya.tlossw.web.definitions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.common.JobTypeDetailsDocument.JobTypeDetails;
import com.likya.tlos.model.xmlbeans.common.SpecialParametersDocument.SpecialParameters;
import com.likya.tlos.model.xmlbeans.webservice.WebServiceDefinitionDocument.WebServiceDefinition;
import com.likya.tlossw.web.utils.WebInputUtils;

@ManagedBean
@ViewScoped
public class WebServicePanelMBean extends JobBaseBean implements Serializable {

	private static final Logger logger = Logger.getLogger(WebServicePanelMBean.class);

	private static final long serialVersionUID = 1911439607648366142L;

	private Collection<SelectItem> webServiceDefinitionList = null;
	private String webServiceDefinition;

	private ArrayList<WebServiceDefinition> webServiceList = new ArrayList<WebServiceDefinition>();
	private WebServiceDefinition selectedWebService = null;

	public void dispose() {

	}

	@PostConstruct
	public void init() {
		initJobPanel();

		webServiceDefinition = "";
		selectedWebService = null;

		// TODO kullanici giris ekrani yapildiktan sonra
		// giris yapan kullanicinin id'si alinacak
		int userId = 1;

		webServiceList = getDbOperations().getWebServiceListForActiveUser(userId);
		setWebServiceDefinitionList(WebInputUtils.fillWebServiceDefinitionList(webServiceList));
	}

	public void fillTabs() {
		fillJobPanel();
		fillWebServiceProperties();
	}

	private void fillWebServiceProperties() {
		JobTypeDetails jobTypeDetails = getJobProperties().getBaseJobInfos().getJobInfos().getJobTypeDetails();

		if (jobTypeDetails.getSpecialParameters() != null && jobTypeDetails.getSpecialParameters().getWebServiceDefinition() != null) {
			selectedWebService = jobTypeDetails.getSpecialParameters().getWebServiceDefinition();
			webServiceDefinition = selectedWebService.getID() + "";
		}
	}

	// web servis isi tanimlanirken veri tabaninda kayitli web servislerin
	// oldugu listede baska bir eleman secildiginde buraya geliyor,
	// o servisin ozelliklerini popup icinde gosteriyor
	public void selectedWSChanged(ValueChangeEvent event) {
		String selectedWSID = event.getNewValue().toString();

		for (int i = 0; i < webServiceList.size(); i++) {
			WebServiceDefinition webServiceDefinition = webServiceList.get(i);

			if (webServiceDefinition.getID().toString().equals(selectedWSID)) {
				selectedWebService = webServiceDefinition;
				return;
			}
		}
	}

	public void insertJobAction() {
		if (validateTimeManagement()) {
			fillJobProperties();
			fillWebServicePropertyDetails();
	
			insertJobDefinition();
		}
	}

	public void updateJobAction() {
		fillJobProperties();
		fillWebServicePropertyDetails();

		updateJobDefinition();
	}

	private void fillWebServicePropertyDetails() {
		JobTypeDetails jobTypeDetails = getJobProperties().getBaseJobInfos().getJobInfos().getJobTypeDetails();

		if (jobTypeDetails.getSpecialParameters() == null) {
			SpecialParameters specialParameters = SpecialParameters.Factory.newInstance();
			WebServiceDefinition webServiceDef = specialParameters.addNewWebServiceDefinition();
			webServiceDef.set(selectedWebService);

			jobTypeDetails.setSpecialParameters(specialParameters);
		} else {
			jobTypeDetails.getSpecialParameters().setWebServiceDefinition(selectedWebService);
		}
	}

	public static Logger getLogger() {
		return logger;
	}

	public Collection<SelectItem> getWebServiceDefinitionList() {
		return webServiceDefinitionList;
	}

	public void setWebServiceDefinitionList(Collection<SelectItem> webServiceDefinitionList) {
		this.webServiceDefinitionList = webServiceDefinitionList;
	}

	public String getWebServiceDefinition() {
		return webServiceDefinition;
	}

	public void setWebServiceDefinition(String webServiceDefinition) {
		this.webServiceDefinition = webServiceDefinition;
	}

	public ArrayList<WebServiceDefinition> getWebServiceList() {
		return webServiceList;
	}

	public void setWebServiceList(ArrayList<WebServiceDefinition> webServiceList) {
		this.webServiceList = webServiceList;
	}

	public WebServiceDefinition getSelectedWebService() {
		return selectedWebService;
	}

	public void setSelectedWebService(WebServiceDefinition selectedWebService) {
		this.selectedWebService = selectedWebService;
	}

}
