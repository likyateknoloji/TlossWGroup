xquery version "1.0";

module namespace db = "http://db.tlos.com/";

import module namespace met = "http://meta.tlos.com/" at "moduleMetaDataOperations.xquery";

declare namespace dbc = "http://www.likyateknoloji.com/XML_dbconnection_types";
declare namespace com = "http://www.likyateknoloji.com/XML_common_types";

(: Mappings
$dbConnectionsDocumentUrl = doc("//db/TLOSSW/xmls/tlosSWDBConnections10.xml") 
$dbProfilesDocumentUrl = doc("//db/TLOSSW/xmls/tlosSWDBConnectionProfiles10.xml")
:)

(: READ :)
declare function db:getDbConnection($documentUrl as xs:string, $id as xs:integer) as element(dbc:dbProperties)?
{    
    let $dbConnectionsDocumentUrl := met:getMetaData($documentUrl, "dbConnections")
	
    let $doc := doc($dbConnectionsDocumentUrl) 
    for $dbConnection in $doc//dbc:dbProperties
    where $dbConnection/@ID = $id
    return $dbConnection
};

declare function db:getDbCP($documentUrl as xs:string, $id as xs:integer) as element(dbc:dbConnectionProfile)?
{
    let $dbProfilesDocumentUrl := met:getMetaData($documentUrl, "dbConnectionProfiles")
 
	for $dbConnection in doc($dbProfilesDocumentUrl)/dbc:dbConnectionProfiles/dbc:dbConnectionProfile
    where $dbConnection/@ID = $id
    return  $dbConnection

};

declare function db:getDbCPfromDefId($documentUrl as xs:string, $id as xs:integer) as element(dbc:dbConnectionProfile)?
{
    let $dbProfilesDocumentUrl := met:getMetaData($documentUrl, "dbConnectionProfiles")
 
	for $dbConnection in doc($dbProfilesDocumentUrl)/dbc:dbConnectionProfiles/dbc:dbConnectionProfile
    where $dbConnection/dbc:dbDefinitionId = $id
    return  $dbConnection

};

declare function db:getDbConnectionByType($documentUrl as xs:string, $connectionName as xs:string) as element(dbc:dbProperties)?
{
    let $dbConnectionsDocumentUrl := met:getMetaData($documentUrl, "dbConnections")
	
    let $doc := doc($dbConnectionsDocumentUrl) 
    for $dbConnection in $doc//dbc:dbProperties
    where $dbConnection/dbc:connectionName = $connectionName
    return $dbConnection
};

declare function db:getDbConnectionAll($documentUrl as xs:string)
{
    let $dbConnectionsDocumentUrl := met:getMetaData($documentUrl, "dbConnections")
	
    let $doc := doc($dbConnectionsDocumentUrl) 
    for $dbConnection in $doc//dbc:dbProperties
    return $dbConnection
};

declare function db:searchDbConnection($documentUrl as xs:string, $dbConnection as element(dbc:dbProperties)) as element(dbc:dbProperties)* 
{
    let $dbConnectionsDocumentUrl := met:getMetaData($documentUrl, "dbConnections")
	
	for $dbProperty in doc($dbConnectionsDocumentUrl)/dbc:dbList/dbc:dbProperties
	return if ((fn:lower-case($dbProperty/dbc:dbType)=fn:lower-case($dbConnection/dbc:dbType) or data($dbConnection/dbc:dbType) = "")  
			and	(fn:lower-case($dbProperty/dbc:hostName)=fn:lower-case($dbConnection/dbc:hostName) or data($dbConnection/dbc:hostName) = ""))
		then $dbProperty
		else  ( )
};

declare function db:searchDbAccessProfile($documentUrl as xs:string, $dbConnProfile as element(dbc:dbConnectionProfile)) as element(dbc:dbConnectionProfile)* 
{
    let $dbProfilesDocumentUrl := met:getMetaData($documentUrl, "dbConnectionProfiles")
	
	for $dbProperty in doc($dbProfilesDocumentUrl)/dbc:dbConnectionProfiles/dbc:dbConnectionProfile
	return if (($dbProperty/dbc:dbDefinitionId=$dbConnProfile/dbc:dbDefinitionId or data($dbConnProfile/dbc:dbDefinitionId) = "")  
			and	(fn:lower-case($dbProperty/com:userName)=fn:lower-case($dbConnProfile/com:userName) or data($dbConnProfile/com:userName) = "")
			and	(fn:lower-case($dbProperty/dbc:deployed)=fn:lower-case($dbConnProfile/dbc:deployed) or data($dbConnProfile/dbc:deployed) = "" )
			and	(fn:lower-case($dbProperty/dbc:active)=fn:lower-case($dbConnProfile/dbc:active) or data($dbConnProfile/dbc:active) = ""))
		then $dbProperty
		else  ( )
};

