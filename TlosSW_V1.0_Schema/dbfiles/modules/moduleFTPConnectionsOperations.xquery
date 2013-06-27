xquery version "1.0";

module namespace fc = "http://fc.tlos.com/";

import module namespace met = "http://meta.tlos.com/" at "moduleMetaDataOperations.xquery";

declare namespace ftp = "http://www.likyateknoloji.com/XML_ftp_adapter_types";
declare namespace com = "http://www.likyateknoloji.com/XML_common_types";

(:
Mapping
$ftpConnnectionsDocumentUrl = doc("//db/TLOSSW/xmls/tlosSWSJFTPConnections10.xml")
:)

declare function fc:searchFTPConnectionById($documentUrl as xs:string, $id as xs:integer) as element(ftp:ftpProperties)? 
{
    let $ftpConnnectionsDocumentUrl := met:getMetaData($documentUrl, "ftpConnnections")
	
	for $ftpProperties in doc($ftpConnnectionsDocumentUrl)/ftp:ftpConnections/ftp:ftpProperties
	where $ftpProperties/@id = $id
	return $ftpProperties
};

declare function fc:getFTPConnectionList($documentUrl as xs:string) as element(ftp:ftpProperties)* 
{
    let $ftpConnnectionsDocumentUrl := met:getMetaData($documentUrl, "ftpConnnections")
	
	for $ftpProperties in doc($ftpConnnectionsDocumentUrl)/ftp:ftpConnections/ftp:ftpProperties
	return $ftpProperties
};

declare function fc:searchFTPConnection($documentUrl as xs:string, $ftpConnection as element(ftp:ftpProperties)) as element(ftp:ftpProperties)* 
{
    let $ftpConnnectionsDocumentUrl := met:getMetaData($documentUrl, "ftpConnnections")
	
	for $ftpProperty in doc($ftpConnnectionsDocumentUrl)/ftp:ftpConnections/ftp:ftpProperties
	return if ((fn:lower-case($ftpProperty/ftp:connection/com:connName)=fn:lower-case($ftpConnection/ftp:connection/com:connName) or data($ftpConnection/ftp:connection/com:connName) = ""))
		then $ftpProperty
		else  ( )
};

declare function fc:deleteFTPConnection($documentUrl as xs:string, $ftpConnection as element(ftp:ftpProperties))
{
    let $ftpConnnectionsDocumentUrl := met:getMetaData($documentUrl, "ftpConnnections")
	
	for $ftpProperty in doc($ftpConnnectionsDocumentUrl)/ftp:ftpConnections/ftp:ftpProperties
	where $ftpProperty/@id = $ftpConnection/@id
	return update delete $ftpProperty
};

declare function fc:checkFTPConnectionName($documentUrl as xs:string, $ftpConnection as element(ftp:ftpProperties)) as element(ftp:ftpConnections)* 
{
    let $ftpConnnectionsDocumentUrl := met:getMetaData($documentUrl, "ftpConnnections")
	
	for $ftpProperty in doc($ftpConnnectionsDocumentUrl)/ftp:ftpConnections/ftp:ftpProperties
	return if ((fn:lower-case($ftpConnection/ftp:connection/com:connName)=fn:lower-case($ftpProperty/ftp:connection/com:connName)) 
			and ($ftpProperty/@id != $ftpConnection/@id))
		then $ftpProperty
		else  ( )
};

(: bu fonksiyon cagrilmadan once id alma fonksiyonu cagriliyor, buraya gelen $ftpConnection datasi icinde id var :)
declare function fc:insertFTPConnection($documentUrl as xs:string, $ftpConnection as element(ftp:ftpProperties)) as xs:boolean?
{	
    let $ftpConnnectionsDocumentUrl := met:getMetaData($documentUrl, "ftpConnnections")
	
	let $insert := update insert $ftpConnection into doc($ftpConnnectionsDocumentUrl)/ftp:ftpConnections
	return true()
} ;

declare function fc:updateFTPConnection($documentUrl as xs:string, $ftpConnection as element(ftp:ftpProperties))
{
    let $ftpConnnectionsDocumentUrl := met:getMetaData($documentUrl, "ftpConnnections")
	
	for $ftpDef in doc($ftpConnnectionsDocumentUrl)/ftp:ftpConnections/ftp:ftpProperties
	where $ftpDef/@id = $ftpConnection/@id
	return  update replace $ftpDef with $ftpConnection
};

declare function fc:updateFTPConnectionLock($documentUrl as xs:string, $ftpConnection as element(ftp:ftpProperties))
{
    let $ftpConnnectionsDocumentUrl := met:getMetaData($documentUrl, "ftpConnnections")
	
    return util:exclusive-lock(doc($ftpConnnectionsDocumentUrl)/ftp:ftpConnections, fc:updateFTPConnection($documentUrl, $ftpConnection))     
};
