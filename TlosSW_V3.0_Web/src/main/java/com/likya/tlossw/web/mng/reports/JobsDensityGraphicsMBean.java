package com.likya.tlossw.web.mng.reports;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.MeterGaugeChartModel;
import org.xmldb.api.base.XMLDBException;

import com.likya.tlos.model.xmlbeans.report.JobDocument.Job;
import com.likya.tlos.model.xmlbeans.report.StatisticsDocument.Statistics;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.db.DBOperations;

@ManagedBean(name = "jobsDensityGraphicsMBean")
@ViewScoped 
public class JobsDensityGraphicsMBean extends TlosSWBaseBean implements Serializable {
	
	@ManagedProperty(value = "#{dbOperations}")
	private DBOperations dbOperations;
	
	private static final long serialVersionUID = 2570957528954820036L;
	private static final Logger logger = Logger.getLogger(JobsDensityGraphicsMBean.class);

	private CartesianChartModel denseModel;
	private Statistics densityJobCountList;
	private int derinlik;
	
	private Long maxValue;
	
	private BigInteger sizeOfReport;
	
	private String pieColorList;
	
	private ArrayList<Job> jobsArray;
	
	private MeterGaugeChartModel meterGaugeModel;

	@PostConstruct
	public void init() {

		logger.info("begin : init");

		FacesContext facesContext = FacesContext.getCurrentInstance();
		String parameter_value = (String) facesContext.getExternalContext().getRequestParameterMap().get("id");

		System.out.println(parameter_value);

		createDenseModel();

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
	
	public void refreshDenseChart() {
		createDenseModel();

	}

	private void createMeterGaugeModel() {

//		List<Number> intervals = new ArrayList<Number>() {
//			{
//				add(2);
//				add(4);
//				add(6);
//				add(12);
//			}
//		};
//
//		meterGaugeModel = new MeterGaugeChartModel(6.5, intervals);
	}

	private void createDenseModel() {
		denseModel = new CartesianChartModel();
       
		ChartSeries dense = new ChartSeries();
		dense.setLabel("Number of Jobs");

		try {
			densityJobCountList = getDbOperations().getDensityReport("xs:string(\"RUNNING\")", "xs:string(\"ON-RESOURCE\")", "xs:string(\"TIME-IN\")" , "xs:dateTime(\"2013-05-15T16:26:25+03:00\")", "xs:dateTime(\"2013-05-15T16:49:27+03:00\")", "xs:dayTimeDuration('PT10S')");
		} catch (XMLDBException e) {
			e.printStackTrace();
		}
        Integer counter = new Integer(0);
        int maxval = 0;
        setSizeOfReport( densityJobCountList.getDataArray(0).getCount() );
		for(Integer i=0; i<densityJobCountList.sizeOfDataArray(); i++) {

			String formattedTime = new SimpleDateFormat("HH:mm:ss").format(densityJobCountList.getDataArray(i).getEDTime().getTime()); // 9:00
			dense.set(formattedTime, densityJobCountList.getDataArray(i).getCount().intValue());
			counter = counter + (densityJobCountList.getDataArray(i).getCount().intValue() > 0 ? 1 : 0);
			maxval = (densityJobCountList.getDataArray(i).getCount().intValue()) > maxval ? densityJobCountList.getDataArray(i).getCount().intValue() : maxval;
		}
		Double max = Math.ceil((double) maxval/counter.longValue())*counter.longValue();
		setMaxValue( max.longValue());

		denseModel.addSeries(dense);

	}

	public CartesianChartModel getDenseModel() {
		return denseModel;
	}

	public void setDenseModel(CartesianChartModel denseModel) {
		this.denseModel = denseModel;
	}

	public MeterGaugeChartModel getMeterGaugeModel() {
		return meterGaugeModel;
	}

	public void setMeterGaugeModel(MeterGaugeChartModel meterGaugeModel) {
		this.meterGaugeModel = meterGaugeModel;
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

}