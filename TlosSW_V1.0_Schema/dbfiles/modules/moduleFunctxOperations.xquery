xquery version "1.0";

(: http://www.xqueryfunctions.com/ 

the FunctX XQuery Function Library
http://www.xqueryfunctions.com/xq/alpha.html
for any description of functions defined below
:)

module namespace functx = "http://www.functx.com/";

declare function functx:is-value-in-sequence 
  ( $value as xs:anyAtomicType? ,
    $seq as xs:anyAtomicType* )  as xs:boolean {
       
   $value = $seq
 } ;
 
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

declare function functx:time 
  ( $hour as xs:anyAtomicType ,
    $minute as xs:anyAtomicType ,
    $second as xs:anyAtomicType )  as xs:time {
       
   xs:time(
     concat(
       functx:pad-integer-to-length(xs:integer($hour),2),':',
       functx:pad-integer-to-length(xs:integer($minute),2),':',
       functx:pad-integer-to-length(xs:integer($second),2)))
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
