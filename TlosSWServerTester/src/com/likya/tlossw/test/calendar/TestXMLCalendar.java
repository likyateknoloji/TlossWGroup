package com.likya.tlossw.test.calendar;

import java.io.File;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.GDuration;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.PeriodInfoDocument.PeriodInfo;
import com.likya.tlossw.core.spc.helpers.PeriodCalculations;
import com.likya.tlossw.test.TestSuit;
import com.likya.tlossw.utils.ParsingUtils;

public class TestXMLCalendar extends TestSuit {

	static Logger globalLogger = Logger.getLogger(TestXMLCalendar.class);

	public static void main(String[] args) {
		
		// new TestXMLCalendar().startTestCalendar();
		
		new TestXMLCalendar().startPeriodCalculations();
		
	}

	
	private JobProperties setupCalendar() {
		
		String fileName = ParsingUtils.getConcatenatedPathAndFileName("src" + File.separator, "ExecuteAsProcess.xml");
		JobProperties jobProperties = getJobPropertiesFromFile(fileName);
		
		PeriodInfo periodInfo = jobProperties.getBaseJobInfos().addNewPeriodInfo();
		
		periodInfo.setComment("Comment");
		periodInfo.setCounter(BigInteger.valueOf(0));
		periodInfo.setMaxCount(BigInteger.valueOf(5));
		periodInfo.setStep(new GDuration("PT0H30M0S"));
		
		Calendar jobCalendar = jobProperties.getTimeManagement().getJsPlannedTime().getStartTime().getTime();
		
		Calendar calendar = Calendar.getInstance();
		jobCalendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
		jobCalendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
		jobCalendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
		jobProperties.getTimeManagement().getJsPlannedTime().getStartTime().setDate(jobCalendar);
		
		return jobProperties;
	}
	
	public void startPeriodCalculations() {
		
		JobProperties jobProperties = setupCalendar();
		
		Calendar startTimePart = jobProperties.getTimeManagement().getJsPlannedTime().getStartTime().getTime();	

		System.out.println("Old One : " + startTimePart);
		
		Calendar newDate = PeriodCalculations.forward(jobProperties);
		
		jobProperties.getTimeManagement().getJsPlannedTime().getStartTime().setTime(newDate);
		startTimePart = jobProperties.getTimeManagement().getJsPlannedTime().getStartTime().getTime();
		
		System.out.println("New One : " + startTimePart);
	}
	
	public void startTestCalendar() {

		JobProperties jobProperties = setupCalendar();
		
		Calendar startTimePart = jobProperties.getTimeManagement().getJsPlannedTime().getStartTime().getTime();	
		Calendar startDatePart = jobProperties.getTimeManagement().getJsPlannedTime().getStartTime().getDate();

		System.out.println("startTimePart : " + startTimePart.getTime());
		System.out.println("startDatePart : " + startDatePart.getTime());
		
		startDatePart.set(Calendar.HOUR, startTimePart.get(Calendar.HOUR));
		startDatePart.set(Calendar.MINUTE, startTimePart.get(Calendar.MINUTE));
		startDatePart.set(Calendar.SECOND, startTimePart.get(Calendar.SECOND));
		
		System.out.println();
		System.out.println("startTimePart : " + startTimePart.getTime());
		System.out.println("startDatePart : " + startDatePart.getTime());


		SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		
		String formattedTime = simpleTimeFormat.format(startTimePart.getTime());
		String formattedDate = simpleDateFormat.format(startDatePart.getTime());
		
		System.out.println("startTimePart : " + formattedTime);
		System.out.println("startDatePart : " + formattedDate);
	}
}
