package com.likya.tlossw.web.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.faces.model.SelectItem;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;

import com.likya.tlos.model.xmlbeans.alarm.AlarmDocument.Alarm;
import com.likya.tlos.model.xmlbeans.alarm.CaseManagementDocument.CaseManagement;
import com.likya.tlos.model.xmlbeans.alarm.DescDocument;
import com.likya.tlos.model.xmlbeans.alarm.EndDateDocument;
import com.likya.tlos.model.xmlbeans.alarm.LevelDocument;
import com.likya.tlos.model.xmlbeans.alarm.StartDateDocument;
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

	public static String calendarToStringTimeFormat(Calendar time, String selectedTZone, String timeOutputFormat) {
		//String timeStr = zeroCheck(date.get(Calendar.HOUR_OF_DAY) + "") + ":" + zeroCheck(date.get(Calendar.MINUTE) + "") + ":" + zeroCheck(date.get(Calendar.SECOND) + "");
		
		DateTimeZone zone = DateTimeZone.forID(selectedTZone);
		LocalTime jobLocalTime = new LocalTime( time );
		DateTimeFormatter formatter = DateTimeFormat.forPattern(timeOutputFormat);
		String timeString = jobLocalTime.toDateTimeToday(zone).toString(formatter);
		
//		DateTimeZone zone = DateTimeZone.forID(selectedTZone);
//		DateTimeFormatter dtf = DateTimeFormat.forPattern(timeInputFormat);
//		LocalTime localTime = dtf.parseLocalTime(time.toString());
//		
//		DateTimeFormatter formatter = DateTimeFormat.forPattern(timeOutputFormat);
//		String jobLocalTime = localTime.toDateTimeToday(zone).toString(formatter);
		
		return timeString;
	}

