xquery version "1.0";  
import module namespace lk="http://likya.tlos.com/" at "xmldb:exist://db/tlossw-serkan/modules/moduleAgentOperations.xquery";
declare namespace res = "http://www.likyateknoloji.com/resource-extension-defs";
lk:searchAgent("xmldb:exist://db/tlossw-serkan/xmls/tlosSWAgents10.xml", 
<agnt:SWAgent xmlns:agnt="http://www.likyateknoloji.com/XML_agent_types" xmlns:res="http://www.likyateknoloji.com/resource-extension-defs" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <res:Resource xsi:nil="true"/>
  <agnt:osType xsi:nil="true"/>
  <agnt:nrpePort>0</agnt:nrpePort>
  <agnt:jmxTlsPort>0</agnt:jmxTlsPort>
  <agnt:jmxUser/>
  <agnt:jmxPassword/>
  <agnt:userStopRequest>null</agnt:userStopRequest>
  <agnt:durationForUnavailability>0</agnt:durationForUnavailability>
  <agnt:jobTransferFailureTime>0</agnt:jobTransferFailureTime>
</agnt:SWAgent>)