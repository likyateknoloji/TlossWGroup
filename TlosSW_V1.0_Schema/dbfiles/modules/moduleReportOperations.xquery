xquery version "1.0";
module namespace hs = "http://hs.tlos.com/";

import module namespace sq = "http://sq.tlos.com/" at "moduleSequenceOperations.xquery";
import module namespace met = "http://meta.tlos.com/" at "moduleMetaDataOperations.xquery";

declare namespace com = "http://www.likyateknoloji.com/XML_common_types";
declare namespace dat="http://www.likyateknoloji.com/XML_data_types";
declare namespace state-types="http://www.likyateknoloji.com/state-types";
declare namespace rep="http://www.likyateknoloji.com/XML_report_types";
declare namespace fn="http://www.w3.org/2005/xpath-functions";

(:
Mappings
$dailyScenariosDocumentUrl = doc("//db/TLOSSW/xmls/tlosSWDailyScenarios10.xml")
$reportsDocumentUrl = doc("//db/TLOSSW/xmls/tlosSWReports10.xml")

:)

(:
Programmed by : Hakan Saribiyik
Version : 0.1
First Release Date : 2 Jan 2013
UpdateDate :
Purpose : Reporting of jobs and their duration and other time related things.
Usage : First step, get related Jobs with getJobsReport(
                 Number of runs that dealt with for the report, 
                 Run Id for which we are focusing and take a referans point, if its value is 0 then it means we are focusing the last run,
				 Job Id if we focusing just a job, or enter 0 for all job related with the second argument,
				 true() if we choose the run id as a referance point, false() otherwise
				 ) function
example;
				 let $run := local:getJobsReport(1,1792,0, true())

Second step, get job array with requested data with getJobArray(
                 output of the getJobsReport function,
				 "ascending|descending" for ascending or descending ordered jobs based on real work time,
				 number of maksimum jobs in the array
                 ) function
example;
return local:getJobArray($run, "ascending", 15)

:)

declare function hs:calculateBaseStats($documentUrl as xs:string, $numberOfElement as xs:int, $runId as xs:int, $jobId as xs:int, $refRunIdBolean as xs:boolean) as node()*
{
  let $runIdx := if( $runId = 0 ) then sq:getId($documentUrl, "runId") (: son run :)
    					 else $runId

  (: Burada son run i bilerek dikkate almiyoruz, ondan onceki 3 run in ortalamasi yeterli :)
  let $localStats :=
    let $arasonuc := <arasonuc> {
                      for $i in (1,2,3)
                       let $getPerStats := hs:getJobsReport($documentUrl, $numberOfElement,$runIdx - $i ,$jobId, $refRunIdBolean)
                       let $getPerStatsExists := if(exists($getPerStats)) then $getPerStats else ()
                       let $hepsi := hs:getJobArray($getPerStatsExists,"descending",50)/@totalDurationInSec
                      return <stat> { $hepsi } </stat>
                     }
                     </arasonuc>

    let $temiz :=    for $i in $arasonuc/stat
                     where $i/@totalDurationInSec[string(.)]
                     return $i
                     
    let $temizArasonuc := <arasonuc count="{count($temiz)}"> {
                     $temiz
    }
                     </arasonuc>
	
	let $maxx := round-half-to-even( max($temizArasonuc/stat/@totalDurationInSec), 2)
	let $minn := round-half-to-even( min($temizArasonuc/stat/@totalDurationInSec), 2)
	let $ortalamaa := round-half-to-even( avg($temizArasonuc/stat/@totalDurationInSec), 2)
	
    let $max :=  if(empty($maxx)) then 0 else $maxx
    let $min := if(empty($minn)) then 0 else $minn
    let $ortalama := if(empty($ortalamaa)) then 0 else $ortalamaa
	
    return <rep:localStats> <rep:max> { $max } </rep:max><rep:min> { $min } </rep:min><rep:avg>{ $ortalama } </rep:avg> 
           </rep:localStats>

    
  return $localStats
};

