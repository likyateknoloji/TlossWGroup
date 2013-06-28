xquery version "1.0";
declare namespace dbc = "http://www.likyateknoloji.com/XML_dbconnection_types";
declare namespace com = "http://www.likyateknoloji.com/XML_common_types";
import module namespace db="http://db.tlos.com/" at "xmldb:exist://db/TLOSSW/modules/moduleDBConnectionsOperations.xquery";
db:searchDbAccessProfile("//db/TLOSSW", <dbc:dbConnectionProfile xmlns:dbc="http://www.likyateknoloji.com/XML_dbconnection_types" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:com="http://www.likyateknoloji.com/XML_common_types">
  <dbc:dbDefinitionId xsi:nil="true"/>
  <com:userName/>
  <dbc:deployed xsi:nil="true"/>
  <com:active xsi:nil="true"/>
</dbc:dbConnectionProfile>)