xquery version "1.0";
module namespace ks = "http://ks.tlos.com/";

declare namespace pp = "http://www.likyateknoloji.com/XML_PP_types";
declare namespace com = "http://www.likyateknoloji.com/XML_common_types";

import module namespace sq = "http://sq.tlos.com/" at "xmldb:exist://db/TLOSSW/modules/moduleSequenceOperations.xquery";

(:fn:empty($prs/com:role):)
declare function ks:searchPP($searchPp as element(pp:License)) as element(pp:License)* 
 {
	for $pp in doc("//db/TLOSSW/xmls/tlosSWProgramProvisioning10.xml")/pp:Licenses/pp:License
	  let $nedirx := if (count($searchPp/pp:ResourcePool/pp:Resource) eq 0 or
                            (count($pp/pp:ResourcePool/pp:Resource) eq 0 and count($searchPp/pp:ResourcePool/pp:Resource) > 0)) then true()
                          else 
	                        let $nedir := 
	                          for $nn in $pp/pp:ResourcePool/pp:Resource,
				                  $xx in $searchPp/pp:ResourcePool/pp:Resource
	                          where fn:lower-case($nn)=fn:lower-case($xx) or 
                                   data($xx) = "" or not(exists($xx))
				              return true()
                            let $kosul := if (not($nedir)) then false()
						                  else true()
						    return $kosul
	let $nedir1 := if ((fn:contains(fn:lower-case($pp/pp:Name), fn:lower-case($searchPp/pp:Name)) or data($searchPp/pp:Name)="")
                  and	(data($pp/com:userId)=data($searchPp/com:userId) or data($searchPp/com:userId) = "-1")
			      and	((data($pp/pp:StartDate) ge data($searchPp/pp:StartDate) or data($searchPp/pp:StartDate) = "")
 			      and (data($pp/pp:EndDate) le data($searchPp/pp:EndDate) or data($searchPp/pp:EndDate) = "")
				 )
               and $nedirx
             ) then $pp else ()
   let $sonuc := $nedir1
   return $sonuc
(:   
	let $nedir := count( 
	              for $nn in $pp/pp:ResourcePool/pp:Resource,
				      $xx in $searchPp/pp:ResourcePool/pp:Resource
	              where fn:lower-case($nn)=fn:lower-case($xx) or 
                        data($xx) = "" or not(exists($xx))
				  return true() )
	return if ((fn:contains(fn:lower-case($pp/pp:Name), fn:lower-case($searchPp/pp:Name)) or data($searchPp/pp:Name)="")
                  and	(data($pp/com:userId)=data($searchPp/com:userId) or data($searchPp/com:userId) = "-1")
			and	((data($pp/pp:StartDate) ge data($searchPp/pp:StartDate) or data($searchPp/pp:StartDate) = "")
 			      and (data($pp/pp:EndDate) le data($searchPp/pp:EndDate) or data($searchPp/pp:EndDate) = "")
				 )
                and	$nedir
             )
		then $pp
		else  ( )
		:)
};

(: ornek kullanim ks:ppList(1,2) ilk iki eleman :)
declare function ks:ppList($firstElement as xs:int, $lastElement as xs:int) as element(pp:License)* 
 {
	for $pp in doc("//db/TLOSSW/xmls/tlosSWProgramProvisioning10.xml")/pp:Licenses/pp:License[position() = ($firstElement to $lastElement)]
	return  $pp
};

(: ornek kullanim ks:searchPpByPpName(xs:string('Tlos SW')) :)
declare function ks:searchPpByPpName($searchPpName as xs:string) as element(pp:License)? 
 {
	for $pp in doc("//db/TLOSSW/xmls/tlosSWProgramProvisioning10.xml")/pp:Licenses/pp:License
	where fn:lower-case($pp/pp:Name)=fn:lower-case($searchPpName)
    return $pp
};

declare function ks:searchPpByID($id as xs:integer) as element(pp:License)? 
 {
	for $pp in doc("//db/TLOSSW/xmls/tlosSWProgramProvisioning10.xml")/pp:Licenses/pp:License
	where $pp/@licenseID = $id
    return $pp
};

declare function ks:insertPpLock($pp as element(pp:License)) as xs:boolean
{
   let $sonuc := util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWProgramProvisioning10.xml")/pp:Licenses, ks:insertPp($pp))     
   return true()
};

declare function ks:insertPp($pp as element(pp:License)) as node()*
{
    let $XXX := $pp
    let $nextId := sq:getNextId("ppId")	

	return update insert 
     <pp:License xmlns="http://www.likyateknoloji.com/XML_PP_types" licenseID="{$nextId}"> 
        <pp:Name>{data($XXX/pp:Name)}</pp:Name>
        <pp:version>{data($XXX/pp:version)}</pp:version>
        <pp:StartDate>{data($XXX/pp:StartDate)}</pp:StartDate>
        <pp:EndDate>{data($XXX/pp:EndDate)}</pp:EndDate>
        <pp:ResourcePool>{$XXX/pp:ResourcePool/*}</pp:ResourcePool>
        {$XXX/pp:Type}
        <com:userId>{data($XXX/com:userId)}</com:userId>
     </pp:License>
	into doc("xmldb:exist:///db/TLOSSW/xmls/tlosSWProgramProvisioning10.xml")/pp:Licenses
} ;

declare function ks:updatePp($pp as element(pp:License))
{
	for $ppdon in doc("//db/TLOSSW/xmls/tlosSWProgramProvisioning10.xml")/pp:Licenses/pp:License
	where $ppdon/@licenseID = $pp/@licenseID
	return  update replace $ppdon with $pp
};

declare function ks:updatePpLock($pp as element(pp:License))
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWProgramProvisioning10.xml")/pp:Licenses/pp:License, ks:updatePp($pp))     
};

declare function ks:deletePp($pp as element(pp:License))
 {
	for $ppdon in doc("//db/TLOSSW/xmls/tlosSWProgramProvisioning10.xml")/pp:Licenses/pp:License
	where $ppdon/@licenseID = $pp/@licenseID
	return update delete $ppdon
};

declare function ks:deletePpLock($pp as element(pp:License))
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWProgramProvisioning10.xml")/pp:Licenses/pp:License, ks:deletePp($pp))     
};
