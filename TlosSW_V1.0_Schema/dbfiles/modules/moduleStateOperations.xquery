xquery version "1.0";

module namespace lk = "http://likya.tlos.com/";

declare namespace state-types="http://www.likyateknoloji.com/state-types";

(:
Mapping
$globalStatesDocumentUrl = doc("//db/TLOSSW/xmls/tlosSWGlobalStates10.xml")
:)

declare function lk:getTlosGlobalStates($globalStatesDocumentUrl as xs:string)
 {
	for $globalStates in doc($globalStatesDocumentUrl)/state-types:GlobalStateDefinition
	return $globalStates
};

