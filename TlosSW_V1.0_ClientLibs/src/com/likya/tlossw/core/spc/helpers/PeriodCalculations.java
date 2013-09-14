package com.likya.tlossw.core.spc.helpers;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.xmlbeans.GDate;
import org.apache.xmlbeans.GDuration;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.PeriodInfoDocument.PeriodInfo;
import com.likya.tlos.model.xmlbeans.data.TimeManagementDocument.TimeManagement;

public class PeriodCalculations {

	public static Calendar forward(JobProperties jobProperties) {

		PeriodInfo periodInfo = jobProperties.getBaseJobInfos().getPeriodInfo();
		TimeManagement timeManagement = jobProperties.getTimeManagement();

		String selectedTZone = timeManagement.getTimeZone();
		Calendar startTime = timeManagement.getJsPlannedTime().getStartTime().getTime();

		if (periodInfo.getMaxCount() == null || periodInfo.getCounter().intValue() >= periodInfo.getMaxCount().intValue()) {
			return null;
		}

		periodInfo.setCounter(BigInteger.valueOf(periodInfo.getCounter().intValue() + 1));

		GDuration gDuration = periodInfo.getStep();

		long periodOfRepeatance = getDurationInMilliSecs(gDuration);

		Calendar startDateTime = dateToXmlTime(startTime.toString(), selectedTZone);

		// Alternative way of finding datetime from time with timeZone
		// String startDateTimeStr = calendarToStringTimeFormat(startTime, selectedTZone, timeOutputFormat);

		Calendar newDateTime = findNextPeriod(startDateTime, periodOfRepeatance, selectedTZone, periodInfo.getRelativeStart());

		jobProperties.getTimeManagement().getJsPlannedTime().getStartTime().setTime(newDateTime);

		if (jobProperties.getTimeManagement().getJsPlannedTime().getStopTime() != null) {
			Calendar stopTime = jobProperties.getTimeManagement().getJsPlannedTime().getStopTime().getTime();
			Calendar stopDateTime = dateToXmlTime(stopTime.toString(), selectedTZone);

			jobProperties.getTimeManagement().getJsPlannedTime().getStopTime().setTime(stopDateTime);

			// Serkan burayi konusalim. Simdilik cikardim. Hs

			// ST : Bu kısım yeninden kurgulanmalı !! boolean notInScheduledDays = Arrays.binarySearch(TlosServer.getTlosParameters().getScheduledDays(), Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) < 0;

			//			if (!checkStayInDay(newDateTime) || newDateTime.after(myStopTime) || newDateTime.before(myStartTime) /* notInScheduledDays */) {
			//				// ST : Bu kısım yeninden kurgulanmalı
			//				// iterateNextDate(jobProperties);
			//				// myDate = jobProperties.getTime();
			//
			//				myDate = null;
			//			}
		}

		return newDateTime;
	}

	/*
	 * public static Date findRangedNextPeriod(JobProperties jobProperties) {
	 * 
	 * Date myDate = findNextPeriod(jobProperties.getTime(), jobProperties.getPeriodTime());
	 * 
	 * jobProperties.setJobPlannedEndTime(changeYMDPart(myDate,
	 * jobProperties.getJobPlannedEndTime()));
	 * jobProperties.setJobPlannedStartTime(changeYMDPart(myDate,
	 * jobProperties.getJobPlannedStartTime()));
	 * 
	 * boolean notInScheduledDays =
	 * Arrays.binarySearch(TlosServer.getTlosParameters().getScheduledDays(),
	 * Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) < 0;
	 * 
	 * if (!checkStayInDay(myDate) || myDate.after(jobProperties.getJobPlannedEndTime()) ||
	 * myDate.before(jobProperties.getJobPlannedStartTime()) || notInScheduledDays) {
	 * iterateNextDate(jobProperties); myDate = jobProperties.getTime(); }
	 * 
	 * return myDate; }
	 */

