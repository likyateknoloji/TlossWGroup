package com.likya.tlossw.web.mng.reports;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

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

@ManagedBean(name = "zonesOfExecutionsMBean")
@ViewScoped
public class ZonesOfExecutionsMBean extends TlosSWBaseBean implements
		Serializable {

	@ManagedProperty(value = "#{dbOperations}")
	private DBOperations dbOperations;

 
	
	private static final long serialVersionUID = 2570957528954820036L;
	private static final Logger logger = Logger
			.getLogger(AlarmReportPanelMBean.class);

	private DashboardModel model;
	private MeterGaugeChartModel meterGaugeModel;

	private JobArray jobsArray;

	private String overallDuration;
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

		
		/*
		 * 0 : kırmızı
           min çalışma süresi : sari
           beklenen çalışma süresi -tolerans: 
           beklenen çalışma süresi : yeşil
           beklenen çalışma süresi +tolerans: sarı
           maximum çalışma süresi : kırmızı

           timeout süresi
		 */
		/* DB den gelecek olan degerlerle dolacak. Simdilik sabit degerler var */
		Integer tolerancePer = new Integer(30);
		Integer minWorkingTimeStat = new Integer(30);
		Integer maxWorkingTimeStat = new Integer(70);
		Integer expWorkingTimeStat = new Integer(50);
		
		/* Hesaplanacak olanlar */
		Integer sifir = new Integer(0);
		
		Integer minWorkingTime = new Integer(minWorkingTimeStat);
		
		Integer minValueCand = new Integer((int) (expWorkingTimeStat - expWorkingTimeStat*(tolerancePer/100.0)));
		Integer minTolWorkingTime = new Integer((minValueCand < 0 ? 0 : minValueCand));
		
		Integer expectedWorkingTime = new Integer(expWorkingTimeStat);
		
		Integer maxWorkingTime = new Integer(maxWorkingTimeStat);
		
		Integer maxTolWorkingTime = new Integer((int) (expWorkingTimeStat + expWorkingTimeStat*(tolerancePer/100.0)));
		// göstergede ençok max süreden %25 fazlası olabilsin. Bu değere kadar toleransın 3 katı yüzde koydum şimdilik.
		Integer maxmaxTolWorkingTime = new Integer((int) (maxTolWorkingTime + maxTolWorkingTime*(Math.min(tolerancePer*3/100.0, 25))));
		
		List<Number> intervals = new ArrayList<Number>();
		intervals.add(sifir);
		if(minTolWorkingTime < minWorkingTime ) intervals.add(minTolWorkingTime); else intervals.add(minWorkingTime);
		intervals.add(expectedWorkingTime);
		if(maxTolWorkingTime > maxWorkingTime ) intervals.add(maxTolWorkingTime); else intervals.add(maxWorkingTime);
		intervals.add(maxmaxTolWorkingTime);
		
		List<Number> ticks = new ArrayList<Number>();
		
		Integer step = new Integer(Math.max(2, (int) Math.ceil(maxmaxTolWorkingTime/12.0)));  //max 12 tick olsun
		Integer numberOfTicks = new Integer( (int) Math.ceil((maxmaxTolWorkingTime - sifir)/(step*1.0)));

		
		
		for(Integer i=0; i<numberOfTicks; i++) {
			ticks.add(sifir + step*i);
		}

		if(ticks.get( (ticks.size()== 0 ? 0 : ticks.size() - 1) ).intValue()< maxmaxTolWorkingTime) ticks.add(maxmaxTolWorkingTime);
		try {
			jobsArray = getDbOperations().getOverallReport(1, 0, 0, "true", "descending", 1);
		} catch (XMLDBException e) {
			e.printStackTrace();
		}

		BigDecimal totalDuration = jobsArray.getTotalDurationInSec();
		
		MeterGaugeChartModel meterGaugeModel = new MeterGaugeChartModel(14, intervals, ticks); 
		setMeterGaugeModel(meterGaugeModel);
		
		SimpleTimeZone tz = new SimpleTimeZone(0, "Out Timezone");        
		TimeZone.setDefault(tz);

		final Calendar cal = Calendar.getInstance(tz);
		cal.setTimeInMillis(totalDuration.scaleByPowerOfTen(totalDuration.scale()).longValue());

		// and here's how to get the String representation
		final String timeString =
		    new SimpleDateFormat("HH:mm:ss.SSS").format(cal.getTime());
		//System.out.println(timeString);
		
		setOverallDuration(timeString);
		setJobCount(jobsArray.getNumberOfJobs());
		//setScenarioCount(jobsArray.getNumberOfScenarios());
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
 
	public String getOverallDuration() {
		return overallDuration;
	}

	public void setOverallDuration(String overallDuration) {
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