declare function hs:getJobsReport($documentUrl as xs:string, $numberOfElement as xs:int, $runId as xs:int, $jobId as xs:int, $refRunIdBolean as xs:boolean) as node()*
{
    let $dailyScenariosDocumentUrl := met:getMetaData($documentUrl, "scenarios")

    let $runIdFound := if ($runId = 0) then sq:getId($documentUrl, "runId")
                       else if ($runId < 0) then sq:getId($documentUrl, "runId") + $runId
	                   else $runId 

    let $posUpper := max(for $runx at $pos in doc($dailyScenariosDocumentUrl)/TlosProcessDataAll/RUN
	                 where $runx[@id = $runIdFound] or not($refRunIdBolean)
	                 return $pos)

    let $posLower := if ($posUpper - $numberOfElement > 0) then $posUpper - $numberOfElement else 0

    let $sonuc := for $runx at $pos in doc($dailyScenariosDocumentUrl)/TlosProcessDataAll/RUN
		  where $pos > $posLower and $pos <=$posUpper and $runx//dat:jobProperties[(@ID = $jobId or $jobId = 0)]
		  order by $runx/@id descending
                  return $runx
				  
    let $sonuc2 := for $runx in $sonuc//dat:jobProperties
		  where $runx[(@ID = $jobId or $jobId = 0) and (boolean(@agentId) and not(@agentId='0'))]
		  order by $runx/@id descending
                  return $runx
				  
    return <all> { $sonuc2 } </all>
};

(: Special DateTime format; source : http://www.w3.org/TR/xpath-functions/#dt-dayTimeDuration
   format PnDTnHnMnS, where nD represents the number of days, T is the date/time separator, nH the number of hours, nM the number of minutes and nS the number of seconds. 
   For example, to indicate a duration of 3 days, 10 hours and 30 minutes, one would write: P3DT10H30M.
:)

