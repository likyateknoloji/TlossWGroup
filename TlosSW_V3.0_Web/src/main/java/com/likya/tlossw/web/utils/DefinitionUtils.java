package com.likya.tlossw.web.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javax.faces.model.SelectItem;

import com.likya.tlos.model.xmlbeans.alarm.DescDocument;
import com.likya.tlos.model.xmlbeans.alarm.EndDateDocument;
import com.likya.tlos.model.xmlbeans.alarm.LevelDocument;
import com.likya.tlos.model.xmlbeans.alarm.StartDateDocument;
import com.likya.tlos.model.xmlbeans.alarm.AlarmDocument.Alarm;
import com.likya.tlos.model.xmlbeans.alarm.CaseManagementDocument.CaseManagement;
import com.likya.tlos.model.xmlbeans.alarm.SystemManagementDocument.SystemManagement;
import com.likya.tlos.model.xmlbeans.alarm.TimeManagementDocument.TimeManagement;
import com.likya.tlos.model.xmlbeans.calendar.ValidFromDocument.ValidFrom;
import com.likya.tlos.model.xmlbeans.calendar.ValidToDocument.ValidTo;
import com.likya.tlos.model.xmlbeans.common.DateDocument.Date;
import com.likya.tlos.model.xmlbeans.common.TimeDocument.Time;

public class DefinitionUtils {

	// yerel saat dilimi ve gun isigindan yararlanma girisleri eski ekranlarda
	// olmadigi icin bu degerleri tek bir zaman ifadesinde birlestirmistim.
	// simdi ayri girisler oldugu icin asagidaki uc metoda ayirdim.
	public static String calendarToStringTime(Calendar date) {

		int localTimeShift = (Calendar.getInstance().getTimeZone().getDSTSavings() + Calendar.getInstance().getTimeZone().getRawOffset()) / 3600000;
		int calendarTimeShift = (date.getTimeZone().getDSTSavings() + date.getTimeZone().getRawOffset()) / 3600000;

		if (localTimeShift == calendarTimeShift) {
			String timeStr = zeroCheck(date.get(Calendar.HOUR_OF_DAY) + "") + ":" + zeroCheck(date.get(Calendar.MINUTE) + "") + ":" + zeroCheck(date.get(Calendar.SECOND) + "");

			return timeStr;
		} else {
			SimpleDateFormat formatter = null;
			formatter = new SimpleDateFormat("HH:mm:ss");

			return formatter.format(date.getTime());
		}
	}

	public static String calendarToStringTimeFormat(Calendar date) {
		String timeStr = zeroCheck(date.get(Calendar.HOUR_OF_DAY) + "") + ":" + zeroCheck(date.get(Calendar.MINUTE) + "") + ":" + zeroCheck(date.get(Calendar.SECOND) + "");
		return timeStr;
	}

	public static int calendarToGMT(Calendar date) {
		int gmt = date.getTimeZone().getRawOffset() / 3600000;
		return gmt;
	}

	public static int calendarToDST(Calendar date) {
		int dst = date.getTimeZone().getDSTSavings() / 3600000;
		return dst;
	}

	@SuppressWarnings("deprecation")
	public static String dateToStringTime(java.util.Date date) {

		String timeStr = zeroCheck(date.getHours() + "") + ":" + zeroCheck(date.getMinutes() + "") + ":" + zeroCheck(date.getSeconds() + "");

		return timeStr;
	}

	@SuppressWarnings("deprecation")
	public static String dateToStringDate(java.util.Date date) {

		String dateStr = zeroCheck(date.getDate() + "") + "." + zeroCheck((date.getMonth() + 1) + "") + "." + zeroCheck((date.getYear() + 1900) + "");

		return dateStr;
	}

	public static Calendar dateTimeToXmlDateTime(java.util.Date date, String time) {

		Calendar timeCalendar = dateToXmlTime(time);
		Calendar dateCalendar = dateToXmlDate(date);

		timeCalendar.set(dateCalendar.get(Calendar.YEAR), dateCalendar.get(Calendar.MONTH), dateCalendar.get(Calendar.DATE));

		return timeCalendar;
	}

	public static Calendar dateToXmlTime(String time) {
		StringTokenizer timeTokenizer = new StringTokenizer(time, ":");
		Integer hour, minute, second;

		hour = Integer.parseInt(timeTokenizer.nextToken());
		minute = Integer.parseInt(timeTokenizer.nextToken());
		second = Integer.parseInt(timeTokenizer.nextToken());

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar;
	}

	// ekrandan girilen saat, saat dilimi ve gun isigindan yararlanma saatine
	// gore sonuc donuyor
	public static Calendar dateToXmlTime(String time, int gmt, boolean dst) {
		StringTokenizer timeTokenizer = new StringTokenizer(time, ":");
		Integer hour, minute, second;

		hour = Integer.parseInt(timeTokenizer.nextToken());
		minute = Integer.parseInt(timeTokenizer.nextToken());
		second = Integer.parseInt(timeTokenizer.nextToken());

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, 0);

		calendar.set(Calendar.ZONE_OFFSET, gmt * 3600000);

