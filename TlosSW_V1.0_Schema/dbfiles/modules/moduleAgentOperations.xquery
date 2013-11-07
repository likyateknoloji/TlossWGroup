xquery version "1.0";

module namespace lk = "http://likya.tlos.com/";

import module namespace sq = "http://sq.tlos.com/" at "../modules/moduleSequenceOperations.xquery";
import module namespace met = "http://meta.tlos.com/" at "moduleMetaDataOperations.xquery";

declare namespace agnt = "http://www.likyateknoloji.com/XML_agent_types";
declare namespace res = "http://www.likyateknoloji.com/resource-extension-defs";
declare namespace par = "http://www.likyateknoloji.com/XML_parameters_types";

(:
Mappings
$agentDocumentUrl = doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")
$sequencesDocumentUrl = doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")

:)

declare function lk:getAgents($documentUrl as xs:string) as element()* 
{
    let $agentDocumentUrl := met:findDocPath($documentUrl, "agents", true())
  
	for $agents in doc($agentDocumentUrl)/agnt:SWAgents
	return  $agents
};

(:declare function lk:checkAgent($documentUrl as xs:string, $agent as element(agnt:SWAgent)) as xs:boolean  
{
	let $swAgent := lk:searchAgent($documentUrl, $agent/agnt:ipAddress, $agent/agnt:jmxTlsPort)
	let $checkUser := if($swAgent/agnt:jmxUser = $agent/agnt:jmxUser and $swAgent/agnt:jmxPassword = $agent/agnt:jmxPassword) 
        then true()
        else (false())
    return $checkUser 
};
:)

declare function lk:checkAgent($documentUrl as xs:string, $agent as element(agnt:SWAgent)) as xs:int  
{

	let $swAgent := lk:searchAgent($documentUrl, $agent/res:Resource, $agent/agnt:jmxTlsPort)
	let $checkUser := if($swAgent/agnt:jmxUser = $agent/agnt:jmxUser and $swAgent/agnt:jmxPassword = $agent/agnt:jmxPassword) 
        then $swAgent/@id
        else (-1)
    return $checkUser 
};

declare function lk:searchAgent($documentUrl as xs:string, $host as xs:string, $jmxport as xs:short)  as element(agnt:SWAgent)? 
{
   let $agentDocumentUrl := met:findDocPath($documentUrl, "agents", true())
   
   return doc($agentDocumentUrl)/agnt:SWAgents/agnt:SWAgent[res:Resource = $host and agnt:jmxTlsPort = $jmxport]
};

declare function lk:searchAgent($documentUrl as xs:string, $searchAgent as element(agnt:SWAgent)) as element(agnt:SWAgent)* 
 {
    let $agentDocumentUrl := met:findDocPath($documentUrl, "agents", true())
	
	for $agent in doc($agentDocumentUrl)/agnt:SWAgents/agnt:SWAgent
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
                   ($agent/agnt:jmxTlsPort eq $searchAgent/agnt:jmxTlsPort or data($searchAgent/agnt:jmxTlsPort)="0")
             )
		then $agent
		else  ( )
};

(: ornek kullanim lk:agentList(1,2) ilk iki eleman :)
declare function lk:agentList($documentUrl as xs:string, $firstElement as xs:int, $lastElement as xs:int) as element(agnt:SWAgent)* 
 {
    let $agentDocumentUrl := met:findDocPath($documentUrl, "agents", true())
	
	for $agent in doc($agentDocumentUrl)/agnt:SWAgents/agnt:SWAgent[position() = ($firstElement to $lastElement)]
	return $agent
};

(: ornek kullanim lk:searchAgentByjmxPort('5555') :)
declare function lk:searchAgentByjmxTlsPort($documentUrl as xs:string, $searchjmxTlsPort as xs:string) as element(agnt:SWAgent)* 
 {
    let $agentDocumentUrl := met:findDocPath($documentUrl, "agents", true())
	
    for $agent in doc($agentDocumentUrl)/agnt:SWAgents/agnt:SWAgent
    where $agent/agnt:jmxTlsPort=$searchjmxTlsPort
    return $agent
};

declare function lk:searchAgentByjmxPort($documentUrl as xs:string, $searchjmxPort as xs:string) as element(agnt:SWAgent)* 
 {
    let $agentDocumentUrl := met:findDocPath($documentUrl, "agents", true())
	
    for $agent in doc($agentDocumentUrl)/agnt:SWAgents/agnt:SWAgent
    where $agent/agnt:jmxPort=$searchjmxPort
    return $agent
};

declare function lk:searchAgentByAgentId($documentUrl as xs:string, $id as xs:integer) as element(agnt:SWAgent)? 
 {
    let $agentDocumentUrl := met:findDocPath($documentUrl, "agents", true())
	
	for $agent in doc($agentDocumentUrl)/agnt:SWAgents/agnt:SWAgent
	where $agent/@id = $id
    return $agent
};

