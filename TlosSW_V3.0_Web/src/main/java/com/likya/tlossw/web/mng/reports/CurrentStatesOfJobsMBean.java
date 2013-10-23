package com.likya.tlossw.web.mng.reports;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;
import org.primefaces.model.chart.PieChartModel;
import org.xmldb.api.base.XMLDBException;

import com.likya.tlos.model.xmlbeans.report.ReportDocument.Report;
import com.likya.tlossw.web.db.DBOperations;
import com.likya.tlossw.web.mng.reports.helpers.ReportsParameters;
import com.likya.tlossw.web.utils.ConstantDefinitions;

@ManagedBean(name = "currentStatesOfJobsMBean")
@ViewScoped
public class CurrentStatesOfJobsMBean extends ReportBase implements Serializable {

	private static final long serialVersionUID = -7887788251298280405L;

	@ManagedProperty(value = "#{dbOperations}")
	private DBOperations dbOperations;

	private static final Logger logger = Logger.getLogger(CurrentStatesOfJobsMBean.class);
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
	private int pieUserChooseResourceCount;
	private int pieUserWaitingCount;
	private int pieCancelledCount;
	private int pieTimeoutCount;

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
		column1.addWidget("userChooseResource");
		column2.addWidget("userWaiting");
		column1.addWidget("cancelled");
		column2.addWidget("timeout");
		
		model.addColumn(column1);
		model.addColumn(column2);
		model.addColumn(column3);

		column3.addWidget("statePanel");

		if (getReportParameters() == null) {
			setReportParameters(new ReportsParameters());
			
		}
		
		createPieModel();

		logger.info("end : init");

