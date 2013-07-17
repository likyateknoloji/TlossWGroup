package com.likya.tlossw.web.exist;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;

@ManagedBean(name = "existConnectionHolder")
@ApplicationScoped
public class ExistConnectionHolder implements Serializable {

	private static final long serialVersionUID = -7948774998363534929L;

	public transient Collection collection;

	@PostConstruct
	public void startUp() {
		ExistClient.tryReconnect = true;
		collection = ExistClient.getCollection();
		String createId;
		if (collection != null) {
			try {
				createId = collection.createId();
				System.err.println(createId);
			} catch (XMLDBException e) {
				e.printStackTrace();
			}
		}
	}

	@PreDestroy
	public void dispose() {
		if (collection != null) {
			try {
				ExistClient.tryReconnect = false;
				String createId = null;
				if (collection != null) {
					createId = collection.createId();
				}
				collection.close();
				System.out.println("Session disposed > " + createId);
				collection = null;
				DatabaseManager.deregisterDatabase(ExistClient.database);
			} catch (XMLDBException e) {
				e.printStackTrace();
			}
		}
	}

	public Collection getCollection() {
		if (collection == null) {
			ExistClient.tryReconnect = true;
			collection = ExistClient.getCollection();
		}
		return collection;
	}

}
