xquery version "1.0";
module namespace hs = "http://hs.tlos.com/";
import module namespace sq = "http://sq.tlos.com/" at "xmldb:exist://db/TLOSSW/modules/moduleSequenceOperations.xquery";
declare namespace trc = "http://www.likyateknoloji.com/XML_trace_types";
declare namespace com = "http://www.likyateknoloji.com/XML_common_types";

declare function hs:insertTrace($trc as element(trc:trace))
{	
	let $nextTraceId := sq:getNextId("traceId")
	for $trace in doc("xmldb:exist:///db/TLOSSW/xmls/tlosSWTrace10.xml")/trc:TlosTraceData
	return  update insert 
		<trace xmlns="http://www.likyateknoloji.com/XML_trace_types" id="{$nextTraceId}"> 
			<trcTime millis="{data($trc/trcTime/@millis)}">{data($trc/trcTime)}</trcTime>
			<trcUser id="{data($trc/trcUser/@id)}" password="{data($trc/trcUser/@password)}" role="{data($trc/trcUser/@role)}">{data($trc/trcUser)}</trcUser>
			<trcUserAgent ip="{data($trc/trcUserAgent/@ip)}">{data($trc/trcUserAgent)}</trcUserAgent>
			<trcSource javaProject="{data($trc/trcSource/@javaProject)}" package="{data($trc/trcSource/@package)}" class="{data($trc/trcSource/@class)}" method="{data($trc/trcSource/@method)}">{data($trc/trcSource)}</trcSource>
			<componentId>{data($trc/componentId)}</componentId>
			<description>{data($trc/description)}</description>
		</trace>	
	into $trace
} ;

