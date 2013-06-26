xquery version "1.0";

module namespace lk = "http://likya.tlos.com/";

declare namespace state-types="http://www.likyateknoloji.com/state-types";

import module namespace met = "http://meta.tlos.com/" at "moduleMetaDataOperations.xquery";

(:
Mapping
$globalStatesDocumentUrl = doc("//db/TLOSSW/xmls/tlosSWGlobalStates10.xml")
:)

declare function lk:getTlosGlobalStates($documentUrl as xs:string)
{
   let $globalStatesDocumentUrl := met:getMetaData($documentUrl, "globalStates")
   
	for $globalStates in doc($globalStatesDocumentUrl)/state-types:GlobalStateDefinition
	return $globalStates
};

