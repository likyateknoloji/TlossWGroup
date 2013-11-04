<xsl:stylesheet xmlns:dat="http://www.likyateknoloji.com/XML_data_types" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:com="http://www.likyateknoloji.com/XML_common_types" xmlns:state-types="http://www.likyateknoloji.com/state-types" xmlns:cal="http://www.likyateknoloji.com/XML_calendar_types" xmlns:usr="http://www.likyateknoloji.com/XML_user_types" version="1.0">
    <xsl:output method="html"/>
	

<!--	
<style type="text/css">table.sample {    border-width: 1px 1px 1px 1px;    border-spacing: 2px;    border-style: outset outset outset outset;    border-color: gray gray gray gray;    border-collapse: separate;    background-color: white;}
                       table.sample th {    border-width: 1px 1px 1px 1px;    padding: 1px 1px 1px 1px;    border-style: inset inset inset inset;    border-color: gray gray gray gray;    background-color: white;    -moz-border-radius: 0px 0px 0px 0px;}
					   table.sample td {    border-width: 1px 1px 1px 1px;    padding: 1px 1px 1px 1px;    border-style: inset inset inset inset;    border-color: gray gray gray gray;    background-color: white;    -moz-border-radius: 0px 0px 0px 0px;}
</style>


<table class="sample">
<tr>    <th>Header</th>    <td>Content</td></tr>
</table>
-->
    <xsl:template match="/">
        <html xsl:version="1.0" lang="en">
            <style type="text/css">
#tablo1
{
font-family:"Trebuchet MS", Arial, Helvetica, sans-serif;
width:100%;
border-collapse:collapse;
}
#tablo1 td, #tablo1 th 
{
font-size:0.8em;
border:1px solid #6600ff;
padding:3px 7px 2px 7px;
}
#tablo1 th 
{
font-size:1em;
text-align:left;
padding-top:5px;
padding-bottom:4px;
background-color:#99CCFF;
color:#ffffff;
}
#tablo1 tr.alt td 
{
color:#000000;
background-color:#D4EEF8;
}

