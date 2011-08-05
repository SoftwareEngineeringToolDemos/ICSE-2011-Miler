package org.eclipse.remail.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.remail.Activator;
import org.eclipse.remail.emails.ListSMTPAccount;
import org.eclipse.remail.emails.SMTPAccount;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePaneEmailUser extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public static final String NEW_ELEMENT = "-- New --";
	Text mailAddress;
	Text username;
	Text password;
	Text server;
	Text port;
	Combo accounts;

	ListSMTPAccount storedAccounts;

	public PreferencePaneEmailUser() {
		super();
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("SMTP Account Settings");
		getStoredAccounts();
	}

	/**
	 * Gets the stored accounts in preference and saves them in the list of
	 * SMTPAccount "storedAccounts"
	 */
	private void getStoredAccounts() {
		String s = Activator.getAccounts();
		if (s.equals("") || s.equals(Activator.DEFAULT_ACCOUNTS_SMTP))
			storedAccounts = new ListSMTPAccount();
		else
			storedAccounts = ListSMTPAccount.fromString(s);
	}

	/**
	 * return the accounts names plus "New" at the beginning
	 * 
	 * @return an array of Strings
	 */
	private String[] getArrayAccounts() {
		String[] arr = new String[storedAccounts.length() + 1];
		arr[0] = NEW_ELEMENT;
		String[] arr2 = storedAccounts.toDisplay();
		for (int i = 0; i < arr2.length; i++) {
			arr[i + 1] = arr2[i];
		}
		return arr;
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected void createFieldEditors() {

		Composite panel = new Composite(getFieldEditorParent(), SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		panel.setLayout(layout);

		GridData gd1 = new GridData();
		gd1.horizontalAlignment = GridData.FILL;
		gd1.grabExcessHorizontalSpace = true;

		Label label = new Label(panel, SWT.NONE);
		label.setText("Select an account or 'New' \n" + "to create a new account. ");

		// Combo box for the accounts
		accounts = new Combo(panel, SWT.READ_ONLY);
		accounts.setLayoutData(gd1);
		accounts.setItems(getArrayAccounts());
		accounts.select(0);
		accounts.addModifyListener(new ModifyListener() {			
			@Override
			public void modifyText(ModifyEvent e) {
				updateSelection(accounts.getText());
			}
		});
		
		Rectangle clientArea = panel.getClientArea();
		panel.setBounds(clientArea.x, clientArea.y, 600, 50);

		Group detail = new Group(getFieldEditorParent(), SWT.SHADOW_ETCHED_IN);
		detail.setLayout(layout);
		detail.setText("Account Details");

		// details of the accounts
		Label l1 = new Label(detail, SWT.NONE);
		l1.setText("E-mail address: ");
		mailAddress = new Text(detail, SWT.SINGLE);
		mailAddress.setLayoutData(gd1);
		Label l2 = new Label(detail, SWT.NONE);
		l2.setText("SMTP username: ");
		username = new Text(detail, SWT.SINGLE);
		username.setLayoutData(gd1);
		Label l3 = new Label(detail, SWT.NONE);
		l3.setText("SMTP password: ");
		password = new Text(detail, SWT.SINGLE);
		password.setEchoChar('*');
		password.setLayoutData(gd1);
		Label l4 = new Label(detail, SWT.NONE);
		l4.setText("SMTP server: ");
		server = new Text(detail, SWT.SINGLE);
		server.setLayoutData(gd1);
		Label l5 = new Label(detail, SWT.NONE);
		l5.setText("SMTP port: ");
		port = new Text(detail, SWT.SINGLE);
		port.setLayoutData(gd1);

		Rectangle clientArea2 = detail.getClientArea();
		detail.setBounds(clientArea2.x, clientArea2.y, 600, 200);

	}

	@Override
	public boolean performOk() {
		// updates the accounts with the inserted values
		String name = mailAddress.getText();
		boolean found = false;
		for (int i = 0; i < storedAccounts.length(); i++) {
			SMTPAccount acc = storedAccounts.get(i);
			if (acc.getMailAddress().equals(name)) {
				found = true;
				acc.update(username.getText(), password.getText(), server.getText(), port.getText());
			}
		}
		// not exists, then create a new account
		if (!found) {
			storedAccounts.append(new SMTPAccount(mailAddress.getText(), username.getText(),
					password.getText(), server.getText(), port.getText()));
		}
		// update the preferences
		getPreferenceStore().setValue(Activator.ACCOUNTS_SMTP, storedAccounts.toString());
		return super.performOk();
	}

	/**
	 * Update all the text boxes based on the item selected in the combo box
	 * 
	 * @param selected
	 *            the item selected
	 */
	private void updateSelection(String selected) {
		if (selected.equals(NEW_ELEMENT)){
			// clear the texts
			mailAddress.setText("");
			username.setText("");
			password.setText("");
			server.setText("");
			port.setText("");
		}
		else {
			for (SMTPAccount acc : storedAccounts) {
				if (selected.equals(acc.getMailAddress())) {
					// update the texts
					mailAddress.setText(acc.getMailAddress());
					username.setText(acc.getUsername());
					password.setText(acc.getPassword());
					server.setText(acc.getServer());
					port.setText(acc.getPort());
					break;
				}
			}
		}
	}
}
