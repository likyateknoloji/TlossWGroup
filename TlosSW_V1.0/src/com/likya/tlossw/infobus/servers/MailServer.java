package com.likya.tlossw.infobus.servers;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.config.TlosConfigInfoDocument.TlosConfigInfo;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.model.engine.EngineeConstants;
import com.likya.tlossw.model.infobus.mail.MultipartMail;
import com.likya.tlossw.model.infobus.mail.SimpleMail;
import com.likya.tlossw.model.infobus.mail.TlosMail;
import com.likya.tlossw.utils.FileUtils;
import com.likya.tlossw.utils.PersistenceUtils;
import com.likya.tlossw.utils.SpaceWideRegistry;


/**
 * @author vista
 * 
 */
public class MailServer implements Runnable {

	private final int timeout = 1000;
	private boolean executePermission = true;
	private ArrayList<TlosMail> mailQueue = new ArrayList<TlosMail>();

	private Properties props;
	private Authenticator authenticator;

	private String userName;
	private String password;
	private String from;
	
	private static Logger logger = SpaceWideRegistry.getGlobalLogger();

	/**
	 * SimpleAuthenticator is used to do simple authentication when the SMTP
	 * server requires it.
	 */
	private class SMTPAuthenticator extends javax.mail.Authenticator {

		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(userName, password);
		}

	}

	public MailServer(TlosConfigInfo tlosConfigInfo) throws Exception {
		this.props = System.getProperties();
		authenticator = new SMTPAuthenticator();

		this.userName = tlosConfigInfo.getSettings().getMailOptions().getSmtpServerUserName().getStringValue();
		this.password = tlosConfigInfo.getSettings().getMailOptions().getSmtpServerPassword();

		this.from = this.userName;
		
		props.put("mail.smtp.host", tlosConfigInfo.getSettings().getMailOptions().getSmtpServerAddress());
		props.put("mail.smtp.port", tlosConfigInfo.getSettings().getMailOptions().getSmtpServerPort() + "");
		props.put("mail.smtp.auth", "true");
	
		if (TlosSpaceWide.isRecoverable() && FileUtils.checkTempFile(PersistenceUtils.persistMailQueueFile, EngineeConstants.tempDir)) {
			mailQueue = PersistenceUtils.recoverMailQueue();
			if(mailQueue == null) {
				logger.warn("MailQueue can not be recovered !");
			}
		}
	}

	public void terminate(boolean forcedTerminate) {
		synchronized (this) {
			if(forcedTerminate) {
				mailQueue.clear();
			}
			this.executePermission = false;
		}
	}

	public void run() {
		
		while (executePermission || mailQueue.size() > 0) {
		
			while (mailQueue.size() > 0) {
				
				TlosMail tlosMail = (TlosMail) mailQueue.get(0);
				logger.debug("Trying to send mail...");
				
				try {
					
					if(tlosMail instanceof SimpleMail) {
						postMail((SimpleMail) tlosMail);
					} else if(tlosMail instanceof MultipartMail) {
						postMultiPartMail((MultipartMail) tlosMail);
					} else {
						logger.error("Undefined object type : " + tlosMail.toString());
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					logger.info("E-posta g�nderiminde hata oldu : " + e.getLocalizedMessage() + "=> e-posta iptal edildi !");
				}
				
				mailQueue.remove(0);
			
				if (TlosSpaceWide.isPersistent()) {
					PersistenceUtils.persistMailQueue(mailQueue);
				}
			}
			
			try {
				// TlosServer.getLogger().debug("Mail server sleeping !");
				Thread.sleep(timeout);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		/*
		 * if (TlosServer.getTlosParameters().isMail()) {
		 * schedulerLogger.info("E-posta sunucusu kapat�l�yor !");
		 * schedulerLogger.info("E-posta kuyru�unun bo�almas�n� bekliyoruz...");
		 * schedulerLogger.info("Kuyruktaki e-posta say�s� : " +
		 * getTlosMailServer().getQueueSize());
		 * 
		 * while (getTlosMailServer().getQueueSize() > 0) {
		 * print(getTlosMailServer().getQueueSize() + "-"); try {
		 * Thread.sleep(3000); } catch (InterruptedException e) {
		 * e.printStackTrace(); } } schedulerLogger.info("E-posta sunucusu
		 * kapat�ld� !"); }
		 */
		// schedulerLogger.info("Uygulama kapat�ld� !");
		// logger.log(Level.WARNING, "��i bitmeyen job'lar olabilir !" +
		// Thread.currentThread().getThreadGroup().activeCount());
		// System.exit(0);
		
		
		logger.info("E-posta sistemi kapat�ld� !");
		logger.info("E-posta kuyruk say�s� : " + mailQueue.size());
	}

	private void postMultiPartMail(MultipartMail multipartMail) throws MessagingException {

		// Get session
		Session session = Session.getDefaultInstance(props, authenticator);

		// Define message
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		
		Iterator<String> mailAddressIterator = multipartMail.getDistributionList().iterator();
		
		message.setSubject(multipartMail.getMailSubject());
		message.setSentDate(new Date());
		// Set the content for the message and transmit
		message.setContent(multipartMail.getMultipart());

		while (mailAddressIterator.hasNext()) {
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailAddressIterator.next()));
			// Send message
			Transport.send(message);
			try {
				// A��r� y�kleme yap�p, sistemi yormas�n diye aral�kl� g�nderim yap�lacak.
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
		}
	}

	private void postMail(SimpleMail simpleMail) throws AddressException, MessagingException {

		// Get session
		Session session = Session.getDefaultInstance(props, authenticator);

		// Define message
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		
		Iterator<String> mailAddressIterator = simpleMail.getDistributionList().iterator();
		while (mailAddressIterator.hasNext()) {
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailAddressIterator.next()));
			message.setSubject(simpleMail.getMailSubject());
			message.setText(simpleMail.getMailText());

			// Send message
			Transport.send(message);
			try {
				// A��r� y�kleme yap�p, sistemi yormas�n diye aral�kl� g�nderim yap�lacak.
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
		}
	}

	private synchronized void addMail(TlosMail tlosMail) {
		mailQueue.add(tlosMail);
	}

	public void sendMail(TlosMail tlosMail) {
		addMail(tlosMail);
	}

	public int getQueueSize() {
		return mailQueue.size();
	}

}
