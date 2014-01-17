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
                 Which document,
                 Number of items that dealt with for the report for the chosen run id, scenario id, job id combination, 
                 Run Id for which we are focusing and take a referans point, if its value is 0 then it means we are focusing the last run,
				 Scenario Id if we focusing the jobs within a scenario, or enter 0 for all scenarios related with the run id,
				 Job Id if we focusing just a job, or enter 0 for all job related with the run id and scenario id given,
				 true() if we choose the run id as a referance point, false() otherwise
				 Whether unfinished jobs used for stats
				 ) function
example;
				 let $run := hs:getJobsReport(1,1792,0, true())

Second step, get job array with requested data with getJobArray(
                 output of the getJobsReport function,
				 "ascending|descending" for ascending or descending ordered jobs based on real work time,
				 number of maksimum jobs in the array
				 Whether unfinished jobs used for stats
                 ) function
example;
return hs:getJobArray($run, "ascending", 15, false())

:)

declare function hs:calculateBaseStats($documentUrl as xs:string, $reportParameters as element(rep:reportParameters) ) as node()*
{
  let $arithmeticA  := $reportParameters/rep:arithmeticA
  let $isCumulative := $arithmeticA/@isCumulative

  let $setA  := $reportParameters/rep:setA
  let $jobId := $setA/@jobId
  let $scenarioId := $setA/@scenarioId
  let $runId := $setA/@runId
  let $justFirstLevel := xs:boolean($setA/@justFirstLevel)
  let $maxNumOfListedJobs := $setA/@maxNumOfListedJobs
  let $docId := $setA/@docId
  let $isGlobal := xs:boolean($setA/@isGlobal)
  let $countInstancesAsOne := xs:boolean($setA/@countInstancesAsOne)

  let $stateRelatedA2 := $reportParameters/rep:stateRelatedA2
  let $includedJobs  := $stateRelatedA2/@includedJobs
  let $includePendingJobs  := xs:boolean($stateRelatedA2/@includePendingJobs)

  let $statisticsA := $reportParameters/rep:statisticsA
  let $statSampleNumber  := $statisticsA/@statSampleNumber

  let $timeRelatedA1 := $reportParameters/rep:timeRelatedA1
  let $startDateTime := xs:dateTime($timeRelatedA1/@startDateTime)
  let $endDateTime   := xs:dateTime($timeRelatedA1/@endDateTime)
  let $typeOfTime    := $timeRelatedA1/@typeOfTime
  let $timeZone      := $timeRelatedA1/@timeZone
  let $automaticTimeInterval := $timeRelatedA1/@automaticTimeInterval

  let $userA := $reportParameters/rep:userA
  let $userId := $userA/@userId
  let $role := $userA/@role
  
  let $runIdx := if ($runId = 0) 
                 then sq:getId($documentUrl, "runId")
                 else if ($runId < 0) 
				      then sq:getId($documentUrl, "runId") + $runId
                      else $runId 
					   
  let $isJob := if( compare($jobId, '0') eq 0 ) 
                then false() 
				else true()
  
  (: Burada son run i bilerek dikkate almiyoruz, ondan onceki 3 run in ortalamasi yeterli :)
  let $localStats :=
    let $arasonuc := <arasonuc> {
                      for $i in (1 to $statSampleNumber)
					   let $reportParametersNew := element rep:reportParameters {
					                                                     $reportParameters/rep:arithmeticA,
																		 $reportParameters/rep:historyA,
																		 element rep:setA { 
	                                                                        attribute jobId {$setA/@jobId},
	                                                                        attribute scenarioId {$setA/@scenarioId},
                                                                            attribute runId {$runIdx - $i},
	                                                                        attribute justFirstLevel {xs:boolean($setA/@justFirstLevel)},
	                                                                        attribute maxNumOfListedJobs {$setA/@maxNumOfListedJobs},
                                                                            attribute docId {$setA/@docId},
																			attribute isGlobal {xs:boolean($setA/@isGlobal)},
																			attribute countInstancesAsOne {xs:boolean($setA/@countInstancesAsOne)}																 
																		 },
																		 $reportParameters/rep:sortingA,
																		 $reportParameters/rep:stateRelatedA1,
																		 $reportParameters/rep:stateRelatedA2,
																		 $reportParameters/rep:statisticsA,
																		 $reportParameters/rep:timeRelatedA1,
																		 $reportParameters/rep:timeRelatedA2,
																		 $reportParameters/rep:userA
																		 }
				  
                       let $getPerStats := hs:getJobsReport($documentUrl, $reportParametersNew)   (: $numberOfElement,$runIdx - $i ,$jobId, $refRunIdBolean, $includeNonResultedRuns) :)
                       let $getPerStatsExists := if(exists($getPerStats)) then $getPerStats else ()
                       let $sonuc :=  hs:getJobArray($getPerStatsExists, $reportParametersNew)
                       let $hepsi := $sonuc/@totalDurationInSec
                       let $overallStart := $sonuc/@overallStart
                       let $overallStop := $sonuc/@overallStop
                      where $overallStart!=""
                      return <stat> {$hepsi, $overallStart, $overallStop} </stat>
                      }
                     </arasonuc>

    let $temiz :=    for $i in $arasonuc/stat
                     where $i/@totalDurationInSec[string(.)]
                     return $i
                     
    let $temizArasonuc := <arasonuc count="{count($temiz)}"> {
                     $temiz
    }
                     </arasonuc>
	
    let $overallStartt :=    min(for $i in $arasonuc/stat
                     return xs:dateTime($i/@overallStart))
    let $overallStopp :=    max(for $i in $arasonuc/stat
                     return xs:dateTime($i/@overallStop))
                     
	let $maxx := round-half-to-even( max($temizArasonuc/stat/@totalDurationInSec), 2)
	let $minn := round-half-to-even( min($temizArasonuc/stat/@totalDurationInSec), 2)
	let $ortalamaa := round-half-to-even( avg($temizArasonuc/stat/@totalDurationInSec), 2)


    
    let $max :=  if(empty($maxx)) then 0 else $maxx
    let $min := if(empty($minn)) then 0 else $minn
    let $ortalama := if(empty($ortalamaa)) then 0 else $ortalamaa
    let $overallStart := if(empty($overallStartt)) then 0 else $overallStartt
    let $overallStop := if(empty($overallStopp)) then 0 else $overallStopp
      
    return <rep:localStats overallStart="{$overallStart}" overallStop="{$overallStop}"> <rep:max> { $max } </rep:max><rep:min> { $min } </rep:min><rep:avg>{ $ortalama } </rep:avg> 
           </rep:localStats>

    
  return $localStats
};

