xquery version "1.0";

module namespace dss = "http://tlos.dss.com/";

import module namespace lk = "http://likya.tlos.com/" at "moduleNrpeOperations.xquery";
import module namespace met = "http://meta.tlos.com/" at "moduleMetaDataOperations.xquery";
import module namespace sq = "http://sq.tlos.com/" at "moduleSequenceOperations.xquery";

declare namespace dat = "http://www.likyateknoloji.com/XML_data_types";
declare namespace com = "http://www.likyateknoloji.com/XML_common_types";
declare namespace cal = "http://www.likyateknoloji.com/XML_calendar_types";
declare namespace agnt= "http://www.likyateknoloji.com/XML_agent_types";
declare namespace xsi = "http://www.w3.org/2001/XMLSchema-instance";
declare namespace fn  = "http://www.w3.org/2005/xpath-functions";
declare namespace lrns= "http://www.likyateknoloji.com/XML_SWResourceNS_types";
declare namespace nrp = "http://www.likyateknoloji.com/XML_nrpe_types";
declare namespace nrpr= "http://www.likyateknoloji.com/XML_nrpe_results";
declare namespace res = "http://www.likyateknoloji.com/resource-extension-defs";
declare namespace sla = "http://www.likyateknoloji.com/XML_SLA_types";
declare namespace pp  = "http://www.likyateknoloji.com/XML_PP_types";
declare namespace functx = "http://www.functx.com"; 
declare namespace funcp = "http://www.likyateknoloji.com/XML_FuncPass_types";

(:
Mappings
$agentsDocumentUrl = doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")
$planDocumentUrl = doc("//db/TLOSSW/xmls/tlosSWDailyPlan10.xml")
$slaDocumentUrl = doc("//db/TLOSSW/xmls/tlosSWSLAs10.xml")
$programProvisioningDocumentUrl = doc("//db/TLOSSW/xmls/tlosSWProgramProvisioning10.xml")
$resourcesDocumentUrl = doc("//db/TLOSSW/xmls/tlosSWResources10.xml")
$nrpeDataDocumentUrl = doc("//db/TLOSSW/xmls/tlosSWNrpeData10.xml")
:)

declare function functx:repeat-string 
  ( $stringToRepeat as xs:string? ,
    $count as xs:integer )  as xs:string {
       
   string-join((for $i in 1 to $count return $stringToRepeat),
                        '')
 } ;

 declare function functx:pad-integer-to-length 
  ( $integerToPad as xs:anyAtomicType? ,
    $length as xs:integer )  as xs:string {
       
   if ($length < string-length(string($integerToPad)))
   then error(xs:QName('functx:Integer_Longer_Than_Length'))
   else concat
         (functx:repeat-string(
            '0',$length - string-length(string($integerToPad))),
          string($integerToPad))
 } ;

declare function functx:time 
  ( $hour as xs:anyAtomicType ,
    $minute as xs:anyAtomicType ,
    $second as xs:anyAtomicType )  as xs:time {
       
   xs:time(
     concat(
       functx:pad-integer-to-length(xs:integer($hour),2),':',
       functx:pad-integer-to-length(xs:integer($minute),2),':',
       functx:pad-integer-to-length(xs:integer($second),2)))
 } ;
 
declare function dss:is-node-in-sequence-deep-equal 
  ( $node as node()? ,
    $seq as node()* )  as xs:boolean {
       
   some $nodeInSeq in $seq satisfies deep-equal($nodeInSeq,$node)
 } ;

declare function dss:distinct-deep 
  ( $nodes as node()* )  as node()* {
       
    for $seq in (1 to count($nodes))
    return $nodes[$seq][not(dss:is-node-in-sequence-deep-equal(
                          .,$nodes[position() < $seq]))]
 } ;

(: Bu fonksiyon var olan kaynak listesi icinden kullanilabilir olanlari listeler. 
   Return : tanimli kaynak listesinin benzeri fakat muhtemelen bir alt listesi seklinde bir liste doner.
 :)

declare function dss:ResourceUnique($n as node(), $jobPropertiesFuncPass as element(funcp:jobPropFuncPass)) as node()*
{
      let $useMonitoringData := xs:boolean($jobPropertiesFuncPass/@useMonitoringData)
      
      let $resources := for $tek in $n/nrpr:nrpeCall
						return <resource entry-name="{ $tek/@entry-name }" 
						                         id="{ $tek/@id }" 
									 inJmxAvailable="{ $tek/@inJmxAvailable }" 
									   JmxAvailable="{ $tek/@JmxAvailable }"
                                       os="{ $tek/@os }">
						                                                  { if($useMonitoringData and exists($tek/nrpr:message)) then 
                                                                              ($tek/nrpr:message/nrpr:response/nrpr:disk[data(@forWhat)="free"] ,
										                                      $tek/nrpr:message/nrpr:response/nrpr:mem[data(@forWhat)="free"] ,
																			  $tek/nrpr:message/nrpr:response/nrpr:cpu[data(@timein)="1"])
                                                                            else
                                                                              ()
										                                  } 
                              </resource>
	  let $kaynaklar := <resource_list>{dss:distinct-deep($resources)}</resource_list>

      return $kaynaklar
};

declare function dss:getMonitoringData($documentUrl as xs:string, $agentName as xs:string) as node()*
{                
  let $nrpeOutput := lk:nrpeOutput($documentUrl, 1, 1, $agentName)
  
  let $monitoringData := for $tek in $nrpeOutput/nrpr:nrpeCall
                  return 
                   <nrpr:nrpeCall entry-name="{$tek/@entry-name}" port="{$tek/@port}" os="{$tek/@os}">
                   {
                      let $lastMessage := (for $tektek in $tek/nrpr:message
                                          order by $tektek/@time descending 
                                          return $tektek)[1]
				      return $lastMessage
                   }
                   </nrpr:nrpeCall>
  
  let $monitoringDataList := <lrns:ResourceList>
                             { 
                                $monitoringData 
                             }
                             </lrns:ResourceList>

  return $monitoringDataList

};

