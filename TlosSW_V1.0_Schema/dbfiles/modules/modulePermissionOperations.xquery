xquery version "1.0";

module namespace hs = "http://hs.tlos.com/";

declare namespace per = "http://www.likyateknoloji.com/XML_permission_types";
declare namespace com = "http://www.likyateknoloji.com/XML_common_types";
declare namespace out="http://www.likyateknoloji.com/XML_userOutput_types";

declare function hs:getPermisions()
{
	for $permission in doc('xmldb:exist:///db/TLOSSW/xmls/tlosSWPermission10.xml')/per:permissions/per:permission 
	return $permission
};

declare function hs:updatePermissionsLock($permissions as element(per:permissions))
 {
	util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWPermission10.xml")/per:permissions, hs:updatePermissions($permissions))
};

declare function hs:updatePermissions($permissions as element(per:permissions))
 {
	for $i in 1 to count($permissions/per:permission)
	return	hs:updatePermission($permissions/per:permission[$i])
};

declare function hs:updatePermission($permission as element(per:permission))
 {
		for $per in doc('xmldb:exist:///db/TLOSSW/xmls/tlosSWPermission10.xml')/per:permissions/per:permission
		where $per/@id = $permission/@id
		return  update replace $per with 
			<per:permission id="{data($permission/@id)}">
				<per:roles>
					{
						for $j in 1 to count($permission/per:roles/per:role)
						return
							<per:role>{data($permission/per:roles/per:role[$j])}</per:role>						
					}	
				</per:roles>
				<per:resource type="{data($permission/per:resource/@type)}">{data($permission/per:resource)}</per:resource>
			</per:permission> 	
	
};

