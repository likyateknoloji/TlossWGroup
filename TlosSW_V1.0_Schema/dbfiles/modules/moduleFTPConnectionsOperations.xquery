xquery version "1.0";

module namespace fc = "http://fc.tlos.com/";

declare namespace ftp = "http://www.likyateknoloji.com/XML_ftp_adapter_types";
declare namespace com = "http://www.likyateknoloji.com/XML_common_types";

(:
Mapping
$ftpConnnectionsDocumentUrl = doc("//db/TLOSSW/xmls/tlosSWSJFTPConnections10.xml")
:)

declare function fc:searchFTPConnectionById($ftpConnnectionsDocumentUrl as xs:string, $id as xs:integer) as element(ftp:ftpProperties)? 
 {
	for $ftpProperties in doc($ftpConnnectionsDocumentUrl)/ftp:ftpConnections/ftp:ftpProperties
	where $ftpProperties/@id = $id
	return $ftpProperties
};

declare function fc:getFTPConnectionList($ftpConnnectionsDocumentUrl as xs:string) as element(ftp:ftpProperties)* 
 {
	for $ftpProperties in doc($ftpConnnectionsDocumentUrl)/ftp:ftpConnections/ftp:ftpProperties
	return $ftpProperties
};

declare function fc:searchFTPConnection($ftpConnnectionsDocumentUrl as xs:string, $ftpConnection as element(ftp:ftpProperties)) as element(ftp:ftpProperties)* 
{
	for $ftpProperty in doc($ftpConnnectionsDocumentUrl)/ftp:ftpConnections/ftp:ftpProperties
	return if ((fn:lower-case($ftpProperty/ftp:connection/com:connName)=fn:lower-case($ftpConnection/ftp:connection/com:connName) or data($ftpConnection/ftp:connection/com:connName) = ""))
		then $ftpProperty
		else  ( )
};

declare function fc:deleteFTPConnection($ftpConnnectionsDocumentUrl as xs:string, $ftpConnection as element(ftp:ftpProperties))
{
	for $ftpProperty in doc($ftpConnnectionsDocumentUrl)/ftp:ftpConnections/ftp:ftpProperties
	where $ftpProperty/@id = $ftpConnection/@id
	return update delete $ftpProperty
};

declare function fc:checkFTPConnectionName($ftpConnnectionsDocumentUrl as xs:string, $ftpConnection as element(ftp:ftpProperties)) as element(ftp:ftpConnections)* 
{
	for $ftpProperty in doc($ftpConnnectionsDocumentUrl)/ftp:ftpConnections/ftp:ftpProperties
	return if ((fn:lower-case($ftpConnection/ftp:connection/com:connName)=fn:lower-case($ftpProperty/ftp:connection/com:connName)) 
			and ($ftpProperty/@id != $ftpConnection/@id))
		then $ftpProperty
		else  ( )
};

(: //TODO id ? :)
declare function fc:insertFTPConnection($ftpConnnectionsDocumentUrl as xs:string, $ftpConnection as element(ftp:ftpProperties)) as xs:boolean?
{	
	let $insert := update insert $ftpConnection into doc($ftpConnnectionsDocumentUrl)/ftp:ftpConnections
	return true()
} ;

declare function fc:updateFTPConnection($ftpConnnectionsDocumentUrl as xs:string, $ftpConnection as element(ftp:ftpProperties))
{
	for $ftpDef in doc($ftpConnnectionsDocumentUrl)/ftp:ftpConnections/ftp:ftpProperties
	where $ftpDef/@id = $ftpConnection/@id
	return  update replace $ftpDef with $ftpConnection
};

declare function fc:updateFTPConnectionLock($ftpConnnectionsDocumentUrl as xs:string, $ftpConnection as element(ftp:ftpProperties))
{
   util:exclusive-lock(doc($ftpConnnectionsDocumentUrl)/ftp:ftpConnections, fc:updateFTPConnection($ftpConnnectionsDocumentUrl, $ftpConnection))     
};
