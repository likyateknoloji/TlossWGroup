xquery version "1.0";

module namespace met = "http://meta.tlos.com/";

declare namespace com = "http://www.likyateknoloji.com/XML_common_types";
declare namespace util = "http://exist-db.org/xquery/util";
declare namespace  meta = "http://www.likyateknoloji.com/XML_metaData_types";
declare namespace  dat="http://www.likyateknoloji.com/XML_data_types";

(: doc(local:getMetaData("//db/TLOSSW/xmls/metaData.xml", "dbConnectionProfiles")) :)

(: TODO hakan
   xmldb:store a fullpath verdigimizde collection i bulamiyor. o yuzden simdilik xmlrpc den sonraki kismi aliyorum.
   esas       : xmldb:exist://localhost:8093/exist/xmlrpc/db/TLOSSW/xmls/tlosSWData10id5.xml
   kullanilan : /db/TLOSSW/xmls/tlosSWData10id5.xml
:)

declare function met:findDocPath($documentUrl as xs:string, $docId as xs:string, $fullPath as xs:boolean) as xs:string {
  let $seperator := "/"
  let $basePath := xs:string("xmls")
  let $metaDataFile := xs:string("metaData.xml")
  let $metaDataFileFull := xs:string(concat( $documentUrl, $seperator, $basePath, $seperator, $metaDataFile))
  
  let $metaDataFile := doc($metaDataFileFull)
  let $fileName := $metaDataFile/meta:metaData/meta:documentInfo/meta:document[@id eq $docId]
  let $typeData := $metaDataFile/meta:metaData/meta:documentInfo/meta:document[@id eq $docId]/@type
  let $collectionPath := $metaDataFile/meta:metaData/meta:dbInfo/meta:collection[@type eq $typeData]
  
  let $result := if(exists($collectionPath)) 
                  then
                    let $path := concat( $documentUrl, $seperator, $collectionPath, $seperator )
                    let $res := if( $fullPath ) 
                                   then concat( $path , $fileName )
                                   else $path
                    return $res
                  else ""
  return $result
};

declare function met:getScopedDocument($documentUrl as xs:string, $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean, $initialDoc as node()?) as xs:string?
{
  let $docName := met:findDocPath($documentUrl, $docId, true())
  let $result := if( $userId eq "0" or $isGlobal ) 
                 then
    			   let $exist := if(doc-available($docName)) 
                                 then ()
                                 else xmldb:store(met:findDocPath(substring-after($documentUrl, 'xmlrpc'), $docId, false()), $docName, $initialDoc)
                   return $docName
                 else
                   let $userDoc := replace( $docName, "\.xml", concat("id", $userId, ".xml"))
    			   let $exist := if(doc-available($userDoc)) 
                                 then () 
                                 else xmldb:store(met:findDocPath(substring-after($documentUrl, 'xmlrpc'), $docId, false()), $userDoc, $initialDoc)
                   return $userDoc
  return $result
};    

