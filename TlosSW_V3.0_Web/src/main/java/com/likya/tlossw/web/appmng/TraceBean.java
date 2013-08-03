package com.likya.tlossw.web.appmng;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlOptions;

import com.likya.tlos.model.xmlbeans.trace.TraceDocument.Trace;
import com.likya.tlos.model.xmlbeans.trace.TrcSourceDocument.TrcSource;
import com.likya.tlos.model.xmlbeans.trace.TrcTimeDocument.TrcTime;
import com.likya.tlos.model.xmlbeans.trace.TrcUserAgentDocument.TrcUserAgent;
import com.likya.tlos.model.xmlbeans.trace.TrcUserDocument.TrcUser;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;

public class TraceBean implements Serializable {

	private static final long serialVersionUID = -7995904142279762207L;
	@SuppressWarnings("unused")
	private static final Log logger = LogFactory.getLog(LocaleBean.class);
	private static final String PROJECT_NAME = "TlosSWWeb";

	public static long dateDiffWithNow(long sDate) {

		Date now = Calendar.getInstance().getTime();
		long timeDiff = now.getTime() - sDate;
		
		return timeDiff;
	}
	
	public static void traceData(StackTraceElement stackTraceElement, String sourceValue, String componentId, String desc) {
		long startTime = System.currentTimeMillis();
		Trace trace = generateTrace();
		
		//Person user = generateLoginUser(getManagerMediator().getLoginManager().getLoginBean());

		HttpServletRequest httpRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		String userAgent = httpRequest.getHeader("User-Agent");
		String ipAddress = httpRequest.getRemoteAddr();

		trace.setId(-1);

//		trace.getTrcTime().setMillis(System.currentTimeMillis());
//		trace.getTrcTime().setStringValue(Utils.getCurrentTimeWithMilliseconds());
//
//		trace.getTrcUser().setId(user.getId());
//		trace.getTrcUser().setPassword(user.getUserPassword());
//		if (user.getRole() != null) {
//			trace.getTrcUser().setRole(user.getRole().toString());
//		}
//		trace.getTrcUser().setStringValue(user.getUserName());

		trace.getTrcUserAgent().setIp(ipAddress);
		trace.getTrcUserAgent().setStringValue(userAgent);

		trace.getTrcSource().setJavaProject(PROJECT_NAME);
		trace.getTrcSource().setPackage(stackTraceElement.getClassName());
		trace.getTrcSource().setClass1(stackTraceElement.getFileName());
		trace.getTrcSource().setMethod(stackTraceElement.getMethodName());
		trace.getTrcSource().setStringValue(sourceValue);

		trace.setComponentId(componentId);
		trace.setDescription(desc);
		System.err.println(" generateTrace : " + dateDiffWithNow(startTime) + "ms");
		// Serkan Ta� 24.10.2012
		// TODO Performans iyile�tirilmesi yap�lana kadar kullan�lmayacak !
		// TEJmxMpDBClient.insertTrace(ManagerMediator.getJmxUser(), getTraceXML(trace));

		System.err.println(" traceData : " + dateDiffWithNow(startTime) + "ms");
	}

	public static String getTraceXML(Trace trace) {
		QName qName = Trace.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String traceXML = trace.xmlText(xmlOptions);

		return traceXML;
	}

//	protected static ManagerMediator getManagerMediator() {
//		ManagerMediator managerMediator = (ManagerMediator) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("managerMediator");
//		return managerMediator;
//	}

	private static Trace generateTrace() {
		Trace trace = Trace.Factory.newInstance();

		TrcTime trcTime = TrcTime.Factory.newInstance();
		trace.setTrcTime(trcTime);

		TrcUser trcUser = TrcUser.Factory.newInstance();
		trace.setTrcUser(trcUser);

		TrcUserAgent trcUserAgent = TrcUserAgent.Factory.newInstance();
		trace.setTrcUserAgent(trcUserAgent);

		TrcSource trcSource = TrcSource.Factory.newInstance();
		trace.setTrcSource(trcSource);

		return trace;
	}

	/*
	private static Person generateLoginUser(LoginBean loginBean) {
		Person attemptedUser = Person.Factory.newInstance();
		Person logginedUser = null; //loginBean.getLogginedUser();
		if (logginedUser.getUserName() == null || logginedUser.getUserPassword() == null) {
			attemptedUser.setUserName(loginBean.getUserName());
			attemptedUser.setUserPassword(loginBean.getUserPassword());
			attemptedUser.setId(-1);
			return attemptedUser;
		}
		return logginedUser;
	}
	*/

	public static String getBrowserName(String ag) {
		String browser = "other";
		ag = ag.toLowerCase();
		if (ag.contains("msie"))
			browser = "IE";
		else if (ag.contains("opera"))
			browser = "Opera";
		else if (ag.contains("chrome"))
			browser = "Chrome";
		else if (ag.contains("firefox"))
			browser = "Firefox";
		else if (ag.contains("safari") && ag.contains("version"))
			browser = "Safari";

		return browser;
	}

}
