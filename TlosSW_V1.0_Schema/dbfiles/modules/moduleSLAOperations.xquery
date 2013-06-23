xquery version "1.0";

module namespace hs = "http://hs.tlos.com/";

declare namespace sla = "http://www.likyateknoloji.com/XML_SLA_types";
declare namespace per = "http://www.likyateknoloji.com/XML_permission_types";
declare namespace com = "http://www.likyateknoloji.com/XML_common_types";
declare namespace fn="http://www.w3.org/2005/xpath-functions";

import module namespace sq = "http://sq.tlos.com/" at "moduleSequenceOperations.xquery";

(:
Mappings
$slaDocumentUrl = doc("//db/TLOSSW/xmls/tlosSWSLAs10.xml")
$sequenceDataDocumentUrl = $documentSeqUrl = doc("//db/TLOSSW/xmls/tlosSWSequenceData10.xml")
:)

(:fn:empty($prs/com:role):)
declare function hs:searchSLA($slaDocumentUrl as xs:string, $searchSla as element(sla:SLA)) as element(sla:SLA)* 
 {
	  for $sla in doc($slaDocumentUrl)/sla:ServiceLevelAgreement/sla:SLA
	  let $nedirx := if (count($searchSla/sla:ResourcePool/sla:Resource) eq 0 or
                            (count($sla/sla:ResourcePool/sla:Resource) eq 0 and count($searchSla/sla:ResourcePool/sla:Resource) > 0)) then true()
                          else 
	                        let $nedir := 
	                          for $nn in $sla/sla:ResourcePool/sla:Resource,
				                  $xx in $searchSla/sla:ResourcePool/sla:Resource
	                          where fn:lower-case($nn)=fn:lower-case($xx) or 
                                   data($xx) = "" or not(exists($xx))
				              return true()
                            let $kosul := if (not($nedir)) then false()
						                  else true()
						    return $kosul
				      
	let $nedir1 := if ((fn:contains(fn:lower-case($sla/sla:Name), fn:lower-case($searchSla/sla:Name)) or data($searchSla/sla:Name)="")
                  and	(data($sla/com:userId)=data($searchSla/com:userId) or data($searchSla/com:userId) = "-1")
			      and	((data($sla/sla:StartDate) ge data($searchSla/sla:StartDate) or data($searchSla/sla:StartDate) = "")
 			      and (data($sla/sla:EndDate) le data($searchSla/sla:EndDate) or data($searchSla/sla:EndDate) = "")
				 )
               and $nedirx
             ) then $sla else ()
   let $sonuc := $nedir1
   return $sonuc
};

declare function hs:searchSLAtest($slaDocumentUrl as xs:string, $searchSla as element(sla:SLA)) as node()*
 {


	  for $sla in doc($slaDocumentUrl)/sla:ServiceLevelAgreement/sla:SLA
	  let $nedirx := if (count($searchSla/sla:ResourcePool/sla:Resource) eq 0 or
                            (count($sla/sla:ResourcePool/sla:Resource) eq 0 and count($searchSla/sla:ResourcePool/sla:Resource) > 0)) then true()
                          else 
	                        let $nedir := 
	                          for $nn in $sla/sla:ResourcePool/sla:Resource,
				                  $xx in $searchSla/sla:ResourcePool/sla:Resource
	                          where fn:lower-case($nn)=fn:lower-case($xx) or 
                                   data($xx) = "" or not(exists($xx))
				              return true()
                            let $kosul := if (not($nedir)) then false()
						                  else true()
						    return $kosul
				      
	let $nedir1 := if ((fn:contains(fn:lower-case($sla/sla:Name), fn:lower-case($searchSla/sla:Name)) or data($searchSla/sla:Name)="")
                  and	(data($sla/com:userId)=data($searchSla/com:userId) or data($searchSla/com:userId) = "-1")
			      and	((data($sla/sla:StartDate) ge data($searchSla/sla:StartDate) or data($searchSla/sla:StartDate) = "")
 			      and (data($sla/sla:EndDate) le data($searchSla/sla:EndDate) or data($searchSla/sla:EndDate) = "")
				 )
               and $nedirx
             ) then $sla else ()
   let $sonuc := $nedir1
   return $sonuc

};

(: ornek kullanim hs:slaList(1,2) ilk iki eleman :)
declare function hs:slaList($slaDocumentUrl as xs:string, $firstElement as xs:int, $lastElement as xs:int) as element(sla:SLA)* 
 {
	for $sla in doc($slaDocumentUrl)/sla:ServiceLevelAgreement/sla:SLA[position() = ($firstElement to $lastElement)]
	return  $sla
};

