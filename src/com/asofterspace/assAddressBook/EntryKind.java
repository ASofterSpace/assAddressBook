/**
 * Unlicensed code created by A Softer Space, 2018
 * www.asofterspace.com/licenses/unlicense.txt
 */
package com.asofterspace.assAddressBook;


public enum EntryKind {

	PERSON("Person"),
	COMPANY("Company");
	
	
	String kindStr;
	

	EntryKind (String kindStr) {
		this.kindStr = kindStr;
	}
	
	public String toString() {
		return kindStr;
	}
	
	public String toLowerCase() {
		return toString().toLowerCase();
	}

}