declare function lk:insertAgentLock($documentUrl as xs:string, $agent as element(agnt:SWAgent)) as xs:boolean
{
    let $agentDocumentUrl := met:findDocPath($documentUrl, "agents", true())
	
    let $sonuc := util:exclusive-lock(doc($agentDocumentUrl)/agnt:SWAgents, lk:insertAgent($documentUrl, $agent))     
    return true()
};

declare function lk:insertAgent($documentUrl as xs:string, $agent as element(agnt:SWAgent)) as node()*
{
    let $agentDocumentUrl := met:findDocPath($documentUrl, "agents", true())
	
    let $XXX := $agent
    let $nextId := sq:getNextId($documentUrl, "agentId")	
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
        <agnt:jmxTlsPort>{data($XXX/agnt:jmxTlsPort)}</agnt:jmxTlsPort>
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
	into doc($agentDocumentUrl)/agnt:SWAgents

} ;

declare function lk:deleteAgent($documentUrl as xs:string, $agent as element(agnt:SWAgent))
 {
    let $agentDocumentUrl := met:findDocPath($documentUrl, "agents", true())
	
	for $agentdon in doc($agentDocumentUrl)/agnt:SWAgents/agnt:SWAgent
	where $agentdon/@id = $agent/@id
	return update delete $agentdon
};

declare function lk:deleteAgentLock($documentUrl as xs:string, $agent as element(agnt:SWAgent))
{
   let $agentDocumentUrl := met:findDocPath($documentUrl, "agents", true())
   
   return util:exclusive-lock(doc($agentDocumentUrl)/agnt:SWAgents/agnt:SWAgent, lk:deleteAgent($documentUrl, $agent))     
};

declare function lk:updateAgent($documentUrl as xs:string, $agent as element(agnt:SWAgent)) as xs:boolean
{
    let $agentDocumentUrl := met:findDocPath($documentUrl, "agents", true())
	
    let $varmi := 	for $agentdon in doc($agentDocumentUrl)/agnt:SWAgents/agnt:SWAgent
	                where $agentdon/@id = $agent/@id
                    return count(1)
    let $update := 
	  for $agentdon in doc($agentDocumentUrl)/agnt:SWAgents/agnt:SWAgent
	  where $agentdon/@id = $agent/@id
	  return update replace $agentdon with $agent

    let $sonuc := if ($varmi > 0) then true() else false()
  return $sonuc
};

declare function lk:updateAgentLock($documentUrl as xs:string, $agent as element(agnt:SWAgent))
{
   let $agentDocumentUrl := met:findDocPath($documentUrl, "agents", true())
   
   return util:exclusive-lock(doc($agentDocumentUrl)/agnt:SWAgents/agnt:SWAgent, lk:updateAgent($documentUrl, $agent))     
};

declare function lk:updateUserStopRequestValueLock($documentUrl as xs:string, $agentId as xs:int, $userStopRequestValue as xs:string)
{
   let $agentDocumentUrl := met:findDocPath($documentUrl, "agents", true())
   
   return util:exclusive-lock(doc($agentDocumentUrl)/agnt:SWAgents, lk:updateUserStopRequestValue($documentUrl, $agentId, $userStopRequestValue))
};

declare function lk:updateUserStopRequestValue($documentUrl as xs:string, $agentId as xs:int, $userStopRequestValue as xs:string)
{
    let $agentDocumentUrl := met:findDocPath($documentUrl, "agents", true())
	
	let $doc := doc($agentDocumentUrl)
	let $update := update value $doc/agnt:SWAgents/agnt:SWAgent[@id=data($agentId)]/agnt:userStopRequest with $userStopRequestValue		
	return true()
};

declare function lk:updateJmxValue($documentUrl as xs:string, $agentId as xs:int, $updateValue as xs:boolean, $islem as xs:string) as xs:boolean
{
    let $agentDocumentUrl := met:findDocPath($documentUrl, "agents", true())

    let $doc := doc($agentDocumentUrl)
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
declare function lk:updateJmxValue($documentUrl as xs:string, $agentId as xs:int, $jmxValue as xs:boolean)
{
    let $agentDocumentUrl := met:findDocPath($documentUrl, "agents", true())
	
	let $doc := doc($agentDocumentUrl)
	let $update := update value $doc/agnt:SWAgents/agnt:SWAgent[@id=data($agentId)]/agnt:jmxAvailable with $jmxValue		
	return true()
};
:)

declare function lk:updateJmxValueLock($documentUrl as xs:string, $agentId as xs:int, $jmxValue as xs:boolean, $islem as xs:string)
{
    let $agentDocumentUrl := met:findDocPath($documentUrl, "agents", true())
	
	return util:exclusive-lock(doc($agentDocumentUrl)/agnt:SWAgents, lk:updateJmxValue($documentUrl, $agentId, $jmxValue, $islem))
};

