package com.likya.tlossw.web.mng.reports;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.MutableDateTime;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;
import org.primefaces.model.chart.MeterGaugeChartModel;
import org.xmldb.api.base.XMLDBException;

import com.likya.tlos.model.xmlbeans.report.JobArrayDocument.JobArray;
import com.likya.tlos.model.xmlbeans.report.LocalStatsDocument.LocalStats;
import com.likya.tlossw.web.db.DBOperations;
import com.likya.tlossw.web.mng.reports.helpers.ReportsParameters;
import com.likya.tlossw.web.utils.ConstantDefinitions;

/*
 * 0 : mavi
 min çalışma süresi : sari
 beklenen çalışma süresi -tolerans: yeşil
 beklenen çalışma süresi : yeşil
 beklenen çalışma süresi +tolerans: yeşil
 maximum çalışma süresi : sarı
 out of time : kırmızı

 timeout süresi
 */

@ManagedBean(name = "zonesOfExecutionsMBean")
@ViewScoped
public class ZonesOfExecutionsMBean extends ReportBase implements Serializable {

	private static final long serialVersionUID = -9044483092848085795L;

	@ManagedProperty(value = "#{dbOperations}")
	private DBOperations dbOperations;

	private static final Logger logger = Logger.getLogger(ZonesOfExecutionsMBean.class);

	private DashboardModel model;
	private MeterGaugeChartModel meterGaugeModel;

	private JobArray jobsArray;

	private List<Number> intervals;
	private String overallDuration;
	private String minWorkingTimeStat;
	private String maxWorkingTimeStat;
	private String expWorkingTimeStat;
	private BigInteger jobCount;
	private BigInteger scenarioCount;
	private String overallStartTime;
	private String overallEndTime;
	private Double totalDurationNormalized;
	private BigDecimal totalDurationBD;
	private Boolean isFinished;
	private BigDecimal totalDurationInSec;
	private BigInteger numberOfJobs;
	private BigInteger numberOfScenarios;

	@PostConstruct
	public void init() {

		logger.info("begin : init");

		FacesContext facesContext = FacesContext.getCurrentInstance();
		String parameter_value = (String) facesContext.getExternalContext().getRequestParameterMap().get("id");

		System.out.println(parameter_value);

		model = new DefaultDashboardModel();
		DashboardColumn column1 = new DefaultDashboardColumn();
		DashboardColumn column2 = new DefaultDashboardColumn();

		column1.addWidget("gauge");
		column2.addWidget("info");
		column2.addWidget("stats");

		model.addColumn(column1);
		model.addColumn(column2);

		createMeterGaugeModel();

		logger.info("end : init");

		setActiveReportPanel(ConstantDefinitions.ZONES_REPORT);
	}

	public void handleReorder(DashboardReorderEvent event) {
		FacesMessage message = new FacesMessage();
		message.setSeverity(FacesMessage.SEVERITY_INFO);
		message.setSummary("Reordered: " + event.getWidgetId());
		message.setDetail("Item index: " + event.getItemIndex() + ", Column index: " + event.getColumnIndex() + ", Sender index: " + event.getSenderColumnIndex());
	}

	public void refreshGaugeChart() {
		createMeterGaugeModel();
	}

