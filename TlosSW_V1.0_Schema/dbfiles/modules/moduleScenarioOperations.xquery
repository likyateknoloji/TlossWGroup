xquery version "1.0";
module namespace hs = "http://hs.tlos.com/";

import module namespace sq = "http://sq.tlos.com/" at "../modules/moduleSequenceOperations.xquery";
import module namespace met = "http://meta.tlos.com/" at "moduleMetaDataOperations.xquery";

declare namespace com = "http://www.likyateknoloji.com/XML_common_types";
declare namespace dat="http://www.likyateknoloji.com/XML_data_types";
declare namespace state-types="http://www.likyateknoloji.com/state-types";
declare namespace fn = "http://www.w3.org/2005/xpath-functions";
declare namespace jsdl="http://schemas.ggf.org/jsdl/2005/11/jsdl";
declare namespace alm = "http://www.likyateknoloji.com/XML_alarm_types";

(:
Mapping
$dataDocumentUrl = doc("//db/TLOSSW/xmls/tlosSWData10.xml")
$templateDataDocumentUrl = doc("//db/TLOSSW/xmls/tlosSWJobTemplates10.xml")
:)

(:-----------------------------------------------------------------------------------------------------------:)
(:--------------------------------------- TlosProcessData operasyonlari-------------------------------------:)

(: READ :)
declare function hs:getTlosDataXml($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean) as element(dat:TlosProcessData)?
{
   let $dataDocumentUrl := met:getDataDocument($documentUrl, $docId, $userId, $isGlobal)
   
	for $tlosProcessData in doc($dataDocumentUrl)/dat:TlosProcessData
	return $tlosProcessData 
}; 

(:-----------------------------------------------------------------------------------------------------------:)
(:----------------------------------------- Senaryo operasyonlari -------------------------------------------:)

(: READ :)
declare function hs:getScenario($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $scenarioPath, $scenarioName as xs:string) as element(dat:scenario)?
{	
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
   
	let $doc := doc($dataDocumentUrl)
	for $scenario in $doc//$scenarioPath 
    where $scenario/dat:baseScenarioInfos/com:jsName = $scenarioName
		return $scenario
};

