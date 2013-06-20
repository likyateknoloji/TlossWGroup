xquery version "1.0";

module namespace db = "http://db.tlos.com/";

declare namespace dbc = "http://www.likyateknoloji.com/XML_dbconnection_types";
declare namespace com = "http://www.likyateknoloji.com/XML_common_types";

(: Mappings
$dbConnectionsDocumentUrl = doc("//db/TLOSSW/xmls/tlosSWDBConnections10.xml") 
$dbProfilesDocumentUrl = doc("//db/TLOSSW/xmls/tlosSWDBConnectionProfiles10.xml")
:)

(: READ :)
declare function db:getDbConnection($dbConnectionsDocumentUrl as xs:string, $id as xs:integer) as element(dbc:dbProperties)?
{              
        let $doc := doc($dbConnectionsDocumentUrl) 
        for $dbConnection in $doc//dbc:dbProperties
        where $dbConnection/@ID = $id
        return $dbConnection
};

declare function db:getDbCP($dbProfilesDocumentUrl as xs:string, $id as xs:integer) as element(dbc:dbConnectionProfile)?
 {
	for $dbConnection in doc($dbProfilesDocumentUrl)/dbc:dbConnectionProfiles/dbc:dbConnectionProfile
    where $dbConnection/@ID = $id
    return  $dbConnection

};

declare function db:getDbConnectionByType($dbConnectionsDocumentUrl as xs:string, $connectionName as xs:string) as element(dbc:dbProperties)?
{              
        let $doc := doc($dbConnectionsDocumentUrl) 
        for $dbConnection in $doc//dbc:dbProperties
        where $dbConnection/dbc:connectionName = $connectionName
        return $dbConnection
};

declare function db:getDbConnectionAll($dbConnectionsDocumentUrl as xs:string)
{              
        let $doc := doc($dbConnectionsDocumentUrl) 
        for $dbConnection in $doc//dbc:dbProperties
        return $dbConnection
};

declare function db:searchDbConnection($dbConnectionsDocumentUrl as xs:string, $dbConnection as element(dbc:dbProperties)) as element(dbc:dbProperties)* 
{
	for $dbProperty in doc($dbConnectionsDocumentUrl)/dbc:dbList/dbc:dbProperties
	return if ((fn:lower-case($dbProperty/dbc:dbType)=fn:lower-case($dbConnection/dbc:dbType) or data($dbConnection/dbc:dbType) = "")  
			and	(fn:lower-case($dbProperty/dbc:hostName)=fn:lower-case($dbConnection/dbc:hostName) or data($dbConnection/dbc:hostName) = ""))
		then $dbProperty
		else  ( )
};

declare function db:searchDbAccessProfile($dbProfilesDocumentUrl as xs:string, $dbConnProfile as element(dbc:dbConnectionProfile)) as element(dbc:dbConnectionProfile)* 
{
	for $dbProperty in doc($dbProfilesDocumentUrl)/dbc:dbConnectionProfiles/dbc:dbConnectionProfile
	return if (($dbProperty/dbc:dbDefinitionId=$dbConnProfile/dbc:dbDefinitionId or data($dbConnProfile/dbc:dbDefinitionId) = "")  
			and	(fn:lower-case($dbProperty/com:userName)=fn:lower-case($dbConnProfile/com:userName) or data($dbConnProfile/com:userName) = "")
			and	(fn:lower-case($dbProperty/dbc:deployed)=fn:lower-case($dbConnProfile/dbc:deployed) or data($dbConnProfile/dbc:deployed) = "" )
			and	(fn:lower-case($dbProperty/dbc:active)=fn:lower-case($dbConnProfile/dbc:active) or data($dbConnProfile/dbc:active) = ""))
		then $dbProperty
		else  ( )
};

declare function db:checkDbConnectionName($dbConnectionsDocumentUrl as xs:string, $dbConnection as element(dbc:dbProperties)) as element(dbc:dbProperties)* 
{
	for $dbProperty in doc($dbConnectionsDocumentUrl)/dbc:dbList/dbc:dbProperties
	return if ((fn:lower-case($dbConnection/dbc:connectionName)=fn:lower-case($dbProperty/dbc:connectionName)) 
			and ($dbProperty/@ID != $dbConnection/@ID))
		then $dbProperty
		else  ( )
};


declare function db:deleteDbConnection($dbConnectionsDocumentUrl as xs:string, $dbConnection as element(dbc:dbProperties))
{
	for $dbProperty in doc($dbConnectionsDocumentUrl)/dbc:dbList/dbc:dbProperties
	where $dbProperty/@ID = $dbConnection/@ID
	return update delete $dbProperty
};

declare function db:deleteDbAccessProfile($dbProfilesDocumentUrl as xs:string, $dbConnProfile as element(dbc:dbConnectionProfile))
{
	for $dbProperty in doc($dbProfilesDocumentUrl)/dbc:dbConnectionProfiles/dbc:dbConnectionProfile
	where $dbProperty/@ID = $dbConnProfile/@ID
	return update delete $dbProperty
};

declare function db:insertDbConnection($dbConnectionsDocumentUrl as xs:string, $dbConnection as element(dbc:dbProperties))
{	
	update insert $dbConnection into doc($dbConnectionsDocumentUrl)/dbc:dbList
} ;

declare function db:insertDbAccessProfile($dbProfilesDocumentUrl as xs:string, $dbConnProfile as element(dbc:dbConnectionProfile))
{	
	update insert $dbConnProfile into doc($dbProfilesDocumentUrl)/dbc:dbConnectionProfiles
} ;

declare function db:updateDbConnection($dbConnectionsDocumentUrl as xs:string, $dbConnection as element(dbc:dbProperties))
{
	for $dbDef in doc($dbConnectionsDocumentUrl)/dbc:dbList/dbc:dbProperties
	where $dbDef/@ID = $dbConnection/@ID
	return  update replace $dbDef with $dbConnection
};

declare function db:updateDbConnectionLock($dbConnectionsDocumentUrl as xs:string, $dbConnection as element(dbc:dbProperties))
{
   util:exclusive-lock(doc($dbConnectionsDocumentUrl)/dbc:dbList, db:updateDbConnection($dbConnection))     
};

declare function db:updateDbAccessProfile($dbProfilesDocumentUrl as xs:string, $dbConnProfile as element(dbc:dbConnectionProfile))
{
	for $dbDef in doc($dbProfilesDocumentUrl)/dbc:dbConnectionProfiles/dbc:dbConnectionProfile
	where $dbDef/@ID = $dbConnProfile/@ID
	return  update replace $dbDef with $dbConnProfile
};

declare function db:updateDbAccessProfileLock($dbProfilesDocumentUrl as xs:string, $dbConnProfile as element(dbc:dbConnectionProfile))
{
   util:exclusive-lock(doc($dbProfilesDocumentUrl)/dbc:dbConnectionProfiles, db:updateDbAccessProfile($dbConnProfile))     
};

declare function db:getDbProfileAll()
{              
    for $dbConnProfile in doc($dbProfilesDocumentUrl)/dbc:dbConnectionProfiles/dbc:dbConnectionProfile
	return $dbConnProfile
};
