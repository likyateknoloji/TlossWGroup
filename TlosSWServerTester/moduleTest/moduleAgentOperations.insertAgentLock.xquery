xquery version "1.0";  
import module namespace lk="http://likya.tlos.com/" at "xmldb:exist://db/TLOSSW/modules/moduleAgentOperations.xquery";
declare namespace res="http://www.likyateknoloji.com/resource-extension-defs";
lk:insertAgentLock("xmldb:exist://db/TLOSSW/xmls/metaData.xml", <agnt:SWAgent xmlns:agnt="http://www.likyateknoloji.com/XML_agent_types" xmlns:res="http://www.likyateknoloji.com/resource-extension-defs">
  <res:Resource>merve-laptop</res:Resource>
  <agnt:osType>Windows</agnt:osType>
  <agnt:agentType>agent</agnt:agentType>
  <agnt:nrpePort>4444</agnt:nrpePort>
  <agnt:jmxTlsPort>5559</agnt:jmxTlsPort>
  <agnt:jmxUser>admin</agnt:jmxUser>
  <agnt:jmxPassword>4444</agnt:jmxPassword>
  <agnt:inJmxAvailable>false</agnt:inJmxAvailable>
  <agnt:outJmxAvailable>false</agnt:outJmxAvailable>
  <agnt:jmxAvailable>false</agnt:jmxAvailable>
  <agnt:userStopRequest>null</agnt:userStopRequest>
  <agnt:nrpeAvailable>false</agnt:nrpeAvailable>
  <agnt:lastHeartBeatTime>0</agnt:lastHeartBeatTime>
  <agnt:durationForUnavailability>900</agnt:durationForUnavailability>
  <agnt:lastJobTransfer>false</agnt:lastJobTransfer>
  <agnt:jobTransferFailureTime>0</agnt:jobTransferFailureTime>
  <agnt:workspacePath>C:/</agnt:workspacePath>
</agnt:SWAgent>)
