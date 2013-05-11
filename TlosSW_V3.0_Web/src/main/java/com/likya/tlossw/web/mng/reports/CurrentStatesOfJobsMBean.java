package com.likya.tlossw.web.mng.reports;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.MeterGaugeChartModel;
import org.primefaces.model.chart.PieChartModel;
import org.xmldb.api.base.XMLDBException;

import com.likya.tlos.model.xmlbeans.report.JobDocument.Job;
import com.likya.tlos.model.xmlbeans.report.ReportDocument.Report;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.db.DBOperations;

@ManagedBean(name = "currentStatesOfJobsMBean")
@ViewScoped 
public class CurrentStatesOfJobsMBean extends TlosSWBaseBean implements Serializable {
	
	@ManagedProperty(value = "#{dbOperations}")
	private DBOperations dbOperations;
	
	private static final long serialVersionUID = 2570957528954820036L;
	private static final Logger logger = Logger.getLogger(AlarmReportPanelMBean.class);
	private PieChartModel pieDashboardModel;
 
	private DashboardModel model;
 

	private int derinlik;
	private Report reportBaseList;
 
	
	private int pieRunningCount;
	private int pieFailedCount;
	private int pieReadyCount;
	private int pieWaitingCount;
	private int pieSuccessCount;
	private int pieLook4RCount;
 
	private String pieColorList;
 
 
	@PostConstruct
	public void init() {

		logger.info("begin : init");

		FacesContext facesContext = FacesContext.getCurrentInstance();
		String parameter_value = (String) facesContext.getExternalContext().getRequestParameterMap().get("id");

		System.out.println(parameter_value);
		
		model = new DefaultDashboardModel();
		DashboardColumn column1 = new DefaultDashboardColumn();
		DashboardColumn column2 = new DefaultDashboardColumn();
		DashboardColumn column3 = new DefaultDashboardColumn();
 

		
	 
		column1.addWidget("running");
		column2.addWidget("failed");
		column1.addWidget("ready");
		column2.addWidget("waiting");
		column1.addWidget("success");
		column2.addWidget("look4resource");
		
		model.addColumn(column1);
		model.addColumn(column2);
		model.addColumn(column3);
 

		column3.addWidget("statePanel");

		
		createPieModel();
 
 

		logger.info("end : init");

	}

	public void handleReorder(DashboardReorderEvent event) {
		FacesMessage message = new FacesMessage();
		message.setSeverity(FacesMessage.SEVERITY_INFO);
		message.setSummary("Reordered: " + event.getWidgetId());
		message.setDetail("Item index: " + event.getItemIndex() + ", Column index: " + event.getColumnIndex() + ", Sender index: " + event.getSenderColumnIndex());

	}
	
 
 
	public void refreshDashboardChart() {

		createPieModel();
	}
	
 
  

