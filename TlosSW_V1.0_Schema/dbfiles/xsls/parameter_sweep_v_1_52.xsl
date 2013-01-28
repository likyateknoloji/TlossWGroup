
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:par="http://www.likyateknoloji.com/XML_parameters_types" xmlns:dat="http://www.likyateknoloji.com/XML_data_types" xmlns:saxon="http://saxon.sf.net/"
                xmlns:com="http://www.likyateknoloji.com/XML_common_types" xmlns:jsdl="http://schemas.ggf.org/jsdl/2005/11/jsdl" xmlns:jsdl-posix="http://schemas.ggf.org/jsdl/2005/11/jsdl-posix" xmlns:sweep="http://schemas.ogf.org/jsdl/2009/03/sweep"
                xmlns:sweepfunc="http://schemas.ogf.org/jsdl/2009/03/sweep/functions" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:functx="http://www.functx.com">
	<xsl:output method="xml"/>

	<xsl:variable name="sonucDeger" as="xs:string?"/>
    <xsl:variable name="kontext" select="@*|node()"/>

<!-- gfd 149 un bir implemantasyonu icin;
     http://projects.arcs.org.au/trac/grisu/wiki/GrisuCloudService/JSDL-->
	<!--JP yi yeniden olusturalim-->
	<xsl:template match="*|@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>

	<!--com:jobTypeDetails in icinde PS icin kullanacagimiz iki tag var.-->
	<xsl:template match="com:jobTypeDetails">
		<xsl:apply-templates select="@*|node()"/>
		<xsl:apply-templates select="jsdl:JobDescription"/>
		<xsl:apply-templates select="sweep:Sweep"/>
	</xsl:template>

	<!--Uygulama ve argumanlarinin komut satirinda calistirilacak sekilde birlestirilmesi icin JobDescription i kullaniyoruz-->
	<xsl:template match="jsdl:JobDescription">
		<xsl:variable name="komut" select="//jsdl-posix:POSIXApplication/jsdl-posix:Executable/text()"/>

		<xsl:call-template name="for.loop">
			<xsl:with-param name="i">1</xsl:with-param>
			<xsl:with-param name="count">
				<xsl:value-of select="count(//jsdl-posix:POSIXApplication/jsdl-posix:Argument)"/>
			</xsl:with-param>
			<xsl:with-param name="value">//jsdl-posix:POSIXApplication/jsdl-posix:Argument</xsl:with-param>
			<xsl:with-param name="fullText">
				<xsl:value-of select="$komut"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="sweep:Sweep">
		<xsl:variable name="komut" select="//jsdl-posix:POSIXApplication/jsdl-posix:Executable/text()"/>

		<xsl:variable name="seqOfApp">
			<xsl:sequence select="//jsdl-posix:POSIXApplication"/>
		</xsl:variable>

		<xsl:call-template name="sweeps">
			<xsl:with-param name="numberOfSweep" select="count(sweep:Sweep)"/>
			<xsl:with-param name="depthOfSweep" select="1"/>
			<xsl:with-param name="nodeOfAssignment" select="sweep:Assignment"/>
			<xsl:with-param name="nodeOfSweep" select="sweep:Sweep"/>
			<xsl:with-param name="seqOfApp" select="$seqOfApp"/>
			<!--<xsl:with-param name="command" select="concat('concat(','''', $komut, '''', ',', $arguman1, ',', $arguman2, ')')"/>-->
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="sweeps">
		<xsl:param name="numberOfSweep" select="0"/>
		<xsl:param name="depthOfSweep" select="0"/>
		<xsl:param name="nodeOfAssignment" select="."/>
		<xsl:param name="nodeOfSweep" select="."/>
		<xsl:param name="seqOfApp" select="."/>
		<!--<xsl:param name="command" select="."/>-->

		<xsl:text>&#xA; (number, depth) Of Sweep (</xsl:text>
		<xsl:value-of select="$numberOfSweep"/>
		<xsl:text>, </xsl:text>
		<xsl:value-of select="$depthOfSweep"/>
		<xsl:text>)</xsl:text>

		<!-- sweep:Assignment var mi? ilk sweep de assignment olmaz, sonraki herbir sweep de enaz bir tane olur !! -->
		<xsl:if test="exists($nodeOfAssignment)">
			<xsl:call-template name="sweep:Assignment">
				<xsl:with-param name="nodeOfAssignment" select="$nodeOfAssignment"/>
				<xsl:with-param name="seqOfApp" select="$seqOfApp"/>
			</xsl:call-template>
		</xsl:if>

		<!-- Baska sweep:Sweep var mi? ilk sweep den sonra bir noktada sweep olmayacak !! -->
		<xsl:if test="exists($nodeOfSweep)">
			<xsl:call-template name="sweeps">
				<xsl:with-param name="numberOfSweep" select="count($nodeOfSweep/sweep:Sweep)"/>
				<xsl:with-param name="depthOfSweep" select="1 + $depthOfSweep"/>
				<xsl:with-param name="nodeOfAssignment" select="$nodeOfSweep/sweep:Assignment"/>
				<xsl:with-param name="nodeOfSweep" select="$nodeOfSweep/sweep:Sweep"/>
				<xsl:with-param name="seqOfApp" select="$seqOfApp"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--PS de job coklama kosullarinin verildigi yer assignments dir.-->
	<xsl:template name="sweep:Assignment">
		<xsl:param name="nodeOfAssignment" select="."/>
		<xsl:param name="seqOfApp" select="."/>
		<!--	
		   DocumentNode 1 .. N
           Values 1
        -->
		<xsl:text>&#xA; Assignment icindeyim !! </xsl:text>
		<xsl:text>&#xA;</xsl:text>
		<xsl:variable name="sequenceOfMathces">
			<xsl:sequence select="$nodeOfAssignment/sweep:DocumentNode/sweep:Match"/>
		</xsl:variable>

		<xsl:variable name="valueOfMatch">
			<xsl:sequence select="for $i in $sequenceOfMathces return $i"/>
			<!--saxon:evaluate($i)-->
		</xsl:variable>

		<xsl:variable name="sequenceOfValues">
			<xsl:sequence select="$nodeOfAssignment/sweepfunc:Values/sweepfunc:Value"/>
		</xsl:variable>

		<xsl:variable name="sequenceOfAssignment">
			<xsl:sequence select="     
			  for $argument in $seqOfApp,        
			      $match in $valueOfMatch,                                                     
				  $value in $sequenceOfValues                            
				  return ($argument, $match, $value)"/>
		</xsl:variable>

		<xsl:sequence select="$sequenceOfAssignment"/>

		<xsl:text>&#xA; Assignment bitti !! </xsl:text>
		<xsl:text>&#xA;</xsl:text>

        <xsl:call-template name="ParameterSweep">
		   <xsl:with-param name="sequenceOfAssignment" select="$sequenceOfAssignment"/>
		</xsl:call-template>
		<xsl:text>&#xA; Parameter Sweep !! </xsl:text>
		<xsl:text>&#xA;</xsl:text>
	</xsl:template>

	<xsl:template name="ParameterSweep">
       <xsl:param name="sequenceOfAssignment" select="."/>
		<xsl:variable name="sequenceOfPOSIXApplication">
			<xsl:sequence select="     
			  for $result in $sequenceOfAssignment/*                           
				  return if($result instance of element(jsdl-posix:POSIXApplication)) then $result else ()"/>
		</xsl:variable>
		<xsl:variable name="sequenceOfMatch">
			<xsl:sequence select="     
			  for $result in $sequenceOfAssignment/*                           
				  return if($result instance of element(sweep:Match)) then $result else ()"/>
		</xsl:variable>
		<xsl:variable name="sequenceOfValue">
			<xsl:sequence select="     
			  for $result in $sequenceOfAssignment/*                           
				  return if($result instance of element(sweepfunc:Value)) then $result else ()"/>
		</xsl:variable>
		<xsl:text>&#xA;1x</xsl:text>
        <xsl:sequence select="$sequenceOfPOSIXApplication"/>
		<xsl:text>&#xA;2x</xsl:text>
		<xsl:sequence select="$sequenceOfMatch"/>
		<xsl:text>&#xA;3x</xsl:text>
		<xsl:sequence select="$sequenceOfValue"/>
		<xsl:text>&#xA;</xsl:text>

		<xsl:text>&#xA; ISLEM </xsl:text>

		<xsl:variable name="sequenceOfTest">
			<xsl:sequence select="
			  for $test in $sequenceOfMatch                         
				  return $test/sweep:Match"/><!--saxon:evaluate(concat($argument,$match))-->
		</xsl:variable>
        <xsl:sequence select="$sequenceOfTest"/>
        <xsl:text>&#xA; VVV </xsl:text>

		<xsl:variable name="sequenceOfTest3">
			<xsl:sequence select="     
			  for $test3 in $sequenceOfValue                  
				  return $test3/sweepfunc:Value"/><!--saxon:evaluate(concat($argument,$match))-->
		</xsl:variable>
        <xsl:sequence select="$sequenceOfTest3"/>
        <xsl:text>&#xA; VVV3 </xsl:text>

		<xsl:variable name="sequenceOfXXX">
			<xsl:sequence select="   
			  for $match in (1 to count($sequenceOfMatch/sweep:Match)),                                                             
				  $value in $sequenceOfValue/*                           
				  return saxon:evaluate($sequenceOfMatch/sweep:Match[$match])"/>
		</xsl:variable>
		<xsl:sequence select="$sequenceOfXXX/jsdl-posix:Argument[1]"/>
<xsl:text>&#xA;111111</xsl:text>

		<xsl:for-each select="$sequenceOfMatch/sweep:Match">
		  <xsl:variable name="F1" select="."/>
		  <xsl:for-each select="$sequenceOfValue">
		   <xsl:variable name="F2" select="."/>
<xsl:text>&#xA;</xsl:text>
              <xsl:sequence select="$kontext"/>
		 </xsl:for-each>
		</xsl:for-each>

		<xsl:text>&#xA;</xsl:text>

	</xsl:template>

	<xsl:template name="dots">

		<xsl:param name="numberOfParameter" select="1"/>
		<xsl:param name="paramValue" select="X"/>
		<xsl:param name="nodeValue" select="Y"/>

		<xsl:if test="$numberOfParameter &gt; 0">

			<!--			<xsl:text>&#xA; NumberOfParameters </xsl:text>
			<xsl:value-of select="$numberOfParameter"/>
			<xsl:text>&#xA; Parametre Bilgisi </xsl:text>
			<xsl:sequence select="$paramValue[$numberOfParameter]"/>
			<xsl:text>&#xA; Node Value </xsl:text>
			<xsl:value-of select="$nodeValue"/>-->

			<!--<xsl:text>&#xA; Parametre degeri ;   </xsl:text>-->
			<xsl:variable name="param" select="concat('$(',$paramValue[$numberOfParameter]/par:name/text(),')')"/>
			<xsl:variable name="paramEscaped" select="$param"/>
			<!--<xsl:value-of select="$param"/>-->
			<!--<xsl:text> = </xsl:text>-->
			<xsl:variable name="value" select="$paramValue[$numberOfParameter]/par:valueString/text()"/>

			<!--<xsl:value-of select="concat('''', $value, '''')"/>-->

			<xsl:variable name="par" select="$nodeValue"/>

			<!--<xsl:text>&#xA; SONUC = </xsl:text>-->

			<xsl:variable name="modifiedNode">
				<xsl:choose>
					<xsl:when test="contains($par , $param )">
						<!--ss><xsl:value-of select="translate($par , $param, concat($value,'$1'))"/></ss-->
						<!--replace(string, regex, regex-replace)-->

						<xsl:variable name="sonucDeger">
							<xsl:call-template name="replace-substring">
								<xsl:with-param name="original" select="$par"/>
								<xsl:with-param name="substring" select="$param"/>
								<xsl:with-param name="replacement" select="$value"/>
							</xsl:call-template>
						</xsl:variable>
						<!--xsl:variable name="sonucDeger" select="replace($par , $paramEscaped, functx:escape-for-regex($value))"/-->
						<xsl:value-of select="$sonucDeger"/>
					</xsl:when>
					<xsl:otherwise>

						<xsl:variable name="sonucDeger" select="$par"/>
						<xsl:value-of select="$sonucDeger"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<!--<xsl:text>&#xA;</xsl:text>
			<xsl:text>&#xA; Sonuc = </xsl:text>
			<xsl:value-of select="$modifiedNode"/>-->


			<xsl:call-template name="dots">
				<xsl:with-param name="numberOfParameter" select="$numberOfParameter - 1"/>
				<xsl:with-param name="paramValue" select="$paramValue"/>
				<xsl:with-param name="nodeValue" select="$modifiedNode"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="$numberOfParameter = 0">
			<xsl:value-of select="$nodeValue/text()"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="com:jobPath|com:jobCommand">

		<xsl:comment>Created by using <xsl:value-of select="system-property('xsl:vendor')"/></xsl:comment>
		<xsl:variable name="par" select="current()"/>
		<!--<xsl:variable name="nn" select="concat('/<','/>')"/>-->
		<!--<xsl:text>&#xA; $par degerii = </xsl:text>-->
		<!--<xsl:value-of select="$par"/>-->

		<xsl:text disable-output-escaping="yes">&lt;</xsl:text>
		<xsl:value-of select="name(.)"/>
		<xsl:text disable-output-escaping="yes">&gt;</xsl:text>
		<!--<com:jobPath>-->

		<xsl:call-template name="dots">
			<xsl:with-param name="numberOfParameter" select="count(//par:parameter)"/>
			<xsl:with-param name="paramValue" select="//par:parameter"/>
			<xsl:with-param name="nodeValue" select="$par"/>
		</xsl:call-template>
		<!--</com:jobPath>-->

		<xsl:text disable-output-escaping="yes">&lt;/</xsl:text>
		<xsl:value-of select="name(.)"/>
		<xsl:text disable-output-escaping="yes">&gt;</xsl:text>
	</xsl:template>


	<xsl:template name="replace-substring">
		<xsl:param name="original"/>
		<xsl:param name="substring"/>
		<xsl:param name="replacement" select="''"/>

		<!--xsl:text>&#xA; Orjinal deger = </xsl:text-->
		<!--<xsl:value-of select="$original"/>-->
		<!--<xsl:text>&#xA;  </xsl:text>-->

		<!--<xsl:text>&#xA; substring deger = </xsl:text>-->
		<!--<xsl:value-of select="$substring"/>-->
		<!--<xsl:text>&#xA;  </xsl:text>-->

		<!--<xsl:text>&#xA; replacement deger = </xsl:text>-->
		<!--<xsl:value-of select="$replacement"/>-->
		<!--<xsl:text>&#xA;  </xsl:text>-->

		<xsl:variable name="first">
			<xsl:choose>
				<xsl:when test="contains($original, $substring)">
					<xsl:value-of select="substring-before($original, $substring)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$original"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="middle">
			<xsl:choose>
				<xsl:when test="contains($original, $substring)">
					<xsl:value-of select="$replacement"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text></xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="last">
			<xsl:choose>
				<xsl:when test="contains($original, $substring)">
					<xsl:choose>
						<xsl:when test="contains(substring-after($original, $substring), $substring)">
							<xsl:call-template name="replace-substring">
								<xsl:with-param name="original">
									<xsl:value-of select="substring-after($original, $substring)"/>
								</xsl:with-param>
								<xsl:with-param name="substring">
									<xsl:value-of select="$substring"/>
								</xsl:with-param>
								<xsl:with-param name="replacement">
									<xsl:value-of select="$replacement"/>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="substring-after($original, $substring)"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text></xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:value-of select="concat($first, $middle, $last)"/>
	</xsl:template>


	<xsl:template name="for.loop">
		<xsl:param name="i"/>
		<xsl:param name="count"/>
		<xsl:param name="value"/>
		<xsl:param name="fullText"/>

		<xsl:if test="$i = ($count+1)">
			<xsl:text>&#xA;</xsl:text>
			<jobKomutu>
				<xsl:value-of select="$fullText"/>
			</jobKomutu>
			<xsl:text>&#xA;  </xsl:text>
		</xsl:if>

		<xsl:if test="$i &lt;= $count">
			<xsl:variable name="deger2">
				<xsl:value-of select="concat($value,'[',$i,']','/text()')"/>
			</xsl:variable>
			<xsl:variable name="deger">
				<xsl:value-of select="saxon:evaluate($deger2)"/>
			</xsl:variable>

			<xsl:call-template name="for.loop">
				<xsl:with-param name="i">
					<xsl:value-of select="$i + 1"/>
				</xsl:with-param>
				<xsl:with-param name="count">
					<xsl:value-of select="$count"/>
				</xsl:with-param>
				<xsl:with-param name="value">
					<xsl:value-of select="$value"/>
				</xsl:with-param>
				<xsl:with-param name="fullText">
					<xsl:value-of select="concat($fullText, ' ', $deger)"/>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet><!-- Stylus Studio meta-information - (c) 2004-2009. Progress Software Corporation. All rights reserved.

<metaInformation>
	<scenarios>
		<scenario default="yes" name="Scenario1" userelativepaths="yes" externalpreview="no" url="job_sample_4parametersweep.xml" htmlbaseurl="" outputurl="job_sample_output.xml" processortype="saxon8" useresolver="yes" profilemode="0" profiledepth=""
		          profilelength="" urlprofilexml="" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" validateoutput="no" validator="internal"
		          customvalidator="">
			<advancedProp name="sInitialMode" value=""/>
			<advancedProp name="bXsltOneIsOkay" value="true"/>
			<advancedProp name="bSchemaAware" value="true"/>
			<advancedProp name="bXml11" value="false"/>
			<advancedProp name="iValidation" value="0"/>
			<advancedProp name="bExtensions" value="true"/>
			<advancedProp name="iWhitespace" value="0"/>
			<advancedProp name="sInitialTemplate" value=""/>
			<advancedProp name="bTinyTree" value="true"/>
			<advancedProp name="xsltVersion" value="2.0"/>
			<advancedProp name="bWarnings" value="true"/>
			<advancedProp name="bUseDTD" value="false"/>
			<advancedProp name="iErrorHandling" value="0"/>
		</scenario>
	</scenarios>
	<MapperMetaTag>
		<MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no">
			<SourceSchema srcSchemaPath="job_sample_4parametersweep.xml" srcSchemaRoot="dat:jobProperties" AssociatedInstance="" loaderFunction="document" loaderFunctionUsesURI="no"/>
		</MapperInfo>
		<MapperBlockPosition>
			<template match="*|@*|node()"></template>
		</MapperBlockPosition>
		<TemplateContext></TemplateContext>
		<MapperFilter side="source"></MapperFilter>
	</MapperMetaTag>
</metaInformation>
-->