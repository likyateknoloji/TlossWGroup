xquery version "1.0";

module namespace hs = "http://hs.tlos.com/";

import module namespace sq = "http://sq.tlos.com/" at "moduleSequenceOperations.xquery";
import module namespace met = "http://meta.tlos.com/" at "moduleMetaDataOperations.xquery";

declare namespace dat="http://www.likyateknoloji.com/XML_data_types";
declare namespace com="http://www.likyateknoloji.com/XML_common_types"; 
declare namespace cal="http://www.likyateknoloji.com/XML_calendar_types";
declare namespace state-types="http://www.likyateknoloji.com/state-types";
declare namespace xsi="http://www.w3.org/2001/XMLSchema-instance"; 
declare namespace fn="http://www.w3.org/2005/xpath-functions"; 
declare namespace functx = "http://www.functx.com";

(:**************************Run PLAN**********************************:) 

declare function functx:is-leap-year 
		  ( $date as xs:anyAtomicType? )  as xs:boolean {
		       
		    for $year in xs:integer(substring(string($date),1,4))
		    return ($year mod 4 = 0 and
		            $year mod 100 != 0) or
		            $year mod 400 = 0
		 } ;

declare function functx:day-of-week 
		  ( $date as xs:anyAtomicType? )  as xs:integer? {	
		       
		  if (empty($date))
		  then ()
		  else xs:integer((xs:date($date) - xs:date('1901-01-06'))
		          div xs:dayTimeDuration('P1D')) mod 7
		 } ;

declare function functx:day-of-week-name-en 
		  ( $date as xs:anyAtomicType? )  as xs:string? {
		       
		   ('SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY',
		    'THURSDAY', 'FRIDAY', 'SATURDAY')
		      [functx:day-of-week($date) + 1]
		 } ;

declare function functx:repeat-string 
		  ( $stringToRepeat as xs:string? ,
		    $count as xs:integer )  as xs:string {
		       
		   if ($count > 0)
		   then string-join((for $i in 1 to $count return $stringToRepeat),
		                        '')
		   else ''
		 } ;

declare function functx:pad-integer-to-length 
		  ( $integerToPad as xs:anyAtomicType? ,
		    $length as xs:integer )  as xs:string {
		       
		   if ($length < string-length(string($integerToPad)))
		   then error(xs:QName('functx:Integer_Longer_Than_Length'))
		   else concat
		         (functx:repeat-string(
		            '0',$length - string-length(string($integerToPad))),
		          string($integerToPad))
		 } ;

declare function functx:date 
		  ( $year as xs:anyAtomicType ,
		    $month as xs:anyAtomicType ,
		    $day as xs:anyAtomicType )  as xs:date {
		       
		   xs:date(
		     concat(
		       functx:pad-integer-to-length(xs:integer($year),4),'-',
		       functx:pad-integer-to-length(xs:integer($month),2),'-',
		       functx:pad-integer-to-length(xs:integer($day),2)))
		 } ;

declare function functx:first-day-of-year 
		  ( $date as xs:anyAtomicType? )  as xs:date? {
		       
		   functx:date(year-from-date(xs:date($date)), 1, 1)
		 } ;

declare function functx:last-day-of-year 
		  ( $date as xs:anyAtomicType? )  as xs:date? {
		       
		   functx:date(year-from-date(xs:date($date)), 12, 31)
		 } ;