		if (dst) {
			calendar.set(Calendar.DST_OFFSET, 3600000);
		}

		return calendar;
	}

	@SuppressWarnings("deprecation")
	public static Calendar dateToXmlDate(java.util.Date date) {
		int day;
		int month;
		int year;

		day = date.getDate();
		month = date.getMonth();
		year = date.getYear() + 1900;

		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);

		return calendar;
	}

	public static java.util.Date xmlDateToDate(Date tlosDate) {
		if (tlosDate == null) {
			return null;
		}

		return tlosDate.getDateValue();
	}

	public static String zeroCheck(String timeValue) {
		if (timeValue.length() == 1) {
			timeValue = "0" + timeValue;
		}

		return timeValue;
	}

	public static boolean dateComparer(ValidTo validTo, ValidFrom validFrom) {
		if (validTo.getDate().after(validFrom.getDate())) {
			return true;
		} else if (validTo.getTime().after(validFrom.getTime())) {
			return true;
		}
		return false;
	}

	/**
	 * Jobin baslangic ve bitis zamanlarini karsilastiriyor.
	 * 
	 * @param stopTime
	 *            jobin bitis zamani
	 * @param startTime
	 *            jobin baslangic zamani
	 * @return bitis zamani baslangic zamanindan sonra ise true, esit ya da once
	 *         ise false donuyor
	 */
	public static boolean dateComparer(Calendar stopTime, Calendar startTime) {

		if (stopTime != null && startTime != null) {
			if (stopTime.getTime().after(startTime.getTime())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Takvim tanimindaki takvimin gecerli oldugu zaman araligini
	 * karsilastiriyor.
	 * 
	 * @param date2
	 *            gecerliligin bittigi tarih, ornegin 2019-12-01
	 * @param time2
	 *            gecerliligin bittigi zaman, ornegin 23:00:00
	 * @param date1
	 *            gecerliligin basladigi tarih, ornegin 2008-12-01
	 * @param time1
	 *            gecerliligin basladigi zaman, ornegin 08:00:00
	 * @return gecerliligin bitis zamani baslangic zamanindan sonra ise true,
	 *         ayni ya da once ise false donuyor
	 */
	public static boolean dateComparer(Date date2, Time time2, Date date1, Time time1) {

		if (date2 != null && date1 != null) {
			if (date2.getCalendarValue().getTime().after(date1.getCalendarValue().getTime())) {
				return true;
			} else if (time2.getCalendarValue().getTime().after(time1.getCalendarValue().getTime())) {
				return true;
			}
		}
		return false;
	}

	public static java.util.Date getCurrentDate() {
		java.util.Date now = Calendar.getInstance().getTime();

		return now;
	}

	public static String getW3CDateTime() {
		java.util.Date date = new java.util.Date();
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
		TimeZone zone = dateFormater.getTimeZone();
		dateFormater.setTimeZone(zone);
		return dateFormater.format(date);
	}

	public static Alarm getAlarmInstance(java.util.Date startDate, java.util.Date endDate) {

		Alarm alarm = Alarm.Factory.newInstance();

		DescDocument descDocument = DescDocument.Factory.newInstance();
		alarm.setDesc(descDocument.getDesc());

		StartDateDocument startDateDocument = StartDateDocument.Factory.newInstance();

		if (startDate != null && !startDate.equals("")) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			alarm.setStartDate(calendar);
		} else {
			alarm.setStartDate(startDateDocument.getStartDate());
		}

		EndDateDocument endDateDocument = EndDateDocument.Factory.newInstance();
		if (endDate != null && !endDate.equals("")) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(endDate);
			alarm.setEndDate(calendar);
		} else {
			alarm.setEndDate(endDateDocument.getEndDate());
		}

		LevelDocument levelDocument = LevelDocument.Factory.newInstance();
		alarm.setLevel(levelDocument.getLevel());

		CaseManagement caseManagement = CaseManagement.Factory.newInstance();
		alarm.setCaseManagement(caseManagement);

		SystemManagement systemManagement = SystemManagement.Factory.newInstance();
		alarm.getCaseManagement().setSystemManagement(systemManagement);

		TimeManagement timeManagement = TimeManagement.Factory.newInstance();
		alarm.getCaseManagement().setTimeManagement(timeManagement);

		alarm.setName(alarm.getName());

		return alarm;
	}

	public static ArrayList<Date> generateDate(List<SelectItem> dateList) {
		ArrayList<Date> generatedDateList = new ArrayList<Date>();

		for (SelectItem item : dateList) {
			Date date = Date.Factory.newInstance();
			String label = item.getLabel();

			StringTokenizer tarihToken = new StringTokenizer(label, ".");

			int day = Integer.parseInt(tarihToken.nextToken());
			int month = Integer.parseInt(tarihToken.nextToken()) - 1;
			int year = Integer.parseInt(tarihToken.nextToken());

			Calendar calendar = Calendar.getInstance();
			calendar.set(year, month, day);

			date.setCalendarValue(calendar);

			generatedDateList.add(date);
		}

		return generatedDateList;
	}

}
