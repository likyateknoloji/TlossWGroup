package com.likya.tlossw.web.validation;

import java.io.Serializable;

import com.likya.tlos.model.xmlbeans.user.PersonDocument.Person;

public class PersonValidation implements Serializable {

	private static final long serialVersionUID = -7650440702841197059L;

	public static boolean validateName(String name) {
		boolean valResult = false;
		String namePattern = "([a-zA-Z ])+";
		valResult = name.matches(namePattern);

		return valResult;
	}
	
	public void validateScreenName(javax.faces.context.FacesContext context, javax.faces.component.UIComponent component, java.lang.Object name) {

		if (!validateName(name.toString())) {
//			ManagerMediator.addMessage("yeniKullanici", null, ManagerMediator.getMessageBundle().getObject("tlos.validation.person.name.characters").toString(), null);
		}

	}

	public static boolean validateSurname(String surname) {
		boolean valResult = false;
		String surnamePattern = "([a-zA-Z ])+";
		valResult = surname.matches(surnamePattern);

		return valResult;
	}

	public void validateScreenSurname(javax.faces.context.FacesContext context, javax.faces.component.UIComponent component, java.lang.Object surname) {
		
		if (!validateSurname(surname.toString())) {
//			ManagerMediator.addMessage("yeniKullanici", null, ManagerMediator.getMessageBundle().getObject("tlos.validation.person.surname.characters").toString(), null);
		}

	}

	public static boolean validateUsername(String username) {
		boolean valResult = false;
		String usernamePattern = "([0-9a-zA-Z])+";
		valResult = username.matches(usernamePattern);

		return valResult;
	}

	public void validateScreenUsername(javax.faces.context.FacesContext context, javax.faces.component.UIComponent component, java.lang.Object username) {
		
		if (!validateUsername(username.toString())) {
//			ManagerMediator.addMessage("yeniKullanici", null, ManagerMediator.getMessageBundle().getObject("tlos.validation.person.user.characters").toString(), null);
		}

	}

	public static boolean validateUserpassword(String password) {
		if (password.length() >= 5 && password.length() <= 8) {
			return true;
		}
		return false;
	}

	public void validateScreenUserpassword(javax.faces.context.FacesContext context, javax.faces.component.UIComponent component, java.lang.Object password) {
		
		if (!validateUserpassword(password.toString())) {
//			ManagerMediator.addMessage("yeniKullanici", null, ManagerMediator.getMessageBundle().getObject("tlos.validation.person.password.length").toString(), null);
		}

	}

	public static boolean validateMail(String mail) {
		boolean valResult = false;
		String mailPattern = "[A-Za-z0-9_]+([-+.'][A-Za-z0-9_]+)*@[A-Za-z0-9_]+([-.][A-Za-z0-9_]+)*\\.[A-Za-z0-9_]+([-.][A-Za-z0-9_]+)*";
		valResult = mail.matches(mailPattern);

		return valResult;
	}

	public void validateScreenMail(javax.faces.context.FacesContext context, javax.faces.component.UIComponent component, java.lang.Object mail) {
		
		if (!validateMail(mail.toString())) {
//			ManagerMediator.addMessage("yeniKullanici", null, ManagerMediator.getMessageBundle().getObject("tlos.validation.management.eMail").toString(), null);
		}

	}

	public static boolean validateTelefon(String telefon) {
		boolean valResult = false;
		String telefonPattern = "[0-9]{12}";
		valResult = telefon.matches(telefonPattern);

		return valResult;
	}

	public void validateScreenTelefon(javax.faces.context.FacesContext context, javax.faces.component.UIComponent component, java.lang.Object telefon) {
		
		if (!validateTelefon(telefon.toString())) {
//			ManagerMediator.addMessage("yeniKullanici", null, ManagerMediator.getMessageBundle().getObject("tlos.validation.person.telephone.length").toString(), null);
		}

	}

