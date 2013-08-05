package com.likya.tlossw.utils.date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;

import com.likya.tlos.model.xmlbeans.common.TypeOfTimeDocument.TypeOfTime;
import com.likya.tlos.model.xmlbeans.data.JsRealTimeDocument.JsRealTime;

public class DateUtils {

	@Deprecated
	/**
	 * Use one of the new methods getFormattedDuration or getUnFormattedDuration
	 * getDuration is mapped to getFormattedDuration due to the backward compatibility
	 * restrictions
	 * @param sDate
	 * @return
	 */
	public static String getDuration(Date sDate) {
		return getUnFormattedDuration(sDate);
	}

	public static long dateDiffWithNow(Date sDate) {

		Date now = Calendar.getInstance().getTime();
		long timeDiff = now.getTime() - sDate.getTime();

		return timeDiff;
	}

	public static long dateDiffWithNow(long sDate) {

		Date now = Calendar.getInstance().getTime();
		long timeDiff = now.getTime() - sDate;

		return timeDiff;
	}

	public static String getFormattedDuration(Date sDate) {
		if (sDate == null) {
			return null;
		}
		return DateUtils.getFormattedElapsedTime((int) dateDiffWithNow(sDate) / 1000);
	}

	public static String getUnFormattedDuration(Date sDate) {
		if (sDate == null) {
			return null;
		}
		return DateUtils.getUnFormattedElapsedTime((int) dateDiffWithNow(sDate) / 1000);
	}

