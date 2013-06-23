xquery version "1.0";

module namespace hs = "http://hs.tlos.com/";

declare namespace fo="http://www.w3.org/1999/XSL/Format";
declare namespace dat="http://www.likyateknoloji.com/XML_data_types";
declare namespace com="http://www.likyateknoloji.com/XML_common_types";

import module namespace transform="http://exist-db.org/xquery/transform";

(:
Mapping
$xmlTransformXslDocumentUrl = doc("xmldb:exist://db/TLOSSW/xsls/tlosXMLTransformXsl.xsl")
$jobTransformDocumentUrl = doc("xmldb:exist://db/TLOSSW/xsls/tlosJobTransform.xsl")
$dataFoDocumentUrl = doc("xmldb:exist://db/TLOSSW/xsls/tlosDataFo.xsl")
$dataXslDocumentUrl = doc("xmldb:exist://db/TLOSSW/xsls/tlosData.xsl")
$dataDocumentUrl = doc("xmldb:exist://db/TLOSSW/xmls/tlosSWData10.xml")
$calendarDocumentUrl = doc("xmldb:exist://db/TLOSSW/xmls/tlosSWCalendar10.xml")
$userDocumentUrl = doc("xmldb:exist://db/TLOSSW/xmls/tlosSWUser10.xml")
:)

declare function hs:tlosXMLTransformXsl($xmlTransformXslDocumentUrl as xs:string)
{
	let $table := doc($xmlTransformXslDocumentUrl)
	return $table
};

declare function hs:tlosJobTransformXsl($jobTransformDocumentUrl as xs:string)
{
	let $table := doc($jobTransformDocumentUrl)
	return $table
};

declare function hs:tlosDataXslFo($dataFoDocumentUrl as xs:string)
{
	let $table := doc($dataFoDocumentUrl)
	return $table
};

declare function hs:tlosDataXsl($dataXslDocumentUrl as xs:string)
{
	let $table := doc($dataXslDocumentUrl)
	return $table
};

declare function hs:tlosData($dataDocumentUrl as xs:string)
{
	let $table := doc($dataDocumentUrl)
	return $table
};

declare function hs:tlosCalendar($calendarDocumentUrl as xs:string)
{
	let $table := doc($calendarDocumentUrl)
	return $table
};

declare function hs:tlosUser($userDocumentUrl as xs:string)
{
	let $table := doc($userDocumentUrl)
	return $table
};