declare function hs:getScenarioExistence($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $scenarioPath as node()*, $scenarioName as xs:string) as xs:integer
{    
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
   
    let $doc := doc($dataDocumentUrl) 
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

declare function hs:getScenarioFromId($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $id as xs:integer) as element(dat:scenario)?
{	
   let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
   
	let $doc := doc($dataDocumentUrl) 
	let $sonuc := if($id eq 0) then
                    <dat:scenario ID="0"> { $doc/dat:TlosProcessData/* } </dat:scenario>
                  else
                    for $scenario in $doc//dat:scenario
                    where $scenario/@ID = $id
                    return $scenario
    return $sonuc
};

declare function hs:scenarioList($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean) as element(dat:scenario)* 
 {
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
	for $scenario in doc($dataDocumentUrl)/dat:TlosProcessData//dat:scenario
	return  $scenario
};

(: INSERT :)
declare function hs:insertScenarioLock($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $scenario as element(dat:scenario), $scenarioPath )
{
   let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
   
   return util:exclusive-lock(doc($dataDocumentUrl)/dat:TlosProcessData, hs:insertScenario($documentUrl,  $docId, $userId, $isGlobal, $scenario,$scenarioPath))     
};

declare function hs:insertScenario($documentUrl as xs:string, $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $scenario as element(dat:scenario), $scenarioPath)
{
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
   
	let $doc := doc($dataDocumentUrl)
	for $xmlScenario in $doc//$scenarioPath
		return  update insert $scenario into $xmlScenario
};

declare function hs:insertScenarioCalendars($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $scenarioPath, $calendars)
{
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
   
    let $doc := doc($dataDocumentUrl)
	return update insert $calendars following  $doc//$scenarioPath/dat:management/dat:timeManagement/dat:jsRelativeTimeOption
};

declare function hs:insertScnCalendar($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $scenarioPath, $scenario)
{
    let $calIdInsert :=  hs:insertScenarioCalendars($documentUrl,  $docId, $userId, $isGlobal, $scenarioPath, $scenario/dat:management/dat:timeManagement/dat:calendars)
    return $calIdInsert
};

declare function hs:insertScenarioDepList($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $scenarioPath, $dependencyList as element())
{
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
    let $doc := doc($dataDocumentUrl)
	return update insert $dependencyList preceding $doc//$scenarioPath/dat:jobList
};


(: DELETE :)
declare function hs:deleteScenarioLock($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $scenario as element(dat:scenario), $scenarioPath )
{
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
   
	return util:exclusive-lock(doc($dataDocumentUrl)/dat:TlosProcessData, hs:deleteScenario($documentUrl, $docId, $userId, $isGlobal, $scenario,$scenarioPath))
};

declare function hs:deleteScenario($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $scenario as element(dat:scenario),$scenarioPath )
{
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
   
	let $doc := doc($dataDocumentUrl)
	for $senaryo in $doc//$scenarioPath 
        where $senaryo/@ID = $scenario/@ID
        return update delete $senaryo
};

declare function hs:deleteScenarioCalendars($documentUrl as xs:string, $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $scenarioPath)
{
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
   
	let $doc := doc($dataDocumentUrl)
	for $calendarId in $doc//$scenarioPath/dat:management/dat:timeManagement/dat:calendars
        return update delete $calendarId
};

declare function hs:deleteScnCalendar($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $scenarioPath)
{
    let $calIdInsert :=  hs:deleteScenarioCalendars($documentUrl,  $docId, $userId, $isGlobal, $scenarioPath)
    return $calIdInsert
};

declare function hs:deleteScenarioDep($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $scenarioPath as node())
{
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
	let $doc := doc($dataDocumentUrl)
	for $depList in $doc//$scenarioPath/dat:DependencyList 
        return update delete $depList
};

(: UPDATE :)
declare function hs:updateScenarioName($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $scenarioPath, $name)
{
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
	let $doc := doc($dataDocumentUrl)
    return update replace $doc//$scenarioPath/dat:baseScenarioInfos/com:jsName with <com:jsName>{data($name)}</com:jsName>
};

declare function hs:updateScenarioComment($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $scenarioPath as node(),$comment)
{
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
	let $doc := doc($dataDocumentUrl)
    return update replace $doc//$scenarioPath/dat:baseScenarioInfos/com:comment with <com:comment>{data($comment)}</com:comment>
};

declare function hs:updateScenarioCalendars($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $scenarioPath as node(), $calendars)
{
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
	let $doc := doc($dataDocumentUrl)
    return update replace $doc//$scenarioPath/dat:baseScenarioInfos/dat:calendars with $calendars
};

declare function hs:updateScnCalendar($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $scenarioPath as node(),$scenario)
{
    let $calIdUpdate :=  hs:updateScenarioCalendars($documentUrl, $docId, $userId, $isGlobal, $scenarioPath,$scenario/dat:management/dat:timeManagement/dat:calendars)
    return $calIdUpdate 	
};

declare function hs:updateScenarioUser($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $scenarioPath as node(),$user)
{
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
	let $doc := doc($dataDocumentUrl)
    return update replace $doc//$scenarioPath/dat:baseScenarioInfos/com:userId with <com:userId>{data($user)}</com:userId>
};

declare function hs:updateScenarioDepList($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $scenarioPath as node(),$dependencyList as element())
{
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
	let $doc := doc($dataDocumentUrl)
	return update replace $doc//$scenarioPath/dat:DependencyList with $dependencyList 		
};

(: Kullanilmiyor 
declare function hs:updateScenarioSuccessCodes($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $scenarioPath as node(),$successCodeList as element())
{
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
	let $doc := doc($dataDocumentUrl)
	return update replace $doc//$scenarioPath/com:jsSuccessCodeList with $successCodeList 		
};
:)

declare function hs:updateScenario($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $scenarioPath, $scenario as element(dat:scenario))
{
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
    let $XXX := $scenario
	let $doc :=  doc($dataDocumentUrl)
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
	                                 <scenario>{hs:updateScenario($documentUrl, $docId, $userId, $isGlobal, $scenarioPath/scenario, $scenario/scenario)}</scenario>
	                       else ()

	return <ok/>
};

declare function hs:updateScenarioLock($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $scenarioPath,$scenario as element(dat:scenario))
{
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
	return util:exclusive-lock(doc($dataDocumentUrl)/dat:TlosProcessData, hs:updateScenario($documentUrl, $docId, $userId, $isGlobal, $scenarioPath,$scenario))
};

(:-----------------------------------------------------------------------------------------------------------:)
(:---------------------------------------- Job operasyonlari ------------------------------------------------:)

(:---- Screen copy-paste operations ----------------:)

declare function hs:copy-jobs-and-scenarios($documentUrl as xs:string, $n as node()*, $nodeName as xs:string, $filteringCondition as node()*, $isNewIdsRequired as xs:boolean) as node()*
{
       typeswitch($n)
       
        case $a as element(dat:jobList)
			 return element dat:jobList
			 {
			   for $job in $a/dat:jobProperties
                
                (:let $x := for $state in $job/dat:stateInfos/state-types:LiveStateInfos 
                          where $state/state-types:LiveStateInfo/state-types:SubstateName
                          order by $state/@LSIDateTime
                          return $state
                let $y := $x[position() eq 1]
                return $y:)
			   return hs:copyJob( $documentUrl, $job, data($job/dat:baseJobInfos/com:jsName), $isNewIdsRequired)
			 }
             
        case $es as element(dat:scenario) 
	       return 
              let $copiedScenario := 
                if (count($es//dat:jobProperties)>0) 
                then 
                  let $jobList := hs:copy-jobs-and-scenarios($documentUrl, $es/dat:jobList, xs:string("dd"),(), $isNewIdsRequired)
                  let $baseScenarioInfos := $es/dat:baseScenarioInfos
                  let $scenarioId := if ( $isNewIdsRequired ) 
                                     then sq:getNextId($documentUrl, "scenarioId") 
				                     else $es/@ID
	             
                  let $cpScenario := 
                     element dat:scenario { 
                       attribute ID { $scenarioId },
                       element dat:baseScenarioInfos { 
                         element com:jsName { data($es/dat:baseScenarioInfos/com:jsName) },
                         $baseScenarioInfos/com:comment,
                         $baseScenarioInfos/dat:jsIsActive,
                         $baseScenarioInfos/com:userId
                       },
                     $jobList,
                     $es/dat:management,
					 $es/dat:alarmPreference,
                     $es/dat:advancedScenarioInfos,
                     $es/com:localParameters,
                     for $scenario in $es/dat:scenario 
                     return hs:copy-jobs-and-scenarios($documentUrl, $scenario, xs:string("dd"),(), $isNewIdsRequired)
                     }
                  return $cpScenario
			    else 
                  ()
               return $copiedScenario
                           
		case $d as element(dat:TlosProcessData) 
		   return 
              element dat:TlosProcessData
		        { for $cd in $d/* return hs:copy-jobs-and-scenarios($documentUrl, $cd, xs:string("dd"), (), $isNewIdsRequired) }
             
	     default return $n 
};

declare function hs:copyScenario($documentUrl as xs:string, $scenario as element(dat:scenario), $newScenarioName as xs:string, $isNewScenarioIdRequired as xs:boolean) as element(dat:scenario)
{

    let $baseScenarioInfos := $scenario/dat:baseScenarioInfos
	let $scenarioId := if ( $isNewScenarioIdRequired ) 
	                   then sq:getNextId($documentUrl, "scenarioId") 
				       else $scenario/@ID
	
    let $copiedScenario := 
      element dat:scenario { 
        attribute ID { $scenarioId },
        element dat:baseScenarioInfos { 
          element com:jsName { $newScenarioName },
          $baseScenarioInfos/com:comment,
          $baseScenarioInfos/dat:jsIsActive,
          $baseScenarioInfos/com:userId
      },
	  $scenario/dat:jobList,
	  $scenario/dat:management,
	  $scenario/dat:alarmPreference,
      $scenario/dat:advancedScenarioInfos,
	  $scenario/com:localParameters,
      for $scenariox in $scenario/dat:scenario 
      return hs:copyScenario($documentUrl, $scenariox, data($scenariox/dat:baseScenarioInfos/com:jsName) ,true)
    }
	
	return $copiedScenario
};

declare function hs:copyJob($documentUrl as xs:string, $job as element(dat:jobProperties), $newJobName as xs:string, $isNewJobIdRequired as xs:boolean) as element(dat:jobProperties)
{

    let $baseJobInfos := $job/dat:baseJobInfos
	let $jobId := if ( $isNewJobIdRequired ) 
	              then sq:getNextId($documentUrl, "jobId") 
				  else $job/@ID
	
    let $copiedJob := 	  
      element dat:jobProperties { 
        attribute agentId {$job/@agentId }, 
        attribute ID { $jobId },
        $job/jsdl:JobDescription,
        element dat:baseJobInfos {
		  $baseJobInfos/com:jsName,
          $baseJobInfos/com:comment,
          $baseJobInfos/com:jobTypeDetails, 
          $baseJobInfos/dat:jobLogFile, 
		  $baseJobInfos/dat:jobLogPath, 
		  $baseJobInfos/dat:jobDeploymentPath,
          $baseJobInfos/dat:oSystem, 
          $baseJobInfos/dat:jobPriority, 
          $baseJobInfos/dat:jsIsActive, 
          $baseJobInfos/com:userId
        },
        $selectedJS/dat:stateInfos,
        $selectedJS/dat:advancedJobInfos,
		$selectedJS/dat:alarmPreference,
        $selectedJS/dat:management,
        $selectedJS/dat:logAnalysis,
		$selectedJS/com:localParameters
    }
	
	return $copiedJob
};


declare function hs:copyJStoJS($documentUrl as xs:string, $fromDocId as xs:string, $toDocId as xs:string, $fromScope as xs:boolean, $toScope as xs:boolean, $userId as xs:string, $isJob as xs:boolean, $jsId as xs:integer, $pathOfJS, $newJSName as xs:string) as element()*
{    
	let $copiedJS := if($isJob) 
	                   then 
					      let $thisJob := hs:getJobFromId($documentUrl, $fromDocId, $userId, $fromScope, $jsId )
						  let $newJob := hs:copyJob( $documentUrl, $thisJob, $newJSName, true())
						  return $newJob
	                   else 
					      let $thisScenario := hs:getScenarioFromId($documentUrl, $fromDocId, $userId, $fromScope, $jsId )
                          let $newScenario := hs:copy-jobs-and-scenarios( $documentUrl, $thisScenario, $newJSName, (), true() )
                          return $newScenario
	
    let $truePath := if($isJob) then 
              hs:insertJobLock($documentUrl, $toDocId, $userId, $toScope, $copiedJS, $pathOfJS/dat:jobList)
           else
              hs:insertScenarioLock($documentUrl, $toDocId, $userId, $toScope, $copiedJS, $pathOfJS )
	return $copiedJS
		
};

(:--------------------------------------------------:)



(: READ :)
declare function hs:getJob($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $jobPath ,$jobName as xs:string) as element(dat:jobProperties)?
{	
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
	let $doc := doc($dataDocumentUrl) 

	for $job in $doc//$jobPath/dat:jobProperties
        where $job/dat:baseJobInfos/com:jsName = $jobName
        return $job
};

declare function hs:getJobFromId($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $id as xs:integer) as element(dat:jobProperties)?
{	
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
	let $doc := doc($dataDocumentUrl)
	for $job in $doc//dat:jobProperties
        where $job/@ID = $id
        return $job
};

declare function hs:getJobCopyFromId($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $id as xs:integer) as element(dat:jobProperties)?
{	
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
	let $doc := doc($dataDocumentUrl)
	let $selectedJS := for $job in $doc//dat:jobProperties
                       where $job/@ID = $id
                       return $job
		
	let $baseJobInfos := $selectedJS/dat:baseJobInfos
	
    let $nextId := sq:getNextId($documentUrl, "jobId") 

    let $copyJob := 
      element dat:jobProperties { 
        attribute agentId {$selectedJS/@agentId }, 
        attribute ID { $nextId },
        $selectedJS/jsdl:JobDescription,
        element dat:baseJobInfos {
		  $baseJobInfos/com:jsName,
          $baseJobInfos/com:comment,
          $baseJobInfos/com:jobTypeDetails, 
          $baseJobInfos/dat:jobLogFile, 
		  $baseJobInfos/dat:jobLogPath, 
		  $baseJobInfos/dat:jobDeploymentPath,
          $baseJobInfos/dat:oSystem, 
          $baseJobInfos/dat:jobPriority, 
          $baseJobInfos/dat:jsIsActive, 
          $baseJobInfos/com:userId
        },
        $selectedJS/dat:stateInfos,
        $selectedJS/dat:advancedJobInfos,
		$selectedJS/dat:alarmPreference,
        $selectedJS/dat:management,
        $selectedJS/dat:logAnalysis,
		$selectedJS/com:localParameters
    }
	return $copyJob
};

declare function hs:getJobExistence($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $jobPath as node()*, $jobName as xs:string) as xs:integer
{    
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
    let $doc := doc($dataDocumentUrl)
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

declare function hs:getJobExistenceResults($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $jobPath as node()*, $jobName as xs:string) as element(dat:jobProperties)*
{    
    let $dataDocumentUrl := met:getDataDocument($documentUrl, $docId, $userId, $isGlobal)
    
    let $doc := doc($dataDocumentUrl)
    let $refPath := $doc//$jobPath
    
    for $job in $refPath/dat:jobProperties
        where fn:matches($job//com:jsName/text(), $jobName)
        return $job
};

(:hs:getJobExistence(xs:string("tlosSWData10.xml"), /dat:TlosProcessData/dat:scenario/dat:jobList, xs:string("job1.bat"), 13) :)

declare function hs:getJobFromJobName($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $jobName as xs:string) as element(dat:jobProperties)?
{	
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
	let $doc := doc($dataDocumentUrl)
	for $job in $doc//dat:jobProperties
        where $job/dat:baseJobInfos/com:jsName = $jobName
        return $job
};

(: ornek kullanim lk:jobList(1,2) ilk uc eleman :)
declare function hs:jobList($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $firstElement as xs:int, $lastElement as xs:int) as element(dat:jobProperties)* 
 {
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	 
	for $jobd in doc($dataDocumentUrl)/dat:TlosProcessData//dat:jobProperties[position() = ($firstElement to $lastElement)]
	return  $jobd
};

(: INSERT :)
declare function hs:insertJobLock($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $jobProperty as element(dat:jobProperties),$jobPath)
{
   let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
   return util:exclusive-lock(doc($dataDocumentUrl)/dat:TlosProcessData, hs:insertJob($documentUrl, $docId, $userId, $isGlobal, $jobProperty,$jobPath))     
};

declare function hs:insertJob($documentUrl as xs:string,  $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $jobProperty as element(dat:jobProperties),$jobPath)
{	
    let $dataDocumentUrl := met:getDataDocument($documentUrl, $docId, $userId, $isGlobal)
	
    let $doc := doc($dataDocumentUrl)
	for $xmlJobList in $doc//$jobPath
		return  update insert $jobProperty into $xmlJobList
};

(: UPDATE :)

declare function hs:updateAlarmViaJobLock($documentUrl as xs:string, $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $jobProperty as element(dat:jobProperties))
{
    let $alarmsDocumentUrl := met:getMetaData($documentUrl, $docId)
    return util:exclusive-lock(doc($alarmsDocumentUrl)/alm:alarmManagement, hs:updateAlarmViaJob($documentUrl, $docId, $userId, $isGlobal, $jobProperty))     
};

declare function hs:updateAlarmViaJob($documentUrl as xs:string, $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $jobProperty as element(dat:jobProperties)) as node()*
{    
    let $alarmsDocumentUrl := met:getMetaData($documentUrl, $docId)

    let $sonuc := for $alarm in $jobProperty/dat:alarmPreference/dat:alarmId
    
                    let $jobsTag := doc($alarmsDocumentUrl)/alm:alarmManagement/alm:alarm[@ID=data($alarm)]/alm:focus/alm:jobs 
                    let $theJob := $jobsTag/alm:job[@id = $jobProperty/@ID]
                  
                    let $ekle := if(not(exists($jobsTag))) 
                                 then 
                                   let $eklenecek := <alm:jobs>
                                                       <alm:job id="{$jobProperty/@ID}"/>
                                                     </alm:jobs>
                                   return update insert $eklenecek into doc($alarmsDocumentUrl)/alm:alarmManagement/alm:alarm[@ID=data($alarm)]/alm:focus
                                 else
                                   let $guncelle := if ( not(exists($theJob)) ) 
                                                    then
                                                      let $eklenecek := <alm:job id="{$jobProperty/@ID}"/>
                                                      return update insert $eklenecek into doc($alarmsDocumentUrl)/alm:alarmManagement/alm:alarm[@ID=data($alarm)]/alm:focus/alm:jobs 
                                                    else
                                                     ()
                                   return $guncelle
                    return $alarm

        return $sonuc
};



declare function hs:updateJobLock($documentUrl as xs:string, $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $jobProperty as element(dat:jobProperties),$jobPath)
{
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
	let $alarmUpdate := hs:updateAlarmViaJobLock($documentUrl, xs:string("alarms"), $userId , $isGlobal , $jobProperty )
	
    return util:exclusive-lock(doc($dataDocumentUrl)/dat:TlosProcessData, hs:updateJob($documentUrl, $docId, $userId, $isGlobal, $jobProperty,$jobPath))     
};

declare function hs:updateJob($documentUrl as xs:string, $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $jobProperty as element(dat:jobProperties),$jobPath)
{	
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
	let $doc := doc($dataDocumentUrl)
	for $job in $doc//$jobPath/dat:jobProperties            
        where $job/@ID = $jobProperty/@ID
        return update replace $job with $jobProperty
};

(: DELETE :)
declare function hs:deleteJobLock($documentUrl as xs:string, $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $jobProperty as element(dat:jobProperties),$jobPath )
{
   let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
   
   return util:exclusive-lock(doc($dataDocumentUrl)/dat:TlosProcessData, hs:deleteJob($documentUrl, $docId, $userId, $isGlobal, $jobProperty,$jobPath))     
};

declare function hs:deleteJob($documentUrl as xs:string, $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $jobProperty as element(dat:jobProperties),$jobPath )
{	
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
	let $jobCount := count($jobPath)
    let $doc := doc($dataDocumentUrl)

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
(:
declare function hs:jobResultListByDates($documentUrl as xs:string, $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $jobId as xs:int, $date1 as xs:date, $date2 as xs:date, $refRunIdBolean as xs:boolean) as element(dat:jobProperties)*
{
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
                let $sonuc := for $runx in doc($dataDocumentUrl)/TlosProcessDataAll/RUN//dat:jobProperties
                              where $runx[(@ID = $jobId or $jobId = 0) and @agentId!="0"]
                                    and not(empty($runx/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date))
                                    and	$runx/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date >= xs:date($date1)
                                    and	$runx/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date <= xs:date($date2)
                                    order by $runx/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date
                                    return   $runx
                return  $sonuc
};
:)
declare function hs:jobResultListbyRunId($documentUrl as xs:string, $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $numberOfElement as xs:int, $runId as xs:int, $jobId as xs:int, $refRunIdBolean as xs:boolean) as element(dat:jobProperties)*
{
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
    let $runIdFound := if ($runId != 0 ) 
	                   then $runId 
	                   else sq:getId($documentUrl, "runId")

    let $posUpper := max(for $runx at $pos in doc($dataDocumentUrl)/TlosProcessDataAll/RUN
	                 where $runx[@id = $runIdFound] or not($refRunIdBolean)
	                 return $pos)

    let $posLower := if ($posUpper - $numberOfElement > 0) then $posUpper - $numberOfElement else 0

	let $sonuc := for $runx at $pos in doc($dataDocumentUrl)/TlosProcessDataAll/RUN
					 where $pos > $posLower and $pos <=$posUpper
					 order by $runx/@id descending
	                 return 
                         for $job in $runx//dat:jobProperties[(@ID = $jobId or $jobId = 0)  and @agentId!="0"]
                         return $job
	return $sonuc
};

declare function hs:jobResultListByDates($documentUrl as xs:string, $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $jobId as xs:int, $date1 as xs:date, $date2 as xs:date, $refRunIdBolean as xs:boolean) as element(dat:jobProperties)*
{
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
                let $sonuc := for $runx in doc($dataDocumentUrl)/TlosProcessDataAll/RUN//dat:jobProperties
                              where $runx[(@ID = $jobId or $jobId = 0) and @agentId!="0"]
                                    and not(empty($runx/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date))
                                    and	$runx/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date >= xs:date($date1)
                                    and	$runx/dat:timeManagement/dat:jsRealTime/dat:startTime/com:date <= xs:date($date2)
                                    order by $runx/dat:timeManagement/dat:jsRealTime/dat:startTime
                                    return   $runx
                return  $sonuc
};

(: INSERT :)

declare function hs:insertLiveJob($documentUrl as xs:string, $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $jobProperty as element(dat:jobProperties),$jobPath )
{	
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
	let $doc := doc($dataDocumentUrl)
	for $xmlJobList in $doc//$jobPath
		return  update insert $jobProperty into $xmlJobList
};

declare function hs:insertLiveJobLock($documentUrl as xs:string, $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $jobProperty as element(dat:jobProperties),$jobPath )
{	
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
    return util:exclusive-lock(doc($dataDocumentUrl)/TlosProcessDataAll, hs:insertLiveJob($documentUrl, $docId, $userId, $isGlobal, $jobProperty, $jobPath))   
};
(: kullanilmiyor :)
declare function hs:insertJobInTheBeginning($documentUrl as xs:string, $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $jobProperty as element(dat:jobProperties),$jobPath )
{	
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
	let $doc := doc($dataDocumentUrl)
	for $xmlJobList in $doc//$jobPath
		return  update insert $jobProperty into $xmlJobList
};

declare function hs:insertJobInTheBeginningLock($documentUrl as xs:string, $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $jobProperty as element(dat:jobProperties),$jobPath )
{	
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
    return util:exclusive-lock(doc($dataDocumentUrl)/TlosProcessDataAll, hs:insertJobInTheBeginning($documentUrl, $docId, $userId, $isGlobal, $jobProperty, $jobPath))   
};

declare function hs:insertFreeJob($documentUrl as xs:string, $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $jobProperty as element(dat:jobProperties),$runId as xs:int)
{
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
    return update insert $jobProperty into  doc($dataDocumentUrl)/TlosProcessDataAll/RUN[@id=data($runId)]/dat:TlosProcessData/dat:jobList  
};

declare function hs:insertFreeJobLock($documentUrl as xs:string, $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $jobProperty as element(dat:jobProperties),$runId as xs:int)
{
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
    return util:exclusive-lock(doc($dataDocumentUrl)/TlosProcessDataAll, hs:insertFreeJob($documentUrl, $docId, $userId, $isGlobal, $jobProperty, $runId))     
};

declare function hs:insertJobAgentId($documentUrl as xs:string, $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $agentId as xs:string, $jobId as xs:string, $jobPath )
{
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
	let $doc := doc($dataDocumentUrl)	
	let $doc := update insert attribute agentId {data($agentId)} into  $doc//$jobPath/dat:jobProperties[@ID=data($jobId) and @agentId='0']
	return true()
};

declare function hs:insertJobAgentIdLock($documentUrl as xs:string, $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $agentId as xs:string, $jobId as xs:string, $jobPath )
{	
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
    return util:exclusive-lock(doc($dataDocumentUrl)/TlosProcessDataAll, hs:insertJobAgentId($documentUrl, $docId, $userId, $isGlobal, $agentId, $jobId, $jobPath))   
};

declare function hs:insertJobState($documentUrl as xs:string, $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $liveStateInfo as element(state-types:LiveStateInfo),$jobPath )
{	
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
	let $doc := doc($dataDocumentUrl)
	for $jobLiveStateInfos in $doc//$jobPath/dat:stateInfos/state-types:LiveStateInfos
		return  update insert $liveStateInfo into $jobLiveStateInfos
};

declare function hs:insertJobStateLock($documentUrl as xs:string, $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $liveStateInfo as element(state-types:LiveStateInfo),$jobPath )
{	
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
    return util:exclusive-lock(doc($dataDocumentUrl)/TlosProcessDataAll, hs:insertJobState($documentUrl, $docId, $userId, $isGlobal, $liveStateInfo, $jobPath))   
};

(: UPDATE :)

declare function hs:updateLiveJobLock($documentUrl as xs:string, $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $jobProperty as element(dat:jobProperties),$jobPath)
{	
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
    return util:exclusive-lock(doc($dataDocumentUrl)/TlosProcessDataAll, hs:updateLiveJob($documentUrl, $docId, $userId, $isGlobal, $jobProperty, $jobPath))   
};

declare function hs:updateLiveJob($documentUrl as xs:string, $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $jobProperty as element(dat:jobProperties),$jobPath)
{	
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
    let $doc := doc($dataDocumentUrl)
    let $state := if ( exists($doc//$jobPath/@LSIDateTime) and $doc//$jobPath[@ID=data($jobProperty/@ID) and @agentId=data($jobProperty/@agentId) and @LSIDateTime=data($jobProperty/@LSIDateTime)]) then 
                      $doc//$jobPath
	              else ()
	return update value $doc//$jobPath with $jobProperty/*
};

declare function hs:updateFirstLiveJobLock($documentUrl as xs:string, $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $jobProperty as element(dat:jobProperties),$jobPath)
{	
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
    return util:exclusive-lock(doc($dataDocumentUrl)/TlosProcessDataAll, hs:updateFirstLiveJob($documentUrl, $docId, $userId, $isGlobal, $jobProperty, $jobPath))   
};

declare function hs:updateFirstLiveJob($documentUrl as xs:string, $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $jobProperty as element(dat:jobProperties),$jobPath)
{	
    let $dataDocumentUrl := met:getDataDocument($documentUrl,  $docId , $userId, $isGlobal)
	
    let $doc := doc($dataDocumentUrl)
    let $arasonuc := update delete $doc//$jobPath//dat:stateInfos/state-types:LiveStateInfos
    let $sonuc := update insert $jobProperty/dat:stateInfos/state-types:LiveStateInfos into $doc//$jobPath//dat:stateInfos
	
    return true()
};

(: DELETE :)

(: THERE IS NOT ANY FUNCT YET :)