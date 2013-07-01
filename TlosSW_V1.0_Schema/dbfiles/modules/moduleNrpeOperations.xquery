xquery version "1.0";

module namespace lk = "http://likya.tlos.com/";

import module namespace met = "http://meta.tlos.com/" at "moduleMetaDataOperations.xquery";

declare namespace nrp   = "http://www.likyateknoloji.com/XML_nrpe_types";
declare namespace xpath = "http://www.w3.org/2005/xpath-functions";
declare namespace xs    = "http://www.w3.org/2001/XMLSchema";

(:
Mapping
$nrpeDataDocumentUrl = doc("//db/TLOSSW/xmls/tlosSWNrpeData10.xml")

:)

declare function lk:nrpes($documentUrl as xs:string) as element()* 
{
   let $nrpeDataDocumentUrl := met:getMetaData($documentUrl, "nrpeData")
   
	for $nrpes in doc($nrpeDataDocumentUrl)/nrp:NrpeData/nrp:nrpeCall
	return $nrpes
};

declare function lk:searchNrpeCall($documentUrl as xs:string, $entry-name as xs:string, $port as xs:int) 
{
   let $nrpeDataDocumentUrl := met:getMetaData($documentUrl, "nrpeData")
   
	for $nrpeCall in doc($nrpeDataDocumentUrl)/nrp:NrpeData/nrp:nrpeCall
        where $nrpeCall/@entry-name = $entry-name and $nrpeCall/@port = $port
    return $nrpeCall 
};

declare function lk:countNrpeCall($documentUrl as xs:string, $entry-name as xs:string, $port as xs:int) 
{
	let $call := lk:searchNrpeCall($documentUrl, $entry-name, $port)
    let $cnt := count($call)
    return $cnt 
};

declare function lk:insertNrpeCallLock($documentUrl as xs:string, $nrpeCall as element(nrp:nrpeCall))
{
   let $nrpeDataDocumentUrl := met:getMetaData($documentUrl, "nrpeData")
   
   return util:exclusive-lock(doc($nrpeDataDocumentUrl)/nrp:NrpeData, lk:insertNrpeCall($documentUrl, $nrpeCall))     
};

declare function lk:insertNrpeCall($documentUrl as xs:string, $nrpeCall as element(nrp:nrpeCall))
{	
   let $nrpeDataDocumentUrl := met:getMetaData($documentUrl, "nrpeData")
   
   return update insert $nrpeCall into doc($nrpeDataDocumentUrl)/nrp:NrpeData
};

declare function lk:insertNrpeMessageLock($documentUrl as xs:string, $nrpeCall as element(nrp:nrpeCall))
{
   let $nrpeDataDocumentUrl := met:getMetaData($documentUrl, "nrpeData")
   
   return util:exclusive-lock(doc($nrpeDataDocumentUrl)/nrp:NrpeData, lk:insertNrpeMessage($documentUrl, $nrpeCall))     
};

declare function lk:insertNrpeMessage($documentUrl as xs:string, $nrpeCall as element(nrp:nrpeCall))
{	
   let $nrpeDataDocumentUrl := met:getMetaData($documentUrl, "nrpeData")
   
	let $doc := doc($nrpeDataDocumentUrl)
	for $call in doc($nrpeDataDocumentUrl)/nrp:NrpeData/nrp:nrpeCall
        where $call/@entry-name = $nrpeCall/@entry-name and $call/@port = $nrpeCall/@port
		return  update insert $nrpeCall/nrp:message into $call
};

declare function lk:insertNrpe($documentUrl as xs:string, $nrpeCall as element(nrp:nrpeCall)) 
{
   let $nrpeCount := lk:countNrpeCall($documentUrl, $nrpeCall/@entry-name, $nrpeCall/@port)
   let $nrpeInsert := if($nrpeCount > 0) then lk:insertNrpeMessage($documentUrl, $nrpeCall)
   else if($nrpeCount = 0) then lk:insertNrpeCallLock($documentUrl, $nrpeCall) 
   else ()
   return $nrpeCount 
};

declare function lk:stringToDateTime($t1 as xs:string)
{
    (: Degisik tarih formatlari icin dusunuldu. 2011-10-13T15:08:31+0300 veya 2011-10-13T15:08:31.91+0300 veya 2011-10-13T15:08:31.897+0300 :)
    
    let $t2 := substring-before($t1, '+')
    let $t3 := substring-after($t1, '+')
    let $t7 := concat($t2, '+', substring($t3,1,2) , ':00')
    let $t8 := xs:dateTime($t7)
    
    return $t8
};