declare function dss:GetAvailableResourcesList($documentUrl as xs:string, $jobPropertiesFuncPass as element(funcp:jobPropFuncPass)) as node()*
{
  let $agentsDocumentUrl := met:getMetaData($documentUrl, "agents")
  let $resourcesDocumentUrl := met:getMetaData($documentUrl, "resources")
		
  let $oSystem := $jobPropertiesFuncPass/funcp:oSystem
  let $useMonitoringData := xs:boolean($jobPropertiesFuncPass/@useMonitoringData)
  
  let $agentAvailabilityList := <agentList> 
                     {
                       for $kek in doc($agentsDocumentUrl)/agnt:SWAgents/agnt:SWAgent
                       where $kek/agnt:osType = $oSystem (:)//TODO OS belli olmadan bir is calistirilir mi?:)
                       return 
                         <agent id="{data($kek/@id)}" 
    					   entry-name="{$kek/res:Resource/text()}"
						   inJmxAvailable="{ $kek/agnt:inJmxAvailable/text() }"
						   JmxAvailable="{ $kek/agnt:jmxAvailable/text() }"
                           os="{$kek/agnt:osType/text()}" >
						 </agent>
				      }
				 </agentList>
                 
  let $resultWithMDL := if($useMonitoringData)
                        then 
                          let $monitoringDataList := dss:getMonitoringData($documentUrl, "All")
                          let $result :=
                              <lrns:ResourceList>
                              {
                                for $cd in doc($resourcesDocumentUrl)/*/*, (: \\ TODO lrns:ResourceList/lrns:Resource, neden calismiyor? ns ile ilgili bir problem galiba :)
                                    $tek in $monitoringDataList/nrpr:nrpeCall,
                                    $de in $agentAvailabilityList/agent
	                            where data($tek/@entry-name)=data($cd/@entry-name) and data($de/@entry-name)=data($cd/@entry-name)
	                            return 
                                  let $monData :=  if( xs:boolean(doc($agentsDocumentUrl)/agnt:SWAgents/agnt:SWAgent[@id = $de/@id]/agnt:nrpeAvailable) )
                                                   then
                                                       $tek/nrpr:message
                                                   else
                                                       ()
                                  return
	                              <nrpr:nrpeCall 
	                                entry-name="{$cd/@entry-name}" 
		                            id="{$de/@id}"
		                            inJmxAvailable="{ $de/@inJmxAvailable }"
	                                JmxAvailable="{ $de/@JmxAvailable }"
                                    os="{$tek/@os}"> { $monData } </nrpr:nrpeCall>
                              }
                              </lrns:ResourceList>
                           return $result
                        else ()
   
  let $resultWithoutMDL :=
   <lrns:ResourceList>
   {
     for $cd in doc($resourcesDocumentUrl)/*/*, (: \\ TODO lrns:ResourceList/lrns:Resource, neden calismiyor? ns ile ilgili bir problem galiba :)
	  $de in $agentAvailabilityList/agent
	 where data($de/@entry-name)=data($cd/@entry-name)
	 return 
	   <nrpr:nrpeCall 
	     entry-name="{$cd/@entry-name}" 
		 id="{$de/@id}"
		 inJmxAvailable="{ $de/@inJmxAvailable }"
	     JmxAvailable="{ $de/@JmxAvailable }"
         os="{$de/@os}"></nrpr:nrpeCall>
   }
   </lrns:ResourceList>

   let $result := if($useMonitoringData)
               then $resultWithMDL
               else $resultWithoutMDL

   return dss:ResourceUnique($result, $jobPropertiesFuncPass)
};

(: 
  Kaynagin belirlenen SSA (SLA) e uyup uymadigini kontrol eder.
  Return :  Eger uygun ise XXX degilse false doner.
:)

declare function dss:GetAvailableResourcesListUnique($n as node()) as node()*
{
      let $resources := for $tek in $n/resource
						return <resource entry-name="{ $tek/@entry-name }">
						                                                  { $tek/nrpr:disk,
										                                    $tek/nrpr:mem,
																			$tek/nrpr:cpu
										                                  } 
                              </resource> 
	  let $kaynaklar := <resource_list>{dss:distinct-deep($resources)}</resource_list>
		
      return $kaynaklar
};