declare function functx:days-in-month 
		  ( $date as xs:anyAtomicType? )  as xs:integer? {
		       
		   if (fn:month-from-date(xs:date($date)) = 2 and
		       functx:is-leap-year($date))
		   then 29
		   else
		   (31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
		    [fn:month-from-date(xs:date($date))]
		 } ;

declare function functx:month-name-en 
		  ( $date as xs:anyAtomicType? )  as xs:string? {
		       
		   ('January', 'February', 'March', 'April', 'May', 'June',
		    'July', 'August', 'September', 'October', 'November', 'December')
		   [fn:month-from-date(xs:date($date))]
		 } ;

declare function functx:first-day-of-month 
		  ( $date as xs:anyAtomicType? )  as xs:date? {
		       
		   functx:date(year-from-date(xs:date($date)),
		            month-from-date(xs:date($date)),
		            1)
		 } ;

declare function functx:last-day-of-month 
		  ( $date as xs:anyAtomicType? )  as xs:date? {
		       
		   functx:date(year-from-date(xs:date($date)),
		            month-from-date(xs:date($date)),
		            1)
		 } ;

(:("January", "February", "March", "April", "May", "June", "July", "August","September","October","November","December")
		    [fn:month-from-date(fn:current-date())]:)

(:************************** START PLANNING **********************************:) 

(: Calendar Selection Phase :)
			(:
			  Takvimi kullanarak planlamayi yapalim ve calisacak isleri belirleyelim.
			:)

(: declare function hs:queryDailyPlan($which_id as xs:integer?, $nextPlanId as xs:integer?) as xs:boolean? :)
declare function hs:createPlanCalendars($documentUrl as xs:string) as node()*
{
    let $planDocumentUrl := met:getMetaData($documentUrl, "plan")
	let $calendarsDocumentUrl := met:getMetaData($documentUrl, "calendar")
	
    let $nextPlanId := sq:getNextId($documentUrl, "planId")
    let $yeniPlan :=

	for $calList in doc($planDocumentUrl)/AllPlanParameters
	return update insert
(:***************************************************************************:)

		for $calendar in doc($calendarsDocumentUrl)/cal:calendarList//cal:calendarProperties
		  (: ################### PLAN KURALLARI HESAPLANSIN ################# :)
		   let $date1  := data($calendar/cal:validFrom/com:date)
		   let $time1  := data($calendar/cal:validFrom/com:time)
		   let $date2  := data($calendar/cal:validTo/com:date)
		   let $time2  := data($calendar/cal:validTo/com:time)
		   
		   let $tarih1 := dateTime($date1,$time1)
           let $tarih2 := dateTime($date2,$time2)

           
		   let $daySpecial  := $calendar/cal:calendarPeriod/com:daySpecial//text()
		   (: ([Ff]irst|[Llast]|[Oo]dd|[Ee]ven|[Aa]ll) :)

		   let $dayDef := $calendar/cal:calendarPeriod/com:dayDef//text()
		   (: ([Dd]aily|[Bb]usinessday|[Ww]eekly|[Hh]oliday[Mm]ontly|[Qq]uarterly|[Yy]early) :)

		   let $calname := $calendar/cal:calendarName//text()
		   let $calid := $calendar/@id
		   
	       let $dayofWeek := functx:day-of-week(fn:current-date())

	       (: where 0 is Sunday, 1 is Monday, etc :)
	       let $weekendsFlg := if (fn:index-of((6,0), $dayofWeek)) then 1 else 0
	       let $weekdaysFlg := if (fn:index-of((1,2,3,4,5), $dayofWeek)) then 1 else 0
	       let $firstDayOfWeekFlg := if ($dayofWeek eq 1) then 1 else 0
	       let $lastDayOfWeekFlg  := if ($dayofWeek eq 0) then 1 else 0
	       let $currentDayByNum  := fn:day-from-date(fn:current-date())
	       let $currentYear    := fn:year-from-date(fn:current-date())
	       let $currentMonthByNum  := fn:month-from-date(fn:current-date()) 
	       let $currentMonthByName  := ("January", "February", "March", "April", "May", "June", "July", "August","September","October","November","December")
	       [fn:month-from-date(fn:current-date())]

	       let $dayFirst:= 1
	       let $dayLast:= if (fn:index-of(('April', 'June', 'September', 'November'), xs:string($currentMonthByName))) then 30 
	                      else if (fn:index-of(('January', 'March', 'May', 'July', 'August', 'October', 'December'), xs:string($currentMonthByName))) then 31
	                      else if (xs:string($currentMonthByName) eq 'February' and functx:is-leap-year($currentYear)) then 29
	                      else 28
	       let $firstDayOfYearFlg := if (functx:first-day-of-year(xs:date(fn:current-date())) eq fn:current-date()) then 1 else 0
	       let $lastDayOfYearFlg  := if (functx:last-day-of-year(xs:date(fn:current-date())) eq fn:current-date()) then 1 else 0
	  
	       let $currentDayByName    := functx:day-of-week-name-en(xs:date(fn:current-date() ))
(:
("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
	    [1 + ((fn:current-date() -  xs:date("1901-01-06")) div xs:dayTimeDuration("P1D") mod 7)]
:)

		   let $currentWeek   := floor((fn:current-date() -  xs:date("2000-12-31")) div xs:dayTimeDuration("P7D")) mod 52

	       let $firstDayOfQ1 := functx:date($currentYear,1,1)
		   let $firstDayOfQ1Flg := if ($firstDayOfQ1 = xs:date(fn:current-date() )) then 1 else 0
	       let $lastDayOfQ1 := functx:date($currentYear,3,31)
		   let $lastDayOfQ1Flg := if ($lastDayOfQ1 = xs:date(fn:current-date() )) then 1 else 0
	       let $firstDayOfQ2 := functx:date($currentYear,4,1)
		   let $firstDayOfQ2Flg := if ($firstDayOfQ2 = xs:date(fn:current-date() )) then 1 else 0
	       let $lastDayOfQ2 := functx:date($currentYear,6,30)
		   let $lastDayOfQ2Flg := if ($lastDayOfQ2 = xs:date(fn:current-date() )) then 1 else 0
	       let $firstDayOfQ3 := functx:date($currentYear,7,1)
		   let $firstDayOfQ3Flg := if ($firstDayOfQ3 = xs:date(fn:current-date() )) then 1 else 0
	       let $lastDayOfQ3 := functx:date($currentYear,9,30)
		   let $lastDayOfQ3Flg := if ($lastDayOfQ3 = xs:date(fn:current-date() )) then 1 else 0
	       let $firstDayOfQ4 := functx:date($currentYear,10,1)
		   let $firstDayOfQ4Flg := if ($firstDayOfQ4 = xs:date(fn:current-date() )) then 1 else 0
	       let $lastDayOfQ4 := functx:date($currentYear,12,31)
		   let $lastDayOfQ4Flg := if ($lastDayOfQ4 = xs:date(fn:current-date() )) then 1 else 0

	       (: functx:days-in-month(fn:current-date()) :)

		   let $firstDayOfYear := functx:first-day-of-year(xs:date(fn:current-date()))
		   let $lastDayOfYear  := functx:last-day-of-year(xs:date(fn:current-date()))
	       let $compbuss := 1 (:xs:date(concat(xs:string($currentYear),'-',$currentMonthByNum,'-',$day1)):)
	       let $currentQuarter :=("Quarter1", "Quarter1", "Quarter1", "Quarter2", "Quarter2", "Quarter2", "Quarter3", "Quarter3","Quarter3","Quarter4","Quarter4","Quarter4")
	       [fn:month-from-date(fn:current-date())]

	       let $firstDayOfMonthFlg := if (functx:first-day-of-month(fn:current-date()) eq fn:current-date()) then 1 else 0
	       let $lastDayOfMonthFlg  := if (functx:date($currentYear,$currentMonthByNum,$dayLast) eq fn:current-date()) then 1 else 0 

	       (: functx:last-day-of-month(fn:current-date()) :)

	       let $dayOddFlg := ceiling($currentDayByNum div 2) - floor($currentDayByNum div 2)
	       let $dayEvenFlg:= if ($dayOddFlg = 0) then 1 else 0
	       let $weekOddFlg := ceiling($currentWeek div 2) - floor($currentWeek div 2)
	       let $weekEvenFlg:= if ($weekOddFlg = 0) then 1 else 0
	       let $monthOddFlg := ceiling($currentMonthByNum div 2) - floor($currentMonthByNum div 2)
	       let $monthEvenFlg:= if ($monthOddFlg = 0) then 1 else 0
	       let $quarterOddFlg := if ($currentQuarter = 'Quarter1' or $currentQuarter = 'Quarter3') then 1 else 0
	       let $quarterEvenFlg:= if ($quarterOddFlg = 0) then 1 else 0 
           let $yearOddFlg := $currentYear mod 2
           let $yearEvenFlg:= if ($yearOddFlg = 0) then 1 else 0

	       let $howmanyTimes   := $calendar/cal:howmanyTimes//text()
	       let $whichOnes := $calendar/cal:whichOnes/cal:Name
		   let $specificDays := $calendar/cal:specificDays/com:date
		   let $exceptionDays := $calendar/cal:exceptionDays/com:date
	       let $whichOnesCnt := count(for $days in $whichOnes
	                                  return $days/text())
		   let $whichOnesFlg := if ($whichOnesCnt) then
		                            count(for $days in $whichOnes
	                                where xs:string($days) eq $currentDayByName
	                                return $days/text())
								else 1
								
           let $firstOne := <ilk>
            {for $days in $whichOnes
             order by $days/@ID ascending
             return $days}</ilk>
           let $firstOneDayName := data($firstOne/cal:Name[1])
           let $firstOneDayFlg := if ($whichOnesCnt) then (
                                     if ($firstOneDayName eq data($currentDayByName)) then 1 else 0)
                                  else if (xs:string('MONDAY') eq data($currentDayByName)) then 1 else 0

           let $lastOne := <son>
            {for $days in $whichOnes
             order by $days/@ID descending
             return $days}</son>
             let $lastOneDayName := data($lastOne/cal:Name[1])
           let $lastOneDayFlg := if ($whichOnesCnt) then (
                        if ($lastOneDayName eq data($currentDayByName)) then 1 else 0)
                        else if (xs:string('SUNDAY') eq data($currentDayByName)) then 1 else 0

	       let $exceptionDaysCnt := count(for $days in $exceptionDays/text()
	                                  return $days)
		   let $exceptionDaysFlg := if ($exceptionDaysCnt) then
		                            if (count(
                                               for $var in $exceptionDays//com:date
		                                 let $excepdate1  := data($var)
					   where fn:current-date() eq xs:date($excepdate1)
	                                      return 1)) then 1 else 0
					  else 0
	       let $specificDaysCnt := count(for $days in $specificDays/text()
	                                  return $days)
		   let $specificDaysFlg := if ($specificDaysCnt) then
		                            if (count(
					  for $var in $specificDays//com:date
		                              let $specdate1  := data($var)
					     where fn:current-date() eq xs:date($specdate1)
	                                      return 1)) then 1 else 0
						else 0

								
		  (:where xs:string($name) eq 'job1':)
		   
	    where 
		   (: ValidFrom ValidTo :)	 
		      $tarih1 - fn:current-dateTime() le xs:dayTimeDuration("P0D") and
		      $tarih2 - fn:current-dateTime() ge xs:dayTimeDuration("P0D") and 
	          (: value="([Ff]irst|[Llast]|[Oo]dd|[Ee]ven|[Aa]ll){1}" :)	   
			  (: value="([Dd]aily|[Bb]usinessday|[Ww]eekly|[Hh]olidays|[Mm]ontly|[Qq]uarterly|[Yy]early){1}" :)
			  (: ################### PLAN KURALLARI ISLETILSIN ################# :)
			  (
			   $specificDaysFlg
			   or
			   (
			    $whichOnesFlg and
			    not($exceptionDaysFlg) and
		        (
	            if (xs:string($dayDef) eq 'DAILY') then (
	             if (xs:string($daySpecial) eq 'ALL') then true()
				 else if (xs:string($daySpecial) eq 'FIRST') then true()
				 else if (xs:string($daySpecial) eq 'LAST') then true()
	             else if (xs:string($daySpecial) eq 'ODD' and $dayOddFlg) then true()
	             else if (xs:string($daySpecial) eq 'EVEN' and $dayEvenFlg) then true()
	             else false()
	            )
			    else if (xs:string($dayDef) eq 'WEEKLY') then  (
	             if (xs:string($daySpecial) eq 'ALL') then true() 
	             else if (xs:string($daySpecial) eq 'FIRST' and $firstOneDayFlg) then true() 
	             else if (xs:string($daySpecial) eq 'LAST'  and $lastOneDayFlg) then true()
				 else if (xs:string($daySpecial) eq 'ODD'  and $weekOddFlg) then true()
				 else if (xs:string($daySpecial) eq 'EVEN'  and $weekEvenFlg) then true()
	             else false()
	            )
		        else if (xs:string($dayDef) eq 'MONTHLY') then  (
	             if (xs:string($daySpecial) eq 'ALL') then true() 
	             else if (xs:string($daySpecial) eq 'FIRST' and $firstDayOfMonthFlg eq 1) then true() 
	             else if (xs:string($daySpecial) eq 'LAST'  and $lastDayOfMonthFlg eq 1) then true()
				 else if (xs:string($daySpecial) eq 'ODD'  and $monthOddFlg) then true()
				 else if (xs:string($daySpecial) eq 'EVEN'  and $monthEvenFlg) then true()
	             else false()
	            )
		        else if (xs:string($dayDef) eq 'QUARTERLY') then  (
	             if (xs:string($daySpecial) eq 'ALL') then true() 
	             else if (xs:string($daySpecial) eq 'FIRST' and xs:string($currentQuarter) = 'Quarter1' and $firstDayOfQ1Flg eq 1) then true() 
	             else if (xs:string($daySpecial) eq 'FIRST' and xs:string($currentQuarter) = 'Quarter2' and $firstDayOfQ2Flg eq 1) then true() 
	             else if (xs:string($daySpecial) eq 'FIRST' and xs:string($currentQuarter) = 'Quarter3' and $firstDayOfQ3Flg eq 1) then true() 
	             else if (xs:string($daySpecial) eq 'FIRST' and xs:string($currentQuarter) = 'Quarter4' and $firstDayOfQ4Flg eq 1) then true() 
	             else if (xs:string($daySpecial) eq 'LAST' and xs:string($currentQuarter) = 'Quarter1' and $lastDayOfQ1Flg eq 1) then true() 
	             else if (xs:string($daySpecial) eq 'LAST' and xs:string($currentQuarter) = 'Quarter2' and $lastDayOfQ2Flg eq 1) then true() 
	             else if (xs:string($daySpecial) eq 'LAST' and xs:string($currentQuarter) = 'Quarter3' and $lastDayOfQ3Flg eq 1) then true() 
	             else if (xs:string($daySpecial) eq 'LAST' and xs:string($currentQuarter) = 'Quarter4' and $lastDayOfQ4Flg eq 1) then true() 
				 else if (xs:string($daySpecial) eq 'ODD'  and $quarterOddFlg) then true()
				 else if (xs:string($daySpecial) eq 'EVEN'  and $quarterEvenFlg) then true()
	             else false()
	            )
		        else if (xs:string($dayDef) eq 'YEARLY') then  (
	             if (xs:string($daySpecial) eq 'ALL') then true() 
	             else if (xs:string($daySpecial) eq 'FIRST' and $firstDayOfYearFlg eq 1) then true() 
	             else if (xs:string($daySpecial) eq 'LAST'  and $lastDayOfYearFlg eq 1) then true()
				 else if (xs:string($daySpecial) eq 'ODD'  and $yearOddFlg) then true()
				 else if (xs:string($daySpecial) eq 'EVEN'  and $yearEvenFlg) then true()
	             else false()
	            )
		        else if (xs:string($dayDef) eq 'TIMELY') then  (
	             if (xs:string($daySpecial) eq 'ALL') then true() 
	             else if (xs:string($daySpecial) eq 'FIRST') then true() 
	             else if (xs:string($daySpecial) eq 'LAST') then true()
				 else if (xs:string($daySpecial) eq 'ODD') then true()
				 else if (xs:string($daySpecial) eq 'EVEN') then true()
	             else false()
	            )
		        else if (xs:string($dayDef) eq 'PERPETUAL') then  (
	             if (xs:string($daySpecial) eq 'ALL') then true() 
	             else if (xs:string($daySpecial) eq 'FIRST') then true() 
	             else if (xs:string($daySpecial) eq 'LAST') then true()
				 else if (xs:string($daySpecial) eq 'ODD') then true()
				 else if (xs:string($daySpecial) eq 'EVEN') then true()
	             else false()
	            )
			    else false()
			   )
			  )
			 )

return

<plan id="{$nextPlanId}">
<planName>{$calname}</planName>
<calID>{data($calid)}</calID>
<currentDayByName>{$currentDayByName}</currentDayByName>
<currentDayByNum>{$currentDayByNum}</currentDayByNum>
<currentMonthByName>{$currentMonthByName}</currentMonthByName>
<currentMonthByNum>{$currentMonthByNum}</currentMonthByNum>
<currentQuarter>{$currentQuarter}</currentQuarter>
<currentYear>{$currentYear}</currentYear>
<daySpecial>{$daySpecial}</daySpecial>
<dayDef>{$dayDef}</dayDef>
<dayFirst>{$dayFirst}</dayFirst>
<dayLast>{$dayLast}</dayLast>
<firstDayOfYear>{$firstDayOfYear}</firstDayOfYear>
<lastDayOfYear>{$lastDayOfYear}</lastDayOfYear>
<firstDayOfWeekFlg>{$firstDayOfWeekFlg}</firstDayOfWeekFlg>
<lastDayOfWeekFlg>{$lastDayOfWeekFlg}</lastDayOfWeekFlg>
<firstDayOfQ1>{$firstDayOfQ1}</firstDayOfQ1>
<lastDayOfQ1>{$lastDayOfQ1}</lastDayOfQ1>
<firstDayOfQ2>{$firstDayOfQ2}</firstDayOfQ2>
<lastDayOfQ2>{$lastDayOfQ2}</lastDayOfQ2>
<firstDayOfQ3>{$firstDayOfQ3}</firstDayOfQ3>
<lastDayOfQ3>{$lastDayOfQ3}</lastDayOfQ3>
<firstDayOfQ4>{$firstDayOfQ4}</firstDayOfQ4>
<lastDayOfQ4>{$lastDayOfQ4}</lastDayOfQ4>
<firstDayOfYearFlg>{$firstDayOfYearFlg}</firstDayOfYearFlg>
<lastDayOfYearFlg>{$lastDayOfYearFlg}</lastDayOfYearFlg>
<dayOfWeek>{$dayofWeek}</dayOfWeek>
<weekendsFlg>{$weekendsFlg}</weekendsFlg>
<weekdaysFlg>{$weekdaysFlg}</weekdaysFlg>
<currentWeek>{$currentWeek}</currentWeek>
<firstDayOfMonthFlg>{$firstDayOfMonthFlg}</firstDayOfMonthFlg>
<lastDayOfMonthFlg>{$lastDayOfMonthFlg}</lastDayOfMonthFlg>
<dayOddFlg>{$dayOddFlg}</dayOddFlg>
<dayEvenFlg>{$dayEvenFlg}</dayEvenFlg>
<weekOddFlg>{$weekOddFlg}</weekOddFlg>
<weekEvenFlg>{$weekEvenFlg}</weekEvenFlg>
<monthOddFlg>{$monthOddFlg}</monthOddFlg>
<monthEvenFlg>{$monthEvenFlg}</monthEvenFlg>
<firstDayOfQ1Flg>{$firstDayOfQ1Flg}</firstDayOfQ1Flg>
<lastDayOfQ1Flg>{$lastDayOfQ1Flg}</lastDayOfQ1Flg>
<firstDayOfQ2Flg>{$firstDayOfQ2Flg}</firstDayOfQ2Flg>
<lastDayOfQ2Flg>{$lastDayOfQ2Flg}</lastDayOfQ2Flg>
<firstDayOfQ3Flg>{$firstDayOfQ3Flg}</firstDayOfQ3Flg>
<lastDayOfQ3Flg>{$lastDayOfQ3Flg}</lastDayOfQ3Flg>
<firstDayOfQ4Flg>{$firstDayOfQ4Flg}</firstDayOfQ4Flg>
<lastDayOfQ4Flg>{$lastDayOfQ4Flg}</lastDayOfQ4Flg>
<quarterOddFlg>{$quarterOddFlg}</quarterOddFlg>
<quarterEvenFlg>{$quarterEvenFlg}</quarterEvenFlg>
<yearOddFlg>{$yearOddFlg}</yearOddFlg>
<yearEvenFlg>{$yearEvenFlg}</yearEvenFlg>
<firstOne>{$firstOne}</firstOne>
<firstOneDayName>{$firstOneDayName}</firstOneDayName>
<firstOneDayFlg>{$firstOneDayFlg}</firstOneDayFlg>
<lastOne>{$lastOne}</lastOne>
<lastOneDayName>{$lastOneDayName}</lastOneDayName>
<lastOneDayFlg>{$lastOneDayFlg}</lastOneDayFlg>
<whichOnes>{$whichOnes}</whichOnes>
<whichOnesFlg>{$whichOnesFlg}</whichOnesFlg>
<whichOnesCnt>{$whichOnesCnt}</whichOnesCnt>
<specificDays>{$specificDays}</specificDays>
<exceptionDays>{$exceptionDays}</exceptionDays>
<specificDaysCnt>{$specificDaysCnt}</specificDaysCnt>
<specificDaysFlg>{$specificDaysFlg}</specificDaysFlg>
<exceptionDaysCnt>{$exceptionDaysCnt}</exceptionDaysCnt>
<exceptionDaysFlg>{$exceptionDaysFlg}</exceptionDaysFlg>
<tarih1>{$tarih1}</tarih1>
<tarih2>{$tarih2}</tarih2>
<currentTime>{fn:current-dateTime()}</currentTime>
</plan>
(:***************************************************************************:)
 into $calList

 return hs:getPlan($documentUrl, $nextPlanId)
};

(: Calendars Selection Phase Ended :)



(: Jobs and Scenarios Selection Phase :)
(: declare function hs:today-jobs-and-scenarios($n as node()) as node()* :)
declare function hs:select-jobs-and-scenarios($n as node(), $runId as xs:integer, $scenarioId as xs:integer, $planId as xs:integer, $plan as node()*) as node()*
{
	   typeswitch($n)
		case $p as element(dat:jobProperties) 
		   return element dat:jobProperties
		     { 
			   attribute ID {$p/@ID},
			   attribute runId {$runId},
			   attribute planId {$planId},
			   attribute scenarioId {$scenarioId},
			   attribute agentId {$p/@agentId},
			   $p/* 
			 }
	    case $a as element(dat:jobList)
			 return element dat:jobList
			 {
			   for $ca in $a/dat:jobProperties[not(dat:stateInfos/state-types:LiveStateInfos/state-types:LiveStateInfo/state-types:SubstateName/text()='DEACTIVATED') and data(dat:baseJobInfos/dat:jsIsActive) = xs:string("YES")]
			      ,$calendar in $plan[data(calID)=data($ca/dat:baseJobInfos/dat:calendarId)]
			         (: $ca/dat:baseJobInfos/dat:jobInfos/com:jobTypeDef/text()='TIME BASED' and :)
			   return hs:select-jobs-and-scenarios($ca, $runId, $scenarioId, $planId, $plan)
			 }
	    case $es as element(dat:scenario) 
	       return if (count($es//dat:jobProperties)>0) then element dat:scenario
	         { 
			   attribute ID {$es/@ID},
			   attribute runId {$runId},
			   attribute planId {$planId},
			   for $ces in $es/* return hs:select-jobs-and-scenarios($ces, $runId, $es/@ID, $planId, $plan) 
			 } 
			 else ()
		case $d as element(dat:TlosProcessData) 
		   return element dat:TlosProcessData
		     { 
			   attribute ID {$scenarioId},
	           attribute planId {$planId},
               attribute runId {$runId},
			   for $cd in $d/* return hs:select-jobs-and-scenarios($cd, $runId, $scenarioId, $planId, $plan) 
			 }
	     default return $n 
};
		
declare function hs:SelectedJobsAndScenarios($n as node(), $runId as xs:integer, $scenarioId as xs:integer, $planId as xs:integer, $plan as node()*) as node()*
{
		 hs:select-jobs-and-scenarios(hs:select-jobs-and-scenarios($n, $runId, $scenarioId, $planId, $plan), $runId, $scenarioId, $planId, $plan)
};

declare function hs:querySelectedJobsAndScenarios($documentUrl as xs:string, $runId as xs:integer, $scenarioId as xs:integer, $planId as xs:integer, $plan as node()*, $isNewRun as xs:boolean, $isNewPlan as xs:boolean ) as element(dat:TlosProcessData)
{
    let $dataDocumentUrl := met:getMetaData($documentUrl, "sjData")
    let $scenariosDocumentUrl := met:getMetaData($documentUrl, "scenarios")
    let $dataDocument := doc($dataDocumentUrl)

    let $targetScenario := if($scenarioId eq 0)
	                       then 
						     doc($dataDocumentUrl)/dat:TlosProcessData 
						   else 
	                         let $scenario := $dataDocument/dat:TlosProcessData//dat:scenario[@ID=$scenarioId] 
                             let $targetScenarioWithinTPD := if(exists($scenario))
							                                 then 
							                                      element dat:TlosProcessData { 
                                                                     $scenario/dat:baseScenarioInfos,
	                                                                 $scenario/dat:jobList,
																     $scenario/dat:timeManagement,
																     $scenario/dat:advancedScenarioInfos,
																     $scenario/dat:concurrencyManagement,
																     $scenario/dat:scenario/*
																  }
															 else 
															     ()
						     return $targetScenarioWithinTPD

    let $currentRun := hs:SelectedJobsAndScenarios($targetScenario, $runId, $scenarioId, $planId, $plan)

    let $result := if( $isNewRun ) 
	                   then 
					     update insert
		                    element RUN { 
							 attribute id {$runId},  
	                         $currentRun
	                        }
                         into doc($scenariosDocumentUrl)/TlosProcessDataAll
					   else 
					     ()

    return $currentRun

};

(: Jobs and Scenarios Selections Phase Ended :)

(:************************** END OF PLANNING PHASE **********************************:) 

declare function hs:getPlan($documentUrl as xs:string, $planId as xs:integer ) as node()*
{    
    let $planDocumentUrl := met:getMetaData($documentUrl, "plan")
	
  	for $plan in doc($planDocumentUrl)/AllPlanParameters/plan
    where $plan[xs:integer(@id) eq $planId]
	return $plan
};

declare function hs:doPlanAndSelectJobsAndScenarios($documentUrl as xs:string, $scenId as xs:integer, $pId as xs:integer ) as element(dat:TlosProcessData)
{	
    let $scenariosDocumentUrl := met:getMetaData($documentUrl, "scenarios")
	
    let $isNewPlan := if( $pId eq 0 ) then true() else false()

    let $retDailyPlan := if( $isNewPlan ) 
	                     then hs:createPlanCalendars($documentUrl) 
						 else hs:getPlan($documentUrl, sq:getId($documentUrl, "planId") )
	
	let $planId    := xs:integer( if($isNewPlan) then sq:getId($documentUrl, "planId") else $pId )
	
	let $isNewRun := if( $scenId lt 0 ) 
	              then true()
				  else false()

	let $runId := if( $isNewRun ) 
	              then sq:getNextId($documentUrl, "runId")
				  else sq:getId($documentUrl, "runId")

    let $scenarioId := if( $scenId lt 0 )
	                   then 0
					   else $scenId

	let $currentRun := hs:querySelectedJobsAndScenarios($documentUrl, $runId, $scenarioId, $planId, $retDailyPlan, $isNewRun, $isNewPlan)
    
	return $currentRun
(:
	let $runId := sq:getId($documentUrl, "runId")

    let $insertPlanId := hs:insertPlanId($documentUrl, string($runId), string($planId))
    let $insertSolsticeId := hs:insertSolsticeId($documentUrl, string($runId), string($solsticeId))
	:)	
(:
    for $dailyRun in doc($scenariosDocumentUrl)/TlosProcessDataAll/RUN
	where $dailyRun/@id = $runId
	return $dailyRun/dat:TlosProcessData
:)

};

declare function hs:insertPlanId($documentUrl as xs:string, $runId as xs:string, $planId as xs:string){
	let $scenariosDocumentUrl := met:getMetaData($documentUrl, "scenarios")
	
	return update insert attribute planId {data($planId)} into  doc($scenariosDocumentUrl)/TlosProcessDataAll/RUN[@id=data($runId)]/dat:TlosProcessData
};

(:
declare function hs:getSolsticeJobsAndScenarios($documentUrl as xs:string, $scenarioId as xs:integer, $planId as xs:integer )
{	
    let $scenariosDocumentUrl := met:getMetaData($documentUrl, "scenarios")
	
    let $isNewPlan := if( $pId eq 0 ) then true() else false()
    let $planId    := xs:integer( if($isNewPlan) then sq:getNextId($documentUrl, "planId") else $pId )
    let $retDailyPlan := if( $pId eq 0 ) then hs:createPlanCalendars($documentUrl) else hs:getPlan($documentUrl, $planId )
	let $retDailyScenarios := hs:querySelectedJobsAndScenarios($documentUrl, $scenarioId, $planId, $retDailyPlan, $isNewPlan)

	let $runId := sq:getId($documentUrl, "runId")
	let $solsticeId := sq:getId($documentUrl, "solsticeId")

    let $insertPlanId := hs:insertPlanId($documentUrl, string($runId), string($planId))
    let $insertSolsticeId := hs:insertSolsticeId($documentUrl, string($runId), string($solsticeId))
	
	for $dailyRun in doc($scenariosDocumentUrl)/TlosProcessDataAll/RUN
	where $dailyRun/@id = $runId
	return $dailyRun/dat:TlosProcessData
};

declare function hs:insertSolsticeId($documentUrl as xs:string, $runId as xs:string, $solsticeId as xs:string){
	let $scenariosDocumentUrl := met:getMetaData($documentUrl, "scenarios")
	
	return update insert attribute solsticeId {data($solsticeId)} into  doc($scenariosDocumentUrl)/TlosProcessDataAll/RUN[@id=data($runId)]/dat:TlosProcessData
};
:)