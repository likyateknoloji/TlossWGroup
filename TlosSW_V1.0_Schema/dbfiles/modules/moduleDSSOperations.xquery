xquery version "1.0";
module namespace dss = "http://tlos.dss.com/";
import module namespace lk = "http://likya.tlos.com/" at "moduleNrpeOperations.xquery";
declare namespace dat = "http://www.likyateknoloji.com/XML_data_types";
declare namespace com = "http://www.likyateknoloji.com/XML_common_types";
declare namespace cal = "http://www.likyateknoloji.com/XML_calendar_types";
declare namespace agnt= "http://www.likyateknoloji.com/XML_agent_types";
declare namespace xsi = "http://www.w3.org/2001/XMLSchema-instance";
declare namespace fn = "http://www.w3.org/2005/xpath-functions";
declare namespace lrns="http://www.likyateknoloji.com/XML_SWResourceNS_types";
declare namespace nrp="http://www.likyateknoloji.com/XML_nrpe_types";
declare namespace nrpr="http://www.likyateknoloji.com/XML_nrpe_results";
declare namespace res="http://www.likyateknoloji.com/resource-extension-defs";
declare namespace sla="http://www.likyateknoloji.com/XML_SLA_types";
declare namespace pp="http://www.likyateknoloji.com/XML_PP_types";


declare namespace functx = "http://www.functx.com"; 
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

declare function dss:ResourceUnique($n as node()) as node()*
{
      let $resources := for $tek in $n/nrpr:nrpeCall
						where $tek/nrpr:message/nrpr:response/nrpr:disk[data(@forWhat)="free"] or 
							  $tek/nrpr:message/nrpr:response/nrpr:mem[data(@forWhat)="free"] or 
                              $tek/nrpr:message/nrpr:response/nrpr:cpu[data(@timein)="1"]
						return <resource entry-name="{ $tek/@entry-name }" 
						                         id="{ $tek/@id }" 
									 inJmxAvailable="{ $tek/@inJmxAvailable }" 
									   JmxAvailable="{ $tek/@JmxAvailable }"
                                       os="{ $tek/@os }">
						                                                  { $tek/nrpr:message/nrpr:response/nrpr:disk[data(@forWhat)="free"] ,
										                                    $tek/nrpr:message/nrpr:response/nrpr:mem[data(@forWhat)="free"] ,
																			$tek/nrpr:message/nrpr:response/nrpr:cpu[data(@timein)="1"]
										                                  } 
                              </resource>
	  let $kaynaklar := <resource_list>{dss:distinct-deep($resources)}</resource_list>

      return $kaynaklar
};

