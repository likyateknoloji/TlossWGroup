xquery version "1.0";

module namespace sq = "http://sq.tlos.com/";

declare namespace com = "http://www.likyateknoloji.com/XML_common_types";
declare namespace util = "http://exist-db.org/xquery/util";
(: declare namespace dbc = "http://www.likyateknoloji.com/XML_dbconnection_types"; :)

declare function sq:getTlosSequenceData($documentUrl as xs:string){	
	for $sequences in doc($documentUrl)/com:TlosSequenceData
	return  $sequences
};

declare function sq:getNextId($documentUrl as xs:string, $node as xs:string) as xs:integer
{
   util:exclusive-lock(doc($documentUrl)/com:TlosSequenceData, 
				let $nextId := doc($documentUrl)/com:TlosSequenceData/com:*[local-name() eq $node] + 1
				let $atama := doc($documentUrl)/com:TlosSequenceData/com:*[local-name() eq $node]
				let $ata:= local-name($atama)
                let $sorgu := util:eval(concat("<com:", $ata, ">", $nextId, "</com:",$ata,">"))
   				let $func := (update replace doc($documentUrl)/com:TlosSequenceData/com:*[local-name() eq $node] with 
								$sorgu)
   				return $nextId )
};

declare function sq:getId($documentUrl as xs:string, $node as xs:string) as xs:int
{
   util:exclusive-lock(doc($documentUrl)/com:TlosSequenceData, 
				let $getId := doc($documentUrl)/com:TlosSequenceData/com:*[local-name() eq $node]
   				return $getId )
};
(: iptal hs.
declare function sq:getNextJobId($documentUrl as xs:string) as xs:int
{
   util:exclusive-lock(doc($documentUrl)/com:TlosSequenceData, 
				let $nextJobId := doc($documentUrl)/com:TlosSequenceData/com:jobId + 1
   				let $func := (update replace doc($documentUrl)/com:TlosSequenceData/com:jobId with 
								<com:jobId>{data($nextJobId)}</com:jobId>)
   				return $nextJobId )
};
:)
(: iptal hs.
declare function sq:getNextScenarioId($documentUrl as xs:string) as xs:int
{
   util:exclusive-lock(doc($documentUrl)/com:TlosSequenceData, 
				let $nextScenarioId := doc($documentUrl)/com:TlosSequenceData/com:scenarioId + 1
   				let $func := (update replace doc($documentUrl)/com:TlosSequenceData/com:scenarioId with 
								<com:scenarioId>{data($nextScenarioId)}</com:scenarioId>)
   				return $nextScenarioId )
};
:)
(: iptal hs.
declare function sq:getNextCalendarId($documentUrl as xs:string) as xs:int
{
   util:exclusive-lock(doc($documentUrl)/com:TlosSequenceData, 
				let $nextCalendarId := doc($documentUrl)/com:TlosSequenceData/com:calendarId + 1
   				let $func := (update replace doc($documentUrl)/com:TlosSequenceData/com:calendarId with 
								<com:calendarId>{data($nextCalendarId)}</com:calendarId>)
   				return $nextCalendarId )
};
:)
(: iptal hs.
declare function sq:getNextUserId($documentUrl as xs:string) as xs:int
{
   util:exclusive-lock(doc($documentUrl)/com:TlosSequenceData, 
				let $nextUserId := doc($documentUrl)/com:TlosSequenceData/com:userId + 1
   				let $func := (update replace doc($documentUrl)/com:TlosSequenceData/com:userId with 
								<com:userId>{data($nextUserId)}</com:userId>)
   				return $nextUserId )
};
:)
declare function sq:getNextPermissionId($documentUrl as xs:string) as xs:int
{
   util:exclusive-lock(doc($documentUrl)/com:TlosSequenceData, 
				let $nextPermissionId := doc($documentUrl)/com:TlosSequenceData/com:permissionId + 1
   				let $func := (update replace doc($documentUrl)/com:TlosSequenceData/com:permissionId with 
								<com:permissionId>{data($nextPermissionId)}</com:permissionId>)
   				return $nextPermissionId )
};

declare function sq:getNextRunId($documentUrl as xs:string) as xs:int
{
   util:exclusive-lock(doc($documentUrl)/com:TlosSequenceData, 
				let $nextRunId := doc($documentUrl)/com:TlosSequenceData/com:runId + 1
   				let $func := (update replace doc($documentUrl)/com:TlosSequenceData/com:runId with 
								<com:runId>{data($nextRunId)}</com:runId>)
   				return $nextRunId )
};

declare function sq:getNextPlanId($documentUrl as xs:string) as xs:int
{
   util:exclusive-lock(doc($documentUrl)/com:TlosSequenceData, 
				let $nextPlanId := doc($documentUrl)/com:TlosSequenceData/com:planId + 1
   				let $func := (update replace doc($documentUrl)/com:TlosSequenceData/com:planId with 
								<com:planId>{data($nextPlanId)}</com:planId>)
   				return $nextPlanId )
};

