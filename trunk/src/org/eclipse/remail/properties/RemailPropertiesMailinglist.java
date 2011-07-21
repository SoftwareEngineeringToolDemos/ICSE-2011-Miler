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
	public Text usernameInput;
	public Text passwordInput;
	public Text againPasswordInput;
	Button okButton;
	private MailingList mailinglist;
	final Shell dialog;
	private boolean usernameOk;
	private boolean passwordOk;
	private boolean againPasswordOk;
	private boolean finish;
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
		usernameOk = false;
		passwordOk = false;
		againPasswordOk = false;
		finish = false;
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
		usernameOk = true;
		passwordOk = true;
		againPasswordOk = true;
		finish = false;
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
		Label usernameLabel = new Label(panel, SWT.NONE);
		usernameLabel.setText("Username: ");
		usernameInput = new Text(panel, SWT.SINGLE);
		Label passwordLabel = new Label(panel, SWT.NONE);
		passwordLabel.setText("Password: ");
		passwordInput = new Text(panel, SWT.SINGLE);
		passwordInput.setEchoChar('*');
		Label retypePassword = new Label(panel, SWT.NONE);
		retypePassword.setText("Retype Password");
		againPasswordInput = new Text(panel, SWT.SINGLE);
		againPasswordInput.setEchoChar('*');

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
		usernameInput.setLayoutData(gd2);
		passwordInput.setLayoutData(gd2);
		againPasswordInput.setLayoutData(gd2);

		// buttons and their properties
		Composite inner = new Composite(dialog, SWT.NONE);
		inner.setLayout(layout);

		Button cancelButton = new Button(inner, SWT.PUSH);
		cancelButton.setText(" Cancel ");
		okButton = new Button(inner, SWT.PUSH);
		okButton.setText("  OK  ");
		okButton.setEnabled(false);

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
				if (usernameInput.getText().length() > 0
						&& passwordInput.getText().length() > 0 && checkPasswordMatch()) {
					// valid input
					String ml= maillistLocationInput.getItem(maillistLocationInput.getSelectionIndex());
					mailinglist.setLocation(ml);				
					mailinglist.setUsername(usernameInput.getText());
					mailinglist.setPassword(passwordInput.getText());
					dialog.dispose();
					finish = true;
					updateList();
				} else {
					// invalid input
					createAlertDialog();
				}
			}
		});

		/*
		 * Listeners for text in order to enable the ok button
		 */
		usernameInput.addListener(SWT.Verify, new Listener() {

			@Override
			public void handleEvent(Event event) {
				int l = usernameInput.getText().length();
				if (event.text != "\b") {
					String s = usernameInput.getText() + event.text;
					l = s.length();
				} else
					l = l - 1;
				if (l > 0)
					usernameOk = true;
				else
					usernameOk = false;
				enableOkButton();
			}
		});
		passwordInput.addListener(SWT.Verify, new Listener() {

			@Override
			public void handleEvent(Event event) {
				int l = passwordInput.getText().length();
				if (event.text != "\b") {
					String s = passwordInput.getText() + event.text;
					l = s.length();
				} else
					l = l - 1;
				if (l > 0)
					passwordOk = true;
				else
					passwordOk = false;
				enableOkButton();
			}
		});
		againPasswordInput.addListener(SWT.Verify, new Listener() {

			@Override
			public void handleEvent(Event event) {
				int l = againPasswordInput.getText().length();
				if (event.text != "\b") {
					String s = againPasswordInput.getText() + event.text;
					l = s.length();
				} else
					l = l - 1;
				if (l > 0)
					againPasswordOk = true;
				else
					againPasswordOk = false;
				enableOkButton();
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
		check=check&&usernameInput.getText().length()>0;
		check=check&&passwordInput.getText().length()>0;
		return usernameOk && passwordOk && againPasswordOk && check;
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
	 * Check if the two passwords matches each other
	 * 
	 * @return true if they are equals false otherwise
	 */
	private boolean checkPasswordMatch() {
		return passwordInput.getText().equals(againPasswordInput.getText());
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

	/**
	 * Tells if the user has finished to insert data, ie has pressed the ok
	 * button ;-)
	 * 
	 * @return true if it has finished, false otherwise
	 */
	public boolean isFinished() {
		return finish;
	}

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
		Label usernameLabel = new Label(panel, SWT.NONE);
		usernameLabel.setText("Username: ");
		usernameInput = new Text(panel, SWT.SINGLE);
		usernameInput.setText(selected.getUsername());
		Label passwordLabel = new Label(panel, SWT.NONE);
		passwordLabel.setText("Password: ");
		passwordInput = new Text(panel, SWT.SINGLE);
		passwordInput.setEchoChar('*');
		passwordInput.setText(selected.getPassword());
		Label retypePassword = new Label(panel, SWT.NONE);
		retypePassword.setText("Retype Password");
		againPasswordInput = new Text(panel, SWT.SINGLE);
		againPasswordInput.setEchoChar('*');
		againPasswordInput.setText(selected.getPassword());

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
		usernameInput.setLayoutData(gd2);
		passwordInput.setLayoutData(gd2);
		againPasswordInput.setLayoutData(gd2);

		// buttons and their properties
		Composite inner = new Composite(dialog, SWT.NONE);
		inner.setLayout(layout);

		Button cancelButton = new Button(inner, SWT.PUSH);
		cancelButton.setText(" Cancel ");
		okButton = new Button(inner, SWT.PUSH);
		okButton.setText("  OK  ");
		okButton.setEnabled(false);

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
				if (usernameInput.getText().length() > 0
						&& passwordInput.getText().length() > 0 && checkPasswordMatch()) {
					// valid input
					
					//remove old
					listMailinglist.remove(selection);
					arrayMailingList.remove(selected);
					//add new
					mailinglist.setLocation(maillistLocationInput.getText());
					mailinglist.setUsername(usernameInput.getText());
					mailinglist.setPassword(passwordInput.getText());
					dialog.dispose();
					finish = true;
					updateList();
				} else {
					// invalid input
					createAlertDialog();
				}
			}
		});

		/*
		 * Listeners for text in order to enable the ok button
		 */
		usernameInput.addListener(SWT.Verify, new Listener() {

			@Override
			public void handleEvent(Event event) {
				int l = usernameInput.getText().length();
				if (event.text != "\b") {
					String s = usernameInput.getText() + event.text;
					l = s.length();
				} else
					l = l - 1;
				if (l > 0)
					usernameOk = true;
				else
					usernameOk = false;
				enableOkButton();
			}
		});
		passwordInput.addListener(SWT.Verify, new Listener() {

			@Override
			public void handleEvent(Event event) {
				int l = passwordInput.getText().length();
				if (event.text != "\b") {
					String s = passwordInput.getText() + event.text;
					l = s.length();
				} else
					l = l - 1;
				if (l > 0)
					passwordOk = true;
				else
					passwordOk = false;
				enableOkButton();
			}
		});
		againPasswordInput.addListener(SWT.Verify, new Listener() {

			@Override
			public void handleEvent(Event event) {
				int l = againPasswordInput.getText().length();
				if (event.text != "\b") {
					String s = againPasswordInput.getText() + event.text;
					l = s.length();
				} else
					l = l - 1;
				if (l > 0)
					againPasswordOk = true;
				else
					againPasswordOk = false;
				enableOkButton();
			}
		});

		dialog.pack();
		dialog.open();
	}
}