xquery version "1.0";
module namespace hs = "http://hs.tlos.com/";
declare namespace fo="http://www.w3.org/1999/XSL/Format";
declare namespace xslfo="http://exist-db.org/xquery/xslfo";
declare namespace dat="http://www.likyateknoloji.com/XML_data_types";
declare namespace com="http://www.likyateknoloji.com/XML_common_types";
import module namespace transform="http://exist-db.org/xquery/transform";

declare function hs:tlosXMLTransformXsl()
{
	let $table := doc("xmldb:exist://db/TLOSSW/xsls/tlosXMLTransformXsl.xsl")
	return $table
};

declare function hs:tlosJobTransformXsl()
{
	let $table := doc("xmldb:exist://db/TLOSSW/xsls/tlosJobTransform.xsl")
	return $table
};

declare function hs:tlosDataXslFo()
{
	let $table := doc("xmldb:exist://db/TLOSSW/xsls/tlosDataFo.xsl")
	return $table
};

declare function hs:tlosDataXsl()
{
	let $table := doc("xmldb:exist://db/TLOSSW/xsls/tlosData.xsl")
	return $table
};

declare function hs:tlosData()
{
	let $table := doc("xmldb:exist://db/TLOSSW/xmls/tlosSWData10.xml")
	return $table
};

declare function hs:tlosCalendar()
{
	let $table := doc("xmldb:exist://db/TLOSSW/xmls/tlosSWCalendar10.xml")
	return $table
};

declare function hs:tlosUser()
{
	let $table := doc("xmldb:exist://db/TLOSSW/xmls/tlosSWUser10.xml")
	return $table
};

(: xml dosyalarini birlestirip sorguladigim metot, simdi kullanilmiyor :)
(:
declare function hs:generateXslDoc(){ 

	let $tlosDataTable := doc("xmldb:exist://db/TLOSSW/xmls/tlosSWData10.xml")
	let $tlosDataTable-fo := transform:transform($tlosDataTable,doc("xmldb:exist://db/TLOSSW/xsls/tlosData.xsl"),())

	let $tlosCalendarTable := doc("xmldb:exist://db/TLOSSW/xmls/tlosSWCalendar10.xml")
	let $tlosCalendarTable-fo := transform:transform($tlosCalendarTable,doc("xmldb:exist://db/TLOSSW/xsls/tlosCalendar.xsl"),())

	let $tlosUserTable := doc("xmldb:exist://db/TLOSSW/xmls/tlosSWUser10.xml")
	let $tlosUserTable-fo := transform:transform($tlosUserTable,doc("xmldb:exist://db/TLOSSW/xsls/tlosUser.xsl"),())

	let $fo := <fo:root xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="hello" page-height="11in" page-width="8.5in" margin-top="1in" margin-bottom="1in" margin-left="1in" margin-right="1in">
                    <fo:region-body margin-top="1in" margin-bottom=".5in"/>
                    <fo:region-before extent=".5in" background-color="silver"/>
                    <fo:region-after extent=".5in" background-color="silver"/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="hello">
                <fo:static-content flow-name="xsl-region-before">
                    <fo:block font-size="24pt">Likya Teknoloji</fo:block>
                </fo:static-content>
                <fo:static-content flow-name="xsl-region-after">
                    <fo:block font-size="24pt" text-align="right">
    					Page <fo:page-number/> of <fo:page-number-citation ref-id="last-page"/>
                    </fo:block>
                </fo:static-content>
                <fo:flow flow-name="xsl-region-body">
                    <fo:block font-size="18pt" text-align="center" font-weight="bold">
						
						{$tlosDataTable-fo}
						----------------------------------------------------------------------------------
						{$tlosCalendarTable-fo}
						----------------------------------------------------------------------------------
						{$tlosUserTable-fo}
							
                    </fo:block>
                    <fo:block id="last-page"/>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    
	let $pdf := xslfo:render($fo, "application/pdf", ())
	return $pdf
};
:)