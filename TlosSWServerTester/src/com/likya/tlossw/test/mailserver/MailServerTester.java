package com.likya.tlossw.test.mailserver;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.config.MailOptionsDocument;
import com.likya.tlos.model.xmlbeans.config.MailOptionsDocument.MailOptions;
import com.likya.tlos.model.xmlbeans.config.SettingsDocument;
import com.likya.tlos.model.xmlbeans.config.SmtpServerUserNameDocument.SmtpServerUserName;
import com.likya.tlos.model.xmlbeans.config.TlosConfigInfoDocument;
import com.likya.tlossw.infobus.servers.MailServer;
import com.likya.tlossw.model.infobus.mail.SimpleMail;
import com.likya.tlossw.test.TestSuit;
import com.likya.tlossw.utils.date.DateUtils;
import com.likya.tlossw.utils.i18n.ResourceMapper;

public class MailServerTester extends TestSuit {

	public static Logger myLogger = Logger.getLogger(MailServerTester.class.getCanonicalName());

	private static boolean isMailEventEnabled = true;

	// private static boolean isSmsEventEnabled = false;
	// private static boolean isSNMPEventEnabled = false;

	public MailServerTester() {
		super();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MailServerTester().startTest();
	}

	public void startTest() {

		TlosConfigInfoDocument tlosConfigInfoDocument = TlosConfigInfoDocument.Factory.newInstance();
		SettingsDocument settingsDocument = SettingsDocument.Factory.newInstance();

		MailOptionsDocument mailOptionsDocument = MailOptionsDocument.Factory.newInstance();

		mailOptionsDocument.addNewMailOptions();
		MailOptions mailOptions = mailOptionsDocument.getMailOptions();

		mailOptions.setSmtpServerAddress("mail.likyateknoloji.com");
		mailOptions.setSmtpServerPort((short)587);

		SmtpServerUserName smtpServerUserName = SmtpServerUserName.Factory.newInstance();
		smtpServerUserName.setStringValue("test9027@likyateknoloji.com");

		mailOptions.setSmtpServerUserName(smtpServerUserName);

		mailOptions.setSmtpServerPassword("7209tset");

		settingsDocument.addNewSettings();
		settingsDocument.getSettings().setMailOptions(mailOptions);

		tlosConfigInfoDocument.addNewTlosConfigInfo();
		tlosConfigInfoDocument.getTlosConfigInfo().setSettings(settingsDocument.getSettings());

		getSpaceWideRegistry().setTlosSWConfigInfo(tlosConfigInfoDocument.getTlosConfigInfo());

		if (isMailEventEnabled) {
			startMailSystem();
		}

		ArrayList<String> distributionList = new ArrayList<String>();
		distributionList.add("serkan.tas@likyateknoloji.com");

		int recordAmount = 10;
		int counter = 0;

		long startTime = System.currentTimeMillis();

		while (counter < recordAmount) {
			SimpleMail simpleMail = new SimpleMail("Merhaba asker ! Bu sana " + counter + ". uyarım '", "Sağol ! " + counter, distributionList);
			getSpaceWideRegistry().getMailServer().sendMail(simpleMail);
			counter++;
		}
		System.out.println("Kuyruk Boyu : " + getSpaceWideRegistry().getMailServer().getQueueSize());

		System.out.println(recordAmount + " Kaydı doldurma süresi : " + DateUtils.getFormattedElapsedTimeMS(System.currentTimeMillis() - startTime));

		startTime = System.currentTimeMillis();

		while (getSpaceWideRegistry().getMailServer().getQueueSize() > 0) {
			// System.out.println(((InfoBusManager)getSpaceWideRegistry().getInfoBus()).getQueueSize());
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

	private void startMailSystem() {
		MailServer mailServer;
		try {
			mailServer = new MailServer(getSpaceWideRegistry().getTlosSWConfigInfo());
			getSpaceWideRegistry().setMailServer(mailServer);
			Thread mailServerService = new Thread(mailServer);
			mailServerService.setName(MailServer.class.getName());
			mailServerService.start();
		} catch (Exception e) {
			e.printStackTrace();
			errprintln(getSpaceWideRegistry().getApplicationResources().getString(ResourceMapper.TERMINATE_APPLICATION));
			System.exit(-1);
		}
	}

}
