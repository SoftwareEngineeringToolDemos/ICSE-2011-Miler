package org.eclipse.remail.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.remail.Activator;
import org.eclipse.swt.SWT;
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
	
	Text mailAddress;
	Text username;
	Text password;
	Text server;
	Text port;
	
	Combo accounts;
	
	public PreferencePaneEmailUser(){
		super();
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("SMTP Account Settings");
	}

	@Override
	public void init(IWorkbench workbench) {
		
	}

	@Override
	protected void createFieldEditors() {
		
		Composite panel = new Composite(getFieldEditorParent(), SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		panel.setLayout(layout);
		
		GridData gd1= new GridData();
		gd1.horizontalAlignment=GridData.FILL;
		gd1.grabExcessHorizontalSpace=true;
		
		Label label= new Label(panel, SWT.NONE);
		label.setText("Select an account or 'New' \n"+"to create a new account. ");
		
		//Combo box for the accounts
		accounts = new Combo(panel, SWT.READ_ONLY);
		String[] accs = new String[]{"-- New --", "Foo", "FooasdnasjdjsadnsajBar"};
		accounts.setLayoutData(gd1);
		accounts.setItems(accs);
		accounts.select(0);
		Rectangle clientArea = panel.getClientArea();
		panel.setBounds(clientArea.x, clientArea.y, 600, 50);
		
		Group detail = new Group(getFieldEditorParent(), SWT.SHADOW_ETCHED_IN);
		detail.setLayout(layout);
		detail.setText("Account Details");

		//details of the accounts
		Label l1= new Label(detail, SWT.NONE);
		l1.setText("E-mail address: ");
		mailAddress=new Text(detail, SWT.SINGLE);
		mailAddress.setLayoutData(gd1);
		Label l2= new Label(detail, SWT.NONE);
		l2.setText("SMTP username: ");
		username=new Text(detail, SWT.SINGLE); 
		username.setLayoutData(gd1);
		Label l3= new Label(detail, SWT.NONE);
		l3.setText("SMTP password: ");
		password=new Text(detail, SWT.SINGLE);
		password.setEchoChar('*');
		password.setLayoutData(gd1);
		Label l4= new Label(detail, SWT.NONE);
		l4.setText("SMTP server: ");
		server=new Text(detail, SWT.SINGLE);
		server.setLayoutData(gd1);
		Label l5= new Label(detail, SWT.NONE);
		l5.setText("SMTP port: ");
		port=new Text(detail, SWT.SINGLE);
		port.setLayoutData(gd1);
		
		Rectangle clientArea2 = detail.getClientArea();
		detail.setBounds(clientArea2.x, clientArea2.y, 600, 200);
		
	}

	

}
