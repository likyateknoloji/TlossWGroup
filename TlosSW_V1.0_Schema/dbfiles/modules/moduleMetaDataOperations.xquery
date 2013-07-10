xquery version "1.0";

module namespace met = "http://meta.tlos.com/";

declare namespace com = "http://www.likyateknoloji.com/XML_common_types";
declare namespace util = "http://exist-db.org/xquery/util";
declare namespace  meta = "http://www.likyateknoloji.com/XML_metaData_types";
declare namespace  dat="http://www.likyateknoloji.com/XML_data_types";

(: doc(local:getMetaData("//db/TLOSSW/xmls/metaData.xml", "dbConnectionProfiles")) :)

declare function met:getScenariosDocument($documentUrl as xs:string, $userId as xs:string, $whichData as xs:string) as xs:string?
{
  let $initialDoc :=
   <TlosProcessDataAll>
   </TlosProcessDataAll>

  let $docName := met:getMetaData($documentUrl, "scenarios")
  let $result := if( $userId eq "0" or not(fn:compare( $whichData, "globaldata" )) ) 
                 then
                   $docName
                 else
                   let $userDoc := replace( $docName, "\.", concat("id", $userId, "."))
    			   let $exist := if(doc-available($userDoc)) 
                                 then () 
                                 else xmldb:store(met:xmlCollectionLocation($documentUrl), $userDoc, $initialDoc)
                   return $userDoc
  return $result
};

declare function met:getDataDocument($documentUrl as xs:string, $userId as xs:string, $whichData as xs:string) as xs:string?
{
  let $initialDoc :=
   <dat:TlosProcessData>
   </dat:TlosProcessData>
   
  let $docName := met:getMetaData($documentUrl, "sjData")
  let $result := if( $userId eq "0" or not(fn:compare( $whichData, "globaldata" )) ) 
                 then
                   $docName
                 else
                   let $userDoc := replace( $docName, "\.", concat("id", $userId, "."))
    			   let $exist := if(doc-available($userDoc)) 
                                 then () 
                                 else xmldb:store(met:xmlCollectionLocation($documentUrl), $userDoc, $initialDoc)
                   return $userDoc
  return $result
};


declare function met:xmlCollectionLocation($documentUrl as xs:string) as xs:string
{
  let $seperator := "/"
  let $xmlCollection := xs:string("xmls")
  return xs:string(concat($documentUrl, $seperator, $xmlCollection, $seperator))
};

declare function met:xslCollectionLocation($documentUrl as xs:string) as xs:string
{
  let $seperator := "/"
  let $xslCollection := xs:string("xsls")
  return xs:string(concat($documentUrl, $seperator, $xslCollection, $seperator))
};

declare function met:getMetaData($documentUrl as xs:string, $docId as xs:string) as xs:string?
{    
    let $metaDataFile := xs:string("metaData.xml")
    (: let $subCol := $docXml/meta:metaData/meta:dbInfo/meta:collection[@type eq "xml"] :)
    
    let $metaDataFileFull := xs:string(concat( met:xmlCollectionLocation($documentUrl), $metaDataFile))
    (:
    let $prefix := $docXml/meta:metaData/meta:dbInfo/meta:prefix/text()
    let $rootCol := $docXml/meta:metaData/meta:dbInfo/meta:rootCollection/text()
    :)
    
    let $result := if (doc($metaDataFileFull)/meta:metaData/meta:documentInfo/meta:document[@id eq xs:string($docId) and @type eq "xml"]) 
        then 
    		let $docFullName := doc($metaDataFileFull)/meta:metaData/meta:documentInfo/meta:document[@id eq xs:string($docId) and @type eq "xml"]
    		return xs:string(concat( met:xmlCollectionLocation($documentUrl), $docFullName))
    	else if (doc($metaDataFileFull)/meta:metaData/meta:documentInfo/meta:document[@id eq xs:string($docId) and @type eq "xsl"])
    	then 
    		let $docFullName := doc($metaDataFileFull)/meta:metaData/meta:documentInfo/meta:document[@id eq xs:string($docId) and @type eq "xsl"]
    		return xs:string(concat( met:xslCollectionLocation($documentUrl), $docFullName))
    	else ()
    
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