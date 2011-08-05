package org.eclipse.remail.properties;

import java.util.Iterator;
import java.util.LinkedHashSet;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.remail.couchdb.util.CouchDBCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * Class used to set the REmail properties of a project,
 * such as the mailing lists relative to that project and
 * the username, password for each mailing list
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 *
 */
public class RemailProperties extends PropertyPage implements
                 IWorkbenchPropertyPage {

	protected List listMailinglist;
	protected LinkedHashSet<MailingList> arrayMailingList = new LinkedHashSet<MailingList>();
	Button addButton;
	Button editButton;
	Button removeButton;
	
	public static final QualifiedName REMAIL_MAILING_LIST = new 
			QualifiedName("org.eclipse.remail", 
			"MAIL_LIST");
	
	private void setUp(){
		try {
			String property=((IResource)getElement()).getPersistentProperty(REMAIL_MAILING_LIST);
			if(property!=null)
				arrayMailingList=MailingList.stringToList(property);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected Control createContents(Composite parent) {
		setUp();
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
		for(MailingList m : arrayMailingList){
			listMailinglist.add(m.toString());
		}
		
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
				String[] sel = listMailinglist.getSelection();
				if(sel.length==0){
					editButton.setEnabled(false);
					removeButton.setEnabled(false);
				}
				if(sel.length==1){
					editButton.setEnabled(true);
					removeButton.setEnabled(true);
				}
				if(sel.length>1){
					editButton.setEnabled(false);
					removeButton.setEnabled(true);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				//We don't need a default selection!
			}
		});
		
		/*
		 * Listener for the add button
		 */
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				RemailPropertiesMailinglist rpm= new RemailPropertiesMailinglist(new MailingList(), listMailinglist, arrayMailingList);
				rpm.createAddDialog();
			}
		});
				
		/*
		 * Listener for the edit button
		 */
		editButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String[] sel = listMailinglist.getSelection();
				RemailPropertiesMailinglist rpm= new RemailPropertiesMailinglist(new MailingList(), listMailinglist, arrayMailingList, sel[0]);
				rpm.createEditDialog();
			}
		});
		
		/*
		 * Listener for the remove button
		 */
		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String[] sel = listMailinglist.getSelection();
				for(String s : sel){
					Iterator<MailingList> it = arrayMailingList.iterator();
					while(it.hasNext()){
						MailingList m = it.next();
						if(m.equals(new MailingList(s)))//, "", "")))
							it.remove();
					}
					listMailinglist.remove(s);
				}
			}
		});
		return panel;
	}
	
	@Override
	public boolean performOk() {
		try {
			//set the properties
			((IResource) getElement()).setPersistentProperty(
					REMAIL_MAILING_LIST,
					MailingList.listToString(arrayMailingList));
			System.out.println(MailingList.listToString(arrayMailingList));
			
			//create databases in couchdb
			for(MailingList ml : arrayMailingList){
				String mailList=ml.getLocation();
				mailList=mailList.replace(".", "_");
				mailList=mailList.replace("@", "-");
				System.out.println("Creating database "+mailList);
				CouchDBCreator create = new CouchDBCreator(mailList);
				boolean r=create.createDatabase();
				System.out.println(r);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return super.performOk();
	}
}
