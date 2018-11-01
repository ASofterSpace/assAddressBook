package com.asofterspace.assAddressBook;


public class Person extends Entry {

	private Company company;


	public Person (EntryCtrl parent, EntryFile file, Company company) {

		super(parent, file);
		
		this.company = company;
	}

	public Company getCompany() {
		return company;
	}
	
	public void setCompany(Company newCompany) {
		this.company = newCompany;
	}

}
