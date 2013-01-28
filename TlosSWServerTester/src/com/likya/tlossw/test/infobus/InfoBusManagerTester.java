package com.likya.tlossw.test.infobus;

import org.apache.log4j.Logger;

import com.likya.tlossw.infobus.InfoBusManager;
import com.likya.tlossw.infobus.helper.JobInfo;
import com.likya.tlossw.test.TestSuit;
import com.likya.tlossw.utils.date.DateUtils;

public class InfoBusManagerTester extends TestSuit {

	public static Logger myLogger = Logger.getLogger(InfoBusManagerTester.class.getCanonicalName());

	public InfoBusManagerTester() {
		super();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new InfoBusManagerTester().startTest();
	}

	public void startTest() {

		try {
			getSpaceWideRegistry().setEXistColllection(geteXistCollection());
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}

		try {
			startInfoBusSystem(myLogger);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int recordAmount = 500;
		int counter = 0;
		
		long startTime = System.currentTimeMillis();
		
		while (counter < recordAmount) {
			JobInfo infoType = getStatusChangeInfo();
			getSpaceWideRegistry().getInfoBus().addInfo(infoType);
			counter ++;
		}
		System.out.println("Kuyruk Boyu : " + ((InfoBusManager)getSpaceWideRegistry().getInfoBus()).getQueueSize());
		
		System.out.println(recordAmount + " Kaydı doldurma süresi : " + DateUtils.getFormattedElapsedTimeMS(System.currentTimeMillis() - startTime));
		
		startTime = System.currentTimeMillis();
		
		while(((InfoBusManager)getSpaceWideRegistry().getInfoBus()).getQueueSize() > 0) {
//			System.out.println(((InfoBusManager)getSpaceWideRegistry().getInfoBus()).getQueueSize());
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println(recordAmount + " Kaydı eritme süresi : " + DateUtils.getFormattedElapsedTimeMS(System.currentTimeMillis() - startTime));

	}

	public static void errprintln(String message) {
		System.err.println(message);
	}

}
