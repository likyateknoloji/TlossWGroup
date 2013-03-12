xquery version "1.0";
module namespace lk = "http://likya.tlos.com/";
declare namespace nrp = "http://www.likyateknoloji.com/XML_nrpe_types";
declare namespace xpath = "http://www.w3.org/2005/xpath-functions";
declare namespace xs = "http://www.w3.org/2001/XMLSchema";

declare function lk:nrpes() as element()* 
{
	for $nrpes in doc("//db/TLOSSW/xmls/tlosSWNrpeData10.xml")/nrp:NrpeData/nrp:nrpeCall
	return $nrpes
};

declare function lk:searchNrpeCall($entry-name as xs:string, $port as xs:int) 
{
	for $nrpeCall in doc("//db/TLOSSW/xmls/tlosSWNrpeData10.xml")/nrp:NrpeData/nrp:nrpeCall
        where $nrpeCall/@entry-name = $entry-name and $nrpeCall/@port = $port
    return $nrpeCall 
};

declare function lk:countNrpeCall($entry-name as xs:string, $port as xs:int) 
{
	let $call := lk:searchNrpeCall($entry-name, $port)
    let $cnt := count($call)
    return $cnt 
};

declare function lk:insertNrpeCallLock($nrpeCall as element(nrp:nrpeCall))
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWNrpeData10.xml")/nrp:NrpeData, lk:insertNrpeCall($nrpeCall))     
};

declare function lk:insertNrpeCall($nrpeCall as element(nrp:nrpeCall))
{	
    update insert $nrpeCall into doc("xmldb:exist:///db/TLOSSW/xmls/tlosSWNrpeData10.xml")/nrp:NrpeData
};

declare function lk:insertNrpeMessageLock($nrpeCall as element(nrp:nrpeCall))
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWNrpeData10.xml")/nrp:NrpeData, lk:insertNrpeMessage($nrpeCall))     
};

declare function lk:insertNrpeMessage($nrpeCall as element(nrp:nrpeCall))
{	
	let $doc := doc("//db/TLOSSW/xmls/tlosSWNrpeData10.xml")
	for $call in doc("//db/TLOSSW/xmls/tlosSWNrpeData10.xml")/nrp:NrpeData/nrp:nrpeCall
        where $call/@entry-name = $nrpeCall/@entry-name and $call/@port = $nrpeCall/@port
		return  update insert $nrpeCall/nrp:message into $call
};

declare function lk:insertNrpe($nrpeCall as element(nrp:nrpeCall)) 
{
   let $nrpeCount := lk:countNrpeCall($nrpeCall/@entry-name, $nrpeCall/@port)
   let $nrpeInsert := if($nrpeCount > 0) then lk:insertNrpeMessage($nrpeCall)
   else if($nrpeCount = 0) then lk:insertNrpeCallLock($nrpeCall) 
   else ()
   return $nrpeCount 
};

