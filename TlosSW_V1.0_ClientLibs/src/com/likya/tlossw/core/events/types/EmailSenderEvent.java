package com.likya.tlossw.core.events.types;

import java.util.ArrayList;
import java.util.Observable;

import com.likya.tlossw.model.infobus.mail.SimpleMail;
import com.likya.tlossw.utils.GlobalRegistry;
/* this is Event Handler */

public class EmailSenderEvent extends TlosBaseEvent {
	
	public EmailSenderEvent(GlobalRegistry globalRegistry) {
		super(globalRegistry);
		// TODO Auto-generated constructor stub
	}

	public void update(Observable obj, Object arg) {

		ArrayList<String> distList = new ArrayList<String>();
		distList.add("serkan.tas@likyateknoloji.com");
		
		SimpleMail simpleMail = new SimpleMail("Log Analizi", "Merhaba, log var. ", distList);

		getGlobalRegistry().getInfoBus().addInfo(simpleMail);
	}
}