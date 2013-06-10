xquery version "1.0";
module namespace lk = "http://likya.tlos.com/";

import module namespace sq = "http://sq.tlos.com/" at "xmldb:exist://db/TLOSSW/modules/moduleSequenceOperations.xquery";
import module namespace hs = "http://hs.tlos.com/" at "xmldb:exist://db/TLOSSW/modules/moduleScenarioOperations.xquery";

declare namespace dat="http://www.likyateknoloji.com/XML_data_types";
declare namespace alm = "http://www.likyateknoloji.com/XML_alarm_types";
declare namespace com="http://www.likyateknoloji.com/XML_common_types";
declare namespace usr = "http://www.likyateknoloji.com/XML_user_types";
declare namespace per = "http://www.likyateknoloji.com/XML_permission_types";
declare namespace sla="http://www.likyateknoloji.com/XML_SLA_types";
declare namespace state-types="http://www.likyateknoloji.com/state-types";
declare namespace fn = "http://www.w3.org/2005/xpath-functions";
declare namespace alm-history="http://www.likyateknoloji.com/XML_alarm_history";

(:
declare function lk:searchAlarm_ayhan($searchAlarm as element(alm:alarm)) as element(alm:alarm)* 
{
	for $alarm in doc("//db/TLOSSW/xmls/tlosSWAlarm10.xml")/alm:alarmManagement/alm:alarm
		return if (
                   (fn:contains(fn:lower-case($alarm/alm:level), fn:lower-case($searchAlarm/alm:level)) or data($searchAlarm/alm:level)="")
             )
		then $alarm
		else  ( )
};
:)

declare function lk:getAlarms($date1 as xs:string, $date2 as xs:string, $level1 as xs:string, $alarm1 as xs:string, $per1 as xs:string) as node()*
{

                let $sonuc := for $alarmHistory in doc("//db/TLOSSW/xmls/tlosSWAlarmHistory10.xml")/alm-history:alarmHistory/alm-history:alarm, 
                                  $alarm in doc("//db/TLOSSW/xmls/tlosSWAlarm10.xml")/alm:alarmManagement/alm:alarm,
                                  $user in doc("//db/TLOSSW/xmls/tlosSWUser10.xml")/usr:user-infos/usr:userList/usr:person,
                                  $job in doc("//db/TLOSSW/xmls/tlosSWData10.xml")/dat:TlosProcessData//dat:jobList/dat:jobProperties
                              where $alarm[@ID = $alarmHistory/alm-history:alarmId] and 
                                    $user[@id = $alarmHistory/alm:subscriber/alm:person/@id] and 
                                    $job[@ID = /$alarmHistory/alm:focus/alm:jobs/alm:job/@id] and 
							        $alarmHistory/alm:creationDate >= $date1 and $alarmHistory/alm:creationDate <= $date2 and 
                                    ($level1 = "All" or ($level1 != "All" and $alarmHistory/alm:level = $level1)) and
                                    ($alarm1 = "All" or ($alarm1 != "All" and $alarmHistory/alm-history:alarmId = $alarm1)) and
                                    ($per1 = "All" or ($per1 != "All" and $alarmHistory/alm:subscriber/alm:person[@id = $per1]))
                                    order by $alarmHistory/alm:creationDate
                                    return   <alm-history:alarmReport>
                                                {$alarm/alm:name}
                                               {$alarm/alm:desc}
                                                {$alarmHistory/alm:creationDate}
                                                 <alm-history:alarmLevel>
                                                 {if ($alarmHistory/alm:level = 1) then "YUKSEK" 
                                                 else if ($alarmHistory/alm:level = 2) then "NORMAL"
                                                 else "BILGILENDIRME" }
                                               </alm-history:alarmLevel>
                                               {$user/com:userName}
                                               {$job/dat:baseJobInfos/com:jsName}
                                             </alm-history:alarmReport>
                return  $sonuc

}; 

