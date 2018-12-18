/**
 * Unlicensed code created by A Softer Space, 2018
 * www.asofterspace.com/licenses/unlicense.txt
 */
package com.asofterspace.assAddressBook;

import com.asofterspace.toolbox.configuration.ConfigFile;
import com.asofterspace.toolbox.io.Directory;
import com.asofterspace.toolbox.io.File;
import com.asofterspace.toolbox.gui.Arrangement;
import com.asofterspace.toolbox.gui.GuiUtils;
import com.asofterspace.toolbox.gui.MainWindow;
import com.asofterspace.toolbox.gui.ProgressDialog;
import com.asofterspace.toolbox.Utils;
import com.asofterspace.toolbox.utils.Callback;
import com.asofterspace.toolbox.utils.ProgressIndicator;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;


public class GUI extends MainWindow {

	private EntryCtrl entryCtrl;
	
	private JPanel mainPanelRight;

	private EntryTab currentlyShownTab;

	// on the left hand side, we add this string to indicate that the entry has changed
	private final static String CHANGE_INDICATOR = " *";

	private JMenuItem refreshEntries;
	private JMenuItem addPerson;
	private JMenuItem addCompany;
	private JMenuItem renameCurEntry;
	private JMenuItem deleteCurEntry;
	private JMenuItem saveEntries;
	private JMenuItem close;
	private JCheckBoxMenuItem showPeople;
	private JCheckBoxMenuItem showCompanies;
	private JMenuItem addPersonPopup;
	private JMenuItem addCompanyPopup;
	private JMenuItem renameCurEntryPopup;
	private JMenuItem deleteCurEntryPopup;

	private List<EntryTab> entryTabs;

	private ConfigFile configuration;
	private JList<String> entryListComponent;
	private JPopupMenu entryListPopup;
	private String[] strEntries;
	
	private boolean showPeopleSwitch = true;
	private boolean showCompaniesSwitch = false;


	public GUI(ConfigFile config) {

		configuration = config;

		strEntries = new String[0];

		entryTabs = new ArrayList<>();
		
		entryCtrl = new EntryCtrl();
		
		showPeopleSwitch = configuration.getBoolean("showPeople", true);
		showCompaniesSwitch = configuration.getBoolean("showCompanies", false);
	}

	@Override
	public void run() {

		super.create();

		GuiUtils.maximizeWindow(mainFrame);

		// Add content to the window
		createMenu(mainFrame);

		createPopupMenu(mainFrame);

		createMainPanel(mainFrame);

		configureGUI();

		refreshTitleBar();

		reEnableDisableMenuItems();

		super.show();
		
		refreshAllData();
	}

