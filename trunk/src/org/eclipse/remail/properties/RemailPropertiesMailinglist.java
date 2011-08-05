package org.eclipse.remail.properties;

import java.util.LinkedHashSet;

import javax.swing.JOptionPane;

import org.eclipse.remail.couchdb.util.CouchDBDatabases;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Static class providing the GUI to insert a new mailing list relative to the
 * project.
 * 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 * 
 */
public class RemailPropertiesMailinglist {

	public Combo maillistLocationInput;
	
	Button okButton;
	private MailingList mailinglist;
	final Shell dialog;
	
//	private boolean finish;
	private List listMailinglist;
	private LinkedHashSet<MailingList> arrayMailingList;
	private String selection;
	MailingList selected;

	/**
	 * Constructor that should be used to Add a mailing list to the list
	 * 
	 * @param m
	 *            mailing list
	 * @param listMailinglist
	 *            the list widgets
	 * @param arrayMailingList
	 *            the list of MailingList object
	 */
	public RemailPropertiesMailinglist(MailingList m, List listMailinglist,
			LinkedHashSet<MailingList> arrayMailingList) {
		mailinglist = m;
		dialog = new Shell(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
//		finish = false;
		this.listMailinglist = listMailinglist;
		this.arrayMailingList = arrayMailingList;
		dialog.setText("Mailing List configuration");
		dialog.setMinimumSize(500, 300);
		dialog.setLayout(new GridLayout());
	}

	/**
	 * Constructor that should be used to Edit a mailing list to the list
	 * 
	 * @param m
	 *            mailing list
	 * @param listMailinglist
	 *            the list widgets
	 * @param arrayMailingList
	 *            the list of MailingList object
	 * @param selection
	 *            the selected item
	 */
	public RemailPropertiesMailinglist(MailingList m, List listMailinglist,
			LinkedHashSet<MailingList> arrayMailingList, String selection) {
		mailinglist = m;
		dialog = new Shell(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

//		finish = false;
		this.listMailinglist = listMailinglist;
		this.arrayMailingList = arrayMailingList;
		this.selection = selection;
		dialog.setText("Mailing List configuration");
		dialog.setMinimumSize(500, 300);
		dialog.setLayout(new GridLayout());
	}

	/**
	 * Create a dialog for adding a mailing list
	 */
	public void createAddDialog() {

		// a nice group
		Group panel = new Group(dialog, SWT.SHADOW_ETCHED_IN);
		panel.setText("Add a new mailing list to the project");
		GridLayout layout = new GridLayout(2, false);
		panel.setLayout(layout);

		// input
		Label maillistLocationLabel = new Label(panel, SWT.NONE);
		maillistLocationLabel.setText("Mailing list location: ");
		maillistLocationInput = new Combo(panel, SWT.READ_ONLY);
		String[] arrDB=(new CouchDBDatabases()).getArrayOfDatabases();
		arrDB=CouchDBDatabases.fromRealNameToNiceName(arrDB);
		maillistLocationInput.setItems(arrDB);
		maillistLocationInput.select(0);
	

		// Set properties for the group
		GridData gd1 = new GridData();
		gd1.horizontalAlignment = GridData.FILL;
		gd1.grabExcessHorizontalSpace = true;
		gd1.verticalAlignment = GridData.FILL;
		gd1.grabExcessVerticalSpace = true;
		panel.setLayoutData(gd1);

		// set properties for the text
		GridData gd2 = new GridData();
		gd2.horizontalAlignment = GridData.FILL;
		gd2.grabExcessHorizontalSpace = true;
		maillistLocationInput.setLayoutData(gd2);

		// buttons and their properties
		Composite inner = new Composite(dialog, SWT.NONE);
		inner.setLayout(layout);

		Button cancelButton = new Button(inner, SWT.PUSH);
		cancelButton.setText(" Cancel ");
		okButton = new Button(inner, SWT.PUSH);
		okButton.setText("  OK  ");
//		okButton.setEnabled(false);

		GridData gd3 = new GridData();
		gd3.horizontalAlignment = SWT.END;
		inner.setLayoutData(gd3);

		/*
		 * Listener for cancel button
		 */
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				mailinglist = new MailingList();
				dialog.dispose();
			}
		});

