package com.likya.tlossw.web.appmng;

import java.io.IOException;
import java.io.OutputStream;

import javax.faces.bean.ManagedProperty;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.likya.tlossw.model.jmx.JmxUser;
import com.likya.tlossw.web.mng.reports.DocumentReportMBean;
import com.likya.tlossw.webclient.TEJmxMpDBClient;

public class ViewFileServlet extends HttpServlet {

	private static final long serialVersionUID = 2212000510316898588L;

	@SuppressWarnings("unused")
	private ServletConfig config;

	@ManagedProperty(value = "#{documentReportMBean}")
	private DocumentReportMBean documentReportMBean;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		this.config = config;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String fileName = request.getParameter("file");
		byte[] xslDoc = null;

		if (fileName.equals("xsl.pdf")) {
			xslDoc = TEJmxMpDBClient.getPdfDoc((JmxUser) getServletContext().getAttribute("JmxUser"), "xsl.pdf");
			response.setContentType("application/pdf");

		} else if (fileName.equals("xsl.html")) {
			xslDoc = getDocumentReportMBean().getHtmlContent();
			response.setContentType("text/html");
		}

		OutputStream outputStream = null;
		outputStream = response.getOutputStream();

		if (xslDoc != null) {
			outputStream.write(xslDoc);
		}
		outputStream.flush();
		outputStream.close();
	}

	public DocumentReportMBean getDocumentReportMBean() {
		return documentReportMBean;
	}

	public void setDocumentReportMBean(DocumentReportMBean documentReportMBean) {
		this.documentReportMBean = documentReportMBean;
	}

}
