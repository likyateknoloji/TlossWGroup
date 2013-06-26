xquery version "1.0";
declare namespace com = "http://www.likyateknoloji.com/XML_common_types";
declare namespace con = "http://www.likyateknoloji.com/XML_config_types";
import module namespace hs="http://hs.tlos.com/" at "xmldb:exist://db/tlossw-serkan/modules//moduleManagementOperations.xquery";
hs:getTlosConfig("//db/tlossw-serkan")