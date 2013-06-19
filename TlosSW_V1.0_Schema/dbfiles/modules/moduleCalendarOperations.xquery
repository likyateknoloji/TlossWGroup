xquery version "1.0";

module namespace hs = "http://hs.tlos.com/";

declare namespace usr = "http://www.likyateknoloji.com/XML_user_types";
declare namespace per = "http://www.likyateknoloji.com/XML_permission_types";
declare namespace com = "http://www.likyateknoloji.com/XML_common_types";
declare namespace out="http://www.likyateknoloji.com/XML_userOutput_types";
declare namespace cal="http://www.likyateknoloji.com/XML_calendar_types";

(: Mapping 
$documentUrl = doc("xmldb:exist://db/TLOSSW/xmls/tlosSWCalendar10.xml")
:)

declare function hs:calendarNames($documentUrl as xs:string)  
 {
	for $calname in doc($documentUrl)/cal:calendarList/cal:calendarProperties/cal:calendarName
	return  data($calname)
};

declare function hs:calendars($documentUrl as xs:string) as element(cal:calendarProperties)*  
 {
	for $calendars in doc($documentUrl)/cal:calendarList/cal:calendarProperties
	return  $calendars
};

declare function hs:searchCalendar($documentUrl as xs:string, $prcal as element(cal:calendarProperties)) as element(cal:calendarProperties)* 
 { 
   for $calendar in doc($documentUrl)//cal:calendarProperties
   return 
	 if ( ($calendar/cal:calendarName= $prcal/cal:calendarName  or data($prcal/cal:calendarName="") )          
           and ($calendar/com:userId =$prcal/com:userId  or $prcal/com:userId= "-1"  )   
           and ( 
				if (exists($prcal/cal:validFrom/com:date))
 					 then  							
						   ( 
						     $prcal/cal:validFrom/com:date >=  $calendar/cal:validFrom/com:date 
                             and 
						     $prcal/cal:validFrom/com:date <=  $calendar/cal:validTo/com:date 
						   )
			        else ("TRUE")
				)
               )             
      then $calendar 
      else () 
};

(: //TODO CalendarId degeri belirlenmesi gerekiyor !! Buraya gelirken nasil belirlendigini kontrol et. hakan :)
declare function hs:insertCalendar($documentUrl as xs:string, $calendar as element(cal:calendarProperties))
{	
	update insert $calendar into doc($documentUrl)/cal:calendarList
};

declare function hs:insertCalendarLock($documentUrl as xs:string, $calendar as element(cal:calendarProperties))
{
   util:exclusive-lock(doc($documentUrl)/cal:calendarList, hs:insertCalendar($documentUrl, $calendar))     
};

declare function hs:updateCalendar($documentUrl as xs:string, $calendar as element(cal:calendarProperties))
 {	
	for $cld in doc($documentUrl)/cal:calendarList/cal:calendarProperties
	where $cld/@id = $calendar/@id
	return  update replace $cld with $calendar 
};

declare function hs:updateCalendarLock($documentUrl as xs:string, $calendar as element(cal:calendarProperties))
{
   util:exclusive-lock(doc($documentUrl)/cal:calendarList, hs:updateCalendar($documentUrl, $calendar))     
};

declare function hs:deleteCalendar($documentUrl as xs:string, $calendar as element(cal:calendarProperties)) 
 { 
	for $cld in doc($documentUrl)/cal:calendarList/cal:calendarProperties
	where $cld/@id = $calendar/@id
	return  update delete $cld
};

declare function hs:deleteCalendarLock($documentUrl as xs:string, $calendar as element(cal:calendarProperties))
{
   util:exclusive-lock(doc($documentUrl)/cal:calendarList, hs:deleteCalendar($documentUrl, $calendar))     
};

declare function hs:searchCalendarByID($documentUrl as xs:string, $id as xs:integer) as element(cal:calendarProperties)? 
 {
	for $calendar in doc($documentUrl)/cal:calendarList/cal:calendarProperties
	where $calendar/@id = $id
	return $calendar
};
