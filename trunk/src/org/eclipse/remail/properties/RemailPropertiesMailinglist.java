package org.eclipse.remail.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Static class providing the GUI to insert a new mailing list
 * relative to the project.
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 *
 */
public class RemailPropertiesMailinglist {

	public static Text maillistLocationInput;
	public static Text usernameInput;
	public static Text passwordInput;
	
	/**
	 * Create a dialog for adding a mailing list
	 */
	public static void createAddDialog() {
		
		final boolean [] result = new boolean [1];
		final Shell dialog = new Shell (SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setText("Mailing List configuration");
		dialog.setMinimumSize(500, 300);
		dialog.setLayout(new GridLayout());
		
		//a nice group
		Group panel = new Group(dialog, SWT.SHADOW_ETCHED_IN);
		panel.setText("Add a new mailing list to the project");
		GridLayout layout= new GridLayout(2, false);
		panel.setLayout(layout);
		
		//input 
		Label maillistLocationLabel = new Label(panel, SWT.NONE);
		maillistLocationLabel.setText("Mailing list location: ");
		maillistLocationInput = new Text(panel, SWT.SINGLE);
		Label usernameLabel = new Label(panel, SWT.NONE);
		usernameLabel.setText("Username: ");
		usernameInput = new Text(panel, SWT.SINGLE);
		Label passwordLabel = new Label(panel, SWT.NONE);
		passwordLabel.setText("Password: ");
		passwordInput = new Text(panel, SWT.SINGLE);
		passwordInput.setEchoChar('*');
		
		//Set properties for the group
		GridData gd1 = new GridData();
		gd1.horizontalAlignment = GridData.FILL;
		gd1.grabExcessHorizontalSpace = true;
		gd1.verticalAlignment= GridData.FILL;
		gd1.grabExcessVerticalSpace=true;
		panel.setLayoutData(gd1);
		
		//set properties for the text
		GridData gd2= new GridData();
		gd2.horizontalAlignment = GridData.FILL;
		gd2.grabExcessHorizontalSpace = true;
		maillistLocationInput.setLayoutData(gd2);
		usernameInput.setLayoutData(gd2);
		passwordInput.setLayoutData(gd2);
		
		
		//buttons and their properties
		Composite inner = new Composite(dialog, SWT.NONE);
		inner.setLayout(layout);
		
		Button cancelButton = new Button(inner, SWT.PUSH);
		cancelButton.setText(" Cancel ");
		Button okButton = new Button(inner, SWT.PUSH);
		okButton.setText("  OK  ");
		
		GridData gd3= new GridData();
		gd3.horizontalAlignment= SWT.END;
		inner.setLayoutData(gd3);		
		
		/*
		 * Listener for cancel button
		 */
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				dialog.dispose();
			}
		});
		
		/*
		 * Listener for ok button
		 */
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				dialog.dispose();
			}
		});
		
		dialog.pack ();
		dialog.open ();
	
	}
}
