package com.asofterspace.assAddressBook;


public class Person extends Entry {

	private Company company;


	public Person (EntryFile file) {
		super(file);
	}

	public Company getCompany() {
		return company;
	}
	
	public void setCompany(Company newCompany) {
		this.company = newCompany;
	}

}
