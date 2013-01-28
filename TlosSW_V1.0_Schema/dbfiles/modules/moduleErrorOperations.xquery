xquery version "1.0";
module namespace lk = "http://likya.tlos.com/";
declare namespace err = "http://www.likyateknoloji.com/XML_error_types";
declare namespace res = "http://www.likyateknoloji.com/resource-extension-defs";

declare function lk:insertError($error as element(err:SWError))
{	
 update insert $error into doc("xmldb:exist:///db/TLOSSW/xmls/tlosSWErrors10.xml")/err:SWErrors
};