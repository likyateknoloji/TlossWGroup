(: gunluk calisacak is ve senaryo listesini sorguluyor :)

xquery version "1.0";  
import module namespace hs="http://hs.tlos.com/" at "xmldb:exist://db/TLOSSW/modules/moduleDailyOperations.xquery";
declare namespace com = "http://www.likyateknoloji.com/XML_common_types";
declare namespace dat = "http://www.likyateknoloji.com/XML_data_types";
hs:doPlanAndSelectJobsAndScenarios(0,0)

xquery version "1.0";  
import module namespace hs="http://hs.tlos.com/" at "xmldb:exist://db/tlossw-serkan/modules/moduleDailyOperations.xquery";
declare namespace com = "http://www.likyateknoloji.com/XML_common_types";
declare namespace dat = "http://www.likyateknoloji.com/XML_data_types";
hs:doPlanAndSelectJobsAndScenarios(
"//db/tlossw-serkan/xmls/tlosSWData10.xml",
"//db/tlossw-serkan/xmls/tlosSWDailyScenarios10.xml", 
"xmldb:exist:///db/tlossw-serkan/xmls/tlosSWDailyPlan10.xml", 
"xmldb:exist:///db/tlossw-serkan/xmls/tlosSWCalendar10.xml", 0,0)