declare function hs:getJobsReport($documentUrl as xs:string, $reportParameters as element(rep:reportParameters) ) as node()*
{

  let $historyA  := $reportParameters/rep:historyA
  let $numberOfRuns := $historyA/@numberOfRuns

  let $setA  := $reportParameters/rep:setA
  let $jobId := $setA/@jobId
  let $scenarioId := $setA/@scenarioId
  let $runId := $setA/@runId
  let $justFirstLevel := xs:boolean($setA/@justFirstLevel)
  let $maxNumOfListedJobs := $setA/@maxNumOfListedJobs
  let $docId := $setA/@docId
  let $isGlobal := xs:boolean($setA/@isGlobal)
  let $countInstancesAsOne := xs:boolean($setA/@countInstancesAsOne)

  let $stateRelatedA2 := $reportParameters/rep:stateRelatedA2
  let $includedJobs  := $stateRelatedA2/@includedJobs
  let $includePendingJobs  := xs:boolean($stateRelatedA2/@includePendingJobs)


    let $dailyScenariosDocumentUrl := met:getMetaData($documentUrl, "scenarios")

    let $runIdFound := if ($runId = 0) then sq:getId($documentUrl, "runId")
                       else if ($runId < 0) then sq:getId($documentUrl, "runId") + $runId
                       else $runId 

    let $posUpper := max(for $runx at $pos in doc($dailyScenariosDocumentUrl)/TlosProcessDataAll/RUN
                     where $runx[@id = $runIdFound]   (: or not($refRunIdBoolean) :)
	                 return $pos)

    let $posLower := if ($posUpper - $numberOfRuns > 0) then $posUpper - $numberOfRuns else 0

    let $runElements := for $runx at $pos in doc($dailyScenariosDocumentUrl)/TlosProcessDataAll/RUN
		  where $pos > $posLower and $pos <=$posUpper
		  order by $runx/@id descending
                  return $runx


   let $requestedJobs := for $x in $runElements/dat:TlosProcessData
                         let $chosen :=
                          if($scenarioId eq xs:string("0")) then
                            if($justFirstLevel)
                            then (: $rootScenarioFirstLevelJobs :)
                             for $runx in $x/dat:jobList/dat:jobProperties
                             where $runx[(@ID = $jobId or $jobId = 0) and ( (boolean(@agentId) and not(@agentId='0')) or $includePendingJobs)]
                             order by $runx/@id descending
                             return $runx
                            else (: $rootScenarioAllJobs :)
                             for $runx in $x//dat:jobProperties
                             where $runx[(@ID = $jobId or $jobId = 0) and ( (boolean(@agentId) and not(@agentId='0')) or $includePendingJobs)]
                             order by $runx/@id descending
                             return $runx
                          else
                            if($justFirstLevel)
                            then (: $otherScenarioFirstLevelJobs :)
                              for $runx in $x//dat:scenario[@ID = $scenarioId]/dat:jobList/dat:jobProperties
                              where $runx[(@ID = $jobId or $jobId = 0) and ( (boolean(@agentId) and not(@agentId='0')) or $includePendingJobs)]
                              order by $runx/@id descending
                              return $runx
                            else (: $otherScenarioAllJobs :)
                              for $runx in $x//dat:scenario[@ID = $scenarioId]//dat:jobProperties
                              where $runx[(@ID = $jobId or $jobId = 0) and ( (boolean(@agentId) and not(@agentId='0')) or $includePendingJobs)]
                              order by $runx/@id descending
                              return $runx
                              
                         let $jobInstances := <jobList>{
                             for $job in $chosen
                             group by $ID := $job/@ID
                             order by $ID
                             return <jobInstances name="{$ID}">
                                   {  $job }
                                  </jobInstances>
                             }</jobList>   

         
                         let  $propertiesList := for $job in $jobInstances/jobInstances
						                         let $count := count($job/dat:jobProperties)

                                                 let $orderedJobList := for $cc in $job/dat:jobProperties
                                                                        order by $cc/@agentId ascending
                                                                        return $cc
												 let $selectedJobList := if($count = 1) 
												                         then $orderedJobList[1] 
																         else $orderedJobList[position()!=1]
                                                 return $selectedJobList
              
                         return $propertiesList
						 
    let $yjob := for $xjob in $requestedJobs
                   let $scenarioId := $xjob/@scenarioId
                   let $scenario :=  if($scenarioId eq "0") 
                                     then $runElements/dat:TlosProcessData[@ID eq $scenarioId]
                                     else $runElements/dat:TlosProcessData//dat:scenario[@ID eq $scenarioId]
                   let $isJobFinished := hs:isJobFinished($xjob/dat:stateInfos/state-types:LiveStateInfos)
                   
                   let $startdate := hs:getJobStartDate( $xjob )
                   let $starttime := hs:getJobStartTime( $xjob )
                       
                   let $stopdate  := hs:getJobStopDate( $xjob )
                   let $stoptime  := hs:getJobStopTime( $xjob )

                   let $startDateTime := xs:string( dateTime($startdate, $starttime) )
                   let $endDateTimeforUnfinishedJobs := if(exists($xjob/@LSIDateTime)) 
                                                        then $xjob/@LSIDateTime
                                                        else current-dateTime()
                                          
                   let $stopDateTime := if( hs:nACheck($stopdate) or hs:nACheck($stoptime)) 
                               then xs:string( fn:adjust-dateTime-to-timezone( $endDateTimeforUnfinishedJobs, timezone-from-dateTime($startDateTime)) )
                        	   else xs:string(dateTime($stopdate, $stoptime))
                               
                   let $LSIDateTimeX := if($isJobFinished) 
                                        then xs:string($stopDateTime)
                                        else $scenario/@LSIDateTime
        
                   let $updatedJob := element dat:jobProperties { 
                        attribute ID {$xjob/@ID},
                        attribute runId {$xjob/@runId},
                        attribute planId {$xjob/@planId},
                        attribute scenarioId {$xjob/@scenarioId},
                        attribute agentId {$xjob/@agentId},
                        attribute LSIDateTime {$LSIDateTimeX},
                        $xjob/*
                    }
                   (: let $guncelle := 
                                    if (exists($xjob/@LSIDateTime))
                                    then update value $xjob/@LSIDateTime with $scenario/@LSIDateTime
                                    else update insert attribute LSIDateTime {$scenario/@LSIDateTime} into $xjob :)
                 return $updatedJob
    
    return <all> { $yjob } </all>
};

(: Special DateTime format; source : http://www.w3.org/TR/xpath-functions/#dt-dayTimeDuration
   format PnDTnHnMnS, where nD represents the number of days, T is the date/time separator, nH the number of hours, nM the number of minutes and nS the number of seconds. 
   For example, to indicate a duration of 3 days, 10 hours and 30 minutes, one would write: P3DT10H30M.
:)

declare function hs:getJobArray($n as node()*, $reportParameters as element(rep:reportParameters)) as node()
{
  let $arithmeticA  := $reportParameters/rep:arithmeticA
  let $isCumulative := xs:boolean($arithmeticA/@isCumulative)

  let $setA  := $reportParameters/rep:setA
  let $jobId := $setA/@jobId
  let $scenarioId := $setA/@scenarioId
  let $runId := $setA/@runId
  let $justFirstLevel := xs:boolean($setA/@justFirstLevel)
  let $maxNumOfListedJobs := $setA/@maxNumOfListedJobs
  let $docId := $setA/@docId
  let $isGlobal := xs:boolean($setA/@isGlobal)
  let $countInstancesAsOne := xs:boolean($setA/@countInstancesAsOne)

  let $sortingA  := $reportParameters/rep:sortingA
  let $orderBy := $sortingA/@orderBy
  let $order := $sortingA/@order

  let $stateRelatedA2 := $reportParameters/rep:stateRelatedA2
  let $includedJobs  := $stateRelatedA2/@includedJobs
  let $includePendingJobs  := xs:boolean($stateRelatedA2/@includePendingJobs)

  let $resultArrayAsc := <rep:jobArray> {
     for $job in $n/dat:jobProperties
    (: hs. is bazen transfering state de kalabiliyor. Bu durumda LSIDateTime dan baslama zamanini aliyoruz. Belkide N/A yapmak gerekir. Emin degilim :)
    
     let $isJobFinished := hs:isJobFinished($job/dat:stateInfos/state-types:LiveStateInfos)
      
     let $startdate := hs:getJobStartDate( $job )
     let $starttime := hs:getJobStartTime( $job )
                       
     let $stopdate  := hs:getJobStopDate( $job )
     let $stoptime  := hs:getJobStopTime( $job )

     let $startDateTime := xs:string( dateTime($startdate, $starttime) )
     let $endDateTimeforUnfinishedJobs := if(exists($job/@LSIDateTime)) 
                                          then $job/@LSIDateTime
                                          else current-dateTime()
     let $stopDateTime := if( hs:nACheck($stopdate) or hs:nACheck($stoptime)) 
                               then xs:string( fn:adjust-dateTime-to-timezone( $endDateTimeforUnfinishedJobs, timezone-from-dateTime($startDateTime)) )
                    		   else xs:string(dateTime($stopdate, $stoptime))
                    
     let $pendingDT := hs:getJobStateDT( $job/dat:stateInfos/state-types:LiveStateInfos, xs:string("PENDING"), xs:string("IDLED"), xs:string("BYTIME"))                        
     let $runningDT := hs:getJobStateDT( $job/dat:stateInfos/state-types:LiveStateInfos, xs:string("RUNNING"), xs:string("STAGE-IN"), xs:string("TIME-IN"))
     let $finishDT := hs:getJobStateDT( $job/dat:stateInfos/state-types:LiveStateInfos, xs:string("FINISHED"), xs:string(""), xs:string(""))
     let $finishSuccessDT := hs:getJobStateDT( $job/dat:stateInfos/state-types:LiveStateInfos, xs:string("FINISHED"), xs:string("COMPLETED"), xs:string("SUCCESS"))
     let $finishFailDT := hs:getJobStateDT( $job/dat:stateInfos/state-types:LiveStateInfos, xs:string("FINISHED"), xs:string("COMPLETED"), xs:string("FAILED"))
     let $finishResult := if(exists($finishSuccessDT)) 
                          then xs:string("success")
                          else if(exists($finishFailDT)) 
                               then xs:string("fail")
                               else ()
     let $pendingDTN := if(exists($pendingDT)) then $pendingDT else xs:dateTime("1970-01-01T00:00:00-00:01")
     let $runningDTN := if(exists($runningDT)) then $runningDT else xs:dateTime("1970-01-01T00:00:00-00:01")
     let $finishDTN := if(exists($finishDT)) then $finishDT else xs:dateTime("1970-01-01T00:00:00-00:01")
	 
     let $diffInTime :=  hs:total-seconds-from-duration( xs:dateTime($stopDateTime) - xs:dateTime($startDateTime))

     let $orderByTD := if( compare($orderBy, xs:string("DURATION")) = 0 ) 
                       then $diffInTime 
                       else if( compare($orderBy, xs:string("STARTTIME")) = 0 )
                       then dateTime($startdate, $starttime)
                       else dateTime($stopdate, $stoptime)
     let $includeJob := $isJobFinished or $includePendingJobs
     where $includeJob
     order by $orderByTD
    return <rep:job id="{$job/@ID}" jname="{$job/dat:baseJobInfos/com:jsName}" startTime="{$startDateTime}" stopTime="{$stopDateTime}" pendingTime="{$pendingDT}" runningTime="{$runningDT}" finishTime="{$finishDT}" isFinished="{$isJobFinished}" result="{$finishResult}" LSIDateTime="{$job/@LSIDateTime}" runId="{$job/@runId}" planId="{$job/@planId}" scenarioId="{$job/@scenarioId}" agentId="{$job/@agentId}" > { $diffInTime }</rep:job>
    }
    </rep:jobArray>
    
  let $numberOfJobs := count($resultArrayAsc/rep:job) 
  let $numberOfScenarios := count($n//dat:scenario)
  let $minStartDateTime := min(for $min in $resultArrayAsc/rep:job return if( hs:nACheck($min/@startTime) ) then current-dateTime() else xs:dateTime($min/@startTime))
  let $maxStopDateTime :=  max(for $max in $resultArrayAsc/rep:job return if( hs:nACheck($max/@stopTime) ) then xs:dateTime("1970-01-01T00:00:00-00:01") else xs:dateTime($max/@stopTime))
  
  let $totalDurationBetweenFirstAndLastJobs := xs:dateTime($maxStopDateTime) - xs:dateTime($minStartDateTime)
   
  let $durationList := 
    for $dur in $resultArrayAsc/rep:job
    where not(hs:nACheck($dur/@stopTime)) or $includePendingJobs (: ya sonlanmis is olacak yada pending state de olsa bile listeye dahil et denecek :)
    return
      $dur


  let $isUnfinishedCount := 
    count( for $dur in $resultArrayAsc/rep:job
           where ( not(hs:nACheck($dur/@stopTime)) or $includePendingJobs) and not(xs:boolean($dur/@isFinished)) (: ya sonlanmis is olacak yada sonuclanmasa bile listeye dahil et denecek :)
           return $dur )
  
  let $isFinished := if( $isUnfinishedCount>0 ) then false() else true()
  
  let $totalDurationInSec := if($isCumulative) then sum(data($durationList))
                                               else hs:total-seconds-from-duration($totalDurationBetweenFirstAndLastJobs)
                                             

  
  let $result :=     
      if(not(exists($n))) 
      then <rep:jobArray totalDurationInSec = "0" overallStart="N/A" overallStop="N/A" isFinished="true" numberOfJobs="0" maxNumOfListedJobs="0" numberOfScenarios="0">  </rep:jobArray> 
      else 
          if(compare($order, xs:string("ascending")) eq 0) 
          then <rep:jobArray totalDurationInSec = "{$totalDurationInSec}" overallStart="{$minStartDateTime}" overallStop="{$maxStopDateTime}" isFinished="{$isFinished}" numberOfJobs="{$numberOfJobs}" maxNumOfListedJobs="{$maxNumOfListedJobs}" numberOfScenarios="{$numberOfScenarios}"> { $resultArrayAsc/rep:job[position()<=$maxNumOfListedJobs]} </rep:jobArray>
          else 
              if(compare($order, xs:string("descending")) eq 0) 
              then <rep:jobArray totalDurationInSec = "{$totalDurationInSec}" overallStart="{$minStartDateTime}" overallStop="{$maxStopDateTime}" isFinished="{$isFinished}" numberOfJobs="{$numberOfJobs}" maxNumOfListedJobs="{$maxNumOfListedJobs}" numberOfScenarios="{$numberOfScenarios}"> { reverse($resultArrayAsc/rep:job)[position()<=$maxNumOfListedJobs] } </rep:jobArray> 
              else <rep:jobArray>-1</rep:jobArray>   
      
 return $result
      
};

declare function hs:getJobStateDT( $states as element(state-types:LiveStateInfos), $stateName as xs:string, $substateName as xs:string, $statusName as xs:string) as xs:string*
{
    let $theState :=
                 for $state in $states/state-types:LiveStateInfo[state-types:StateName/text() eq $stateName]
                   let $thisState := if(exists($state))
                                then 
                                  if( ($state[ state-types:SubstateName/text() eq $substateName or xs:string("") eq $substateName ] and 
                                       $state[ state-types:StatusName/text() eq $statusName or xs:string("") eq $statusName ]
                                      )
                                    )
                                  then 
                                    $state
                                  else
                                    ()
                                else
                                  ()
                 return $thisState
    return $theState/@LSIDateTime
};

declare function hs:getOverallReport($documentUrl as xs:string, $reportParameters as element(rep:reportParameters)) as node()*
{
  let $setA  := $reportParameters/rep:setA
  let $jobId := $setA/@jobId

  let $isJob := if( $jobId eq '0' ) then false() else true()
	
  let $jobsReport := hs:getJobsReport($documentUrl, $reportParameters)
  let $result     := hs:getJobArray($jobsReport , $reportParameters)
  return $result
};

declare function hs:getJobStartDate($job as element(dat:jobProperties) ) as xs:string 
{
  
     let $date := if(exists($job/dat:management/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date)) 
                       then 
                           $job/dat:management/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date
                       else 
                           if(exists($job/@LSIDateTime)) 
                           then xs:string(xs:date(hs:stringToDateTime($job/@LSIDateTime))) 
                           else xs:string(xs:date(hs:stringToDateTime($job/dat:stateInfos/state-types:LiveStateInfos/state-types:LiveStateInfo[1]/@LSIDateTime))) 
                           
    return $date
    
};

declare function hs:getJobStartTime($job as element(dat:jobProperties) ) as xs:string 
{
  
     let $date := if(exists($job/dat:management/dat:timeManagement/dat:jsRealTime/dat:startTime/com:time)) 
                       then 
                           $job/dat:management/dat:timeManagement/dat:jsRealTime/dat:startTime/com:time
                       else 
                           if(exists($job/@LSIDateTime)) 
                           then xs:string(xs:time(hs:stringToDateTime($job/@LSIDateTime))) 
                           else xs:string(xs:time(hs:stringToDateTime($job/dat:stateInfos/state-types:LiveStateInfos/state-types:LiveStateInfo[1]/@LSIDateTime))) 
                           
    return $date
    
};

declare function hs:getJobStopDate($job as element(dat:jobProperties) ) as xs:string 
{
  
     let $date := if(exists($job/dat:management/dat:timeManagement/dat:jsRealTime/dat:stopTime/com:date)) 
                       then 
                           $job/dat:management/dat:timeManagement/dat:jsRealTime/dat:stopTime/com:date
                       else 
                           xs:string("N/A")
                           
    return $date
    
};

declare function hs:getJobStopTime($job as element(dat:jobProperties) ) as xs:string 
{
  
     let $date := if(exists($job/dat:management/dat:timeManagement/dat:jsRealTime/dat:stopTime/com:time)) 
                       then 
                           $job/dat:management/dat:timeManagement/dat:jsRealTime/dat:stopTime/com:time
                       else 
                           xs:string("N/A")
                           
    return $date
    
};

declare function hs:get-dayTimeDuration-from-dateTimes($dateTime1 as xs:dateTime) as xs:dayTimeDuration
{
    $dateTime1 - xs:dateTime("1970-01-01T00:00:00-00:00")
};

declare function hs:get-dayTimeDuration-from-dateTimes($dateTime1 as xs:dateTime, $dateTime2 as xs:dateTime) as xs:dayTimeDuration
{
    $dateTime1 - $dateTime2
};

declare function hs:isJobFinished( $states as element(state-types:LiveStateInfos) ) as xs:boolean
{
    if( compare( $states/state-types:LiveStateInfo[position() = 1]/state-types:StateName/text(), xs:string("FINISHED")) eq 0 ) 
    then true() 
    else false()
};

declare function hs:total-seconds-from-duration 
  ( $duration as xs:dayTimeDuration? )  as xs:decimal? {
       
   $duration div xs:dayTimeDuration('PT1S')
 } ;
 

declare function hs:total-duration-from-seconds
  ( $duration as xs:decimal )  as xs:dayTimeDuration? {
       
   $duration * xs:dayTimeDuration('PT1S')
 } ;
 
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
		and ( if(not(empty($jobProperty/dat:management/dat:timeManagement/dat:jsRealTime/dat:startTime)) and not(empty($jobProperty/dat:management/dat:timeManagement/dat:jsRealTime/dat:stopTime)))
 				 then 	not(empty($jobs/dat:management/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date)) 
						and (string($jobs/dat:management/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date) != '') 
						and	$jobs/dat:management/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date >= $jobProperty/dat:management/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date
						and $jobs/dat:management/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date <= $jobProperty/dat:management/dat:timeManagement/dat:jsRealTime/dat:stopTime/com:date
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
declare function hs:jobStateListbyRunId($documentUrl as xs:string, $planIdInput as xs:integer, $reportParameters as element(rep:reportParameters)) as node()*
 {
 (: $numberOfElement as xs:int, $runId as xs:int, $jobId as xs:int, $refRunIdBolean as xs:boolean :)
 
    let $jobList := hs:getJobsReport($documentUrl, $reportParameters)
    let $planId := if($planIdInput = 0 ) 
                   then sq:getId($documentUrl, "planId") 
                   else $planIdInput
	let $setA  := $reportParameters/rep:setA
    let $jobId := $setA/@jobId
  
    let $dailyScenariosDocumentUrl := met:getMetaData($documentUrl, "scenarios")

    let $nextId := sq:getNextId($documentUrl, "reportId")

    let $createBlankReport := hs:insertStateReportLock($documentUrl, $jobId, $planId, $nextId)

	let $arasonuc := hs:jobStateReport($documentUrl, $jobList, $jobId, $nextId)

    let $sonuc := if(exists($arasonuc)) 
                  then hs:searchStateReportById($documentUrl, sq:getId($documentUrl, "reportId"))
	              else ()
	return $sonuc
};

declare function hs:jobStateReport($documentUrl as xs:string, $n as node(), $jobId as xs:int, $nextId as xs:int) as node()*
{
   (: Son state leri belirleme kismi :)
   
    let $result :=
     for $job in $n/dat:jobProperties
     
     let $stateLast := (for $lsi in $job/dat:stateInfos/state-types:LiveStateInfos/state-types:LiveStateInfo
	                    order by hs:stringToDateTime($lsi/@LSIDateTime) descending
						return $lsi)[1]
     return hs:jobStateReportFromLiveStateInfo($documentUrl, $stateLast, $jobId, $nextId)

    return $n
 };

declare function hs:jobStateReportFromLiveStateInfo($documentUrl as xs:string, $stateLast as element(state-types:LiveStateInfo), $jobId as xs:int, $nextId as xs:int) as node()*
{
    let $reportsDocumentUrl := met:getMetaData($documentUrl, "reports")
    
   let $docrep := doc($reportsDocumentUrl)

    (: raporu guncelleme kismi :)

	let $valStateName := $stateLast/state-types:StateName
	let $valSubstateName := $stateLast/state-types:SubstateName
	let $valStatusName := $stateLast/state-types:StatusName

    let $relPath := $docrep/rep:reportAll/rep:stateReport/rep:report[@id=$nextId]

    let $sorgu := if ( empty($stateLast/state-types:StatusName) ) 
	           then 
			     if ( empty($stateLast/state-types:SubstateName) ) 
	             then util:eval(concat("<rep:", $valStateName, ">", $relPath/rep:*[local-name() eq $stateLast/state-types:StateName]  + 1, "</rep:",$valStateName,">"))
			     else util:eval(concat("<rep:", $valSubstateName, ">", $relPath/rep:*[local-name() eq $stateLast/state-types:StateName]/rep:*[local-name() eq $stateLast/state-types:SubstateName]  + 1 , "</rep:",$valSubstateName,">"))
			   else util:eval(concat("<rep:", $valStatusName, ">", $relPath/rep:*[local-name() eq $stateLast/state-types:StateName]/rep:*[local-name() eq $stateLast/state-types:SubstateName]/rep:*[local-name() eq $stateLast/state-types:StatusName] + 1 , "</rep:",$valStatusName,">"))

    let $df := if ( empty($stateLast/state-types:StatusName) ) 
	           then 
			     if ( empty($stateLast/state-types:SubstateName) ) 
	             then update replace $relPath/rep:*[local-name() eq $stateLast/state-types:StateName] 
				                with $relPath/rep:*[local-name() eq $stateLast/state-types:StateName]  + 1
			     else update replace $relPath/rep:*[local-name() eq $stateLast/state-types:StateName]/rep:*[local-name() eq $stateLast/state-types:SubstateName] 
				                with $sorgu
			   else update replace $relPath/rep:*[local-name() eq $stateLast/state-types:StateName]/rep:*[local-name() eq $stateLast/state-types:SubstateName]/rep:*[local-name() eq $stateLast/state-types:StatusName]
			                    with $sorgu

   return $stateLast
};

declare function hs:insertStateReportLock($documentUrl as xs:string, $jsId as xs:int, $planId as xs:integer, $nextId as xs:int) as xs:boolean
{
   let $reportsDocumentUrl := met:getMetaData($documentUrl, "reports")
	
   let $docrep := doc($reportsDocumentUrl)
   let $relPath := $docrep/rep:reportAll/rep:stateReport
   
   let $sonuc := util:exclusive-lock($relPath, hs:insertBlankStateReport($documentUrl, $jsId, $planId, $nextId))    
   return true()
};

declare function hs:insertBlankStateReport($documentUrl as xs:string, $jsId as xs:int, $planId as xs:integer, $nextId as xs:int) as node()*
{
   let $reportsDocumentUrl := met:getMetaData($documentUrl, "reports")
   
   let $docrep := doc($reportsDocumentUrl)
   let $relPath := $docrep/rep:reportAll/rep:stateReport

(:    let $nextId := sq:getNextId("reportId")	:)
    let $scope := xs:string("job")
	return update insert 
		<rep:report xmlns="http://www.likyateknoloji.com/XML_report_types" id="{$nextId}" jsId="{$jsId}" scope="{$scope}" planId="{$planId}" LSIDateTime="{current-dateTime()}">
			<rep:PENDING>
				<rep:CREATED>
					<rep:DEVELOPMENT>0</rep:DEVELOPMENT>
					<rep:TEST>0</rep:TEST>
                    <rep:REQUEST>0</rep:REQUEST>
					<rep:DEPLOYED>0</rep:DEPLOYED>
				</rep:CREATED>
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
