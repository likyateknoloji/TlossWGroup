package com.likya.tlossw.test;

import org.apache.log4j.Logger;

import com.likya.tlossw.infobus.InfoBusManager;
import com.likya.tlossw.utils.SpaceWideRegistry;

public abstract class GenericTestSuit {

	public void startInfoBusSystem(Logger myLogger, SpaceWideRegistry spaceWideRegistry) throws Exception {

		myLogger.info("");
		myLogger.info("############# infoBus Manager  ##################");
		myLogger.info("Start the infoBus manager...");

		InfoBusManager infoBusManager = new InfoBusManager();

		Thread infoBusManagerService = new Thread(infoBusManager);

		infoBusManagerService.setName(InfoBusManager.class.getName());
		spaceWideRegistry.setInfoBus(infoBusManager);

		infoBusManagerService.start();

		myLogger.info("Started !");
		myLogger.info("Waiting for incoming messages ...");
		myLogger.info("#############################################");
		myLogger.info("");
	}
}