	private void createMeterGaugeModel() {

		setReportParameters(new ReportsParameters());

		LocalStats localStats = null;
		try {
			localStats = getDbOperations().getStatsReport(getReportParameters().getReportParametersXML());
		} catch (XMLDBException e) {
			e.printStackTrace();
		}

		Double tolerancePer = new Double(20.0);
		Double minWorkingTimeStat = localStats.getMin().doubleValue(); // new Integer(30);
		Double maxWorkingTimeStat = localStats.getMax().doubleValue(); // new Integer(70);
		Double expWorkingTimeStat = localStats.getAvg().doubleValue();
		; // new Integer(50);

		/* Hesaplanacak olanlar */

		Double minWorkingTime = new Double(minWorkingTimeStat);

		Double minValueCand = new Double(expWorkingTimeStat - expWorkingTimeStat * (tolerancePer / 100.0));
		Double minTolWorkingTime = new Double((minValueCand < 0 ? 0 : minValueCand));

		Double expectedWorkingTime = new Double(expWorkingTimeStat);

		Double maxWorkingTime = new Double(maxWorkingTimeStat);

		Double maxTolWorkingTime = new Double(expWorkingTimeStat + expWorkingTimeStat * (tolerancePer / 100.0));
		// göstergede ençok max süreden %25 fazlası olabilsin. Bu değere kadar toleransın 2 katı yüzde koydum şimdilik.
		// /////Double maxmaxTolWorkingTime = new Double(maxTolWorkingTime + maxTolWorkingTime*(Math.min(tolerancePer*2/100.0, 25)));
		Double maxmaxTolWorkingTime = new Double(maxWorkingTime + maxWorkingTime * (Math.min(tolerancePer * 2 / 100.0, 25)));

		/* Ekranda gozukecek en kucuk deger yani goreceli sifir ne olacak */
		int deger = (int) (Math.min(minWorkingTimeStat, minTolWorkingTime) / 2);
		Double sifir = new Double(deger);

		// Renk bolgelerini belirliyoruz...

		intervals = new ArrayList<Number>();
		intervals.add(sifir);
		if (minTolWorkingTime < minWorkingTime) {
			if (minTolWorkingTime > sifir)
				intervals.add(minTolWorkingTime);
			else
				intervals.add(minTolWorkingTime + 0.1);
			intervals.add(minWorkingTime);
		} else {
			if (minWorkingTime > sifir)
				intervals.add(minWorkingTime);
			else
				intervals.add(minWorkingTime + 0.1);
			intervals.add(minTolWorkingTime);
		}

		intervals.add(expectedWorkingTime);

		if (maxTolWorkingTime > maxWorkingTime) {
			intervals.add(maxWorkingTime);
			intervals.add(maxTolWorkingTime);
		} else {
			intervals.add(maxTolWorkingTime);
			intervals.add(maxWorkingTime);
		}

		intervals.add(maxmaxTolWorkingTime);

		List<Number> ticks = new ArrayList<Number>();

		Integer step = new Integer(Math.max(2, (int) Math.ceil(maxmaxTolWorkingTime / 12.0))); // max 12 tick olsun
		Integer numberOfTicks = new Integer((int) Math.ceil((maxmaxTolWorkingTime - sifir) / (step * 1.0)));

		for (Integer i = 0; i < numberOfTicks; i++) {
			ticks.add(sifir + step * i);
		}

		int index = ticks.size() == 0 ? 0 : ticks.size() - 1;

		if (!ticks.isEmpty()) {
			for (int j = 1; ticks.get(index).doubleValue() < maxmaxTolWorkingTime; j++) {
				ticks.add(ticks.get(index).doubleValue() + step * j);
				index = ticks.size() == 0 ? 0 : ticks.size() - 1;
			}
			maxmaxTolWorkingTime = ticks.get(index).doubleValue();
			int intervalIndex = (intervals.size() == 0) ? 0 : intervals.size() - 1;
			intervals.set(intervalIndex, maxmaxTolWorkingTime);
		}

		try {
			jobsArray = getDbOperations().getOverallReport(getReportParameters().getReportParametersXML());
		} catch (XMLDBException e) {
			e.printStackTrace();
		}
		Double totalDuration = new Double(0.0);
		totalDurationBD = new BigDecimal(0);
		totalDurationNormalized = new Double(0.0);
		totalDurationInSec = new BigDecimal(0);
		String overallStart = "N/A";
		String overallStop = "N/A";
		isFinished = false;

		if (jobsArray.sizeOfJobArray() > 0) {
			totalDuration = jobsArray.getTotalDurationInSec().doubleValue();
			totalDurationBD = jobsArray.getTotalDurationInSec();
			totalDurationNormalized = totalDuration;
			overallStart = jobsArray.getOverallStart().toString();
			overallStop = jobsArray.getOverallStop().toString();
			isFinished = jobsArray.getIsFinished();
			totalDurationInSec = jobsArray.getTotalDurationInSec();
			numberOfJobs = jobsArray.getNumberOfJobs();
			numberOfScenarios = jobsArray.getNumberOfScenarios();
		}
		// ibre sinirlari asmasin ..
		if (totalDuration.compareTo(sifir) < 0) {
			totalDurationNormalized = sifir;
		} else if (totalDuration.compareTo(maxmaxTolWorkingTime) >= 0) {
			totalDurationNormalized = maxmaxTolWorkingTime;
		}

		MeterGaugeChartModel meterGaugeModel = new MeterGaugeChartModel(totalDurationNormalized, intervals, ticks);
		setMeterGaugeModel(meterGaugeModel);

		setOverallDuration(numberToDayTimeFormat(totalDurationBD));
		setMinWorkingTimeStat(numberToTimeFormat(localStats.getMin()));
		setMaxWorkingTimeStat(numberToTimeFormat(localStats.getMax()));
		setExpWorkingTimeStat(numberToTimeFormat(localStats.getAvg()));
		setJobCount(numberOfJobs);
		setScenarioCount(numberOfScenarios);
		setIsFinished(isFinished);
		setTotalDurationInSec(totalDurationInSec);
		setOverallStartTime(overallStart);
		setOverallEndTime(overallStop);
	}

	public String numberToTimeFormat(BigDecimal number) {
		final int SCALE = 3; // Virgulden sonra 3 hane oldugunu kabul ettik.

		SimpleTimeZone tz = new SimpleTimeZone(0, "Out Timezone");
		TimeZone.setDefault(tz);

		Calendar cal = Calendar.getInstance(tz);
		cal.setTimeInMillis(number.scaleByPowerOfTen(SCALE).longValue());

		// and here's how to get the String representation
		String timeString = new SimpleDateFormat("HH:mm:ss.SSS").format(cal.getTime());

		return timeString;
	}

