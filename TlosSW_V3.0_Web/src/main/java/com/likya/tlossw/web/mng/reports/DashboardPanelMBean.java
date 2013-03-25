package com.likya.tlossw.web.mng.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

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
import com.likya.tlossw.web.db.DBOperations;

@ManagedBean(name = "dashboardPanelMBean")
@ViewScoped
public class DashboardPanelMBean implements Serializable {
	
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

	private ArrayList<Job> jobsArray;
	
//	private JobArray jobReturnArray;
	
	private MeterGaugeChartModel meterGaugeModel;

	@PostConstruct
	public void init() {

		logger.info("begin : init");

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
		
		model.addColumn(column1);
		model.addColumn(column2);
		model.addColumn(column3);
		model.addColumn(column4);

		column3.addWidget("status");

		
		createPieModel();
		createPieModel7();

		
		
		createCurCategoryModel();
		createPrevCategoryModel();
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

	public void refreshCurDurationChart() {

		createCurCategoryModel();
	}

	public void refreshPrevDurationChart() {

		createPrevCategoryModel();
	}

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
		pieDashboardModel = new PieChartModel();

		pieDashboardModel.set("Success", 40);
		pieDashboardModel.set("Failed", 5);
		pieDashboardModel.set("Ready", 62);
		pieDashboardModel.set("Pending", 30);
	}
	
	private void createPieModel7() {
		pieDashboardModel7 = new PieChartModel();

		pieDashboardModel7.set("Success", 540);
		pieDashboardModel7.set("Failed", 325);
		pieDashboardModel7.set("Ready", 702);
		pieDashboardModel7.set("Pending", 421);
	}

	private void createCurCategoryModel() {

		curDurationModel = new CartesianChartModel();

		resetJobReportAction();
		
		try {
			jobsArray = getDbOperations().getJobArrayReport(1, 0, "ascending", 10);
		} catch (XMLDBException e) {
			e.printStackTrace();
		}

		ChartSeries jobs = new ChartSeries();
		jobs.setLabel("Jobs");

		for (Job job : jobsArray) {
			jobs.set(job.getJname(), job.getBigDecimalValue());
		}
		
		curDurationModel.addSeries(jobs);

	}

	private void createPrevCategoryModel() {
		prevDurationModel = new CartesianChartModel();

	    resetJobReportAction();
		
		try {
			jobsArray = getDbOperations().getJobArrayReport(1, -1, "ascending", 10);
		} catch (XMLDBException e) {
			e.printStackTrace();
		}
		
		ChartSeries prevJobs = new ChartSeries();
		prevJobs.setLabel("Jobs");

		for (Job job : jobsArray) {
			prevJobs.set(job.getJname(), job.getBigDecimalValue());
		}
		
		prevDurationModel.addSeries(prevJobs);

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

}