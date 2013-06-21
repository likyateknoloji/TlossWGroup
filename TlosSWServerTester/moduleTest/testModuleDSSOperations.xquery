xquery version "1.0";  
import module namespace dss="http://tlos.dss.com/" at "xmldb:exist://db/tlossw-serkan/modules/moduleDSSOperations.xquery";
declare namespace dat = "http://www.likyateknoloji.com/XML_data_types";
declare namespace com = "http://www.likyateknoloji.com/XML_common_types";
declare namespace cal  = "http://www.likyateknoloji.com/XML_calendar_types";
declare namespace agnt = "http://www.likyateknoloji.com/XML_agent_types";
declare namespace xsi  = "http://www.w3.org/2001/XMLSchema-instance";
declare namespace fn   = "http://www.w3.org/2005/xpath-functions";
declare namespace lrns   = "www.likyateknoloji.com/XML_SWResourceNS_types";
declare namespace nrp   = "www.likyateknoloji.com/XML_nrpe_types";
declare namespace res = "http://www.likyateknoloji.com/resource-extension-defs";
dss:SWFindResourcesForAJob(<jobPropFuncPass xmlns="http://www.likyateknoloji.com/XML_FuncPass_types" ID="16">
  <oSystem>Windows</oSystem>
</jobPropFuncPass>, fn:current-dateTime())