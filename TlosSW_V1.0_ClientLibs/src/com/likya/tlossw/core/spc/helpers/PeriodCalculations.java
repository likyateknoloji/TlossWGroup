package com.likya.tlossw.core.spc.helpers;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

import org.apache.xmlbeans.GDate;
import org.apache.xmlbeans.GDuration;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.PeriodInfoDocument.PeriodInfo;

public class PeriodCalculations {

	public static Date forward(JobProperties jobProperties) {

		PeriodInfo periodInfo = jobProperties.getBaseJobInfos().getPeriodInfo();

		if (periodInfo.getMaxCount() == null || periodInfo.getCounter().intValue() >= periodInfo.getMaxCount().intValue()) {
			return null;
		}

		periodInfo.setCounter(BigInteger.valueOf(periodInfo.getCounter().intValue() + 1));

		GDuration gDuration = periodInfo.getStep();

		long periodOfRepeatance = getDurationInMilliSecs(gDuration);
		
		Calendar startTime = jobProperties.getTimeManagement().getJsPlannedTime().getStartTime().getTime();
		Date myDate = findNextPeriod(startTime.getTime(), periodOfRepeatance);

		Date myStartTime = changeYMDPart(myDate, startTime.getTime());
		Calendar myStartCalendar = Calendar.getInstance();
		myStartCalendar.setTime(myStartTime);
		jobProperties.getTimeManagement().getJsPlannedTime().getStartTime().setDate(myStartCalendar);

		if (jobProperties.getTimeManagement().getJsPlannedTime().getStopTime() != null) {
			Calendar stopTime = jobProperties.getTimeManagement().getJsPlannedTime().getStopTime().getTime();
			Date myStopTime = changeYMDPart(myDate, stopTime.getTime());
			Calendar myStopCalendar = Calendar.getInstance();
			myStopCalendar.setTime(myStartTime);
			jobProperties.getTimeManagement().getJsPlannedTime().getStopTime().setDate(myStopCalendar);

			// ST : Bu kısım yeninden kurgulanmalı !! boolean notInScheduledDays = Arrays.binarySearch(TlosServer.getTlosParameters().getScheduledDays(), Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) < 0;

			if (!checkStayInDay(myDate) || myDate.after(myStopTime) || myDate.before(myStartTime) /* notInScheduledDays */) {
				// ST : Bu kısım yeninden kurgulanmalı
				// iterateNextDate(jobProperties);
				// myDate = jobProperties.getTime();

				myDate = null;
			}
		}

		return myDate;
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

	private static Date findNextPeriod(Date nextPeriodTime, long period) {

		// zatem ms olarak geliyor period = period * 1000; // Convert to milliseconds
		Date currentTime = Calendar.getInstance().getTime();
		// System.out.println(currentTime + "\n" + nextPeriodTime);

		long diffDate = currentTime.getTime() - nextPeriodTime.getTime();

		if (diffDate < 0) {
			return nextPeriodTime;
		}

		long divDate = diffDate / period;
		// System.out.println(diffDate);
		// System.out.println(divDate * period);
		if ((divDate * period) < diffDate) {
			++divDate;
		}

		long newTime = divDate * period;
		// System.out.println(newTime);
		nextPeriodTime = new Date(nextPeriodTime.getTime() + newTime);
		// System.out.println(nextPeriodTime.getTime() + newTime);

		// System.out.println(nextPeriodTime);
		return nextPeriodTime;
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

	private static boolean checkStayInDay(Date date) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		if (cal.get(Calendar.DAY_OF_MONTH) != Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
			return false;
		}

		return true;
	}

	private static long getDurationInMilliSecs(GDuration gDuration) {

		// GDuration gDuration = new GDuration(durationString);

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);

		GDate base = new GDate(cal);
		GDate d = base.add(gDuration);

		long durationInMillis = d.getDate().getTime();

		return durationInMillis;
	}

}