declare function sq:getNextSolticeId($documentUrl as xs:string) as xs:int
{
   util:exclusive-lock(doc($documentUrl)/com:TlosSequenceData, 
				let $nextSolticeId := doc($documentUrl)/com:TlosSequenceData/com:solsticeId + 1
   				let $func := (update replace doc($documentUrl)/com:TlosSequenceData/com:solsticeId with 
								<com:solsticeId>{data($nextSolticeId)}</com:solsticeId>)
   				return $nextSolticeId )
};
(: iptal hs.
declare function sq:getNextTraceId($documentUrl as xs:string) as xs:int
{
   util:exclusive-lock(doc($documentUrl)/com:TlosSequenceData, 
				let $nextTraceId := doc($documentUrl)/com:TlosSequenceData/com:traceId + 1
   				let $func := (update replace doc($documentUrl)/com:TlosSequenceData/com:traceId with 
								<com:traceId>{data($nextTraceId)}</com:traceId>)
   				return $nextTraceId )
};
:)
(: iptal hs.
declare function sq:getNextErrorId($documentUrl as xs:string) as xs:int
{
   util:exclusive-lock(doc($documentUrl)/com:TlosSequenceData, 
				let $nextErrorId := doc($documentUrl)/com:TlosSequenceData/com:errorId + 1
   				let $func := (update replace doc($documentUrl)/com:TlosSequenceData/com:errorId with 
								<com:errorId>{data($nextErrorId)}</com:errorId>)
   				return $nextErrorId )
};
:)
declare function sq:getJobId($documentUrl as xs:string) as xs:int
{
   util:exclusive-lock(doc($documentUrl)/com:TlosSequenceData, 
				let $jobId := doc($documentUrl)/com:TlosSequenceData/com:jobId
   				return $jobId )
};

declare function sq:getScenarioId($documentUrl as xs:string) as xs:int
{
   util:exclusive-lock(doc($documentUrl)/com:TlosSequenceData, 
				let $scenarioId := doc($documentUrl)/com:TlosSequenceData/com:scenarioId 
   				return $scenarioId )
};

declare function sq:getCalendarId($documentUrl as xs:string) as xs:int
{
   util:exclusive-lock(doc($documentUrl)/com:TlosSequenceData, 
				let $calendarId := doc($documentUrl)/com:TlosSequenceData/com:calendarId
   				return $calendarId )
};

declare function sq:getUserId($documentUrl as xs:string) as xs:int
{
   util:exclusive-lock(doc($documentUrl)/com:TlosSequenceData, 
				let $userId := doc($documentUrl)/com:TlosSequenceData/com:userId
   				return $userId )
};

declare function sq:getReportId($documentUrl as xs:string) as xs:int
{
   util:exclusive-lock(doc($documentUrl)/com:TlosSequenceData, 
				let $reportId := doc($documentUrl)/com:TlosSequenceData/com:reportId
   				return $reportId )
};

declare function sq:getPermissionId($documentUrl as xs:string) as xs:int
{
   util:exclusive-lock(doc($documentUrl)/com:TlosSequenceData, 
				let $permissionId := doc($documentUrl)/com:TlosSequenceData/com:permissionId
   				return $permissionId )
};

declare function sq:getRunId($documentUrl as xs:string) as xs:int
{
   util:exclusive-lock(doc($documentUrl)/com:TlosSequenceData, 
				let $runId := doc($documentUrl)/com:TlosSequenceData/com:runId
   				return $runId )
};

declare function sq:getPlanId($documentUrl as xs:string) as xs:int
{
   util:exclusive-lock(doc($documentUrl)/com:TlosSequenceData, 
				let $planId := doc($documentUrl)/com:TlosSequenceData/com:planId
   				return $planId )
};

declare function sq:getSolticeId($documentUrl as xs:string) as xs:int
{
   util:exclusive-lock(doc($documentUrl)/com:TlosSequenceData, 
				let $solticeId := doc($documentUrl)/com:TlosSequenceData/com:solsticeId
   				return $solticeId )
};

declare function sq:getTraceId($documentUrl as xs:string) as xs:int
{
   util:exclusive-lock(doc($documentUrl)/com:TlosSequenceData, 
				let $traceId := doc($documentUrl)/com:TlosSequenceData/com:traceId
   				return $traceId )
};

declare function sq:getErrorId($documentUrl as xs:string) as xs:int
{
   util:exclusive-lock(doc($documentUrl)/com:TlosSequenceData, 
				let $errorId := doc($documentUrl)/com:TlosSequenceData/com:errorId
   				return $errorId )
};