(:fn:empty($prs/com:role):)
declare function lk:searchAlarm($searchAlarm as element(alm:alarm)) as element(alm:alarm)* 
{
      for $alarm in doc("//db/TLOSSW/xmls/tlosSWAlarm10.xml")/alm:alarmManagement/alm:alarm
        let $itemStartDate  := data($alarm/alm:startDate)
        let $itemEndDate    := data($alarm/alm:endDate)
        let $searchedStartDate := data($searchAlarm/alm:startDate)
        let $searchedEndDate   := data($searchAlarm/alm:endDate)
        
    let $nedir := if ( 
                       (fn:contains(fn:lower-case($alarm/alm:name), fn:lower-case($searchAlarm/alm:name)) or data($searchAlarm/alm:name)="")
                       and    
                       ( data($alarm/alm:subscriber/alm:person/@id)=data($searchAlarm/alm:subscriber/alm:person/@id) 
                         or data($searchAlarm/alm:subscriber/alm:person/@id) = "-1" 
                         or data($searchAlarm/alm:subscriber/alm:person/@id) = "0"
                       )
			           and 
                       ( 
                        ( $searchedStartDate='' and $searchedEndDate = '' )
                        or
                        (
                          ($searchedEndDate='' or ( exists($searchedEndDate) and xs:dateTime($searchedEndDate) > xs:dateTime($itemStartDate)  ))
                          and
                          ($searchedStartDate='' or ( exists($searchedStartDate) and xs:dateTime($searchedStartDate) < xs:dateTime($itemStartDate)  ))
                        )                       
                        or                       
                        ( 
                         ( $searchedStartDate='' or 
                          (
                          exists($searchedStartDate) and xs:dateTime($itemStartDate) < xs:dateTime($searchedStartDate) and  xs:dateTime($searchedStartDate) < xs:dateTime($itemEndDate)
                          )
                         )
                         and
                         ( $searchedEndDate='' or (
                           exists($searchedEndDate) and xs:dateTime($searchedEndDate) > xs:dateTime($itemStartDate) and xs:dateTime($searchedEndDate) < xs:dateTime($itemEndDate) )
                         )
                        )
                       )
                      ) then $alarm else ()
   let $sonuc := $nedir
   return $sonuc
};


(: ornek kullanim lk:alarmList(1,2) ilk iki eleman :)
(: Su anda kullanilmiyor :)
declare function lk:alarmList($firstElement as xs:int, $lastElement as xs:int) as element(alm:alarm)* 
 {
	for $alarm in doc("//db/TLOSSW/xmls/tlosSWAlarm10.xml")/alm:alarmManagement/alm:alarm[position() = ($firstElement to $lastElement)]
	return  $alarm
};

declare function lk:searchAlarmByName($alarmname as xs:string) as element(alm:alarm)? 
 {
	for $alarm in doc("//db/TLOSSW/xmls/tlosSWAlarm10.xml")/alm:alarmManagement/alm:alarm 
	where $alarm/alm:name = $alarmname
	return $alarm
};

 
declare function lk:alarms() as element(alm:alarm)* 
 {
	for $alarm in doc("//db/TLOSSW/xmls/tlosSWAlarm10.xml")/alm:alarmManagement/alm:alarm 
	return  $alarm
};

(: son kac gun icerisinde arama yapilacaksa $numberOfDay kismina yaziliyor :)
declare function lk:jobAlarmListbyRunId($numberOfElement as xs:int, $runId as xs:int, $jobId as xs:int, $refRunIdBolean as xs:boolean, $numberOfDay as xs:int) as element(alm:alarm)*
 {
    let $runIdFound := if ($runId != 0 ) 
	                   then $runId 
	                   else sq:getId("runId")

    let $posUpper := max(for $runx at $pos in doc("//db/TLOSSW/xmls/tlosSWAlarmHistory10.xml")/alm-history:alarmHistory/alm-history:alarm
	                 where ($runx[@runId = $runIdFound] or not($refRunIdBolean)) and $runx/alm:focus/alm:jobs/alm:job[@id = $jobId or $jobId = 0]
	                 return $pos)

    let $posLower := if ($posUpper - $numberOfElement > 0) then $posUpper - $numberOfElement else 0

	let $sonuc := for $runx at $pos in doc("//db/TLOSSW/xmls/tlosSWAlarmHistory10.xml")/alm-history:alarmHistory/alm-history:alarm
					 where $pos > $posLower and $pos <=$posUpper and ($runx[@runId = $runIdFound] or not($refRunIdBolean)) 
					       and $runx/alm:focus/alm:jobs/alm:job[@id = $jobId or $jobId = 0]
					 order by $runx/@aHistoryId descending
	                 return  $runx
	return $sonuc
};

(: ornek kullanim lk:searchAlarmByAlarmName(xs:string('Failed alarmi')) :)
(: Su anda kullanilmiyor :)
declare function lk:searchAlarmByAlarmName($searchAlarmName as xs:string) as element(alm:alarm)? 
 {
	for $alarm in doc("//db/TLOSSW/xmls/tlosSWAlarm10.xml")/alm:alarmManagement/alm:alarm
	where fn:lower-case($alarm/alm:name)=fn:lower-case($searchAlarmName)
    return $alarm
};

