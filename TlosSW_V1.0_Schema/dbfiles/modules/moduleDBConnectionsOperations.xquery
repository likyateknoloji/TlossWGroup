xquery version "1.0";
module namespace db = "http://db.tlos.com/";
declare namespace dbc = "http://www.likyateknoloji.com/XML_dbconnection_types";
declare namespace com = "http://www.likyateknoloji.com/XML_common_types";

(: READ :)
declare function db:getDbConnection($id as xs:integer) as element(dbc:dbProperties)?
{              
        let $doc := doc("//db/TLOSSW/xmls/tlosSWDBConnections10.xml") 
        for $dbConnection in $doc//dbc:dbProperties
        where $dbConnection/@ID = $id
        return $dbConnection
};

declare function db:getDbCP($id as xs:integer) as element(dbc:dbConnectionProfile)?
 {
	for $dbConnection in doc("//db/TLOSSW/xmls/tlosSWDBConnectionProfiles10.xml")/dbc:dbConnectionProfiles/dbc:dbConnectionProfile
    where $dbConnection/@ID = $id
    return  $dbConnection

};

declare function db:getDbConnectionByType($connectionName as xs:string) as element(dbc:dbProperties)?
{              
        let $doc := doc("//db/TLOSSW/xmls/tlosSWDBConnections10.xml") 
        for $dbConnection in $doc//dbc:dbProperties
        where $dbConnection/dbc:connectionName = $connectionName
        return $dbConnection
};

declare function db:getDbConnectionAll()
{              
        let $doc := doc("//db/TLOSSW/xmls/tlosSWDBConnections10.xml") 
        for $dbConnection in $doc//dbc:dbProperties
        return $dbConnection
};

declare function db:searchDbConnection($dbConnection as element(dbc:dbProperties)) as element(dbc:dbProperties)* 
{
	for $dbProperty in doc("//db/TLOSSW/xmls/tlosSWDBConnections10.xml")/dbc:dbList/dbc:dbProperties
	return if ((fn:lower-case($dbProperty/dbc:dbType)=fn:lower-case($dbConnection/dbc:dbType) or data($dbConnection/dbc:dbType) = "")  
			and	(fn:lower-case($dbProperty/dbc:hostName)=fn:lower-case($dbConnection/dbc:hostName) or data($dbConnection/dbc:hostName) = ""))
		then $dbProperty
		else  ( )
};

declare function db:searchDbAccessProfile($dbConnProfile as element(dbc:dbConnectionProfile)) as element(dbc:dbConnectionProfile)* 
{
	for $dbProperty in doc("//db/TLOSSW/xmls/tlosSWDBConnectionProfiles10.xml")/dbc:dbConnectionProfiles/dbc:dbConnectionProfile
	return if (($dbProperty/dbc:dbDefinitionId=$dbConnProfile/dbc:dbDefinitionId or data($dbConnProfile/dbc:dbDefinitionId) = "")  
			and	(fn:lower-case($dbProperty/com:userName)=fn:lower-case($dbConnProfile/com:userName) or data($dbConnProfile/com:userName) = "")
			and	(fn:lower-case($dbProperty/dbc:deployed)=fn:lower-case($dbConnProfile/dbc:deployed) or data($dbConnProfile/dbc:deployed) = "" )
			and	(fn:lower-case($dbProperty/dbc:active)=fn:lower-case($dbConnProfile/dbc:active) or data($dbConnProfile/dbc:active) = ""))
		then $dbProperty
		else  ( )
};

declare function db:checkDbConnectionName($dbConnection as element(dbc:dbProperties)) as element(dbc:dbProperties)* 
{
	for $dbProperty in doc("//db/TLOSSW/xmls/tlosSWDBConnections10.xml")/dbc:dbList/dbc:dbProperties
	return if ((fn:lower-case($dbConnection/dbc:connectionName)=fn:lower-case($dbProperty/dbc:connectionName)) 
			and ($dbProperty/@ID != $dbConnection/@ID))
		then $dbProperty
		else  ( )
};


declare function db:deleteDbConnection($dbConnection as element(dbc:dbProperties))
{
	for $dbProperty in doc("//db/TLOSSW/xmls/tlosSWDBConnections10.xml")/dbc:dbList/dbc:dbProperties
	where $dbProperty/@ID = $dbConnection/@ID
	return update delete $dbProperty
};

declare function db:deleteDbAccessProfile($dbConnProfile as element(dbc:dbConnectionProfile))
{
	for $dbProperty in doc("//db/TLOSSW/xmls/tlosSWDBConnectionProfiles10.xml")/dbc:dbConnectionProfiles/dbc:dbConnectionProfile
	where $dbProperty/@ID = $dbConnProfile/@ID
	return update delete $dbProperty
};

declare function db:insertDbConnection($dbConnection as element(dbc:dbProperties))
{	
	update insert $dbConnection into doc("xmldb:exist:///db/TLOSSW/xmls/tlosSWDBConnections10.xml")/dbc:dbList
} ;

declare function db:insertDbAccessProfile($dbConnProfile as element(dbc:dbConnectionProfile))
{	
	update insert $dbConnProfile into doc("xmldb:exist:///db/TLOSSW/xmls/tlosSWDBConnectionProfiles10.xml")/dbc:dbConnectionProfiles
} ;

declare function db:updateDbConnection($dbConnection as element(dbc:dbProperties))
{
	for $dbDef in doc("//db/TLOSSW/xmls/tlosSWDBConnections10.xml")/dbc:dbList/dbc:dbProperties
	where $dbDef/@ID = $dbConnection/@ID
	return  update replace $dbDef with $dbConnection
};

declare function db:updateDbConnectionLock($dbConnection as element(dbc:dbProperties))
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWDBConnections10.xml")/dbc:dbList, db:updateDbConnection($dbConnection))     
};

declare function db:updateDbAccessProfile($dbConnProfile as element(dbc:dbConnectionProfile))
{
	for $dbDef in doc("//db/TLOSSW/xmls/tlosSWDBConnectionProfiles10.xml")/dbc:dbConnectionProfiles/dbc:dbConnectionProfile
	where $dbDef/@ID = $dbConnProfile/@ID
	return  update replace $dbDef with $dbConnProfile
};

declare function db:updateDbAccessProfileLock($dbConnProfile as element(dbc:dbConnectionProfile))
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWDBConnectionProfiles10.xml")/dbc:dbConnectionProfiles, db:updateDbAccessProfile($dbConnProfile))     
};

declare function db:getDbProfileAll()
{              
    for $dbConnProfile in doc("//db/TLOSSW/xmls/tlosSWDBConnectionProfiles10.xml")/dbc:dbConnectionProfiles/dbc:dbConnectionProfile
	return $dbConnProfile
};