		setActiveReportPanel(ConstantDefinitions.JOB_STATE_REPORT);
	}

	public void handleReorder(DashboardReorderEvent event) {
		FacesMessage message = new FacesMessage();
		message.setSeverity(FacesMessage.SEVERITY_INFO);
		message.setSummary("Reordered: " + event.getWidgetId());
		message.setDetail("Item index: " + event.getItemIndex()
				+ ", Column index: " + event.getColumnIndex()
				+ ", Sender index: " + event.getSenderColumnIndex());
	}

	public void refreshDashboardChart() {

		createPieModel();
	}

	private void createPieModel() {
	
		getReportParameters().fillReportParameters();
		
		pieColorList = "";

		pieRunningCount = 0;
		pieFailedCount = 0;
		pieReadyCount = 0;
		pieWaitingCount = 0;
		pieSuccessCount = 0;
		pieLook4RCount = 0;
		pieUserChooseResourceCount = 0;
		pieUserWaitingCount = 0;
		pieCancelledCount = 0;
		pieTimeoutCount = 0;
		
		try {
			reportBaseList = getDbOperations().getDashboardReport( getReportParameters().getReportParametersXML() );
		} catch (XMLDBException e) {
			e.printStackTrace();
		}

		pieDashboardModel = new PieChartModel();

		int running = 0,
		    failed = 0,
		    waiting = 0,
		    ready = 0,
		    success = 0,
		    lookForResource = 0,
		    userChooseResource = 0,
		    userWaiting = 0,
		    cancelled = 0,
		    timeout = 0;
		
		if (reportBaseList != null) {
			
			running = reportBaseList.getRUNNING().getONRESOURCE().getTIMEIN().intValue() + reportBaseList.getRUNNING().getSTAGEIN().intValue() + reportBaseList.getRUNNING().getSTAGEOUT().intValue();
			failed = reportBaseList.getFINISHED().getCOMPLETED().getFAILED().intValue();
			waiting = reportBaseList.getPENDING().getIDLED().getBYTIME().intValue();
			ready = reportBaseList.getPENDING().getREADY().getWAITING().intValue();
			success = reportBaseList.getFINISHED().getCOMPLETED().getSUCCESS().intValue();
			lookForResource = reportBaseList.getPENDING().getREADY().getLOOKFORRESOURCE().intValue();
			userChooseResource = reportBaseList.getPENDING().getREADY().getUSERCHOOSERESOURCE().intValue();
			userWaiting = reportBaseList.getPENDING().getREADY().getUSERWAITING().intValue();
			cancelled = reportBaseList.getCANCELLED().intValue();
			timeout = reportBaseList.getRUNNING().getONRESOURCE().getTIMEOUT().intValue();
			
			setPieRunningCount( running );
			setPieFailedCount( failed );
			setPieReadyCount( ready );
			setPieWaitingCount( waiting );
			setPieSuccessCount( success );
			setPieLook4RCount( lookForResource );
			setPieUserChooseResourceCount( userChooseResource );
			setPieUserWaitingCount( userWaiting );
			setPieCancelledCount( cancelled );
			setPieTimeoutCount( timeout );
			
			int i = 0;

			if (
				(reportBaseList.getRUNNING().getONRESOURCE().getTIMEIN() != null)
				  && (running > 0 )
			   ) {
				pieDashboardModel.set(
						resolveMessage("tlos.reports.chart.Running"),
						reportBaseList.getRUNNING().getONRESOURCE().getTIMEIN().doubleValue());
				pieColorList = pieColorList + "4962EE";
				i++;
			}

			if ((reportBaseList.getPENDING().getIDLED().getBYTIME() != null)
					&& (waiting > 0)) {
				pieDashboardModel.set(
						resolveMessage("tlos.reports.chart.Waiting"),
						reportBaseList.getPENDING().getIDLED().getBYTIME().doubleValue());
				if (i > 0)
					pieColorList = pieColorList + ", FFBF00";
				else
					pieColorList = pieColorList + "FFBF00";
				i++;
			}

			if ((reportBaseList.getPENDING().getREADY().getWAITING() != null)
					&& (ready > 0)) {
				pieDashboardModel.set(
						resolveMessage("tlos.reports.chart.Ready"),
						reportBaseList.getPENDING().getREADY().getWAITING().doubleValue());
				if (i > 0)
					pieColorList = pieColorList + ", DAC9D7";
				else
					pieColorList = pieColorList + "DAC9D7";
				i++;
			}

			if ((reportBaseList.getPENDING().getREADY().getLOOKFORRESOURCE() != null)
					&& (lookForResource > 0 )) {
				pieDashboardModel.set(
						resolveMessage("tlos.reports.chart.Look"),
						reportBaseList.getPENDING().getREADY().getLOOKFORRESOURCE().doubleValue());
				if (i > 0)
					pieColorList = pieColorList + ", F65FE2";
				else
					pieColorList = pieColorList + "F65FE2";
				i++;
			}
			if ((reportBaseList.getPENDING().getREADY().getUSERCHOOSERESOURCE() != null)
					&& (userChooseResource > 0)) {
				pieDashboardModel.set(
						resolveMessage("tlos.reports.chart.LookUR"),
						reportBaseList.getPENDING().getREADY().getUSERCHOOSERESOURCE().doubleValue());
				if (i > 0)
					pieColorList = pieColorList + ", F6C660";
				else
					pieColorList = pieColorList + "F6C660";
				i++;
			}

			if ((reportBaseList.getPENDING().getREADY().getUSERWAITING() != null)
					&& (userWaiting > 0)) {
				pieDashboardModel.set(
						resolveMessage("tlos.reports.chart.LookUW"),
						reportBaseList.getPENDING().getREADY().getUSERWAITING().doubleValue());
				if (i > 0)
					pieColorList = pieColorList + ",60E5F6";
				else
					pieColorList = pieColorList + "60E5F6";
				i++;
			}
			if ((reportBaseList.getFINISHED().getCOMPLETED().getSUCCESS() != null)
					&& (success > 0)) {
				pieDashboardModel.set(
						resolveMessage("tlos.reports.chart.Success"),
						reportBaseList.getFINISHED().getCOMPLETED().getSUCCESS().doubleValue());
				if (i > 0)
					pieColorList = pieColorList + ", 31B404";
				else
					pieColorList = pieColorList + "31B404";
				i++;
			}
			if ((reportBaseList.getFINISHED().getCOMPLETED().getFAILED() != null)
					&& (failed > 0)) {
				pieDashboardModel.set(
						resolveMessage("tlos.reports.chart.Failed"),
						reportBaseList.getFINISHED().getCOMPLETED().getFAILED().doubleValue());
				if (i > 0)
					pieColorList = pieColorList + ", FA1B0B";
				else
					pieColorList = pieColorList + "FA1B0B";
				i++;
			}
			if ((reportBaseList.getCANCELLED() != null)
					&& (cancelled > 0)) {
				pieDashboardModel.set(
						resolveMessage("tlos.reports.chart.Cancelled"),
						reportBaseList.getCANCELLED().doubleValue());
				if (i > 0)
					pieColorList = pieColorList + ", F6EA60";
				else
					pieColorList = pieColorList + "F6EA60";
				i++;
			}
			if ((reportBaseList.getRUNNING().getONRESOURCE().getTIMEOUT() != null)
					&& (timeout != 0)) {
				pieDashboardModel.set(
						resolveMessage("tlos.reports.chart.TimeOut"),
						reportBaseList.getRUNNING().getONRESOURCE().getTIMEOUT().doubleValue());
				if (i > 0)
					pieColorList = pieColorList + ", BE81F7";
				else
					pieColorList = pieColorList + "BE81F7";
				i++;
			}
		}
	}

	public void refreshReport(ActionEvent actionEvent) {
		createPieModel();
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

	public int getPieUserChooseResourceCount() {
		return pieUserChooseResourceCount;
	}

	public void setPieUserChooseResourceCount(int pieUserChooseResourceCount) {
		this.pieUserChooseResourceCount = pieUserChooseResourceCount;
	}

	public int getPieUserWaitingCount() {
		return pieUserWaitingCount;
	}

	public void setPieUserWaitingCount(int pieUserWaitingCount) {
		this.pieUserWaitingCount = pieUserWaitingCount;
	}

	public int getPieCancelledCount() {
		return pieCancelledCount;
	}

	public void setPieCancelledCount(int pieCancelledCount) {
		this.pieCancelledCount = pieCancelledCount;
	}

	public int getPieTimeoutCount() {
		return pieTimeoutCount;
	}

	public void setPieTimeoutCount(int pieTimeoutCount) {
		this.pieTimeoutCount = pieTimeoutCount;
	}
	
}