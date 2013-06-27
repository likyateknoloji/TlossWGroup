xquery version "1.0";
declare namespace res = "http://www.likyateknoloji.com/resource-extension-defs";import module namespace lk="http://likya.tlos.com/" at "xmldb:exist://db/tlossw-serkan/modules//moduleAgentOperations.xquery";
lk:updateJmxValueLock("//db/tlossw-serkan", 1, true(), "outJMX")