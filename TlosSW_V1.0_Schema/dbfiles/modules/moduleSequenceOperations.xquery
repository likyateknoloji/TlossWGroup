xquery version "1.0";
module namespace sq = "http://sq.tlos.com/";
declare namespace com = "http://www.likyateknoloji.com/XML_common_types";
declare namespace util = "http://exist-db.org/xquery/util";
(: declare namespace dbc = "http://www.likyateknoloji.com/XML_dbconnection_types"; :)

declare function sq:getTlosSequenceData(){	
	for $sequences in doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData
	return  $sequences
};

declare function sq:getNextId($node as xs:string) as xs:integer
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData, 
				let $nextId := doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:*[local-name() eq $node] + 1
				let $atama := doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:*[local-name() eq $node]
				let $ata:= local-name($atama)
                let $sorgu := util:eval(concat("<com:", $ata, ">", $nextId, "</com:",$ata,">"))
   				let $func := (update replace doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:*[local-name() eq $node] with 
								$sorgu)
   				return $nextId )
};

declare function sq:getId($node as xs:string) as xs:int
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData, 
				let $getId := doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:*[local-name() eq $node]
   				return $getId )
};
(: iptal hs.
declare function sq:getNextJobId() as xs:int
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData, 
				let $nextJobId := doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:jobId + 1
   				let $func := (update replace doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:jobId with 
								<com:jobId>{data($nextJobId)}</com:jobId>)
   				return $nextJobId )
};
:)
(: iptal hs.
declare function sq:getNextScenarioId() as xs:int
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData, 
				let $nextScenarioId := doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:scenarioId + 1
   				let $func := (update replace doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:scenarioId with 
								<com:scenarioId>{data($nextScenarioId)}</com:scenarioId>)
   				return $nextScenarioId )
};
:)
(: iptal hs.
declare function sq:getNextCalendarId() as xs:int
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData, 
				let $nextCalendarId := doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:calendarId + 1
   				let $func := (update replace doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:calendarId with 
								<com:calendarId>{data($nextCalendarId)}</com:calendarId>)
   				return $nextCalendarId )
};
:)
(: iptal hs.
declare function sq:getNextUserId() as xs:int
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData, 
				let $nextUserId := doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:userId + 1
   				let $func := (update replace doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:userId with 
								<com:userId>{data($nextUserId)}</com:userId>)
   				return $nextUserId )
};
:)
declare function sq:getNextPermissionId() as xs:int
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData, 
				let $nextPermissionId := doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:permissionId + 1
   				let $func := (update replace doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:permissionId with 
								<com:permissionId>{data($nextPermissionId)}</com:permissionId>)
   				return $nextPermissionId )
};

declare function sq:getNextRunId() as xs:int
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData, 
				let $nextRunId := doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:runId + 1
   				let $func := (update replace doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:runId with 
								<com:runId>{data($nextRunId)}</com:runId>)
   				return $nextRunId )
};

declare function sq:getNextPlanId() as xs:int
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData, 
				let $nextPlanId := doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:planId + 1
   				let $func := (update replace doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:planId with 
								<com:planId>{data($nextPlanId)}</com:planId>)
   				return $nextPlanId )
};

declare function sq:getNextSolticeId() as xs:int
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData, 
				let $nextSolticeId := doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:solsticeId + 1
   				let $func := (update replace doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:solsticeId with 
								<com:solsticeId>{data($nextSolticeId)}</com:solsticeId>)
   				return $nextSolticeId )
};
(: iptal hs.
declare function sq:getNextTraceId() as xs:int
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData, 
				let $nextTraceId := doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:traceId + 1
   				let $func := (update replace doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:traceId with 
								<com:traceId>{data($nextTraceId)}</com:traceId>)
   				return $nextTraceId )
};
:)
(: iptal hs.
declare function sq:getNextErrorId() as xs:int
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData, 
				let $nextErrorId := doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:errorId + 1
   				let $func := (update replace doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:errorId with 
								<com:errorId>{data($nextErrorId)}</com:errorId>)
   				return $nextErrorId )
};
:)
declare function sq:getJobId() as xs:int
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData, 
				let $jobId := doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:jobId
   				return $jobId )
};

declare function sq:getScenarioId() as xs:int
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData, 
				let $scenarioId := doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:scenarioId 
   				return $scenarioId )
};

declare function sq:getCalendarId() as xs:int
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData, 
				let $calendarId := doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:calendarId
   				return $calendarId )
};

declare function sq:getUserId() as xs:int
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData, 
				let $userId := doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:userId
   				return $userId )
};

declare function sq:getReportId() as xs:int
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData, 
				let $reportId := doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:reportId
   				return $reportId )
};

declare function sq:getPermissionId() as xs:int
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData, 
				let $permissionId := doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:permissionId
   				return $permissionId )
};

declare function sq:getRunId() as xs:int
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData, 
				let $runId := doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:runId
   				return $runId )
};

declare function sq:getPlanId() as xs:int
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData, 
				let $planId := doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:planId
   				return $planId )
};

declare function sq:getSolticeId() as xs:int
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData, 
				let $solticeId := doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:solsticeId
   				return $solticeId )
};

declare function sq:getTraceId() as xs:int
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData, 
				let $traceId := doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:traceId
   				return $traceId )
};

declare function sq:getErrorId() as xs:int
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData, 
				let $errorId := doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:errorId
   				return $errorId )
};
(: gerek yok. digerleri de kalkacak. hakan
declare function sq:getNextDbConnectionId() as xs:int
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWDBConnections10.xml")/dbc:dbList, 
				let $nextDbConnectionId := doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:dbConnectionId + 1
   				let $func := (update replace doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")/com:TlosSequenceData/com:dbConnectionId with 
								<com:dbConnectionId>{data($nextDbConnectionId)}</com:dbConnectionId>)
   				return $nextDbConnectionId )
};
:)