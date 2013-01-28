package com.likya.tlosswagent.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import com.likya.tlos.model.xmlbeans.state.GlobalStateDefinitionDocument.GlobalStateDefinition;
import com.likya.tlossw.model.jmx.JmxAgentUser;
import com.likya.tlossw.model.jmx.JmxUser;
import com.likya.tlosswagent.TlosSWAgent;

public class PersistenceUtils {
	
	public static boolean persistGlobalStateDefinition(String fileName, GlobalStateDefinition globalStateDefinition) {

		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		if (globalStateDefinition == null) {
			SWAgentRegistry.getsWAgentLogger().fatal("Global State Tanýmý boþ olmamalý !");
			SWAgentRegistry.getsWAgentLogger().fatal("Program sona erdi !");
			return false;
		}
		try {
			File file = new File(System.getProperty("tlosAgent.tmpdir") + "/" + fileName);
			fos = new FileOutputStream(file);  

			out = new ObjectOutputStream(fos);
			out.writeObject(globalStateDefinition);
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return true;

	}

	public static boolean recoverGlobalStateDefinition(String fileName) {

		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {
			fis = new FileInputStream(System.getProperty("tlosAgent.tmpdir") + "/" + fileName);
			in = new ObjectInputStream(fis);
			Object input = in.readObject();

			TlosSWAgent.getSwAgentRegistry().setGlobalStateDefinition((GlobalStateDefinition) input);
//			in.close();
			
		} catch (FileNotFoundException fnf) {
			SWAgentRegistry.getsWAgentLogger().info("tmp/globalStateDef dosyasi bulunamadigindan Gorev Kuyrugu Yerine konamadi,  (NOT Recovered) !");
			return false;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		} catch (NullPointerException  np) {
			np.printStackTrace();
			return false;
		} catch (SecurityException   se) {
			se.printStackTrace();
			return false;
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
			return false;
		} finally {
			try {
				if(in!=null) in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return true;
	}
	
	public static boolean persistJmxUser(String fileName, JmxUser jmxUser) {

		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		try {
			File file = new File(System.getProperty("tlosAgent.tmpdir") + "/" + fileName);
			fos = new FileOutputStream(file);  

			out = new ObjectOutputStream(fos);
			out.writeObject(jmxUser);
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return true;

	}

	public static boolean recoverJmxUser(String fileName) throws StreamCorruptedException {

		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {
			fis = new FileInputStream(System.getProperty("tlosAgent.tmpdir") + "/" + fileName);
			in = new ObjectInputStream(fis);
			Object input = in.readObject();

			TlosSWAgent.getSwAgentRegistry().setJmxAgentUser((JmxAgentUser) input);
			
//			Burada kapattiktan sonra finally icinde kapatamayinca hata veriyor
//			in.close();
			
		} catch (FileNotFoundException fnf) {
			SWAgentRegistry.getsWAgentLogger().info("tmp\\jmxUser dosyasi yok. Daha once kimlik dogrulamasi yapilmamis !");
			return false;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		} catch (NullPointerException  np) {
			np.printStackTrace();
			return false;
		} catch (SecurityException   se) {
			se.printStackTrace();
			return false;
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
			return false;
		} finally {
			try {
				if(in!=null) in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return true;
	}

}
