/*
 * TlosFaz2
 * com.likya.tlos.utils : ConfigLoader.java
 * @author Serkan Ta�
 * Tarih : 10.Kas.2008 13:45:56
 */

package com.likya.tlossw.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ResourceBundle;

import com.likya.tlos.model.xmlbeans.config.TlosConfigInfoDocument;
import com.likya.tlos.model.xmlbeans.config.TlosConfigInfoDocument.TlosConfigInfo;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.utils.i18n.ResourceMapper;
import com.likyateknoloji.xmlServerConfigTypes.ServerConfigDocument;
import com.likyateknoloji.xmlServerConfigTypes.ServerConfigDocument.ServerConfig;

public class ConfigLoader {

	public static TlosConfigInfo readTlosConfig(ResourceBundle resourceBaundle) {

		TlosConfigInfo tlosConfigInfo = null;
		String fileName = null;

		try {
			fileName = System.getProperty("tlossw.config");

			if ((fileName == null) || !FileUtils.checkFile(fileName)) {
				throw new FileNotFoundException();
			}

			File xmlFile = new File(System.getProperty("tlossw.config"));

			tlosConfigInfo = TlosConfigInfoDocument.Factory.parse(new FileReader(xmlFile)).getTlosConfigInfo();

		} catch (FileNotFoundException e) {
			TlosSpaceWide.errprintln("TlosSpaceWide Config file not found ! fileName => " + fileName);
			TlosSpaceWide.errprintln(resourceBaundle.getString(ResourceMapper.TERMINATE_APPLICATION));
			System.exit(-1);
		} catch (RuntimeException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return tlosConfigInfo;
	}

	public static ServerConfig readServerConfig(ResourceBundle resourceBaundle) {

		ServerConfig serverConfig = null;
		String fileName = null;

		try {
			fileName = System.getProperty("tlossw.config");

			if ((fileName == null) || !FileUtils.checkFile(fileName)) {
				throw new FileNotFoundException();
			}

			File xmlFile = new File(System.getProperty("tlossw.config"));

			serverConfig = ServerConfigDocument.Factory.parse(new FileReader(xmlFile)).getServerConfig();

		} catch (FileNotFoundException e) {
			TlosSpaceWide.errprintln("TlosSpaceWide Config file not found ! fileName => " + fileName);
			TlosSpaceWide.errprintln(resourceBaundle.getString(ResourceMapper.TERMINATE_APPLICATION));
			System.exit(-1);
		} catch (RuntimeException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return serverConfig;
	}
}
