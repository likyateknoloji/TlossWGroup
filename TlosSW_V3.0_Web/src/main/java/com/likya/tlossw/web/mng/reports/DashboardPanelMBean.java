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

@ManagedBean(name = "dashboardPanelMBean")
@ViewScoped 
public class DashboardPanelMBean extends TlosSWBaseBean implements Serializable {
	
	@ManagedProperty(value = "#{dbOperations}")
	private DBOperations dbOperations;
	
	private static final long serialVersionUID = 2570957528954820036L;
	private static final Logger logger = Logger.getLogger(AlarmReportPanelMBean.class);
	private PieChartModel pieDashboardModel;
	private PieChartModel pieDashboardModel7;
	private DashboardModel model;
	private CartesianChartModel curDurationModel;
	private CartesianChartModel prevDurationModel;
	private CartesianChartModel denseModel;

	private int derinlik;
	private Report reportBaseList;
	private Report reportBaseList7;
	
	private int pieRunningCount;
	private int pieFailedCount;
	private int pieReadyCount;
	private int pieWaitingCount;
	private int pieSuccessCount;
	private int pieLook4RCount;
	
	private int pieRunningCount7;
	private int pieFailedCount7;
	private int pieReadyCount7;
	private int pieWaitingCount7;
	private int pieSuccessCount7;
	private int pieLook4RCount7;
	
	private String pieColorList;
	
	private ArrayList<Job> jobsArray;
	
//	private JobArray jobReturnArray;
	
	private MeterGaugeChartModel meterGaugeModel;

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
		DashboardColumn column4 = new DefaultDashboardColumn();

		
		column1.addWidget("top10");
		column1.addWidget("gauge");
		column2.addWidget("topPrev10");
		column2.addWidget("density");
		column3.addWidget("status7");
		column1.addWidget("info");
		column2.addWidget("info2");
		column1.addWidget("info3");
		column2.addWidget("info4");
		column1.addWidget("info5");
		column2.addWidget("info6");
		
		model.addColumn(column1);
		model.addColumn(column2);
		model.addColumn(column3);
		model.addColumn(column4);

		column3.addWidget("statePanel");

		
		createPieModel();
		createPieModel7();

		
		
//		createCurCategoryModel();
//		createPrevCategoryModel();
		createDenseModel();
		createMeterGaugeModel();

		logger.info("end : init");

	}

	public void handleReorder(DashboardReorderEvent event) {
		FacesMessage message = new FacesMessage();
		message.setSeverity(FacesMessage.SEVERITY_INFO);
		message.setSummary("Reordered: " + event.getWidgetId());
		message.setDetail("Item index: " + event.getItemIndex() + ", Column index: " + event.getColumnIndex() + ", Sender index: " + event.getSenderColumnIndex());

	}
	
	public void resetJobReportAction() {
 
		jobsArray = new ArrayList<Job>();
		 
	}
	
	public String getChartSeriesColors() {
        return "red, blue, 0x18e0b1, 0xd5e018";
    }
	
	public String getDatatipFormat(){
		   return "<span style=\"display:none;\">%s</span><span>%s</span>";
		}
	
	public void refreshDashboardChart() {

		createPieModel();
	}
	
	public void refreshDashboardChart7() {

		createPieModel7();
	}