declare function met:getDataDocument($documentUrl as xs:string, $docId as xs:string, $userId as xs:string, $isGlobal as xs:boolean) as xs:string?
{
  
  let $initialDoc := 
  if( not(fn:compare( $docId, "sjData" )) or not(fn:compare( $docId, "deploymentData" )) or not(fn:compare( $docId, "jobTemplates" ))) then
   <dat:TlosProcessData xmlns:pn="http://www.likyateknoloji.com/XML_process_node" xmlns:rs="http://www.likyateknoloji.com/XML_executeRShell_types" xmlns:db="http://www.likyateknoloji.com/XML_db_job_types" xmlns:adp="http://www.likyateknoloji.com/XML_adapter_types" xmlns:com="http://www.likyateknoloji.com/XML_common_types" xmlns:state-types="http://www.likyateknoloji.com/state-types" xmlns:dat="http://www.likyateknoloji.com/XML_data_types" xmlns:par="http://www.likyateknoloji.com/XML_parameters_types" xmlns:lstn="http://www.likyateknoloji.com/XML_listener_types" xmlns:jsdl="http://schemas.ggf.org/jsdl/2005/11/jsdl" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.likyateknoloji.com/XML_data_types ../xsds/tlosSWData_v_1_0.xsd">
    <dat:baseScenarioInfos>
        <com:jsName>Serbest isler</com:jsName>
        <com:comment>Serbest isler burada yer alir</com:comment>
        <dat:jsIsActive>YES</dat:jsIsActive>
        <com:userId>{$userId}</com:userId>
    </dat:baseScenarioInfos>
    <dat:jobList/>
    <dat:timeManagement/>
    <dat:advancedScenarioInfos>
        <com:schedulingAlgorithm>FirstComeFirstServed</com:schedulingAlgorithm>
    </dat:advancedScenarioInfos>
    <dat:concurrencyManagement>
        <com:concurrent>true</com:concurrent>
    </dat:concurrencyManagement>
   </dat:TlosProcessData>
  else  
   if( not(fn:compare( $docId, "scenarios" )) ) then
     <TlosProcessDataAll>
     </TlosProcessDataAll>
     else
	 ()
  
  
  let $result := met:getScopedDocument( $documentUrl, $docId, $userId, $isGlobal, $initialDoc)
      
  return $result
};

declare function met:getMetaData($documentUrl as xs:string, $docId as xs:string) as xs:string?
{    
    
    let $metaDataFileFull := met:findDocPath($documentUrl, xs:string("metaData"), true())
    
    let $result := met:findDocPath($documentUrl, $docId, true())
    
	return $result
};

declare function met:readMetaData($documentUrl as xs:string) as element(meta:metaData)
{    
    
    let $metaDataFileFull := met:findDocPath($documentUrl, xs:string("metaData"), true())
    
    let $result := doc($metaDataFileFull)/meta:metaData
    
	return $result
};

declare function met:insertMetaData($documentUrl as xs:string, $doc as element(meta:document))
{	
    let $metaDataDocumentUrl := met:getMetaData($documentUrl, "metaData")
	
	return update insert $doc into doc($metaDataDocumentUrl)/meta:metaData/meta:dbInfo
} ;

declare function met:insertMetaLock($documentUrl as xs:string, $doc as element(meta:document))
{
   let $metaDataDocumentUrl := met:getMetaData($documentUrl, "metaData")
	
   return util:exclusive-lock(doc($metaDataDocumentUrl)/meta:metaData/documentInfo, met:insertMetaData($documentUrl, $doc))     
};

declare function met:updateMeta($documentUrl as xs:string, $doc as element(meta:document))
{
   let $metaDataDocumentUrl := met:getMetaData($documentUrl, "metaData")
   
	for $d in doc($metaDataDocumentUrl)/meta:metaData/documentInfo/document
	where $d/@id = $doc/@id
	return update replace $d with $doc
};

declare function met:updateMetaLock($documentUrl as xs:string, $doc as element(meta:document))
{
   let $metaDataDocumentUrl := met:getMetaData($documentUrl, "metaData")
   
   return util:exclusive-lock(doc($metaDataDocumentUrl)/meta:metaData/documentInfo/document, met:updateMeta($documentUrl, $doc))     
};

declare function met:deleteMeta($documentUrl as xs:string, $doc as element(meta:document))
{
   let $metaDataDocumentUrl := met:getMetaData($documentUrl, "metaData")
   
	for $d in doc($metaDataDocumentUrl)/meta:metaData/documentInfo/document
	where $d/@id = $doc/@id
	return update delete $d
};

declare function met:deleteMetaLock($documentUrl as xs:string, $doc as element(meta:document))
{
   let $metaDataDocumentUrl := met:getMetaData($documentUrl, "metaData")
   
   return util:exclusive-lock(doc($metaDataDocumentUrl)/meta:metaData/documentInfo/document, met:deleteMeta($documentUrl, $doc))     
};