package com.likya.tlossw.web.mng.reports;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.xmldb.api.base.XMLDBException;

import com.likya.tlos.model.xmlbeans.report.JobDocument.Job;
import com.likya.tlos.model.xmlbeans.report.StatisticsDocument.Statistics;
import com.likya.tlossw.web.db.DBOperations;
import com.likya.tlossw.web.mng.reports.helpers.ReportsParameters;
import com.likya.tlossw.web.utils.ConstantDefinitions;

@ManagedBean(name = "jobsDensityGraphicsMBean")
@ViewScoped
public class JobsDensityGraphicsMBean extends ReportBase implements Serializable {

	@ManagedProperty(value = "#{dbOperations}")
	private DBOperations dbOperations;

	private static final long serialVersionUID = 2570957528954820036L;
	private static final Logger logger = Logger.getLogger(JobsDensityGraphicsMBean.class);

	private CartesianChartModel denseModel;
	private Statistics densityJobCountList;
	private int derinlik;

	private Long maxValue;
	
	private boolean stacked = false;
	
	private BigInteger sizeOfReport;

	private String pieColorList;

	private ArrayList<Job> jobsArray;

	@PostConstruct
	public void init() {

		logger.info("begin : init");

		FacesContext facesContext = FacesContext.getCurrentInstance();
		String parameter_value = (String) facesContext.getExternalContext().getRequestParameterMap().get("id");

		if (getReportParameters() == null) {
			setReportParameters(new ReportsParameters());
		}
		fillStepForDensity();
		fillTimeProperties();
		fillStateProperties();
		System.out.println(parameter_value);
		stacked = false;
		
		createDenseModel();

		logger.info("end : init");

		setActiveReportPanel(ConstantDefinitions.JOB_DENSITY_REPORT);
	}

	public void handleReorder(DashboardReorderEvent event) {
		FacesMessage message = new FacesMessage();
		message.setSeverity(FacesMessage.SEVERITY_INFO);
		message.setSummary("Reordered: " + event.getWidgetId());
		message.setDetail("Item index: " + event.getItemIndex()
				+ ", Column index: " + event.getColumnIndex()
				+ ", Sender index: " + event.getSenderColumnIndex());
	}

	public void resetJobReportAction() {
		jobsArray = new ArrayList<Job>();
	}

	public String getChartSeriesColors() {
		return "red, blue, 0x18e0b1, 0xd5e018";
	}

	public String getDatatipFormat() {
		return "<span style=\"display:none;\">%s</span><span>%s</span>";
	}

	public void refreshDenseChart() {
		if(stacked) stacked = false;
		else stacked = true;
		
		createDenseModel();
	}

	public void changeDensityStep() {
		createDenseModel();
	}
	
    public void itemSelect(ItemSelectEvent event) {  
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Item selected",  
                        "Item Index: " + event.getItemIndex() + ", Series Index:" + event.getSeriesIndex());  
  
        FacesContext.getCurrentInstance().addMessage(null, msg);  
    }  
    
	private void createDenseModel() {

		getReportParameters().fillReportParameters();
		
		denseModel = new CartesianChartModel();
	
		//ReportParameters reportParameters = ReportParameters.Factory.newInstance();
		
		HashMap<String, StatsByAgent> agentMap = new HashMap<String, StatsByAgent>();
		StatsByAgent statsByAgent = null;

		
		try {
			densityJobCountList = getDbOperations().getDensityReport(getReportParameters().getReportParametersXML());
		} catch (XMLDBException e) {
			e.printStackTrace();
		}
		
		if(densityJobCountList.sizeOfDataArray() > 0 ) setSizeOfReport(densityJobCountList.getDataArray(0).getCount());

		Integer numberOfJobsInThisGroup = new Integer(0);
		int agentId;

		for (int i = 0; i < densityJobCountList.sizeOfDataArray(); i++) {

			String formattedTime = new SimpleDateFormat("HH:mm:ss").format(densityJobCountList.getDataArray(i).getEDTime().getTime()); // 9:00
			numberOfJobsInThisGroup = densityJobCountList.getDataArray(i).getCount().intValue();

			for (int j = 0; j < numberOfJobsInThisGroup.intValue(); j++) {
				agentId = densityJobCountList.getDataArray(i).getGroupArray(j).getAgentId().intValue();
				if (agentMap.get(agentId + "") == null) {
					statsByAgent = new StatsByAgent(new Integer(i));
					agentMap.put(agentId + "", statsByAgent);
				}
				if (agentMap.get(agentId + "").getStatsArray().get(formattedTime) == null) {
					agentMap.get(agentId + "").setStatsArray(formattedTime, 0);
				}
				agentMap.get(agentId + "").incrementCount(formattedTime);
			}
		}
		
		Iterator<String> iter = agentMap.keySet().iterator();

		Double maxMax = new Double(0.0);

		while (iter.hasNext()) {
			int maxVal = 0;
			Integer counter = new Integer(0);

			String key = iter.next();
			StatsByAgent value = agentMap.get(key);
			System.out.print(key + ": ");
			System.out.println(value);

			ChartSeries dense = new ChartSeries();
			dense.setLabel("On Agent (" + key + ")");

			Integer numberOfJobs = new Integer(0);

			for (int j = 0; j < densityJobCountList.sizeOfDataArray(); j++) {
				String formattedTime = new SimpleDateFormat("HH:mm:ss").format(densityJobCountList.getDataArray(j).getEDTime().getTime()); // 9:00
				numberOfJobs = value.getStatsArray().containsKey(formattedTime) ? value.getStatsArray().get(formattedTime).intValue() : 0;
				dense.set(formattedTime, numberOfJobs);
				maxVal = (numberOfJobs) > maxVal ? numberOfJobs : maxVal;
				counter = counter + numberOfJobs > 0 ? 1 : 0;
			}
			Double max = Math.ceil((double) maxVal / counter.longValue()) * counter.longValue();
			maxMax = Math.max(maxMax, max);

			denseModel.addSeries(dense);

		}
		setMaxValue(maxMax.longValue());
	}

	public void refreshReport(ActionEvent actionEvent) {

		fillTimeProperties();
		fillStateProperties();
		fillStepForDensity();

		createDenseModel();
	}

	public CartesianChartModel getDenseModel() {
		return denseModel;
	}

	public void setDenseModel(CartesianChartModel denseModel) {
		this.denseModel = denseModel;
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

	public String getPieColorList() {
		return pieColorList;
	}

	public void setPieColorList(String pieColorList) {
		this.pieColorList = pieColorList;
	}

	public Long getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Long maxValue) {
		this.maxValue = maxValue;
	}

	public BigInteger getSizeOfReport() {
		return sizeOfReport;
	}

	public void setSizeOfReport(BigInteger sizeOfReport) {
		this.sizeOfReport = sizeOfReport;
	}

	public boolean isStacked() {
		return stacked;
	}


}