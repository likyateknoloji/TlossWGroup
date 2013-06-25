xquery version "1.0";  
import module namespace lk="http://likya.tlos.com/" at "xmldb:exist://db/TLOSSW/modules/moduleAgentOperations.xquery";
lk:getAgents("xmldb:exist://db/TLOSSW/xmls/metaData.xml")