		/*
		 * Listener for ok button
		 */
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
					String ml= maillistLocationInput.getItem(maillistLocationInput.getSelectionIndex());
					mailinglist.setLocation(ml);				
					dialog.dispose();
//					finish = true;
					updateList();
				
			}
		});

		dialog.pack();
		dialog.open();

	}

	/**
	 * Update the list
	 */
	protected void updateList() {
		arrayMailingList.remove(mailinglist);
		arrayMailingList.add(mailinglist);
		String[] elems = listMailinglist.getItems();
		boolean has = false;
		for (String s : elems) {
			if (s.equals(mailinglist.toString()))
				has = true;
		}
		if (!has) {
			listMailinglist.add(mailinglist.toString());
			listMailinglist.update();
			listMailinglist.redraw();
		}
	}

	/**
	 * Check if the input values are ok!
	 * 
	 * @return true if they are not empty, false otherwise
	 */
	private boolean checkInput() {
		boolean check=maillistLocationInput.getText().length()>0;
	
		return check;
	}

	/**
	 * Enable the ok button
	 */
	private void enableOkButton() {
		if (checkInput()) {
			okButton.setEnabled(true);
			dialog.redraw();
		} else {
			okButton.setEnabled(false);
			dialog.redraw();
		}
	}

	
	/**
	 * Crate an alert dialog which is used to inform user that the input values
	 * are not correct
	 */
	private void createAlertDialog() {
		java.awt.Label message = new java.awt.Label("The values provided are not correct. \n"
				+ "It can be that the mailing list location is not a valid url "
				+ "or passwords doesn't match. \n\n" + "Please, go back and check!");
		JOptionPane.showMessageDialog(null, message, "Wrong inputs!", JOptionPane.ERROR_MESSAGE);
	}

	public MailingList getMailingList() {
		return mailinglist;
	}

//	/**
//	 * Tells if the user has finished to insert data, ie has pressed the ok
//	 * button ;-)
//	 * 
//	 * @return true if it has finished, false otherwise
//	 */
//	public boolean isFinished() {
//		return finish;
//	}

	/**
	 * Creates the Edit dialog 
	 */
	public void createEditDialog() {
		
		//get the selection item from the real list of mailing list
		selected=null;
		for (MailingList m : arrayMailingList)
			if(m.getLocation().equals(selection))
				selected=m;
		
		// a nice group
		Group panel = new Group(dialog, SWT.SHADOW_ETCHED_IN);
		panel.setText("Add a new mailing list to the project");
		GridLayout layout = new GridLayout(2, false);
		panel.setLayout(layout);

		// input
		Label maillistLocationLabel = new Label(panel, SWT.NONE);
		maillistLocationLabel.setText("Mailing list location: ");
		maillistLocationInput = new Combo(panel, SWT.READ_ONLY);
		String[] arrDB=(new CouchDBDatabases()).getArrayOfDatabases();
		arrDB=CouchDBDatabases.fromRealNameToNiceName(arrDB);
		int index=0;
		for(int i=0;i<arrDB.length; i++)
			if(arrDB[i].equals(selected.getLocation()))
				index=i;
		maillistLocationInput.setItems(arrDB);
		maillistLocationInput.select(index);
		maillistLocationInput.setEnabled(false);

		// Set properties for the group
		GridData gd1 = new GridData();
		gd1.horizontalAlignment = GridData.FILL;
		gd1.grabExcessHorizontalSpace = true;
		gd1.verticalAlignment = GridData.FILL;
		gd1.grabExcessVerticalSpace = true;
		panel.setLayoutData(gd1);

		// set properties for the text
		GridData gd2 = new GridData();
		gd2.horizontalAlignment = GridData.FILL;
		gd2.grabExcessHorizontalSpace = true;
		maillistLocationInput.setLayoutData(gd2);

		// buttons and their properties
		Composite inner = new Composite(dialog, SWT.NONE);
		inner.setLayout(layout);

		Button cancelButton = new Button(inner, SWT.PUSH);
		cancelButton.setText(" Cancel ");
		okButton = new Button(inner, SWT.PUSH);
		okButton.setText("  OK  ");
//		okButton.setEnabled(false);

		GridData gd3 = new GridData();
		gd3.horizontalAlignment = SWT.END;
		inner.setLayoutData(gd3);
		
		/*
		 * Listener for cancel button
		 */
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				mailinglist = new MailingList();
				dialog.dispose();
			}
		});

		/*
		 * Listener for ok button
		 */
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
		
					//remove old
					listMailinglist.remove(selection);
					arrayMailingList.remove(selected);
					//add new
					mailinglist.setLocation(maillistLocationInput.getText());
					dialog.dispose();
//					finish = true;
					updateList();
			
			}
		});	

		dialog.pack();
		dialog.open();
	}
}