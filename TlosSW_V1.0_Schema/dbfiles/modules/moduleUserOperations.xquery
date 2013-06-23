xquery version "1.0";

module namespace hs = "http://hs.tlos.com/";

declare namespace usr = "http://www.likyateknoloji.com/XML_user_types";
declare namespace com = "http://www.likyateknoloji.com/XML_common_types";

(:
Mapping
$userDocumentUrl = doc("//db/TLOSSW/xmls/tlosSWUser10.xml")
:)

(:fn:empty($prs/com:role):)
declare function hs:searchUser($userDocumentUrl as xs:string, $prs as element(usr:person)) as element(usr:person)* 
 {
	for $user in doc($userDocumentUrl)/usr:user-infos/usr:userList/usr:person
	return if ((fn:lower-case($user/com:name)=fn:lower-case($prs/com:name) or data($prs/com:name) = "")  
			and	(fn:lower-case($user/com:surname)=fn:lower-case($prs/com:surname) or data($prs/com:surname) = "")
			and	(fn:lower-case($user/com:role)=fn:lower-case($prs/com:role) or data($prs/com:role) = "" )
			and	(fn:lower-case($user/com:userName)=fn:lower-case($prs/com:userName) or data($prs/com:userName) = ""))
		then $user
		else  ( )
};

declare function hs:users($userDocumentUrl as xs:string) as element(usr:person)* 
 {
	for $user in doc($userDocumentUrl)/usr:user-infos/usr:userList/usr:person
	return  $user
};

declare function hs:searchUserByUsername($userDocumentUrl as xs:string, $username as xs:string) as element(usr:person)? 
 {
	for $user in doc($userDocumentUrl)/usr:user-infos/usr:userList/usr:person
	where $user/com:userName = $username
	return $user
};

declare function hs:searchUserByUserId($userDocumentUrl as xs:string, $id as xs:integer) as element(usr:person)? 
 {
	for $user in doc($userDocumentUrl)/usr:user-infos/usr:userList/usr:person
	where $user/@id = $id
	return $user
};

declare function hs:getSubscribers($userDocumentUrl as xs:string, $id as xs:integer, $role as xs:string) as element(usr:person)* 
 {
	for $user in doc($userDocumentUrl)/usr:user-infos/usr:userList/usr:person
	where $user/@id = $id or $user/com:role = $role
	return $user
};

declare function hs:insertUser($userDocumentUrl as xs:string, $prs as element(usr:person))
{	
	update insert $prs into doc($userDocumentUrl)/usr:user-infos/usr:userList
} ;

declare function hs:insertUserLock($userDocumentUrl as xs:string, $prs as element(usr:person))
{
   util:exclusive-lock(doc($userDocumentUrl)/usr:user-infos/usr:userList, hs:insertUser($userDocumentUrl, $prs))     
};

declare function hs:updateUser($userDocumentUrl as xs:string, $prs as element(usr:person))
{
	for $user in doc($userDocumentUrl)/usr:user-infos/usr:userList/usr:person
	where $user/@id = $prs/@id
	return  update replace $user with $prs
};

declare function hs:updateUserLock($userDocumentUrl as xs:string, $prs as element(usr:person))
{
   util:exclusive-lock(doc($userDocumentUrl)/usr:user-infos/usr:userList, hs:updateUser($userDocumentUrl, $prs))     
};

declare function hs:deleteUser($userDocumentUrl as xs:string, $prs as element(usr:person))
 {
	for $user in doc($userDocumentUrl)/usr:user-infos/usr:userList/usr:person
	where $user/@id = $prs/@id
	return  update delete $user
};

declare function hs:deleteUserLock($userDocumentUrl as xs:string, $prs as element(usr:person))
{
   util:exclusive-lock(doc($userDocumentUrl)/usr:user-infos/usr:userList, hs:deleteUser($userDocumentUrl, $prs))     
};
