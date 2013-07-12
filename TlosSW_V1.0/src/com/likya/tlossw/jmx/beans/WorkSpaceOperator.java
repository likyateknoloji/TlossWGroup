package com.likya.tlossw.jmx.beans;

import org.apache.xmlbeans.XmlException;

import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument;
import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument.TlosProcessData;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.core.cpc.CpcTester;
import com.likya.tlossw.exceptions.TlosException;
import com.likya.tlossw.model.jmx.JmxUser;

public class WorkSpaceOperator implements WorkSpaceOperatorMBean {
	
	@Override
	public int getNbChanges() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setState(String s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addTestData(JmxUser jmxUser, String tlosProcessDataText) {
		
		TlosProcessData tlosProcessData = null;
		
		try {
			CpcTester cpcTester = TlosSpaceWide.getSpaceWideRegistry().getCpcTesterReference();
			tlosProcessData = TlosProcessDataDocument.Factory.parse(tlosProcessDataText).getTlosProcessData();
			cpcTester.addTestData(tlosProcessData);
			
			synchronized (cpcTester.getExecuterThread()) {
				cpcTester.getExecuterThread().notifyAll();
			}
			
		} catch (XmlException e) {
			e.printStackTrace();
		} catch (TlosException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}

}
