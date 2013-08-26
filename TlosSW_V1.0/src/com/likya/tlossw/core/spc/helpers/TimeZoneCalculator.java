package com.likya.tlossw.core.spc.helpers;

import java.util.Calendar;

import com.likya.tlos.model.xmlbeans.common.TypeOfTimeDocument.TypeOfTime;
import com.likya.tlos.model.xmlbeans.data.TimeManagementDocument.TimeManagement;
import com.likya.tlossw.utils.date.DateUtils;

public class TimeZoneCalculator {
	
	public static boolean calculateExecutionTime(Calendar tmpTime, TimeManagement timeManagement) {

		// tmpTime Job daki zaman
		// currentTime simdiki zaman

		boolean isDstExistWhenJobIsDefined = tmpTime.getTimeZone().useDaylightTime(); // Su an kullanılmıyor ama geliştirme henüz bitmedi. Lazım olacak.

		String serverTimeZone = new String("Europe/Istanbul"); // Bu server ın olduğu makinadan otomatik mi alınsın, yoksa kullanıcı mı seçsin. Yoksa ikisi birlikte mi?
		String jobTimeZone = timeManagement.getTimeZone(); // Job tanımında seçilen Time Zone
		String agentTimeZone = new String("America/Los_Angeles"); // Agent ın bulunduğu makinanın Time Zone bilgisi

		// tmpTime bilgisinde sadece hh:mm:ss var, YYYY:MM:DD bilgisini ekleyelim.
		Calendar calendar = Calendar.getInstance();

		tmpTime.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
		tmpTime.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
		tmpTime.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));

		boolean timeHasCome = false;

		// Hangi tip zaman için işlem yapılacak?
		TypeOfTime.Enum myType = timeManagement.getTypeOfTime();

		String serverDateTimeStr = DateUtils.getServerW3CDateTime(); // Server da simdiki zaman
		String jobDateTimeStr = DateUtils.getW3CDateTime(tmpTime, jobTimeZone, myType); // Job da belirtilen zaman
		String agentDateTimeStr = DateUtils.getW3CDateTime(tmpTime, agentTimeZone, myType); // Agent da calismasi gereken zaman

		System.out.println("Server saati      : " + serverDateTimeStr + "\n" + "Job saati         : " + jobDateTimeStr + "\n" + "Agent yerel saati : " + agentDateTimeStr);

		Calendar serverDateTime = DateUtils.dateToXmlTime(serverDateTimeStr, serverTimeZone); // Server da simdiki zaman
		Calendar jobDateTime = DateUtils.dateToXmlTime(jobDateTimeStr, jobTimeZone); // Job da belirtilen zaman, Job daki zaman dilimine cevriliyor.
		Calendar jobDateTimeAtServer = DateUtils.dateToXmlTime(jobDateTimeStr, serverTimeZone); // Job da belirtilen zaman, TZ bilgisiine gore hesaplanıyor, Server daki zaman dilimine cevriliyor.
		Calendar jobDateTimeAtAgent = DateUtils.dateToXmlTime(agentDateTimeStr, serverTimeZone); // Job da belirtilen zaman, agent TZ offset, Server daki zaman dilimine cevriliyor

		switch (myType.intValue()) {
		case TypeOfTime.INT_ACTUAL:
		case TypeOfTime.INT_RECURRING:
			timeHasCome = jobDateTime.before(serverDateTime);
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
