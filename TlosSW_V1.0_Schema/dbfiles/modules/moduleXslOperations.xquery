xquery version "1.0";

module namespace hs = "http://hs.tlos.com/";

declare namespace fo="http://www.w3.org/1999/XSL/Format";
declare namespace dat="http://www.likyateknoloji.com/XML_data_types";
declare namespace com="http://www.likyateknoloji.com/XML_common_types";

import module namespace transform="http://exist-db.org/xquery/transform";
import module namespace met = "http://meta.tlos.com/" at "moduleMetaDataOperations.xquery";

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

declare function hs:tlosXMLTransformXsl($documentUrl as xs:string)
{
   let $xmlTransformXslDocumentUrl := met:getMetaData($documentUrl, "xmlTransformXsl")
   
	let $table := doc($xmlTransformXslDocumentUrl)
	return $table
};

declare function hs:tlosJobTransformXsl($documentUrl as xs:string)
{
   let $jobTransformDocumentUrl := met:getMetaData($documentUrl, "jobTransform")
   
	let $table := doc($jobTransformDocumentUrl)
	return $table
};

declare function hs:tlosDataXslFo($documentUrl as xs:string)
{
   let $dataFoDocumentUrl := met:getMetaData($documentUrl, "dataFo")
   
	let $table := doc($dataFoDocumentUrl)
	return $table
};

declare function hs:tlosDataXsl($documentUrl as xs:string)
{
   let $dataXslDocumentUrl := met:getMetaData($documentUrl, "dataXsl")
   
	let $table := doc($dataXslDocumentUrl)
	return $table
};

declare function hs:tlosData($documentUrl as xs:string)
{
   let $dataDocumentUrl := met:getMetaData($documentUrl, "sjData")
   
	let $table := doc($dataDocumentUrl)
	return $table
};

declare function hs:tlosCalendar($documentUrl as xs:string)
{
   let $calendarDocumentUrl := met:getMetaData($documentUrl, "calendar")
   
	let $table := doc($calendarDocumentUrl)
	return $table
};

declare function hs:tlosUser($documentUrl as xs:string)
{
   let $userDocumentUrl := met:getMetaData($documentUrl, "user")
   
	let $table := doc($userDocumentUrl)
	return $table
};