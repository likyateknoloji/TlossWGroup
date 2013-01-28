/*
 * TlosFaz2
 * com.likya.tlos.utils : ConfigLoader.java
 * @author Serkan Ta�
 * Tarih : 10.Kas.2008 13:45:56
 */

package com.likya.tlosswagent.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.likya.tlos.model.xmlbeans.agentconfig.AgentConfigInfoDocument;
import com.likya.tlos.model.xmlbeans.agentconfig.AgentConfigInfoDocument.AgentConfigInfo;
import com.likya.tlossw.utils.FileUtils;
import com.likya.tlosswagent.TlosSWAgent;
import com.likya.tlosswagent.utils.i18n.ResourceMapper;

public class ConfigLoader {
	
	public static final String configFilePropertyName = "tlosSwAgent.config";
	
	public static AgentConfigInfo readTlosConfig() {
		
		AgentConfigInfo agentConfigInfo = null;
		String fileName = null;
		
		try {
			fileName = System.getProperty(configFilePropertyName);
			
			if((fileName == null) || !FileUtils.checkFile(fileName)) {
				throw new FileNotFoundException();
			}
			
			File xmlFile = new File(System.getProperty(configFilePropertyName));

			agentConfigInfo = AgentConfigInfoDocument.Factory.parse(new FileReader(xmlFile)).getAgentConfigInfo();

		} catch (FileNotFoundException e) {
			TlosSWAgent.errprintln("TlosSWAgent Config file not found ! fileName => " + fileName);
			TlosSWAgent.errprintln(TlosSWAgent.getSwAgentRegistry().getApplicationResources().getString(ResourceMapper.TERMINATE_APPLICATION));
			System.exit(-1);
		} catch (RuntimeException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return agentConfigInfo;
	}
	
}
