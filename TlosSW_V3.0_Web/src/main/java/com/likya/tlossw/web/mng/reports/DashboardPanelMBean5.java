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
import org.primefaces.model.chart.MeterGaugeChartModel;
import org.xmldb.api.base.XMLDBException;

import com.likya.tlos.model.xmlbeans.report.JobArrayDocument.JobArray;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.db.DBOperations;

@ManagedBean(name = "dashboardPanelMBean5")
@ViewScoped
public class DashboardPanelMBean5 extends TlosSWBaseBean implements
		Serializable {

	@ManagedProperty(value = "#{dbOperations}")
	private DBOperations dbOperations;

 
	
	private static final long serialVersionUID = 2570957528954820036L;
	private static final Logger logger = Logger
			.getLogger(AlarmReportPanelMBean.class);

	private DashboardModel model;
	private MeterGaugeChartModel meterGaugeModel;

	private JobArray jobsArray;

	private BigDecimal overallDuration;
	private BigInteger jobCount;
	private BigInteger scenarioCount;
	private String overallStartTime;
	private String overallEndTime;
	
	@PostConstruct
	public void init() {

		logger.info("begin : init");

		FacesContext facesContext = FacesContext.getCurrentInstance();
		String parameter_value = (String) facesContext.getExternalContext()
				.getRequestParameterMap().get("id");

		System.out.println(parameter_value);

		model = new DefaultDashboardModel();
		DashboardColumn column1 = new DefaultDashboardColumn();
		DashboardColumn column2 = new DefaultDashboardColumn();
		DashboardColumn column3 = new DefaultDashboardColumn();

		column1.addWidget("gauge");
		column1.addWidget("info");

		model.addColumn(column1);
		model.addColumn(column2);
		model.addColumn(column3);

		createMeterGaugeModel();

		logger.info("end : init");

	}

	public void handleReorder(DashboardReorderEvent event) {
		FacesMessage message = new FacesMessage();
		message.setSeverity(FacesMessage.SEVERITY_INFO);
		message.setSummary("Reordered: " + event.getWidgetId());
		message.setDetail("Item index: " + event.getItemIndex()
				+ ", Column index: " + event.getColumnIndex()
				+ ", Sender index: " + event.getSenderColumnIndex());

	}

	public void refreshGaugeChart() {

		createMeterGaugeModel();
	}
	
	private void createMeterGaugeModel() {

		setMeterGaugeModel(new MeterGaugeChartModel());

		List<Number> intervals = new ArrayList<Number>() {
			{
				add(2);
				add(4);
				add(6);
				add(12);
			}
		};

		try {
			jobsArray = getDbOperations()
					.getOverallReport(1, 0, "ascending", 1);
		} catch (XMLDBException e) {
			e.printStackTrace();
		}

		setMeterGaugeModel(new MeterGaugeChartModel(jobsArray.getTotalDurationInSec().divide(new BigDecimal("3600"), 0), intervals));

		setOverallDuration(jobsArray.getTotalDurationInSec().divide(new BigDecimal("3600"), 0));
		setJobCount(jobsArray.getNumberOfJobs());
		setScenarioCount(jobsArray.getNumberOfScenarios());
		setOverallStartTime(jobsArray.getOverallStart().toString());
		setOverallEndTime(jobsArray.getOverallStop().toString());
		
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

	public MeterGaugeChartModel getMeterGaugeModel() {
		return meterGaugeModel;
	}

	public void setMeterGaugeModel(MeterGaugeChartModel meterGaugeModel) {
		this.meterGaugeModel = meterGaugeModel;
	}

	public BigDecimal getOverallDuration() {
		return overallDuration;
	}

	public void setOverallDuration(BigDecimal overallDuration) {
		this.overallDuration = overallDuration;
	}

 
	public String getOverallStartTime() {
		return overallStartTime;
	}

	public void setOverallStartTime(String overallStartTime) {
		this.overallStartTime = overallStartTime;
	}

	public String getOverallEndTime() {
		return overallEndTime;
	}

	public void setOverallEndTime(String overallEndTime) {
		this.overallEndTime = overallEndTime;
	}

	public BigInteger getJobCount() {
		return jobCount;
	}

	public void setJobCount(BigInteger jobCount) {
		this.jobCount = jobCount;
	}

	public BigInteger getScenarioCount() {
		return scenarioCount;
	}

	public void setScenarioCount(BigInteger scenarioCount) {
		this.scenarioCount = scenarioCount;
	}



 
}