declare function db:checkDbConnectionName($documentUrl as xs:string, $dbConnection as element(dbc:dbProperties)) as element(dbc:dbProperties)* 
{
    let $dbConnectionsDocumentUrl := met:getMetaData($documentUrl, "dbConnections")
	
	for $dbProperty in doc($dbConnectionsDocumentUrl)/dbc:dbList/dbc:dbProperties
	return if ((fn:lower-case($dbConnection/dbc:connectionName)=fn:lower-case($dbProperty/dbc:connectionName)) 
			and ($dbProperty/@ID != $dbConnection/@ID))
		then $dbProperty
		else  ( )
};


declare function db:deleteDbConnection($documentUrl as xs:string, $dbConnection as element(dbc:dbProperties))
{
    let $dbConnectionsDocumentUrl := met:getMetaData($documentUrl, "dbConnections")
	
	for $dbProperty in doc($dbConnectionsDocumentUrl)/dbc:dbList/dbc:dbProperties
	where $dbProperty/@ID = $dbConnection/@ID
	return update delete $dbProperty
};

declare function db:deleteDbAccessProfile($documentUrl as xs:string, $dbConnProfile as element(dbc:dbConnectionProfile))
{
    let $dbProfilesDocumentUrl := met:getMetaData($documentUrl, "dbConnectionProfiles")
	
	for $dbProperty in doc($dbProfilesDocumentUrl)/dbc:dbConnectionProfiles/dbc:dbConnectionProfile
	where $dbProperty/@ID = $dbConnProfile/@ID
	return update delete $dbProperty
};

declare function db:insertDbConnection($documentUrl as xs:string, $dbConnection as element(dbc:dbProperties))
{
    let $dbConnectionsDocumentUrl := met:getMetaData($documentUrl, "dbConnections")
	
	return update insert $dbConnection into doc($dbConnectionsDocumentUrl)/dbc:dbList
} ;

declare function db:insertDbAccessProfile($documentUrl as xs:string, $dbConnProfile as element(dbc:dbConnectionProfile))
{	
    let $dbProfilesDocumentUrl := met:getMetaData($documentUrl, "dbConnectionProfiles")
	
	return update insert $dbConnProfile into doc($dbProfilesDocumentUrl)/dbc:dbConnectionProfiles
} ;

declare function db:updateDbConnection($documentUrl as xs:string, $dbConnection as element(dbc:dbProperties))
{
    let $dbConnectionsDocumentUrl := met:getMetaData($documentUrl, "dbConnections")
	
	for $dbDef in doc($dbConnectionsDocumentUrl)/dbc:dbList/dbc:dbProperties
	where $dbDef/@ID = $dbConnection/@ID
	return  update replace $dbDef with $dbConnection
};

declare function db:updateDbConnectionLock($documentUrl as xs:string, $dbConnection as element(dbc:dbProperties))
{
    let $dbConnectionsDocumentUrl := met:getMetaData($documentUrl, "dbConnections")
	
    return util:exclusive-lock(doc($dbConnectionsDocumentUrl)/dbc:dbList, db:updateDbConnection($documentUrl, $dbConnection))     
};

declare function db:updateDbAccessProfile($documentUrl as xs:string, $dbConnProfile as element(dbc:dbConnectionProfile))
{
    let $dbProfilesDocumentUrl := met:getMetaData($documentUrl, "dbConnectionProfiles")
	
	for $dbDef in doc($dbProfilesDocumentUrl)/dbc:dbConnectionProfiles/dbc:dbConnectionProfile
	where $dbDef/@ID = $dbConnProfile/@ID
	return  update replace $dbDef with $dbConnProfile
};

declare function db:updateDbAccessProfileLock($documentUrl as xs:string, $dbConnProfile as element(dbc:dbConnectionProfile))
{
    let $dbProfilesDocumentUrl := met:getMetaData($documentUrl, "dbConnectionProfiles")
	
    return util:exclusive-lock(doc($dbProfilesDocumentUrl)/dbc:dbConnectionProfiles, db:updateDbAccessProfile($documentUrl, $dbConnProfile))     
};

declare function db:getDbProfileAll($documentUrl as xs:string)
{
    let $dbProfilesDocumentUrl := met:getMetaData($documentUrl, "dbConnectionProfiles")
	
    for $dbConnProfile in doc($dbProfilesDocumentUrl)/dbc:dbConnectionProfiles/dbc:dbConnectionProfile
	return $dbConnProfile
};