//	public static int calendarToGMT(Calendar date) {
//		int gmt = date.getTimeZone().getRawOffset() / 3600000;
//		return gmt;
//	}
//
//	public static int calendarToDST(Calendar date) {
//		int dst = date.getTimeZone().getDSTSavings() / 3600000;
//		return dst;
//	}

	@SuppressWarnings("deprecation")
	public static String dateToStringTime(java.util.Date date) {

		String timeStr = zeroCheck(date.getHours() + "") + ":" + zeroCheck(date.getMinutes() + "") + ":" + zeroCheck(date.getSeconds() + "");

		return timeStr;
	}

	@SuppressWarnings("deprecation")
	public static String dateToStringDate(java.util.Date date, String selectedTZone) {
		
		DateTimeZone zone = DateTimeZone.forID(selectedTZone);

		String timeOutputFormat = new String("dd.MM.yyyy");
		
		DateTime dateTime = new DateTime(date, zone);
		String dateStr = dateTime.toString(timeOutputFormat);

		//String dateStr = zeroCheck(date.getDate() + "") + "." + zeroCheck((date.getMonth() + 1) + "") + "." + zeroCheck((date.getYear() + 1900) + "");
		// format : 01.07.2009

		return dateStr;
	}

	public static java.util.Date dateToDate(java.util.Date date, String selectedTZone) {
		
		DateTimeZone zone = DateTimeZone.forID(selectedTZone);

//		String timeOutputFormat = new String("dd.MM.yyyy");
		
		DateTime dateTime = new DateTime(date, zone);
		
//		String dateStr = dateTime.toString(timeOutputFormat);

		return dateTime.toDate();
	}
	
	public static Calendar dateTimeToXmlDateTime(java.util.Date date, String time, String selectedTZone) {

		//Calendar timeCalendar = dateToXmlTime(time, selectedTZone);
		Calendar dateCalendar = dateToXmlTime(date, time, selectedTZone);

		//timeCalendar.set(dateCalendar.get(Calendar.YEAR), dateCalendar.get(Calendar.MONTH), dateCalendar.get(Calendar.DATE));

		return dateCalendar;
	}

	public static Calendar dateToXmlTime(String time, String selectedTZone) {
		
		DateTimeZone zonex = DateTimeZone.forID(selectedTZone);
		
		DateTimeParser[] parsers = { 
		        DateTimeFormat.forPattern( "HH:mm:ss.SSSZZ" ).getParser(),
		        DateTimeFormat.forPattern( "HH:mm:ss.SSS" ).getParser(),
		        DateTimeFormat.forPattern( "HH:mm:ss" ).getParser() };
		
//		DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm:ss.SSSZZ");
		DateTimeFormatter dtf = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
		
		LocalTime jobLocalTime = dtf.parseLocalTime(time);
		//LocalTime jobLocalTime = new LocalTime( time, zonex);
		
		LocalDate tx = new LocalDate();
		DateTime dtx = tx.toDateTime(jobLocalTime).toDateTime(zonex);
		
//		StringTokenizer timeTokenizer = new StringTokenizer(time, ":");
//		Integer hour, minute, second, millisecond;
//
//		hour = Integer.parseInt(timeTokenizer.nextToken());
//		minute = Integer.parseInt(timeTokenizer.nextToken());
//		String ek = timeTokenizer.nextToken();
//		if(ek.indexOf(".")>0) {
//			second = Integer.parseInt(ek.substring(0, ek.indexOf(".")-1));
//			millisecond = Integer.parseInt(ek.substring(ek.indexOf(".")+1));			
//		}
//		else second = Integer.parseInt(timeTokenizer.nextToken());
//
//		DateTimeZone zone = DateTimeZone.forID(selectedTZone);
//		LocalTime localTime2 = new LocalTime(hour, minute, second);
//		
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(Calendar.HOUR_OF_DAY, hour);
//		calendar.set(Calendar.MINUTE, minute);
//		calendar.set(Calendar.SECOND, second);
//		calendar.set(Calendar.MILLISECOND, 0);
//
//		LocalDate t = new LocalDate();
//		DateTime dt = t.toDateTime(localTime2).toDateTime(zone);
		
		return dtx.toCalendar(Locale.US);
	}

	// ekrandan girilen saat, saat dilimi ve gun isigindan yararlanma saatine
	// gore sonuc donuyor
	public static Calendar dateToXmlTime(java.util.Date date, String time, String selectedTZone) {
		
//		StringTokenizer timeTokenizer = new StringTokenizer(time, ":");
//		Integer hour, minute, second;
        
		DateTimeZone zone = DateTimeZone.forID(selectedTZone);
		LocalTime jobLocalTime = new LocalTime( time, zone);
		//DateTime jdkDate = new DateTime(localTime2, zone);
		//Calendar timeCalendar = dateToXmlTime(jobCalendar, selectedTZone);

		//startTime = DefinitionUtils.calendarToStringTimeFormat(jobCalendar);
//		String startTime = jobLocalTime.toString();
		
		
//		DateTimeZone zoneUTC = DateTimeZone.UTC;
//		LocalTime localTime = new LocalTime(zoneUTC);

//		hour = Integer.parseInt(timeTokenizer.nextToken());
//		minute = Integer.parseInt(timeTokenizer.nextToken());
//		second = Integer.parseInt(timeTokenizer.nextToken());
//		LocalTime localTime2 = new LocalTime(hour, minute, second);
		// merge, resulting in 2004-25-12T12:20 (default time zone)
		//DateTime dt = new DateTime(date);
		LocalDate t = new LocalDate(date);
		DateTime dt = t.toDateTime(jobLocalTime).toDateTime(zone);

		return dt.toCalendar(Locale.US);
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

	@SuppressWarnings("deprecation")
	public static Calendar dateToXmlDateWithoutZone(java.util.Date date) {
		int day;
		int month;
		int year;

		day = date.getDate();
		month = date.getMonth();
		year = date.getYear() + 1900;

		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);
		calendar.set(Calendar.ZONE_OFFSET, 0);

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

	/**
	 * Takvim tanimindaki takvimin gecerli oldugu zaman araligini karsilastiriyor.
	 * 
	 * @param validTo gecerliligin bittigi zaman
	 * @param validFrom gecerliligin basladigi zaman
	 * @return gecerliligin bitis zamani baslangic zamanindan sonra ise true, ayni ya da once ise false donuyor
	 */
	public static boolean dateComparer(ValidTo validTo, ValidFrom validFrom) {
		if (validTo.getDate().after(validFrom.getDate())) {
			return true;
		} else if (validTo.getDate().equals(validFrom.getDate())) {
			if (validTo.getTime().after(validFrom.getTime())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Jobin baslangic ve bitis zamanlarini karsilastiriyor.
	 * 
	 * @param stopTime jobin bitis zamani
	 * @param startTime jobin baslangic zamani
	 * @return bitis zamani baslangic zamanindan sonra ise true, esit ya da once ise false donuyor
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
	 * Takvim tanimindaki takvimin gecerli oldugu zaman araligini karsilastiriyor.
	 * 
	 * @param date2 gecerliligin bittigi tarih, ornegin 2019-12-01
	 * @param time2 gecerliligin bittigi zaman, ornegin 23:00:00
	 * @param date1 gecerliligin basladigi tarih, ornegin 2008-12-01
	 * @param time1 gecerliligin basladigi zaman, ornegin 08:00:00
	 * @return gecerliligin bitis zamani baslangic zamanindan sonra ise true, ayni ya da once ise false donuyor
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

	public static String getW3CDateTime(String selectedTZone) {

		DateTimeZone zone = DateTimeZone.forID(selectedTZone);
		LocalTime jobLocalTime = new LocalTime(zone);
		LocalDate t = new LocalDate();
		DateTime dt = t.toDateTime(jobLocalTime).toDateTime(zone);

		String outputFormat = new String("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
		String dateStr = dt.toString(outputFormat);
		
		return dateStr;
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

	public static String getTreePath(String treePath) {
		String path = "/dat:TlosProcessData";

		StringTokenizer pathTokenizer = new StringTokenizer(treePath, "/");

		// ilk gelen isim senaryo agacinin koku oldugu icin onu cikariyoruz
		if (pathTokenizer.hasMoreTokens()) {
			pathTokenizer.nextToken();
		}

		while (pathTokenizer.hasMoreTokens()) {
			String scenarioName = pathTokenizer.nextToken();

			if (scenarioName.contains("|")) {
				scenarioName = getXFromNameId(scenarioName, "Name");
			}

			path = path + "/dat:scenario/dat:baseScenarioInfos[com:jsName/text() = '" + scenarioName + "']/..";
		}

		path = path + "/dat:jobList";

		return path;
	}

	public static String getXFromNameId(String nameAndId, String X) {
		StringTokenizer nameTokenizer = new StringTokenizer(nameAndId, "|");
		String name = nameTokenizer.nextToken().trim();
		String id = nameTokenizer.nextToken().trim();

		if(X.equalsIgnoreCase("Name")) return name;
		else if(X.equalsIgnoreCase("Id"))return id;
		else return null;
	}

	public static String toXSString(int intData) {
		return toXSString("" + intData);
	}

	
	public static String toXSString(String stringData) {
		return "xs:string(\"" + stringData + "\"";
	}
	
}
