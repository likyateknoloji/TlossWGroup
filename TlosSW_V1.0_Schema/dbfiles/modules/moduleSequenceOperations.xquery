xquery version "1.0";

module namespace sq = "http://sq.tlos.com/";

import module namespace met = "http://meta.tlos.com/" at "moduleMetaDataOperations.xquery";

declare namespace com = "http://www.likyateknoloji.com/XML_common_types";
declare namespace util = "http://exist-db.org/xquery/util";
(: declare namespace dbc = "http://www.likyateknoloji.com/XML_dbconnection_types"; :)

declare function sq:getTlosSequenceData($documentUrl as xs:string)
{
    let $sequenceDocumentUrl := met:getMetaData($documentUrl, "sequenceData")
	
	for $sequences in doc($sequenceDocumentUrl)/com:TlosSequenceData
	return  $sequences
};

declare function sq:getNextId($documentUrl as xs:string, $node as xs:string) as xs:integer
{
    let $sequenceDocumentUrl := met:getMetaData($documentUrl, "sequenceData")
	
	let $doc := doc($sequenceDocumentUrl)
    return util:exclusive-lock($doc/com:TlosSequenceData, 
				let $nextId := $doc/com:TlosSequenceData/com:*[local-name() eq $node] + 1
				let $atama := $doc/com:TlosSequenceData/com:*[local-name() eq $node]
				let $ata:= local-name($atama)
                let $sorgu := util:eval(concat("<com:", $ata, ">", $nextId, "</com:",$ata,">"))
   				let $func := (update replace $doc/com:TlosSequenceData/com:*[local-name() eq $node] with 
								$sorgu)
   				return $nextId )
};

declare function sq:getId($documentUrl as xs:string, $node as xs:string) as xs:int
{
    let $sequenceDocumentUrl := met:getMetaData($documentUrl, "sequenceData")
	
	let $doc := doc($sequenceDocumentUrl)
    return util:exclusive-lock($doc/com:TlosSequenceData, 
				let $getId := $doc/com:TlosSequenceData/com:*[local-name() eq $node]
   				return $getId )
};
