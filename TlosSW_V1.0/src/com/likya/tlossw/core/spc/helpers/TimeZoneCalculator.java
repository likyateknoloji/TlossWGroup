package com.likya.tlossw.core.spc.helpers;

import java.util.Calendar;
import java.util.TimeZone;

import com.likya.tlos.model.xmlbeans.common.TypeOfTimeDocument.TypeOfTime;
import com.likya.tlos.model.xmlbeans.data.TimeManagementDocument.TimeManagement;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likya.tlossw.utils.date.DateUtils;

public class TimeZoneCalculator {
	
	public static boolean calculateExecutionTime(TimeManagement timeManagement) {

		// tmpTime Job daki zaman
		// currentTime simdiki zaman

		Calendar startTime = timeManagement.getJsPlannedTime().getStartTime().getTime();
		
		String serverTimeZone = new String("Europe/Istanbul"); // Bu server ın olduğu makinadan otomatik mi alınsın, yoksa kullanıcı mı seçsin. Yoksa ikisi birlikte mi?
		String jobTimeZone = timeManagement.getTimeZone(); // Job tanımında seçilen Time Zone
		String agentTimeZone = new String("America/Los_Angeles"); // Agent ın bulunduğu makinanın Time Zone bilgisi

		TimeZone tz = TimeZone.getTimeZone(jobTimeZone);
		
		startTime.setTimeZone(tz);
		
		// boolean isDstExistWhenJobIsDefined = startTime.getTimeZone().useDaylightTime(); // Su an kullanılmıyor ama geliştirme henüz bitmedi. Lazım olacak.
		
		// tmpTime bilgisinde sadece hh:mm:ss var, YYYY:MM:DD bilgisini ekleyelim.										
		Calendar calendar = Calendar.getInstance();

		startTime.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
		startTime.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
		startTime.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));

		boolean timeHasCome = false;

		// Hangi tip zaman için işlem yapılacak?
		TypeOfTime.Enum myType = timeManagement.getTypeOfTime();

		String serverDateTimeStr = DateUtils.getServerW3CDateTime(); // Server da simdiki zaman
		String jobDateTimeStr = DateUtils.getW3CDateTime(startTime, jobTimeZone, myType); // Job da belirtilen zaman
		String agentDateTimeStr = DateUtils.getW3CDateTime(startTime, agentTimeZone, myType); // Agent da calismasi gereken zaman

		if(SpaceWideRegistry.isDebug) {
			System.out.println("Server saati      : " + serverDateTimeStr);
			System.out.println("Job saati         : " + jobDateTimeStr);
			System.out.println("Agent yerel saati : " + agentDateTimeStr);
		}
		
		Calendar serverDateTime = DateUtils.dateToXmlTime(serverDateTimeStr, serverTimeZone); // Server da simdiki zaman
		// Calendar jobDateTime = DateUtils.dateToXmlTime(jobDateTimeStr, jobTimeZone); // Job da belirtilen zaman, Job daki zaman dilimine cevriliyor.
		Calendar jobDateTimeAtServer = DateUtils.dateToXmlTime(jobDateTimeStr, serverTimeZone); // Job da belirtilen zaman, TZ bilgisine gore hesaplanıyor, Server daki zaman dilimine cevriliyor.
		Calendar jobDateTimeAtAgent = DateUtils.dateToXmlTime(agentDateTimeStr, serverTimeZone); // Job da belirtilen zaman, agent TZ offset, Server daki zaman dilimine cevriliyor

		switch (myType.intValue()) {
		case TypeOfTime.INT_ACTUAL:
		case TypeOfTime.INT_RECURRING:
			timeHasCome = jobDateTimeAtServer.before(serverDateTime);
			break;

		case TypeOfTime.INT_BROADCAST:
			timeHasCome = jobDateTimeAtAgent.before(serverDateTime);
			break;

		default:
			break;
		}

		return timeHasCome;

	}
}
