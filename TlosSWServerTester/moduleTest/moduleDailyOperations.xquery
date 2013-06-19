(: gunluk calisacak is ve senaryo listesini sorguluyor :)

xquery version "1.0";  
import module namespace hs="http://hs.tlos.com/" at "xmldb:exist://db/TLOSSW/modules/moduleDailyOperations.xquery";
declare namespace com = "http://www.likyateknoloji.com/XML_common_types";
declare namespace dat = "http://www.likyateknoloji.com/XML_data_types";
hs:doPlanAndSelectJobsAndScenarios(0,0)