declare function lk:dateTimeDifference($time1 as xs:string, $time2 as xs:string, $df as xs:int)
{ 
   let $diff := lk:stringToDateTime($time2) - lk:stringToDateTime($time1)
   let $tdiffHour := fn:hours-from-duration($diff)
   let $diffResult := if($tdiffHour > $df or $tdiffHour = $df) then true()
   else (false())

   return $diffResult
};

declare function lk:deleteExpiredNrpeMessages($documentUrl as xs:string, $currentTime as xs:string, $expireHour as xs:int)
{	
   let $nrpeDataDocumentUrl := met:getMetaData($documentUrl, "nrpeData")
   
	let $doc := doc($nrpeDataDocumentUrl)
	for $message in doc($nrpeDataDocumentUrl)/nrp:NrpeData/nrp:nrpeCall/nrp:message
        where lk:dateTimeDifference($message/@time, $currentTime, $expireHour)
		return  update delete $message
};

declare function lk:deleteExpiredNrpeMessagesLock($documentUrl as xs:string, $currentTime as xs:string, $expireHour as xs:int)
{
   let $nrpeDataDocumentUrl := met:getMetaData($documentUrl, "nrpeData")
   
   return util:exclusive-lock(doc($nrpeDataDocumentUrl)/nrp:NrpeData, lk:deleteExpiredNrpeMessages($documentUrl, $currentTime, $expireHour))     
};

(:******************************OLDIES*******************************************:)
declare function lk:nrpes2($documentUrl as xs:string ) as element()* 
{
   let $nrpeDataDocumentUrl := met:getMetaData($documentUrl, "nrpeData")
   
	for $nrpe in doc($nrpeDataDocumentUrl)/NrpeData/nrpeCall
	return  $nrpe
};

declare function lk:insertNrpeCall2($documentUrl as xs:string, $nrpe as element())
{	
   let $nrpeDataDocumentUrl := met:getMetaData($documentUrl, "nrpeData")
   
   return update insert $nrpe into doc($nrpeDataDocumentUrl)/NrpeData
} ;

declare function lk:insertNrpeLock2($documentUrl as xs:string, $nrpe as element())
{
   let $nrpeDataDocumentUrl := met:getMetaData($documentUrl, "nrpeData")
   
   return util:exclusive-lock(doc($nrpeDataDocumentUrl)/NrpeData, lk:insertNrpeCall($documentUrl, $nrpe))     
};

(:**********************************OLDIES END*************************************:)


(:
The following examples exhibit the use of sub-expressions: 
replace('Chap 2...Chap 3...Chap 4...',
        'Chap (\d)', 'Sec $1.0')
 Sec 2.0...Sec 3.0...Sec 4.0...
 
replace('abc123', '([a-z])', '$1x')
 axbxcx123
 
replace('2315551212',
        '(\d{3})(\d{3})(\d{4})', '($1) $2-$3')
 (231) 555-1212
 
replace('2006-10-18',
        '\d{2}(\d{2})-(\d{2})-(\d{2})',
        '$2/$3/$1')
 10/18/06
 
replace('25', '(\d+)', '\$$1.00')
 $25.00
 
:)

declare function lk:replace-first($arg as xs:string?, $pattern as xs:string, $replacement as xs:string) as xs:string
{
  replace($arg, concat('(^.*?)', $pattern), concat('$1', $replacement))
};

declare function lk:index-of-match-first($arg as xs:string?, $pattern as xs:string) as xs:integer?
{
  if(matches($arg, $pattern)) then string-length(tokenize($arg, $pattern)[1]) + 1
  else ()
};

declare function lk:get-matches-and-non-matches($string as xs:string?, $regex as xs:string) as element()*
{
  let $iomf := lk:index-of-match-first($string, $regex)
  return
    if(empty($iomf)) then <non-match>{ $string }</non-match>
    else
      if($iomf > 1) then (<non-match>{ substring($string, 1, $iomf - 1) }</non-match>, lk:get-matches-and-non-matches(substring($string, $iomf), $regex))
      else
        let $length := string-length($string) - string-length(lk:replace-first($string, $regex, ''))
        return
          (<match>{ substring($string, 1, $length) }</match>, 
           if(string-length($string) > $length) then lk:get-matches-and-non-matches(substring($string, $length + 1), $regex)
           else ())
};

declare function lk:get-matches($string as xs:string?, $regex as xs:string) as xs:string*
{
  lk:get-matches-and-non-matches($string, $regex)/string(self::match)
};

declare function lk:substring-before-last-match 
  ( $arg as xs:string? ,
    $regex as xs:string )  as xs:string? {
       
   replace($arg,concat('^(.*)',$regex,'.*'),'$1')
 } ;