declare function lk:searchAlarmByAlarmId($id as xs:integer) as element(alm:alarm)? 
 {
	for $alarm in doc("//db/TLOSSW/xmls/tlosSWAlarm10.xml")/alm:alarmManagement/alm:alarm
	where $alarm/@ID = $id
    return $alarm
};

declare function lk:searchAlarmHistoryById($id as xs:integer) as element(alm-history:alarm)? 
 {
	for $alarm in doc("//db/TLOSSW/xmls/tlosSWAlarmHistory10.xml")/alm-history:alarmHistory/alm-history:alarm
	where $alarm/@aHistoryId = $id
    return $alarm
};

declare function lk:insertAlarmLock($alarm as element(alm:alarm)) as xs:boolean
{
   let $sonuc := util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWAlarm10.xml")/alm:alarmManagement, lk:insertAlarm($alarm))     
   return true()
};

declare function lk:insertAlarm($alarm as element(alm:alarm)) as node()*
{
    let $XXX := $alarm
    let $nextId := sq:getNextId("alarmId")	

	return update insert 
		<alm:alarm xmlns="http://www.likyateknoloji.com/XML_alarm_types" ID="{$nextId}"> 
                  <alm:name>{data($XXX/alm:name)}</alm:name>
                  <alm:desc>{data($XXX/alm:desc)}</alm:desc>
				  <alm:alarmType>{data($XXX/alm:alarmType)}</alm:alarmType>
				  <alm:subscriptionType>{data($XXX/alm:subscriptionType)}</alm:subscriptionType>
                  <alm:creationDate>{data($XXX/alm:creationDate)}</alm:creationDate>
                  <alm:startDate>{data($XXX/alm:startDate)}</alm:startDate>
                  <alm:endDate>{data($XXX/alm:endDate)}</alm:endDate>
                  <alm:level>{data($XXX/alm:level)}</alm:level>
                  <alm:subscriber>{$XXX/alm:subscriber/*}</alm:subscriber>
                  <alm:focus>{$XXX/alm:focus/*}</alm:focus>
                  <alm:caseManagement>{$XXX/alm:caseManagement/*}</alm:caseManagement>	
                </alm:alarm>	
	into doc("xmldb:exist:///db/TLOSSW/xmls/tlosSWAlarm10.xml")/alm:alarmManagement
} ;

declare function lk:updateAlarm($alarm as element(alm:alarm))
{
	for $alarmdon in doc("//db/TLOSSW/xmls/tlosSWAlarm10.xml")/alm:alarmManagement/alm:alarm
	where $alarmdon/@ID = $alarm/@ID
	return  update replace $alarmdon with $alarm
};

declare function lk:updateAlarmLock($alarm as element(alm:alarm))
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWAlarm10.xml")/alm:alarmManagement/alm:alarm, lk:updateAlarm($alarm))     
};

declare function lk:deleteAlarm($alarm as element(alm:alarm))
 {
	for $alarmdon in doc("//db/TLOSSW/xmls/tlosSWAlarm10.xml")/alm:alarmManagement/alm:alarm
	where $alarmdon/@ID = $alarm/@ID
	return update delete $alarmdon
};

declare function lk:deleteAlarmLock($alarm as element(alm:alarm))
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWAlarm10.xml")/alm:alarmManagement/alm:alarm, lk:deleteAlarm($alarm))     
};

(: ******************* Alarm History ****************************** :)

declare function lk:SWFindAlarms($jobID as xs:string, $userID as xs:int, $agentId as xs:int, $liveStateInfoXML as element(state-types:LiveStateInfo)) as element(alm-history:alarm)?
{

let $alarmPref := doc("//db/TLOSSW/xmls/tlosSWData10.xml")//dat:jobProperties[@ID=$jobID]/dat:alarmPreference
let $alarmList := for $alarmx in $alarmPref/dat:alarmId,
                      $herbiri in doc("//db/TLOSSW/xmls/tlosSWAlarm10.xml")/alm:alarmManagement/alm:alarm
                  where data($herbiri/@ID) = data($alarmx)
                  return $herbiri
let $alarmParams := for $alarmx in $alarmList/alm:focus/alm:jobs/alm:job
                  where data($alarmx/@id) = $jobID
                  return $alarmx

let $depth := data($alarmParams/@depth)
let $warnBy := data($alarmParams/@warnBy)

let $alarmId := $alarmList/data(@ID)

let $alarmLevel := data($alarmList/alm:level)

let $kimicin := $alarmList/alm:subscriber/alm:person

let $alarmCases := $alarmList/alm:caseManagement
let $systemManagement := $alarmCases/alm:systemManagement 
let $stateManagement:= $alarmCases/alm:stateManagement
let $SLAManagement:= $alarmCases/alm:SLAManagement
let $timeManagement:= $alarmCases/alm:timeManagement

let $stateTypesOccured := $liveStateInfoXML/state-types:StateName/text()
let $substateTypesOccured := $liveStateInfoXML/state-types:SubstateName/text()
let $statusTypesOccured := $liveStateInfoXML/state-types:StatusName/text()

let $stateAlarmOccured := for $alarm_kosulu in $stateManagement/state-types:LiveStateInfo, (: alarm durumu :)
                            $isin_durumu in $liveStateInfoXML (: is durumu :)
                        where $alarm_kosulu/state-types:StateName/text()=$isin_durumu/state-types:StateName/text() and
                              (not(fn:exists($alarm_kosulu/state-types:SubstateName/text())) or $alarm_kosulu/state-types:SubstateName/text()=$isin_durumu/state-types:SubstateName/text()) and
                              (not(fn:exists($alarm_kosulu/state-types:StatusName/text())) or $alarm_kosulu/state-types:StatusName/text()=$isin_durumu/state-types:StatusName/text())
                       return <sonuc> <isinDurumu> { $isin_durumu } </isinDurumu>
                                      <alarmKosulu> { $alarm_kosulu} </alarmKosulu>
                              </sonuc>
let $stateAlarmCheckTF := if (fn:exists($stateAlarmOccured) and fn:exists($alarmParams)) then true() else false()

let $sonuc2 :=  
  if ( $stateAlarmCheckTF ) then 
    (: let $userID := 4 :)
    (: let $agentId := 112 :)

    let $nextHistoryId := 111


    let $zaman := fn:dateTime(xs:date("2012-01-16"), xs:time("09:30:10+03:00"))

    let $sonuc := 
      <alm-history:alarm aHistoryId="{$nextHistoryId}" agentId="{$agentId}">
        <alm:creationDate>{fn:current-dateTime()}</alm:creationDate>
        <alm:level>{$alarmLevel}</alm:level>
        <alm-history:alarmId>{$alarmId}</alm-history:alarmId>
        <alm:subscriber>
            {$kimicin}
        </alm:subscriber>
        <alm:focus>
            <alm:jobs>
                {$alarmParams}
            </alm:jobs>
        </alm:focus>
        <alm:caseManagement>
            <alm:stateManagement>
		{$stateAlarmOccured/alarmKosulu/*}
            </alm:stateManagement>
        </alm:caseManagement>
        <alm-history:caseOccured>
            <alm:stateManagement>
              <state-types:LiveStateInfo LSIDateTime="{$stateAlarmOccured/isinDurumu/state-types:LiveStateInfo/@LSIDateTime}">
	       {$stateAlarmOccured/isinDurumu/state-types:LiveStateInfo/*}
              </state-types:LiveStateInfo>
            </alm:stateManagement>
        </alm-history:caseOccured>
      </alm-history:alarm>

    let $insertIt := lk:insertAlarmHistoryLock($sonuc) 
	return $sonuc
  else ()
	
let $sonuc3 := if (fn:exists($sonuc2)) then $sonuc2 else ()

return $sonuc3

};

declare function lk:insertAlarmHistoryLock($alarm as element(alm-history:alarm)) as xs:boolean
{
   let $sonuc := util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWAlarmHistory10.xml")/alm-history:alarmHistory, lk:insertAlarmHistory($alarm))     
   return true()
};

declare function lk:insertAlarmHistory($alarm as element(alm-history:alarm)) as node()*
{
    let $islem := if (fn:exists($alarm)) then
    let $nextId := sq:getNextId("aHistoryId")	
    let $runId := sq:getId("runId")
	  return update insert
      <alm-history:alarm aHistoryId="{$nextId}" agentId="{$alarm/@agentId}" runId="{$runId }">
        {$alarm/*}
      </alm-history:alarm>
	  into doc("xmldb:exist:///db/TLOSSW/xmls/tlosSWAlarmHistory10.xml")/alm-history:alarmHistory
    else ()
    return  $islem
};