declare function dss:ResourceSLACheck($documentUrl as xs:string, $gununtarihi as xs:dateTime, $planId as xs:integer, $jobPropertiesFuncPass as element(funcp:jobPropFuncPass)) as node()*
{
  let $planDocumentUrl := met:getMetaData($documentUrl, "plan")
  let $slaDocumentUrl := met:getMetaData($documentUrl, "sla")

  let $currentPlanId := if($planId eq 0) 
                        then 
                          sq:getId($documentUrl, "planId") 
                        else 
                          $planId
  
  let $dailyPlan := doc($planDocumentUrl)
  
  let $slaId := $jobPropertiesFuncPass/funcp:SLAId
  let $acceptedSLAId := if(exists($slaId))
                        then 
                          $slaId 
                        else 
                          "0"
                          
  let $zaman := functx:time( fn:hours-from-dateTime($gununtarihi),
                             fn:minutes-from-dateTime($gununtarihi),
                             fn:seconds-from-dateTime($gununtarihi))
    						 
(:  let $gununtarihi := fn:dateTime(xs:date("2011-09-30"), xs:time("09:30:10+06:00")) :)

return
    <ResourceSLACheck>
      {
        for $tek in doc($slaDocumentUrl)/sla:ServiceLevelAgreement/sla:SLA[@ID=$acceptedSLAId or $acceptedSLAId = "0"],
            $calendar in $dailyPlan/AllPlanParameters/plan[@id = $currentPlanId and data(calID) = data($tek/sla:calendarId)]
        return
          <SLACheck>
    	  {
            $tek, 
            if( data($tek/sla:StartDate) <= $gununtarihi and data($tek/sla:EndDate) > $gununtarihi ) 
			then 
                if( xs:time($tek/sla:SInterval/sla:startTime) <= xs:time($zaman) and xs:time($tek/sla:SInterval/sla:stopTime) > xs:time($zaman) ) 
                then 
                  if( xs:time($tek/sla:RInterval/sla:startTime) <= xs:time($zaman) and xs:time($tek/sla:RInterval/sla:stopTime) > xs:time($zaman) ) 
                  then <slaValidation result="false">Bu SLA tanimli oldugu tarih araligi icinde fakat haric tutulan zamandadir. Gecersizdir !</slaValidation>
                  else <slaValidation result="true">SLA tarih ve zaman kontrolu. Gecerli !</slaValidation>
                else <slaValidation result="false">Bu SLA tanimli oldugu tarih araligi icinde fakat belirtilen zaman araligi disindadir. Gecersizdir !</slaValidation>
            else <slaValidation result="false">Bu SLA tanimli oldugu tarih araligi disindadir. Gecersizdir !</slaValidation>
          }</SLACheck>
      }
	</ResourceSLACheck>

};



(: Calistirilacak isin cinsine gore gerekli olan lisans kontrollerini ilgili kaynak dikkate alinarak yapar.
   Return: Lisans problemi yoksa True varsa False doner.
:)


declare function dss:ResourceProvisioningCheck($documentUrl as xs:string, $n as node(), $gunzaman as xs:dateTime, $jobPropertiesFuncPass as element(funcp:jobPropFuncPass)) as node()*
{
  (: SLA kosullari kontrol ediliyor :)
  (: Software/license :)
  (:---------------------------- KAYNAK TANIMLA ----------------------------:)
  (: kaynak ismi ve varsa kullanim parametresini alalim :)

  let $programProvisioningDocumentUrl := met:getMetaData($documentUrl, "programProvisioning")
    
  let $kaynaklar := $n

 (:------------------------------------------------------------------------:)

  let $sonuc := <ResourceProvisioningCheck> 
                { (: SLA den kosullari alalim :)
                  for $sla in dss:ResourceSLACheck($documentUrl, $gunzaman, 0, $jobPropertiesFuncPass)/SLACheck
                         
                   (: --------------------- ATAMALAR ----------------------------------------:)
                   (: SLA den ilgili parametreleri degiskenlere aktaralim (Burada License)   :)
				   
                  let $slaSoftwareList := $sla/sla:SLA/sla:ResourceReq/sla:Software
                  let $ResourcePool := $sla/sla:SLA/sla:ResourcePool
                  (: where data($tek/result) = "TRUE" *HS SLA ler uymazsa hardware kontrolu yapamiyordu. O yuzden kaldirdim. :)

				   (:------------------------------------------------------------------------:)

					(:--------------------- CEVIRI ve TRANSFORMASYONLAR ----------------------:)
					(: (Varsa) Gerekli cevrim ve donusumler burada yapilacak :)

                    (:------------------------------------------------------------------------:)
                    (: SLA kontrolu icin hersey hazir. Kontrolleri kaynak bazinda yapmak gerekiyor :)

					(:---------------------------- KAYNAK TANIMLA ----------------------------:)
					(: kaynak ismi ve varsa kullanim parametresini alalim                     :)
                         
					(:------------------------------------------------------------------------:)
                         
				    (:----------------------- UYGUNLUK KONTROLU / ESAS ISLEMLER --------------:)
                  return
                     <SLA ID="{ $sla/sla:SLA/@ID }">{
                     (:  Secilen SLA icin kaynak bazında kontrolleri yapmak icin hersey hazir :)
                     for $resources_data in $kaynaklar/resource
                      (: Her bir kaynak icin ... :)
                     return
                       <resource entry-name="{ $resources_data/@entry-name }">
                         {
                           (: kaynaklarin herbiri icin SLA e uygunluk kontrolunu yapalim :)
                           (: Birden fazla alt bilgi kontrolu varsa for dongusu ile donerek kontroller yapilir :)
                           (: SLA de istenen lisanslar($License_list) bu kaynak uzerinde var mi? :)
                           (: Belirli kaynak uzerinde lisans kontrolu :)
						   
                         let $ara := <hepsi>
						             {
									 for $license in doc($programProvisioningDocumentUrl)/pp:Licenses/pp:License,
                                         $software in $slaSoftwareList
                                     where $license/@licenseID = $software/sla:Program/@licenseID or count($software/sla:Program) eq 0
                                     return <ResourcePool>
									        {   
											    $license/@licenseID,
												$license/pp:ResourcePool/pp:Resource, 
												$license/pp:StartDate, 
												$license/pp:EndDate,
                                                $software
											}
											</ResourcePool>
									 }
									 </hepsi>
                           (:--------------------- CEVIRI ve TRANSFORMASYONLAR ----------------------:)
                           (:------------ Kaynak verisinden kullandiklarimizdan, ceviri ve transformayon yapacaklarimiz varsa burada yapalim :)
                           (:------------------------------------------------------------------------:)
                         (: let $gununtarihi := fn:dateTime(xs:date("2011-09-30"), xs:time("09:30:10+06:00")) :)
                         
                         (: ------------------ KONTROL NOKTASI ------------------------------------:)
                         let $licenseResourceSLA := for $abc in $ara/ResourcePool
                                                   (:where $resources_data/@entry-name = data($abc/Resource):)
                                                     return
                                                       <license>
						                               { 
                                                         let $arasonuc := 
                                                         
                                                           let $listedeVarmi := count( for $don in $abc/pp:Resource
                                                                                      where data($resources_data/@entry-name) = data($don)
                                                                                      return $don )
                                                           let $checkSoft :=
                                                           if( $listedeVarmi > 0 or 
							                                   not(exists($abc/pp:Resource)) or 
                                                               not(exists($abc/sla:Software/sla:Program)) 
                                                             )(: Kaynak kisitlanmis mi? :)  
						                                   then
                                                             if(data($abc/pp:StartDate) <= $gunzaman and data($abc/pp:EndDate) > $gunzaman) 
							                                 then <provisionCheck result="true">Kaynak icin software provision ok!</provisionCheck>
                                                             else <provisionCheck result="false">Kaynak icin software provision u yok! Listede var ama tarihi gecerli degil.</provisionCheck>
                                                           else 
                                                             <provisionCheck result="false">Kaynak icin software provision u yok! Listede yok!</provisionCheck>
                                                           return $checkSoft
                                                           
                                                          return 
                                                          ($abc/@licenseID, $arasonuc, $abc)
                                                       }
						                               </license>
                         let $vaybe := <fact> 
						               {
									    $licenseResourceSLA,
    					               <result> 
  					                   { (: Her bir kaynak icin SLA de kaynak tanimi acisindan problem var mi? ... :)
                                         let $toplam := sum(for $herbiri in $licenseResourceSLA
    						                                return 
								                              if (not(xs:boolean($herbiri/provisionCheck/@result)))
								                              then 1 
								                              else 0
                                                            )
							             return 
                                           if ($toplam eq 0 ) 
							               then "TRUE" 
							               else "FALSE"
                                        }
						                </result>
										}
										</fact>
						 
                         return $vaybe

                       (:------------------------------------------------------------------------:)
                       }
					 </resource>,
                     $sla/slaValidation
                   }
				 </SLA>
                 (:$tek/ResourceReq/Hardware/disk :)
                }
              </ResourceProvisioningCheck>
             (:let $SLA := for $tek in $SLA/ResourceReq/Hardware/disk return $tek:)
             return $sonuc
};


