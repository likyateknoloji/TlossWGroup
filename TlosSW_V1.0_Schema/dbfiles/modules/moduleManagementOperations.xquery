xquery version "1.0";

module namespace hs = "http://hs.tlos.com/";

import module namespace met = "http://meta.tlos.com/" at "moduleMetaDataOperations.xquery";

declare namespace com = "http://www.likyateknoloji.com/XML_common_types";
declare namespace con ="http://www.likyateknoloji.com/XML_config_types";
declare namespace state-types="http://www.likyateknoloji.com/state-types";

(:
Mappings
$configDocumentUrl = doc("//db/TLOSSW/xmls/tlosSWConfig10.xml")
$globalStatesDocumentUrl = doc("//db/TLOSSW/xmls/tlosSWGlobalStates10.xml")
:)

declare function hs:getTlosConfig($documentUrl as xs:string)
{
   let $configDocumentUrl := met:getMetaData($documentUrl, "serverConfig")
   
   for $config in doc($configDocumentUrl)/con:TlosConfigInfo
   return $config
};

declare function hs:updateTlosConfigMailOptions($documentUrl as xs:string, $mailOption as element(con:mailOptions))
{	
   let $configDocumentUrl := met:getMetaData($documentUrl, "serverConfig")
   
	for $moption in doc($configDocumentUrl)/con:TlosConfigInfo/con:settings/con:mailOptions
	return  update replace $moption with 	
	<con:mailOptions>
		<con:useMail>
			<com:comment>{data($mailOption/con:useMail/com:comment)}</com:comment>
			<com:value_boolean>{data($mailOption/con:useMail/com:value_boolean)}</com:value_boolean>
		</con:useMail>
		<con:smtpServerUserName id="1">
		 <com:name>{data($mailOption/con:smtpServerUserName/com:name)}</com:name>
		{
			for $j in 1 to count($mailOption/con:smtpServerUserName/com:Email)
			return
				<com:Email id="{data($mailOption/con:smtpServerUserName/com:Email[$j]/@id)}">
					{data($mailOption/con:smtpServerUserName/com:Email[$j])}
				</com:Email>
		 }
		</con:smtpServerUserName>
		<com:smtpServerPassword>{data($mailOption/com:smtpServerPassword)}</com:smtpServerPassword>
		<com:smtpServerAddress>{data($mailOption/com:smtpServerAddress)}</com:smtpServerAddress>
		<con:statusListForMail>
			{
			for $k in 1 to count($mailOption/con:statusListForMail/state-types:StatusName)
			return
				<state-types:StatusName>{data($mailOption/con:statusListForMail/state-types:StatusName[$k])}</state-types:StatusName>
			}
		</con:statusListForMail>
	</con:mailOptions>
};

declare function hs:updateTlosSmsOptions($documentUrl as xs:string, $smsOption as element(con:smsOptions))
{	
    let $configDocumentUrl := met:getMetaData($documentUrl, "serverConfig")
	
	for $soption in doc($configDocumentUrl)/con:TlosConfigInfo/con:settings/con:smsOptions
	return  update replace $soption with 	
	<con:smsOptions>
		<con:useSms>{data($smsOption/con:useSms)}</con:useSms>			
	</con:smsOptions>
};

declare function hs:updateTlosConfigFrequency($documentUrl as xs:string, $tlosFreq as element(con:tlosFrequency))
{	
    let $configDocumentUrl := met:getMetaData($documentUrl, "serverConfig")
	 
	for $tFrequency in doc($configDocumentUrl)/con:TlosConfigInfo/con:settings/con:tlosFrequency
	return  update replace $tFrequency with 	
	<con:tlosFrequency>
		<com:comment>{data($tlosFreq/com:comment)}</com:comment>
		<com:frequency>{data($tlosFreq/com:frequency)}</com:frequency>
	</con:tlosFrequency>
};

