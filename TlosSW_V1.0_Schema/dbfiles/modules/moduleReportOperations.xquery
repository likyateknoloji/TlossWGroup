xquery version "1.0";
module namespace hs = "http://hs.tlos.com/";

import module namespace sq = "http://sq.tlos.com/" at "xmldb:exist://db/TLOSSW/modules/moduleSequenceOperations.xquery";

declare namespace com = "http://www.likyateknoloji.com/XML_common_types";
declare namespace dat="http://www.likyateknoloji.com/XML_data_types";
declare namespace state-types="http://www.likyateknoloji.com/state-types";
declare namespace rep="http://www.likyateknoloji.com/XML_report_types";


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
                 output of the getJonsReport function,
				 "ascending|descending" for ascending or descending ordered jons based on real work time,
				 number of maksimum jobs in the array
                 ) function
example;
return local:getJobArray($run, "ascending", 15)

:)

declare function hs:getJobsReport($numberOfElement as xs:int, $runId as xs:int, $jobId as xs:int, $refRunIdBolean as xs:boolean) as node()*
 {

    let $runIdFound := if ($runId != 0 ) 
	                   then $runId 
	                   else sq:getId("runId")

    let $posUpper := max(for $runx at $pos in doc("//db/TLOSSW/xmls/tlosSWDailyScenarios10.xml")/TlosProcessDataAll/RUN
	                 where $runx[@id = $runIdFound] or not($refRunIdBolean)
	                 return $pos)

    let $posLower := if ($posUpper - $numberOfElement > 0) then $posUpper - $numberOfElement else 0

    let $sonuc := for $runx at $pos in doc("//db/TLOSSW/xmls/tlosSWDailyScenarios10.xml")/TlosProcessDataAll/RUN
		  where $pos > $posLower and $pos <=$posUpper and $runx//dat:jobProperties[(@ID = $jobId or $jobId = 0) and @agentId!="0"]
		  order by $runx/@id descending
                  return $runx
    return $sonuc
};

(: Special DateTime format; source : http://www.w3.org/TR/xpath-functions/#dt-dayTimeDuration
   format PnDTnHnMnS, where nD represents the number of days, T is the date/time separator, nH the number of hours, nM the number of minutes and nS the number of seconds. 
   For example, to indicate a duration of 3 days, 10 hours and 30 minutes, one would write: P3DT10H30M.
:)

