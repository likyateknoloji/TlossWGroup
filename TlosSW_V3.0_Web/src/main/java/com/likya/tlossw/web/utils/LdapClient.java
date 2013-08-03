package com.likya.tlossw.web.utils;

//$Header: /home/cvs/jakarta-jmeter/src/protocol/ldap/org/apache/jmeter/protocol/ldap/sampler/LdapClient.java,v 1.7 2004/02/13 02:40:54 sebb Exp $
/*
 * Copyright 2003-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.log4j.Logger;
//import javax.naming.directory.Attributes;
//import javax.naming.directory.SearchResult;

/**
 * Ldap Client class is main class to create, modify, search and delete all the
 * LDAP functionality available.
 * 
 * @author T.Elanjchezhiyan(chezhiyan@siptech.co.in) - Sip Technologies and
 *         Exports Ltd.
 *         Created Apr 29 2003 11:00 AM
 * @version $Revision: 1.7 $ Last updated: $Date: 2004/02/13 02:40:54 $
 */
public class LdapClient {
	transient private static Logger log = Logger.getLogger(LdapClient.class);
	private DirContext dirContext = null;

	/**
	 * Constructor for the LdapClient object.
	 */
	public LdapClient() {
	}

	/**
	 * Connect to server.
	 */
	public void connect(String host, String port, String rootdn, String username, String password) throws NamingException {
		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://" + host + ":" + port + "/" + rootdn);
		env.put(Context.REFERRAL, "throw");
		env.put(Context.SECURITY_CREDENTIALS, password);
		env.put(Context.SECURITY_PRINCIPAL, username);
		dirContext = new InitialDirContext(env);
	}

	/**
	 * Disconnect from the server.
	 */
	public void disconnect() {
		try {
			if (dirContext != null) {
				dirContext.close();
				dirContext = null;
			}
		} catch (NamingException e) {
			log.error("Ldap client - ", e);
		}
	}

	/**
	 * Filter the data in the ldap directory for the given search base.
	 * 
	 * @param searchBase
	 *            where the search should start
	 * @param searchFilter
	 *            filter this value from the base
	 */
	public boolean searchTest(String searchBase, String searchFilter) throws NamingException {
		// System.out.println("Base="+searchBase+" Filter="+searchFilter);
		SearchControls searchcontrols = new SearchControls(SearchControls.SUBTREE_SCOPE, 1L, // count limit
				0, // time limit
				null,// attributes (null = all)
				false,// return object ?
				false);// dereference links?
		NamingEnumeration<SearchResult> objs = dirContext.search(searchBase, searchFilter, searchcontrols);

		// Loop through the objects returned in the search
		while (objs.hasMoreElements()) {
			// Each item is a SearchResult object
			SearchResult match = (SearchResult) objs.nextElement();

			// Print out the node name
			System.out.println("Found " + match.getName() + ":");

			// Get the node's attributes
			Attributes attrs = match.getAttributes();
			@SuppressWarnings("rawtypes")
			NamingEnumeration e = attrs.getAll();

			// Loop through the attributes
			while (e.hasMoreElements()) {
				// Get the next attribute
				Attribute attr = (Attribute) e.nextElement();

				// Print out the attribute's value(s)
				System.out.print(attr.getID() + " = ");
				for (int i = 0; i < attr.size(); i++) {
					if (i > 0)
						System.out.print(", ");
					System.out.print(attr.get(i));
				}
				System.out.println();
			}
			System.out.println("---------------------------------------");
		}

		// System.out.println("Loop " + ne.toString() + " " + ne.hasMore());
		// while (ne.hasMore()) {
		// Attributes tmp = ne.nextElement().getAttributes(); // ne.next();ne.getClass().getName(); ne.nextElement().getAttributes().getAll();
		// System.out.println(tmp.getClass().getName() + " Size " + tmp.size());
		//
		// while (tmp.hasMoreElements()) {
		// str = (String) names.nextElement();
		// System.out.println(str + ": " + result.get(str));
		// }
		// System.out.println();
		//
		// SearchResult sr = (SearchResult) tmp;
		// Attributes at = sr.getAttributes();
		// System.out.println(at.get("cn"));
		// }
		// System.out.println("Done " + ne.hasMore());
		return true;
	}

	/**
	 * Modify the attribute in the ldap directory for the given string.
	 * 
	 * @param mods
	 *            add all the entry in to the ModificationItem
	 * @param string
	 *            the string (dn) value
	 */
	public void modifyTest(ModificationItem[] mods, String string) throws NamingException {
		dirContext.modifyAttributes(string, mods);
	}

	/**
	 * Create the attribute in the ldap directory for the given string.
	 * 
	 * @param basicattributes
	 *            add all the entry in to the basicattribute
	 * @param string
	 *            the string (dn) value
	 */
	public void createTest(BasicAttributes basicattributes, String string) throws NamingException {
		// DirContext dc = //TODO perhaps return this?
		dirContext.createSubcontext(string, basicattributes);
	}

	/**
	 * Delete the attribute from the ldap directory.
	 * 
	 * @param string
	 *            the string (dn) value
	 */
	public void deleteTest(String string) throws NamingException {
		dirContext.destroySubcontext(string);
	}

	public DirContext getDirContext() {
		return dirContext;
	}

	public void setDirContext(DirContext dirContext) {
		this.dirContext = dirContext;
	}
}