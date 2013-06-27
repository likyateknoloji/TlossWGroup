xquery version "1.0";declare namespace com = "http://www.likyateknoloji.com/XML_common_types";
declare namespace dat = "http://www.likyateknoloji.com/XML_data_types";import module namespace hs="http://hs.tlos.com/" at "xmldb:exist://db/tlossw-serkan/modules//moduleDailyOperations.xquery";
hs:doPlanAndSelectJobsAndScenarios("//db/tlossw-serkan", 0, 0)