//	public void refreshCurDurationChart() {
//
//		createCurCategoryModel();
//	}
//
//	public void refreshPrevDurationChart() {
//
//		createPrevCategoryModel();
//	}

	public void refresDenseChart() {
		createDenseModel();

	}

	private void createMeterGaugeModel() {

		List<Number> intervals = new ArrayList<Number>() {
			{
				add(2);
				add(4);
				add(6);
				add(12);
			}
		};

		meterGaugeModel = new MeterGaugeChartModel(6.5, intervals);
	}

	private void createDenseModel() {
		denseModel = new CartesianChartModel();

		ChartSeries dense = new ChartSeries();
		dense.setLabel("Number of Jobs");

		dense.set("00:00", 3);
		dense.set("00:30", 5);
		dense.set("01:30", 10);
		dense.set("01:30", 10);
		dense.set("02:00", 12);
		dense.set("02:30", 13);
		dense.set("03:00", 11);
		dense.set("03:30", 9);
		dense.set("04:00", 9);
		dense.set("04:30", 10);
		dense.set("05:00", 6);
		dense.set("05:30", 4);
		dense.set("06:00", 1);
		dense.set("06:30", 2);
		dense.set("07:00", 3);
		dense.set("07:30", 2);
		dense.set("08:00", 1);

		denseModel.addSeries(dense);

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
		setPieReadyCount(reportBaseList.getPENDING().getIDLED().getBYTIME().intValue());
		setPieWaitingCount(reportBaseList.getPENDING().getREADY().getWAITING().intValue());
		setPieSuccessCount(reportBaseList.getFINISHED().getCOMPLETED().getSUCCESS().intValue());
		setPieLook4RCount(reportBaseList.getPENDING().getREADY().getLOOKFORRESOURCE().intValue());
		
		int i=0;
		
		if ((reportBaseList.getRUNNING().getONRESOURCE().getTIMEIN() != null) && (reportBaseList.getRUNNING().getONRESOURCE().getTIMEIN().compareTo(BigInteger.valueOf(0))) != 0) {
			pieDashboardModel.set(resolveMessage("tlos.reports.chart.Running"), reportBaseList.getRUNNING().getONRESOURCE().getTIMEIN().doubleValue());
			pieColorList=pieColorList+"4962EE"; 
			i++;
		}
		
		if ((reportBaseList.getPENDING().getREADY().getWAITING() != null) && (reportBaseList.getPENDING().getREADY().getWAITING().compareTo(BigInteger.valueOf(0))) != 0) {
			pieDashboardModel.set(resolveMessage("tlos.reports.chart.Waiting"), reportBaseList.getPENDING().getREADY().getWAITING().doubleValue());
			if (i>0) pieColorList=pieColorList+", FFBF00";
			else pieColorList=pieColorList+"FFBF00";
			i++;
		}
		
		if ((reportBaseList.getPENDING().getIDLED().getBYTIME() != null) && (reportBaseList.getPENDING().getIDLED().getBYTIME().compareTo(BigInteger.valueOf(0))) != 0) {
			pieDashboardModel.set(resolveMessage("tlos.reports.chart.Ready"), reportBaseList.getPENDING().getIDLED().getBYTIME().doubleValue());
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
	
	private void createPieModel7() {
//		pieDashboardModel7 = new PieChartModel();
//
//		pieDashboardModel7.set("Success", 540);
//		pieDashboardModel7.set("Failed", 325);
//		pieDashboardModel7.set("Ready", 702);
//		pieDashboardModel7.set("Pending", 421);
		
		derinlik = 7;
		pieColorList="";
		
		pieRunningCount7=0;
		pieFailedCount7=0;
		pieReadyCount7=0;
		pieWaitingCount7=0;
		
		try {
			reportBaseList7 = getDbOperations().getDashboardReport(derinlik);
		} catch (XMLDBException e) {
			e.printStackTrace();
		}
	 
		pieDashboardModel7 = new PieChartModel();
 
		setPieRunningCount7(reportBaseList7.getRUNNING().getONRESOURCE().getTIMEIN().intValue());
		setPieFailedCount7(reportBaseList7.getFINISHED().getCOMPLETED().getFAILED().intValue());
		setPieReadyCount7(reportBaseList7.getPENDING().getIDLED().getBYTIME().intValue());
		setPieWaitingCount7(reportBaseList7.getPENDING().getREADY().getWAITING().intValue());
		setPieSuccessCount7(reportBaseList7.getFINISHED().getCOMPLETED().getSUCCESS().intValue());
		setPieLook4RCount7(reportBaseList7.getPENDING().getREADY().getLOOKFORRESOURCE().intValue());
		
		int i=0;
		
		if ((reportBaseList7.getRUNNING().getONRESOURCE().getTIMEIN() != null) && (reportBaseList7.getRUNNING().getONRESOURCE().getTIMEIN().compareTo(BigInteger.valueOf(0))) != 0) {
			pieDashboardModel7.set(resolveMessage("tlos.reports.chart.Running"), reportBaseList7.getRUNNING().getONRESOURCE().getTIMEIN().doubleValue());
			pieColorList=pieColorList+"4962EE"; 
			i++;
		}
		
		if ((reportBaseList7.getPENDING().getREADY().getWAITING() != null) && (reportBaseList7.getPENDING().getREADY().getWAITING().compareTo(BigInteger.valueOf(0))) != 0) {
			pieDashboardModel7.set(resolveMessage("tlos.reports.chart.Waiting"), reportBaseList7.getPENDING().getREADY().getWAITING().doubleValue());
			if (i>0) pieColorList=pieColorList+", FFBF00";
			else pieColorList=pieColorList+"FFBF00";
			i++;
		}
		
		if ((reportBaseList7.getPENDING().getIDLED().getBYTIME() != null) && (reportBaseList7.getPENDING().getIDLED().getBYTIME().compareTo(BigInteger.valueOf(0))) != 0) {
			pieDashboardModel7.set(resolveMessage("tlos.reports.chart.Ready"), reportBaseList7.getPENDING().getIDLED().getBYTIME().doubleValue());
			if (i>0) pieColorList=pieColorList+", DAC9D7";
			else pieColorList=pieColorList+"DAC9D7";
			i++;
		}
		
		if ((reportBaseList7.getPENDING().getREADY().getLOOKFORRESOURCE() != null) && (reportBaseList7.getPENDING().getREADY().getLOOKFORRESOURCE().compareTo(BigInteger.valueOf(0))) != 0) {
			pieDashboardModel7.set(resolveMessage("tlos.reports.chart.Look"), reportBaseList7.getPENDING().getREADY().getLOOKFORRESOURCE().doubleValue());
			if (i>0) pieColorList=pieColorList+", F65FE2";
			else pieColorList=pieColorList+"F65FE2";
			i++;
		}
		if ((reportBaseList7.getPENDING().getREADY().getUSERCHOOSERESOURCE() != null) && (reportBaseList7.getPENDING().getREADY().getUSERCHOOSERESOURCE().compareTo(BigInteger.valueOf(0))) != 0) {
			pieDashboardModel7.set(resolveMessage("tlos.reports.chart.LookUR"), reportBaseList7.getPENDING().getREADY().getUSERCHOOSERESOURCE().doubleValue());
		}
		
		if ((reportBaseList7.getPENDING().getREADY().getUSERWAITING() != null) && (reportBaseList7.getPENDING().getREADY().getUSERWAITING().compareTo(BigInteger.valueOf(0))) != 0) {
			pieDashboardModel7.set(resolveMessage("tlos.reports.chart.LookUW"), reportBaseList7.getPENDING().getREADY().getUSERWAITING().doubleValue());
		}
		if ((reportBaseList7.getFINISHED().getCOMPLETED().getSUCCESS() != null) && (reportBaseList7.getFINISHED().getCOMPLETED().getSUCCESS().compareTo(BigInteger.valueOf(0))) != 0) {
			pieDashboardModel7.set(resolveMessage("tlos.reports.chart.Success"), reportBaseList7.getFINISHED().getCOMPLETED().getSUCCESS().doubleValue());
			if (i>0) pieColorList=pieColorList+", 31B404";
			else pieColorList=pieColorList+"31B404";
			i++;
		}
		if ((reportBaseList7.getFINISHED().getCOMPLETED().getFAILED() != null) && (reportBaseList7.getFINISHED().getCOMPLETED().getFAILED().compareTo(BigInteger.valueOf(0))) != 0) {
			pieDashboardModel7.set(resolveMessage("tlos.reports.chart.Failed"), reportBaseList7.getFINISHED().getCOMPLETED().getFAILED().doubleValue());
			if (i>0) pieColorList=pieColorList+", FA1B0B";
			else pieColorList=pieColorList+"FA1B0B";
			i++;
		}
		if ((reportBaseList7.getCANCELLED() != null) && (reportBaseList7.getCANCELLED().compareTo(BigInteger.valueOf(0))) != 0) {
			pieDashboardModel7.set(resolveMessage("tlos.reports.chart.Cancelled"), reportBaseList7.getCANCELLED().doubleValue());
		}
		if ((reportBaseList7.getRUNNING().getONRESOURCE().getTIMEOUT() != null) && (reportBaseList7.getRUNNING().getONRESOURCE().getTIMEOUT().compareTo(BigInteger.valueOf(0))) != 0) {
			pieDashboardModel7.set(resolveMessage("tlos.reports.chart.TimeOut"), reportBaseList7.getRUNNING().getONRESOURCE().getTIMEOUT().doubleValue());
		}
		
	}

//	private void createCurCategoryModel() {
//
//		curDurationModel = new CartesianChartModel();
//
//		resetJobReportAction();
//		
//		try {
//			jobsArray = getDbOperations().getJobArrayReport(1, 0, "descending", 10);
//		} catch (XMLDBException e) {
//			e.printStackTrace();
//		}
//
//		ChartSeries jobs = new ChartSeries();
//		jobs.setLabel("Jobs");
//
//		for (Job job : jobsArray) {
//			jobs.set(job.getJname(), job.getBigDecimalValue().divide(new BigDecimal("60"), 0));
//		}
//		
//		curDurationModel.addSeries(jobs);
//
//	}
//
//	private void createPrevCategoryModel() {
//		prevDurationModel = new CartesianChartModel();
//
//	    resetJobReportAction();
//		
//		try {
//			jobsArray = getDbOperations().getJobArrayReport(1, -1, "descending", 10);
//		} catch (XMLDBException e) {
//			e.printStackTrace();
//		}
//		
//		ChartSeries prevJobs = new ChartSeries();
//		prevJobs.setLabel("Jobs");
//
//		for (Job job : jobsArray) {
//			prevJobs.set(job.getJname(), job.getBigDecimalValue().divide(new BigDecimal("60"), 0));
//		}
//		
//		prevDurationModel.addSeries(prevJobs);
//
//	}

	public PieChartModel getPieDashboardModel() {
		return pieDashboardModel;
	}

	public DashboardModel getModel() {
		return model;
	}

	public void setModel(DashboardModel model) {
		this.model = model;
	}

	public CartesianChartModel getDenseModel() {
		return denseModel;
	}

	public void setDenseModel(CartesianChartModel denseModel) {
		this.denseModel = denseModel;
	}

	public CartesianChartModel getPrevDurationModel() {
		return prevDurationModel;
	}

	public void setPrevDurationModel(CartesianChartModel prevDurationModel) {
		this.prevDurationModel = prevDurationModel;
	}

	public CartesianChartModel getCurDurationModel() {
		return curDurationModel;
	}

	public void setCurDurationModel(CartesianChartModel curDurationModel) {
		this.curDurationModel = curDurationModel;
	}

	public MeterGaugeChartModel getMeterGaugeModel() {
		return meterGaugeModel;
	}

	public void setMeterGaugeModel(MeterGaugeChartModel meterGaugeModel) {
		this.meterGaugeModel = meterGaugeModel;
	}

	public PieChartModel getPieDashboardModel7() {
		return pieDashboardModel7;
	}

	public void setPieDashboardModel7(PieChartModel pieDashboardModel7) {
		this.pieDashboardModel7 = pieDashboardModel7;
	}

	public DBOperations getDbOperations() {
		return dbOperations;
	}

	public void setDbOperations(DBOperations dbOperations) {
		this.dbOperations = dbOperations;
	}

	public ArrayList<Job> getJobsArray() {
		return jobsArray;
	}

	public void setJobsArray(ArrayList<Job> jobsArray) {
		this.jobsArray = jobsArray;
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

	public int getPieLook4RCount7() {
		return pieLook4RCount7;
	}

	public void setPieLook4RCount7(int pieLook4RCount7) {
		this.pieLook4RCount7 = pieLook4RCount7;
	}

	public int getPieSuccessCount7() {
		return pieSuccessCount7;
	}

	public void setPieSuccessCount7(int pieSuccessCount7) {
		this.pieSuccessCount7 = pieSuccessCount7;
	}

	public int getPieWaitingCount7() {
		return pieWaitingCount7;
	}

	public void setPieWaitingCount7(int pieWaitingCount7) {
		this.pieWaitingCount7 = pieWaitingCount7;
	}

	public int getPieReadyCount7() {
		return pieReadyCount7;
	}

	public void setPieReadyCount7(int pieReadyCount7) {
		this.pieReadyCount7 = pieReadyCount7;
	}

	public int getPieFailedCount7() {
		return pieFailedCount7;
	}

	public void setPieFailedCount7(int pieFailedCount7) {
		this.pieFailedCount7 = pieFailedCount7;
	}

	public int getPieRunningCount7() {
		return pieRunningCount7;
	}

	public void setPieRunningCount7(int pieRunningCount7) {
		this.pieRunningCount7 = pieRunningCount7;
	}

	public Report getReportBaseList7() {
		return reportBaseList7;
	}

	public void setReportBaseList7(Report reportBaseList7) {
		this.reportBaseList7 = reportBaseList7;
	}

 

}