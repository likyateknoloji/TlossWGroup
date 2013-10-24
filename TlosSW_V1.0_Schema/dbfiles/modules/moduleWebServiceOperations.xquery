xquery version "1.0";

module namespace wso = "http://wso.tlos.com/";

declare namespace ws = "http://www.likyateknoloji.com/XML_web_service_types";
declare namespace com = "http://www.likyateknoloji.com/XML_common_types";
declare namespace usr = "http://www.likyateknoloji.com/XML_user_types";

import module namespace met = "http://meta.tlos.com/" at "moduleMetaDataOperations.xquery";

(:
Mappings
$wsAccessProfilesDocumentUrl = doc("//db/TLOSSW/xmls/tlosSWWebServiceAccessProfiles10.xml")
$sjWebServicesDocumentUrl = doc("xmldb:exist:///db/TLOSSW/xmls/tlosSWSJWebServices10.xml")
$userDocumentUrl = doc("//db/TLOSSW/xmls/tlosSWUser10.xml")
:)

declare function wso:getWSAccessProfile($documentUrl as xs:string, $id as xs:integer) as element(ws:userAccessProfile)?
 {
    let $wsAccessProfilesDocumentUrl := met:getMetaData($documentUrl, "wsAccessProfiles")
	
	for $userProfile in doc($wsAccessProfilesDocumentUrl)/ws:userAccessProfiles/ws:userAccessProfile
    where $userProfile/@ID = $id
    return  $userProfile

};

declare function wso:insertWSDefinition($documentUrl as xs:string, $wsDefinition as element(ws:webServiceDefinition))
{	
    let $sjWebServicesDocumentUrl := met:getMetaData($documentUrl, "jobWebServices")
	
	return update insert $wsDefinition into doc($sjWebServicesDocumentUrl)/ws:webServiceList
} ;

declare function wso:getWSDefinitionList($documentUrl as xs:string) as element(ws:webServiceDefinition)* 
 {
    let $sjWebServicesDocumentUrl := met:getMetaData($documentUrl, "jobWebServices")
	
	for $wsDefinition in doc($sjWebServicesDocumentUrl)/ws:webServiceList/ws:webServiceDefinition
	return $wsDefinition
};

declare function wso:searchWSAccessProfiles($documentUrl as xs:string, $profile as element(ws:userAccessProfile)) as element(ws:userAccessProfile)* 
{
    let $wsAccessProfilesDocumentUrl := met:getMetaData($documentUrl, "wsAccessProfiles")
	
	for $userProfile in doc($wsAccessProfilesDocumentUrl)/ws:userAccessProfiles/ws:userAccessProfile
	return if ((fn:contains(fn:lower-case($userProfile/ws:allowedUsers), fn:lower-case($profile/ws:allowedUsers/com:userId)) or data($profile/ws:allowedUsers) = "")
			and	(fn:contains(fn:lower-case($userProfile/ws:allowedRoles), fn:lower-case($profile/ws:allowedRoles/com:role)) or data($profile/ws:allowedRoles) = "")
			and	(fn:lower-case($userProfile/com:active)=fn:lower-case($profile/com:active) or data($profile/com:active) = ""))
		then $userProfile
		else  ( )
};

declare function wso:insertWSAccessProfile($documentUrl as xs:string, $profile as element(ws:userAccessProfile))
{	
    let $wsAccessProfilesDocumentUrl := met:getMetaData($documentUrl, "wsAccessProfiles")
	
	return update insert $profile into doc($wsAccessProfilesDocumentUrl)/ws:userAccessProfiles
};

declare function wso:deleteWSAccessProfile($documentUrl as xs:string, $profile as element(ws:userAccessProfile))
{	
    let $wsAccessProfilesDocumentUrl := met:getMetaData($documentUrl, "wsAccessProfiles")

	for $userProfile in doc($wsAccessProfilesDocumentUrl)/ws:userAccessProfiles/ws:userAccessProfile
	where $userProfile/@ID = $profile/@ID
	return update delete $userProfile
};

declare function wso:updateWSAccessProfile($documentUrl as xs:string, $profile as element(ws:userAccessProfile))
{
    let $wsAccessProfilesDocumentUrl := met:getMetaData($documentUrl, "wsAccessProfiles")
	
	for $userProfile in doc($wsAccessProfilesDocumentUrl)/ws:userAccessProfiles/ws:userAccessProfile
	where $userProfile/@ID = $profile/@ID
	return  update replace $userProfile with $profile
};

declare function wso:updateWSAccessProfileLock($documentUrl as xs:string, $profile as element(ws:userAccessProfile))
{
   let $wsAccessProfilesDocumentUrl := met:getMetaData($documentUrl, "wsAccessProfiles")
   
   return util:exclusive-lock(doc($wsAccessProfilesDocumentUrl)/ws:userAccessProfiles, wso:updateWSAccessProfile($documentUrl, $profile))     
};

(: login olan kullanicinin yetkili oldugu web servisleri donuyor :)
declare function wso:getWSDefinitionListForActiveUser($documentUrl as xs:string, $userId as xs:int) as element(ws:webServiceDefinition)* 
 {
    let $wsAccessProfilesDocumentUrl := met:getMetaData($documentUrl, "wsAccessProfiles")
	let $sjWebServicesDocumentUrl := met:getMetaData($documentUrl, "jobWebServices")
	let $userDocumentUrl := met:getMetaData($documentUrl, "user")
	
 	let $profileDoc := doc($wsAccessProfilesDocumentUrl)
 	
 	let $role := for $user in doc($userDocumentUrl)/usr:user-infos/usr:userList/usr:person
				where $user/@id = $userId
				return $user/com:role
				
	for $wsDefinition in doc($sjWebServicesDocumentUrl)/ws:webServiceList/ws:webServiceDefinition
	return let $allowedService := for $userProfile in $profileDoc/ws:userAccessProfiles/ws:userAccessProfile
									where (($userProfile/ws:webServiceID = $wsDefinition/@ID) and 
										(fn:contains($userProfile/ws:allowedUsers, $userId) or fn:contains($userProfile/ws:allowedRoles, $role) or $role = 'ADMIN') )
									return $wsDefinition
	return $allowedService
};

(: login olan kullanicinin yetkili oldugu veya role ADMIN ise yetkilweb servisleri donuyor :)
declare function wso:getWSDefinitionListForAccessDef($documentUrl as xs:string, $userId as xs:int) as element(ws:webServiceDefinition)* 
 {
    let $wsAccessProfilesDocumentUrl := met:getMetaData($documentUrl, "wsAccessProfiles")
    let $sjWebServicesDocumentUrl := met:getMetaData($documentUrl, "jobWebServices")
	let $userDocumentUrl := met:getMetaData($documentUrl, "user")
	
 	let $profileDoc := doc($wsAccessProfilesDocumentUrl)
 	
 	let $role := for $user in doc($userDocumentUrl)/usr:user-infos/usr:userList/usr:person
				where $user/@id = $userId
				return $user/com:role
				
	for $wsDefinition in doc($sjWebServicesDocumentUrl)/ws:webServiceList/ws:webServiceDefinition
	where data($wsDefinition/com:userId) = $userId or $role = 'ADMIN'
    
	return $wsDefinition
};

