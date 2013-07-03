xquery version "1.0";

module namespace met = "http://meta.tlos.com/";

declare namespace com = "http://www.likyateknoloji.com/XML_common_types";
declare namespace util = "http://exist-db.org/xquery/util";
declare namespace  meta = "http://www.likyateknoloji.com/XML_metaData_types";

(: doc(local:getMetaData("//db/TLOSSW/xmls/metaData.xml", "dbConnectionProfiles")) :)
(: eski versiyon
declare function met:getMetaData($documentUrl as xs:string, $docId as xs:string) as xs:string
{    
    let $docXml := doc($documentUrl)
    let $prefix := $docXml/meta:metaData/meta:dbInfo/meta:prefix/text()
    let $rootCol := $docXml/meta:metaData/meta:dbInfo/meta:rootCollection/text()
    let $subCol := $docXml/meta:metaData/meta:dbInfo/meta:collection[@type eq "xml"]
    let $docName := $docXml/meta:metaData/meta:documentInfo/meta:document[@id eq xs:string($docId) and @type eq "xml"]
	return concat($prefix, $rootCol, '/', $subCol, '/', $docName)
};
:)
declare function met:getMetaData($documentUrl as xs:string, $docId as xs:string) as xs:string?
{    
    let $seperator := "/"
    let $metaDataFile := xs:string("metaData.xml")
    let $xmlCollection := xs:string("xmls")
    let $xslCollection := xs:string("xsls")
    (: let $subCol := $docXml/meta:metaData/meta:dbInfo/meta:collection[@type eq "xml"] :)
    
    let $metaDataFileFull := xs:string(concat($documentUrl, $seperator, $xmlCollection, $seperator, $metaDataFile))
    (:
    let $prefix := $docXml/meta:metaData/meta:dbInfo/meta:prefix/text()
    let $rootCol := $docXml/meta:metaData/meta:dbInfo/meta:rootCollection/text()
    :)
    
    let $result := if (doc($metaDataFileFull)/meta:metaData/meta:documentInfo/meta:document[@id eq xs:string($docId) and @type eq "xml"]) 
    	then 
    		let $docFullName := doc($metaDataFileFull)/meta:metaData/meta:documentInfo/meta:document[@id eq xs:string($docId) and @type eq "xml"]
    		return xs:string(concat($documentUrl, $seperator, $xmlCollection, $seperator, $docFullName)) 
    	else if (doc($metaDataFileFull)/meta:metaData/meta:documentInfo/meta:document[@id eq xs:string($docId) and @type eq "xsl"])
    	then 
    		let $docFullName := doc($metaDataFileFull)/meta:metaData/meta:documentInfo/meta:document[@id eq xs:string($docId) and @type eq "xsl"]
    		return xs:string(concat($documentUrl, $seperator, $xslCollection, $seperator, $docFullName))
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