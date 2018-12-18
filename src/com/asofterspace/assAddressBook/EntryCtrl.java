/**
 * Unlicensed code created by A Softer Space, 2018
 * www.asofterspace.com/licenses/unlicense.txt
 */
package com.asofterspace.assAddressBook;

import com.asofterspace.toolbox.io.Directory;
import com.asofterspace.toolbox.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class EntryCtrl {

	private Directory baseDir;
	
	private List<Entry> entries;
	

	public EntryCtrl () {
	
		baseDir = null;
		
		entries = new ArrayList<>();
	}
	
	public void loadDirectory(Directory baseDir) {

		this.baseDir = baseDir;
		
		baseDir.create();
		
		entries = new ArrayList<>();
		
		// all files directly inside the base dir are company files (then inside the companies are the actual people files)
		List<File> companyFiles = baseDir.getAllFiles(false);
		
		for (File companyFile : companyFiles) {
			
			Company curCompany = new Company(this, new EntryFile(companyFile));
			
			entries.add(curCompany);
			
			Directory curCompanyDir = baseDir.createChildDir(curCompany.getDirectoryName());
			
			List<File> peopleFiles = curCompanyDir.getAllFiles(false);
			
			for (File peopleFile : peopleFiles) {
				entries.add(new Person(this, new EntryFile(peopleFile), curCompany));
			}
		}
	}
	
	public EntryFile loadAnotherCompanyFile(File fileToLoad) {
		
		EntryFile result = new EntryFile(fileToLoad);
		
		entries.add(new Company(this, result));
		
		return result;
	}
	
	public EntryFile loadAnotherPersonFile(File fileToLoad, Company belongsTo) {
		
		EntryFile result = new EntryFile(fileToLoad);
		
		entries.add(new Person(this, result, belongsTo));
		
		return result;
	}
	
	public Directory getLastLoadedDirectory() {
		return baseDir;
	}
	
	public boolean hasDirectoryBeenLoaded() {
		return baseDir != null;
	}
	
	public List<Entry> getEntries() {
		return entries;
	}
	
	public List<Company> getCompanies() {
	
		List<Company> result = new ArrayList<>();
		
		for (Entry entry : entries) {
			if (entry instanceof Company) {
				result.add((Company) entry);
			}
		}
		
		return result;
	}
	
	public List<Person> getPeople() {
	
		List<Person> result = new ArrayList<>();
		
		for (Entry entry : entries) {
			if (entry instanceof Person) {
				result.add((Person) entry);
			}
		}
		
		return result;
	}
	
	public List<Person> getPeople(Company belongingTo) {
	
		List<Person> result = new ArrayList<>();
		
		for (Entry entry : entries) {
			if (entry instanceof Person) {
				Person person = (Person) entry;
				if (belongingTo.equals(person.getCompany())) {
					result.add(person);
				}
			}
		}
		
		return result;
	}
	
	public void removeEntry(Entry entryToRemove) {
		entries.remove(entryToRemove);
	}
	
	/**
	 * This saves all entries - however, we try to avoid calling this method, as several people might
	 * the using the address book at the same time, and therefore we want to only save when there actually
	 * was a change (therefore, we are going via the EntryTabs which keep track of changes and never call
	 * entryCtrl.save())
	 */
	public void save() {
		
		for (Entry entry : entries) {
			entry.save();
		}
	}

}
