package com.likya.tlossw.web.mng.reports;

import java.io.Serializable;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.impl.values.XmlValueOutOfRangeException;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.model.chart.OhlcChartModel;
import org.primefaces.model.chart.OhlcChartSeries;
import org.xmldb.api.base.XMLDBException;

import com.likya.tlos.model.xmlbeans.report.JobArrayDocument.JobArray;
import com.likya.tlos.model.xmlbeans.report.JobDocument.Job;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.db.DBOperations;

@ManagedBean(name = "jobsDistributionGraphicsMBean")
@ViewScoped 
public class JobsDistributionGraphicsMBean extends TlosSWBaseBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6736991439032005658L;


	@ManagedProperty(value = "#{dbOperations}")
	private DBOperations dbOperations;
	

	private static final Logger logger = Logger.getLogger(JobsDistributionGraphicsMBean.class);

	private OhlcChartModel ohlcModel;

	private int derinlik;
	
	private String pieColorList;
	
	private JobArray jobsArray;

	@PostConstruct
	public void init() {

		logger.info("begin : init");

		FacesContext facesContext = FacesContext.getCurrentInstance();
		String parameter_value = (String) facesContext.getExternalContext().getRequestParameterMap().get("id");

		System.out.println(parameter_value);

		createOhlcModel(1, 0, 0, "true()", "xs:string(\"descending\")", 10);

		logger.info("end : init");

	}

	public void handleReorder(DashboardReorderEvent event) {
		FacesMessage message = new FacesMessage();
		message.setSeverity(FacesMessage.SEVERITY_INFO);
		message.setSummary("Reordered: " + event.getWidgetId());
		message.setDetail("Item index: " + event.getItemIndex() + ", Column index: " + event.getColumnIndex() + ", Sender index: " + event.getSenderColumnIndex());

	}
	
	public String getChartSeriesColors() {
        return "red, blue, 0x18e0b1, 0xd5e018";
    }
	
	public String getDatatipFormat(){
		   return "<span style=\"display:none;\">%s</span><span>%s</span>";
		}
	
	public void refreshOhlcChart() {
		createOhlcModel(1, 0, 0, "true()", "xs:string(\"descending\")", 10);

	}

	public long getMilliSec(long param) {
		return param - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(param));
	}
	
	private void createOhlcModel(int derinlik, int runType, int jobId,  String refPoint, String orderType, int jobCount) {
		
        ohlcModel = new OhlcChartModel();  

		try {
			jobsArray = getDbOperations().getOverallReport(derinlik, runType, jobId, refPoint, orderType, jobCount);
		} catch (XMLDBException e) {
			e.printStackTrace();
		}

		
		//BigDecimal dividend = new BigDecimal(1); //new BigDecimal("60");
		Calendar overallStart = Calendar.getInstance();
		if(jobsArray.sizeOfJobArray() > 0) {
		  overallStart = jobsArray.getOverallStart();
		}
		//Calendar overallStop = jobsArray.getOverallStop();

				// and here's how to get the String representation
		//final String timeString =
		    //new SimpleDateFormat("HH:mm:ss.SSS").format(overallStart.getTimeInMillis());
		
		for (Job job : jobsArray.getJobArray()) {

			String result = String.format("%d sec", 
				    getMilliSec(job.getStartTime().getTimeInMillis())
				);
			System.out.println(result);
			Calendar figure = null;
			try {
			   figure = job.getStopTime();
			}
			catch(XmlValueOutOfRangeException e){
	            //do something clever with the exception
				figure = job.getStartTime();
	            System.out.println(e.getMessage());				
			}
			
			ohlcModel.add(new OhlcChartSeries(TimeUnit.MILLISECONDS.toSeconds(job.getStartTime().getTimeInMillis() - overallStart.getTimeInMillis()), 
					(double) TimeUnit.MILLISECONDS.toSeconds(job.getStartTime().getTimeInMillis() - overallStart.getTimeInMillis()) ,
					(double) TimeUnit.MILLISECONDS.toSeconds(figure.getTimeInMillis() - overallStart.getTimeInMillis()) ,
					(double) TimeUnit.MILLISECONDS.toSeconds(job.getStartTime().getTimeInMillis() - overallStart.getTimeInMillis()) ,
					(double) TimeUnit.MILLISECONDS.toSeconds(figure.getTimeInMillis() - overallStart.getTimeInMillis())
					));
			
		}



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

	public String getPieColorList() {
		return pieColorList;
	}

	public void setPieColorList(String pieColorList) {
		this.pieColorList = pieColorList;
	}

	public OhlcChartModel getOhlcModel() {
		return ohlcModel;
	}

	public void setOhlcModel(OhlcChartModel ohlcModel) {
		this.ohlcModel = ohlcModel;
	}

	public JobArray getJobsArray() {
		return jobsArray;
	}

	public void setJobsArray(JobArray jobsArray) {
		this.jobsArray = jobsArray;
	}

}