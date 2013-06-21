xquery version "1.0";
module namespace lk = "http://likya.tlos.com/";
declare namespace err = "http://www.likyateknoloji.com/XML_error_types";
declare namespace res = "http://www.likyateknoloji.com/resource-extension-defs";

(:
Mapping
$errorsDocumentUrl = doc("xmldb:exist:///db/TLOSSW/xmls/tlosSWErrors10.xml")
:)

(: //TODO seq id arttirimi ? :)
declare function lk:insertError($errorsDocumentUrl as xs:string, $error as element(err:SWError))
{	
 update insert $error into doc($errorsDocumentUrl)/err:SWErrors
};