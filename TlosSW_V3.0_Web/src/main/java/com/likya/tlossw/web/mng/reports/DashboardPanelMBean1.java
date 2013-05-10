package com.likya.tlossw.web.mng.reports;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

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
import org.xmldb.api.base.XMLDBException;

import com.likya.tlos.model.xmlbeans.report.JobDocument.Job;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.db.DBOperations;

@ManagedBean(name = "dashboardPanelMBean1")
@ViewScoped 
public class DashboardPanelMBean1 extends TlosSWBaseBean implements Serializable {
	
	@ManagedProperty(value = "#{dbOperations}")
	private DBOperations dbOperations;
	
	private static final long serialVersionUID = 2570957528954820036L;
	private static final Logger logger = Logger.getLogger(AlarmReportPanelMBean.class);
 
	private DashboardModel model;
	private CartesianChartModel curDurationModel;
	private CartesianChartModel prevDurationModel;
 
	private ArrayList<Job> jobsArray;
	
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

		
		column1.addWidget("top10");
		column2.addWidget("topPrev10");
 
		
		model.addColumn(column1);
		model.addColumn(column2);
		model.addColumn(column3);
		
		createCurCategoryModel();
		createPrevCategoryModel();

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
	
	public void refreshCurDurationChart() {

		createCurCategoryModel();
	}

	public void refreshPrevDurationChart() {

		createPrevCategoryModel();
	}


	private void createCurCategoryModel() {

		curDurationModel = new CartesianChartModel();

		resetJobReportAction();
		
		try {
			jobsArray = getDbOperations().getJobArrayReport(1, 0, 5, "ascending", "", 10);
		} catch (XMLDBException e) {
			e.printStackTrace();
		}

		ChartSeries jobs = new ChartSeries();
		jobs.setLabel("Jobs");

		for (Job job : jobsArray) {
			jobs.set(job.getJname(), job.getBigDecimalValue().divide(new BigDecimal("60"), 0));
		}
		
		curDurationModel.addSeries(jobs);

	}

	private void createPrevCategoryModel() {
		prevDurationModel = new CartesianChartModel();

	    resetJobReportAction();
		
		try {
			jobsArray = getDbOperations().getJobArrayReport(1, 1, -1, "ascending", "", 10);
		} catch (XMLDBException e) {
			e.printStackTrace();
		}
		
		ChartSeries prevJobs = new ChartSeries();
		prevJobs.setLabel("Jobs");

		for (Job job : jobsArray) {
			prevJobs.set(job.getJname(), job.getBigDecimalValue().divide(new BigDecimal("60"), 0));
		}
		
		prevDurationModel.addSeries(prevJobs);

	}

	public DashboardModel getModel() {
		return model;
	}

	public void setModel(DashboardModel model) {
		this.model = model;
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