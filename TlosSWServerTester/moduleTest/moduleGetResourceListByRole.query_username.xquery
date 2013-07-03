xquery version "1.0";
import module namespace hs="http://hs.tlos.com/" at "xmldb:exist://127.0.0.1:8093/exist/xmlrpc/db/TLOSSW/modules/moduleGetResourceListByRole.xquery";
hs:query_username("xmldb:exist://127.0.0.1:8093/exist/xmlrpc/db/TLOSSW", xs:string("admin"))