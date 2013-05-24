xquery version "1.0";
module namespace wso = "http://wso.tlos.com/";
declare namespace ws = "http://www.likyateknoloji.com/XML_web_service_types";
declare namespace com = "http://www.likyateknoloji.com/XML_common_types";
declare namespace usr = "http://www.likyateknoloji.com/XML_user_types";

declare function wso:getWSAccessProfile($id as xs:integer) as element(ws:userAccessProfile)?
 {
	for $userProfile in doc("//db/TLOSSW/xmls/tlosSWWebServiceAccessProfiles10.xml")/ws:userAccessProfiles/ws:userAccessProfile
    where $userProfile/@ID = $id
    return  $userProfile

};

declare function wso:insertWSDefinition($wsDefinition as element(ws:webServiceDefinition))
{	
	update insert $wsDefinition into doc("xmldb:exist:///db/TLOSSW/xmls/tlosSWSJWebServices10.xml")/ws:webServiceList
} ;

declare function wso:getWSDefinitionList() as element(ws:webServiceDefinition)* 
 {
	for $wsDefinition in doc("//db/TLOSSW/xmls/tlosSWSJWebServices10.xml")/ws:webServiceList/ws:webServiceDefinition
	return $wsDefinition
};

declare function wso:searchWSAccessProfiles($profile as element(ws:userAccessProfile)) as element(ws:userAccessProfile)* 
{
	for $userProfile in doc("//db/TLOSSW/xmls/tlosSWWebServiceAccessProfiles10.xml")/ws:userAccessProfiles/ws:userAccessProfile
	return if ((fn:contains(fn:lower-case($userProfile/ws:allowedUsers), fn:lower-case($profile/ws:allowedUsers/com:userId)) or data($profile/ws:allowedUsers) = "")
			and	(fn:contains(fn:lower-case($userProfile/ws:allowedRoles), fn:lower-case($profile/ws:allowedRoles/com:role)) or data($profile/ws:allowedRoles) = "")
			and	(fn:lower-case($userProfile/com:active)=fn:lower-case($profile/com:active) or data($profile/com:active) = ""))
		then $userProfile
		else  ( )
};

declare function wso:insertWSAccessProfile($profile as element(ws:userAccessProfile))
{	
	update insert $profile into doc("xmldb:exist:///db/TLOSSW/xmls/tlosSWWebServiceAccessProfiles10.xml")/ws:userAccessProfiles
};

declare function wso:deleteWSAccessProfile($profile as element(ws:userAccessProfile))
{	
	for $userProfile in doc("//db/TLOSSW/xmls/tlosSWWebServiceAccessProfiles10.xml")/ws:userAccessProfiles/ws:userAccessProfile
	where $userProfile/@ID = $profile/@ID
	return update delete $userProfile
};

declare function wso:updateWSAccessProfile($profile as element(ws:userAccessProfile))
{
	for $userProfile in doc("//db/TLOSSW/xmls/tlosSWWebServiceAccessProfiles10.xml")/ws:userAccessProfiles/ws:userAccessProfile
	where $userProfile/@ID = $profile/@ID
	return  update replace $userProfile with $profile
};

declare function wso:updateWSAccessProfileLock($profile as element(ws:userAccessProfile))
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWWebServiceAccessProfiles10.xml")/ws:userAccessProfiles, wso:updateWSAccessProfile($profile))     
};

(: login olan kullanicinin yetkili oldugu web servisleri donuyor :)
declare function wso:getWSDefinitionListForActiveUser($userId as xs:int) as element(ws:webServiceDefinition)* 
 {
 	let $profileDoc := doc("//db/TLOSSW/xmls/tlosSWWebServiceAccessProfiles10.xml")
 	
 	let $role := for $user in doc("//db/TLOSSW/xmls/tlosSWUser10.xml")/usr:user-infos/usr:userList/usr:person
				where $user/@id = $userId
				return $user/com:role
				
	for $wsDefinition in doc("//db/TLOSSW/xmls/tlosSWSJWebServices10.xml")/ws:webServiceList/ws:webServiceDefinition
	return let $allowedService := for $userProfile in $profileDoc/ws:userAccessProfiles/ws:userAccessProfile
									where (($userProfile/ws:webServiceID = $wsDefinition/@ID) and 
										(fn:contains($userProfile/ws:allowedUsers, $userId) or fn:contains($userProfile/ws:allowedRoles, $role) or $role = 'ADMIN') )
									return $wsDefinition
	return $allowedService
};