	/*
	 * public static void iterateNextDate(JobProperties jobProperties) {
	 * 
	 * // Date tmpDate = jobProperties.getTime(); Date scenarioDate =
	 * jobProperties.getScenarioTime(); Date executionDate = jobProperties.getTime(); Calendar
	 * tmpCal = Calendar.getInstance(); tmpCal.setTime(scenarioDate);
	 * 
	 * RestrictedDailyIterator restrictedDailyIterator = new
	 * RestrictedDailyIterator(tmpCal.get(Calendar.HOUR_OF_DAY), tmpCal.get(Calendar.MINUTE),
	 * tmpCal.get(Calendar.SECOND), TlosServer.getTlosParameters().getScheduledDays());
	 * jobProperties.setPreviousTime(DateUtils.getDate(executionDate));
	 * jobProperties.setTime(restrictedDailyIterator.next()); }
	 */

	private static Calendar findNextPeriod(Calendar startDateTime, long period, String selectedTZone, boolean isRelativeStart) {

		Date currentTime = Calendar.getInstance().getTime();
		
		if (isRelativeStart) {
			startDateTime.setTime(currentTime);
		} else {
			
			long diffDate = currentTime.getTime() - startDateTime.getTimeInMillis();

			if (diffDate < 0) {
				return startDateTime;
			}

			long divDate = diffDate / period;

			if ((divDate * period) < diffDate) {
				++divDate;
			}

			period = divDate * period;

		}
		
		Calendar returnCal = addPeriod(startDateTime, period, selectedTZone);

		return returnCal;

	}

	private static Calendar addPeriod(Calendar startDateTime, long period, String selectedTZone) {

		DateTimeZone zonex = DateTimeZone.forID(selectedTZone);

		// construct DateTime from JDK Date
		DateTime dt = new DateTime(startDateTime, zonex);

		Period periodInJoda = new Period(period);

		DateTime newDateTime = dt.plus(periodInJoda);

		//String outputFormat = new String("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
		//String dateStr = newDateTime.toString(outputFormat);

		return newDateTime.toCalendar(Locale.ENGLISH);
	}

	public static Date changeYMDPart(Date firstDate, Date secondDate) {

		Calendar calendarFirst = Calendar.getInstance();
		calendarFirst.setTime(firstDate);

		Calendar calendarSecond = Calendar.getInstance();
		calendarSecond.setTime(secondDate);

		calendarSecond.set(Calendar.YEAR, calendarFirst.get(Calendar.YEAR));
		calendarSecond.set(Calendar.MONTH, calendarFirst.get(Calendar.MONTH));
		calendarSecond.set(Calendar.DAY_OF_MONTH, calendarFirst.get(Calendar.DAY_OF_MONTH));

		return calendarSecond.getTime();
	}

	//	private static boolean checkStayInDay(Date date) {
	//
	//		Calendar cal = Calendar.getInstance();
	//		cal.setTime(date);
	//
	//		if (cal.get(Calendar.DAY_OF_MONTH) != Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
	//			return false;
	//		}
	//
	//		return true;
	//	}

	private static long getDurationInMilliSecs(GDuration gDuration) {

		// GDuration gDuration = new GDuration(durationString);

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);

		GDate base = new GDate(cal);
		GDate d = base.add(gDuration);

		long durationInMillis = d.getDate().getTime();

		return durationInMillis;
	}

	public static Calendar dateToXmlTime(String time, String selectedTZone) {

		DateTimeZone zonex = DateTimeZone.forID(selectedTZone);

		DateTimeParser[] parsers = { DateTimeFormat.forPattern("HH:mm:ss.SSSZZ").getParser(), DateTimeFormat.forPattern("HH:mm:ss.SSS").getParser(), DateTimeFormat.forPattern("HH:mm:ss").getParser() };

		DateTimeFormatter dtf = new DateTimeFormatterBuilder().append(null, parsers).toFormatter();

		LocalTime jobLocalTime = dtf.parseLocalTime(time);

		LocalDate tx = new LocalDate(zonex);
		DateTime dtx = tx.toDateTime(jobLocalTime, zonex);

		return dtx.toCalendar(Locale.US);
	}

	// Alternative way of finding datetime from time with timeZone
	public static String calendarToStringTimeFormat(Calendar time, String selectedTZone, String timeOutputFormat) {

		DateTimeZone zone = DateTimeZone.forID(selectedTZone);
		LocalTime jobLocalTime = new LocalTime(time);
		DateTimeFormatter formatter = DateTimeFormat.forPattern(timeOutputFormat);
		String timeString = jobLocalTime.toDateTimeToday(zone).toString(formatter);

		return timeString;
	}

}