declare function hs:updateTlosConfigPerformance($documentUrl as xs:string, $perform as element(con:performance))
{	
    let $configDocumentUrl := met:getMetaData($documentUrl, "serverConfig")
	 
	for $tPerformance in doc($configDocumentUrl)/con:TlosConfigInfo/con:performance
	return  update replace $tPerformance with 	
	<con:performance>
		<con:threshold>
			<con:high>{data($perform/con:threshold/con:high)}</con:high>
			<con:low>{data($perform/con:threshold/con:low)}</con:low>
		</con:threshold>
	</con:performance>
};

declare function hs:updateTlosConfigInfo($documentUrl as xs:string, $tlosConfigInfo as element(con:TlosConfigInfo))
{
	let $retMailOption := hs:updateTlosConfigMailOptions($documentUrl, $tlosConfigInfo/con:settings/con:mailOptions)
	let $retSmsOption := hs:updateTlosSmsOptions($documentUrl, $tlosConfigInfo/con:settings/con:smsOptions)
	let $retSmsOption := hs:updateTlosConfigFrequency($documentUrl, $tlosConfigInfo/con:settings/con:tlosFrequency)
	let $retPerformance := hs:updateTlosConfigPerformance($documentUrl, $tlosConfigInfo/con:performance)
	return $retMailOption
};

declare function hs:updateTlosConfigInfoLock($documentUrl as xs:string, $tlosConfigInfo as element(con:TlosConfigInfo))
{
    let $configDocumentUrl := met:getMetaData($documentUrl, "serverConfig")
	
	return util:exclusive-lock(doc($configDocumentUrl)/con:TlosConfigInfo, hs:updateTlosConfigInfo($documentUrl, $tlosConfigInfo))
};

