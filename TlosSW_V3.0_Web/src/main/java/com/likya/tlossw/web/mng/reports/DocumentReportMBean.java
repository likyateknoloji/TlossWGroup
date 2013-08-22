package com.likya.tlossw.web.mng.reports;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.likya.tlossw.web.TlosSWBaseBean;

@ManagedBean(name = "documentReportMBean")
@ViewScoped
public class DocumentReportMBean extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = -4980088925798400965L;

	public byte[] getHtmlContent() throws IOException {

		String tlosDataXslDoc = null;
		String tlosData = null;
		String tlosCalendar = null;
		String tlosUser = null;

		tlosDataXslDoc = getDbOperations().getDbDoc("hs:tlosDataXsl");
		tlosData = getDbOperations().getDbDoc("hs:tlosData");
		tlosCalendar = getDbOperations().getDbDoc("hs:tlosCalendar");
		tlosUser = getDbOperations().getDbDoc("hs:tlosUser");

		StringReader xslReader = new StringReader(tlosDataXslDoc);
		StringReader tDataReader = new StringReader(tlosData);

		File tlosCalendarFile = new File("tlosSWCalendar10.xml");
		FileOutputStream calendarFos = new FileOutputStream(tlosCalendarFile);

		OutputStreamWriter calendarOsw = new OutputStreamWriter(calendarFos, "UTF8");
		BufferedWriter calendarBufferedWriter = new BufferedWriter(calendarOsw);

		calendarBufferedWriter.write(tlosCalendar);
		calendarBufferedWriter.close();

		File tlosUserFile = new File("tlosSWUser10.xml");
		FileOutputStream userFos = new FileOutputStream(tlosUserFile);

		OutputStreamWriter userOsw = new OutputStreamWriter(userFos, "UTF8");
		BufferedWriter userBufferedWriter = new BufferedWriter(userOsw);

		userBufferedWriter.write(tlosUser);
		userBufferedWriter.close();

		TransformerFactory tFactory = TransformerFactory.newInstance();

		StreamSource streamSource = new StreamSource(xslReader);

		Transformer transformer = null;
		try {
			transformer = tFactory.newTransformer(streamSource);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}

		StreamSource xmlDoc = new StreamSource(tDataReader);

		StringWriter stringWriter = new StringWriter();
		StreamResult streamResult = new StreamResult(stringWriter);

		try {
			transformer.transform(xmlDoc, streamResult);
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		return stringWriter.toString().getBytes("utf-8");
	}

	public byte[] openHtmlAction(ActionEvent e) {
		/*
		 * JavascriptContext.addJavascriptCall(FacesContext.getCurrentInstance(), "window.open('viewFile?file=xsl.html', 'myWindow');"); return "sdfsdf";
		 */
		try {
			return getHtmlContent();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return null;
	}

}
