xquery version "1.0";
module namespace lk = "http://likya.tlos.com/";

declare namespace state-types="http://www.likyateknoloji.com/state-types";

declare function lk:getTlosGlobalStates()
 {
	for $globalStates in doc("//db/TLOSSW/xmls/tlosSWGlobalStates10.xml")/state-types:GlobalStateDefinition
	return $globalStates
};