(: ornek kullanim hs:searchSlaBySlaName(xs:string('Genel SLA')) :)
declare function hs:searchSlaBySlaName($slaDocumentUrl as xs:string, $searchSlaName as xs:string) as element(sla:SLA)? 
 {
	for $sla in doc($slaDocumentUrl)/sla:ServiceLevelAgreement/sla:SLA
	where fn:lower-case($sla/sla:Name)=fn:lower-case($searchSlaName)
    return $sla
};

declare function hs:searchSlaBySlaId($slaDocumentUrl as xs:string, $id as xs:integer) as element(sla:SLA)? 
 {
	for $sla in doc($slaDocumentUrl)/sla:ServiceLevelAgreement/sla:SLA
	where $sla/@ID = $id
    return $sla
};

declare function hs:insertSlaLock($slaDocumentUrl as xs:string, $sequenceDataDocumentUrl as xs:string, $sla as element(sla:SLA)) as xs:boolean
{
   let $sonuc := util:exclusive-lock(doc($slaDocumentUrl)/sla:ServiceLevelAgreement, hs:insertSla($slaDocumentUrl, $sequenceDataDocumentUrl, $sla))     
   return true()
};

declare function hs:insertSla($slaDocumentUrl as xs:string, $sequenceDataDocumentUrl as xs:string, $sla as element(sla:SLA)) as node()*
{
    let $XXX := $sla
    
    let $nextId := sq:getNextId($sequenceDataDocumentUrl, "slaId")	
    (: 
     let $quote := "&#34;"
     let $yap := util:eval(concat("<sla:SLA ID=",$quote,$nextId,$quote,"/>"))
    :)
	return update insert 
		<sla:SLA xmlns="http://www.likyateknoloji.com/XML_SLA_types" ID="{$nextId}"> 
		  <sla:Organization>{data($XXX/sla:Organization)}</sla:Organization>
                  <sla:Name>{data($XXX/sla:Name)}</sla:Name>
                  <sla:Desc>{data($XXX/sla:Desc)}</sla:Desc>
                  <sla:CreationDate>{data($XXX/sla:CreationDate)}</sla:CreationDate>
                  <sla:StartDate>{data($XXX/sla:StartDate)}</sla:StartDate>
                  <sla:EndDate>{data($XXX/sla:EndDate)}</sla:EndDate>
                  <sla:SInterval>{$XXX/sla:SInterval/*}</sla:SInterval>
                  <sla:RInterval>{$XXX/sla:RInterval/*}</sla:RInterval>
                  <sla:Priority>{data($XXX/sla:Priority)}</sla:Priority>
                  <sla:ResourcePool>{$XXX/sla:ResourcePool/*}</sla:ResourcePool>
                  <sla:ResourceReq>{$XXX/sla:ResourceReq/*}</sla:ResourceReq>
                  <sla:calendarId>{data($XXX/sla:calendarId)}</sla:calendarId>
                  <sla:QueueFrame>{$XXX/sla:QueueFrame/*}</sla:QueueFrame>
                  <sla:ResolveIncident>{$XXX/sla:ResolveIncident/*}</sla:ResolveIncident>
                  <sla:JobsInStatus>{$XXX/sla:JobsInStatus/*}</sla:JobsInStatus>
                  <com:userId>{data($XXX/com:userId)}</com:userId>		
                </sla:SLA>	
	into doc($slaDocumentUrl)/sla:ServiceLevelAgreement
} ;

declare function hs:updateSLA($slaDocumentUrl as xs:string, $sla as element(sla:SLA))
{
	for $sladon in doc($slaDocumentUrl)/sla:ServiceLevelAgreement/sla:SLA
	where $sladon/@ID = $sla/@ID
	return  update replace $sladon with $sla
};

declare function hs:updateSLALock($slaDocumentUrl as xs:string, $sla as element(sla:SLA))
{
   util:exclusive-lock(doc($slaDocumentUrl)/sla:ServiceLevelAgreement/sla:SLA, hs:updateSLA($slaDocumentUrl, $sla))     
};

declare function hs:deleteSLA($slaDocumentUrl as xs:string, $sla as element(sla:SLA))
 {
	for $sladon in doc($slaDocumentUrl)/sla:ServiceLevelAgreement/sla:SLA
	where $sladon/@ID = $sla/@ID
	return update delete $sladon
};

declare function hs:deleteSLALock($slaDocumentUrl as xs:string, $sla as element(sla:SLA))
{
   util:exclusive-lock(doc($slaDocumentUrl)/sla:ServiceLevelAgreement/sla:SLA, hs:deleteSLA($slaDocumentUrl, $sla))     
};
