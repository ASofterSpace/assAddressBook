package com.asofterspace.assAddressBook;

import com.asofterspace.toolbox.configuration.ConfigFile;
import com.asofterspace.toolbox.io.Directory;
import com.asofterspace.toolbox.io.File;
import com.asofterspace.toolbox.gui.Arrangement;
import com.asofterspace.toolbox.gui.GuiUtils;
import com.asofterspace.toolbox.utils.Callback;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.border.CompoundBorder;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;


public class EntryTab {

	private JPanel parent;

	private JPanel visualPanel;
	
	private Entry entry;
	
	private EntryCtrl entryCtrl;

	private GUI gui;

	private Callback onChangeCallback;

	private boolean changed = false;

	// graphical components
	private JLabel nameLabel;
	private JTextPane detailsMemo;


	public EntryTab(JPanel parentPanel, Entry entry, final GUI gui, EntryCtrl entryCtrl) {

		this.parent = parentPanel;

		this.entry = entry;

		this.entryCtrl = entryCtrl;
		
		this.gui = gui;
		
		this.onChangeCallback = new Callback() {
			public void call() {
				if (!changed) {
					changed = true;
					gui.regenerateEntryList();
				}
			}
		};

		visualPanel = createVisualPanel();
	}

	private JPanel createVisualPanel() {

		JPanel tab = new JPanel();
		tab.setLayout(new GridBagLayout());

		nameLabel = new JLabel(entry.getName());
		nameLabel.setPreferredSize(new Dimension(0, nameLabel.getPreferredSize().height));
		tab.add(nameLabel, new Arrangement(0, 0, 1.0, 0.0));

		detailsMemo = new JTextPane() {
			public boolean getScrollableTracksViewportWidth() {
				return getUI().getPreferredSize(this).width <= getParent().getSize().width;
			}
		};
		DetailsHighlighter highlighter = new DetailsHighlighter(detailsMemo);
		detailsMemo.setText(entry.getDetails());
		highlighter.setOnChange(onChangeCallback);
		JScrollPane sourceCodeScroller = new JScrollPane(detailsMemo);
		sourceCodeScroller.setPreferredSize(new Dimension(1, 1));
		tab.add(sourceCodeScroller, new Arrangement(0, 1, 1.0, 0.8));

		parent.add(tab);

		tab.setVisible(false);

		// scroll to the top
		detailsMemo.setCaretPosition(0);

	    return tab;
	}

	public Entry getEntry() {
		return entry;
	}

	public boolean isItem(String item) {

		if (item == null) {
			return false;
		}

		if (entry == null) {
			return false;
		}

		return item.equals(entry.getName());
	}

	public boolean hasBeenChanged() {

		return changed;
	}

	public String getName() {

		return entry.getName();
	}

	public void setName(String newName) {

		nameLabel.setText(newName);

		changed = true;

		entry.setName(newName);
	}

	public void setChanged(boolean changed) {

		this.changed = changed;
	}

	public void show() {

		visualPanel.setVisible(true);
	}

	public void hide() {

		visualPanel.setVisible(false);
	}

	public void setEntryDetailsContent(String newEntryDetailsContent) {

		// set the new entry content (without saving it anywhere)
		detailsMemo.setText(newEntryDetailsContent);

		// scroll to the top
		detailsMemo.setCaretPosition(0);
	}

	public void applyChanges() {

		entry.setDetails(detailsMemo.getText());

		changed = false;
	}

	public void remove() {

		parent.remove(visualPanel);
	}

	public void delete() {

		// even after calling delete, we do not set the entry to null, as we want to be able
		// to call save() later - and THEN actually delete the file on disk!
		entry.delete();

		remove();
	}

}
