package com.likya.tlossw.web.management;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlOptions;

import com.likya.tlos.model.xmlbeans.common.ActiveDocument.Active;
import com.likya.tlos.model.xmlbeans.ftpadapter.AdapterTypeDocument.AdapterType;
import com.likya.tlos.model.xmlbeans.ftpadapter.AuthenticationTypeDocument.AuthenticationType;
import com.likya.tlos.model.xmlbeans.ftpadapter.ConnectionDocument.Connection;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpPropertiesDocument.FtpProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpSecureDocument.FtpSecure;
import com.likya.tlos.model.xmlbeans.ftpadapter.ProxyPropertiesDocument.ProxyProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.SftpPropertiesDocument.SftpProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.TransportProviderDocument.TransportProvider;
import com.likya.tlossw.model.FTPAccessInfoTypeClient;
import com.likya.tlossw.model.jmx.JmxUser;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.utils.WebInputUtils;
import com.likya.tlossw.webclient.TEJmxMpValidationClient;

@ManagedBean(name = "ftpAccessPanelMBean")
@ViewScoped
public class FTPAccessPanelMBean extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = -365882428087470839L;

	private String selectedFTPAccessID;
	private String insertCheck;
	private String iCheck;

	private FtpProperties ftpProperties;

	private String adapterType;
	private Collection<SelectItem> adapterTypeList = null;

	private String connName;
	private String confirmUserPassword;
	private String ftpPortNumber;
	private String active;

	// SFTP ozellikleri
	private Collection<SelectItem> sftpAuthenticationTypeList = null;
	private String sftpAuthenticationType;
	private Collection<SelectItem> sftpTransportProviderList = null;
	private String sftpTransportProvider;
	private String sftpUseProxy;
	private String proxyUserName;
	private String proxyPassword;
	private String confirmProxyPassword;
	private String proxyIpAddress;
	private String proxyPortNumber;

	private boolean insertButton;

	public void dispose() {
		resetFTPAccessProfileAction();
	}

	@PostConstruct
	public void init() {
		resetFTPAccessProfileAction();

		fillAdapterTypeList();
		fillSftpAuthenticationTypeList();
		fillSftpTransportProviderList();

		selectedFTPAccessID = String.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("selectedFTPAccessID"));
		insertCheck = String.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("insertCheck"));
		iCheck = String.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("iCheck"));

		if (iCheck != null && iCheck.equals("insert"))
			insertButton = true;

		if (insertCheck != null) {

			if (insertCheck.equals("update")) {

				insertButton = false;

				ftpProperties = getDbOperations().searchFTPConnectionById(Integer.parseInt(selectedFTPAccessID));

				if (ftpProperties != null) {
					fillFtpPanel();
				}

			} else {
				insertButton = true;
			}
		}
	}

	private void fillFtpPanel() {
		ftpPortNumber = ftpProperties.getConnection().getFtpPortNumber() + "";
		confirmUserPassword = ftpProperties.getConnection().getUserPassword();
		active = ftpProperties.getActive().toString();

		if (ftpProperties.getFtpSecure() != null && ftpProperties.getFtpSecure().getSftpProperties() != null) {
			adapterType = AdapterType.SFTP_PROCESS.toString();

			SftpProperties sftpProperties = ftpProperties.getFtpSecure().getSftpProperties();

			sftpAuthenticationType = sftpProperties.getAuthenticationType().toString();
			sftpTransportProvider = sftpProperties.getTransportProvider().toString();
			sftpUseProxy = sftpProperties.getUseProxy() + "";

			if (sftpProperties.getUseProxy()) {
				ProxyProperties proxyProperties = sftpProperties.getProxyProperties();
				proxyUserName = proxyProperties.getUserName();
				proxyPassword = proxyProperties.getUserPassword();
				confirmProxyPassword = proxyPassword;
				proxyIpAddress = proxyProperties.getIpAddress();
				proxyPortNumber = proxyProperties.getPortNumber() + "";
			}

		} else {
			adapterType = AdapterType.FTP_PROCESS.toString();
		}

		// TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + ftpProperties.getId(), e.getComponent().getId(), resolveMessage("tlos.trace.ftpAccess.edit.desc").toString());
	}

	public void resetFTPAccessProfileAction() {
		ftpProperties = FtpProperties.Factory.newInstance();
		Connection connection = Connection.Factory.newInstance();
		ftpProperties.setConnection(connection);

		connName = "";
		adapterType = "";
		confirmUserPassword = "";
		ftpPortNumber = "";
		active = "";

		sftpAuthenticationType = "";
		sftpTransportProvider = "";
		sftpUseProxy = "";
		proxyUserName = "";
		proxyPassword = "";
		confirmProxyPassword = "";
		proxyIpAddress = "";
		proxyPortNumber = "";
	}

	public String getFTPPropertiesXML() {
		QName qName = FtpProperties.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String ftpPropertiesXML = ftpProperties.xmlText(xmlOptions);

		return ftpPropertiesXML;
	}

	public void updateFTPAccessAction(ActionEvent e) {
		// baglanti ismi tekil olmali
		if (getDbOperations().checkFTPConnectionName(getFTPPropertiesXML())) {

			if (adapterType.equals(AdapterType.SFTP_PROCESS.toString())) {
				fillSftpProperties();
			}

			ftpProperties.getConnection().setFtpPortNumber(new Short(ftpPortNumber));
			ftpProperties.setActive(Active.Enum.forString(active));

			if (getDbOperations().updateFTPAccessConnection(getFTPPropertiesXML())) {
				addMessage("updateFTPAccessConnection", FacesMessage.SEVERITY_INFO, "tlos.success.ftpConnectionDef.update", null);
			} else {
				addMessage("updateFTPAccessConnection", FacesMessage.SEVERITY_ERROR, "tlos.error.ftpConnection.update", null);
			}

			// TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + ftpProperties.getId(), "updateFTPAccessConnection", resolveMessage("tlos.trace.ftpAccess.update.desc").toString());
		} else {
			addMessage("updateFTPAccessConnection", FacesMessage.SEVERITY_ERROR, "tlos.error.ftpConnection.dublicateName", null);
		}
	}

	public void insertFTPAccessAction(ActionEvent e) {
		if (setFtpConnectionID()) {

			// baglanti ismi tekil olmali
			if (getDbOperations().checkFTPConnectionName(getFTPPropertiesXML())) {

				if (adapterType.equals(AdapterType.SFTP_PROCESS.toString())) {
					fillSftpProperties();
				}

				ftpProperties.getConnection().setFtpPortNumber(new Short(ftpPortNumber));
				ftpProperties.setActive(Active.Enum.forString(active));

				if (getDbOperations().insertFTPAccessConnection(getFTPPropertiesXML())) {
					addMessage("insertFTPAccessConnection", FacesMessage.SEVERITY_INFO, "tlos.success.ftpConnectionDef.insert", null);
					resetFTPAccessProfileAction();
				} else {
					addMessage("insertFTPAccessConnection", FacesMessage.SEVERITY_ERROR, "tlos.error.ftpConnection.insert", null);
				}

				// TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "insertFTPAccessConnection", e.getComponent().getId(), resolveMessage("tlos.trace.ftpAccess.insert.desc").toString());

			} else {
				addMessage("insertFTPAccessConnection", FacesMessage.SEVERITY_ERROR, "tlos.error.ftpConnection.dublicateName", null);
			}
		}
	}

	private void fillSftpProperties() {
		FtpSecure ftpSecure = FtpSecure.Factory.newInstance();

		SftpProperties sftpProperties = SftpProperties.Factory.newInstance();
		sftpProperties.setAuthenticationType(AuthenticationType.Enum.forString(getSftpAuthenticationType()));
		sftpProperties.setTransportProvider(TransportProvider.Enum.forString(getSftpTransportProvider()));
		sftpProperties.setUseProxy(Boolean.parseBoolean(getSftpUseProxy()));

		if (Boolean.parseBoolean(getSftpUseProxy())) {
			ProxyProperties proxyProperties = ProxyProperties.Factory.newInstance();
			proxyProperties.setUserName(getProxyUserName());
			proxyProperties.setUserPassword(getProxyPassword());
			proxyProperties.setIpAddress(getProxyIpAddress());
			proxyProperties.setPortNumber(new Short(getProxyPortNumber()));

			sftpProperties.setProxyProperties(proxyProperties);
		}

		ftpSecure.setSftpProperties(sftpProperties);
		ftpProperties.setFtpSecure(ftpSecure);
	}

	// veri tabaninda kayitli siradaki id degerini set ediyor
	public boolean setFtpConnectionID() {
		int ftpConnectionId = getDbOperations().getNextFTPConnectionId();

		if (ftpConnectionId < 0) {
			addMessage("insertFtpConnection", FacesMessage.SEVERITY_ERROR, "tlos.error.ftpConnection.getId", null);
			return false;
		}
		ftpProperties.setId(ftpConnectionId);

		return true;
	}

	public void testFTPAccessAction(ActionEvent e) {
		// Simdilik sftp yapilmadigi icin onunla ilgili test etme kismi yok. Bu yuzden sftp alanlarini doldurmadim
		/*if (adapterType.equals(AdapterType.SFTP_PROCESS.toString())) {
			fillSftpProperties();
		}*/

		ftpProperties.getConnection().setFtpPortNumber(new Short(ftpPortNumber));
		ftpProperties.setActive(Active.Enum.forString(active));
		
		FTPAccessInfoTypeClient ftpAccessInfoTypeClient = new FTPAccessInfoTypeClient();
		ftpAccessInfoTypeClient.setIpAddress(ftpProperties.getConnection().getIpAddress());
		ftpAccessInfoTypeClient.setPort(ftpProperties.getConnection().getFtpPortNumber());
		ftpAccessInfoTypeClient.setUserName(ftpProperties.getConnection().getUserName());
		ftpAccessInfoTypeClient.setPassword(ftpProperties.getConnection().getUserPassword());
		
		String message = TEJmxMpValidationClient.checkFTPAccess(new JmxUser(), ftpAccessInfoTypeClient);
		
		System.out.println(message);
		
		/*FTPClient ftpClient = new FTPClient();
		
		try {
			if (ftpProperties.getConnection().getFtpPortNumber() == 0) {
				ftpClient.connect(ftpProperties.getConnection().getIpAddress());
			} else {
				ftpClient.connect(ftpProperties.getConnection().getIpAddress(), ftpProperties.getConnection().getFtpPortNumber());
			}
		} catch (Exception ex) {
			addMessage("testftpAccessConnection", FacesMessage.SEVERITY_ERROR, "tlos.error.ftpConnection.test", null);
			
			return;
		}
		
		boolean login = false;
		
		try {
			login = ftpClient.login(ftpProperties.getConnection().getUserName(), ftpProperties.getConnection().getUserPassword());
		} catch (Exception ex) {
			addMessage("testftpAccessConnection", FacesMessage.SEVERITY_ERROR, "tlos.error.ftpConnection.testLogin", null);
			
			return;
		}
		
		if (login) {
			addMessage("testftpAccessConnection", FacesMessage.SEVERITY_INFO, "tlos.success.ftpConnectionDef.test", null);
		} else {
			addMessage("testftpAccessConnection", FacesMessage.SEVERITY_ERROR, "tlos.error.ftpConnection.testLogin", null);
		}*/
	}
	
	private void fillAdapterTypeList() {
		if (adapterTypeList == null) {
			adapterTypeList = WebInputUtils.fillAdapterTypeList();
		}
	}

	private void fillSftpAuthenticationTypeList() {
		if (sftpAuthenticationTypeList == null) {
			sftpAuthenticationTypeList = WebInputUtils.fillSftpAuthenticationTypeList();
		}
	}

	private void fillSftpTransportProviderList() {
		if (sftpTransportProviderList == null) {
			sftpTransportProviderList = WebInputUtils.fillSftpTransportProviderList();
		}
	}

	public String getSelectedFTPAccessID() {
		return selectedFTPAccessID;
	}

	public void setSelectedFTPAccessID(String selectedFTPAccessID) {
		this.selectedFTPAccessID = selectedFTPAccessID;
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

	public FtpProperties getFtpProperties() {
		return ftpProperties;
	}

	public void setFtpProperties(FtpProperties ftpProperties) {
		this.ftpProperties = ftpProperties;
	}

	public String getAdapterType() {
		return adapterType;
	}

	public void setAdapterType(String adapterType) {
		this.adapterType = adapterType;
	}

	public Collection<SelectItem> getAdapterTypeList() {
		return adapterTypeList;
	}

	public void setAdapterTypeList(Collection<SelectItem> adapterTypeList) {
		this.adapterTypeList = adapterTypeList;
	}

	public String getConnName() {
		return connName;
	}

	public void setConnName(String connName) {
		this.connName = connName;
	}

	public String getConfirmUserPassword() {
		return confirmUserPassword;
	}

	public void setConfirmUserPassword(String confirmUserPassword) {
		this.confirmUserPassword = confirmUserPassword;
	}

	public String getFtpPortNumber() {
		return ftpPortNumber;
	}

	public void setFtpPortNumber(String ftpPortNumber) {
		this.ftpPortNumber = ftpPortNumber;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public Collection<SelectItem> getSftpAuthenticationTypeList() {
		return sftpAuthenticationTypeList;
	}

	public void setSftpAuthenticationTypeList(Collection<SelectItem> sftpAuthenticationTypeList) {
		this.sftpAuthenticationTypeList = sftpAuthenticationTypeList;
	}

	public String getSftpAuthenticationType() {
		return sftpAuthenticationType;
	}

	public void setSftpAuthenticationType(String sftpAuthenticationType) {
		this.sftpAuthenticationType = sftpAuthenticationType;
	}

	public Collection<SelectItem> getSftpTransportProviderList() {
		return sftpTransportProviderList;
	}

	public void setSftpTransportProviderList(Collection<SelectItem> sftpTransportProviderList) {
		this.sftpTransportProviderList = sftpTransportProviderList;
	}

	public String getSftpTransportProvider() {
		return sftpTransportProvider;
	}

	public void setSftpTransportProvider(String sftpTransportProvider) {
		this.sftpTransportProvider = sftpTransportProvider;
	}

	public String getSftpUseProxy() {
		return sftpUseProxy;
	}

	public void setSftpUseProxy(String sftpUseProxy) {
		this.sftpUseProxy = sftpUseProxy;
	}

	public String getProxyUserName() {
		return proxyUserName;
	}

	public void setProxyUserName(String proxyUserName) {
		this.proxyUserName = proxyUserName;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	public String getConfirmProxyPassword() {
		return confirmProxyPassword;
	}

	public void setConfirmProxyPassword(String confirmProxyPassword) {
		this.confirmProxyPassword = confirmProxyPassword;
	}

	public String getProxyIpAddress() {
		return proxyIpAddress;
	}

	public void setProxyIpAddress(String proxyIpAddress) {
		this.proxyIpAddress = proxyIpAddress;
	}

	public String getProxyPortNumber() {
		return proxyPortNumber;
	}

	public void setProxyPortNumber(String proxyPortNumber) {
		this.proxyPortNumber = proxyPortNumber;
	}

	public boolean isInsertButton() {
		return insertButton;
	}

	public void setInsertButton(boolean insertButton) {
		this.insertButton = insertButton;
	}

}
