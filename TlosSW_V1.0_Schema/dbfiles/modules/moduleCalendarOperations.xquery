xquery version "1.0";
module namespace hs = "http://hs.tlos.com/";
declare namespace usr = "http://www.likyateknoloji.com/XML_user_types";
declare namespace per = "http://www.likyateknoloji.com/XML_permission_types";
declare namespace com = "http://www.likyateknoloji.com/XML_common_types";
declare namespace out="http://www.likyateknoloji.com/XML_userOutput_types";
declare namespace cal="http://www.likyateknoloji.com/XML_calendar_types";

declare function hs:calendarNames( )  
 {
	for $calname in doc("xmldb:exist://db/TLOSSW/xmls/tlosSWCalendar10.xml")/cal:calendarList/cal:calendarProperties/cal:calendarName
	return  data($calname)
};

declare function hs:calendars( ) as element(cal:calendarProperties)*  
 {
	for $calendars in doc("xmldb:exist://db/TLOSSW/xmls/tlosSWCalendar10.xml")/cal:calendarList/cal:calendarProperties
	return  $calendars
};
(: ($calendar/com:userName= $prcal/com:userName          or data($prcal/com:userName)= "")  :)
declare function hs:searchCalendar( $prcal as element(cal:calendarProperties)) as element(cal:calendarProperties)* 
 { 
   for $calendar in doc("//db/TLOSSW/xmls/tlosSWCalendar10.xml")//cal:calendarProperties
   return 
	 if ( ($calendar/cal:calendarName= $prcal/cal:calendarName  or data($prcal/cal:calendarName="") )          
           and ($calendar/com:userId =$prcal/com:userId  or $prcal/com:userId= "-1"  )   
           and ( 
				if (string($prcal/cal:validFrom/com:date/com:year) != '')
 					 then  							
						   xs:date(concat($prcal/cal:validFrom/com:date/com:year,'-', $prcal/cal:validFrom/com:date/com:month,'-',$prcal/cal:validFrom/com:date/com:day ) ) 
               			   >=  xs:date(concat($calendar/cal:validFrom/com:date/com:year,'-', $calendar/cal:validFrom/com:date/com:month,'-',$calendar/cal:validFrom/com:date/com:day ) ) 
                           and xs:date(concat($prcal/cal:validFrom/com:date/com:year,'-', $prcal/cal:validFrom/com:date/com:month,'-',$prcal/cal:validFrom/com:date/com:day ) ) 
                           <=  xs:date(concat($calendar/cal:validTo/com:date/com:year,'-', $calendar/cal:validTo/com:date/com:month,'-',$calendar/cal:validTo/com:date/com:day ) )							
			        else ("TRUE")
				)
               )             
      then $calendar 
      else () 
};

declare function hs:insertCalendar($calendar as element(cal:calendarProperties))
{	
	update insert $calendar into doc("xmldb:exist:///db/TLOSSW/xmls/tlosSWCalendar10.xml")/cal:calendarList
};

declare function hs:insertCalendarLock($calendar as element(cal:calendarProperties))
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWCalendar10.xml")/cal:calendarList, hs:insertCalendar($calendar))     
};

declare function hs:updateCalendar($calendar as element(cal:calendarProperties))
 {	
	for $cld in doc("xmldb:exist:///db/TLOSSW/xmls/tlosSWCalendar10.xml")/cal:calendarList/cal:calendarProperties
	where $cld/@id = $calendar/@id
	return  update replace $cld with $calendar 
};

declare function hs:updateCalendarLock($calendar as element(cal:calendarProperties))
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWCalendar10.xml")/cal:calendarList, hs:updateCalendar($calendar))     
};

declare function hs:deleteCalendar( $calendar as element(cal:calendarProperties)) 
 { 
	for $cld in doc("xmldb:exist:///db/TLOSSW/xmls/tlosSWCalendar10.xml")/cal:calendarList/cal:calendarProperties
	where $cld/@id = $calendar/@id
	return  update delete $cld
};

declare function hs:deleteCalendarLock($calendar as element(cal:calendarProperties))
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWCalendar10.xml")/cal:calendarList, hs:deleteCalendar($calendar))     
};

declare function hs:searchCalendarByID($id as xs:integer) as element(cal:calendarProperties)? 
 {
	for $calendar in doc("//db/TLOSSW/xmls/tlosSWCalendar10.xml")/cal:calendarList/cal:calendarProperties
	where $calendar/@id = $id
	return $calendar
};
