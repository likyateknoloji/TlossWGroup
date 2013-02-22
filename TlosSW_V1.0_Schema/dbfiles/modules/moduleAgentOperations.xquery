xquery version "1.0";
module namespace lk = "http://likya.tlos.com/";

import module namespace sq = "http://sq.tlos.com/" at "xmldb:exist://db/TLOSSW/modules/moduleSequenceOperations.xquery";

declare namespace agnt = "http://www.likyateknoloji.com/XML_agent_types";
declare namespace res = "http://www.likyateknoloji.com/resource-extension-defs";

declare function lk:getAgents() as element()* 
{
	for $agents in doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")/agnt:SWAgents
	return  $agents
};

(:declare function lk:checkAgent($agent as element(agnt:SWAgent)) as xs:boolean  
{
	let $swAgent := lk:searchAgent($agent/agnt:ipAddress, $agent/agnt:jmxPort)
	let $checkUser := if($swAgent/agnt:jmxUser = $agent/agnt:jmxUser and $swAgent/agnt:jmxPassword = $agent/agnt:jmxPassword) 
        then true()
        else (false())
    return $checkUser 
};
:)

declare function lk:checkAgent($agent as element(agnt:SWAgent)) as xs:int  
{
	(:let $swAgent := lk:searchAgent($agent/agnt:ipAddress, $agent/agnt:jmxPort):)
	let $swAgent := lk:searchAgent($agent/res:Resource, $agent/agnt:jmxPort)
	let $checkUser := if($swAgent/agnt:jmxUser = $agent/agnt:jmxUser and $swAgent/agnt:jmxPassword = $agent/agnt:jmxPassword) 
        then $swAgent/@id
        else (-1)
    return $checkUser 
};

declare function lk:searchAgent($host as element(res:Resource), $jmxport as element(agnt:jmxPort))  as element(agnt:SWAgent)? 
{
   doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")/agnt:SWAgents/agnt:SWAgent[res:Resource = $host and agnt:jmxPort = $jmxport]
};

(: eski versiyon , coklama hatasi veriyordu. hs. 10eylul2012
declare function lk:searchAgent($host as element(res:Resource), $jmxport as as element(res:Resource))  as element(agnt:SWAgent)? 
{
	for $agent in doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")/agnt:SWAgents/agnt:SWAgent
        where $agent/res:Resource = $host and $agent/agnt:jmxPort = $jmxport 
    return $agent 
};
:)


(:
declare function lk:searchAgent($ip as xs:string, $jmxport as xs:int)  as element(agnt:SWAgent)? 
{
	for $agent in doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")/agnt:SWAgents/agnt:SWAgent
        where $agent/agnt:ipAddress = $ip and $agent/agnt:jmxPort = $jmxport 
    return $agent 
};

:)

declare function lk:searchAgent($searchAgent as element(agnt:SWAgent)) as element(agnt:SWAgent)* 
 {
	for $agent in doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")/agnt:SWAgents/agnt:SWAgent
	return if ((fn:contains(fn:lower-case($agent/res:Resource), fn:lower-case($searchAgent/res:Resource)) or data($searchAgent/res:Resource)="")
                   and
                   (fn:contains(fn:lower-case($agent/agnt:osType), fn:lower-case($searchAgent/agnt:osType)) or data($searchAgent/agnt:osType)="")
                   and
                   (fn:contains(fn:lower-case($agent/agnt:jmxUser), fn:lower-case($searchAgent/agnt:jmxUser)) or data($searchAgent/agnt:jmxUser)="")
                   and
                   (fn:contains(fn:lower-case($agent/agnt:inJmxAvailable), fn:lower-case($searchAgent/agnt:inJmxAvailable)) or data($searchAgent/agnt:inJmxAvailable)="") 
                   and
                   (fn:contains(fn:lower-case($agent/agnt:jmxAvailable), fn:lower-case($searchAgent/agnt:jmxAvailable)) or data($searchAgent/agnt:jmxAvailable)="") 
                   and
                   ($agent/agnt:jmxPort eq $searchAgent/agnt:jmxPort or data($searchAgent/agnt:jmxPort)="0")
             )
		then $agent
		else  ( )
};

(: ornek kullanim lk:agentList(1,2) ilk iki eleman :)
declare function lk:agentList($firstElement as xs:int, $lastElement as xs:int) as element(agnt:SWAgent)* 
 {
	for $agent in doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")/agnt:SWAgents/agnt:SWAgent[position() = ($firstElement to $lastElement)]
	return $agent
};

(: ornek kullanim lk:searchAgentByjmxPort('5555') :)
declare function lk:searchAgentByjmxPort($searchjmxPort as xs:string) as element(agnt:SWAgent)* 
 {
    for $agent in doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")/agnt:SWAgents/agnt:SWAgent
    where $agent/agnt:jmxPort=$searchjmxPort
    return $agent
};

declare function lk:searchAgentByAgentId($id as xs:integer) as element(agnt:SWAgent)? 
 {
	for $agent in doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")/agnt:SWAgents/agnt:SWAgent
	where $agent/@id = $id
    return $agent
};

