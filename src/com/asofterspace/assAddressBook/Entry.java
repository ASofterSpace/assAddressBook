package com.asofterspace.assAddressBook;

import com.asofterspace.toolbox.io.XmlElement;

import java.util.List;


public abstract class Entry {

	private EntryFile file;


	public Entry(EntryFile file) {

		this.file = file;
	}
	
	protected String getValue(String key) {
	
		List<XmlElement> keyElems = file.domGetElems(key);
		
		if (keyElems.size() < 1) {
			return "(unknown)";
		}

		return keyElems.get(0).getInnerText();
	}
	
	protected void setValue(String key, String value) {
		
		List<XmlElement> keyElems = file.domGetElems(key);
		
		for (XmlElement keyElem : keyElems) {
			keyElem.setInnerText(value);
		}
	}
	
	public String getName() {
		return getValue("name");
	}
	
	public void setName(String newName) {
		setValue("name", newName);
	}
	
	public String getDetails() {
		return getValue("details");
	}
	
	public void setDetails(String newDetails) {
		setValue("details", newDetails);
	}
	
	public void delete() {
		// TODO
	}
	
	public void save() {
		file.save();
	}

}
