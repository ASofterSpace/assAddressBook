package com.asofterspace.assAddressBook;

import com.asofterspace.toolbox.io.Directory;
import com.asofterspace.toolbox.io.File;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class EntryCtrl {

	private Directory baseDir;
	
	private Set<Entry> entries;
	

	public EntryCtrl () {
	
		baseDir = null;
		
		entries = new HashSet<>();
	}
	
	public void loadDirectory(Directory baseDir) {

		this.baseDir = baseDir;
		
		baseDir.create();
		
		entries = new HashSet<>();
		
		// all files directly inside the base dir are company files (then inside the companies are the actual people files)
		List<File> companyFiles = baseDir.getAllFiles(false);
		
		for (File companyFile : companyFiles) {
			
			Company curCompany = new Company(new EntryFile(companyFile));
			
			entries.add(curCompany);
			
			Directory curCompanyDir = baseDir.createChildDir(curCompany.getDirectoryName());
			
			List<File> peopleFiles = curCompanyDir.getAllFiles(false);
			
			for (File peopleFile : peopleFiles) {
				entries.add(new Person(new EntryFile(peopleFile)));
			}
		}
	}
	
	public EntryFile loadAnotherEntryFile(File fileToLoad, EntryKind kind) {
		
		EntryFile result = new EntryFile(fileToLoad);
		
		if (EntryKind.PERSON.equals(kind)) {
			entries.add(new Person(result));
		} else {
			entries.add(new Company(result));
		}
		
		return result;
	}
	
	public Directory getLastLoadedDirectory() {
		return baseDir;
	}
	
	public boolean hasDirectoryBeenLoaded() {
		return baseDir != null;
	}
	
	public Set<Entry> getEntries() {
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
	
	public void save() {
		
		for (Entry entry : entries) {
			entry.save();
		}
	}

}