	public boolean validatePerson(Person person, String userPassword2) {
		boolean validationValue = true;

		if (person.getName().equals("")) {
			validationValue = false;
//			ManagerMediator.addMessage("yeniKullanici", null, ManagerMediator.getMessageBundle().getObject("tlos.validation.person.name").toString(), null);
		} else if (!PersonValidation.validateName(person.getName())) {
			validationValue = false;
//			ManagerMediator.addMessage("yeniKullanici", null, ManagerMediator.getMessageBundle().getObject("tlos.validation.person.name.characters").toString(), null);
		}

		if (person.getSurname().equals("")) {
			validationValue = false;
//			ManagerMediator.addMessage("yeniKullanici", null, ManagerMediator.getMessageBundle().getObject("tlos.validation.person.surname").toString(), null);
		} else if (!PersonValidation.validateSurname(person.getSurname())) {
			validationValue = false;
//			ManagerMediator.addMessage("yeniKullanici", null, ManagerMediator.getMessageBundle().getObject("tlos.validation.person.surname.characters").toString(), null);
		}

		if (person.getRole() == null) {
			validationValue = false;
//			ManagerMediator.addMessage("yeniKullanici", null, ManagerMediator.getMessageBundle().getObject("tlos.validation.person.role.choose").toString(), null);
		}

		if (person.getUserName().equals("")) {
			validationValue = false;
//			ManagerMediator.addMessage("yeniKullanici", null, ManagerMediator.getMessageBundle().getObject("tlos.validation.person.user.name").toString(), null);
		} else if (!PersonValidation.validateUsername(person.getUserName())) {
			validationValue = false;
//			ManagerMediator.addMessage("yeniKullanici", null, ManagerMediator.getMessageBundle().getObject("tlos.validation.person.user.characters").toString(), null);
		}

		if (person.getUserPassword().equals("")) {
			validationValue = false;
//			ManagerMediator.addMessage("yeniKullanici", null, ManagerMediator.getMessageBundle().getObject("tlos.validation.person.password").toString(), null);
		} else if (!PersonValidation.validateUserpassword(person.getUserPassword())) {
			validationValue = false;
//			ManagerMediator.addMessage("yeniKullanici", null, ManagerMediator.getMessageBundle().getObject("tlos.validation.person.password.length").toString(), null);
		}

		if (!person.getUserPassword().equals(userPassword2)) {
			validationValue = false;
//			ManagerMediator.addMessage("yeniKullanici", null, ManagerMediator.getMessageBundle().getObject("tlos.validation.person.password.conflict").toString(), null);
		}

		if (person.getEmailList().getEmailArray(0).equals("")) {
			validationValue = false;
//			ManagerMediator.addMessage("yeniKullanici", null, ManagerMediator.getMessageBundle().getObject("tlos.validation.person.email").toString(), null);
		} else if(!person.getEmailList().validate()){
			validationValue = false;
//			ManagerMediator.addMessage("yeniKullanici", null, ManagerMediator.getMessageBundle().getObject("tlos.validation.person.emaillist.error").toString(), null);
		}

		if (person.getTelList().getTelArray(0).equals("") /* || !validateTelefon(person.getTelList().getTelArray(0))*/) {
			validationValue = false;
//			ManagerMediator.addMessage("yeniKullanici", null, ManagerMediator.getMessageBundle().getObject("tlos.validation.person.telephone.length").toString(), null);
		} 
//			else if(!person.getTelList().validate()){
//			validationValue = false;
//			ManagerMediator.addMessage("yeniKullanici", null, ManagerMediator.getMessageBundle().getObject("tlos.validation.person.tellist.error").toString(), null);
//		}
		
//		if (person.getUserPassword().equals(person.getUserName())) {
//			validationValue = false;
//			ManagerMediator.addMessage("yeniKullanici", null, ManagerMediator.getMessageBundle().getObject("tlos.validation.person.passanduser.conflict").toString(), null);
//		}

		return validationValue;
	}
	
}
