xquery version "1.0";
module namespace hs = "http://hs.tlos.com/";

import module namespace sq = "http://sq.tlos.com/" at "xmldb:exist://db/TLOSSW/modules/moduleSequenceOperations.xquery";

declare namespace com = "http://www.likyateknoloji.com/XML_common_types";
declare namespace dat="http://www.likyateknoloji.com/XML_data_types";
declare namespace state-types="http://www.likyateknoloji.com/state-types";
declare namespace rep="http://www.likyateknoloji.com/XML_report_types";
declare namespace fn="http://www.w3.org/2005/xpath-functions";

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

(:
Kullanim:
let $kacEleman := 1
let $jobId := 0 (: Eger belirli bir jobId girilirse o job ile ilgili sonuclar, sifir girilirse butun joblar ile ilgili sonuclar:)
let $runId := 0 (: Eger belirli bir runId girilirse oradan geriye, sifir girilirse en son runId den geriye:)
let $refRunIdBolean := true()  (: Eger true secilirse bu runId yi referans kabul et anlamina gelir. false ise runId yi dikkate almaz.:)

return hs:jobStateListbyRunId($kacEleman, $runId, $jobId, $refRunIdBolean)
:)
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
   let $propertiesList := $n//dat:jobProperties[(@ID = $jobId or $jobId = 0)]
   let $stateList :=
         for $donbaba in fn:distinct-values($propertiesList//dat:jobProperties/@ID)
         let $jobsInGroup := $propertiesList//dat:jobProperties[@ID = $donbaba]
         return
             let $kacdefa := count($jobsInGroup)
             let $list := if ($kacdefa = 1) then $jobsInGroup[@agentId="0"]/dat:stateInfos/state-types:LiveStateInfos
                          else $jobsInGroup[@agentId!="0"]/dat:stateInfos/state-types:LiveStateInfos
	     return hs:jobStateReportFromLiveStateInfo($list, $jobId, $nextId)
 (:  let $stateList :=
         for $donbaba in $n//dat:jobProperties[(@ID = $jobId or $jobId = 0) and @agentId!="0"]/dat:stateInfos/state-types:LiveStateInfos
	     order by $donbaba descending
	     return hs:jobStateReportFromLiveStateInfo($donbaba, $jobId, $nextId)
:)
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

