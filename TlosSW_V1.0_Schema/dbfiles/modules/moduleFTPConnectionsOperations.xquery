xquery version "1.0";
module namespace fc = "http://fc.tlos.com/";
declare namespace ftp = "http://www.likyateknoloji.com/XML_ftp_adapter_types";
declare namespace com = "http://www.likyateknoloji.com/XML_common_types";

declare function fc:searchFTPConnectionById($id as xs:integer) as element(ftp:ftpProperties)? 
 {
	for $ftpProperties in doc("//db/TLOSSW/xmls/tlosSWSJFTPConnections10.xml")/ftp:ftpConnections/ftp:ftpProperties
	where $ftpProperties/@id = $id
	return $ftpProperties
};

declare function fc:getFTPConnectionList() as element(ftp:ftpProperties)* 
 {
	for $ftpProperties in doc("//db/TLOSSW/xmls/tlosSWSJFTPConnections10.xml")/ftp:ftpConnections/ftp:ftpProperties
	return $ftpProperties
};

declare function fc:searchFTPConnection($ftpConnection as element(ftp:ftpProperties)) as element(ftp:ftpProperties)* 
{
	for $ftpProperty in doc("//db/TLOSSW/xmls/tlosSWSJFTPConnections10.xml")/ftp:ftpConnections/ftp:ftpProperties
	return if ((fn:lower-case($ftpProperty/ftp:connection/com:connName)=fn:lower-case($ftpConnection/ftp:connection/com:connName) or data($ftpConnection/ftp:connection/com:connName) = ""))
		then $ftpProperty
		else  ( )
};

declare function fc:deleteFTPConnection($ftpConnection as element(ftp:ftpProperties))
{
	for $ftpProperty in doc("//db/TLOSSW/xmls/tlosSWSJFTPConnections10.xml")/ftp:ftpConnections/ftp:ftpProperties
	where $ftpProperty/@id = $ftpConnection/@id
	return update delete $ftpProperty
};

declare function fc:checkFTPConnectionName($ftpConnection as element(ftp:ftpProperties)) as element(ftp:ftpConnections)* 
{
	for $ftpProperty in doc("//db/TLOSSW/xmls/tlosSWSJFTPConnections10.xml")/ftp:ftpConnections/ftp:ftpProperties
	return if ((fn:lower-case($ftpConnection/ftp:connection/com:connName)=fn:lower-case($ftpProperty/ftp:connection/com:connName)) 
			and ($ftpProperty/@id != $ftpConnection/@id))
		then $ftpProperty
		else  ( )
};

declare function fc:insertFTPConnection($ftpConnection as element(ftp:ftpProperties))
{	
	update insert $ftpConnection into doc("xmldb:exist:///db/TLOSSW/xmls/tlosSWSJFTPConnections10.xml")/ftp:ftpConnections
} ;

declare function fc:updateFTPConnection($ftpConnection as element(ftp:ftpProperties))
{
	for $ftpDef in doc("//db/TLOSSW/xmls/tlosSWSJFTPConnections10.xml")/ftp:ftpConnections/ftp:ftpProperties
	where $ftpDef/@id = $ftpConnection/@id
	return  update replace $ftpDef with $ftpConnection
};

declare function fc:updateFTPConnectionLock($ftpConnection as element(ftp:ftpProperties))
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWSJFTPConnections10.xml")/ftp:ftpConnections, fc:updateFTPConnection($ftpConnection))     
};
