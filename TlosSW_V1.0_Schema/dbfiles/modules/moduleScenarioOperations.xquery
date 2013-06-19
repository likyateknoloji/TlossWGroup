xquery version "1.0";
module namespace hs = "http://hs.tlos.com/";

import module namespace sq = "http://sq.tlos.com/" at "../modules/moduleSequenceOperations.xquery";

declare namespace com = "http://www.likyateknoloji.com/XML_common_types";
declare namespace dat="http://www.likyateknoloji.com/XML_data_types";
declare namespace state-types="http://www.likyateknoloji.com/state-types";
declare namespace fn = "http://www.w3.org/2005/xpath-functions";

(:-----------------------------------------------------------------------------------------------------------:)
(:--------------------------------------- TlosProcessData operasyonlari-------------------------------------:)

(: READ :)
declare function hs:getTlosDataXml($documentUrl as xs:string) as element(dat:TlosProcessData)?
{
	for $tlosProcessData in doc($documentUrl)/dat:TlosProcessData
	return $tlosProcessData 
};

(:-----------------------------------------------------------------------------------------------------------:)
(:----------------------------------------- Senaryo operasyonlari -------------------------------------------:)

(: READ :)
declare function hs:getScenario($documentUrl as xs:string, $scenarioPath, $scenarioName as xs:string) as element(dat:scenario)?
{	
	let $doc := doc($documentUrl)
	for $scenario in $doc//$scenarioPath 
    where $scenario/dat:baseScenarioInfos/com:jsName = $scenarioName
		return $scenario
};

