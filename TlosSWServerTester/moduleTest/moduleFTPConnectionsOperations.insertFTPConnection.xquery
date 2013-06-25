xquery version "1.0";  
import module namespace fc="http://fc.tlos.com/" at "xmldb:exist://db/TLOSSW/modules/moduleFTPConnectionsOperations.xquery";
declare namespace ftp = "http://www.likyateknoloji.com/XML_ftp_adapter_types";
declare namespace com = "http://www.likyateknoloji.com/XML_common_types";
fc:insertFTPConnection("xmldb:exist://db/TLOSSW/xmls/tlosSWSJFTPConnections10.xml", <ftp:ftpProperties xmlns:ftp="http://www.likyateknoloji.com/XML_ftp_adapter_types" xmlns:com="http://www.likyateknoloji.com/XML_common_types" id="19">
  <com:active>yes</com:active>
  <ftp:connection>
    <com:connName>test</com:connName>
    <com:userName>test</com:userName>
    <com:userPassword>test</com:userPassword>
    <ftp:ftpPortNumber>1111</ftp:ftpPortNumber>
    <com:ipAddress>1.2.3.4</com:ipAddress>
  </ftp:connection>
</ftp:ftpProperties>)