declare function lk:substring-after-last-match 
  ( $arg as xs:string? ,
    $regex as xs:string )  as xs:string {
       
   replace($arg,concat('^.*',$regex),'')
 } ;

declare function lk:nrpeOutput($documentUrl as xs:string, $firstElement as xs:int, $lastElement as xs:int, $agentName as xs:string) as node()*
{
   let $nrpeDataDocumentUrl := met:getMetaData($documentUrl, "nrpeData")
   
let $nrpeData :=
<nrpr:NrpeData xmlns:nrpr="http://www.likyateknoloji.com/XML_nrpe_results" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.likyateknoloji.com/XML_nrpe_results ../xsds/tlosSWNrpeResult_v_1_0.xsd">
  {
    let $doc := doc($nrpeDataDocumentUrl)
    for $nrpeCall in $doc/nrp:NrpeData/nrp:nrpeCall
	where $agentName = xs:string("All") or $nrpeCall/@entry-name = $agentName
    return (: Her bir kaynak icin :)
      <nrpr:nrpeCall entry-name="{ $nrpeCall/@entry-name }" port="{ $nrpeCall/@port }" os="{ $nrpeCall/@os }">
        {
		  let $realFirstElement := count($nrpeCall/*) + 1 - $firstElement
		  let $realLastElement := $realFirstElement + 1 - $lastElement
		  
          for $message in $nrpeCall/nrp:message[position() = ($realLastElement to $realFirstElement)]
          order by $message/@id descending
          return (: Her bir mesaj icin :)
            <nrpr:message time="{ $message/@time }">
              {
                for $response in $message/nrp:response
                return (: Her bir response icin :)
                  <nrpr:response command="{ data($response/@command) }">
				    {
                       if (data($response/@command) = xs:string('check_disk')) 
                       then (<nrpr:disk forWhat="used" birim="{replace( $response/@value, 
							      '^(.*?)/=(\d{1,8}.?\d{0,3})(G(B)?|M(B)?|K(B)?);(.*?);(.*?);(.*?);(\d{1,5}.?\d{0,3})$', '$3')}">
					   {xs:decimal(replace( $response/@value, 
							      '^(.*?)/=(\d{1,8}.?\d{0,3})(G(B)?|M(B)?|K(B)?);(.*?);(.*?);(.*?);(\d{1,5}.?\d{0,3})$', '$10'))}

                       </nrpr:disk>,
                       <nrpr:disk forWhat="free" birim="{replace( $response/@value, 
							      '^(.*?)/=(\d{1,8}.?\d{0,3})(G(B)?|M(B)?|K(B)?);(.*?);(.*?);(.*?);(\d{1,5}.?\d{0,3})$', '$3')}">
					     {xs:decimal(replace( $response/@value, 
							      '^(.*?)/=(\d{1,8}.?\d{0,3})(G(B)?|M(B)?|K(B)?);(.*?);(.*?);(.*?);(\d{1,5}.?\d{0,3})$', '$10')) - 
								  xs:decimal(replace( $response/@value, 
							      '^(.*?)/=(\d{1,8}.?\d{0,3})(G(B)?|M(B)?|K(B)?);(.*?)$', '$2'))}
								  </nrpr:disk>)
                       else if(data($response/@command) = xs:string('check_moad'))
                            then (<nrpr:cpu timein="1" birim="%"> {xs:decimal(replace( $response/@value, 
							      '^(.*?): (\d{1,3}.\d{1,3}), (\d{1,3}.\d{1,3}), (\d{1,3}.\d{1,3})\|(.*?)$', '$2'))*100}</nrpr:cpu>,
                                  <nrpr:cpu timein="5" birim="%"> {xs:decimal(replace( $response/@value, 
								  '^(.*?): (\d{1,3}.\d{1,3}), (\d{1,3}.\d{1,3}), (\d{1,3}.\d{1,3})\|(.*?)$', '$3'))*100}</nrpr:cpu>,
                                  <nrpr:cpu timein="15" birim="%"> {xs:decimal(replace( $response/@value, 
								   '^(.*?): (\d{1,3}.\d{1,3}), (\d{1,3}.\d{1,3}), (\d{1,3}.\d{1,3})\|(.*?)$', '$4'))*100}</nrpr:cpu>
								  )
                       else if(data($response/@command) = xs:string('check_mem'))
                            then (<nrpr:mem forWhat="used" birim="{replace( $response/@value, '^(.*?) USED=(\d{1,8}.?\d{0,3})(G(B)?|M(B)?|K(B)?)(.*?)$', '$3')}"> 
							     {xs:decimal(replace( $response/@value, 
							      '^(.*?) USED=(\d{1,8}.?\d{0,3})(G(B)?|M(B)?|K(B)?)(.*?)$', '$2'))}</nrpr:mem>,
                                  <nrpr:mem forWhat="free" birim="{replace( $response/@value, '^(.*?) FREE=(\d{1,8}.?\d{0,3})(G(B)?|M(B)?|K(B)?)(.*?)$', '$3')}"> 
								  {xs:decimal(replace( $response/@value, 
							      '^(.*?) FREE=(\d{1,8}.?\d{0,3})(G(B)?|M(B)?|K(B)?)(.*?)$', '$2'))}</nrpr:mem>
								  )
                       else if(data($response/@command) = xs:string('alias_cpu'))
                            then (<nrpr:cpu timein="1" birim="%"> {replace( $response/@value, 
							      '^(.*?)''1m''=(\d{1,3})%(.*?)$', '$2')}</nrpr:cpu>,
                                  <nrpr:cpu timein="5" birim="%"> {replace( $response/@value, 
							      '^(.*?)''5m''=(\d{1,3})%(.*?)$', '$2')}</nrpr:cpu>,
                                  <nrpr:cpu timein="15" birim="%"> {replace( $response/@value, 
							      '^(.*?)''15m''=(\d{1,3})%(.*?)$', '$2')}</nrpr:cpu>
								  )
                       else if(data($response/@command) = xs:string('alias_disk'))
                            then (<nrpr:disk forWhat="used" birim="{replace( $response/@value, 
							      '^(.*?)''C:\\''=(\d{1,3}.?\d{0,3})(G(B)?|M(B)?|K(B)?);(.*?)$', '$3')}"> {xs:decimal(replace( $response/@value, 
							      '^(.*?)''C:\\''=(\d{1,3}.?\d{0,3})(G(B)?|M(B)?|K(B)?);(.*?)$', '$2'))}</nrpr:disk>,
                                  <nrpr:disk forWhat="free" birim="{replace( $response/@value, 
							      '^(.*?)''C:\\''=(\d{1,3}.?\d{0,3})(G(B)?|M(B)?|K(B)?);(.*?)$', '$3')}"> {xs:decimal(replace( $response/@value, 
							      '^(.*?)''C:\\''=(\d{1,3}.?\d{0,3})(G(B)?|M(B)?|K(B)?);(.*?);(.*?);(.*?);(\d{1,5}.?\d{0,3})(;| )(.*?)$', '$10')) -
								  xs:decimal(replace( $response/@value, '^(.*?)''C:\\''=(\d{1,3}.?\d{0,3})(G(B)?|M(B)?|K(B)?);(.*?)$', '$2'))}</nrpr:disk>
								  )
                       else if(data($response/@command) = xs:string('alias_mem'))
                            then (<nrpr:mem forWhat="used" birim="{replace( $response/@value, '^(.*?)=(\d{1,3}.?\d{0,3})(G(B)?|M(B)?|K(B)?);(.*?);(.*?);(.*?);(\d{1,5}.?\d{0,3})(; |)$', '$3')}"> 
							{replace( $response/@value, 
							      '^(.*?)=(\d{1,3}.?\d{0,3})(G(B)?|M(B)?|K(B)?);(.*?);(.*?);(.*?);(\d{1,5}.?\d{0,3})(; |)$', '$2')}</nrpr:mem>,
                                  <nrpr:mem forWhat="free" birim="{replace( $response/@value, '^(.*?)=(\d{1,3}.?\d{0,3})(G(B)?|M(B)?|K(B)?);(.*?);(.*?);(.*?);(\d{1,5}.?\d{0,3})(; |)$', '$3')}"> {xs:decimal(replace( $response/@value, 
							      '^(.*?)=(\d{1,3}.?\d{0,3})(G(B)?|M(B)?|K(B)?);(.*?);(.*?);(.*?);(\d{1,5}.?\d{0,3})(; |)$', '$10')) - xs:decimal(replace( $response/@value, 
							      '^(.*?)=(\d{1,3}.?\d{0,3})(G(B)?|M(B)?|K(B)?);(.*?);(.*?);(.*?);(\d{1,5}.?\d{0,3})(; |)$', '$2'))}</nrpr:mem>
								  )
                      else ()
			
                 }

				 </nrpr:response>
              }
            </nrpr:message>
        }
      </nrpr:nrpeCall>
  }
</nrpr:NrpeData>

return $nrpeData
};