declare function lk:stringToDateTime($t1 as xs:string)
{
    (: Degisik tarih formatlari icin dusunuldu. 2011-10-13T15:08:31+0300 veya 2011-10-13T15:08:31.91+0300 veya 2011-10-13T15:08:31.897+0300 :)
    let $uzunluk := string-length($t1)
    let $baslangic := if($uzunluk eq 24) then 19 else if($uzunluk eq 27) then 22 else if($uzunluk eq 28) then 23 else ()
    
    let $t2 := substring($t1, 1, $baslangic)
    let $t3 := substring($t1, $baslangic+2, 2)
    let $t7 := concat($t2, '+', $t3, ':00')
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

declare function lk:deleteExpiredNrpeMessages($currentTime as xs:string, $expireHour as xs:int)
{	
	let $doc := doc("//db/TLOSSW/xmls/tlosSWNrpeData10.xml")
	for $message in doc("//db/TLOSSW/xmls/tlosSWNrpeData10.xml")/nrp:NrpeData/nrp:nrpeCall/nrp:message
        where lk:dateTimeDifference($message/@time, $currentTime, $expireHour)
		return  update delete $message
};

declare function lk:deleteExpiredNrpeMessagesLock($currentTime as xs:string, $expireHour as xs:int)
{
   util:exclusive-lock(doc("//db/TLOSSW/xmls/tlosSWNrpeData10.xml")/nrp:NrpeData, lk:deleteExpiredNrpeMessages($currentTime, $expireHour))     
};

(:******************************OLDIES*******************************************:)
declare function lk:nrpes2() as element()* 
{
	for $nrpe in doc("//db/TLOSSW/tlosNrpeDataSW.xml")/NrpeData/nrpeCall
	return  $nrpe
};

declare function lk:insertNrpeCall2($nrpe as element())
{	
	update insert $nrpe into doc("xmldb:exist:///db/TLOSSW/tlosNrpeDataSW.xml")/NrpeData
} ;

declare function lk:insertNrpeLock2($nrpe as element())
{
   util:exclusive-lock(doc("//db/TLOSSW/tlosNrpeDataSW.xml")/NrpeData, lk:insertNrpeCall($nrpe))     
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

declare function lk:nrpeOutput($firstElement as xs:int, $lastElement as xs:int, $agentName as xs:string) as node()*
{
let $nrpeData :=
<nrpr:NrpeData xmlns:nrpr="http://www.likyateknoloji.com/XML_nrpe_results" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.likyateknoloji.com/XML_nrpe_results xmldb:exist:///db/TLOSSW/xsds/tlosSWNrpeResult_v_1_0.xsd">
  {
    let $doc := doc("//db/TLOSSW/xmls/tlosSWNrpeData10.xml")
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
                       then (<nrpr:disk for="used" birim="{replace( $response/@value, 
							      '^(.*?)/=(\d{1,8}.?\d{0,3})(G(B)?|M(B)?|K(B)?);(.*?);(.*?);(.*?);(\d{1,5}.?\d{0,3})$', '$3')}">
					   {xs:decimal(replace( $response/@value, 
							      '^(.*?)/=(\d{1,8}.?\d{0,3})(G(B)?|M(B)?|K(B)?);(.*?);(.*?);(.*?);(\d{1,5}.?\d{0,3})$', '$10'))}

                       </nrpr:disk>,
                       <nrpr:disk for="free" birim="{replace( $response/@value, 
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
                            then (<nrpr:mem for="used" birim="{replace( $response/@value, '^(.*?) USED=(\d{1,8}.?\d{0,3})(G(B)?|M(B)?|K(B)?)(.*?)$', '$3')}"> 
							     {xs:decimal(replace( $response/@value, 
							      '^(.*?) USED=(\d{1,8}.?\d{0,3})(G(B)?|M(B)?|K(B)?)(.*?)$', '$2'))}</nrpr:mem>,
                                  <nrpr:mem for="free" birim="{replace( $response/@value, '^(.*?) FREE=(\d{1,8}.?\d{0,3})(G(B)?|M(B)?|K(B)?)(.*?)$', '$3')}"> 
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
                            then (<nrpr:disk for="used" birim="{replace( $response/@value, 
							      '^(.*?)''C:\\''=(\d{1,3}.?\d{0,3})(G(B)?|M(B)?|K(B)?);(.*?)$', '$3')}"> {xs:decimal(replace( $response/@value, 
							      '^(.*?)''C:\\''=(\d{1,3}.?\d{0,3})(G(B)?|M(B)?|K(B)?);(.*?)$', '$2'))}</nrpr:disk>,
                                  <nrpr:disk for="free" birim="{replace( $response/@value, 
							      '^(.*?)''C:\\''=(\d{1,3}.?\d{0,3})(G(B)?|M(B)?|K(B)?);(.*?)$', '$3')}"> {xs:decimal(replace( $response/@value, 
							      '^(.*?)''C:\\''=(\d{1,3}.?\d{0,3})(G(B)?|M(B)?|K(B)?);(.*?);(.*?);(.*?);(\d{1,5}.?\d{0,3})(;| )(.*?)$', '$10')) -
								  xs:decimal(replace( $response/@value, '^(.*?)''C:\\''=(\d{1,3}.?\d{0,3})(G(B)?|M(B)?|K(B)?);(.*?)$', '$2'))}</nrpr:disk>
								  )
                       else if(data($response/@command) = xs:string('alias_mem'))
                            then (<nrpr:mem for="used" birim="{replace( $response/@value, '^(.*?)=(\d{1,3}.?\d{0,3})(G(B)?|M(B)?|K(B)?);(.*?);(.*?);(.*?);(\d{1,5}.?\d{0,3})(; |)$', '$3')}"> 
							{replace( $response/@value, 
							      '^(.*?)=(\d{1,3}.?\d{0,3})(G(B)?|M(B)?|K(B)?);(.*?);(.*?);(.*?);(\d{1,5}.?\d{0,3})(; |)$', '$2')}</nrpr:mem>,
                                  <nrpr:mem for="free" birim="{replace( $response/@value, '^(.*?)=(\d{1,3}.?\d{0,3})(G(B)?|M(B)?|K(B)?);(.*?);(.*?);(.*?);(\d{1,5}.?\d{0,3})(; |)$', '$3')}"> {xs:decimal(replace( $response/@value, 
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
