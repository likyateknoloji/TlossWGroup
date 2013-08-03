package com.likya.tlossw.infobus.helper.mail;

import java.util.ArrayList;

public class SimpleMail extends TlosMail {

	private static final long serialVersionUID = -5034090656169633715L;
	
	private String mailSubject;
	private String mailText;

	public SimpleMail(String mailSubject, String mailText, ArrayList<String> distributionList) {
		super.setDistributionList(distributionList);
		this.mailSubject = mailSubject;
		this.mailText = mailText;
	}

	public String getMailSubject() {
		return mailSubject;
	}

	public String getMailText() {
		return mailText;
	}

}