(: 
  Kaynagin belirlenen SSA (SLA) e uyup uymadigini kontrol eder.
  Return :  Eger uygun ise XXX degilse false doner.
:)

declare function dss:ResourceHardwareCheck($documentUrl as xs:string, $n as node(), $gunzaman as xs:dateTime, $jobPropertiesFuncPass as element(funcp:jobPropFuncPass)) as node()*
{
    (:---------------------------- KAYNAK TANIMLA ----------------------------:)
	(: kaynak ismi ve varsa kullanim parametresini alalim (burada disk) :)

	  let $kaynaklar := $n

    (:------------------------------------------------------------------------:)
  (: SLA kosullari kontrol ediliyor :)
  
   let $slaId := $jobPropertiesFuncPass/funcp:SLAId
  
   (: Hardware/disk :)
   let $sonuc := <ResourceHardwareCheck> { (: SLA den kosullari alalim :)
                         for $tek in dss:ResourceSLACheck($documentUrl, $gunzaman, 0, $jobPropertiesFuncPass)/SLACheck
                         
						 (: --------------------- ATAMALAR -----------------------------------:)
                         (: SLA den ilgili parametreleri degiskenlere aktaralim (Burada DISK) :)
						 let $SLA_disk_birim     := $tek/sla:SLA/sla:ResourceReq/sla:Hardware/sla:disk/@sla:birim
						 let $SLA_disk_for       := $tek/sla:SLA/sla:ResourceReq/sla:Hardware/sla:disk/@sla:forWhat
						 let $SLA_disk_condition := $tek/sla:SLA/sla:ResourceReq/sla:Hardware/sla:disk/@sla:condition
 						 let $SLA_mem_birim      := $tek/sla:SLA/sla:ResourceReq/sla:Hardware/sla:mem/@sla:birim
						 let $SLA_mem_for        := $tek/sla:SLA/sla:ResourceReq/sla:Hardware/sla:mem/@sla:forWhat
						 let $SLA_mem_condition  := $tek/sla:SLA/sla:ResourceReq/sla:Hardware/sla:mem/@sla:condition 
 						 let $SLA_cpu_birim      := $tek/sla:SLA/sla:ResourceReq/sla:Hardware/sla:cpu/@sla:birim
						 let $SLA_cpu_timein     := $tek/sla:SLA/sla:ResourceReq/sla:Hardware/sla:cpu/@sla:timein 
                         let $SLA_cpu_condition  := $tek/sla:SLA/sla:ResourceReq/sla:Hardware/sla:cpu/@sla:condition 
						 (:-------------------------------------------------------------------:)
                         let $ResourcePool := $tek/sla:SLA/sla:ResourcePool
						 (:--------------------- CEVIRI ve TRANSFORMASYONLAR ----------------------:)
						 (: Butun islemlerde disk alani olcumlemeleri MB a cevrilerek kullanilacak :)
						 let $SLA_disk_birim_result     := if ($SLA_disk_birim = "GB" or $SLA_disk_birim = "G") then data($tek/sla:SLA/sla:ResourceReq/sla:Hardware/sla:disk)*1024
						                                   else if ($SLA_disk_birim = "MB" or $SLA_disk_birim = "M") then data($tek/sla:SLA/sla:ResourceReq/sla:Hardware/sla:disk)
						                                   else if ($SLA_disk_birim = "KB" or $SLA_disk_birim = "K") then data($tek/sla:SLA/sla:ResourceReq/sla:Hardware/sla:disk) div 1024
															else ()
						 let $SLA_disk_for_result       := if ($SLA_disk_for = "free") then true() 
						                                    else if ($SLA_disk_for = "used") then false()
															else ()
						 let $SLA_disk_condition_gt_result := if ($SLA_disk_condition = "gt") then true() 
						                                      else if ($SLA_disk_condition = "lt") then false()
																else ()

                    	 (: Memory olcumlemeleri MB a cevrilerek kullanilacak :)
						 let $SLA_mem_birim_result        :=     if ($SLA_mem_birim  = "GB" or $SLA_mem_birim  = "G") then data($tek/sla:SLA/sla:ResourceReq/sla:Hardware/sla:mem)*1024
						                                    else if ($SLA_mem_birim = "MB" or $SLA_mem_birim = "M") then data($tek/sla:SLA/sla:ResourceReq/sla:Hardware/sla:mem)
						                                    else if ($SLA_mem_birim = "KB" or $SLA_mem_birim = "K") then data($tek/sla:SLA/sla:ResourceReq/sla:Hardware/sla:mem) div 1024
														    else ()
						 let $SLA_mem_for_result          :=     if ($SLA_mem_for    = "free") then true() 
						                                    else if ($SLA_mem_for   = "used") then false()
									                        else ()
						 let $SLA_mem_condition_gt_result :=     if ($SLA_mem_condition  = "gt") then true() 
						                                    else if ($SLA_mem_condition = "lt") then false()
														    else ()

                         (: cpu'nun son 1 dakikadaki degerlerini ogrenmek istiyoruz      :)
                         let $SLA_cpu_value_result        := data($tek/sla:SLA/sla:ResourceReq/sla:Hardware/sla:cpu) 
 		            
					     let $SLA_cpu_timein_result       :=     if ($SLA_cpu_timein =  "1") then true() 
					          	                            else if ($SLA_cpu_timein =  "5") then false()
						                                    else if ($SLA_cpu_timein = "15") then false()
										                    else ()
                        let $SLA_cpu_condition_lt_result  :=     if ($SLA_cpu_condition  = "lt") then true() 
						                                    else if ($SLA_cpu_condition = "gt") then false()
														    else ()
                         (:------------------------------------------------------------------------:)


                          (: SLA kontrolu icin hersey hazir. Kontrolleri kaynak bazinda yapmak gerekiyor :)

						 (:---------------------------- KAYNAK TANIMLA ----------------------------:)
						 (: kaynak ismi ve varsa kullanim parametresini alalim (burada disk) :)
(:                         let $resources := for $tekken in $n/nrp:nrpeCall
						                   where $tekken/nrp:message/nrp:response/disk[data(@forWhat)="free"] 
										   return <resource entry-name="{$tekken/@entry-name}"> { $tekken/nrp:message/nrp:response/disk[data(@forWhat)="free"] } </resource>
						 let $kaynaklar := <a>{$resources}</a>:)
                         (:------------------------------------------------------------------------:)
                         
                         (: where data($tek/result) = "TRUE" *HS SLA ler uymazsa hardware kontrolu yapamiyordu. O yuzden kaldirdim. :)
						 (: SLA Gecerli mi?:)
						 (:----------------------- UYGUNLUK KONTROLU / ESAS ISLEMLER --------------:)
						 (: kaynaklarin herbiri icin SLA e uygunluk kontrolunu yapalim :) 
						 
						 return <SLA ID="{$tek/sla:SLA/@ID}"> {
						 
						 (: Her bir SLA icin kaynak bazında kontrolleri yapmak icin hersey hazir :)
						   for $resources_data in $kaynaklar/resource (: Her bir kaynak icin ... :)
                           return 
                             let $isResourceIncluded := if(data($ResourcePool/sla:Resource) = $resources_data/@entry-name or fn:count($ResourcePool/sla:Resource) = 0) 
    									            then <isResourceIncuded result="true" >Kaynak ismi Resource Pool da Var</isResourceIncuded>
                                                    else <isResourceIncuded result="false" >Kaynak ismi Resource Pool da Yok</isResourceIncuded>
                             return
                             <resource entry-name="{ $resources_data/@entry-name }">
						          {
                                      $isResourceIncluded,
								  (:------- Birden fazla alt bilgi kontrolu olabilir diye for dongusu ile donerek kontrolleri yapiyoruz ---------:)

								  for $donbaba in $resources_data/nrpr:disk
                                  (:--------------------- CEVIRI ve TRANSFORMASYONLAR ----------------------:)
								  (:------------ Kaynak verisinden kullandiklarimizdan, ceviri ve transformayon yapacaklarimiz varsa burada yapalim :)
								  let $resource_disk_free := if ($donbaba/@birim = "MB" or $donbaba/@birim = "M") then data($donbaba) * 1
								                             else if ($donbaba/@birim = "GB" or $donbaba/@birim = "G") then data($donbaba)*1024
															 else if ($donbaba/@birim = "KB" or $donbaba/@birim = "K") then data($donbaba) div 1024
															 else ()
								  (:------------------------------------------------------------------------:)
                                  
								  (: ------------------ KONTROL NOKTASI ------------------------------------:)
						          let $resource_disk_result := if ($SLA_disk_condition_gt_result) then (: GT :)
						                                          (: KURAL : Kaynaktaki bos disk miktari, SLA deki ihtiyactan az olamaz :)
						                                           (if ( $resource_disk_free > $SLA_disk_birim_result ) 
                                                                   then <resourceDisk result="true" >Kaynaktaki bos disk miktari, SLA deki miktardan cok!</resourceDisk>
                                                                   else <resourceDisk result="false" >Kaynaktaki bos disk miktari, SLA deki miktardan az olamaz!</resourceDisk>
						                                           )
						                                       else if ( $resource_disk_free < $SLA_disk_birim_result ) 
                                                                    then <resourceDisk result="true" >Kaynaktaki bos disk miktari, SLA deki miktardan az!</resourceDisk>
                                                                    else <resourceDisk result="false" >Kaynaktaki bos disk miktari, SLA deki miktardan fazla olamaz!</resourceDisk>
                                                                    (:Disk condition=LT:)
								  (:return ( $donbaba , <result> { $resource_SLA_result } </result> ):)
								  return <Condition> {$donbaba, $resource_disk_result } </Condition> ,

								  for $donbaba in $resources_data/nrpr:mem
                                  (:--------------------- CEVIRI ve TRANSFORMASYONLAR ----------------------:)
								  (:------------ Kaynak verisinden kullandiklarimizdan, ceviri ve transformayon yapacaklarimiz varsa burada yapalim :)
								  let $resource_mem_free := if ($donbaba/@birim = "MB" or $donbaba/@birim = "M") then data($donbaba) * 1
								                             else if ($donbaba/@birim = "GB" or $donbaba/@birim = "G") then data($donbaba)*1024
															 else if ($donbaba/@birim = "KB" or $donbaba/@birim = "K") then data($donbaba) div 1024
															 else ()
								  (:------------------------------------------------------------------------:)
                                  
								  (: ------------------ KONTROL NOKTASI ------------------------------------:)
						          let $resource_mem_result := if ($SLA_mem_condition_gt_result) then (: GT :)
						                                         (: KURAL : Kaynaktaki bos memory miktari, SLA deki ihtiyactan az olamaz :)
						                                         (if ( $resource_mem_free > $SLA_mem_birim_result ) 
                                                                  then <resourceMem result="true" >Kaynaktaki bos memory miktari, SLA deki ihtiyactan cok!</resourceMem>
                                                                  else <resourceMem result="false" >Kaynaktaki bos memory miktari, SLA deki ihtiyactan az olamaz!</resourceMem>
						                                         )
						                                 else if (     $resource_mem_free < $SLA_mem_birim_result ) 
                                                              then <resourceMem result="true" >Kaynaktaki bos memory miktari, SLA deki ihtiyactan az!</resourceMem>
                                                              else <resourceMem result="false" >Kaynaktaki bos memory miktari, SLA deki ihtiyactan fazla olamaz!</resourceMem>
   (:Mem condition=LT:)
								  
								  return <Condition> {$donbaba, $resource_mem_result } </Condition> ,
								  (:------------------------------------------------------------------------:)

								  for $donbaba in $resources_data/nrpr:cpu
								    let $resource_cpu_value :=  data($donbaba)
								    (:------------------------------------------------------------------------:)

								    (: ------------------ KONTROL NOKTASI ------------------------------------:)
						            let $resource_cpu_result := if ($SLA_cpu_condition_lt_result) then (: LT :)
						                                         (: KURAL : Kaynakta CPU kullanimi SLA deki degeri asiyor mu? :)
						                                          (if ( xs:integer($resource_cpu_value) < xs:integer($SLA_cpu_value_result)  )  
                                                                  then <resourceCpu result="true" >Kaynakta CPU kullanimi SLA deki degeri asmiyor!</resourceCpu>
                                                                  else <resourceCpu result="false" >Kaynakta CPU kullanimi SLA deki degeri asiyor!</resourceCpu>
						                                          )
						                                        else if (  xs:integer($resource_cpu_value) > xs:integer($SLA_cpu_value_result) ) 
                                                                     then <resourceCpu result="true" >Kaynakta CPU kullanimi SLA deki degeri asiyor?</resourceCpu>
                                                                     else <resourceCpu result="false" >Kaynakta CPU kullanimi SLA deki degeri asmiyor!</resourceCpu>   (:CPU condition=GT:)
								  
								  return <Condition> {$donbaba , $resource_cpu_result } </Condition>
								  (:------------------------------------------------------------------------:)
								  } </resource>,
                                  $tek/slaValidation
						  }</SLA>
						 (:$tek/ResourceReq/Hardware/disk :)
					   } 
			   </ResourceHardwareCheck>

   (:let $SLA := for $tek in $SLA/ResourceReq/Hardware/disk return $tek:)
   return dss:distinct-deep($sonuc)
};

(: 
  Herturlu onay ve erisim yetkileri kontrol edilir. Kullanici belirlenen isi belirlenen kaynakta calistirma yetkisine sahip mi,
  lisansli programi kullanma execute etme yetkisi var mi gibi, gerektiginde SLA acisindna yetkilendirme kontrolleri de bu fonksiyon icerisinde
  gerceklestirilecektir.
  Return: Onay varsa True, yoksa False donulur.
:)

declare function dss:FindResourcesForAJob($documentUrl as xs:string, $jobPropertiesFuncPass as element(funcp:jobPropFuncPass), $gunzaman as xs:dateTime, $reportType as xs:string) as node()*
{
  let $agentsDocumentUrl := met:getMetaData($documentUrl, "agents")
  let $agentsDoc := doc($agentsDocumentUrl)
  
  (: ------------------------------------------ AGENTS -------------------------------------------------------------:)
  let $AvailableResourceListAll := dss:GetAvailableResourcesList($documentUrl, $jobPropertiesFuncPass) 

  let $oSystem := $jobPropertiesFuncPass/funcp:oSystem
  let $slaId := $jobPropertiesFuncPass/funcp:SLAId
  
  let $useSLA := xs:boolean($jobPropertiesFuncPass/@useSLA)
  let $useMonitoringData := xs:boolean($jobPropertiesFuncPass/@useMonitoringData)
  let $useProvisionData :=xs:boolean("true")
  
  let $AvailableResourceListFiltered := $AvailableResourceListAll/resource

  let $AvailableResourceList := if( not(fn:exists($AvailableResourceListFiltered)) ) 
                                then 
                                   <resource_list/> 
                                else 
                                   <resource_list> 
                                    { 
                                      $AvailableResourceListFiltered 
                                    } 
                                   </resource_list>
  
  (: ------------------------------------------ AGENTS EVALUATIONS-------------------------------------------------------------:)                               
  let $AgentCheck      := $AvailableResourceList
  let $AgentCheckLogic := <AgentCheckLogic>
                          {
                            for $agnt in $AvailableResourceList/resource
                            return
                              <agent ID="{ $agnt/@id }" 
    						        entry-name="{ $agnt/@entry-name }" 
								inJmxAvailable="{ $agnt/@inJmxAvailable }" 
								  JmxAvailable="{ $agnt/@JmxAvailable }">
                              {
                                if(sum(
                                         if(xs:boolean($agnt/@inJmxAvailable) or xs:boolean($agnt/@JmxAvailable) ) 
										 then 1
                                         else 0) 
									   > 0) 
								then "TRUE"
                                else "FALSE"
                              }
                              </agent>
                          }
                          </AgentCheckLogic>
                          
  (: Eger SLA kullanilmiyorsa bastan halledip gerikalan SLA islemlerini yapmayalim :)
  
  let $sonuc := 
        if(not($useSLA))
        then
          <rsr:ResourceAgentList 
            xmlns:rsr="http://www.likyateknoloji.com/XML_ResourceAgent_results" 
    		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
			xsi:schemaLocation="http://www.likyateknoloji.com/XML_ResourceAgent_results tlosSW_RALv_1_0.xsd"
            time="{adjust-dateTime-to-timezone(
                     current-dateTime(),
                     xs:dayTimeDuration('PT2H'))}"
          >
          {
            for $resource in $AvailableResourceList/resource
            (:for $resource in $LicenseCheckLogic/*:)
            return
              <rsr:resource 
		       entry-name="{ $resource/@entry-name }" 
			   agentid="{ $resource/@id }"
			   totalCost="123">
              {
                let $tutAgent := sum(for $Agent in $AgentCheckLogic/agent
                                     where data($resource/@entry-name) = data($Agent/@entry-name) and data($resource/@id) = data($Agent/@ID)
                                     return
                                       if(data($Agent) = "FALSE") 
    								   then 1
                                       else 0
									)
                return
                  if( $tutAgent > 0 )
				  then "FALSE"
                  else "TRUE"
                }
              </rsr:resource>
          }
          </rsr:ResourceAgentList>
        else
  (: ------------------------------------------ HARDWARE -------------------------------------------------------------:)

          let $resourceListUnique := dss:GetAvailableResourcesListUnique($AvailableResourceList)
          let $HardwareCheck         := dss:ResourceHardwareCheck($documentUrl, $resourceListUnique, $gunzaman, $jobPropertiesFuncPass)
  
          let $HardwareCheckLogic    := <HardwareCheckLogic>
                                { 
                                         for $sla in $HardwareCheck/SLA
                                         return 
    									 <SLA ID="{$sla/@ID}"> 
										 {
                                            let $validation := xs:boolean($sla/slaValidation/@result)
										    for $resource in $sla/resource    (: Herbir SLA icin kaynak bazinda hardware kontrolleri yapalim :)
											return 
											<resource entry-name="{$resource/@entry-name}">
											{
											  (:data($don/Condition/result):)
											   if (sum(for $herbiri in $resource/*
											           return 
											            if ( 
                                                             ( exists($resource/Condition)) and 
                                                               (
                                                                 not(xs:boolean($resource/Condition/resourceDisk/@result)) or 
                                                                 not(xs:boolean($resource/Condition/resourceMem/@result)) or 
                                                                 not(xs:boolean($resource/Condition/resourceCpu/@result)
                                                               )
                                                              ) or 
                                                              not(xs:boolean($resource/isResourceIncuded/@result))
                                                            )
												        then 1 
												        else 0) 
														= 0
												   ) 
												then "true" 
												else "false"
											}
											</resource>
										 ,$sla/slaValidation}
										 </SLA>
								} 
								</HardwareCheckLogic>

  (: ------------------------------------------ SOFTWARE PROVISION -------------------------------------------------------------:)
  
          let $ProvisioningCheck          := dss:ResourceProvisioningCheck($documentUrl, $resourceListUnique, $gunzaman, $jobPropertiesFuncPass)
          let $ProvisioningCheckLogic    := <ProvisioningCheckLogic>
                               { 
                                 for $sla in $ProvisioningCheck/SLA
                                 return 
								 <SLA ID="{$sla/@ID}"> 
								 {
								   for $resource in $sla/resource
								   return 
								   <resource entry-name="{$resource/@entry-name}">
								   {
								     if (sum(for $herbiri in $resource/fact/license
									         return 
									          if (not(xs:boolean($herbiri/provisionCheck/@result)) or data($herbiri/ancestor::result) = "FALSE")
									          then 1 
									          else 0) 
											 = 0) 
									 then "true" 
									 else "false"
								   }
								   </resource>,
                                    $sla/slaValidation
								 }
								 </SLA>
							   } 
							   </ProvisioningCheckLogic>

  (: ------------------------------------------ CONCLUSION  -------------------------------------------------------------:)
  (: SLA lerin birden fazla olmasi durumunda butun SLA lere gore uygun olan kaynaga atama yapaagiz. 
     //TODO Diger durum yani bazi SLA lere uymasa da uyan SLA veya
     //     agirliklandirilmis SLA ileriye donuk calismalara birakildi. :)
     
              let $sonuc := <rsr:ResourceAgentList 
                    xmlns:rsr="http://www.likyateknoloji.com/XML_ResourceAgent_results" 
					xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
					xsi:schemaLocation="http://www.likyateknoloji.com/XML_ResourceAgent_results tlosSW_RALv_1_0.xsd"
                    time="{adjust-dateTime-to-timezone(
                             current-dateTime(),
                             xs:dayTimeDuration('PT2H'))}"
                    >
                {
                  for $resource in $AvailableResourceList/resource
                  return
                    <rsr:resource 
					   entry-name="{ $resource/@entry-name }" 
					   agentid="{ $resource/@id }"
					   totalCost="123">
                    {
                      let $tutLicense := <tutLicense>
                                              {
                                              attribute slaValidation {xs:boolean("true")},
                                              let $top :=sum( for $license in $ProvisioningCheckLogic/SLA/resource
                                                              where data($resource/@entry-name) = data($license/@entry-name)
                                                              return
                                                               if( xs:boolean($license) or xs:boolean($license/ancestor::SLA/slaValidation/@result) ) 
											                   then 0
                                                               else 1
											                 )
                                                return $top
                                              }
                                              </tutLicense>
                      let $tutHardware := <tutHardware>
                                              {
                                              attribute slaValidation {xs:boolean("true")},
                                              let $top :=sum(for $hardware in $HardwareCheckLogic/SLA/resource
                                                             where data($resource/@entry-name) = data($hardware/@entry-name)
                                                             return
                                                               if( xs:boolean($hardware) or xs:boolean($hardware/ancestor::SLA/slaValidation/@result)) 
												               then 0
                                                               else 1
											                 )
                                                return $top
                                              }
                                              </tutHardware>
                      let $tutAgent := sum(for $Agent in $AgentCheckLogic/agent
                                           where data($resource/@entry-name) = data($Agent/@entry-name) and data($resource/@id) = data($Agent/@ID)
                                           return
                                             if(data($Agent) = "FALSE") 
    										 then 1
                                             else 0
										   )
                      return
                        let $hardwareLogic := if(xs:boolean($tutHardware/@slaValidation))
                                              then
                                                if( $useMonitoringData and xs:boolean($agentsDoc/agnt:SWAgents/agnt:SWAgent[@id=$resource/@id]/agnt:nrpeAvailable) ) 
                                                then
                                                  if( data($tutHardware) > 0 )
                                                  then false()
                                                  else true()
                                                 else
                                                  true() (://TODO Monitoring datasi yoksa veya kullanilmasin denmis ise hardware acisindan problem yok olarak kabul ediyoruz. Alternatif ? :)
                                              else
                                                false()
                        let $softwareLogic := if(xs:boolean($tutLicense/@slaValidation))
                                              then
                                                if( $useProvisionData ) 
                                                then
                                                  if( data($tutLicense) > 0 )
                                                  then false()
                                                  else true()
                                                 else
                                                  true() (://TODO Provisioning kullanilmasin denmis ise software acisindan problem yok olarak kabul ediyoruz. Alternatif ? :)
                                              else
                                                false()
                        return
                        if(
                             $tutAgent > 0
                          )
						then "FALSE"
                        else if( $hardwareLogic and $softwareLogic ) then "TRUE" else "FALSE"
                    }
                     </rsr:resource>
                }
                </rsr:ResourceAgentList>

                
              let $iste_sonuc := if($reportType = "Result") then $sonuc
                     else
                       if($reportType = "HardwareCheckLogic") then $HardwareCheckLogic
                       else
                         if($reportType = "HardwareCheckLogicDetail") then $HardwareCheck
                         else
                           if($reportType = "ProvisioningCheckLogic") then $ProvisioningCheckLogic
                           else
                             if($reportType = "ProvisioningCheckLogicDetail") then $ProvisioningCheck
                             else
                               if($reportType = "AgentCheckLogic") then $AgentCheckLogic
                               else
                                 if($reportType = "AgentCheckLogicDetail") then $AgentCheck
                                 else ()
                                 
              let $istesonuc := 
                             if($reportType = "AllDetails") 
							 then <sonuc>{($sonuc, $HardwareCheck, $ProvisioningCheck, $AgentCheck, dss:ResourceSLACheck($documentUrl, $gunzaman, 0, $jobPropertiesFuncPass)/SLACheck)}</sonuc>
                             else $iste_sonuc
                             
              return $istesonuc

  return $sonuc
};


declare function dss:SWFindResourcesForAJob($documentUrl as xs:string, $jobPropertiesFuncPass as element(funcp:jobPropFuncPass), $gunzaman as xs:dateTime)
{
	dss:FindResourcesForAJob($documentUrl, $jobPropertiesFuncPass , $gunzaman, 'Result')
};


(: 
local:GetAvailableResourcesList("xmldb:exist://127.0.0.1:8093/exist/xmlrpc/db/apps/tlossw-test", 
<funcp:jobPropFuncPass xmlns:funcp="http://www.likyateknoloji.com/XML_FuncPass_types" ID="204" useSLA="false" useMonitoringData="true" planId="1639">
  <funcp:oSystem>Windows</funcp:oSystem>
  <funcp:SLAId>1</funcp:SLAId>
</funcp:jobPropFuncPass>) 

local:ResourceSLACheck("xmldb:exist://127.0.0.1:8093/exist/xmlrpc/db/apps/tlossw-test", fn:current-dateTime(), 0, "kk")

local:getMonitoringData("xmldb:exist://127.0.0.1:8093/exist/xmlrpc/db/apps/tlossw-test", "All")


local:SWFindResourcesForAJob("xmldb:exist://127.0.0.1:8093/exist/xmlrpc/db/apps/tlossw-test", 
<funcp:jobPropFuncPass xmlns:funcp="http://www.likyateknoloji.com/XML_FuncPass_types" ID="204" useSLA="true" useMonitoringData="true" planId="1641">
  <funcp:oSystem>Windows</funcp:oSystem>
  <funcp:SLAId>1</funcp:SLAId>
</funcp:jobPropFuncPass>,  fn:current-dateTime() )
:)