declare function hs:getScenarioExistence($documentUrl as xs:string, $scenarioPath as node()*, $scenarioName as xs:string) as xs:integer
{    
    let $doc := doc($documentUrl) 
    let $refPath := $doc//$scenarioPath

    let $exactMatch := count($refPath/dat:scenario/dat:baseScenarioInfos[.//normalize-space(com:jsName/text()) = normalize-space(xs:string($scenarioName))])
    let $partialMatchIn := count($refPath//dat:scenario/dat:baseScenarioInfos[.//normalize-space(com:jsName/text()) = normalize-space(xs:string($scenarioName))])
    let $partialMatchOut := count($doc//dat:scenario/dat:baseScenarioInfos[.//normalize-space(com:jsName/text()) = normalize-space(xs:string($scenarioName))])
    
    let $sonuc := if($exactMatch > 0) then 1 (: ayni senaryoda var :)
                  else if($partialMatchIn > 0) then 2 (: Senaryonun icindeki bir senaryoda var :)
                  else if($partialMatchOut > 0) then 3 (: Senaryonun disindaki bir senaryoda var :)
                  else 0
    return $sonuc
};

declare function hs:getScenarioFromId($documentUrl as xs:string, $id as xs:integer) as element(dat:scenario)?
{	
	let $doc := doc($documentUrl) 
	for $scenario in $doc//dat:scenario
        where $scenario/@ID = $id
        return $scenario
};

declare function hs:scenarioList($documentUrl as xs:string) as element(dat:scenario)* 
 {
	for $scenario in doc($documentUrl)/dat:TlosProcessData//dat:scenario
	return  $scenario
};

(: INSERT :)
declare function hs:insertScenarioLock($documentUrl as xs:string, $scenario as element(dat:scenario), $scenarioPath )
{
   util:exclusive-lock(doc($documentUrl)/dat:TlosProcessData, hs:insertScenario($documentUrl, $scenario,$scenarioPath))     
};

declare function hs:insertScenario($documentUrl as xs:string, $scenario as element(dat:scenario), $scenarioPath)
 {	
	let $doc := doc($documentUrl)
	for $xmlScenario in $doc//$scenarioPath
		return  update insert $scenario into $xmlScenario
};

declare function hs:insertScenarioCalendarId($documentUrl as xs:string, $scenarioPath, $calendarId){
    let $doc := doc($documentUrl)
	return update insert $calendarId following  $doc//$scenarioPath/dat:baseScenarioInfos/com:comment
};

declare function hs:insertScnCalendar($documentUrl as xs:string, $scenarioPath, $scenario){
    let $calIdInsert :=  hs:insertScenarioCalendarId($documentUrl, $scenarioPath, $scenario/dat:calendarId)
    return $calIdInsert
};

declare function hs:insertScenarioDepList($documentUrl as xs:string, $scenarioPath, $dependencyList as element()){
    let $doc := doc($documentUrl)
	return update insert $dependencyList preceding $doc//$scenarioPath/dat:jobList
};


(: DELETE :)
declare function hs:deleteScenarioLock($documentUrl as xs:string, $scenario as element(dat:scenario), $scenarioPath )
{
	util:exclusive-lock(doc($documentUrl)/dat:TlosProcessData, hs:deleteScenario($documentUrl, $scenario,$scenarioPath))
};

declare function hs:deleteScenario($documentUrl as xs:string, $scenario as element(dat:scenario),$scenarioPath )
{
	let $doc := doc($documentUrl)
	for $senaryo in $doc//$scenarioPath 
        where $senaryo/@ID = $scenario/@ID
        return update delete $senaryo
};

declare function hs:deleteScenarioCalendarId($documentUrl as xs:string, $scenarioPath)
{
	let $doc := doc($documentUrl)
	for $calendarId in $doc//$scenarioPath/dat:baseScenarioInfos/dat:calendarId 
        return update delete $calendarId
};

declare function hs:deleteScnCalendar($documentUrl as xs:string, $scenarioPath){
    let $calIdInsert :=  hs:deleteScenarioCalendarId($documentUrl, $scenarioPath)
    return $calIdInsert
};

declare function hs:deleteScenarioDep($documentUrl as xs:string, $scenarioPath as node())
{
	let $doc := doc($documentUrl)
	for $depList in $doc//$scenarioPath/dat:DependencyList 
        return update delete $depList
};

(: UPDATE :)
declare function hs:updateScenarioName($documentUrl as xs:string, $scenarioPath, $name){
	let $doc := doc($documentUrl)
    return update replace $doc//$scenarioPath/dat:baseScenarioInfos/com:jsName with <com:jsName>{data($name)}</com:jsName>
};

declare function hs:updateScenarioComment($documentUrl as xs:string, $scenarioPath as node(),$comment){
	let $doc := doc($documentUrl)
    return update replace $doc//$scenarioPath/dat:baseScenarioInfos/com:comment with <com:comment>{data($comment)}</com:comment>
};

declare function hs:updateScenarioCalendarId($documentUrl as xs:string, $scenarioPath as node(),$calendarId){
	let $doc := doc($documentUrl)
    return update replace $doc//$scenarioPath/dat:baseScenarioInfos/dat:calendarId with <dat:calendarId>{data($calendarId)}</dat:calendarId>
};

declare function hs:updateScnCalendar($documentUrl as xs:string, $scenarioPath as node(),$scenario){
    let $calIdUpdate :=  hs:updateScenarioCalendarId($documentUrl, $scenarioPath,$scenario/dat:calendarId)
    return $calIdUpdate 	
};

declare function hs:updateScenarioUser($documentUrl as xs:string, $scenarioPath as node(),$user){
	let $doc := doc($documentUrl)
    return update replace $doc//$scenarioPath/dat:baseScenarioInfos/com:userId with <com:userId>{data($user)}</com:userId>
};

declare function hs:updateScenarioDepList($documentUrl as xs:string, $scenarioPath as node(),$dependencyList as element()){
	let $doc := doc($documentUrl)
	return update replace $doc//$scenarioPath/dat:DependencyList with $dependencyList 		
};

(: Kullanilmiyor 
declare function hs:updateScenarioSuccessCodes($documentUrl as xs:string, $scenarioPath as node(),$successCodeList as element()){
	let $doc := doc($documentUrl)
	return update replace $doc//$scenarioPath/com:jsSuccessCodeList with $successCodeList 		
};
:)

declare function hs:updateScenario($documentUrl as xs:string, $scenarioPath as node(),$scenario as element(dat:scenario)){

    let $XXX := $scenario
	let $doc :=  doc($documentUrl)
	let $updateBaseScenarioInfos :=  update replace $doc//$scenarioPath/dat:baseScenarioInfos with 
	                   <dat:baseScenarioInfos>{$XXX/dat:baseScenarioInfos/*}</dat:baseScenarioInfos>

	let $updateDependencyList := if(count($scenario/dat:DependencyList)>0) then 
	                               update replace $doc//$scenarioPath/dat:DependencyList with 
	                                 <dat:DependencyList>{$XXX/dat:DependencyList/*}</dat:DependencyList>
	                             else ()
	let $updateJobList :=  update replace $doc//$scenarioPath/dat:jobList with 
	                   <dat:jobList>{$XXX/dat:jobList/*}</dat:jobList>

	let $updateScenarioStatusList := if(count($scenario/state-types:ScenarioStatusList)>0) then 
	                               update replace $doc//$scenarioPath/state-types:ScenarioStatusList with 
	                                 <state-types:ScenarioStatusList>{$XXX/state-types:ScenarioStatusList/*}</state-types:ScenarioStatusList>
	                             else ()
	let $updateAlarmPreference := if(count($scenario/dat:alarmPreference)>0) then 
	                               update replace $doc//$scenarioPath/dat:alarmPreference with 
	                                 <dat:alarmPreference>{$XXX/dat:alarmPreference/*}</dat:alarmPreference>
	                             else ()
	let $updateTimeManagement := if(count($scenario/dat:timeManagement)>0) then 
	                               update replace $doc//$scenarioPath/dat:timeManagement with 
	                                 <dat:timeManagement>{$XXX/dat:timeManagement/*}</dat:timeManagement>
	                             else ()
	let $updateAdvancedScenarioInfos :=  update replace $doc//$scenarioPath/dat:advancedScenarioInfos with 
	                   <dat:advancedScenarioInfos>{$XXX/dat:advancedScenarioInfos/*}</dat:advancedScenarioInfos>
	let $updateConcurrencyManagement :=  update replace $doc//$scenarioPath/dat:concurrencyManagement with 
	                   <dat:concurrencyManagement>{$XXX/dat:concurrencyManagement/*}</dat:concurrencyManagement>
	let $updateLocalParameters := if(count($scenario/com:localParameters)>0) then 
	                               update replace $doc//$scenarioPath/com:localParameters with 
	                                 <com:localParameters>{$XXX/com:localParameters/*}</com:localParameters>
	                             else ()
	let $updateScenario := if(count($scenario/scenario)>0) then 
	                         for $sdon1 in $doc//$scenarioPath/scenario,
							     $sdon2 in $scenario/scenario
							 where $sdon1/@ID = $sdon2/@ID
	                         return update replace $doc//$scenarioPath/scenario with 
	                                 <scenario>{hs:updateScenario($documentUrl, $scenarioPath/scenario, $scenario/scenario)}</scenario>
	                       else ()

	return <ok/>
};

declare function hs:updateScenarioLock($documentUrl as xs:string, $scenarioPath as node(),$scenario as element(dat:scenario))
{
	util:exclusive-lock(doc($documentUrl)/dat:TlosProcessData, hs:updateScenario($documentUrl, $scenarioPath,$scenario))
};

(:-----------------------------------------------------------------------------------------------------------:)
(:---------------------------------------- Job operasyonlari ------------------------------------------------:)

(: READ :)
declare function hs:getJob($documentUrl as xs:string, $jobPath ,$jobName as xs:string) as element(dat:jobProperties)?
{	
	let $doc := doc($documentUrl) 

	for $job in $doc//$jobPath/dat:jobProperties
        where $job/dat:baseJobInfos/com:jsName = $jobName
        return $job
};

declare function hs:getJobFromId($documentUrl as xs:string, $id as xs:integer) as element(dat:jobProperties)?
{	
	let $doc := doc($documentUrl)
	for $job in $doc//dat:jobProperties
        where $job/@ID = $id
        return $job
};


declare function hs:getJobExistence($documentUrl as xs:string, $jobPath as node()*, $jobName as xs:string) as xs:integer
{    
    let $doc := doc($documentUrl)
    let $refPath := $doc//$jobPath
    let $refPathPrevious := $refPath/..
    let $exactMatch := count($refPath/dat:jobProperties[.//com:jsName/text() = $jobName])
    let $partialMatchIn  := count($refPathPrevious//dat:jobProperties[.//com:jsName/text() = $jobName])
    let $partialMatchOut := count($doc//dat:jobProperties[.//com:jsName/text() = $jobName])
    
    let $sonuc := if($exactMatch > 0) then 1 (: ayni senaryoda var :)
                  else if($partialMatchIn > 0) then 2 (: Senaryonun icindeki bir senaryoda var :)
                  else if($partialMatchOut > 0) then 3 (: Senaryonun disindaki bir senaryoda var :)
                  else 0
    return  $sonuc
};

(:hs:getJobExistence(xs:string("tlosSWData10.xml"), /dat:TlosProcessData/dat:scenario/dat:jobList, xs:string("job1.bat"), 13) :)

declare function hs:getJobFromJobName($documentUrl as xs:string, $jobName as xs:string) as element(dat:jobProperties)?
{	
	let $doc := doc($documentUrl)
	for $job in $doc//dat:jobProperties
        where $job/dat:baseJobInfos/com:jsName = $jobName
        return $job
};

(: ornek kullanim lk:jobList(1,2) ilk uc eleman :)
declare function hs:jobList($documentUrl as xs:string, $firstElement as xs:int, $lastElement as xs:int) as element(dat:jobProperties)* 
 {
	for $jobd in doc($documentUrl)/dat:TlosProcessData//dat:jobProperties[position() = ($firstElement to $lastElement)]
	return  $jobd
};

(: INSERT :)
declare function hs:insertJobLock($documentUrl as xs:string, $jobProperty as element(dat:jobProperties),$jobPath)
{
   util:exclusive-lock(doc($documentUrl)/dat:TlosProcessData, hs:insertJob($documentUrl, $jobProperty,$jobPath))     
};

declare function hs:insertJob($documentUrl as xs:string, $jobProperty as element(dat:jobProperties),$jobPath)
{	
	let $doc := doc($documentUrl)
	for $xmlJobList in $doc//$jobPath
		return  update insert $jobProperty into $xmlJobList
};

(: UPDATE :)
declare function hs:updateJobLock($documentUrl as xs:string, $jobProperty as element(dat:jobProperties),$jobPath)
{
   util:exclusive-lock(doc($documentUrl)/dat:TlosProcessData, hs:updateJob($documentUrl, $jobProperty,$jobPath))     
};

declare function hs:updateJob($documentUrl as xs:string, $jobProperty as element(dat:jobProperties),$jobPath)
{	
	let $doc := doc($documentUrl)
	for $job in $doc//$jobPath/dat:jobProperties            
        where $job/@ID = $jobProperty/@ID
        return update replace $job with $jobProperty
};

(: DELETE :)
declare function hs:deleteJobLock($documentUrl as xs:string, $jobProperty as element(dat:jobProperties),$jobPath )
{
   util:exclusive-lock(doc($documentUrl)/dat:TlosProcessData, hs:deleteJob($documentUrl, $jobProperty,$jobPath))     
};

declare function hs:deleteJob($documentUrl as xs:string, $jobProperty as element(dat:jobProperties),$jobPath )
{	
	let $jobCount := count($jobPath)
    let $doc := doc($documentUrl)

	for $senaryo in $doc
	return if($jobCount > 0)
           then
			    for $job in $doc//$jobPath/dat:jobProperties 
                where $job/dat:baseJobInfos/com:jsName = $jobProperty/dat:baseJobInfos/com:jsName
                return update delete $job
           else ()
};

(:-----------------------------------------------------------------------------------------------------------:)
(:---------------------------------------- Job Result Doc Operations ------------tlosSWDailyScenarios10.xml--:)

(: READ :)

declare function hs:jobResultListByDates($documentUrl as xs:string, $jobId as xs:int, $date1 as xs:date, $date2 as xs:date, $refRunIdBolean as xs:boolean) as element(dat:jobProperties)*
{

                let $sonuc := for $runx in doc($documentUrl)/TlosProcessDataAll/RUN//dat:jobProperties
                              where $runx[(@ID = $jobId or $jobId = 0) and @agentId!="0"]
                                    and not(empty($runx/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date))
                                    and	$runx/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date >= xs:date($date1)
                                    and	$runx/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date <= xs:date($date2)
                                    order by $runx/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date
                                    return   $runx
                return  $sonuc
};

declare function hs:jobResultListbyRunId($documentUrl as xs:string, $numberOfElement as xs:int, $runId as xs:int, $jobId as xs:int, $refRunIdBolean as xs:boolean) as element(dat:jobProperties)*
 {

    let $runIdFound := if ($runId != 0 ) 
	                   then $runId 
	                   else sq:getId("runId")

    let $posUpper := max(for $runx at $pos in doc($documentUrl)/TlosProcessDataAll/RUN
	                 where $runx[@id = $runIdFound] or not($refRunIdBolean)
	                 return $pos)

    let $posLower := if ($posUpper - $numberOfElement > 0) then $posUpper - $numberOfElement else 0

	let $sonuc := for $runx at $pos in doc($documentUrl)/TlosProcessDataAll/RUN
					 where $pos > $posLower and $pos <=$posUpper and $runx//dat:jobProperties[(@ID = $jobId or $jobId = 0) and @agentId!="0"]
					 order by $runx/@id descending
	                 return  $runx//dat:jobProperties[(@ID = $jobId or $jobId = 0) and @agentId!="0"]
	return $sonuc
};

declare function hs:jobResultListByDates($documentUrl as xs:string, $jobId as xs:int, $date1 as xs:date, $date2 as xs:date, $refRunIdBolean as xs:boolean) as element(dat:jobProperties)*
{

                let $sonuc := for $runx in doc($documentUrl)/TlosProcessDataAll/RUN//dat:jobProperties
                              where $runx[(@ID = $jobId or $jobId = 0) and @agentId!="0"]
                                    and not(empty($runx/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date))
                                    and	$runx/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date >= xs:date($date1)
                                    and	$runx/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date <= xs:date($date2)
                                    order by $runx/dat:timeManagement/dat:jsRealTime/dat:startTime
                                    return   $runx
                return  $sonuc
};

(: INSERT :)

declare function hs:insertLiveJob($documentUrl as xs:string, $jobProperty as element(dat:jobProperties),$jobPath )
{	
	let $doc := doc($documentUrl)
	for $xmlJobList in $doc//$jobPath
		return  update insert $jobProperty into $xmlJobList
};

declare function hs:insertLiveJobLock($documentUrl as xs:string, $jobProperty as element(dat:jobProperties),$jobPath )
{	
  util:exclusive-lock(doc($documentUrl)/TlosProcessDataAll, hs:insertLiveJob($documentUrl, $jobProperty, $jobPath))   
};
(: kullanilmiyor :)
declare function hs:insertJobInTheBeginning($documentUrl as xs:string, $jobProperty as element(dat:jobProperties),$jobPath )
{	
	let $doc := doc($documentUrl)
	for $xmlJobList in $doc//$jobPath
		return  update insert $jobProperty into $xmlJobList
};

declare function hs:insertJobInTheBeginningLock($documentUrl as xs:string, $jobProperty as element(dat:jobProperties),$jobPath )
{	
  util:exclusive-lock(doc($documentUrl)/TlosProcessDataAll, hs:insertJobInTheBeginning($documentUrl, $jobProperty, $jobPath))   
};

declare function hs:insertFreeJob($documentUrl as xs:string, $jobProperty as element(dat:jobProperties),$runId as xs:int)
{
   update insert $jobProperty into  doc($documentUrl)/TlosProcessDataAll/RUN[@id=data($runId)]/dat:TlosProcessData/dat:jobList  
};

declare function hs:insertFreeJobLock($documentUrl as xs:string, $jobProperty as element(dat:jobProperties),$runId as xs:int)
{
   util:exclusive-lock(doc($documentUrl)/TlosProcessDataAll, hs:insertFreeJob($documentUrl, $jobProperty, $runId))     
};

declare function hs:insertJobAgentId($documentUrl as xs:string, $agentId as xs:string, $jobId as xs:string, $jobPath )
{
	let $doc := doc($documentUrl)	
	let $doc := update insert attribute agentId {data($agentId)} into  $doc//$jobPath/dat:jobProperties[@ID=data($jobId) and @agentId='0']
	return true()
};

declare function hs:insertJobAgentIdLock($documentUrl as xs:string, $agentId as xs:string, $jobId as xs:string, $jobPath )
{	
  util:exclusive-lock(doc($documentUrl)/TlosProcessDataAll, hs:insertJobAgentId($documentUrl, $agentId, $jobId, $jobPath))   
};

declare function hs:insertJobState($documentUrl as xs:string, $liveStateInfo as element(state-types:LiveStateInfo),$jobPath )
{	
	let $doc := doc($documentUrl)
	for $jobLiveStateInfos in $doc//$jobPath/dat:stateInfos/state-types:LiveStateInfos
		return  update insert $liveStateInfo into $jobLiveStateInfos
};

declare function hs:insertJobStateLock($documentUrl as xs:string, $liveStateInfo as element(state-types:LiveStateInfo),$jobPath )
{	
  util:exclusive-lock(doc($documentUrl)/TlosProcessDataAll, hs:insertJobState($documentUrl, $liveStateInfo, $jobPath))   
};

(: UPDATE :)

declare function hs:updateLiveJobLock($documentUrl as xs:string, $jobProperty as element(dat:jobProperties),$jobPath)
{	
  util:exclusive-lock(doc($documentUrl)/TlosProcessDataAll, hs:updateLiveJob($documentUrl, $jobProperty, $jobPath))   
};

declare function hs:updateLiveJob($documentUrl as xs:string, $jobProperty as element(dat:jobProperties),$jobPath)
{	
    let $doc := doc($documentUrl)
    let $state := if ( exists($doc//$jobPath/@LSIDateTime) and $doc//$jobPath[@ID=data($jobProperty/@ID) and @agentId=data($jobProperty/@agentId) and @LSIDateTime=data($jobProperty/@LSIDateTime)]) then 
                      $doc//$jobPath
	              else ()
	return update value $doc//$jobPath with $jobProperty/*
};

declare function hs:updateFirstLiveJobLock($documentUrl as xs:string, $jobProperty as element(dat:jobProperties),$jobPath)
{	
  util:exclusive-lock(doc($documentUrl)/TlosProcessDataAll, hs:updateFirstLiveJob($documentUrl, $jobProperty, $jobPath))   
};

declare function hs:updateFirstLiveJob($documentUrl as xs:string, $jobProperty as element(dat:jobProperties),$jobPath)
{	
  let $doc := doc($documentUrl)
  let $arasonuc := update delete $doc//$jobPath//dat:stateInfos/state-types:LiveStateInfos
  let $sonuc := update insert $jobProperty/dat:stateInfos/state-types:LiveStateInfos into $doc//$jobPath//dat:stateInfos
  return true()
};

(: DELETE :)

(: THERE IS NOT ANY FUNCT YET :)