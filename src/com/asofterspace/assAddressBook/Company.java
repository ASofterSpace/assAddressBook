package com.asofterspace.assAddressBook;

import java.util.List;


public class Company extends Entry {

	public Company (EntryCtrl parent, EntryFile file) {
		super(parent, file);
	}
	
	public String getDirectoryName() {
		return getValue("directoryName");
	}
	
	public List<Person> getPeople() {
		return parent.getPeople(this);
	}

}
