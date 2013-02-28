package com.likya.tlossw.web.management;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;
import org.primefaces.component.accordionpanel.AccordionPanel;
import org.primefaces.component.tabview.Tab;
import org.primefaces.component.wizard.Wizard;
import org.primefaces.event.FlowEvent;

import com.likya.tlos.model.xmlbeans.webservice.BindingDocument.Binding;
import com.likya.tlos.model.xmlbeans.webservice.BindingListDocument.BindingList;
import com.likya.tlos.model.xmlbeans.webservice.BindingNameDocument.BindingName;
import com.likya.tlos.model.xmlbeans.webservice.EnumListDocument.EnumList;
import com.likya.tlos.model.xmlbeans.webservice.OperationDocument.Operation;
import com.likya.tlos.model.xmlbeans.webservice.ParameterListDocument.ParameterList;
import com.likya.tlos.model.xmlbeans.webservice.WebServiceDefinitionDocument.WebServiceDefinition;
import com.likya.tlossw.model.webservice.Function;
import com.likya.tlossw.model.webservice.Parameter;
import com.likya.tlossw.model.webservice.WebService;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.utils.ConstantDefinitions;
import com.likya.tlossw.webclient.TEJmxMpWSClient;

@ManagedBean(name = "wsWizardPanelMBean")
@ViewScoped
public class WebServiceWizardPanelMBean extends TlosSWBaseBean implements Serializable {

	private static final Logger logger = Logger.getLogger(WebServiceWizardPanelMBean.class);

	private static final long serialVersionUID = -6537847067749285956L;

	private String wsdlAddress;

	private WebServiceDefinition webServiceDefinition;

	private String serviceName;
	private String recordName;

	private ArrayList<Function> functionList;

	private Function currentFunction = new Function();

	private String result = null;

	private boolean skip;

	private final String WSDL_TAB = "wsdlTab";
	private final String OPERATIONS_TAB = "operationsTab";
	private final String OPERATION_TAB = "operationTab";
	
	private final String CONFIRM = "confirm";

	public void dispose() {
		wsdlAddress = null;
	}

	@PostConstruct
	public void init() {
		webServiceDefinition = WebServiceDefinition.Factory.newInstance();
	}

	public String onFlowProcess(FlowEvent event) {
		if (skip) {
			skip = false;
			return CONFIRM;

		} else {
			String oldStep = event.getOldStep();
			String newStep = event.getNewStep();

			if (oldStep.equals(WSDL_TAB) && newStep.equals(OPERATIONS_TAB)) {
				getWsMethodsAction();

			} else if (oldStep.equals(OPERATIONS_TAB) && newStep.equals(OPERATION_TAB)) {
				System.out.println("");

				Wizard wizard = (Wizard) event.getSource();
				Tab operationsTab = (Tab) wizard.getChildren().get(1);
				AccordionPanel accordionPanel = (AccordionPanel) operationsTab.getChildren().get(0);

				int activeTab = Integer.parseInt(accordionPanel.getActiveIndex());

				@SuppressWarnings("unchecked")
				ArrayList<Function> list = (ArrayList<Function>) accordionPanel.getValue();
				currentFunction = list.get(activeTab);

				result = null;
			}

			return newStep;
		}
	}

	public void getWsMethodsAction() {
		WebService webService = TEJmxMpWSClient.getWsOperationList(getWsdlAddress());

		if (webService == null) {
			addMessage("wsCozumleme", FacesMessage.SEVERITY_ERROR, "tlos.info.webservice.error", null);
			return;
		}

		serviceName = webService.getServiceName();

		if (serviceName == null || serviceName.equals("")) {
			addMessage("wsCozumleme", FacesMessage.SEVERITY_INFO, "tlos.info.webservice.nameNotFound", null);
		} else {
			recordName = serviceName;
		}

		functionList = webService.getFunctionList();

		if (functionList == null || functionList.size() == 0) {
			addMessage("wsCozumleme", FacesMessage.SEVERITY_ERROR, "tlos.info.webservice.operationsNotFound", null);
		}
	}

	public void testWsMethodAction(ActionEvent e) {
		setResult(TEJmxMpWSClient.callOperation(getWsdlAddress(), currentFunction));
	}