(:
declare function lk:updateInJmxValue($documentUrl as xs:string, $agentId as xs:int, $inJmxValue as xs:boolean){
    let $agentDocumentUrl := met:findDocPath($documentUrl, "agents", true())
	
	let $doc := doc($agentDocumentUrl)
	let $update := update value $doc/agnt:SWAgents/agnt:SWAgent[@id=data($agentId)]/agnt:inJmxAvailable with $inJmxValue		
	return true()
};

declare function lk:updateInJmxValueLock($documentUrl as xs:string, $agentId as xs:int, $inJmxValue as xs:boolean)
{   
    let $agentDocumentUrl := met:findDocPath($documentUrl, "agents", true())
	
	return util:exclusive-lock(doc($agentDocumentUrl)/agnt:SWAgents, lk:updateInJmxValue($documentUrl, $agentId, $inJmxValue))
};

declare function lk:updateOutJmxValue($documentUrl as xs:string, $agentId as xs:int, $outJmxValue as xs:boolean)
{
    let $agentDocumentUrl := met:findDocPath($documentUrl, "agents", true())
	
	let $doc := doc($agentDocumentUrl)
	let $update := update value $doc/agnt:SWAgents/agnt:SWAgent[@id=data($agentId)]/agnt:outJmxAvailable with $outJmxValue		
	return true()
};

declare function lk:updateOutJmxValueLock($documentUrl as xs:string, $agentId as xs:int, $outJmxValue as xs:boolean)
{
    let $agentDocumentUrl := met:findDocPath($documentUrl, "agents", true())
	
	return util:exclusive-lock(doc($agentDocumentUrl)/agnt:SWAgents, lk:updateOutJmxValue($documentUrl, $agentId, $outJmxValue))
};
:)

declare function lk:updateNrpeValue($documentUrl as xs:string, $host as element(res:Resource), $nrpeValue as xs:boolean)
{
    let $agentDocumentUrl := met:findDocPath($documentUrl, "agents", true())
	
	let $doc := doc($agentDocumentUrl)
	let $update := update value $doc/agnt:SWAgents/agnt:SWAgent[res:Resource=$host]/agnt:nrpeAvailable with $nrpeValue		
	return true()
};

declare function lk:updateNrpeValueLock($documentUrl as xs:string, $host as element(res:Resource), $nrpeValue as xs:boolean)
{
    let $agentDocumentUrl := met:findDocPath($documentUrl, "agents", true())
	
	return util:exclusive-lock(doc($agentDocumentUrl)/agnt:SWAgents, lk:updateNrpeValue($documentUrl, $host, $nrpeValue))
};

declare function lk:updateAgentToAvailable($documentUrl as xs:string, $agentId as xs:int)
{
	let $updateJmx := lk:updateJmxValue($documentUrl, $agentId, true(), xs:string("JMX"))
	let $updateInJmx := lk:updateJmxValue($documentUrl, $agentId, true(), xs:string("inJMX"))
	let $updateOutJmx := lk:updateJmxValue($documentUrl, $agentId, true(), xs:string("outJMX"))		
	return true()
};

declare function lk:updateAgentToAvailableLock($documentUrl as xs:string, $agentId as xs:int)
{
    let $agentDocumentUrl := met:findDocPath($documentUrl, "agents", true())
	
	return util:exclusive-lock(doc($agentDocumentUrl)/agnt:SWAgents, lk:updateAgentToAvailable($documentUrl, $agentId))
};

declare function lk:updateAgentToUnAvailable($documentUrl as xs:string, $agentId as xs:int)
{
	let $updateJmx := lk:updateJmxValue($documentUrl, $agentId, false(), xs:string("JMX"))
	let $updateInJmx := lk:updateJmxValue($documentUrl, $agentId, false(), xs:string("inJMX"))
	let $updateOutJmx := lk:updateJmxValue($documentUrl, $agentId, false(), xs:string("outJMX"))	
	return true()
};

declare function lk:updateAgentToUnAvailableLock($documentUrl as xs:string, $agentId as xs:int)
{
    let $agentDocumentUrl := met:findDocPath($documentUrl, "agents", true())
	
	return util:exclusive-lock(doc($agentDocumentUrl)/agnt:SWAgents, lk:updateAgentToUnAvailable($documentUrl, $agentId))
};

declare function lk:getResorces($documentUrl as xs:string) as element()*
{
    let $agentDocumentUrl := met:findDocPath($documentUrl, "agents", true())
	
	for $resources in distinct-values(doc($agentDocumentUrl)/agnt:SWAgents/agnt:SWAgent/res:Resource)
	return
	for $agent in doc($agentDocumentUrl)/agnt:SWAgents/agnt:SWAgent[res:Resource=$resources]
	where $agent/@id = min($agent/@id)
	return $agent
};