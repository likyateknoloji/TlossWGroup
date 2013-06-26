xquery version "1.0";

module namespace lk = "http://likya.tlos.com/";

import module namespace met = "http://meta.tlos.com/" at "moduleMetaDataOperations.xquery";

declare namespace fn = "http://www.w3.org/2005/xpath-functions";
declare namespace par="http://www.likyateknoloji.com/XML_parameters_types";

(:
Mapping
$parametersDocumentUrl = doc("//db/TLOSSW/xmls/tlosSWParameters10.xml")
:)

declare function lk:searchGlobalParameter($documentUrl as xs:string, $searchParam as xs:NCName) as element(par:parameter)* 
{
   let $parametersDocumentUrl := met:getMetaData($documentUrl, "parameters")
   
	for $param in doc($parametersDocumentUrl)/par:Parameters/par:Globals/par:parameter
		return if (
                   (fn:contains(fn:upper-case($param/par:name), fn:upper-case($searchParam)) or data($searchParam)="")
             )
		then $param
		else  ( )
};

(: ornek kullanim lk:parameterList(1,2) ilk uc eleman :)
declare function lk:parameterList($documentUrl as xs:string, $firstElement as xs:int, $lastElement as xs:int) as element(par:parameter)*
{
   let $parametersDocumentUrl := met:getMetaData($documentUrl, "parameters")
   
	for $par in doc($parametersDocumentUrl)/par:Parameters/par:Globals/par:parameter[position() = ($firstElement to $lastElement)]
	return  $par
};