declare function lk:insertAgentLock($agent as element(agnt:SWAgent)) as xs:boolean
{
   let $sonuc := util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")/agnt:SWAgents, lk:insertAgent($agent))     
   return true()
};

declare function lk:insertAgent($agent as element(agnt:SWAgent)) as node()*
{
    let $XXX := $agent
    let $nextId := sq:getNextId("agentId")	
	let $localParameters := count($XXX/agnt:locals)
    let $locals := 	for $loc in $XXX/agnt:locals
	                  return 
					    <agnt:locals> 
	                    {
						for $par in $loc/par:parameter
						return
						 <par:parameter  id="{$nextId}">
                           <par:name>{data($par/par:name)}</par:name>
                           <par:valueString>{data($par/par:valueString)}</par:valueString>
                           <par:preValue type="{data($par/par:preValue/@type)}">{data($par/par:preValue)}</par:preValue>
                           <par:desc>{data($par/par:desc)}</par:desc>
                         </par:parameter>
						}
					    </agnt:locals> 
	return update insert 
	  <agnt:SWAgent id="{$nextId}"> 
        <res:Resource>{data($XXX/res:Resource)}</res:Resource>
        <agnt:osType>{data($XXX/agnt:osType)}</agnt:osType>
        <agnt:agentType>{data($XXX/agnt:agentType)}</agnt:agentType>
        <agnt:nrpePort>{data($XXX/agnt:nrpePort)}</agnt:nrpePort>
        <agnt:jmxPort>{data($XXX/agnt:jmxPort)}</agnt:jmxPort>
        <agnt:jmxUser>{data($XXX/agnt:jmxUser)}</agnt:jmxUser>
        <agnt:jmxPassword>{data($XXX/agnt:jmxPassword)}</agnt:jmxPassword>
        <agnt:inJmxAvailable>{data($XXX/agnt:inJmxAvailable)}</agnt:inJmxAvailable>
        <agnt:outJmxAvailable>{data($XXX/agnt:outJmxAvailable)}</agnt:outJmxAvailable>
        <agnt:jmxAvailable>{data($XXX/agnt:jmxAvailable)}</agnt:jmxAvailable>
        <agnt:userStopRequest>{data($XXX/agnt:userStopRequest)}</agnt:userStopRequest>
        <agnt:nrpeAvailable>{data($XXX/agnt:nrpeAvailable)}</agnt:nrpeAvailable>
        <agnt:lastHeartBeatTime>{data($XXX/agnt:lastHeartBeatTime)}</agnt:lastHeartBeatTime>
        <agnt:durationForUnavailability>{data($XXX/agnt:durationForUnavailability)}</agnt:durationForUnavailability>
        <agnt:lastJobTransfer>{data($XXX/agnt:lastJobTransfer)}</agnt:lastJobTransfer>
        <agnt:jobTransferFailureTime>{data($XXX/agnt:jobTransferFailureTime)}</agnt:jobTransferFailureTime>
        <agnt:workspacePath>{data($XXX/agnt:workspacePath)}</agnt:workspacePath>
        { $locals }
      </agnt:SWAgent>	
	into doc("xmldb:exist:///db/TLOSSW/xmls/tlosSWAgents10.xml")/agnt:SWAgents

} ;

declare function lk:deleteAgent($agent as element(agnt:SWAgent))
 {
	for $agentdon in doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")/agnt:SWAgents/agnt:SWAgent
	where $agentdon/@id = $agent/@id
	return update delete $agentdon
};

declare function lk:deleteAgentLock($agent as element(agnt:SWAgent))
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")/agnt:SWAgents/agnt:SWAgent, lk:deleteAgent($agent))     
};

declare function lk:updateAgent($agent as element(agnt:SWAgent)) as xs:boolean
{
    let $varmi := 	for $agentdon in doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")/agnt:SWAgents/agnt:SWAgent
	                where $agentdon/@id = $agent/@id
                    return count(1)
    let $update := 
	  for $agentdon in doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")/agnt:SWAgents/agnt:SWAgent
	  where $agentdon/@id = $agent/@id
	  return update replace $agentdon with $agent

    let $sonuc := if ($varmi > 0) then true() else false()
  return $sonuc
};

declare function lk:updateAgentLock($agent as element(agnt:SWAgent))
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")/agnt:SWAgents/agnt:SWAgent, lk:updateAgent($agent))     
};

declare function lk:updateUserStopRequestValueLock($agentId as xs:int, $userStopRequestValue as xs:string)
{
	util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")/agnt:SWAgents, lk:updateUserStopRequestValue($agentId, $userStopRequestValue))
};

declare function lk:updateUserStopRequestValue($agentId as xs:int, $userStopRequestValue as xs:string)
{
	let $doc := doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")
	let $update := update value $doc/agnt:SWAgents/agnt:SWAgent[@id=data($agentId)]/agnt:userStopRequest with $userStopRequestValue		
	return true()
};

