xquery version "1.0";

module namespace ks = "http://ks.tlos.com/";

declare namespace pp = "http://www.likyateknoloji.com/XML_PP_types";
declare namespace com = "http://www.likyateknoloji.com/XML_common_types";

import module namespace sq = "http://sq.tlos.com/" at "moduleSequenceOperations.xquery";
(:
Mappings
$programProvisioningDocumentUrl = doc("//db/TLOSSW/xmls/tlosSWProgramProvisioning10.xml")
$sequenceDataDocumentUrl = $documentSeqUrl = doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")
:)

(:fn:empty($prs/com:role):)
declare function ks:searchPP($programProvisioningDocumentUrl as xs:string, $searchPp as element(pp:License)) as element(pp:License)* 
 {
	for $pp in doc($programProvisioningDocumentUrl)/pp:Licenses/pp:License
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
declare function ks:ppList($programProvisioningDocumentUrl as xs:string, $firstElement as xs:int, $lastElement as xs:int) as element(pp:License)* 
 {
	for $pp in doc($programProvisioningDocumentUrl)/pp:Licenses/pp:License[position() = ($firstElement to $lastElement)]
	return  $pp
};

(: ornek kullanim ks:searchPpByPpName(xs:string('Tlos SW')) :)
declare function ks:searchPpByPpName($programProvisioningDocumentUrl as xs:string, $searchPpName as xs:string) as element(pp:License)? 
 {
	for $pp in doc($programProvisioningDocumentUrl)/pp:Licenses/pp:License
	where fn:lower-case($pp/pp:Name)=fn:lower-case($searchPpName)
    return $pp
};

declare function ks:searchPpByID($programProvisioningDocumentUrl as xs:string, $id as xs:integer) as element(pp:License)? 
 {
	for $pp in doc($programProvisioningDocumentUrl)/pp:Licenses/pp:License
	where $pp/@licenseID = $id
    return $pp
};

declare function ks:insertPpLock($programProvisioningDocumentUrl as xs:string, $pp as element(pp:License)) as xs:boolean
{
   let $sonuc := util:exclusive-lock(doc($programProvisioningDocumentUrl)/pp:Licenses, ks:insertPp($programProvisioningDocumentUrl, $pp))     
   return true()
};

declare function ks:insertPp($programProvisioningDocumentUrl as xs:string, $sequenceDataDocumentUrl as xs:string, $pp as element(pp:License)) as node()*
{
    let $XXX := $pp
    let $nextId := sq:getNextId($sequenceDataDocumentUrl, "ppId")	

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
	into doc($programProvisioningDocumentUrl)/pp:Licenses
} ;

declare function ks:updatePp($programProvisioningDocumentUrl as xs:string, $pp as element(pp:License))
{
	for $ppdon in doc($programProvisioningDocumentUrl)/pp:Licenses/pp:License
	where $ppdon/@licenseID = $pp/@licenseID
	return  update replace $ppdon with $pp
};

declare function ks:updatePpLock($programProvisioningDocumentUrl as xs:string, $pp as element(pp:License))
{
   util:exclusive-lock(doc($programProvisioningDocumentUrl)/pp:Licenses/pp:License, ks:updatePp($programProvisioningDocumentUrl, $pp))     
};

declare function ks:deletePp($programProvisioningDocumentUrl as xs:string, $pp as element(pp:License))
 {
	for $ppdon in doc($programProvisioningDocumentUrl)/pp:Licenses/pp:License
	where $ppdon/@licenseID = $pp/@licenseID
	return update delete $ppdon
};

declare function ks:deletePpLock($programProvisioningDocumentUrl as xs:string, $pp as element(pp:License))
{
   util:exclusive-lock(doc($programProvisioningDocumentUrl)/pp:Licenses/pp:License, ks:deletePp($programProvisioningDocumentUrl, $pp))     
};