	public static String getDate(Date executionTime) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		return formatter.format(executionTime);
	}

	public static String getTime(Date executionTime) {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		return formatter.format(executionTime);
	}

	/*
	 * public static String getCurrentGMTDate() {
	 * final int msInMin = 60000;
	 * final int minInHr = 60;
	 * Date date = new Date();
	 * int Hours, Minutes;
	 * // DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG );
	 * DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.DATE_FIELD, DateFormat.LONG, new Locale("EN") );
	 * TimeZone zone = dateFormat.getTimeZone();
	 * 
	 * Minutes =zone.getOffset( date.getTime() ) / msInMin;
	 * Hours = Minutes / minInHr;
	 * zone = zone.getTimeZone( "GMT Time" +(Hours>=0?"+":"")+Hours+":"+ Minutes);
	 * dateFormat.setTimeZone( zone );
	 * return dateFormat.format( date )+"+"+Hours;
	 * }
	 */

	public static String getFormattedElapsedTimeMS(long timeInMilliSeconds) {

		long hours, minutes, seconds, milliseconds;

		hours = timeInMilliSeconds / (1000 * 3600);

		timeInMilliSeconds = timeInMilliSeconds - (hours * 1000 * 3600);

		minutes = timeInMilliSeconds / (1000 * 60);

		timeInMilliSeconds = timeInMilliSeconds - (minutes * 1000 * 60);

		seconds = timeInMilliSeconds / 1000;

		timeInMilliSeconds = timeInMilliSeconds - (seconds * 1000);

		milliseconds = timeInMilliSeconds;

		// System.out.println(hours + " hour(s) " + minutes + " minute(s) " +
		// seconds + " second(s)");

		return hours + " saat " + minutes + " dakika " + seconds + " saniye " + milliseconds + " milisaniye";
	}

	public static String getFormattedElapsedTime(int timeInSeconds) {
		int hours, minutes, seconds;
		hours = timeInSeconds / 3600;
		timeInSeconds = timeInSeconds - (hours * 3600);
		minutes = timeInSeconds / 60;
		timeInSeconds = timeInSeconds - (minutes * 60);
		seconds = timeInSeconds;
		// System.out.println(hours + " hour(s) " + minutes + " minute(s) " +
		// seconds + " second(s)");

		return hours + " saat " + minutes + " dakika " + seconds + " saniye";
	}

	public static String getUnFormattedElapsedTime(int timeInSeconds) {
		return getUnFormattedElapsedTime((long) timeInSeconds);
	}

	public static String getUnFormattedElapsedTime(long timeInSeconds) {
		long hours, minutes, seconds;

		hours = timeInSeconds / 3600;
		timeInSeconds = timeInSeconds - (hours * 3600);
		minutes = timeInSeconds / 60;
		timeInSeconds = timeInSeconds - (minutes * 60);
		seconds = timeInSeconds;
		// System.out.println(hours + " hour(s) " + minutes + " minute(s) " +
		// seconds + " second(s)");

		return getDigitStr(hours) + ":" + getDigitStr(minutes) + ":" + getDigitStr(seconds);
	}

	private static String getDigitStr(long digit) {
		if (digit == 0) {
			return "00";
		} else if (digit < 10) {
			return "0" + digit;
		} else {
			return "" + digit;
		}
	}

	public static Date getDateTime(String dateTimeInString) {

		// You want to convert a string reprenting a time into a time object
		// in Java. As we know that Java is representing a time information
		// in a class java.util.Date, this class keep information about date
		// and time.

		// Now if you have a string of time you can use a SimpleDateFormat
		// object to parse the string date and return a date object. The pattern
		// of the string should be passed to the simple date format constructor.
		// In the example below the string is formatted as hh:mm:ss
		// (hour:minutes:
		// second).

		DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		Date dateTime = null;

		try {
			// The get the date object from the string just called the parse
			// method and pass the time string to it. The method throws
			// ParseException
			// if the time string is in an invalid format. But remember as we
			// don't
			// pass the date information this date object will represent the 1st
			// of

			dateTime = dateTimeFormat.parse(dateTimeInString);

			// As the parse process success we'll have our time string in the
			// created date instance.

			// System.out.println("Date and Time: " + date);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return dateTime;

	}

	public static Date getTime(String timeInString) {

		// You want to convert a string reprenting a time into a time object
		// in Java. As we know that Java is representing a time information
		// in a class java.util.Date, this class keep information about date
		// and time.

		// Now if you have a string of time you can use a SimpleDateFormat
		// object to parse the string date and return a date object. The pattern
		// of the string should be passed to the simple date format constructor.
		// In the example below the string is formatted as hh:mm:ss
		// (hour:minutes:
		// second).

		DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
		Date date = null;

		try {
			// The get the date object from the string just called the parse
			// method and pass the time string to it. The method throws
			// ParseException
			// if the time string is in an invalid format. But remember as we
			// don't
			// pass the date information this date object will represent the 1st
			// of

			date = dateFormat.parse(timeInString);

			// As the parse process success we'll have our time string in the
			// created date instance.

			// System.out.println("Date and Time: " + date);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return date;

	}

	public static Calendar getSolsticeDateTime(Calendar now, Calendar solsticeCalendar) {

		now.set(Calendar.HOUR_OF_DAY, solsticeCalendar.get(Calendar.HOUR_OF_DAY));
		now.set(Calendar.MINUTE, solsticeCalendar.get(Calendar.MINUTE));
		now.set(Calendar.SECOND, solsticeCalendar.get(Calendar.SECOND));

		return now;
	}

	/*
	 * public static Date xmlbeansToNative(Time myTime) {
	 * 
	 * Calendar tmpCalendar = Calendar.getInstance();
	 * 
	 * tmpCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(myTime.getHour()));
	 * tmpCalendar.set(Calendar.MINUTE, Integer.parseInt(myTime.getMinute()));
	 * tmpCalendar.set(Calendar.SECOND, Integer.parseInt(myTime.getSecond()));
	 * 
	 * return tmpCalendar.getTime();
	 * }
	 * 
	 * public static Date castorToNative(Time myTime) {
	 * 
	 * Calendar tmpCalendar = Calendar.getInstance();
	 * 
	 * tmpCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(myTime.getHour()));
	 * tmpCalendar.set(Calendar.MINUTE, Integer.parseInt(myTime.getMinute()));
	 * tmpCalendar.set(Calendar.SECOND, Integer.parseInt(myTime.getSecond()));
	 * 
	 * return tmpCalendar.getTime();
	 * }
	 */

	public static Calendar normalizeDate(Calendar calendar) {

		Calendar tmpCalendar = Calendar.getInstance();

		tmpCalendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
		tmpCalendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
		tmpCalendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND));

		return tmpCalendar;
	}

	/*
	 * public static Time nativeToCastor(Date myDate) {
	 * 
	 * Time myTime = Time.Factory.newInstance();
	 * 
	 * Calendar tmpCalendar = Calendar.getInstance();
	 * tmpCalendar.setTime(myDate);
	 * 
	 * myTime.setHour("" + tmpCalendar.get(Calendar.HOUR_OF_DAY));
	 * myTime.setMinute("" + tmpCalendar.get(Calendar.MINUTE));
	 * myTime.setSecond("" + tmpCalendar.get(Calendar.SECOND));
	 * 
	 * return myTime;
	 * }
	 * 
	 * public static com.likya.tlos.model.xmlbeans.common.TimeDocument.Time nativeToXMLBeansTime(Date myDate) {
	 * 
	 * com.likya.tlos.model.xmlbeans.common.TimeDocument.Time myTime = com.likya.tlos.model.xmlbeans.common.TimeDocument.Time.Factory.newInstance();
	 * 
	 * Calendar tmpCalendar = Calendar.getInstance();
	 * tmpCalendar.setTime(myDate);
	 * 
	 * myTime.setHour(appendZero(tmpCalendar.get(Calendar.HOUR_OF_DAY)));
	 * myTime.setMinute(appendZero(tmpCalendar.get(Calendar.MINUTE)));
	 * myTime.setSecond(appendZero(tmpCalendar.get(Calendar.SECOND)));
	 * 
	 * return myTime;
	 * }
	 * 
	 * public static com.likya.tlos.model.xmlbeans.common.DateDocument.Date nativeToXMLBeansDate(Date myDate) {
	 * 
	 * com.likya.tlos.model.xmlbeans.common.DateDocument.Date xmlDate = com.likya.tlos.model.xmlbeans.common.DateDocument.Date.Factory.newInstance();
	 * 
	 * Calendar tmpCalendar = Calendar.getInstance();
	 * tmpCalendar.setTime(myDate);
	 * 
	 * xmlDate.setDay(appendZero(tmpCalendar.get(Calendar.DAY_OF_MONTH)));
	 * xmlDate.setMonth(appendZero((tmpCalendar.get(Calendar.MONTH) + 1)));
	 * xmlDate.setYear("" + tmpCalendar.get(Calendar.YEAR));
	 * 
	 * return xmlDate;
	 * }
	 * 
	 * public static com.likya.tlos.model.xmlbeans.data.StartTimeDocument.StartTime nativeDateToXMLStartTime(Date myDate){
	 * com.likya.tlos.model.xmlbeans.data.StartTimeDocument.StartTime startTime = com.likya.tlos.model.xmlbeans.data.StartTimeDocument.StartTime.Factory.newInstance();
	 * com.likya.tlos.model.xmlbeans.common.DateDocument.Date xmlDate = com.likya.tlos.model.xmlbeans.common.DateDocument.Date.Factory.newInstance();
	 * com.likya.tlos.model.xmlbeans.common.TimeDocument.Time xmlTime = com.likya.tlos.model.xmlbeans.common.TimeDocument.Time.Factory.newInstance();
	 * 
	 * xmlDate = nativeToXMLBeansDate(myDate);
	 * xmlTime = nativeToXMLBeansTime(myDate);
	 * 
	 * startTime.setDate(xmlDate);
	 * startTime.setTime(xmlTime);
	 * 
	 * return startTime;
	 * }
	 * 
	 * public static com.likya.tlos.model.xmlbeans.data.StopTimeDocument.StopTime nativeDateToXMLStopTime(Date myDate){
	 * com.likya.tlos.model.xmlbeans.data.StopTimeDocument.StopTime stopTime = com.likya.tlos.model.xmlbeans.data.StopTimeDocument.StopTime.Factory.newInstance();
	 * com.likya.tlos.model.xmlbeans.common.DateDocument.Date xmlDate = com.likya.tlos.model.xmlbeans.common.DateDocument.Date.Factory.newInstance();
	 * com.likya.tlos.model.xmlbeans.common.TimeDocument.Time xmlTime = com.likya.tlos.model.xmlbeans.common.TimeDocument.Time.Factory.newInstance();
	 * 
	 * xmlDate = nativeToXMLBeansDate(myDate);
	 * xmlTime = nativeToXMLBeansTime(myDate);
	 * 
	 * stopTime.setDate(xmlDate);
	 * stopTime.setTime(xmlTime);
	 * 
	 * return stopTime;
	 * }
	 */

	/*
	 * public static String jobTimeToString(JobPlannedTime jobPlannedTime, boolean startTime) {
	 * 
	 * String jobTime = "";
	 * 
	 * if (startTime) {
	 * if (jobPlannedTime.getStartTime().getDate() != null) {
	 * jobTime = jobPlannedTime.getStartTime().getDate().getDay() + "." + jobPlannedTime.getStartTime().getDate().getMonth() + "." + jobPlannedTime.getStartTime().getDate().getYear() + " ";
	 * }
	 * 
	 * jobTime = jobTime + jobPlannedTime.getStartTime().getTime().getHour() + ":" + jobPlannedTime.getStartTime().getTime().getMinute() + ":" + jobPlannedTime.getStartTime().getTime().getSecond();
	 * } else {
	 * if (jobPlannedTime.getStopTime().getDate() != null) {
	 * jobTime = jobPlannedTime.getStopTime().getDate().getDay() + "." + jobPlannedTime.getStopTime().getDate().getMonth() + "." + jobPlannedTime.getStopTime().getDate().getYear() + " ";
	 * }
	 * 
	 * jobTime = jobTime + jobPlannedTime.getStopTime().getTime().getHour() + ":" + jobPlannedTime.getStopTime().getTime().getMinute() + ":" + jobPlannedTime.getStopTime().getTime().getSecond();
	 * }
	 * return jobTime;
	 * }
	 */

	public static String calendarToString(Calendar date, boolean transformToLocalTime) {
		// clienttan alacagimiz degerlerle dolduracagiz
		int clientZoneOffset = 7200000; // milisecond
		int clientDSTOffset = 3600000; // milisecond

		String stringDate = "";

		if (!transformToLocalTime) {

			stringDate = appendZero(date.get(Calendar.DATE)) + "." + appendZero(date.get(Calendar.MONTH) + 1) + "." + date.get(Calendar.YEAR);
			stringDate += " " + appendZero(date.get(Calendar.HOUR_OF_DAY)) + ":" + appendZero(date.get(Calendar.MINUTE)) + ":" + appendZero(date.get(Calendar.SECOND));

			int localTimeShift = clientDSTOffset + clientZoneOffset;
			int dateTimeShift = date.get(Calendar.DST_OFFSET) + date.get(Calendar.ZONE_OFFSET);

			if (localTimeShift != dateTimeShift) {
				stringDate += getTimeZoneStr(dateTimeShift);
			}

		} else {
			SimpleDateFormat formatter = null;
			formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

			TimeZone timeZone = TimeZone.getDefault();
			if (clientDSTOffset == timeZone.getDSTSavings()) {
				timeZone.setRawOffset(clientZoneOffset);
			} else if (clientDSTOffset > timeZone.getDSTSavings()) {
				timeZone.setRawOffset(clientZoneOffset + clientDSTOffset);
			} else {
				timeZone.setRawOffset(clientZoneOffset - timeZone.getDSTSavings());
			}
			formatter.setTimeZone(timeZone);

			stringDate = formatter.format(date.getTime());
		}

		return stringDate;
	}

	public static String jobTimeToString(Calendar timeCalendar, boolean transformToLocalTime) {

		int clientZoneOffset = 7200000; // milisecond
		int clientDSTOffset = 3600000; // milisecond

		Calendar jobCalendar = Calendar.getInstance();

		jobCalendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
		jobCalendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
		jobCalendar.set(Calendar.SECOND, timeCalendar.get(Calendar.SECOND));
		jobCalendar.set(Calendar.ZONE_OFFSET, timeCalendar.get(Calendar.ZONE_OFFSET));
		jobCalendar.set(Calendar.DST_OFFSET, 0);
		
		int localTimeShift = clientDSTOffset + clientZoneOffset;
		int jobTimeShift = jobCalendar.get(Calendar.ZONE_OFFSET);

		if (!transformToLocalTime) {
			String jobTime = appendZero(jobCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + appendZero(jobCalendar.get(Calendar.MINUTE)) + ":" + appendZero(jobCalendar.get(Calendar.SECOND));

			if (jobTimeShift != localTimeShift) {
				jobTime += getTimeZoneStr(jobTimeShift);
			}

			return jobTime;

		} else {
			SimpleDateFormat formatter = null;

			// formatter = new SimpleDateFormat("HH:mm:ss Z");
			formatter = new SimpleDateFormat("HH:mm:ss");

			TimeZone timeZone = TimeZone.getDefault();
			if (clientDSTOffset == timeZone.getDSTSavings()) {
				timeZone.setRawOffset(clientZoneOffset);
			} else if (clientDSTOffset > timeZone.getDSTSavings()) {
				timeZone.setRawOffset(clientZoneOffset + clientDSTOffset);
			} else {
				timeZone.setRawOffset(clientZoneOffset - timeZone.getDSTSavings());
			}
			formatter.setTimeZone(timeZone);

			return formatter.format(jobCalendar.getTime());
		}
	}

	/*
	public static String jobTimeToString(JsPlannedTime jobPlannedTime, boolean startTime, boolean transformToLocalTime) {
		// clienttan alacagimiz degerlerle dolduracagiz
		int clientZoneOffset = 7200000; // milisecond
		int clientDSTOffset = 3600000; // milisecond

		Calendar jobCalendar = Calendar.getInstance();

		if (startTime) {
			// jobCalendar = jobPlannedTime.getStartTime().getTime();
			jobCalendar.set(Calendar.HOUR_OF_DAY, jobPlannedTime.getStartTime().getTime().get(Calendar.HOUR_OF_DAY));
			jobCalendar.set(Calendar.MINUTE, jobPlannedTime.getStartTime().getTime().get(Calendar.MINUTE));
			jobCalendar.set(Calendar.SECOND, jobPlannedTime.getStartTime().getTime().get(Calendar.SECOND));
			jobCalendar.set(Calendar.ZONE_OFFSET, jobPlannedTime.getStartTime().getTime().get(Calendar.ZONE_OFFSET));
			jobCalendar.set(Calendar.DST_OFFSET, 0);
		} else {
			// jobCalendar = jobPlannedTime.getStopTime().getTime();
			jobCalendar.set(Calendar.HOUR_OF_DAY, jobPlannedTime.getStopTime().getTime().get(Calendar.HOUR_OF_DAY));
			jobCalendar.set(Calendar.MINUTE, jobPlannedTime.getStopTime().getTime().get(Calendar.MINUTE));
			jobCalendar.set(Calendar.SECOND, jobPlannedTime.getStopTime().getTime().get(Calendar.SECOND));
			jobCalendar.set(Calendar.ZONE_OFFSET, jobPlannedTime.getStopTime().getTime().get(Calendar.ZONE_OFFSET));
			jobCalendar.set(Calendar.DST_OFFSET, 0);
		}

		int localTimeShift = clientDSTOffset + clientZoneOffset;
		int jobTimeShift = jobCalendar.get(Calendar.ZONE_OFFSET);

		if (!transformToLocalTime) {
			String jobTime = appendZero(jobCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + appendZero(jobCalendar.get(Calendar.MINUTE)) + ":" + appendZero(jobCalendar.get(Calendar.SECOND));

			if (jobTimeShift != localTimeShift) {
				jobTime += getTimeZoneStr(jobTimeShift);
			}

			return jobTime;

		} else {
			SimpleDateFormat formatter = null;

			// formatter = new SimpleDateFormat("HH:mm:ss Z");
			formatter = new SimpleDateFormat("HH:mm:ss");

			TimeZone timeZone = TimeZone.getDefault();
			if (clientDSTOffset == timeZone.getDSTSavings()) {
				timeZone.setRawOffset(clientZoneOffset);
			} else if (clientDSTOffset > timeZone.getDSTSavings()) {
				timeZone.setRawOffset(clientZoneOffset + clientDSTOffset);
			} else {
				timeZone.setRawOffset(clientZoneOffset - timeZone.getDSTSavings());
			}
			formatter.setTimeZone(timeZone);

			return formatter.format(jobCalendar.getTime());
		}
	}
	
	*/

	private static String getTimeZoneStr(int timeShift) {

		String timeZome = "";

		if (timeShift > 0) {
			timeZome += "+";

		} else {
			timeZome += "-";

			timeShift = timeShift * (-1);
		}

		int zoneHour = timeShift / 3600000;
		int zoneMin = (timeShift - zoneHour * 3600000) / 60000;

		timeZome += appendZero(zoneHour) + ":" + appendZero(zoneMin);

		return timeZome;
	}

	// jobin gercek calisma zamani verildiginde string degerini donuyor. gercek calisma zamani icinde tarih olmadigi icin bugunun tarihi set ediliyor
	public static String jobRealTimeToString(JsRealTime jobRealTime, boolean startTime, boolean transformToLocalTime) {
		// clienttan alacagimiz degerlerle dolduracagiz
		int clientZoneOffset = 7200000; // milisecond
		int clientDSTOffset = 3600000; // milisecond

		Calendar jobCalendar = Calendar.getInstance();

		if (startTime) {
			jobCalendar.set(Calendar.HOUR_OF_DAY, jobRealTime.getStartTime().getTime().get(Calendar.HOUR_OF_DAY));
			jobCalendar.set(Calendar.MINUTE, jobRealTime.getStartTime().getTime().get(Calendar.MINUTE));
			jobCalendar.set(Calendar.SECOND, jobRealTime.getStartTime().getTime().get(Calendar.SECOND));
			jobCalendar.set(Calendar.ZONE_OFFSET, jobRealTime.getStartTime().getTime().get(Calendar.ZONE_OFFSET));
			jobCalendar.set(Calendar.DST_OFFSET, 0);
		} else {
			jobCalendar.set(Calendar.HOUR_OF_DAY, jobRealTime.getStopTime().getTime().get(Calendar.HOUR_OF_DAY));
			jobCalendar.set(Calendar.MINUTE, jobRealTime.getStopTime().getTime().get(Calendar.MINUTE));
			jobCalendar.set(Calendar.SECOND, jobRealTime.getStopTime().getTime().get(Calendar.SECOND));
			jobCalendar.set(Calendar.ZONE_OFFSET, jobRealTime.getStopTime().getTime().get(Calendar.ZONE_OFFSET));
			jobCalendar.set(Calendar.DST_OFFSET, 0);
		}

		int localTimeShift = clientDSTOffset + clientZoneOffset;
		int jobTimeShift = jobCalendar.get(Calendar.ZONE_OFFSET);

		if (!transformToLocalTime) {
			String todayDate = appendZero(jobCalendar.get(Calendar.DATE)) + "." + appendZero(jobCalendar.get(Calendar.MONTH) + 1) + "." + jobCalendar.get(Calendar.YEAR);
			String jobTime = todayDate + " " + appendZero(jobCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + appendZero(jobCalendar.get(Calendar.MINUTE)) + ":" + appendZero(jobCalendar.get(Calendar.SECOND));

			if (jobTimeShift != localTimeShift) {
				jobTime += getTimeZoneStr(jobTimeShift);
			}

			return jobTime;

		} else {
			SimpleDateFormat formatter = null;

			// formatter = new SimpleDateFormat("HH:mm:ss Z");
			formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

			TimeZone timeZone = TimeZone.getDefault();
			if (clientDSTOffset == timeZone.getDSTSavings()) {
				timeZone.setRawOffset(clientZoneOffset);
			} else if (clientDSTOffset > timeZone.getDSTSavings()) {
				timeZone.setRawOffset(clientZoneOffset + clientDSTOffset);
			} else {
				timeZone.setRawOffset(clientZoneOffset - timeZone.getDSTSavings());
			}
			formatter.setTimeZone(timeZone);

			return formatter.format(jobCalendar.getTime());
		}
	}

	public static String jobRealTimeToStringReport(JsRealTime jobRealTime, boolean startTime, boolean transformToLocalTime) {
		// clienttan alacagimiz degerlerle dolduracagiz
		int clientZoneOffset = 7200000; // milisecond
		int clientDSTOffset = 3600000; // milisecond

		Calendar jobCalendar = Calendar.getInstance();

		if (startTime) {
			jobCalendar.set(Calendar.YEAR, jobRealTime.getStartTime().getDate().get(Calendar.YEAR));
			jobCalendar.set(Calendar.MONTH, jobRealTime.getStartTime().getDate().get(Calendar.MONTH));
			jobCalendar.set(Calendar.DAY_OF_MONTH, jobRealTime.getStartTime().getDate().get(Calendar.DAY_OF_MONTH));
			jobCalendar.set(Calendar.HOUR_OF_DAY, jobRealTime.getStartTime().getTime().get(Calendar.HOUR_OF_DAY));
			jobCalendar.set(Calendar.MINUTE, jobRealTime.getStartTime().getTime().get(Calendar.MINUTE));
			jobCalendar.set(Calendar.SECOND, jobRealTime.getStartTime().getTime().get(Calendar.SECOND));
			jobCalendar.set(Calendar.ZONE_OFFSET, jobRealTime.getStartTime().getTime().get(Calendar.ZONE_OFFSET));
			jobCalendar.set(Calendar.DST_OFFSET, 0);
		} else {
			jobCalendar.set(Calendar.YEAR, jobRealTime.getStartTime().getDate().get(Calendar.YEAR));
			jobCalendar.set(Calendar.MONTH, jobRealTime.getStartTime().getDate().get(Calendar.MONTH));
			jobCalendar.set(Calendar.DAY_OF_MONTH, jobRealTime.getStartTime().getDate().get(Calendar.DAY_OF_MONTH));
			jobCalendar.set(Calendar.HOUR_OF_DAY, jobRealTime.getStopTime().getTime().get(Calendar.HOUR_OF_DAY));
			jobCalendar.set(Calendar.MINUTE, jobRealTime.getStopTime().getTime().get(Calendar.MINUTE));
			jobCalendar.set(Calendar.SECOND, jobRealTime.getStopTime().getTime().get(Calendar.SECOND));
			jobCalendar.set(Calendar.ZONE_OFFSET, jobRealTime.getStopTime().getTime().get(Calendar.ZONE_OFFSET));
			jobCalendar.set(Calendar.DST_OFFSET, 0);
		}

		int localTimeShift = clientDSTOffset + clientZoneOffset;
		int jobTimeShift = jobCalendar.get(Calendar.ZONE_OFFSET);

		if (!transformToLocalTime) {
			String todayDate = appendZero(jobCalendar.get(Calendar.DATE)) + "." + appendZero(jobCalendar.get(Calendar.MONTH) + 1) + "." + jobCalendar.get(Calendar.YEAR);
			String jobTime = todayDate + " " + appendZero(jobCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + appendZero(jobCalendar.get(Calendar.MINUTE)) + ":" + appendZero(jobCalendar.get(Calendar.SECOND));

			if (jobTimeShift != localTimeShift) {
				jobTime += getTimeZoneStr(jobTimeShift);
			}

			return jobTime;

		} else {
			SimpleDateFormat formatter = null;

			// formatter = new SimpleDateFormat("HH:mm:ss Z");
			formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

			TimeZone timeZone = TimeZone.getDefault();
			if (clientDSTOffset == timeZone.getDSTSavings()) {
				timeZone.setRawOffset(clientZoneOffset);
			} else if (clientDSTOffset > timeZone.getDSTSavings()) {
				timeZone.setRawOffset(clientZoneOffset + clientDSTOffset);
			} else {
				timeZone.setRawOffset(clientZoneOffset - timeZone.getDSTSavings());
			}
			formatter.setTimeZone(timeZone);

			return formatter.format(jobCalendar.getTime());
		}
	}

	// jobin baslangic ve bitis zamanlarindan calisma suresi hesaplaniyor
	public static String getJobWorkDuration(JsRealTime jobRealTime, boolean pastExecution) {

		if (jobRealTime == null) {
			return "-";
		}

		Calendar startCalendar = Calendar.getInstance();
		startCalendar.set(Calendar.HOUR_OF_DAY, jobRealTime.getStartTime().getTime().get(Calendar.HOUR_OF_DAY));
		startCalendar.set(Calendar.MINUTE, jobRealTime.getStartTime().getTime().get(Calendar.MINUTE));
		startCalendar.set(Calendar.SECOND, jobRealTime.getStartTime().getTime().get(Calendar.SECOND));

		Calendar stopCalendar = Calendar.getInstance();

		// is bitmisse
		if (jobRealTime.getStopTime() != null) {
			stopCalendar.set(Calendar.HOUR_OF_DAY, jobRealTime.getStopTime().getTime().get(Calendar.HOUR_OF_DAY));
			stopCalendar.set(Calendar.MINUTE, jobRealTime.getStopTime().getTime().get(Calendar.MINUTE));
			stopCalendar.set(Calendar.SECOND, jobRealTime.getStopTime().getTime().get(Calendar.SECOND));
		} else {
			if (pastExecution) {
				return "-";
			}
		}

		Long diff = (stopCalendar.getTimeInMillis() - startCalendar.getTimeInMillis()) / 1000;

		Long hour = diff / 3600;
		Long min = (diff - hour * 3600) / 60;
		Long sec = diff - hour * 3600 - min * 60;

		String workDuration = appendZero(hour.intValue()) + ":" + appendZero(min.intValue()) + ":" + appendZero(sec.intValue());

		return workDuration;
	}

	public static String appendZero(int value) {
		String zeroAppendedValue = "" + value;

		if (value < 10 && (value + "").length() == 1) {
			zeroAppendedValue = "0" + value;
		}

		return zeroAppendedValue;
	}

	/*
	 * public static Long timeToLong(Time time) {
	 * Long longTime = Long.parseLong(time.getHour()) * 3600 + Long.parseLong(time.getMinute()) * 60 + Long.parseLong(time.getSecond());
	 * 
	 * return longTime;
	 * }
	 */

	public static Date longtoDate(Long longDate) {
		Date date = new Date();
		date.setTime(longDate);

		return date;
	}

	public static Date findNextPeriod(Date nextPeriodTime, Long period) {
		boolean loop = true;

		while (loop) {
			Date currentTime = Calendar.getInstance().getTime();
			if (nextPeriodTime.before(currentTime)) {
				nextPeriodTime = DateUtils.longtoDate(nextPeriodTime.getTime() + period);
			} else {
				loop = false;
			}

		}

		return nextPeriodTime;
	}

	public static String getCurrentTimeWithMilliseconds() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.S");
		Date timeStr = Calendar.getInstance().getTime();
		return formatter.format(timeStr);
	}

	public static String getCurrentTimeForFileName() {
		SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy_HHmmssS");
		Date timeStr = Calendar.getInstance().getTime();
		return formatter.format(timeStr);
	}

	public static String getW3CDateTime() {
		Date date = new Date();
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
		TimeZone zone = dateFormater.getTimeZone();
		dateFormater.setTimeZone(zone);
		return dateFormater.format(date);
	}

	public static String getServerW3CDateTime() {

		String serverTimeZone = new String("Europe/Istanbul");
		
		TimeZone timeZone = TimeZone.getTimeZone(serverTimeZone);
		Calendar calendar = Calendar.getInstance(timeZone);
		
		DateTimeZone zone = DateTimeZone.forID(serverTimeZone);

		
		LocalTime jobLocalTime = new LocalTime(calendar.getTime(), zone);
		LocalDate t = new LocalDate(calendar.getTime(), zone);
		DateTime dt = t.toDateTime(jobLocalTime, zone);

		boolean isStandardOffset = zone.isStandardOffset(dt.getMillis());
		boolean isDaylightOfset = !isStandardOffset;
		
		String outputFormat = new String("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
		String dateStr = dt.toString(outputFormat);

		return dateStr;
	}
	
	public static Calendar dateToXmlTime(String time, String selectedTZone) {
		
		DateTimeZone zonex = DateTimeZone.forID(selectedTZone);

		DateTimeParser[] parsers = { DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ").getParser(), DateTimeFormat.forPattern("HH:mm:ss.SSSZZ").getParser(), DateTimeFormat.forPattern("HH:mm:ss.SSS").getParser(), DateTimeFormat.forPattern("HH:mm:ss").getParser() };

		DateTimeFormatter dtf = new DateTimeFormatterBuilder().append(null, parsers).toFormatter();

		LocalTime jobLocalTime = dtf.parseLocalTime(time);
		DateTime tx = jobLocalTime.toDateTimeToday(zonex);
		
		// TODO Locale için doğru seçim konusunda birşeyler yapmak lazım.
		return tx.toCalendar(Locale.US);
	}
	
	public static String getW3CDateTime(Calendar calendar, String selectedTZone, TypeOfTime.Enum myType) {

		String serverTimeZone = new String("Europe/Istanbul"); // Bu server ın olduğu makinadan otomatik mi alınsın, yoksa kullanıcı mı seçsin. Yoksa ikisi birlikte mi? 
		//TimeZone serverTZ = TimeZone.getTimeZone(serverTimeZone);
		
		String selectedTimeZone = new String(selectedTZone);
		TimeZone timeZone = TimeZone.getTimeZone(selectedTimeZone);
		
		boolean isDstExistWhenJobIsDefined = calendar.getTimeZone().useDaylightTime(); 
		boolean isDstExistWhenJobIsPlannedToRun = timeZone.inDaylightTime(calendar.getTime());
		
		DateTimeZone zonex = DateTimeZone.forID(serverTimeZone);
		DateTimeZone zone = DateTimeZone.forID(selectedTimeZone);
		
		//int timeOffSet2 = zone.getOffset(calendar.getTimeInMillis())/3600000;
		int timeOffSet = calendar.getTimeZone().getRawOffset()/3600000; // İşin tanımlandığı andaki Offset i, ornek +02:00
		//LocalTime localTime = new LocalTime(calendar.getTimeInMillis(), zonex.UTC);
		LocalTime jobLocalTime = null;
		LocalDateTime localDateTime = null;
		// Seçilen zaman tipine göre işin çalışma zamanını belirleyelim
		switch (myType.intValue()) {
			case TypeOfTime.INT_ACTUAL:
				jobLocalTime = new LocalTime(calendar.getTime(), zone);
				localDateTime = new LocalDateTime(calendar.getTime(), zone);
				break;

			case TypeOfTime.INT_RECURRING:
				jobLocalTime = new LocalTime(calendar.getTime(), DateTimeZone.forOffsetHours(timeOffSet));
				localDateTime = new LocalDateTime(calendar.getTime(), DateTimeZone.forOffsetHours(timeOffSet));
				break;

			case TypeOfTime.INT_BROADCAST: // Burası nasıl olacak tam karar vermedim.
				jobLocalTime = new LocalTime(calendar.getTime(), zone);
				localDateTime = new LocalDateTime(calendar.getTime(), zonex.UTC);
				break;
					
			default:
				break;
		}

		//LocalDate t = new LocalDate(calendar.getTime(), zone);
		DateTime dt = localDateTime.toDateTime(zone);

		String outputFormat = new String("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
		String dateStr = dt.toString(outputFormat);

		return dateStr;
	}
	
	public static long getCurrentTimeMilliseconds() {
		return System.currentTimeMillis();
	}
}