</style>
            <head>
                <title>Tlos SW is raporu</title>
                <xsl:call-template name="get_css"/>
            </head>
            <body>
                <center>
                    <h1>Tlos SW is raporu</h1>
                </center>
                <xsl:variable name="tlosData" select="dat:TlosProcessData"/>
                <xsl:variable name="tlosCalendar" select="document('tlosSWCalendar10.xml')/cal:calendarList"/>
                <xsl:variable name="tlosUser" select="document('tlosSWUser10.xml')/usr:user-infos"/>
                <h2>1 Giris</h2>
				Bu Dokuman otomatik olarak 
				<i>Tlos SW Dokuman Yoneticisi</i> tarafindan olusturulmustur. 
				
				<p>
                    <h3>1.1 Tanimlar</h3>
                    <br/>
					Job : Birim is anlamindadir. 
					<br/>
					Senaryo : Birbirleri ile iliskili is gruplarini ifade eder. Isler bir senaryo altinda olabilecekleri gibi senaryodan bagimsiz (serbest) isler tanimlamak ta mumkundur. 
					<br/>
                </p>
                <p/>
                <h2>2. Takvim tanimlari</h2>
				Tlos SW'de tanimlanmis isler belirlenen bir takvime gore calistirilir. Cok sayida takvim tanimlamak mumkundur. Asagida tanimlanmis olan takvimler
				listelenmektedir.  
				<p/>
                <table id="tablo1">
                    <tr>
                        <th>Takvim Adi</th>
                        <th>Periyod</th>
                        <th>Tekrar sayisi</th>
                        <th>Takvim Baslangici</th>
                        <th>Takvim Sonu</th>
                        <th>Ozel Gunler</th>
                        <th>Istisna Gunler</th>
                    </tr>
                    <xsl:apply-templates select="$tlosCalendar"/> 
					
					<!-- <xsl:apply-templates select="$tlosData/dat:jobList" mode="liste"/> 
					<xsl:apply-templates select="$tlosData/dat:scenario" mode="liste"/> 
					-->
                </table>
				
				<!-- <xsl:apply-templates select="$tlosCalendar/cal:calendarProperties/cal:calendarName"/>
				<xsl:apply-templates select="$tlosCalendar/cal:calendarProperties/cal:calendarPeriod/com:daySpecial"/>
				<xsl:apply-templates select="$tlosCalendar/cal:calendarProperties/cal:validFrom/com:date"/>
				-->
                <p/>
                <h2>3. Is ve senaryo detaylari</h2>
                <table id="tablo1">
                    <tr>
                        <th>Is ismi</th>
                        <th>Is Tanimi</th>
                        <th>Is Takvimi</th>
                        <th>Planlanan Zaman</th>
                        <th>Is Turu</th>
                        <th>Is Statusu</th>
                    </tr>
                    <tr>
                        <td colspan="8" BGCOLOR="#99CCFF"> Serbest isler  </td>
                    </tr>
                    <xsl:apply-templates select="$tlosData/dat:jobList" mode="liste"/>
                    <xsl:apply-templates select="$tlosData/dat:scenario" mode="liste"/>
                </table>
                <h2>4. Kullanici tanimlari</h2>
				
				Sistemde tanimli kullanicilar asagida listelenmistir. 
				
				<p/>
                <table id="tablo1">
                    <tr>
                        <th>Kullanici Login</th>
                        <th>Isim</th>
                        <th>Soyisim</th>
                        <th>e-posta</th>
                        <th>Yetki seviyesi</th>
                    </tr>
                    <xsl:apply-templates select="$tlosUser/usr:userList"/>
                </table>
                <p/>
                <h2>5. Is ve Senaryolarin farkli sekillerde gosterimleri</h2>
                <h3>2.1 Is ve Senaryo agaci</h3>
				Asagida sistemde tanimli is ve senaryolar agac yapisi seklinde ifade edilmislerdir. Senaryodan bagimsiz serbest isler en basta listelenmektedirler.
				<p/>
                <ul>
                    <xsl:apply-templates select="$tlosData/dat:jobList"/>
                    <xsl:apply-templates select="$tlosData/dat:scenario"/>
                </ul>
                <h3>2.2 Is ve senaryo kutu gosterimi</h3>
				Is ve senaryolar ic ice tanimlanabilirler. Asagida mevcut is ve senaryolarin bu sekilde ifade edilmeleri temsil edilmektedir. 
				<p/>
                <table border="2">
                    <xsl:apply-templates select="$tlosData/dat:jobList" mode="kutular"/>
                    <xsl:apply-templates select="$tlosData/dat:scenario" mode="kutular"/>
                </table>
            </body>
        </html>
    </xsl:template>

	<!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 
		Template MAIN -->
    <xsl:template name="main">
        <xsl:param name="lang"/>
        <div>
            <xsl:attribute name="id">lang_<xsl:value-of select="$lang"/>
            </xsl:attribute>
        </div>
    </xsl:template>
    
    <xsl:template match="dat:jobList">
        <ol>
            <hr align="left" size="2" width="150"/>
            <xsl:apply-templates select="dat:jobProperties"/>
            <hr align="left" size="2" width="150"/>
        </ol>
    </xsl:template>
    <xsl:template match="dat:jobList" mode="liste">
        <xsl:apply-templates select="dat:jobProperties" mode="liste"/>
    </xsl:template>
    <xsl:template match="dat:scenario">
        <ul>
            <li>
                <b>
                    <em>
                        <xsl:value-of select="dat:baseScenarioInfos/com:jsName"/>
                    </em>
                </b>
            </li>
            <xsl:apply-templates select="dat:jobList"/>
            <xsl:apply-templates select="dat:scenario"/>
			<!-- scenario varsa tr konacak kontrolu lazim -->
        </ul>
    </xsl:template>
    <xsl:template match="dat:scenario" mode="liste">
        <td colspan="8" BGCOLOR="#99CCFF">
            <xsl:value-of select="dat:baseScenarioInfos/com:jsName"/>
        </td>
        <xsl:apply-templates select="dat:jobList" mode="liste"/>
        <xsl:apply-templates select="dat:scenario" mode="liste"/>
    </xsl:template>
    <xsl:template match="dat:jobProperties">
        <ul>
            <li>
                <table>
                    <td>
                        <xsl:value-of select="dat:baseJobInfos/com:jsName"/>
                    </td>
                </table>
            </li>
        </ul>
    </xsl:template>
    <xsl:template match="dat:jobProperties" mode="liste">
        <tr>
            <td>
                <xsl:value-of select="dat:baseJobInfos/com:jsName"/>
            </td>
            <td>
                <xsl:value-of select="dat:baseJobInfos/com:comment"/>
            </td>
            <td>
                <xsl:value-of select="dat:baseJobInfos/dat:calendarId"/>
            </td>
            <td>
                <xsl:value-of select="dat:timeManagement/dat:jsPlannedTime/dat:startTime/com:time"/>
            </td>
            <td>
               <xsl:value-of select="dat:baseJobInfos/dat:jobInfos/com:jobTypeDetails/com:jobCommandType"/>
            </td>
            <td>
                <xsl:value-of select="state-types:LiveStateInfos"/>
            </td>
        </tr>
    </xsl:template>
    <xsl:template match="dat:jobList" mode="kutular">
        <xsl:apply-templates select="dat:jobProperties" mode="kutular"/>
    </xsl:template>
    <xsl:template match="dat:scenario" mode="kutular">
        <tr>
            <td>
                <b>
                    <em>
                        <xsl:value-of select="dat:baseScenarioInfos/com:jsName"/>
                    </em>
                </b>
            </td>
            <td>
                <table border="3">
                    <xsl:apply-templates select="dat:jobList" mode="kutular"/>
                    <xsl:apply-templates select="dat:scenario" mode="kutular"/>
					<!-- scenario varsa tr konacak kontrolu lazim -->
                </table>
            </td>
        </tr>
    </xsl:template>
    <xsl:template match="dat:jobProperties" mode="kutular">
        <tr>
            <td>
                <xsl:value-of select="dat:baseJobInfos/com:jsName"/>
            </td>
            <td/>
        </tr>
	
		<!-- <xsl:if test="dat:jobPlannedTime/dat:startTime/com:time/com:hour > 
			0"> <xsl:attribute name="style"> <xsl:text>color:red</xsl:text> </xsl:attribute> 
			</xsl:if> <xsl:value-of select="dat:jobPlannedTime/dat:startTime/com:time/com:hour"/> 
			</td> <td> <xsl:value-of select="position()"/> </td> -->
    </xsl:template>
    <xsl:template match="usr:person">
        <tr>
            <td>
                <xsl:value-of select="com:userName"/>
            </td>
            <td>
                <xsl:value-of select="com:name"/>
            </td>
            <td>
                <xsl:value-of select="com:surname"/>
            </td>
            <td>
                <xsl:value-of select="com:email"/>
            </td>
            <td>
                <xsl:value-of select="com:role"/>
            </td>
        </tr>
    </xsl:template>
    <xsl:template match="cal:calendarProperties">
        <tr>
            <td>
                <xsl:value-of select="cal:calendarName"/>
            </td>
            <td>
                <xsl:value-of select="cal:calendarPeriod"/>
            </td>
            <td>
                <xsl:value-of select="cal:validFrom"/>
            </td>
            <td>
                <xsl:value-of select="cal:validTo"/>
            </td>
            <td>
                <xsl:value-of select="cal:specificDays"/>
            </td>
            <td>
                <xsl:value-of select="cal:exceptionDays"/>
            </td>
        </tr>
    </xsl:template> 

	
	<!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 
		Template get_css -->
    <xsl:template name="get_css">
        <style type="text/css"><![CDATA[

      body { background-color:#FFFFFF; margin-left:20px; font-family:verdana,arial,sans-serif;font-size:10pt; }

    ]]></style>
    </xsl:template>
</xsl:stylesheet>