	public void saveWSDefinitionAction(ActionEvent e) {
		if(!setWsDefinitionID()) {
			return;
		}
		
		fillWSProperties();

		if (getDbOperations().insertWSDefinition(getWSPropertiesXML())) {
			addMessage("wsKayit", FacesMessage.SEVERITY_INFO, "tlos.success.wsDef.insert", null);
		} else {
			addMessage("wsKayit", FacesMessage.SEVERITY_ERROR, "tlos.error.wsDefinition.insert", null);
		}
	}
	
	public boolean setWsDefinitionID() {
		int wsDefinitionId = getDbOperations().getNextId(ConstantDefinitions.WSDEFINITION_ID);

		if (wsDefinitionId < 0) {
			addMessage("wsKayit", FacesMessage.SEVERITY_ERROR, "tlos.error.wsDefinition.getId", null);
			return false;
		}
		webServiceDefinition.setID(new BigInteger(wsDefinitionId + ""));

		return true;
	}

	public String getWSPropertiesXML() {
		QName qName = WebServiceDefinition.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String webServiceXML = webServiceDefinition.xmlText(xmlOptions);

		return webServiceXML;
	}

	private void fillWSProperties() {
		webServiceDefinition.setServiceName(recordName);
		webServiceDefinition.setWsdlAddress(wsdlAddress);

		BindingList bindingList = BindingList.Factory.newInstance();
		Binding binding = Binding.Factory.newInstance();
		binding.setBindingName(BindingName.SOAP_1_1);

		Operation currentOperation = Operation.Factory.newInstance();
		currentOperation.setOperationName(currentFunction.getFunctionName());

		ParameterList parameterList = ParameterList.Factory.newInstance();

		Iterator<Parameter> parameterIterator = currentFunction.getParameterList().iterator();

		while (parameterIterator.hasNext()) {
			Parameter param = parameterIterator.next();

			com.likya.tlos.model.xmlbeans.webservice.ParameterDocument.Parameter currentParam = com.likya.tlos.model.xmlbeans.webservice.ParameterDocument.Parameter.Factory.newInstance();
			currentParam.setParameterName(param.getParameterName());
			currentParam.setParameterType(param.getParameterType());
			currentParam.setIsEnum(param.getIsEnum());

			// parametre enum tipinde ise enum listesi dolduruluyor
			if (currentParam.getIsEnum()) {
				EnumList enumList = EnumList.Factory.newInstance();

				for (int i = 0; i < param.getEnumList().length; i++) {
					enumList.addNewEnumValue1();
					enumList.setEnumValue1Array(enumList.sizeOfEnumValue1Array() - 1, param.getEnumList()[i]);
				}

				currentParam.setEnumList(enumList);
			}

			currentParam.setParameterValue(param.getValue().toString());

			com.likya.tlos.model.xmlbeans.webservice.ParameterDocument.Parameter myParam = parameterList.addNewParameter();
			myParam.set(currentParam);
		}

		currentOperation.setParameterList(parameterList);

		Operation operation = binding.addNewOperation();
		operation.set(currentOperation);

		Binding currentBinding = bindingList.addNewBinding();
		currentBinding.set(binding);

		webServiceDefinition.setBindingList(bindingList);

		// TODO login kismi olmadigi icin simdilik userid degerini sabit verdim
		webServiceDefinition.setUserId(1);
	}

	public static Logger getLogger() {
		return logger;
	}

	public String getWsdlAddress() {
		return wsdlAddress;
	}

	public void setWsdlAddress(String wsdlAddress) {
		this.wsdlAddress = wsdlAddress;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public ArrayList<Function> getFunctionList() {
		return functionList;
	}

	public void setFunctionList(ArrayList<Function> functionList) {
		this.functionList = functionList;
	}

	public boolean isSkip() {
		return skip;
	}

	public void setSkip(boolean skip) {
		this.skip = skip;
	}

	public String getRecordName() {
		return recordName;
	}

	public void setRecordName(String recordName) {
		this.recordName = recordName;
	}

	public Function getCurrentFunction() {
		return currentFunction;
	}

	public void setCurrentFunction(Function currentFunction) {
		this.currentFunction = currentFunction;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public WebServiceDefinition getWebServiceDefinition() {
		return webServiceDefinition;
	}

	public void setWebServiceDefinition(WebServiceDefinition webServiceDefinition) {
		this.webServiceDefinition = webServiceDefinition;
	}

}