declare function hs:getJobArray($n as node()*, $order as xs:string, $maxNumOfListedJobs as xs:int) as node()*
{
  let $resultArrayAsc := <rep:jobArray> {
    for $job in $n//dat:jobProperties[boolean(@agentId) and not(@agentId='0')]
	(: hs. is bazen transfering state de kalabiliyor. Bu durumda LSIDateTime dan baslama zamanini aliyoruz. Belkide N/A yapmak gerekir. Emin degilim :)
     let $startdate := if(exists($job/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date)) then $job/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date else xs:string(xs:date(hs:stringToDateTime($job/@LSIDateTime)))
     let $starttime := if(exists($job/dat:timeManagement/dat:jsRealTime/dat:startTime/com:time)) then $job/dat:timeManagement/dat:jsRealTime/dat:startTime/com:time else xs:string(xs:time(hs:stringToDateTime($job/@LSIDateTime)))
     let $stopdate := if(exists($job/dat:timeManagement/dat:jsRealTime/dat:stopTime/com:date)) then $job/dat:timeManagement/dat:jsRealTime/dat:stopTime/com:date else xs:string("N/A")
     let $stoptime := if(exists($job/dat:timeManagement/dat:jsRealTime/dat:stopTime/com:time)) then $job/dat:timeManagement/dat:jsRealTime/dat:stopTime/com:time else xs:string("N/A")

     let $startdatetime := fn:dateTime(xs:date($startdate),xs:time($starttime)) - xs:dateTime("1970-01-01T00:00:00-00:00")
     let $stopdatetime := if( hs:nACheck($stopdate) or hs:nACheck($stoptime) ) 
	                      then xs:dateTime("1970-01-01T00:00:00-00:00") - xs:dateTime("1970-01-01T00:00:00-00:01")
						  else fn:dateTime($stopdate,$stoptime) - xs:dateTime("1970-01-01T00:00:00-00:00") 

     let $startdatetimeDTD := xs:dayTimeDuration($startdatetime) div xs:dayTimeDuration('PT1S')
     let $stopdatetimeDTD  := xs:dayTimeDuration($stopdatetime) div xs:dayTimeDuration('PT1S') 
	 let $datetimeDTD := $stopdatetimeDTD - $startdatetimeDTD

	 let $ordertime := if( hs:nACheck($startdatetime) or hs:nACheck($stopdatetime) ) then xs:dateTime("1970-01-01T00:00:00-00:00") else $datetimeDTD

     let $startTimeFormatted := concat(substring($startdate, 1, 10), 'T', $starttime) 
     let $stopTimeFormatted := if( hs:nACheck($stopdate) or hs:nACheck($stoptime)) 
	                           then xs:string("N/A")
							   else concat(substring($stopdate, 1, 10), 'T', $stoptime) 




     let $diffInTime := if( hs:nACheck($stopdate) ) 
	                    then xs:dayTimeDuration('-PT1S') 
					    else $datetimeDTD
    order by $ordertime
    return <rep:job id="{$job/@ID}" jname="{$job/dat:baseJobInfos/com:jsName}" startTime="{$startTimeFormatted}" stopTime="{$stopTimeFormatted}"> { $diffInTime }</rep:job>
    } </rep:jobArray>

  let $numberOfJobs := count($resultArrayAsc/rep:job) 
  let $numberOfScenarios := count($n//dat:scenario) 
  let $minStartDateTime := min(for $min in $resultArrayAsc/rep:job return if( hs:nACheck($min/@startTime) ) then current-dateTime() else xs:dateTime($min/@startTime))
  let $maxStopDateTime :=  max(for $max in $resultArrayAsc/rep:job return if( hs:nACheck($max/@stopTime) ) then xs:dateTime("1970-01-01T00:00:00-00:01") else xs:dateTime($max/@stopTime))
  (:
     let $maxStopDateTimeDTD := xs:dayTimeDuration($maxStopDateTime) div xs:dayTimeDuration('PT1S')
     let $minStartDateTimeDTD  := xs:dayTimeDuration($minStartDateTime) div xs:dayTimeDuration('PT1S') 
	 let $totalDurationInSec := $maxStopDateTimeDTD - $minStartDateTimeDTD
  :)
  let $totalDuration := $maxStopDateTime - $minStartDateTime
  let $totalDurationInSec := fn:days-from-duration($totalDuration)*24*60*60+fn:hours-from-duration($totalDuration)*60*60+fn:minutes-from-duration($totalDuration)*60+fn:seconds-from-duration($totalDuration)

  return
    if(not(exists($n))) then <rep:jobArray totalDurationInSec = "0" overallStart="N/A" overallStop="N/A" numberOfJobs="0" maxNumOfListedJobs="0" numberOfScenarios="0">  </rep:jobArray> 
    else if(compare($order, "ascending") eq 0) then <rep:jobArray totalDurationInSec = "{$totalDurationInSec}" overallStart="{$minStartDateTime}" overallStop="{$maxStopDateTime}" numberOfJobs="{$numberOfJobs}" maxNumOfListedJobs="{$maxNumOfListedJobs}" numberOfScenarios="{$numberOfScenarios}"> { $resultArrayAsc/rep:job[position()<=$maxNumOfListedJobs]} </rep:jobArray>
    else if(compare($order, "descending") eq 0) then <rep:jobArray totalDurationInSec = "{$totalDurationInSec}" overallStart="{$minStartDateTime}" overallStop="{$maxStopDateTime}" numberOfJobs="{$numberOfJobs}" maxNumOfListedJobs="{$maxNumOfListedJobs}" numberOfScenarios="{$numberOfScenarios}"> { reverse($resultArrayAsc/rep:job)[position()<=$maxNumOfListedJobs] } </rep:jobArray> 
    else <rep:jobArray>-1</rep:jobArray>   
};


declare function hs:getOverallReport($documentUrl as xs:string, $numberOfElement as xs:int, $runId as xs:int, $jobId as xs:int, $refRunIdBolean as xs:boolean, $order as xs:string, $maxNumOfListedJobs as xs:int) as node()*
{
  let $jobsReport := hs:getJobsReport($documentUrl, $numberOfElement, $runId , $jobId, $refRunIdBolean)
  let $result     := hs:getJobArray($jobsReport , $order, $maxNumOfListedJobs)
  return $result
};

declare function hs:getJobArrayXX($n as node()?, $order as xs:string, $maxNumOfListedJobs as xs:int) as node()*
{
  let $resultArrayAsc := <rep:jobArray> {
    for $job in $n//dat:jobProperties[boolean(@agentId) and not(@agentId='0')]
	(: hs. is bazen transfering state de kalabiliyor. Bu durumda LSIDateTime dan baslama zamanini aliyoruz. Belkide N/A yapmak gerekir. Emin degilim :)
     let $startdate := if(exists($job/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date)) then $job/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date else xs:string(xs:date(hs:stringToDateTime($job/@LSIDateTime)))
     let $starttime := if(exists($job/dat:timeManagement/dat:jsRealTime/dat:startTime/com:time)) then $job/dat:timeManagement/dat:jsRealTime/dat:startTime/com:time else xs:string(xs:time(hs:stringToDateTime($job/@LSIDateTime)))
     let $stopdate := if(exists($job/dat:timeManagement/dat:jsRealTime/dat:stopTime/com:date)) then $job/dat:timeManagement/dat:jsRealTime/dat:stopTime/com:date else xs:string("N/A")
     let $stoptime := if(exists($job/dat:timeManagement/dat:jsRealTime/dat:stopTime/com:time)) then $job/dat:timeManagement/dat:jsRealTime/dat:stopTime/com:time else xs:string("N/A")

     let $startdatetime := fn:dateTime(xs:date($startdate),xs:time($starttime)) - xs:dateTime("1970-01-01T00:00:00-00:00")
     let $stopdatetime := if( hs:nACheck($stopdate) or hs:nACheck($stoptime) ) 
	                      then xs:dateTime("1970-01-01T00:00:00-00:00") - xs:dateTime("1970-01-01T00:00:00-00:01")
						  else fn:dateTime($stopdate,$stoptime) - xs:dateTime("1970-01-01T00:00:00-00:00") 

     let $startdatetimeDTD := xs:dayTimeDuration($startdatetime) div xs:dayTimeDuration('PT1S')
     let $stopdatetimeDTD  := xs:dayTimeDuration($stopdatetime) div xs:dayTimeDuration('PT1S') 
	 let $datetimeDTD := $stopdatetimeDTD - $startdatetimeDTD

	 let $ordertime := if( hs:nACheck($startdatetime) or hs:nACheck($stopdatetime) ) then xs:dateTime("1970-01-01T00:00:00-00:00") else $datetimeDTD

     let $startTimeFormatted := concat(substring($startdate, 1, 10), 'T', $starttime) 
     let $stopTimeFormatted := if( hs:nACheck($stopdate) or hs:nACheck($stoptime)) 
	                           then xs:string("N/A")
							   else concat(substring($stopdate, 1, 10), 'T', $stoptime) 




     let $diffInTime := if( hs:nACheck($stopdate) ) 
	                    then xs:dayTimeDuration('-PT1S') 
					    else $datetimeDTD
    order by $ordertime
    return <rep:job id="{$startdatetime}" jname="{$stopdatetime}" startTime="{$startTimeFormatted}" stopTime="{$stopTimeFormatted}"> { $diffInTime }</rep:job>
    } </rep:jobArray>

  let $numberOfJobs := count($resultArrayAsc/rep:job) 
  let $numberOfScenarios := count($n//dat:scenario) 
  let $minStartDateTime := min(for $min in $resultArrayAsc/rep:job return if( hs:nACheck($min/@startTime) ) then current-dateTime() else xs:dateTime($min/@startTime))
  let $maxStopDateTime :=  max(for $max in $resultArrayAsc/rep:job return if( hs:nACheck($max/@stopTime) ) then xs:dateTime("1970-01-01T00:00:00-00:01") else xs:dateTime($max/@stopTime))
  (:
     let $maxStopDateTimeDTD := xs:dayTimeDuration($maxStopDateTime) div xs:dayTimeDuration('PT1S')
     let $minStartDateTimeDTD  := xs:dayTimeDuration($minStartDateTime) div xs:dayTimeDuration('PT1S') 
	 let $totalDurationInSec := $maxStopDateTimeDTD - $minStartDateTimeDTD
  :)
  let $totalDuration := $maxStopDateTime - $minStartDateTime
  let $totalDurationInSec := fn:days-from-duration($totalDuration)*24*60*60+fn:hours-from-duration($totalDuration)*60*60+fn:minutes-from-duration($totalDuration)*60+fn:seconds-from-duration($totalDuration)

  return
    if(not(exists($n))) then <rep:jobArray totalDurationInSec = "0" overallStart="N/A" overallStop="N/A" numberOfJobs="0" maxNumOfListedJobs="0" numberOfScenarios="0">  </rep:jobArray> 
    else if(compare($order, "ascending") eq 0) then <rep:jobArray totalDurationInSec = "{$totalDurationInSec}" overallStart="{$minStartDateTime}" overallStop="{$maxStopDateTime}" numberOfJobs="{$numberOfJobs}" maxNumOfListedJobs="{$maxNumOfListedJobs}" numberOfScenarios="{$numberOfScenarios}"> { $resultArrayAsc/rep:job[position()<=$maxNumOfListedJobs]} </rep:jobArray>
    else if(compare($order, "descending") eq 0) then <rep:jobArray totalDurationInSec = "{$totalDurationInSec}" overallStart="{$minStartDateTime}" overallStop="{$maxStopDateTime}" numberOfJobs="{$numberOfJobs}" maxNumOfListedJobs="{$maxNumOfListedJobs}" numberOfScenarios="{$numberOfScenarios}"> { reverse($resultArrayAsc/rep:job)[position()<=$maxNumOfListedJobs] } </rep:jobArray> 
    else <rep:jobArray>-1</rep:jobArray>   
};

declare function hs:stringToDateTime($t1 as xs:string)
{
    (: Degisik tarih formatlari icin dusunuldu. 2011-10-13T15:08:31+0300 veya 2011-10-13T15:08:31.91+0300 veya 2011-10-13T15:08:31.897+0300 :)
    
    let $t2 := substring-before($t1, '+')
    let $t3 := substring-after($t1, '+')
    let $t7 := concat($t2, '+', substring($t3,1,2) , ':00')
    let $t8 := xs:dateTime($t7)
    
    return $t8
};

declare function hs:nACheck($x as xs:anyAtomicType) as xs:boolean
{
  let $result := if (compare(xs:string($x),xs:string("N/A")) eq 0) 
				 then true() 
				 else false()

  return $result
} ;

declare function hs:stringCheck($x as xs:anyAtomicType) as xs:boolean
{
typeswitch ($x)

case xs:string return true()
default
return false()
} ;

(: jobProperties icinde gelen baslangic ve bitis tarih araligindaki o jobin calisma bilgilerini donuyor :)
declare function hs:getJobs($documentUrl as xs:string, $jobProperty as element(dat:jobProperties), $jobPath) as element(dat:jobProperties)*
{	
    let $dailyScenariosDocumentUrl := met:getMetaData($documentUrl, "scenarios")
	
	let $doc := doc($dailyScenariosDocumentUrl)
	for $jobs in $jobPath (:/TlosProcessDataAll/RUN/dat:TlosProcessData/dat:scenario[com:jsName = 'Senaryo3']/dat:jobList/dat:jobProperties:)
		where $jobs/dat:baseJobInfos/com:jsName = $jobProperty/dat:baseJobInfos/com:jsName and not($jobs[@agentId="0"])
		and ( if(not(empty($jobProperty/dat:timeManagement/dat:jsRealTime/dat:startTime)) and not(empty($jobProperty/dat:timeManagement/dat:jsRealTime/dat:stopTime)))
 				 then 	not(empty($jobs/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date)) 
						and (string($jobs/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date) != '') 
						and	$jobs/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date >= $jobProperty/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date
						and $jobs/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date <= $jobProperty/dat:timeManagement/dat:jsRealTime/dat:stopTime/com:date
				  else true()
			)
	return  $jobs
};

(:
Kullanim:
let $kacEleman := 1
let $jobId := 0 (: Eger belirli bir jobId girilirse o job ile ilgili sonuclar, sifir girilirse butun joblar ile ilgili sonuclar:)
let $runId := 0 (: Eger belirli bir runId girilirse oradan geriye, sifir girilirse en son runId den geriye:)
let $refRunIdBolean := true()  (: Eger true secilirse bu runId yi referans kabul et anlamina gelir. false ise runId yi dikkate almaz.:)

return hs:jobStateListbyRunId($documentUrl, $kacEleman, $runId, $jobId, $refRunIdBolean)
:)
declare function hs:jobStateListbyRunId($documentUrl as xs:string, $numberOfElement as xs:int, $runId as xs:int, $jobId as xs:int, $refRunIdBolean as xs:boolean) as node()*
 {
    let $dailyScenariosDocumentUrl := met:getMetaData($documentUrl, "scenarios")
	
    let $runIdFound := if ($runId != 0 ) 
	                   then $runId 
	                   else sq:getId($documentUrl, "runId")

    let $posUpper := max(for $runx at $pos in doc($dailyScenariosDocumentUrl)/TlosProcessDataAll/RUN
	                 where $runx[@id = $runIdFound] or not($refRunIdBolean)
	                 return $pos)

    let $posLower := if ($posUpper - $numberOfElement > 0) then $posUpper - $numberOfElement else 0

    let $nextId := sq:getNextId($documentUrl, "reportId")
    let $createBlankReport := hs:insertStateReportLock($documentUrl, $jobId, $nextId)

	let $arasonuc := for $runx at $pos in doc($dailyScenariosDocumentUrl)/TlosProcessDataAll/RUN
					 where $pos > $posLower and $pos <=$posUpper and $runx//dat:jobProperties[(@ID = $jobId or $jobId = 0) and @agentId!="0"]
					 order by $runx/@id descending
	                 return hs:jobStateReport($documentUrl, $runx/dat:TlosProcessData, $jobId, $nextId)
    let $sonuc := if(exists($arasonuc)) 
	              then hs:searchStateReportById($documentUrl, sq:getId($documentUrl, "reportId"))
	              else ()
	return $sonuc
};

declare function hs:jobStateReport($documentUrl as xs:string, $n as element(dat:TlosProcessData), $jobId as xs:int, $nextId as xs:int) as node()*
{
   (: Son state leri belirleme kismi :)
   let $propertiesList := $n//dat:jobProperties[(@ID = $jobId or $jobId = 0)]
   let $stateList :=
         for $donbaba in fn:distinct-values($propertiesList/@ID)
         let $jobsInGroup := $propertiesList[@ID = $donbaba]
         return
             let $kacdefa := count($jobsInGroup)
             let $list := if ($kacdefa = 1) then $jobsInGroup[@agentId="0"]/dat:stateInfos/state-types:LiveStateInfos
                          else $jobsInGroup[@agentId!="0"][1]/dat:stateInfos/state-types:LiveStateInfos
         return hs:jobStateReportFromLiveStateInfo($documentUrl, $list, $jobId, $nextId)

    let $sonuc2 := for $runx at $pos in $stateList/state-types:LiveStateInfo
                   order by $runx/@LSIDateTime descending
	               return $runx
	return $sonuc2
 };

declare function hs:jobStateReportFromLiveStateInfo($documentUrl as xs:string, $stateList as element(state-types:LiveStateInfos), $jobId as xs:int, $nextId as xs:int) as node()*
{
    let $reportsDocumentUrl := met:getMetaData($documentUrl, "reports")
	
   let $docrep := doc($reportsDocumentUrl)

	let $sonuc2 := (for $runx at $pos in $stateList/state-types:LiveStateInfo
                   order by $runx/@LSIDateTime descending
	               return $runx)[1]

    (: raporu guncelleme kismi :)
(:    let $ee := data($sonuc2/state-types:StateName)
    let $test := concat(xs:string("rep:reportAll/rep:stateReport/rep:report/rep:"),$ee):)

	let $valStateName := $sonuc2/state-types:StateName
	let $valSubstateName := $sonuc2/state-types:SubstateName
	let $valStatusName := $sonuc2/state-types:StatusName

    let $relPath := $docrep/rep:reportAll/rep:stateReport/rep:report[@id=$nextId]

    let $sorgu := if ( empty($sonuc2/state-types:StatusName) ) 
	           then 
			     if ( empty($sonuc2/state-types:SubstateName) ) 
	             then util:eval(concat("<rep:", $valStateName, ">", $relPath/rep:*[local-name() eq $sonuc2/state-types:StateName]  + 1, "</rep:",$valStateName,">"))
			     else util:eval(concat("<rep:", $valSubstateName, ">", $relPath/rep:*[local-name() eq $sonuc2/state-types:StateName]/rep:*[local-name() eq $sonuc2/state-types:SubstateName]  + 1 , "</rep:",$valSubstateName,">"))
			   else util:eval(concat("<rep:", $valStatusName, ">", $relPath/rep:*[local-name() eq $sonuc2/state-types:StateName]/rep:*[local-name() eq $sonuc2/state-types:SubstateName]/rep:*[local-name() eq $sonuc2/state-types:StatusName] + 1 , "</rep:",$valStatusName,">"))

    let $df := if ( empty($sonuc2/state-types:StatusName) ) 
	           then 
			     if ( empty($sonuc2/state-types:SubstateName) ) 
	             then update replace $relPath/rep:*[local-name() eq $sonuc2/state-types:StateName] 
				                with $relPath/rep:*[local-name() eq $sonuc2/state-types:StateName]  + 1
			     else update replace $relPath/rep:*[local-name() eq $sonuc2/state-types:StateName]/rep:*[local-name() eq $sonuc2/state-types:SubstateName] 
				                with $sorgu
			   else update replace $relPath/rep:*[local-name() eq $sonuc2/state-types:StateName]/rep:*[local-name() eq $sonuc2/state-types:SubstateName]/rep:*[local-name() eq $sonuc2/state-types:StatusName]
			                    with $sorgu

   return <ss>{$sonuc2}</ss>
};

declare function hs:insertStateReportLock($documentUrl as xs:string, $jsId as xs:int, $nextId as xs:int) as xs:boolean
{
   let $reportsDocumentUrl := met:getMetaData($documentUrl, "reports")
	
   let $docrep := doc($reportsDocumentUrl)
   let $relPath := $docrep/rep:reportAll/rep:stateReport
   
   let $sonuc := util:exclusive-lock($relPath, hs:insertBlankStateReport($documentUrl, $jsId, $nextId))    
   return true()
};

declare function hs:insertBlankStateReport($documentUrl as xs:string, $jsId as xs:int, $nextId as xs:int) as node()*
{
   let $reportsDocumentUrl := met:getMetaData($documentUrl, "reports")
   
   let $docrep := doc($reportsDocumentUrl)
   let $relPath := $docrep/rep:reportAll/rep:stateReport

(:    let $nextId := sq:getNextId("reportId")	:)
    let $scope := xs:string("job")
	return update insert 
		<rep:report xmlns="http://www.likyateknoloji.com/XML_report_types" id="{$nextId}" jsId="{$jsId}" scope="{$scope}" LSIDateTime="{current-dateTime()}">
			<rep:PENDING>
				<rep:CREATED>0</rep:CREATED>
				<rep:DEACTIVATED>0</rep:DEACTIVATED>
				<rep:VALIDATED>0</rep:VALIDATED>
				<rep:IDLED>
					<rep:BYTIME>0</rep:BYTIME>
					<rep:BYUSER>0</rep:BYUSER>
                    <rep:BYEVENT>0</rep:BYEVENT>
				</rep:IDLED>
				<rep:READY>
					<rep:LOOKFOR-RESOURCE>0</rep:LOOKFOR-RESOURCE>
					<rep:USER-CHOOSE-RESOURCE>0</rep:USER-CHOOSE-RESOURCE>
					<rep:USER-WAITING>0</rep:USER-WAITING>
					<rep:WAITING>0</rep:WAITING>
					<rep:TRANSFERING>0</rep:TRANSFERING>
				</rep:READY>
				<rep:PAUSED>0</rep:PAUSED>
			</rep:PENDING>
			<rep:RUNNING>
				<rep:STAGE-IN>0</rep:STAGE-IN>
				<rep:MIGRATING>0</rep:MIGRATING>
				<rep:ON-RESOURCE>
					<rep:TIME-IN>0</rep:TIME-IN>
					<rep:TIME-OUT>0</rep:TIME-OUT>
				</rep:ON-RESOURCE>
				<rep:HELD>0</rep:HELD>
				<rep:STAGE-OUT>0</rep:STAGE-OUT>
			</rep:RUNNING>
			<rep:FAILED>0</rep:FAILED>
			<rep:CANCELLED>0</rep:CANCELLED>
			<rep:FINISHED>
				<rep:COMPLETED>
					<rep:SUCCESS>0</rep:SUCCESS>
					<rep:WARNING>0</rep:WARNING>
					<rep:FAILED>0</rep:FAILED>
				</rep:COMPLETED>
				<rep:SKIPPED>0</rep:SKIPPED>
				<rep:STOPPED>0</rep:STOPPED>
			</rep:FINISHED>
		</rep:report>
	into $relPath
} ;

declare function hs:searchStateReportById($documentUrl as xs:string, $reportId as xs:int) as element(rep:report)? 
 {
    let $reportsDocumentUrl := met:getMetaData($documentUrl, "reports")
	
    let $docrep := doc($reportsDocumentUrl)
	let $relPath := $docrep/rep:reportAll/rep:stateReport/rep:report

	for $report in $relPath
	where $report/@id = $reportId
    return $report
};
