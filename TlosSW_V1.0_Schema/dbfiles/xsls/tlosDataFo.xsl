<xsl:stylesheet xmlns:dat="http://www.likyateknoloji.com/XML_data_types" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:com="http://www.likyateknoloji.com/XML_common_types" xmlns:usr="http://www.likyateknoloji.com/XML_user_types" xmlns:cal="http://www.likyateknoloji.com/XML_calendar_types" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.1" exclude-result-prefixes="fo">
    <xsl:template match="root">
        <xsl:variable name="tlosCalendar" select="document('tlosSWCalendar10.xml')/cal:calendarList"/>
        <xsl:variable name="tlosUser" select="document('tlosSWUser10.xml')/usr:user-infos"/>
        <xsl:variable name="tlosData" select="dat:TlosProcessData"/>
        <fo:root>
            <fo:layout-master-set>
                <fo:simple-page-master master-name="my-page" page-height="210mm" page-width="297mm" margin="1cm">
                    <fo:region-body margin="1in" margin-bottom=".5in"/>
                    <fo:region-before extent=".5in" background-color="silver"/>
                    <fo:region-after extent=".5in" background-color="silver"/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="my-page">
                <fo:static-content flow-name="xsl-region-before">
                    <fo:block font-size="18pt" text-align="center" alignment-adjust="central">Tlos SW Mevcut Isler Raporu</fo:block>
                </fo:static-content>
                <fo:static-content flow-name="xsl-region-after">
                    <fo:block font-size="14pt" text-align="right">
					Page <fo:page-number/> of <fo:page-number-citation ref-id="last-page"/>
                    </fo:block>
                </fo:static-content>
                <fo:flow flow-name="xsl-region-body">
                    <fo:block>
                        <fo:leader leader-length="9in" leader-pattern="rule" rule-thickness="2pt" color="blue"/>
                    </fo:block>
                    <fo:block font-size="16pt" text-align="center">	
				TAKVIM LISTESI
				</fo:block>
                    <fo:table>
                        <fo:table-column column-number="1"/>
                        <fo:table-column column-number="2"/>
                        <fo:table-column column-number="3"/>
                        <fo:table-column column-number="4"/>
                        <fo:table-column column-number="5"/>
                        <fo:table-column column-number="6"/>
                        <fo:table-column column-number="7"/>
                        <fo:table-body>
                            <fo:table-row>
                                <fo:table-cell column-number="1" display-align="before">
                                    <fo:block> TAKVIM ADI </fo:block>
                                </fo:table-cell>
                                <fo:table-cell column-number="2" display-align="before">
                                    <fo:block>PERIYOD</fo:block>
                                </fo:table-cell>
                                <fo:table-cell column-number="3" display-align="before">
                                    <fo:block>TEKRAR</fo:block>
                                </fo:table-cell>
                                <fo:table-cell column-number="4" display-align="before">
                                    <fo:block>BASLANGIC</fo:block>
                                </fo:table-cell>
                                <fo:table-cell column-number="5" display-align="before">
                                    <fo:block>SON</fo:block>
                                </fo:table-cell>
                                <fo:table-cell column-number="6" display-align="before">
                                    <fo:block>OZEL GUNLER</fo:block>
                                </fo:table-cell>
                                <fo:table-cell column-number="7" display-align="before">
                                    <fo:block>ISTISNA GUNLER</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>
                    <fo:block>
                        <fo:leader leader-length="9in" leader-pattern="rule" rule-thickness="1pt" color="black"/>
                    </fo:block>
                    <xsl:apply-templates select="$tlosCalendar"/>
                    <fo:block>
                        <fo:leader leader-length="9in" leader-pattern="rule" rule-thickness="2pt" color="blue"/>
                    </fo:block>
                    <fo:block font-size="16pt" text-align="center">	
				KULLANICI LISTESI
				</fo:block>
                    <fo:table>
                        <fo:table-column column-number="1"/>
                        <fo:table-column column-number="2"/>
                        <fo:table-column column-number="3"/>
                        <fo:table-column column-number="4"/>
                        <fo:table-body>
                            <fo:table-row>
                                <fo:table-cell column-number="1" display-align="before">
                                    <fo:block> KULLANICI ADI </fo:block>
                                </fo:table-cell>
                                <fo:table-cell column-number="2" display-align="before">
                                    <fo:block>ISIM</fo:block>
                                </fo:table-cell>
                                <fo:table-cell column-number="3" display-align="before">
                                    <fo:block>SOYISIM</fo:block>
                                </fo:table-cell>
                                <fo:table-cell column-number="4" display-align="before">
                                    <fo:block>YETKI</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>
                    <fo:block>
                        <fo:leader leader-length="9in" leader-pattern="rule" rule-thickness="1pt" color="black"/>
                    </fo:block>
                    <xsl:apply-templates select="$tlosUser/usr:userList"/>
                    <fo:block>
                        <fo:leader leader-length="9in" leader-pattern="rule" rule-thickness="2pt" color="blue"/>
                    </fo:block>
                    <fo:block font-size="16pt" text-align="center">	
				ISLERIN LISTESI
				</fo:block>
                    <fo:table>
                        <fo:table-column column-number="1"/>
                        <fo:table-column column-number="2"/>
                        <fo:table-column column-number="3"/>
                        <fo:table-column column-number="4"/>
                        <fo:table-column column-number="5"/>
                        <fo:table-column column-number="6"/>
                        <fo:table-body>
                            <fo:table-row>
                                <fo:table-cell column-number="1" display-align="before">
                                    <fo:block> IS ISMI </fo:block>
                                </fo:table-cell>
                                <fo:table-cell column-number="2" display-align="before">
                                    <fo:block>IS TANIMI</fo:block>
                                </fo:table-cell>
                                <fo:table-cell column-number="3" display-align="before">
                                    <fo:block>IS TAKVIMI</fo:block>
                                </fo:table-cell>
                                <fo:table-cell column-number="4" display-align="before">
                                    <fo:block>PLANLANAN ZAMAN</fo:block>
                                </fo:table-cell>
                                <fo:table-cell column-number="5" display-align="before">
                                    <fo:block>IS TURU</fo:block>
                                </fo:table-cell>
                                <fo:table-cell column-number="6" display-align="before">
                                    <fo:block>IS STATUSU</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>
                    <fo:block>
                        <fo:leader leader-length="9in" leader-pattern="rule" rule-thickness="1pt" color="black"/>
                    </fo:block>
                    <xsl:apply-templates select="$tlosData/dat:jobList"/>
                    <xsl:apply-templates select="$tlosData/dat:scenario"/>
                    <fo:block>
                        <fo:leader leader-length="9in" leader-pattern="rule" rule-thickness="2pt" color="blue"/>
                    </fo:block>
                    <fo:block id="last-page"/>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
    <xsl:attribute-set name="inside-table">
        <xsl:attribute name="start-indent">0pt</xsl:attribute>
        <xsl:attribute name="text-align">start</xsl:attribute>
    </xsl:attribute-set>
    <xsl:template match="p">
        <fo:block>
            <xsl:apply-templates/>
        </fo:block>
    </xsl:template>
    <xsl:template match="table">
        <fo:table-and-caption text-align="center" start-indent="100pt">
            <xsl:apply-templates/>
        </fo:table-and-caption>
    </xsl:template>
    <xsl:template match="caption">
        <fo:table-caption xsl:use-attribute-sets="inside-table">
            <xsl:apply-templates/>
        </fo:table-caption>
    </xsl:template>
    <xsl:template match="tgroup">
        <fo:table width="{@width}" table-layout="fixed">
            <xsl:apply-templates/>
        </fo:table>
    </xsl:template>
    <xsl:template match="colspec">
        <fo:table-column column-width="{@colwidth}">
            <xsl:attribute name="column-number">
                <xsl:number count="colspec"/>
            </xsl:attribute>
        </fo:table-column>
    </xsl:template>
    <xsl:template match="tbody">
        <fo:table-body xsl:use-attribute-sets="inside-table">
            <xsl:apply-templates/>
        </fo:table-body>
    </xsl:template>
    <xsl:template match="row">
        <fo:table-row>
            <xsl:apply-templates/>
        </fo:table-row>
    </xsl:template>
    <xsl:template match="entry">
        <fo:table-cell>
            <xsl:apply-templates/>
        </fo:table-cell>
    </xsl:template>
    <xsl:template match="cal:calendarProperties">
        <fo:table>
            <fo:table-column column-number="1"/>
            <fo:table-column column-number="2"/>
            <fo:table-column column-number="3"/>
            <fo:table-column column-number="4"/>
            <fo:table-column column-number="5"/>
            <fo:table-column column-number="6"/>
            <fo:table-column column-number="7"/>
            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell column-number="1" display-align="before">
                        <fo:block>
                            <xsl:value-of select="cal:calendarName"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell column-number="1" display-align="before">
                        <fo:block>
                            <xsl:value-of select="cal:calendarPeriod"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell column-number="1" display-align="before">
                        <fo:block>
                            <xsl:value-of select="cal:validFrom"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell column-number="1" display-align="before">
                        <fo:block>
                            <xsl:value-of select="cal:validTo"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell column-number="1" display-align="before">
                        <fo:block>
                            <xsl:value-of select="cal:specificDays"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell column-number="1" display-align="before">
                        <fo:block>
                            <xsl:value-of select="cal:exceptionDays"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
    </xsl:template>
    <xsl:template match="usr:person">
        <fo:table>
            <fo:table-column column-number="1"/>
            <fo:table-column column-number="2"/>
            <fo:table-column column-number="3"/>
            <fo:table-column column-number="4"/>
            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell column-number="1" display-align="before">
                        <fo:block>
                            <xsl:value-of select="com:userName"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell column-number="2" display-align="before">
                        <fo:block>
                            <xsl:value-of select="com:name"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell column-number="3" display-align="before">
                        <fo:block>
                            <xsl:value-of select="com:surname"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell column-number="4" display-align="before">
                        <fo:block>
                            <xsl:value-of select="com:role"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
    </xsl:template>
    <xsl:template match="dat:jobList">
        <xsl:apply-templates select="dat:jobProperties"/>
    </xsl:template>
    <xsl:template match="dat:scenario">
        <xsl:apply-templates select="dat:jobList"/>
        <xsl:apply-templates select="dat:scenario"/>
    </xsl:template>
    <xsl:template match="dat:jobProperties">
        <fo:table>
            <fo:table-column column-number="1"/>
            <fo:table-column column-number="2"/>
            <fo:table-column column-number="3"/>
            <fo:table-column column-number="4"/>
            <fo:table-column column-number="5"/>
            <fo:table-column column-number="6"/>
            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell column-number="1" display-align="before">
                        <fo:block>
                            <xsl:value-of select="dat:baseJobInfos/com:jsName"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell column-number="2" display-align="before">
                        <fo:block>
                            <xsl:value-of select="dat:baseJobInfos/com:comment"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell column-number="3" display-align="before">
                        <fo:block>
                            <xsl:value-of select="dat:baseJobInfos/dat:calendarId"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell column-number="4" display-align="before">	
                        <fo:block>
                            <xsl:value-of select="dat:timeManagement/dat:jsPlannedTime/dat:startTime/com:time"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell column-number="5" display-align="before">
                        <fo:block>
                            <xsl:value-of select="dat:baseJobInfos/dat:jobInfos/com:jobTypeDetails/com:jobCommandType"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell column-number="6" display-align="before">
                        <fo:block>
			<!-- 				<xsl:value-of select="state-types:LiveStateInfos"/> -->
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
    </xsl:template>
</xsl:stylesheet>