package com.likya.tlossw.core.events.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;

import com.likya.tlossw.model.infobus.mail.SimpleMail;
import com.likya.tlossw.utils.GlobalRegistry;

/* this is Event Handler */

public class EmailSenderEvent extends TlosBaseEvent {

	private String[] emailList;
	private String content = "İçerik Girilmedi !";

	public EmailSenderEvent(GlobalRegistry globalRegistry, String[] emailList) {
		super(globalRegistry);
		this.emailList = emailList;
	}

	public void update(Observable obj, Object arg) {

		ArrayList<String> distList = new ArrayList<String>(Arrays.asList(emailList));
		
		if(arg != null || !"".equals(arg)) {
			content = arg.toString();
		}

		SimpleMail simpleMail = new SimpleMail("Log Analizi", content, distList);

		getGlobalRegistry().getInfoBus().addInfo(simpleMail);
	}
}