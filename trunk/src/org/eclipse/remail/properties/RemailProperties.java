package org.eclipse.remail.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

/**
 * Class used to set the REmail properties of a project,
 * such as the mailing lists relative to that project and
 * the username, password for each mailing list
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 *
 */
public class RemailProperties extends PropertyPage implements
                 IWorkbenchPropertyPage {

	List listMailinglist;
	Button addButton;
	Button editButton;
	Button removeButton;
	
	@Override
	protected Control createContents(Composite parent) {
		Group panel = new Group(parent, SWT.SHADOW_ETCHED_IN);
		GridLayout layout= new GridLayout();
		panel.setLayout(layout);
		panel.setText("List of mailing list relative to the project");
		
		GridData gd1 = new GridData();
		gd1.verticalAlignment=SWT.BEGINNING;
		panel.setLayoutData(gd1);
				
		//create the place where to put the list and buttons
		Composite innerPanel = new Composite(panel, SWT.NONE);
		GridLayout innerLayout = new GridLayout(2, false);
		innerPanel.setLayout(innerLayout);
		listMailinglist = new List (innerPanel, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		GridData gd2 = new GridData();
		gd2.horizontalAlignment = GridData.FILL;
		gd2.grabExcessHorizontalSpace = true;
		gd2.verticalAlignment= GridData.FILL;
		gd2.grabExcessVerticalSpace=true;
		innerPanel.setLayoutData(gd2);
		listMailinglist.setLayoutData(gd2);
		
		//create the place where to put buttons
		Composite buttonPanel = new Composite(innerPanel, SWT.NONE);
		GridLayout buttonLayout= new GridLayout();
		buttonPanel.setLayout(buttonLayout);
		addButton = new Button(buttonPanel, SWT.PUSH);
		addButton.setText("Add");
		editButton = new Button(buttonPanel, SWT.PUSH);
		editButton.setText("Edit");
		editButton.setEnabled(false);
		removeButton = new Button(buttonPanel, SWT.PUSH);
		removeButton.setText("Remove");
		removeButton.setEnabled(false);

		/*
		 * Listener For the list of mails
		 */
		listMailinglist.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		/*
		 * Listener for the add button
		 */
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				RemailPropertiesMailinglist.createAddDialog();
			}
		});
		
		return panel;
	}

}