declare function hs:updateTlosConfigInfo2($documentUrl as xs:string, $tlosConfigInfo as element(con:TlosConfigInfo))
{	
    let $configDocumentUrl := met:getMetaData($documentUrl, "serverConfig")
	
	for $config in doc($configDocumentUrl)/con:TlosConfigInfo
	return  update replace $config with 
	<con:TlosConfigInfo>
		<con:Product-info>
			<com:comment>{data($tlosConfigInfo/con:Product-info/com:comment)}</com:comment>
		 	 <com:date> 
		  	    <com:day>{data($tlosConfigInfo/con:Product-info/com:date/com:day)}</com:day>
				<com:month>{data($tlosConfigInfo/con:Product-info/com:date/com:month)}</com:month>
				<com:year>{data($tlosConfigInfo/con:Product-info/com:date/com:month)}</com:year>
		  	</com:date>
			<com:version>{data($tlosConfigInfo/con:Product-info/com:version)}</com:version>
			<con:company>
				<com:name>{data($tlosConfigInfo/con:Product-info/con:company/com:name)}</com:name>
				<com:address>{data($tlosConfigInfo/con:Product-info/con:company/com:address)}</com:address>
				{
				  for $i in 1 to count($tlosConfigInfo/con:Product-info/con:company/com:telephone)
				  return
					<com:telephone id="{data($tlosConfigInfo/con:Product-info/con:company/com:telephone[$i]/@id)}">
						{data($tlosConfigInfo/con:Product-info/con:company/com:telephone[$i])}
				    </com:telephone>
			    }				
			</con:company>
		</con:Product-info>
		
		<con:settings>
			<con:machine-info>
				<com:comment>{data($tlosConfigInfo/con:settings/con:machine-info/com:comment)}</com:comment>
				<com:name>{data($tlosConfigInfo/con:settings/con:machine-info/com:name)}</com:name>
			</con:machine-info>
			<con:paths>
				<com:comment>{data($tlosConfigInfo/con:settings/con:paths/com:comment)}</com:comment>
				<con:tlos-bins>{data($tlosConfigInfo/con:settings/con:paths/con:tlos-bins)}</con:tlos-bins>
				<con:tlos-jobs>{data($tlosConfigInfo/con:settings/con:paths/con:tlos-jobs)}</con:tlos-jobs>
				<con:tlos-logs>{data($tlosConfigInfo/con:settings/con:paths/con:tlos-logs)}</con:tlos-logs>
			</con:paths>
			<con:isPersistent>
				<com:comment>{data($tlosConfigInfo/con:settings/con:isPersistent/com:comment)}</com:comment>
				<com:value_boolean>{data($tlosConfigInfo/con:settings/con:isPersistent/com:value_boolean)}</com:value_boolean>
			</con:isPersistent>
			<con:mailOptions>
				<con:useMail>
					<com:comment>{data($tlosConfigInfo/con:settings/con:mailOptions/con:useMail/com:comment)}</com:comment>
					<com:value_boolean>{data($tlosConfigInfo/con:settings/con:mailOptions/con:useMail/com:value_boolean)}</com:value_boolean>
				</con:useMail>
				<con:smtpServerUserName id="1">
			 	 <com:name>{data($tlosConfigInfo/con:settings/con:mailOptions/con:smtpServerUserName/com:name)}</com:name>
			  	{
					for $j in 1 to count($tlosConfigInfo/con:settings/con:mailOptions/con:smtpServerUserName/com:Email)
					return
						<com:Email id="{data($tlosConfigInfo/con:settings/con:mailOptions/con:smtpServerUserName/com:Email[$j]/@id)}">
							{data($tlosConfigInfo/con:settings/con:mailOptions/con:smtpServerUserName/com:Email[$j])}
						</com:Email>
			 	 }
				</con:smtpServerUserName>
				<com:smtpServerPassword>{data($tlosConfigInfo/con:settings/con:mailOptions/com:smtpServerPassword)}</com:smtpServerPassword>
				<com:smtpServerAddress>{data($tlosConfigInfo/con:settings/con:mailOptions/com:smtpServerAddress)}</com:smtpServerAddress>
				<con:statusListForMail>
					{
					for $k in 1 to count($tlosConfigInfo/con:settings/con:mailOptions/con:statusListForMail/com:status)
					return
						<com:status>{data($tlosConfigInfo/con:settings/con:mailOptions/con:statusListForMail/com:status[$k])}</com:status>
					}
				</con:statusListForMail>
			</con:mailOptions>
	
			<con:smsOptions>
				<con:useSms>{data($tlosConfigInfo/con:settings/con:smsOptions/con:useSms)}</con:useSms>			
			</con:smsOptions>
	
			<con:logFile>{data($tlosConfigInfo/con:settings/con:logFile)}</con:logFile>

			<con:tlosFrequency>
				<com:comment>{data($tlosConfigInfo/con:settings/con:tlosFrequency/com:comment)}</com:comment>
				<com:frequency>{data($tlosConfigInfo/con:settings/con:tlosFrequency/com:frequency)}</com:frequency>
			</con:tlosFrequency>	

			<con:remoteManagerProperties>
				<com:comment>{data($tlosConfigInfo/con:settings/con:remoteManagerProperties/com:comment)}</com:comment>
				<com:portNumber>{data($tlosConfigInfo/con:settings/con:remoteManagerProperties/com:portNumber)}</com:portNumber>
				<com:bufferSize>{data($tlosConfigInfo/con:settings/con:remoteManagerProperties/com:bufferSize)}</com:bufferSize>
			</con:remoteManagerProperties>

			<con:httpManagerProperties>
				<com:ipAddress>{data($tlosConfigInfo/con:settings/con:httpManagerProperties/com:ipAddress)}</com:ipAddress>
				<com:portNumber>{data($tlosConfigInfo/con:settings/con:httpManagerProperties/com:portNumber)}</com:portNumber>
			</con:httpManagerProperties>
		</con:settings>
		
		<con:performance>
			<con:threshold>
				<con:high>{data($tlosConfigInfo/con:performance/con:threshold/con:high)}</con:high>
				<con:low>{data($tlosConfigInfo/con:performance/con:threshold/con:low)}</con:low>
			</con:threshold>
		</con:performance>
 </con:TlosConfigInfo>
};

declare function hs:getTlosGlobalStates($documentUrl as xs:string)
{
    let $globalStatesDocumentUrl := met:getMetaData($documentUrl, "globalStates")
	   
	for $globalStates in doc($globalStatesDocumentUrl)/state-types:GlobalStateDefinition
	return $globalStates
};