	private JMenuBar createMenu(JFrame parent) {

		JMenuBar menu = new JMenuBar();

		// TODO :: add undo / redo (for basically any action, but first of all of course for the editor)

		JMenu file = new JMenu("File");
		menu.add(file);
		refreshEntries = new JMenuItem("Refresh All Entries From Shared Disk");
		refreshEntries.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ifAllowedToLeaveCurrentDirectory(new Callback() {
					public void call() {
						refreshAllData();
					}
				});
			}
		});
		file.add(refreshEntries);
		file.addSeparator();
		addPerson = new JMenuItem("Add Person");
		addPerson.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
		addPerson.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openAddNewPersonDialog();
			}
		});
		file.add(addPerson);
		addCompany = new JMenuItem("Add Company");
		addCompany.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		addCompany.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openAddNewCompanyDialog();
			}
		});
		file.add(addCompany);
		file.addSeparator();
		renameCurEntry = new JMenuItem("Rename Current Entry");
		renameCurEntry.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openRenameCurrentEntryDialog();
			}
		});
		file.add(renameCurEntry);
		deleteCurEntry = new JMenuItem("Delete Current Entry");
		deleteCurEntry.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openDeleteCurrentEntryDialog();
			}
		});
		file.add(deleteCurEntry);
		file.addSeparator();
		saveEntries = new JMenuItem("Save All Changed Entries");
		saveEntries.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveEntries();
			}
		});
		file.add(saveEntries);
		file.addSeparator();
		close = new JMenuItem("Close");
		close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ifAllowedToLeaveCurrentDirectory(new Callback() {
					public void call() {
						System.exit(0);
					}
				});
			}
		});
		file.add(close);
		
		JMenu show = new JMenu("Show");
		menu.add(show);
		showPeople = new JCheckBoxMenuItem("Show People");
		showPeople.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setShowPeopleSwitch(!showPeopleSwitch);
			}
		});
		showPeople.setSelected(showPeopleSwitch);
		show.add(showPeople);
		showCompanies = new JCheckBoxMenuItem("Show Companies");
		showCompanies.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setShowCompaniesSwitch(!showCompaniesSwitch);
			}
		});
		showCompanies.setSelected(showCompaniesSwitch);
		show.add(showCompanies);
		
		JMenu huh = new JMenu("?");
		JMenuItem about = new JMenuItem("About");
		about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String aboutMessage = "This is the " + Main.PROGRAM_TITLE + ".\n" +
					"Version: " + Main.VERSION_NUMBER + " (" + Main.VERSION_DATE + ")\n" +
					"Brought to you by: A Softer Space";
				JOptionPane.showMessageDialog(mainFrame, aboutMessage, "About", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		huh.add(about);
		menu.add(huh);

		parent.setJMenuBar(menu);

		return menu;
	}

	private JPopupMenu createPopupMenu(JFrame parent) {

		entryListPopup = new JPopupMenu();

		addPersonPopup = new JMenuItem("Add Person");
		addPersonPopup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openAddNewPersonDialog();
			}
		});
		entryListPopup.add(addPersonPopup);
		addCompanyPopup = new JMenuItem("Add Company");
		addCompanyPopup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openAddNewCompanyDialog();
			}
		});
		entryListPopup.add(addCompanyPopup);
		entryListPopup.addSeparator();
		renameCurEntryPopup = new JMenuItem("Rename Current Entry");
		renameCurEntryPopup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openRenameCurrentEntryDialog();
			}
		});
		entryListPopup.add(renameCurEntryPopup);
		deleteCurEntryPopup = new JMenuItem("Delete Current Entry");
		deleteCurEntryPopup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openDeleteCurrentEntryDialog();
			}
		});
		entryListPopup.add(deleteCurEntryPopup);

		// don't do the following:
		//   entryListComponent.setComponentPopupMenu(popupMenu);
		// instead manually show the popup when the right mouse key is pressed in the mouselistener
		// for the entry list, because that means that we can right click on an entry, select it immediately,
		// and open the popup for exactly that entry

		return entryListPopup;
	}

	private JPanel createMainPanel(JFrame parent) {

	    JPanel mainPanel = new JPanel();
	    mainPanel.setPreferredSize(new Dimension(800, 500));
		GridBagLayout mainPanelLayout = new GridBagLayout();
		mainPanel.setLayout(mainPanelLayout);

	    mainPanelRight = new JPanel();
		mainPanelRight.setLayout(new CardLayout());
		mainPanelRight.setPreferredSize(new Dimension(8, 8));

	    JPanel gapPanel = new JPanel();
	    gapPanel.setPreferredSize(new Dimension(8, 8));

		String[] entryList = new String[0];
		entryListComponent = new JList<String>(entryList);
		entryTabs = new ArrayList<>();

		entryListComponent.addMouseListener(new MouseListener() {

			@Override
		    public void mouseClicked(MouseEvent e) {
				showSelectedTab();
		    }

			@Override
			public void mousePressed(MouseEvent e) {
				showPopupAndSelectedTab(e);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				showPopupAndSelectedTab(e);
			}

			private void showPopupAndSelectedTab(MouseEvent e) {
			    if (e.isPopupTrigger()) {
					entryListComponent.setSelectedIndex(entryListComponent.locationToIndex(e.getPoint()));
					entryListPopup.show(entryListComponent, e.getX(), e.getY());
				}

				showSelectedTab();
			}
		});

        entryListComponent.addKeyListener(new KeyListener() {
		
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) { 
					case KeyEvent.VK_UP:
					case KeyEvent.VK_DOWN:
						showSelectedTab();
						break;
				}
			}
		});
		
		JScrollPane entryListScroller = new JScrollPane(entryListComponent);
		entryListScroller.setPreferredSize(new Dimension(8, 8));
		entryListScroller.setBorder(BorderFactory.createEmptyBorder());

		mainPanel.add(entryListScroller, new Arrangement(0, 0, 0.2, 1.0));

		mainPanel.add(gapPanel, new Arrangement(1, 0, 0.0, 0.0));

	    mainPanel.add(mainPanelRight, new Arrangement(2, 0, 1.0, 1.0));

		parent.add(mainPanel, BorderLayout.CENTER);

	    return mainPanel;
	}

	public void setShowPeopleSwitch(boolean value) {

		showPeopleSwitch = value;
		
		showPeople.setSelected(showPeopleSwitch);

		configuration.set("showPeople", showPeopleSwitch);

		regenerateEntryList();
	}

	public void setShowCompaniesSwitch(boolean value) {

		showCompaniesSwitch = value;
		
		showCompanies.setSelected(showCompaniesSwitch);

		configuration.set("showCompanies", showCompaniesSwitch);

		regenerateEntryList();
	}

	private void showSelectedTab() {

		String selectedItem = (String) entryListComponent.getSelectedValue();

		if (selectedItem == null) {
			return;
		}

		if (selectedItem.endsWith(CHANGE_INDICATOR)) {
			selectedItem = selectedItem.substring(0, selectedItem.length() - CHANGE_INDICATOR.length());
		}

		showTab(selectedItem);
	}

	public void showTab(String name) {

		for (EntryTab tab : entryTabs) {
			if (tab.isItem(name)) {
				tab.show();
				currentlyShownTab = tab;
			} else {
				tab.hide();
			}
		}
	}

	private void configureGUI() {

		/*
		Integer configFontSize = configuration.getInteger(CONFIG_KEY_EDITOR_FONT_SIZE);

		if ((configFontSize != null) && (configFontSize > 0)) {
			currentFontSize = configFontSize;
		}

		GroovyCode.setFontSize(currentFontSize);
		*/
	}

	/*
	private void openLoadEntriesDialog() {

		ifAllowedToLeaveCurrentDirectory(new Callback() {
			public void call() {
				// TODO :: de-localize the JFileChooser (by default it seems localized, which is inconsistent when the rest of the program is in English...)
				// (while you're at it, make Öffnen into Save for the save dialog, but keep it as Open for the open dialog... ^^)
				JFileChooser activeCdmPicker;

				String lastDirectory = configuration.getValue(CONFIG_KEY_LAST_DIRECTORY);

				if ((lastDirectory != null) && !"".equals(lastDirectory)) {
					activeCdmPicker = new JFileChooser(new java.io.File(lastDirectory));
				} else {
					activeCdmPicker = new JFileChooser();
				}

				// TODO :: also allow opening a CDM zipfile

				activeCdmPicker.setDialogTitle("Open a CDM working directory");
				activeCdmPicker.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				int result = activeCdmPicker.showOpenDialog(mainFrame);

				switch (result) {

					case JFileChooser.APPROVE_OPTION:

						clearAllEntryTabs();

						// load the CDM files
						configuration.set(CONFIG_KEY_LAST_DIRECTORY, activeCdmPicker.getCurrentDirectory().getAbsolutePath());
						final Directory cdmDir = new Directory(activeCdmPicker.getSelectedFile());

						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									// add a progress bar (which is especially helpful when the CDM contains no entries
									// so the main view stays empty after loading a CDM!)
									ProgressDialog progress = new ProgressDialog("Loading the CDM directory...");
									entryCtrl.loadCdmDirectory(cdmDir, progress);
								} catch (AttemptingEmfException | CdmLoadingException e) {
									JOptionPane.showMessageDialog(mainFrame, e.getMessage(), "CDM Loading Failed", JOptionPane.ERROR_MESSAGE);
								}

								SwingUtilities.invokeLater(new Runnable() {
									public void run() {
										reloadAllEntryTabs();
									}
								});
							}
						}).start();

						break;

					case JFileChooser.CANCEL_OPTION:
						// cancel was pressed... do nothing for now
						break;
				}
			}
		});
	}
	*/

	private void saveEntries() {

		if (!entryCtrl.hasDirectoryBeenLoaded()) {
			JOptionPane.showMessageDialog(mainFrame, "The entries cannot be saved as no directory has been opened.", "Sorry", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// TODO :: add validation step here, in which we validate that all entries are assigned to activities, and if they are not,
		// then we ask the user explicitly whether we should really save the entries in the current state or not
		// (for this, we can call EntryCtrl.checkValidity())

		// apply all changes, such that the current source code editor contents are actually stored in the CDM file objects
		for (EntryTab entryTab : entryTabs) {
			entryTab.saveIfChanged();
		}

		// remove all change indicators on the left-hand side
		regenerateEntryList();
		
		// DO NOT save all opened files - instead, we called entryTab.saveIfChanged, so we only save un-saved changes!
		// entryCtrl.save();

		JOptionPane.showMessageDialog(mainFrame, "All changed entries have been saved!", "Entries Saved", JOptionPane.INFORMATION_MESSAGE);
	}

	/*
	private void saveEntriesAs() {

		// open a save dialog in which a directory can be picked
		JFileChooser saveCdmPicker;

		String lastDirectory = configuration.getValue(CONFIG_KEY_LAST_DIRECTORY);

		if ((lastDirectory != null) && !"".equals(lastDirectory)) {
			saveCdmPicker = new JFileChooser(new java.io.File(lastDirectory));
		} else {
			saveCdmPicker = new JFileChooser();
		}

		saveCdmPicker.setDialogTitle("Select the new CDM working directory");
		saveCdmPicker.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int result = saveCdmPicker.showOpenDialog(mainFrame);

		switch (result) {

			case JFileChooser.APPROVE_OPTION:

				configuration.set(CONFIG_KEY_LAST_DIRECTORY, saveCdmPicker.getCurrentDirectory().getAbsolutePath());
				Directory cdmDir = new Directory(saveCdmPicker.getSelectedFile());

				// if the new directory does not yet exist, then we have to create it...
				if (!cdmDir.exists()) {
					cdmDir.create();
				}

				// complain if the directory is not empty
				Boolean isEmpty = cdmDir.isEmpty();
				if ((isEmpty == null) || !isEmpty) {
					JOptionPane.showMessageDialog(mainFrame, "The specified directory is not empty - please save into an empty directory!", "Directory Not Empty", JOptionPane.ERROR_MESSAGE);
					saveCdmAs();
					return;
				}

				prepareToSave();

				// for all currently opened CDM files, save them relative to the new directory as they were in the previous one
				entryCtrl.saveTo(cdmDir);

				// also copy over the Manifest file
				// TODO

				for (EntryTab entryTab : entryTabs) {
					entryTab.invalidateInfo();
				}

				refreshTitleBar();

				JOptionPane.showMessageDialog(mainFrame, "The currently opened CDM files have been saved!", "CDM Saved", JOptionPane.INFORMATION_MESSAGE);

				break;

			case JFileChooser.CANCEL_OPTION:
				// cancel was pressed... do nothing for now
				break;
		}
	}
	*/

	private void openAddNewPersonDialog() {
		openAddNewEntryDialog(EntryKind.PERSON);
	}
	
	private void openAddNewCompanyDialog() {
		openAddNewEntryDialog(EntryKind.COMPANY);
	}
	
	private void openAddNewEntryDialog(EntryKind kind) {

		// open a dialog in which the name of the new entry can be entered

		// Create the window
		final JDialog addDialog = new JDialog(mainFrame, "Add " + kind, true);
		GridLayout addDialogLayout = new GridLayout(3, 1);
		if (EntryKind.PERSON.equals(kind)) {
			addDialogLayout = new GridLayout(5, 1);
		}
		addDialogLayout.setVgap(8);
		addDialog.setLayout(addDialogLayout);
		addDialog.getRootPane().setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		// Populate the window
		JLabel explanationLabel = new JLabel();
		explanationLabel.setText("Please enter the name of the new " + kind.toLowerCase() + ":");
		addDialog.add(explanationLabel);

		final JTextField newEntryName = new JTextField();
		if (kind == EntryKind.PERSON) {
			newEntryName.setText("Someone Someonesson");
		} else {
			newEntryName.setText("Some Company Ltd.");
		}
		addDialog.add(newEntryName);
		
		// for a new user, allow selecting a company immediately - so that we know where to save that person!
		JLabel explanationLabelCompany = new JLabel();
		explanationLabelCompany.setText("Please enter the company that this person works for:");

		List<Company> companies = new ArrayList<>(entryCtrl.getCompanies());
		
		Collections.sort(companies, new Comparator<Company>() {
			public int compare(Company a, Company b) {
				return a.getName().toLowerCase().compareTo(b.getName().toLowerCase());
			}
		});

		final String[] companiesArr = new String[companies.size()];
		int i = 0;
		for (Company company : companies) {
			companiesArr[i] = company.getName();
			i++;
		}

		final JComboBox<String> newCompany = new JComboBox<>(companiesArr);
		if (i > 0) {
			newCompany.setSelectedIndex(0);
		}
		newCompany.setEditable(false);
		
		if (EntryKind.PERSON.equals(kind)) {
			addDialog.add(explanationLabelCompany);
			addDialog.add(newCompany);
		}

		JPanel buttonRow = new JPanel();
		GridLayout buttonRowLayout = new GridLayout(1, 2);
		buttonRowLayout.setHgap(8);
		buttonRow.setLayout(buttonRowLayout);
		addDialog.add(buttonRow);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			
				Company belongsToCompany = null;
				
				if (EntryKind.PERSON.equals(kind)) {
					belongsToCompany = companies.get(newCompany.getSelectedIndex());
				}
			
				if (addEntry(kind, newEntryName.getText().trim(), belongsToCompany)) {
					addDialog.dispose();
				}
			}
		});
		buttonRow.add(okButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addDialog.dispose();
			}
		});
		buttonRow.add(cancelButton);

		// Set the preferred size of the dialog
		int width = 450;
		int height = 140;
		if (EntryKind.PERSON.equals(kind)) {
			height = 210;
		}
		addDialog.setSize(width, height);
		addDialog.setPreferredSize(new Dimension(width, height));

		GuiUtils.centerAndShowWindow(addDialog);
	}
	
	private String sanitizeName(String name) {
		
		StringBuilder result = new StringBuilder();
		
		for (int i = 0; i < name.length(); i++) {
			char curChar = name.charAt(i);
			if (Character.isLetter(curChar) || Character.isDigit(curChar)) {
				int isCurCharAscii = name.charAt(i);
				if (isCurCharAscii < 0x80) {
					result.append(curChar);
				}
			}
		}
		
		if (result.length() > 0) {
			return result.toString();
		}
		return "nameless";
	}

	// TODO :: move main part of this to EntryCtrl!
	private boolean addEntry(EntryKind kind, String newEntryName, Company belongsToCompany) {
	
		String origName = sanitizeName(newEntryName);
		String newName = origName;
		
		Directory entryBaseDir = entryCtrl.getLastLoadedDirectory();

		if (EntryKind.PERSON.equals(kind)) {
			entryBaseDir = entryBaseDir.getChildDir(belongsToCompany.getDirectoryName());
		}

		File newFileLocation = new File(entryBaseDir, newName + ".xml");
		
		int counter = 1;

		// check that the new name is not already the file name of some other file!
		while (newFileLocation.exists()) {
			counter++;
			newName = origName + "_" + counter;
			newFileLocation = new File(entryBaseDir, newName + ".xml");
		}

		// add a entry CI with one entry with exactly this name - but do not save it on the hard disk just yet
		String entryCiContent =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<entry createdBy=\"Created by the " + Utils.getFullProgramIdentifier() + "\">\n" +
			"  <kind>" + kind + "</kind>\n" +
			"  <name>" + newEntryName + "</name>\n" +
			"  <details></details>\n";
		
		if (EntryKind.COMPANY.equals(kind)) {
			entryCiContent += "  <directoryName>" + newName + "</directoryName>\n";
		}
			
		entryCiContent += "</entry>";

		File tmpCi = new File("tmpfile.tmp");
		tmpCi.setContent(entryCiContent);
		tmpCi.save();

		// keep track of which entries there were before loading somesuch... (making a shallow copy!)
		List<Entry> entriesBefore = new ArrayList<>(entryCtrl.getEntries());

		// try {
			EntryFile newEntryFile;

			if (EntryKind.PERSON.equals(kind)) {
				newEntryFile = entryCtrl.loadAnotherPersonFile(tmpCi, belongsToCompany);
			} else {
				newEntryFile = entryCtrl.loadAnotherCompanyFile(tmpCi);
			}

			List<Entry> entriesAfter = new ArrayList<>(entryCtrl.getEntries());

			entriesAfter.removeAll(entriesBefore);

			if (entriesAfter.size() != 1) {
				JOptionPane.showMessageDialog(mainFrame, "Oops - while trying to create the new entry, after creating it temporarily, it could not be found!", "Sorry", JOptionPane.ERROR_MESSAGE);
				return true;
			}
			
			newEntryFile.getRoot();

			newEntryFile.setFilelocation(newFileLocation);

			tmpCi.delete();

			// add an entry tab for the new entry as currentlyShownTab
			currentlyShownTab = new EntryTab(mainPanelRight, entriesAfter.iterator().next(), this, entryCtrl);

			currentlyShownTab.setChanged(true);

			// add the new entry to the GUI
			entryTabs.add(currentlyShownTab);

		/*
		} catch (AttemptingEmfException | CdmLoadingException e) {
			JOptionPane.showMessageDialog(mainFrame, "Oops - while trying to create the new entry, after creating it temporarily, it could not be loaded!", "Sorry", JOptionPane.ERROR_MESSAGE);
		}
		*/

		// this also automagically switches to the newly added tab, as it is the currentlyShownTab
		regenerateEntryList();

		reEnableDisableMenuItems();

		return true;
	}

	private void openRenameCurrentEntryDialog() {

		// figure out which entry tab is currently open (show error if none is open)
		if (currentlyShownTab == null) {
			JOptionPane.showMessageDialog(mainFrame, "No entry has been selected, so no entry can be renamed - sorry!", "Sorry", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// open a dialog in which the new name is to be entered (pre-filled with the current name)

		// Create the window
		final JDialog renameDialog = new JDialog(mainFrame, "Rename Entry", true);
		GridLayout renameDialogLayout = new GridLayout(3, 1);
		renameDialogLayout.setVgap(8);
		renameDialog.setLayout(renameDialogLayout);
		renameDialog.getRootPane().setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		// Populate the window
		JLabel explanationLabel = new JLabel();
		explanationLabel.setText("Please enter the new name of the entry file:");
		renameDialog.add(explanationLabel);

		final JTextField newEntryName = new JTextField();
		newEntryName.setText(currentlyShownTab.getName());
		renameDialog.add(newEntryName);

		JPanel buttonRow = new JPanel();
		GridLayout buttonRowLayout = new GridLayout(1, 2);
		buttonRowLayout.setHgap(8);
		buttonRow.setLayout(buttonRowLayout);
		renameDialog.add(buttonRow);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (renameCurrentEntry(newEntryName.getText().trim())) {
					renameDialog.dispose();
				}
			}
		});
		buttonRow.add(okButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				renameDialog.dispose();
			}
		});
		buttonRow.add(cancelButton);

		// Set the preferred size of the dialog
		int width = 350;
		int height = 160;
		renameDialog.setSize(width, height);
		renameDialog.setPreferredSize(new Dimension(width, height));

		GuiUtils.centerAndShowWindow(renameDialog);
	}

	/**
	 * Rename the currently opened entry to the name newEntryStr
	 * @return true if something happened and the dialog should be closed, false if it should stay open
	 */
	private boolean renameCurrentEntry(String newEntryStr) {

		// TODO :: also add a way to rename the associated activity, if an activity is associated, or even the alias

		if ("".equals(newEntryStr)) {
			JOptionPane.showMessageDialog(mainFrame, "Please enter a new name for the entry.", "Enter Name", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		if (currentlyShownTab == null) {
			JOptionPane.showMessageDialog(mainFrame, "The entry cannot be renamed as currently no entry has been opened.", "Sorry", JOptionPane.ERROR_MESSAGE);
			return true;
		}

		// if the name does not change - do nothing... ;)
		String oldEntryStr = currentlyShownTab.getName();
		if (oldEntryStr.equals(newEntryStr)) {
			return true;
		}

		// tell the currently opened entry tab to tell the cdmentry to tell the cdmfile to change the entry name
		// (oh and the entry tab should change its name, and and and...)
		for (EntryTab tab : entryTabs) {
			if (tab.isItem(oldEntryStr)) {
				tab.setName(newEntryStr);
				tab.show();
				currentlyShownTab = tab;
			} else {
				tab.hide();
			}
		}

		// apply changed marker on the left hand side
		regenerateEntryList();

		return true;
	}

	private void openDeleteCurrentEntryDialog() {

		// figure out which entry tab is currently open (show error if none is open)
		if (currentlyShownTab == null) {
			JOptionPane.showMessageDialog(mainFrame, "No entry has been selected, so no entry can be deleted - sorry!", "Sorry", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// open a dialog to confirm that the entry should be deleted

		// Create the window
		String deleteEntry = currentlyShownTab.getName();
		final JDialog deleteDialog = new JDialog(mainFrame, "Delete " + deleteEntry, true);
		GridLayout deleteDialogLayout = new GridLayout(3, 1);
		deleteDialogLayout.setVgap(8);
		deleteDialog.setLayout(deleteDialogLayout);
		deleteDialog.getRootPane().setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		// Populate the window
		JLabel explanationLabel = new JLabel();
		explanationLabel.setText("Do you really want to delete the entry:");
		deleteDialog.add(explanationLabel);

		JLabel entryNameLabel = new JLabel();
		entryNameLabel.setText(deleteEntry);
		deleteDialog.add(entryNameLabel);

		JPanel buttonRow = new JPanel();
		GridLayout buttonRowLayout = new GridLayout(1, 2);
		buttonRowLayout.setHgap(8);
		buttonRow.setLayout(buttonRowLayout);
		deleteDialog.add(buttonRow);

		JButton deleteButton = new JButton("Delete");
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (deleteCurrentEntry()) {
					deleteDialog.dispose();
				}
			}
		});
		buttonRow.add(deleteButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteDialog.dispose();
			}
		});
		buttonRow.add(cancelButton);

		// Set the preferred size of the dialog
		int width = 300;
		int height = 160;
		deleteDialog.setSize(width, height);
		deleteDialog.setPreferredSize(new Dimension(width, height));

		GuiUtils.centerAndShowWindow(deleteDialog);
	}

	/**
	 * Delete the currently opened entry
	 * @return true if something happened and the dialog should be closed, false if it should stay open
	 */
	private boolean deleteCurrentEntry() {

		if (currentlyShownTab == null) {
			JOptionPane.showMessageDialog(mainFrame, "The entry cannot be deleted as currently no entry has been opened.", "Sorry", JOptionPane.ERROR_MESSAGE);
			return true;
		}

		// tell the currently opened entry tab to tell the cdmentry to tell the cdmfile to delete the entry
		// (actually, most likely the whole file has to be deleted, together with potentially the activity mapper
		// entry that attaches the entry to an activity, and possibly even the entire activity... hooray!)
		currentlyShownTab.delete();

		// remove the currently shown tab from the list of existing tabs
		List<EntryTab> oldEntryTabs = entryTabs;

		entryTabs = new ArrayList<>();
		for (EntryTab sT : oldEntryTabs) {
			if (sT != currentlyShownTab) {
				entryTabs.add(sT);
			}
		}

		currentlyShownTab = null;

		// remove entry from the left hand side
		regenerateEntryList();

		reEnableDisableMenuItems();

		return true;
	}

	/**
	 * Regenerate the entry list on the left hand side based on the entryTabs list,
	 * and (if at least one entry exists), select and open the current tab or, if it
	 * is null, the first one
	 */
	public void regenerateEntryList() {

		List<EntryTab> tabs = new ArrayList<>();
		
		for (EntryTab curTab : entryTabs) {
			if (curTab.representsPerson() && showPeopleSwitch) {
				tabs.add(curTab);
			} else {
				if ((!curTab.representsPerson()) && showCompaniesSwitch) {
					tabs.add(curTab);
				}
			}
		}

		Collections.sort(tabs, new Comparator<EntryTab>() {
			public int compare(EntryTab a, EntryTab b) {
				return a.getName().toLowerCase().compareTo(b.getName().toLowerCase());
			}
		});

		strEntries = new String[tabs.size()];

		int i = 0;

		for (EntryTab entryTab : tabs) {
			strEntries[i] = entryTab.getName();
			if (entryTab.hasBeenChanged()) {
				strEntries[i] += CHANGE_INDICATOR;
			}
			i++;
		}

		entryListComponent.setListData(strEntries);

		// if there is no last shown tab...
		if (currentlyShownTab == null) {
			// ... show the first tab explicitly - this is fun, and the tabbed layout otherwise shows it anyway, so may as well...
			if (tabs.size() > 0) {
				currentlyShownTab = tabs.get(0);
			}
		}

		// if there still is no last shown tab (e.g. we just deleted the very last one)...
		if (currentlyShownTab == null) {
			// ... then we do not need to show or highlight any ;)
			return;
		}

		// show the last shown tab
		showTab(currentlyShownTab.getName());

		highlightTabInLeftList(currentlyShownTab.getName());
	}

	public void highlightTabInLeftList(String name) {

		int i = 0;

		for (String strEntry : strEntries) {
		
			if (strEntry.endsWith(CHANGE_INDICATOR)) {
				strEntry = strEntry.substring(0, strEntry.length() - CHANGE_INDICATOR.length());
			}

			if (name.equals(strEntry)) {
				entryListComponent.setSelectedIndex(i);
				break;
			}
			i++;
		}
	}

	/**
	 * Enable and disable menu items related to the current state of the application,
	 * e.g. if no CDM is loaded at all, do not enable the user to add entries to the
	 * current CDM, etc.
	 */
	private void reEnableDisableMenuItems() {

		boolean dirLoaded = entryCtrl.hasDirectoryBeenLoaded();
		
		boolean companiesExist = entryCtrl.getCompanies().size() > 0;

		boolean entriesExist = entryTabs.size() > 0;

		boolean entryIsSelected = currentlyShownTab != null;

		// enabled and disable menu items according to the state of the application
		refreshEntries.setEnabled(dirLoaded);
		saveEntries.setEnabled(dirLoaded);
		// saveEntriesAs.setEnabled(dirLoaded);
		addPerson.setEnabled(companiesExist);
		addPersonPopup.setEnabled(companiesExist);
		addCompany.setEnabled(dirLoaded);
		addCompanyPopup.setEnabled(dirLoaded);
		renameCurEntry.setEnabled(entryIsSelected);
		renameCurEntryPopup.setEnabled(entryIsSelected);
		deleteCurEntry.setEnabled(entryIsSelected);
		deleteCurEntryPopup.setEnabled(entryIsSelected);
	}
	
	private void refreshAllData() {
	
		entryCtrl.loadDirectory(new Directory("data"));
						
		reloadAllEntryTabs();
	}

	private void refreshTitleBar() {

		Directory lastLoadedDir = entryCtrl.getLastLoadedDirectory();

		if (lastLoadedDir == null) {
			mainFrame.setTitle(Main.PROGRAM_TITLE);
		} else {
			mainFrame.setTitle(Main.PROGRAM_TITLE + " - " + lastLoadedDir.getDirname());
		}
	}

	private void clearAllEntryTabs() {

		// remove old entry tabs
		for (EntryTab entryTab : entryTabs) {
			entryTab.remove();
		}
		strEntries = new String[0];
		entryTabs = new ArrayList<>();
		entryListComponent.setListData(strEntries);
		currentlyShownTab = null;

		mainPanelRight.repaint();
	}

	private void reloadAllEntryTabs() {
	
		if (entryTabs != null) {
			for (EntryTab entryTab : entryTabs) {
				entryTab.remove();
			}
		}

		// update the entry list on the left and load the new entry tabs
		entryTabs = new ArrayList<>();

		List<Entry> entries = entryCtrl.getEntries();
		for (Entry entry : entries) {
			entryTabs.add(new EntryTab(mainPanelRight, entry, this, entryCtrl));
		}

		regenerateEntryList();

		reEnableDisableMenuItems();

		refreshTitleBar();
	}

	/**
	 * Check if currently entries are loaded, and if so then if files have been changed,
	 * and if yes ask the user if we want to save first, proceed, or cancel
	 * return true if we saved or proceed anyway, and false if we cancel
	 */
	private void ifAllowedToLeaveCurrentDirectory(final Callback proceedWithThisIfAllowed) {

		// check all entries; if any have been changed, ask first before closing!
		boolean noneHaveBeenChanged = true;

		for (EntryTab entryTab : entryTabs) {
			if (entryTab.hasBeenChanged()) {
				noneHaveBeenChanged = false;
				break;
			}
		}

		// if none have been changed, then we are allowed to proceed in any case :)
		if (noneHaveBeenChanged) {
			proceedWithThisIfAllowed.call();
			return;
		}

		// okay, something has been changed, so we now want to ask the user about what to do...

		// Create the window
		final JDialog whatToDoDialog = new JDialog(mainFrame, "What to do?", true);
		GridLayout whatToDoDialogLayout = new GridLayout(2, 1);
		whatToDoDialogLayout.setVgap(8);
		whatToDoDialog.setLayout(whatToDoDialogLayout);
		whatToDoDialog.getRootPane().setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		// Populate the window
		JLabel explanationLabel = new JLabel();
		explanationLabel.setText("The currently loaded entries have been modified - what do you want to do?");
		whatToDoDialog.add(explanationLabel);

		JPanel buttonRow = new JPanel();
		GridLayout buttonRowLayout = new GridLayout(1, 3);
		buttonRowLayout.setHgap(8);
		buttonRow.setLayout(buttonRowLayout);
		whatToDoDialog.add(buttonRow);

		JButton saveButton = new JButton("Save, then Proceed");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveEntries();
				whatToDoDialog.dispose();
				proceedWithThisIfAllowed.call();
			}
		});
		buttonRow.add(saveButton);

		JButton proceedButton = new JButton("Proceed without Saving");
		proceedButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				whatToDoDialog.dispose();
				proceedWithThisIfAllowed.call();
			}
		});
		buttonRow.add(proceedButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				whatToDoDialog.dispose();
			}
		});
		buttonRow.add(cancelButton);

		// Set the preferred size of the dialog
		int width = 600;
		int height = 120;
		whatToDoDialog.setSize(width, height);
		whatToDoDialog.setPreferredSize(new Dimension(width, height));

		GuiUtils.centerAndShowWindow(whatToDoDialog);
	}

}
