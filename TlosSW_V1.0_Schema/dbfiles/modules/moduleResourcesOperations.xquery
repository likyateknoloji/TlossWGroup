xquery version "1.0";
module namespace rsc = "http://rsc.tlos.com/";

declare namespace res = "http://www.likyateknoloji.com/XML_resource_types";
declare namespace rns = "http://schemas.ogf.org/rns/2009/12/rns";
declare namespace lrns = "http://www.likyateknoloji.com/XML_SWResourceNS_types";
declare namespace wsa = "http://www.w3.org/2005/08/addressing";
declare namespace jsdl = "http://schemas.ggf.org/jsdl/2005/11/jsdl";

import module namespace sq = "http://sq.tlos.com/" at "xmldb:exist://db/TLOSSW/modules/moduleSequenceOperations.xquery";

(:fn:empty($prs/com:role):)

declare function rsc:searchResources($searchResource as element(lrns:ResourceType)) as element(lrns:ResourceList)* 
 {
   <lrns:ResourceList>
   {
   for $rsc in doc("//db/TLOSSW/xmls/tlosSWResources10.xml")/lrns:ResourceList/lrns:Resource
   let $sonuc := if ((fn:contains(fn:lower-case($rsc/@entry-name), fn:lower-case($searchResource/@entry-name)) or data($searchResource/@entry-name)="" or not(fn:exists($searchResource/@entry-name))))
                 then $rsc else ()
   return $sonuc
   }
   </lrns:ResourceList>
};

(: eskisi mart 2013 Hakan
declare function rsc:searchResources($searchResource as element(lrns:Resource)) as element(lrns:Resource)* 
 {
   for $rsc in doc("//db/TLOSSW/xmls/tlosSWResources10.xml")/lrns:ResourceList/lrns:Resource
   let $sonuc := if ((fn:contains(fn:lower-case($rsc/@entry-name), fn:lower-case($searchResource/@entry-name)) or data($searchResource/@entry-name)=""))
                 then $rsc else ()
   return $sonuc
};

:)
(: ornek kullanim rsc:resourcesList(1,2) ilk iki eleman :)
declare function rsc:resourcesList($firstElement as xs:int, $lastElement as xs:int) as element(lrns:Resource)* 
 {
   <lrns:ResourceList>
   {
	for $rsc in doc("//db/TLOSSW/xmls/tlosSWResources10.xml")/lrns:ResourceList/lrns:Resource[position() = ($firstElement to $lastElement)]
	return  $rsc
   }
   </lrns:ResourceList>
};

(: ornek kullanim rsc:searchResourcesByResourceName(xs:string('Tlos SW')) :)
declare function rsc:searchResourcesByResourceName($searchResourceName as xs:string) as element(lrns:Resource)? 
 {
   <lrns:ResourceList>
   {
	for $rsc in doc("//db/TLOSSW/xmls/tlosSWResources10.xml")/lrns:ResourceList/lrns:Resource
	where fn:lower-case($rsc/@entry-name)=fn:lower-case($searchResourceName)
    return $rsc
   }
   </lrns:ResourceList>
};

declare function rsc:insertResourceLock($resource as element(lrns:Resource)) as xs:boolean
{
   let $sonuc := util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWResources10.xml")/lrns:ResourceList, rsc:insertResource($resource))     
   return true()
};

declare function rsc:insertResource($resource as element(lrns:Resource)) as node()*
{
    let $XXX := $resource

	return update insert 
     <lrns:Resource xmlns="http://www.likyateknoloji.com/XML_resource_types" entry-name="{data($resource/@entry-name)}"> 
        <rns:endpoint>{$XXX/rns:endpoint/*}</rns:endpoint>
		<rns:metadata>{$XXX/rns:metadata/*}</rns:metadata>
		<jsdl:OperatingSystemName>{data($XXX/jsdl:OperatingSystemName)}</jsdl:OperatingSystemName>
     </lrns:Resource>
	into doc("xmldb:exist:///db/TLOSSW/xmls/tlosSWResources10.xml")/lrns:ResourceList
} ;

declare function rsc:updateResource($resource as element(lrns:Resource))
{
	for $resourcedon in doc("//db/TLOSSW/xmls/tlosSWResources10.xml")/lrns:ResourceList/lrns:Resource
	where $resourcedon/@entry-name = $resource/@entry-name
	return  update replace $resourcedon with $resource
};

declare function rsc:updateResourceLock($resource as element(lrns:Resource))
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWResources10.xml")/lrns:ResourceList/lrns:Resource, rsc:updateResource($resource))     
};

declare function rsc:deleteResource($resource as element(lrns:ResourceType))
 {
	for $resourcedon in doc("//db/TLOSSW/xmls/tlosSWResources10.xml")/lrns:ResourceList/lrns:Resource
	where $resourcedon/@entry-name = $resource/@entry-name
	return update delete $resourcedon
};

declare function rsc:deleteResourceLock($resource as element(lrns:ResourceType))
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWResources10.xml")/lrns:ResourceList/lrns:Resource, rsc:deleteResource($resource))     
};
