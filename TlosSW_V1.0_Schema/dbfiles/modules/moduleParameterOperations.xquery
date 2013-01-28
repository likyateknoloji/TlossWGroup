xquery version "1.0";
module namespace lk = "http://likya.tlos.com/";
import module namespace sq = "http://sq.tlos.com/" at "xmldb:exist://db/TLOSSW/modules/moduleSequenceOperations.xquery";
declare namespace dat="http://www.likyateknoloji.com/XML_data_types";

declare namespace state-types="http://www.likyateknoloji.com/state-types";
declare namespace fn = "http://www.w3.org/2005/xpath-functions";
declare namespace par="http://www.likyateknoloji.com/XML_parameters_types";


declare function lk:searchGlobalParameter($searchParam as xs:NCName) as element(par:parameter)* 
 {
	for $param in doc("//db/TLOSSW/xmls/tlosSWParameters10.xml")/par:Parameters/par:Globals/par:parameter
		return if (
                   (fn:contains(fn:upper-case($param/par:name), fn:upper-case($searchParam)) or data($searchParam)="")
             )
		then $param
		else  ( )
};

(: ornek kullanim lk:parameterList(1,2) ilk uc eleman :)
declare function lk:parameterList($firstElement as xs:int, $lastElement as xs:int) as element(par:parameter)*
 {
	for $par in doc("//db/TLOSSW/xmls/tlosSWParameters10.xml")/par:Parameters/par:Globals/par:parameter[position() = ($firstElement to $lastElement)]
	return  $par
};