declare function lk:updateJmxValue($agentId as xs:int, $updateValue as xs:boolean, $islem as xs:string) as xs:boolean
{
let $doc := doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")
let $sonuc := if ($islem = "outJMX" and data($doc/agnt:SWAgents/agnt:SWAgent[@id=data($agentId)]/agnt:outJmxAvailable) != $updateValue) then
               update value $doc/agnt:SWAgents/agnt:SWAgent[@id=data($agentId)]/agnt:outJmxAvailable with $updateValue 
               else if ($islem = "inJMX" and data($doc/agnt:SWAgents/agnt:SWAgent[@id=data($agentId)]/agnt:inJmxAvailable) != $updateValue) then
               update value $doc/agnt:SWAgents/agnt:SWAgent[@id=data($agentId)]/agnt:inJmxAvailable with $updateValue 
               else if ($islem = "JMX" and data($doc/agnt:SWAgents/agnt:SWAgent[@id=data($agentId)]/agnt:jmxAvailable) != $updateValue) then
               update value $doc/agnt:SWAgents/agnt:SWAgent[@id=data($agentId)]/agnt:jmxAvailable with $updateValue 
               else false()
let $result := true() (: burasi sonuca gore degismeli. sonra yap :)


    return $result
};
(: Kullanim -> local:updateJmxValue(1, true(), xs:string("inJMX")) :)

(:
declare function lk:updateJmxValue($agentId as xs:int, $jmxValue as xs:boolean)
{
	let $doc := doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")
	let $update := update value $doc/agnt:SWAgents/agnt:SWAgent[@id=data($agentId)]/agnt:jmxAvailable with $jmxValue		
	return true()
};
:)

declare function lk:updateJmxValueLock($agentId as xs:int, $jmxValue as xs:boolean, $islem as xs:string)
{
	util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")/agnt:SWAgents, lk:updateJmxValue($agentId, $jmxValue, $islem))
};

(:
declare function lk:updateInJmxValue($agentId as xs:int, $inJmxValue as xs:boolean){
	let $doc := doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")
	let $update := update value $doc/agnt:SWAgents/agnt:SWAgent[@id=data($agentId)]/agnt:inJmxAvailable with $inJmxValue		
	return true()
};

declare function lk:updateInJmxValueLock($agentId as xs:int, $inJmxValue as xs:boolean)
{
	util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")/agnt:SWAgents, lk:updateInJmxValue($agentId, $inJmxValue))
};

declare function lk:updateOutJmxValue($agentId as xs:int, $outJmxValue as xs:boolean){
	let $doc := doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")
	let $update := update value $doc/agnt:SWAgents/agnt:SWAgent[@id=data($agentId)]/agnt:outJmxAvailable with $outJmxValue		
	return true()
};

declare function lk:updateOutJmxValueLock($agentId as xs:int, $outJmxValue as xs:boolean)
{
	util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")/agnt:SWAgents, lk:updateOutJmxValue($agentId, $outJmxValue))
};
:)

declare function lk:updateNrpeValue($host as element(res:Resource), $nrpeValue as xs:boolean){
	let $doc := doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")
	let $update := update value $doc/agnt:SWAgents/agnt:SWAgent[res:Resource=$host]/agnt:nrpeAvailable with $nrpeValue		
	return true()
};

declare function lk:updateNrpeValueLock($host as element(res:Resource), $nrpeValue as xs:boolean)
{
	util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")/agnt:SWAgents, lk:updateNrpeValue($host, $nrpeValue))
};

declare function lk:updateAgentToAvailable($agentId as xs:int){
	let $updateJmx := lk:updateJmxValue($agentId, true(), xs:string("JMX"))
	let $updateInJmx := lk:updateJmxValue($agentId, true(), xs:string("inJMX"))
	let $updateOutJmx := lk:updateJmxValue($agentId, true(), xs:string("outJMX"))		
	return true()
};

declare function lk:updateAgentToAvailableLock($agentId as xs:int)
{
	util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")/agnt:SWAgents, lk:updateAgentToAvailable($agentId))
};

declare function lk:updateAgentToUnAvailable($agentId as xs:int){
	let $updateJmx := lk:updateJmxValue($agentId, false(), xs:string("JMX"))
	let $updateInJmx := lk:updateJmxValue($agentId, false(), xs:string("inJMX"))
	let $updateOutJmx := lk:updateJmxValue($agentId, false(), xs:string("outJMX"))	
	return true()
};

declare function lk:updateAgentToUnAvailableLock($agentId as xs:int)
{
	util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")/agnt:SWAgents, lk:updateAgentToUnAvailable($agentId))
};

declare function lk:getResorces() as element()*
{
	for $resources in distinct-values(doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")/agnt:SWAgents/agnt:SWAgent/res:Resource)
	return
	for $agent in doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")/agnt:SWAgents/agnt:SWAgent[res:Resource=$resources]
	where $agent/@id = min($agent/@id)
	return $agent
};