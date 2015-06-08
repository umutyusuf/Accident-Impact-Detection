package com.umut.accidentrecognitioncp.data;

import java.util.List;

public class ContactsData {
	private List<String> contactNames;
	private List<String> contactPhoneNumbers;

	public ContactsData(List<String> contactNames,
			List<String> contactPhoneNumbers) {
		this.contactNames = contactNames;
		this.contactPhoneNumbers = contactPhoneNumbers;
	}

	public List<String> getContacts() {
		return contactNames;
	}

	public void setContacts(List<String> contacts) {
		this.contactNames = contacts;
	}

	public List<String> getContactPhoneNumbers() {
		return contactPhoneNumbers;
	}

	public void setContactPhoneNumbers(List<String> contactPhoneNumbers) {
		this.contactPhoneNumbers = contactPhoneNumbers;
	}
}