	private void createPieModel() {
		
		derinlik = 1;
		pieColorList="";
		
		pieRunningCount=0;
		pieFailedCount=0;
		pieReadyCount=0;
		pieWaitingCount=0;
		
		try {
			reportBaseList = getDbOperations().getDashboardReport(derinlik);
		} catch (XMLDBException e) {
			e.printStackTrace();
		}
	 
		pieDashboardModel = new PieChartModel();
 
		setPieRunningCount(reportBaseList.getRUNNING().getONRESOURCE().getTIMEIN().intValue());
		setPieFailedCount(reportBaseList.getFINISHED().getCOMPLETED().getFAILED().intValue());
		setPieReadyCount(reportBaseList.getPENDING().getREADY().getWAITING().intValue());
		setPieWaitingCount(reportBaseList.getPENDING().getIDLED().getBYTIME().intValue());
		setPieSuccessCount(reportBaseList.getFINISHED().getCOMPLETED().getSUCCESS().intValue());
		setPieLook4RCount(reportBaseList.getPENDING().getREADY().getLOOKFORRESOURCE().intValue());
		
		int i=0;
		
		if ((reportBaseList.getRUNNING().getONRESOURCE().getTIMEIN() != null) && (reportBaseList.getRUNNING().getONRESOURCE().getTIMEIN().compareTo(BigInteger.valueOf(0))) != 0) {
			pieDashboardModel.set(resolveMessage("tlos.reports.chart.Running"), reportBaseList.getRUNNING().getONRESOURCE().getTIMEIN().doubleValue());
			pieColorList=pieColorList+"4962EE"; 
			i++;
		}
		
		if ((reportBaseList.getPENDING().getIDLED().getBYTIME() != null) && (reportBaseList.getPENDING().getIDLED().getBYTIME().compareTo(BigInteger.valueOf(0))) != 0) {
			pieDashboardModel.set(resolveMessage("tlos.reports.chart.Waiting"), reportBaseList.getPENDING().getIDLED().getBYTIME().doubleValue());
			if (i>0) pieColorList=pieColorList+", FFBF00";
			else pieColorList=pieColorList+"FFBF00";
			i++;
		}
		
		if ((reportBaseList.getPENDING().getREADY().getWAITING() != null) && (reportBaseList.getPENDING().getREADY().getWAITING().compareTo(BigInteger.valueOf(0))) != 0) {
			pieDashboardModel.set(resolveMessage("tlos.reports.chart.Ready"), reportBaseList.getPENDING().getREADY().getWAITING().doubleValue());
			if (i>0) pieColorList=pieColorList+", DAC9D7";
			else pieColorList=pieColorList+"DAC9D7";
			i++;
		}
		
		if ((reportBaseList.getPENDING().getREADY().getLOOKFORRESOURCE() != null) && (reportBaseList.getPENDING().getREADY().getLOOKFORRESOURCE().compareTo(BigInteger.valueOf(0))) != 0) {
			pieDashboardModel.set(resolveMessage("tlos.reports.chart.Look"), reportBaseList.getPENDING().getREADY().getLOOKFORRESOURCE().doubleValue());
			if (i>0) pieColorList=pieColorList+", F65FE2";
			else pieColorList=pieColorList+"F65FE2";
			i++;
		}
		if ((reportBaseList.getPENDING().getREADY().getUSERCHOOSERESOURCE() != null) && (reportBaseList.getPENDING().getREADY().getUSERCHOOSERESOURCE().compareTo(BigInteger.valueOf(0))) != 0) {
			pieDashboardModel.set(resolveMessage("tlos.reports.chart.LookUR"), reportBaseList.getPENDING().getREADY().getUSERCHOOSERESOURCE().doubleValue());
		}
		
		if ((reportBaseList.getPENDING().getREADY().getUSERWAITING() != null) && (reportBaseList.getPENDING().getREADY().getUSERWAITING().compareTo(BigInteger.valueOf(0))) != 0) {
			pieDashboardModel.set(resolveMessage("tlos.reports.chart.LookUW"), reportBaseList.getPENDING().getREADY().getUSERWAITING().doubleValue());
		}
		if ((reportBaseList.getFINISHED().getCOMPLETED().getSUCCESS() != null) && (reportBaseList.getFINISHED().getCOMPLETED().getSUCCESS().compareTo(BigInteger.valueOf(0))) != 0) {
			pieDashboardModel.set(resolveMessage("tlos.reports.chart.Success"), reportBaseList.getFINISHED().getCOMPLETED().getSUCCESS().doubleValue());
			if (i>0) pieColorList=pieColorList+", 31B404";
			else pieColorList=pieColorList+"31B404";
			i++;
		}
		if ((reportBaseList.getFINISHED().getCOMPLETED().getFAILED() != null) && (reportBaseList.getFINISHED().getCOMPLETED().getFAILED().compareTo(BigInteger.valueOf(0))) != 0) {
			pieDashboardModel.set(resolveMessage("tlos.reports.chart.Failed"), reportBaseList.getFINISHED().getCOMPLETED().getFAILED().doubleValue());
			if (i>0) pieColorList=pieColorList+", FA1B0B";
			else pieColorList=pieColorList+"FA1B0B";
			i++;
		}
		if ((reportBaseList.getCANCELLED() != null) && (reportBaseList.getCANCELLED().compareTo(BigInteger.valueOf(0))) != 0) {
			pieDashboardModel.set(resolveMessage("tlos.reports.chart.Cancelled"), reportBaseList.getCANCELLED().doubleValue());
		}
		if ((reportBaseList.getRUNNING().getONRESOURCE().getTIMEOUT() != null) && (reportBaseList.getRUNNING().getONRESOURCE().getTIMEOUT().compareTo(BigInteger.valueOf(0))) != 0) {
			pieDashboardModel.set(resolveMessage("tlos.reports.chart.TimeOut"), reportBaseList.getRUNNING().getONRESOURCE().getTIMEOUT().doubleValue());
		}
		
//		pieDashboardModel.set("Failed", 5);
//		pieDashboardModel.set("Ready", 62);
//		pieDashboardModel.set("Pending", 30);
	
	}
	 

	public PieChartModel getPieDashboardModel() {
		return pieDashboardModel;
	}

	public DashboardModel getModel() {
		return model;
	}

	public void setModel(DashboardModel model) {
		this.model = model;
	}
 
 
	public DBOperations getDbOperations() {
		return dbOperations;
	}

	public void setDbOperations(DBOperations dbOperations) {
		this.dbOperations = dbOperations;
	}

 

	public int getDerinlik() {
		return derinlik;
	}

	public void setDerinlik(int derinlik) {
		this.derinlik = derinlik;
	}

	public Report getReportBaseList() {
		return reportBaseList;
	}

	public void setReportBaseList(Report reportBaseList) {
		this.reportBaseList = reportBaseList;
	}

	public int getPieRunningCount() {
		return pieRunningCount;
	}

	public void setPieRunningCount(int pieRunningCount) {
		this.pieRunningCount = pieRunningCount;
	}

	public int getPieFailedCount() {
		return pieFailedCount;
	}

	public void setPieFailedCount(int pieFailedCount) {
		this.pieFailedCount = pieFailedCount;
	}

	public int getPieReadyCount() {
		return pieReadyCount;
	}

	public void setPieReadyCount(int pieReadyCount) {
		this.pieReadyCount = pieReadyCount;
	}

	public int getPieWaitingCount() {
		return pieWaitingCount;
	}

	public void setPieWaitingCount(int pieWaitingCount) {
		this.pieWaitingCount = pieWaitingCount;
	}

	public int getPieSuccessCount() {
		return pieSuccessCount;
	}

	public void setPieSuccessCount(int pieSuccessCount) {
		this.pieSuccessCount = pieSuccessCount;
	}

	public int getPieLook4RCount() {
		return pieLook4RCount;
	}

	public void setPieLook4RCount(int pieLook4RCount) {
		this.pieLook4RCount = pieLook4RCount;
	}

	public String getPieColorList() {
		return pieColorList;
	}

	public void setPieColorList(String pieColorList) {
		this.pieColorList = pieColorList;
	}
 

}