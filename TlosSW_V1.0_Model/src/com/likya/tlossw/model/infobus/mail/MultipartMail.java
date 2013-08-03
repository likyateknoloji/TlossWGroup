package com.likya.tlossw.model.infobus.mail;

import java.util.ArrayList;

import javax.mail.Multipart;

public class MultipartMail extends TlosMail {

	private static final long serialVersionUID = -4501151857952027038L;
	
	private String mailSubject;
	private Multipart multipart;

	public MultipartMail(String mailSubject, Multipart multipart, ArrayList<String> distributionList) {
		super.setDistributionList(distributionList);
		this.mailSubject = mailSubject;
		this.multipart = multipart;
	}
	
	public String getMailSubject() {
		return mailSubject;
	}

	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}

	public Multipart getMultipart() {
		return multipart;
	}

	public void setMultipart(Multipart multipart) {
		this.multipart = multipart;
	}

}
