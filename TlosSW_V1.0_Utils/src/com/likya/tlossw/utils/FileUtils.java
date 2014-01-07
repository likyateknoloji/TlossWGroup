/*
 * com.likya.tlos.utils : FileUtils.java
 * @author Serkan Taş
 * Tarih : Feb 1, 2009 2:04:40 AM
 */

package com.likya.tlossw.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;

import org.apache.log4j.Logger;

import com.likya.tlossw.utils.date.DateUtils;

public class FileUtils {

	public static boolean checkTempFile(String fileName, String tempDir) {
		return checkFile(System.getProperty(tempDir) + "/" + fileName);
	}

	public static boolean checkFile(String fileName) {
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(fileName);
			fis.close();
		} catch (FileNotFoundException fnfex) {
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static StringBuffer readFile(String fileName) {

		FileInputStream fis = null;
		StringBuffer outputBuffer = new StringBuffer();

		try {
			fis = new FileInputStream(fileName);
			InputStreamReader inputStreamReader = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String bufferString = null;

			while ((bufferString = bufferedReader.readLine()) != null) {
				outputBuffer.append(bufferString + '\n');
			}

			fis.close();

		} catch (FileNotFoundException fnfex) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return outputBuffer;
	}

	public static StringBuffer readFile(String fileName, String coloredLineIndicator, boolean useSections, boolean isXML) {

		FileInputStream fis = null;
		StringBuffer outputBuffer = new StringBuffer();

		try {
			fis = new FileInputStream(fileName);
			InputStreamReader inputStreamReader = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String bufferString = "";

			if (useSections) {
				bufferString = "<font color=\"red\">" + "****************************** File Header ******************************" + "</font>" + '\n';
				outputBuffer.append(bufferString);
			}
			while ((bufferString = bufferedReader.readLine()) != null) {
				if (isXML) {
					if (bufferString.equals("<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">")) {
						continue;
					}
				}
				if (bufferString.indexOf(coloredLineIndicator) > 0) {
					outputBuffer.append("<font color=\"red\">");
					outputBuffer.append(bufferString);
					outputBuffer.append("</font>" + '\n');
				} else {
					outputBuffer.append(bufferString + '\n');
				}
			}

			if (useSections) {
				bufferString = "<font color=\"red\">" + "****************************** File Footer ******************************" + "</font>" + '\n';
				outputBuffer.append(bufferString);
			}

			fis.close();

		} catch (FileNotFoundException fnfex) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return outputBuffer;
	}

	/**
	 * Yerel dizindeki dosyayi siliyor
	 * 
	 * @param sourceFile
	 *            Silinecek kaynak dosyanin bulundugu dizinle birlikte ismi
	 * @return islem gerceklestirilirse true, gerceklestirilemezse false donuyor
	 */
	public static boolean deleteLocalFile(String sourceFile, Writer outputFile, Logger myLogger) {

		File file = new File(sourceFile);

		boolean isDeleted = file.delete();

		try {
			if (isDeleted) {
				myLogger.info("Yerel dosya silindi");
				outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Yerel dosya silindi" + System.getProperty("line.separator"));

			} else {
				myLogger.error("Yerel dosya silinemedi");
				outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Yerel dosya silinemedi" + System.getProperty("line.separator"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return isDeleted;
	}

	public static boolean checkFileExist(String fileName) {
		File file = new File(fileName);
		return file.exists();
	}
	
	public static boolean writeFile(String fileName, String fileContent) {
		
		Writer output = null;

		try {
			
			if(fileContent == null)
				System.out.println("WARNING : File content is null !!");
			
			output = new BufferedWriter(new FileWriter(fileName));
			
			output.write(fileContent);

			output.close();

		} catch (IOException ex) {
			ex.printStackTrace();
			
			return false;
		}
		
		return true;
	}
	
}
