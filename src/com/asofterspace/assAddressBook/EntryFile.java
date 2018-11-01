package com.asofterspace.assAddressBook;

import com.asofterspace.toolbox.io.File;
import com.asofterspace.toolbox.io.XmlFile;


public class EntryFile extends XmlFile {

	/**
	 * You can construct an EntryFile instance by directly from a path name.
	 */
	public EntryFile(String fullyQualifiedFileName) {

		super(fullyQualifiedFileName);
	}

	/**
	 * You can construct an EntryFile instance by basing it on an existing file object.
	 */
	public EntryFile(File regularFile) {

		super(regularFile);
	}
	
}
