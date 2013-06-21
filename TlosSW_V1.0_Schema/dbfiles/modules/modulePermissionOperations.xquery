xquery version "1.0";

module namespace hs = "http://hs.tlos.com/";

declare namespace per = "http://www.likyateknoloji.com/XML_permission_types";
(:
Mapping
$permissionsDocumentUrl = doc('xmldb:exist:///db/TLOSSW/xmls/tlosSWPermission10.xml')
:)

declare function hs:getPermisions($permissionsDocumentUrl as xs:string)
{
	for $permission in doc(permissionsDocumentUrl)/per:permissions/per:permission 
	return $permission
};

declare function hs:updatePermissionsLock($permissionsDocumentUrl as xs:string, $permissions as element(per:permissions))
 {
	util:exclusive-lock(doc(permissionsDocumentUrl)/per:permissions, hs:updatePermissions($permissions))
};

declare function hs:updatePermissions($permissionsDocumentUrl as xs:string, $permissions as element(per:permissions))
 {
	for $i in 1 to count($permissions/per:permission)
	return	hs:updatePermission($permissionsDocumentUrl, $permissions/per:permission[$i])
};

declare function hs:updatePermission($permissionsDocumentUrl as xs:string, $permission as element(per:permission))
 {
		for $per in doc($permissionsDocumentUrl)/per:permissions/per:permission
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

