package com.likya.tlossw.web.definitions.advanced;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlOptions;
import org.ogf.schemas.rns.x2009.x12.rns.RNSMetadataType;
import org.ogf.schemas.rns.x2009.x12.rns.RNSSupportType;
import org.ogf.schemas.rns.x2009.x12.rns.SupportsRNSType;
import org.w3.x2005.x08.addressing.AttributedURIType;
import org.w3.x2005.x08.addressing.EndpointReferenceType;

import com.likya.tlos.model.xmlbeans.jsdl.OperatingSystemTypeEnumeration;
import com.likya.tlos.model.xmlbeans.swresourcens.ResourceType;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.TlosSWBaseBean;

@ManagedBean(name = "resourcePanelMBean")
@RequestScoped
public class ResourcePanelMBean extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = 45790551446681215L;

	@ManagedProperty(value = "#{param.selectedResourceName}")
	private String selectedResourceName;

	@ManagedProperty(value = "#{param.insertCheck}")
	private String insertCheck;

	@ManagedProperty(value = "#{param.iCheck}")
	private String iCheck;

	private ResourceType resource;

	private String resourceName;
	private String endpointAddress;
	private boolean supportsRns = true;
	private String os;

	private boolean insertButton;

	@PostConstruct
	public void init() {
		resetResourceAction();

		if (iCheck != null && iCheck.equals("insert"))
			insertButton = true;

		if (insertCheck != null) {

			if (insertCheck.equals("update")) {
				insertButton = false;

				resource = getDbOperations().searchResourceByResourceName(selectedResourceName);
				if (resource != null) {
					resourceName = resource.getEntryName();
					endpointAddress = resource.getEndpoint().getAddress().getStringValue();
					supportsRns = Boolean.parseBoolean(resource.getMetadata().getSupportsRns().getValue().toString());
					os = resource.getOperatingSystemName().toString();
				}

			} else {
				insertButton = true;
			}
		}
	}

	public String getResourceXML() {
		//QName qName = ResourceListType.type.getOuterType().getDocumentElementName();
		//QName qName = ResourceType.type.getName();
		
		// TODO update işleminde Resource tagi ile güncelleme yapsın diye aşağıdaki şekilde yaptım.
		// üstteki kullanım için daha sonra değişiklik yapılabilir.
		QName qName = new QName("http://www.likyateknoloji.com/XML_SWResourceNS_types", "Resource", "lrns");
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String resourceXML = resource.xmlText(xmlOptions);
		return resourceXML;
	}

	public void resetResourceAction() {
		resource = ResourceType.Factory.newInstance();
		resourceName = "";
		endpointAddress = "";
		supportsRns = true;
		setOs(OperatingSystemTypeEnumeration.WINDOWS.toString());
	}

	public void updateResourceAction(ActionEvent e) {
		fillResourceProperties();

		if (getDbOperations().updateResource(getResourceXML())) {
			addMessage("insertResource", FacesMessage.SEVERITY_INFO, "tlos.success.resource.update", null);
		} else {
			addMessage("insertResource", FacesMessage.SEVERITY_ERROR, "tlos.error.resource.update", null);
		}
	}

	private void fillResourceProperties() {
		resource.setEntryName(resourceName);

		EndpointReferenceType endpointReferenceType = EndpointReferenceType.Factory.newInstance();
		AttributedURIType attributedURIType = AttributedURIType.Factory.newInstance();
		attributedURIType.setStringValue(endpointAddress);
		endpointReferenceType.setAddress(attributedURIType);
		resource.setEndpoint(endpointReferenceType);

		RNSMetadataType rnsMetadataType = RNSMetadataType.Factory.newInstance();
		SupportsRNSType supportsRNSType = SupportsRNSType.Factory.newInstance();
		if (supportsRns) {
			supportsRNSType.setValue(RNSSupportType.TRUE);
		} else {
			supportsRNSType.setValue(RNSSupportType.FALSE);
		}
		rnsMetadataType.setSupportsRns(supportsRNSType);
		resource.setMetadata(rnsMetadataType);

		resource.setOperatingSystemName(OperatingSystemTypeEnumeration.Enum.forString(os));
	}

	public void insertResourceAction(ActionEvent e) {
		fillResourceProperties();

		if (getDbOperations().insertResource(getResourceXML())) {
			addMessage("insertResource", FacesMessage.SEVERITY_INFO, "tlos.success.resource.insert", null);
			resetResourceAction();
		} else {
			addMessage("insertResource", FacesMessage.SEVERITY_ERROR, "tlos.error.resource.insert", null);
		}
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

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getEndpointAddress() {
		return endpointAddress;
	}

	public void setEndpointAddress(String endpointAddress) {
		this.endpointAddress = endpointAddress;
	}

	public boolean isSupportsRns() {
		return supportsRns;
	}

	public void setSupportsRns(boolean supportsRns) {
		this.supportsRns = supportsRns;
	}

	public String getSelectedResourceName() {
		return selectedResourceName;
	}

	public void setSelectedResourceName(String selectedResourceName) {
		this.selectedResourceName = selectedResourceName;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public ResourceType getResource() {
		return resource;
	}

	public void setResource(ResourceType resource) {
		this.resource = resource;
	}

}
