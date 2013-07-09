package com.likya.tlossw.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;

import com.likya.tlossw.utils.date.DateUtils;

public class FtpUtils {

	/**
	 * Ftp kullanarak verilen dizindeki dosya ve klasorleri listeliyor
	 * 
	 * @param ftpClient	Ftp islemlerini yapmak icin kullanilan ftp istemcisi
	 * @param directory	Dosya ve klasorleri listelenen dizin
	 * @return listelenen dosya ve klasorler donuyor ya da null donuyor
	 * @see org.apache.commons.net.ftp.FTPClient
	 */
	public static FTPFile[] listRemoteFiles(FTPClient ftpClient, String directory, Writer outputFile, Logger myLogger) {
		
		try {
			
			ftpClient.enterLocalPassiveMode();
			
			FTPFile[] files = ftpClient.listFiles(directory);
			
			outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Dosya Listesi:" + System.getProperty( "line.separator" ));
			
			for (int i = 0; i < files.length; i++) {
//				System.out.println(files[i].getName());
				outputFile.write("\t" + files[i].getName() + System.getProperty( "line.separator" ));
			}
			
			return files;
			
		} catch (IOException e) {
			
			myLogger.error("Dosya listelemede hata !");
			myLogger.error("Ftp ReplyCode: " + ftpClient.getReplyCode());
			myLogger.error("Ftp ReplyString: " + ftpClient.getReplyString());
			
			try {
				outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Dosya listelemede hata !" + System.getProperty( "line.separator" ));
				outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Ftp ReplyCode: " + ftpClient.getReplyCode() + System.getProperty( "line.separator" ));
				outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Ftp ReplyString: " + ftpClient.getReplyString());
				
				for(StackTraceElement element: e.getStackTrace()) {
					outputFile.write("\t" + element.toString() + System.getProperty( "line.separator" ));
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			
			return null;
			
		}
	}
	
	/**
	 * Ftp kullanarak uzak dizindeki dosyayi siliyor
	 * 
	 * @param ftpClient Ftp islemlerini yapmak i�in kullanilan ftp istemcisi
	 * @param sourceFile Silinecek kaynak dosyanin bulundugu dizinle birlikte ismi
	 * @return islem gerceklestirilirse true, gerceklestirilemezse false donuyor
	 */
	public static boolean deleteRemoteFile(FTPClient ftpClient, String sourceFile, Writer outputFile, Logger myLogger) {
		
		try {
			
			ftpClient.enterLocalPassiveMode();
			
			boolean isDeleted = ftpClient.deleteFile(sourceFile);
			
			if(isDeleted) {
				myLogger.info("Kaynak dosya silindi");
				outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Kaynak dosya silindi" + System.getProperty( "line.separator" ));
				
				return isDeleted;
			} else {
				myLogger.error("Kaynak dosya silinemedi");
				myLogger.error("Ftp ReplyCode: " + ftpClient.getReplyCode());
				myLogger.error("Ftp ReplyString: " + ftpClient.getReplyString());
				
				outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Kaynak dosya silinemedi" + System.getProperty( "line.separator" ));
				outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Ftp ReplyCode: " + ftpClient.getReplyCode() + System.getProperty( "line.separator" ));
				outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Ftp ReplyString: " + ftpClient.getReplyString());
			}
		} catch (IOException e) {
			myLogger.error("Kaynak dosya silinemedi");
			
			try {
				outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Kaynak dosya silinemedi: " + sourceFile + System.getProperty( "line.separator" ));
				
				for(StackTraceElement element: e.getStackTrace()) {
					outputFile.write("\t" + element.toString() + System.getProperty( "line.separator" ));
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
//			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Ftp kullanarak yerel dizindeki dosyayi uzak dizine kopyaliyor (put islemi)
	 * 
	 * @param ftpClient Ftp islemlerini yapmak i�in kullanilan ftp istemcisi
	 * @param sourceFile Kopyalanacak kaynak dosyanin bulundugu dizinle birlikte ismi
	 * @param targetFile Dosyanin kopyalanacagi dizinle birlikte ismi
	 * @return islem gerceklestirilirse true, gerceklestirilemezse false donuyor
	 */
	public static boolean copyLocalFileToRemote(FTPClient ftpClient, String sourceFile, String targetFile, Writer outputFile, Logger myLogger) {
		InputStream is;
		
		try {
			is = new FileInputStream(sourceFile);
			
			ftpClient.enterLocalPassiveMode();
			
			boolean put = ftpClient.storeFile(targetFile, is);

			if(put) {
				myLogger.info("Kaynak dosya uzak dizine kopyalandi");
				outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Kaynak dosya uzak dizine kopyalandi" + System.getProperty( "line.separator" ));
			} else {
				myLogger.error("Kaynak dosya uzak dizine kopyalanamadi");
				myLogger.error("Ftp ReplyCode: " + ftpClient.getReplyCode());
				myLogger.error("Ftp ReplyString: " + ftpClient.getReplyString());
				
				outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Kaynak dosya uzak dizine kopyalanamadi" + System.getProperty( "line.separator" ));
				outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Ftp ReplyCode: " + ftpClient.getReplyCode() + System.getProperty( "line.separator" ));
				outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Ftp ReplyString: " + ftpClient.getReplyString());
			}

			is.close();

			return put;
			
		} catch (FileNotFoundException e) {
			myLogger.error("Kaynak dosya bulunamadi: " + sourceFile);
			
			try {
				outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Kaynak dosya bulunamadi: " + sourceFile + System.getProperty( "line.separator" ));
				
				for(StackTraceElement element: e.getStackTrace()) {
					outputFile.write("\t" + element.toString() + System.getProperty( "line.separator" ));
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			
//			e.printStackTrace();
		} catch (IOException e) {
			myLogger.error("Dosya kopyalanamadi");
			
			try {
				outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Dosya kopyalanamadi" + System.getProperty( "line.separator" ));
				
				for(StackTraceElement element: e.getStackTrace()) {
					outputFile.write("\t" + element.toString() + System.getProperty( "line.separator" ));
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
//			e.printStackTrace();
		}
        return false;
	}
	
	/**
	 * Ftp kullanarak uzak dizindeki dosyayi yerel dizine kopyaliyor (get islemi)
	 * 
	 * @param ftpClient Ftp islemlerini yapmak için kullanilan ftp istemcisi
	 * @param sourceFile Kopyalanacak kaynak dosyanin bulundugu dizinle birlikte ismi
	 * @param targetFile Dosyanin kopyalanacagi dizinle birlikte ismi
	 * @return islem gerceklestirilirse true, gerceklestirilemezse false donuyor
	 */
	public static boolean copyRemoteFileToLocal(FTPClient ftpClient, String sourceFile, String targetFile, Writer outputFile, Logger myLogger) {
		OutputStream os;
		
		try {
			os = new FileOutputStream(new File(targetFile));
			
			ftpClient.enterLocalPassiveMode();
			
	        boolean get = ftpClient.retrieveFile(sourceFile, os);

	        if(get) {
	        	myLogger.info("Kaynak dosya yerel dizine kopyalandi");
	        	outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Kaynak dosya yerel dizine kopyalandi" + System.getProperty( "line.separator" ));
			} else {
				myLogger.error("Kaynak dosya yerel dizine kopyalanamadi");
				myLogger.error("Ftp ReplyCode: " + ftpClient.getReplyCode());
				myLogger.error("Ftp ReplyString: " + ftpClient.getReplyString());
				
				outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Kaynak dosya yerel dizine kopyalanamadi" + System.getProperty( "line.separator" ));
				outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Ftp ReplyCode: " + ftpClient.getReplyCode() + System.getProperty( "line.separator" ));
				outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Ftp ReplyString: " + ftpClient.getReplyString());
			}
	        
	        os.close();
	        
	        return get;
		} catch (FileNotFoundException e) {
			myLogger.error("Kaynak dosya bulunamadi: " + sourceFile);
			
			try {
				outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Kaynak dosya bulunamadi: " + sourceFile + System.getProperty( "line.separator" ));
				
				for(StackTraceElement element: e.getStackTrace()) {
					outputFile.write("\t" + element.toString() + System.getProperty( "line.separator" ));
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			
//			e.printStackTrace();
		} catch (IOException e) {
			myLogger.error("Dosya kopyalanamadi");
			
			try {
				outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Dosya kopyalanamadi " + sourceFile + System.getProperty( "line.separator" ));
				
				for(StackTraceElement element: e.getStackTrace()) {
					outputFile.write("\t" + element.toString() + System.getProperty( "line.separator" ));
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
//			e.printStackTrace();
		}
        return false;
	}
}