declare function hs:getJobArray($n as node(), $order as xs:string, $maxNumOfListedJobs) as node()*
{
  let $resultArrayAsc := <jobArray> {
    for $job in $n//dat:jobProperties[@agentId!="0"]
     let $startdate := if(exists($job/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date)) then $job/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date else current-date()
     let $starttime := if(exists($job/dat:timeManagement/dat:jsRealTime/dat:startTime/com:time)) then $job/dat:timeManagement/dat:jsRealTime/dat:startTime/com:time else current-time()
     let $stopdate := if(exists($job/dat:timeManagement/dat:jsRealTime/dat:stopTime/com:date)) then $job/dat:timeManagement/dat:jsRealTime/dat:stopTime/com:date else "N/A"
     let $stoptime := if(exists($job/dat:timeManagement/dat:jsRealTime/dat:stopTime/com:time)) then $job/dat:timeManagement/dat:jsRealTime/dat:stopTime/com:time else "N/A"

     let $startdatetime := (fn:dateTime($startdate,$starttime) - xs:dateTime("1970-01-01T00:00:00-00:00"))
     let $stopdatetime := if(compare($stopdate,"N/A") ne 0) 
	                      then fn:dateTime($stopdate,$stoptime) - xs:dateTime("1970-01-01T00:00:00-00:00") 
						  else xs:dateTime("1970-01-01T00:00:00-00:00") - xs:dateTime("1970-01-01T00:00:00-00:01")
     let $startTimeFormatted := concat(substring($startdate, 1, 10), 'T', $starttime) 
     let $stopTimeFormatted := if(compare($stopdate,"N/A") ne 0) 
	                           then concat(substring($stopdate, 1, 10), 'T', $stoptime) 
							   else "N/A"
    let $diffInTime := if(compare($stopdate,"N/A") ne 0) then $stopdatetime - $startdatetime else xs:dayTimeDuration('-PT1S')
    order by ($stopdatetime - $startdatetime)
    return <job id="{$job/@ID}" jname="{$job/dat:baseJobInfos/com:jsName}" startTime="{$startTimeFormatted}" stopTime="{$stopTimeFormatted}"> { (($diffInTime) div xs:dayTimeDuration('PT1S')) }</job>
    } </jobArray>

  let $numberOfJobs := count($resultArrayAsc/job) 
  let $numberOfScenarios := count($n//dat:scenario) 
  let $minStartDateTime := min(for $min in $resultArrayAsc/job return if(compare($min/@startTime,"N/A") ne 0) then xs:dateTime($min/@startTime) else current-dateTime())
  let $maxStopDateTime :=  max(for $max in $resultArrayAsc/job return if(compare($max/@stopTime,"N/A") ne 0) then xs:dateTime($max/@stopTime)  else xs:dateTime("1970-01-01T00:00:00-00:01"))
  let $totalDuration := $maxStopDateTime - $minStartDateTime
  let $totalDurationInSec := fn:days-from-duration($totalDuration)*24*60*60+fn:hours-from-duration($totalDuration)*60*60+fn:minutes-from-duration($totalDuration)*60+fn:seconds-from-duration($totalDuration)

  return
    if(compare($order, "ascending") eq 0) then <jobArray totalDurationInSec = "{$totalDurationInSec}" overallStart="{$minStartDateTime}" overallStop="{$maxStopDateTime}" numberOfJobs="{$numberOfJobs}" maxNumOfListedJobs="{$maxNumOfListedJobs}" numberOfScenarios="{$numberOfScenarios}"> { $resultArrayAsc/job[position()<=$maxNumOfListedJobs]} </jobArray>
    else if(compare($order, "descending") eq 0) then <jobArray totalDurationInSec = "{$totalDurationInSec}" overallStart="{$minStartDateTime}" overallStop="{$maxStopDateTime}" numberOfJobs="{$numberOfJobs}" maxNumOfListedJobs="{$maxNumOfListedJobs}" numberOfScenarios="{$numberOfScenarios}"> { reverse($resultArrayAsc/job)[position()<=$maxNumOfListedJobs] } </jobArray> 
    else <jobArray>-1</jobArray>   
};

(: jobProperties icinde gelen baslangic ve bitis tarih araligindaki o jobin calisma bilgilerini donuyor :)
declare function hs:getJobs($jobProperty as element(dat:jobProperties), $jobPath) as element(dat:jobProperties)*
{	
	let $doc := doc("//db/TLOSSW/xmls/tlosSWDailyScenarios10.xml")
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

declare function hs:jobStateListbyRunId($numberOfElement as xs:int, $runId as xs:int, $jobId as xs:int, $refRunIdBolean as xs:boolean) as node()*
 {

    let $runIdFound := if ($runId != 0 ) 
	                   then $runId 
	                   else sq:getId("runId")

    let $posUpper := max(for $runx at $pos in doc("//db/TLOSSW/xmls/tlosSWDailyScenarios10.xml")/TlosProcessDataAll/RUN
	                 where $runx[@id = $runIdFound] or not($refRunIdBolean)
	                 return $pos)

    let $posLower := if ($posUpper - $numberOfElement > 0) then $posUpper - $numberOfElement else 0

    let $nextId := sq:getNextId("reportId")
    let $createBlankReport := hs:insertStateReportLock($jobId, $nextId)

	let $sonuc := for $runx at $pos in doc("//db/TLOSSW/xmls/tlosSWDailyScenarios10.xml")/TlosProcessDataAll/RUN
					 where $pos > $posLower and $pos <=$posUpper and $runx//dat:jobProperties[(@ID = $jobId or $jobId = 0) and @agentId!="0"]
					 order by $runx/@id descending
	                 return hs:jobStateReport($runx/dat:TlosProcessData, $jobId, $nextId)

	return $sonuc
};

declare function hs:jobStateReport($n as element(dat:TlosProcessData), $jobId as xs:int, $nextId as xs:int) as node()*
{
   (: Son state leri belirleme kismi :)
   let $stateList :=
         for $donbaba in $n//dat:jobProperties[(@ID = $jobId or $jobId = 0) and @agentId!="0"]/dat:stateInfos/state-types:LiveStateInfos
	     order by $donbaba descending
	     return hs:jobStateReportFromLiveStateInfo($donbaba, $jobId, $nextId)

	let $sonuc2 := for $runx at $pos in $stateList/state-types:LiveStateInfo
                   order by $runx/@LSIDateTime descending
	               return $runx
	return $n
 };

declare function hs:jobStateReportFromLiveStateInfo($stateList as element(state-types:LiveStateInfos), $jobId as xs:int, $nextId as xs:int) as node()*
{
   let $docrep := doc("//db/TLOSSW/xmls/tlosSWReports10.xml")

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

declare function hs:insertStateReportLock($jsId as xs:int, $nextId as xs:int) as xs:boolean
{
   let $docrep := doc("//db/TLOSSW/xmls/tlosSWReports10.xml")
   let $relPath := $docrep/rep:reportAll/rep:stateReport
   
   let $sonuc := util:exclusive-lock($relPath, hs:insertBlankStateReport($jsId, $nextId))    
   return true()
};

declare function hs:insertBlankStateReport($jsId as xs:int, $nextId as xs:int) as node()*
{

   let $docrep := doc("//db/TLOSSW/xmls/tlosSWReports10.xml")
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

declare function hs:searchStateReportById($reportId as xs:int) as element(rep:report)? 
 {
    let $docrep := doc("//db/TLOSSW/xmls/tlosSWReports10.xml")
	let $relPath := $docrep/rep:reportAll/rep:stateReport/rep:report

	for $report in $relPath
	where $report/@id = $reportId
    return $report
};

(:declare function hs:getJobs($jobProperty as element(dat:jobProperties), $jobPath) as element(dat:jobProperties)*
{	
	let $doc := doc("//db/TLOSSW/xmls/tlosSWDailyScenarios10.xml")
	for $jobs in $jobPath (:/TlosProcessDataAll/RUN/dat:TlosProcessData/dat:scenario[com:jsName = 'Senaryo3']/dat:jobList/dat:jobProperties:)
		where $jobs/com:jsName = $jobProperty/com:jsName
		and ( if(not(empty($jobProperty/dat:jobRealTime/dat:startTime)) and not(empty($jobProperty/dat:jobRealTime/dat:stopTime)))
 				 then 	not(empty($jobs/dat:jobRealTime/dat:startTime/com:date/com:year)) 
						and (string($jobs/dat:jobRealTime/dat:startTime/com:date/com:year) != '') 
						and not(empty($jobs/dat:jobRealTime/dat:startTime/com:date/com:month))
						and (string($jobs/dat:jobRealTime/dat:startTime/com:date/com:month) != '')
						and not(empty($jobs/dat:jobRealTime/dat:startTime/com:date/com:day))
						and (string($jobs/dat:jobRealTime/dat:startTime/com:date/com:day) != '')
						and	xs:date(concat($jobs/dat:jobRealTime/dat:startTime/com:date/com:year,'-', $jobs/dat:jobRealTime/dat:startTime/com:date/com:month,'-',$jobs/dat:jobRealTime/dat:startTime/com:date/com:day ) ) 
							>=  xs:date(concat($jobProperty/dat:jobRealTime/dat:startTime/com:date/com:year,'-', $jobProperty/dat:jobRealTime/dat:startTime/com:date/com:month,'-',$jobProperty/dat:jobRealTime/dat:startTime/com:date/com:day ) ) 
						and xs:date(concat($jobs/dat:jobRealTime/dat:startTime/com:date/com:year,'-', $jobs/dat:jobRealTime/dat:startTime/com:date/com:month,'-',$jobs/dat:jobRealTime/dat:startTime/com:date/com:day ) ) 
							<=  xs:date(concat($jobProperty/dat:jobRealTime/dat:stopTime/com:date/com:year,'-', $jobProperty/dat:jobRealTime/dat:stopTime/com:date/com:month,'-',$jobProperty/dat:jobRealTime/dat:stopTime/com:date/com:day ) )	
				  else ("TRUE")
			)
	return  $jobs
};:)