(:
declare function dss:GetAvailableResourcesListxxx($n as node(), $prev as xs:string) as node()*
{
	
  let $nrpeOutput := lk:nrpeOutput()
  let $liste1 := (for $tek in $nrpeOutput/nrp:nrpeCall
                  return 
                   <nrp:nrpeCall entry-name="{$tek/@entry-name}" port="{$tek/@port}">
                   {
				     for $tektek in $tek/nrp:message[1] return $tektek
                   }
                   </nrp:nrpeCall>
  )
  let $liste2 := (for $tek in $nrpeOutput/nrp:nrpeCall
                  return 
                   <nrp:nrpeCall entry-name="{$tek/@entry-name}" port="{$tek/@port}">
                   {
				     (for $tektek in $tek/nrp:message
                     order by $tektek/@time descending return $tektek)[1]
                   }
                   </nrp:nrpeCall>
  )
   return <ss>{$liste1, $liste2}</ss>
};

:)
declare function dss:GetAvailableResourcesList($nrpeDataDocumentUrl as xs:string, $n as node(), $prev as xs:string) as node()*
{
	
  let $nrpeOutput := lk:nrpeOutput($nrpeDataDocumentUrl, 1, 1, "All")
  let $liste1 := (for $tek in $nrpeOutput/nrpr:nrpeCall
                  return 
                   <nrpr:nrpeCall entry-name="{$tek/@entry-name}" port="{$tek/@port}" os="{$tek/@os}">
                   {
				     (: for $tektek in $tek/nrp:message[1] return $tektek :)
				     (for $tektek in $tek/nrpr:message
                     order by $tektek/@time descending return $tektek)[1]
                   }
                   </nrpr:nrpeCall>
  )
  
  let $liste2 := <yy> 
                     {
                       for $kek in doc("//db/TLOSSW/xmls/tlosSWAgents10.xml")/agnt:SWAgents/agnt:SWAgent
                       return 
                         <kk id="{data($kek/@id)}" 
						 entry-name="{$kek/res:Resource/text()}"
						 inJmxAvailable="{ $kek/agnt:inJmxAvailable/text() }"
						 JmxAvailable="{ $kek/agnt:jmxAvailable/text() }"
						 > </kk>
				      }
				 </yy>
(:  let $liste2 := <yy>
	<kk id="0" entry-name="hakan-laptop"/>
	<kk id="1" entry-name="nurkan-laptop"/>
  </yy>:)

  (:let $mega := <mega>{ $liste2, $liste3 } </mega>:)
  let $montoring_last_results := <lrns:ResourceList>{ $liste1 } </lrns:ResourceList>
  let $results :=
   <lrns:ResourceList>
   {
     for $cd in $n/lrns:Resource,
	  $tek in $montoring_last_results/nrpr:nrpeCall,
	  $de in $liste2/*
	 where data($tek/@entry-name)=data($cd/@entry-name) and data($de/@entry-name)=data($cd/@entry-name)
	 return 
	   <nrpr:nrpeCall 
	     entry-name="{$cd/@entry-name}" 
		 id="{$de/@id}"
		 inJmxAvailable="{ $de/@inJmxAvailable }"
	     JmxAvailable="{ $de/@JmxAvailable }"
         os="{$tek/@os}"
	   > { $tek/nrpr:message } </nrpr:nrpeCall>

   }
   </lrns:ResourceList>
(:  let $results2 :=
   <ResourceList>
   {
     for $cd in $liste2/*,
	  $tek in $results/nrpr:nrpeCall
	 where data($tek/@entry-name)=data($cd/@entry-name)
	 return <nrpr:nrpeCall entry-name="{$tek/@entry-name}" id="{$cd/@id}" > { $tek/nrpr:message } </nrpr:nrpeCall>

   }
   </ResourceList>:)
   return dss:ResourceUnique($results)
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

declare function dss:ResourceSLACheck($n as node(), $gununtarihi as xs:dateTime, $prev as xs:string) as node()*
{

  (: let $maxid := 130 :)
  
  let $planCnt := count(doc("//db/TLOSSW/xmls/tlosSWDailyPlan10.xml")/AllPlanParameters/plan)
  let $maxid := if($planCnt = 0) then 0 else max(doc("//db/TLOSSW/xmls/tlosSWDailyPlan10.xml")/AllPlanParameters/plan/@id)
  
  let $zaman := functx:time( fn:hours-from-dateTime($gununtarihi),
                             fn:minutes-from-dateTime($gununtarihi),
                             fn:seconds-from-dateTime($gununtarihi))
							 
(:  let $gununtarihi := fn:dateTime(xs:date("2011-09-30"), xs:time("09:30:10+06:00")) :)

  return
    <ResourceSLACheck>
      {
        for $tek in doc("//db/TLOSSW/xmls/tlosSWSLAs10.xml")/sla:ServiceLevelAgreement/sla:SLA,
            $calendar in doc("//db/TLOSSW/xmls/tlosSWDailyPlan10.xml")/AllPlanParameters/plan[@id = $maxid and data(calID) = data($tek/sla:calendarId)]
        (:    where data($calendar/calID)=data($tek/calendarId):)
        return
          <SLACheck>
		  {
            $tek, 
            <result>{
                     if((data($tek/sla:StartDate) <= $gununtarihi and data($tek/sla:EndDate) > $gununtarihi) and data($calendar/calID) = data($tek/sla:calendarId)
					     and (xs:time($tek/sla:SInterval/sla:startTime) <= xs:time($zaman) and xs:time($tek/sla:SInterval/sla:stopTime) > xs:time($zaman))
					 )  
					 then "TRUE"
                     else "FALSE"
                    }
			</result>
          }</SLACheck>
      }
	</ResourceSLACheck>
};



(: Calistirilacak isin cinsine gore gerekli olan lisans kontrollerini ilgili kaynak dikkate alinarak yapar.
   Return: Lisans problemi yoksa True varsa False doner.
:)


declare function dss:ResourceProvisioningCheck($n as node(), $gunzaman as xs:dateTime, $prev as xs:string) as node()*
{
  (: SLA kosullari kontrol ediliyor :)
  (: Software/license :)
  (:---------------------------- KAYNAK TANIMLA ----------------------------:)
  (: kaynak ismi ve varsa kullanim parametresini alalim :)

  let $kaynaklar := $n

 (:------------------------------------------------------------------------:)

  let $sonuc := <ResourceProvisioningCheck> 
                { (: SLA den kosullari alalim :)
                  for $tek in dss:ResourceSLACheck(doc("//db/TLOSSW/xmls/tlosSWSLAs10.xml"), $gunzaman, "kk")/SLACheck
                         
                   (: --------------------- ATAMALAR ----------------------------------------:)
                   (: SLA den ilgili parametreleri degiskenlere aktaralim (Burada License)   :)
				   
                  let $License_list := $tek/sla:SLA/sla:ResourceReq/sla:Software
                  let $ResourcePool := $tek/sla:SLA/sla:ResourcePool
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
                     <SLA ID="{ $tek/sla:SLA/@ID }">{
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
									 for $donbaba in doc("//db/TLOSSW/xmls/tlosSWProgramProvisioning10.xml")/pp:Licenses/pp:License,
                                         $License in $License_list/sla:Program
                                     where $donbaba/@licenseID = $License/@licenseID
                                     return <ResourcePool>
									        {   
											    $donbaba/@licenseID,
												$donbaba/pp:ResourcePool/pp:Resource, 
												$donbaba/pp:StartDate, 
												$donbaba/pp:EndDate 
											}
											</ResourcePool>
									 }
									 </hepsi>
                           (:--------------------- CEVIRI ve TRANSFORMASYONLAR ----------------------:)
                           (:------------ Kaynak verisinden kullandiklarimizdan, ceviri ve transformayon yapacaklarimiz varsa burada yapalim :)
                           (:------------------------------------------------------------------------:)
                         (: let $gununtarihi := fn:dateTime(xs:date("2011-09-30"), xs:time("09:30:10+06:00")) :)
                         
                         (: ------------------ KONTROL NOKTASI ------------------------------------:)
                         let $resource_SLA_result := for $abc in $ara/ResourcePool
                                                   (:where $resources_data/@entry-name = data($abc/Resource):)
                                                     return
                                                       <license>
						                               { 
                                                         if(data($resources_data/@entry-name) = data($abc/pp:Resource) or 
							                                  fn:count($abc/pp:Resource) = 0)(: Kaynak kistlanmis mi? :)  
						                                 then
                                                           if(data($abc/pp:StartDate) <= $gunzaman and data($abc/pp:EndDate) > $gunzaman) 
							                               then ($abc/@licenseID, <result>TRUE</result>, $abc)
                                                           else ($abc/@licenseID, <result>FALSE</result>, $abc)
                                                         else ($abc/@licenseID, <result>FALSE</result>, $abc)
                                                       }
						                               </license>
                         let $vaybe := <fact> 
						               {
									    $resource_SLA_result,
						               <result> 
  					                   { (: Her bir kaynak icin SLA de kaynak tanimi acisindan problem var mi? ... :)
							             if (sum(for $herbiri in $resource_SLA_result/license
							                     return 
								                 if (data($herbiri/result) = "FALSE")
								                 then 1 
								                 else 0) 
								                = 0
								            ) 
							             then "TRUE" 
							             else "FALSE"
                                        }
						                </result>
										}
										</fact>
						 
                         return $vaybe

                       (:------------------------------------------------------------------------:)
                       }
					 </resource>
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

declare function dss:ResourceHardwareCheck($n as node(), $gunzaman as xs:dateTime, $prev as xs:string) as node()*
{
	(:---------------------------- KAYNAK TANIMLA ----------------------------:)
	(: kaynak ismi ve varsa kullanim parametresini alalim (burada disk) :)

	  let $kaynaklar := $n

    (:------------------------------------------------------------------------:)
  (: SLA kosullari kontrol ediliyor :)
   (: Hardware/disk :)
   let $sonuc := <ResourceHardwareCheck> { (: SLA den kosullari alalim :)
                         for $tek in dss:ResourceSLACheck(doc("//db/TLOSSW/xmls/tlosSWSLAs10.xml"), $gunzaman, "kk")/SLACheck
                         
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
                             <resource entry-name="{ $resources_data/@entry-name }">
                                <result>{
                                          if(data($ResourcePool/Resource) = $resources_data/@entry-name or fn:count($ResourcePool/Resource) = 0) 
										  then "TRUE"
                                          else "FALSE"
                                        }
								</result>
						          {
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
						                                           (if ( $resource_disk_free > $SLA_disk_birim_result ) then "TRUE" else "FALSE" )
						                                       else if ( $resource_disk_free < $SLA_disk_birim_result ) then "TRUE" else "FALSE"  (:Disk condition=LT:)
								  (:return ( $donbaba , <result> { $resource_SLA_result } </result> ):)
								  return <Condition> {$donbaba, <result> { $resource_disk_result } </result>} </Condition> ,

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
						                                         (if ( $resource_mem_free > $SLA_mem_birim_result ) then "TRUE" else "FALSE" )
						                                 else if (     $resource_mem_free < $SLA_mem_birim_result ) then "TRUE" else "FALSE"   (:Mem condition=LT:)
								  
								  return <Condition> {$donbaba, <result> { $resource_mem_result } </result>} </Condition> ,
								  (:------------------------------------------------------------------------:)

								  for $donbaba in $resources_data/nrpr:cpu
								    let $resource_cpu_value :=  data($donbaba)
								    (:------------------------------------------------------------------------:)

								    (: ------------------ KONTROL NOKTASI ------------------------------------:)
						            let $resource_cpu_result := if ($SLA_cpu_condition_lt_result) then (: LT :)
						                                         (: KURAL : Kaynakta CPU kullanimi SLA deki degeri asiyor mu? :)
						                                          (if ( xs:integer($resource_cpu_value) < xs:integer($SLA_cpu_value_result)  ) then "TRUE" else "FALSE" )
						                                        else if (  xs:integer($resource_cpu_value) > xs:integer($SLA_cpu_value_result) ) then "TRUE" else "FALSE"   (:CPU condition=GT:)
								  
								  return <Condition> {$donbaba, <result> { $resource_cpu_result } </result>} </Condition>
								  (:------------------------------------------------------------------------:)
								  } </resource>
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

declare function dss:FindResourcesForAJob($jobPropertiesFuncPass as node(), $gunzaman as xs:dateTime, $prev as xs:string) as node()*
{
  let $AvailableResourceListx := dss:GetAvailableResourcesList(doc("//db/TLOSSW/xmls/tlosSWResources10.xml")/*,'root.') 
  (: let $AvailableResourceListy := $AvailableResourceListx[resource/@os = $jobPropertiesFuncPass/*/text()] :)
  let $par := $jobPropertiesFuncPass/*/text()
  let $AvailableResourceListy := for $d in $AvailableResourceListx/resource
                                 where $d/@os = $par
                                 return $d
  let $AvailableResourceList := if( not(fn:exists($AvailableResourceListy)) ) then <resource_list/> else <resource_list> { $AvailableResourceListy } </resource_list>
  
  let $HardwareCheck         := dss:ResourceHardwareCheck(dss:GetAvailableResourcesListUnique($AvailableResourceList), $gunzaman, 'root.')
  let $HardwareCheckLogic    := <HardwareCheckLogic>
                                { 
                                         for $sira in $HardwareCheck/*
                                         return 
										 <SLA ID="{$sira/@ID}"> 
										 {
										    for $don in $sira/*
											return 
											<resource entry-name="{$don/@entry-name}">
											{
											  (:data($don/Condition/result):)
											   if (sum(for $herbiri in $don/*
											           return 
											            if (data($don/Condition/result) = "FALSE" or data($don/result) = "FALSE")
												        then 1 
												        else 0) 
														= 0
												   ) 
												then "TRUE" 
												else "FALSE"
											}
											</resource>
										 }
										 </SLA>
								} 
								</HardwareCheckLogic>

  let $ProvisioningCheck          := dss:ResourceProvisioningCheck(dss:GetAvailableResourcesListUnique($AvailableResourceList), $gunzaman, 'root.')
  let $ProvisioningCheckLogic    := <ProvisioningCheckLogic>
                               { 
                                 for $sira in $ProvisioningCheck/*
                                 return 
								 <SLA ID="{$sira/@ID}"> 
								 {
								   for $don in $sira/*
								   return 
								   <resource entry-name="{$don/@entry-name}">
								   {
								     if (sum(for $herbiri in $don/*
									         return 
									          if (data($don/license/result) = "FALSE" or data($don/result) = "FALSE")
									          then 1 
									          else 0) 
											 = 0) 
									 then "TRUE" 
									 else "FALSE"
								   }
								   </resource>
								 }
								 </SLA>
							   } 
							   </ProvisioningCheckLogic>
  let $AgentCheck      := $AvailableResourceList
  let $AgentCheckLogic := <AgentCheckLogic>
                          {
                            for $agnt in $AvailableResourceList/*
                            return
                              <agent     ID="{ $agnt/@id }" 
							        entry-name="{ $agnt/@entry-name }" 
								inJmxAvailable="{ $agnt/@inJmxAvailable }" 
								  JmxAvailable="{ $agnt/@JmxAvailable }">
                              {
                                if(sum(for $herbiri in $agnt/*
                                       return
                                         if($agnt/@inJmxAvailable = "false" or $agnt/@JmxAvailable = "false") 
										 then 1
                                         else 0) 
									   = 0) 
								then "TRUE"
                                else "FALSE"
                              }
                              </agent>
                          }
                          </AgentCheckLogic>

  let $sonuc := <rsr:ResourceAgentList 
                    xmlns:rsr="http://www.likyateknoloji.com/XML_ResourceAgent_results" 
					xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
					xsi:schemaLocation="http://www.likyateknoloji.com/XML_ResourceAgent_results tlosSW_RALv_1_0.xsd">
                {
                  for $resource in $AvailableResourceList/*
                  (:for $resource in $LicenseCheckLogic/*:)
                  return
                    <rsr:resource 
					   entry-name="{ $resource/@entry-name }" 
					   agentid="{ $resource/@id }"
					   totalCost="123">
                    {
                      let $tutLicense := sum(for $license in $ProvisioningCheckLogic/SLA/*
                                             where data($resource/@entry-name) = data($license/@entry-name)
                                             return
                                               if(data($license) = "FALSE") 
											   then 1
                                               else 0
											)
                      let $tutHardware := sum(for $hardware in $HardwareCheckLogic/SLA/*
                                              where data($resource/@entry-name) = data($hardware/@entry-name)
                                              return
                                                if(data($hardware) = "FALSE") 
												then 1
                                                else 0
											  )
                      let $tutAgent := sum(for $Agent in $AgentCheckLogic/agent
                                           where data($resource/@entry-name) = data($Agent/@entry-name) and data($resource/@id) = data($Agent/@ID)
                                           return
                                             if(data($Agent) = "FALSE") 
											 then 1
                                             else 0
										   )
                      return
                        if($tutLicense > 0 or $tutHardware > 0 or $tutAgent > 0) 
						then "FALSE"
                        else "TRUE"
                    }
                     </rsr:resource>
                }
                </rsr:ResourceAgentList>
  let $iste_sonuc := if($prev = "Result") then $sonuc
                     else
                       if($prev = "HardwareCheckLogic") then $HardwareCheckLogic
                       else
                         if($prev = "HardwareCheckLogicDetail") then $HardwareCheck
                         else
                           if($prev = "ProvisioningCheckLogic") then $ProvisioningCheckLogic
                           else
                             if($prev = "ProvisioningCheckLogicDetail") then $ProvisioningCheck
                             else
                               if($prev = "AgentCheckLogic") then $AgentCheckLogic
                               else
                                 if($prev = "AgentCheckLogicDetail") then $AgentCheck
                                 else ()
  let $istesonuc := 
                             if($prev = "AllDetails") 
							 then <sonuc>{($sonuc, $HardwareCheck, $ProvisioningCheck, $AgentCheck, dss:ResourceSLACheck(doc("//db/TLOSSW/xmls/tlosSWSLAs10.xml"), $gunzaman, "kk")/SLACheck)}</sonuc>
                             else $iste_sonuc

  return $istesonuc
};


declare function dss:SWFindResourcesForAJob($jobPropertiesFuncPass as node(), $gunzaman as xs:dateTime)
{
	dss:FindResourcesForAJob($jobPropertiesFuncPass , $gunzaman, 'Result')
};


(:dss:GetAvailableResourcesList(doc("//db/TLOSSW/xmls/tlosSWResources10.xml")/*, 'root.'):)
(:let $cikti := dss:GetAvailableResourcesList(doc("//db/TLOSSW/xmls/tlosSWResources.xml")/*, 'root.'):)

(:return dss:hakan($cikti, 'root.'):)
(:return $cikti:)
(:return dss:ResourceUnique($cikti):)
(:return dss:ResourceHardwareCheck($cikti, 'root.'):)
(:return dss:ResourceProvisioningCheck($cikti, 'root.'):)

(: Genel sonuc :)
(:dss:FindResourcesForAJob(<JobList><jobProperties ID="2"/></JobList>, 'Result'):)

(: Donanim detay bilgi :)
(:dss:FindResourcesForAJob(<JobList><jobProperties ID="2"/></JobList>, 'HardwareCheckLogicDetail'):)
(:return dss:FindResourcesForAJob(<JobList><jobProperties ID="2"/></JobList>, 'HardwareCheckLogic'):)

(: Yazilim lisans detay bilgi:)
(:dss:FindResourcesForAJob(<JobList><jobProperties ID="2"/></JobList>, 'ProvisioningCheckLogicDetail'):)
(:return dss:FindResourcesForAJob(<JobList><jobProperties ID="2"/></JobList>, 'ProvisioningCheckLogic'):)
