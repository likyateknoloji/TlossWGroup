xquery version "1.0";
module namespace hs = "http://hs.tlos.com/";
declare namespace usr = "http://www.likyateknoloji.com/XML_user_types";
declare namespace per = "http://www.likyateknoloji.com/XML_permission_types";
declare namespace com = "http://www.likyateknoloji.com/XML_common_types";
declare namespace out="http://www.likyateknoloji.com/XML_userOutput_types";

(:fn:empty($prs/com:role):)
declare function hs:searchUser($prs as element(usr:person)) as element(usr:person)* 
 {
	for $user in doc("//db/TLOSSW/xmls/tlosSWUser10.xml")/usr:user-infos/usr:userList/usr:person
	return if ((fn:lower-case($user/com:name)=fn:lower-case($prs/com:name) or data($prs/com:name) = "")  
			and	(fn:lower-case($user/com:surname)=fn:lower-case($prs/com:surname) or data($prs/com:surname) = "")
			and	(fn:lower-case($user/com:role)=fn:lower-case($prs/com:role) or data($prs/com:role) = "" )
			and	(fn:lower-case($user/com:userName)=fn:lower-case($prs/com:userName) or data($prs/com:userName) = ""))
		then $user
		else  ( )
};

declare function hs:users() as element(usr:person)* 
 {
	for $user in doc("//db/TLOSSW/xmls/tlosSWUser10.xml")/usr:user-infos/usr:userList/usr:person
	return  $user
};

declare function hs:searchUserByUsername($username as xs:string) as element(usr:person)? 
 {
	for $user in doc("//db/TLOSSW/xmls/tlosSWUser10.xml")/usr:user-infos/usr:userList/usr:person
	where $user/com:userName = $username
	return $user
};

declare function hs:searchUserByUserId($id as xs:integer) as element(usr:person)? 
 {
	for $user in doc("//db/TLOSSW/xmls/tlosSWUser10.xml")/usr:user-infos/usr:userList/usr:person
	where $user/@id = $id
	return $user
};

declare function hs:getSubscribers($id as xs:integer, $role as xs:string) as element(usr:person)* 
 {
	for $user in doc("//db/TLOSSW/xmls/tlosSWUser10.xml")/usr:user-infos/usr:userList/usr:person
	where $user/@id = $id or $user/com:role = $role
	return $user
};

declare function hs:insertUser($prs as element(usr:person))
{	
	update insert $prs into doc("xmldb:exist:///db/TLOSSW/xmls/tlosSWUser10.xml")/usr:user-infos/usr:userList
} ;

declare function hs:insertUserLock($prs as element(usr:person))
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWUser10.xml")/usr:user-infos/usr:userList, hs:insertUser($prs))     
};

declare function hs:updateUser($prs as element(usr:person))
{
	for $user in doc("//db/TLOSSW/xmls/tlosSWUser10.xml")/usr:user-infos/usr:userList/usr:person
	where $user/@id = $prs/@id
	return  update replace $user with $prs
};

declare function hs:updateUserLock($prs as element(usr:person))
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWUser10.xml")/usr:user-infos/usr:userList, hs:updateUser($prs))     
};

declare function hs:deleteUser($prs as element(usr:person))
 {
	for $user in doc("//db/TLOSSW/xmls/tlosSWUser10.xml")/usr:user-infos/usr:userList/usr:person
	where $user/@id = $prs/@id
	return  update delete $user
};

declare function hs:deleteUserLock($prs as element(usr:person))
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWUser10.xml")/usr:user-infos/usr:userList, hs:deleteUser($prs))     
};