	public String numberToDayTimeFormat(BigDecimal number) {

		MutableDateTime epoch = new MutableDateTime();
		epoch.setDate(0); // Set to Epoch time

		DateTime dt = new DateTime(totalDurationBD.longValue() * 1000);

		String timepart = dt.toString("HH:mm:ss.SSS");
		Days days = Days.daysBetween(epoch, dt);
		String daypart = "";

		if (days.getDays() > 0)
			daypart = days.getDays() + " " + resolveMessage("tlos.report.gauge.days") + " ";

		return daypart + timepart;
	}

	public void refreshReport(ActionEvent actionEvent) {
		System.out.println("aaa");

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

	public String getMinWorkingTimeStat() {
		return minWorkingTimeStat;
	}

	public void setMinWorkingTimeStat(String minWorkingTimeStat) {
		this.minWorkingTimeStat = minWorkingTimeStat;
	}

	public String getMaxWorkingTimeStat() {
		return maxWorkingTimeStat;
	}

	public void setMaxWorkingTimeStat(String maxWorkingTimeStat) {
		this.maxWorkingTimeStat = maxWorkingTimeStat;
	}

	public String getExpWorkingTimeStat() {
		return expWorkingTimeStat;
	}

	public void setExpWorkingTimeStat(String expWorkingTimeStat) {
		this.expWorkingTimeStat = expWorkingTimeStat;
	}

	public Double getTotalDurationNormalized() {
		return totalDurationNormalized;
	}

	public void setTotalDurationNormalized(Double totalDurationNormalized) {
		this.totalDurationNormalized = totalDurationNormalized;
	}

	public String[] findZone() {
		String zones[][] = { { resolveMessage("tlos.report.gauge.problem"), resolveMessage("tlos.report.gauge.problemExp") }, { resolveMessage("tlos.report.gauge.blue"), isFinished ? resolveMessage("tlos.report.gauge.finished.blueExplanation") : resolveMessage("tlos.report.gauge.running.blueExplanation") }, { resolveMessage("tlos.report.gauge.yellow1"), isFinished ? resolveMessage("tlos.report.gauge.finished.yellow1Explanation") : resolveMessage("tlos.report.gauge.running.yellow1Explanation") }, { resolveMessage("tlos.report.gauge.green1"), isFinished ? resolveMessage("tlos.report.gauge.finished.green1Explanation") : resolveMessage("tlos.report.gauge.running.green1Explanation") },
				{ resolveMessage("tlos.report.gauge.green2"), isFinished ? resolveMessage("tlos.report.gauge.finished.green2Explanation") : resolveMessage("tlos.report.gauge.running.green2Explanation") }, { resolveMessage("tlos.report.gauge.yellow2"), isFinished ? resolveMessage("tlos.report.gauge.finished.yellow2Explanation") : resolveMessage("tlos.report.gauge.running.yellow2Explanation") }, { resolveMessage("tlos.report.gauge.red"), isFinished ? resolveMessage("tlos.report.gauge.finished.redExplanation") : resolveMessage("tlos.report.gauge.running.redExplanation") } };

		int sonuc = 0;
		for (int i = 0; i < intervals.size(); i++)
			if (totalDurationNormalized.compareTo((Double) intervals.get(i)) < 0) {
				sonuc = i;
				break;
			}
		return totalDurationNormalized.compareTo((Double) intervals.get(intervals.size() - 2)) > 0 ? zones[intervals.size() - 1] : zones[sonuc];
	}

	public BigDecimal getTotalDurationBD() {
		return totalDurationBD;
	}

	public void setTotalDurationBD(BigDecimal totalDurationBD) {
		this.totalDurationBD = totalDurationBD;
	}

	public List<Number> getIntervals() {
		return intervals;
	}

	public void setIntervals(List<Number> intervals) {
		this.intervals = intervals;
	}

	public Boolean getIsFinished() {
		return isFinished;
	}

	public void setIsFinished(Boolean isFinished) {
		this.isFinished = isFinished;
	}

	public BigDecimal getTotalDurationInSec() {
		return totalDurationInSec;
	}

	public void setTotalDurationInSec(BigDecimal totalDurationInSec) {
		this.totalDurationInSec = totalDurationInSec;
	}

	public BigInteger getNumberOfJobs() {
		return numberOfJobs;
	}

	public void setNumberOfJobs(BigInteger numberOfJobs) {
		this.numberOfJobs = numberOfJobs;
	}

	public BigInteger getNumberOfScenarios() {
		return numberOfScenarios;
	}

	public void setNumberOfScenarios(BigInteger numberOfScenarios) {
		this.numberOfScenarios = numberOfScenarios;
	}

	public String getJSStateText() {
		return isFinished ? resolveMessage("tlos.report.gauge.jsStateInText.finished") : resolveMessage("tlos.report.gauge.jsStateInText.running");
	}

	public String getJSEndText() {
		return isFinished ? resolveMessage("tlos.report.gauge.overallEndTime") : resolveMessage("tlos.report.gauge.reportTime");
	}

	public String getStatSampleNumberText() {
		return resolveMessage("tlos.report.gauge.statsText1") + " " + getReportParameters().getStatSampleNumber() + " " + resolveMessage("tlos.report.gauge.statsText2");
	}

}