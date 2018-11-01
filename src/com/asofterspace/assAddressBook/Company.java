package com.asofterspace.assAddressBook;


public class Company extends Entry {

	public Company (EntryFile file) {
		super(file);
	}
	
	public String getDirectoryName() {
		return getValue("directoryName");
	}

}
