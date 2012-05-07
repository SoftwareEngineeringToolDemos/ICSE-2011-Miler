package org.eclipse.remail.views;

import java.util.List;

import org.eclipse.jface.preference.PathEditor;
import org.eclipse.remail.Activator;
import org.eclipse.remail.emails.EmailChecker;
import org.eclipse.remail.emails.EmailSender;
import org.eclipse.remail.emails.ListSMTPAccount;
import org.eclipse.remail.preferences.FilePathEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

/**
 * Class used to provide to the user a nice interface for writing e-mails
 * 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 * 
 */
public class MailWriter extends ViewPart {

	ListSMTPAccount storedAccounts;

	// Buttons
	private Button sendButton;
	private Button attachButton;

	// Text fields
	private Combo fromField;
	private Text toField;
	private Text ccField;
	private Text bccField;
	private Text subjectField;
	private StyledText contentField;
	private StyledText keywordsField;

	// keyword list
	private String keywords;

	private CoolBar buttonBar;
	private org.eclipse.swt.widgets.List attachList;

	/**
	 * Empty constructor. Should never be used explicitly
	 */
	public MailWriter() {
		super();
		keywords = "";
		getStoredAccounts();
	}

	public MailWriter(List<String> keywordList) {
		super();
		keywords = "";
		for (String s : keywordList)
			keywords += s + ", ";
		getStoredAccounts();

	}

	/**
	 * Set the keyword list to the given list
	 * 
	 * @param keywordList
	 *            the list
	 */
	public void setKeywords(List<String> keywordList) {
		keywords = keywordsField.getText();
		for (String s : keywordList) {
			if (!keywords.contains(s))
				keywords += s + ", ";
		}

		keywordsField.setText(keywords);
	}

	/**
	 * Set the mail's content to the give text
	 * 
	 * @param text
	 *            as a String
	 */
	public void setMailContent(String text) {
		String s = contentField.getText();
		contentField.setText(s + "\n" + text);
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
		return storedAccounts.toDisplay();
	}
	
	private void buttonsBar(Composite outerView) {
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.verticalAlignment = GridData.FILL;
		gd.grabExcessVerticalSpace = true;
		
		
		buttonBar = new CoolBar(outerView, SWT.HORIZONTAL);
		
		
		CoolItem buttonItem = new CoolItem(buttonBar, SWT.NONE | SWT.DROP_DOWN);
		
		Composite composite = new Composite(buttonBar, SWT.NONE);
	    composite.setLayout(new GridLayout(3, true));
	    
	    
	    sendButton = new Button(composite, SWT.PUSH | SWT.FLAT);
		sendButton.setText("Send");
		sendButton.pack();
		
/*		attachButton = new Button(composite, SWT.PUSH | SWT.FLAT);
		attachButton.setText("Attach");
		attachButton.pack();
		
		attachList = new org.eclipse.swt.widgets.List(composite, SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL | SWT.H_SCROLL);
		attachList.setLayoutData(gd);
		attachList.pack(); */
		
		
		composite.pack();
		
		Point size = composite.getSize();
	    buttonItem.setControl(composite);
	    buttonItem.setSize(buttonItem.computeSize(size.x, size.y));
	}
	


	@Override
	public void createPartControl(Composite parent) {
		// style for all the page
		Composite allView = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		GridData gd1 = new GridData();
		gd1.horizontalAlignment = GridData.FILL;
		gd1.grabExcessHorizontalSpace = true;
		gd1.verticalAlignment = GridData.FILL;
		gd1.grabExcessVerticalSpace = true;
		allView.setLayout(layout);
		allView.setLayoutData(gd1);

		// Buttons
		this.buttonsBar(allView);

		// style for the headers
		Group headers = new Group(allView, SWT.SHADOW_ETCHED_IN);
		GridLayout headersLayout = new GridLayout(2, false);
		headers.setLayout(headersLayout);
		GridData gridLeft = new GridData();
		gridLeft.horizontalAlignment = GridData.FILL;
		gridLeft.grabExcessHorizontalSpace = false;
		GridData gridRight = new GridData();
		gridRight.horizontalAlignment = GridData.FILL;
		gridRight.grabExcessHorizontalSpace = true;
		GridData hd = new GridData();
		hd.horizontalAlignment = GridData.FILL;
		hd.grabExcessHorizontalSpace = true;
		hd.verticalAlignment = GridData.BEGINNING;
		hd.grabExcessVerticalSpace = false;
		headers.setLayoutData(hd);
		// from
		Label from = new Label(headers, SWT.None);
		from.setText("From: ");
		from.setLayoutData(gridLeft);
		fromField = new Combo(headers, SWT.SINGLE);
		fromField.setLayoutData(gridRight);
		fromField.setItems(getArrayAccounts());
		// to
		Label to = new Label(headers, SWT.None);
		to.setText("To: ");
		to.setLayoutData(gridLeft);
		toField = new Text(headers, SWT.SINGLE);
		toField.setLayoutData(gridRight);
		// cc
		Label cc = new Label(headers, SWT.None);
		cc.setText("Cc: ");
		cc.setLayoutData(gridLeft);
		ccField = new Text(headers, SWT.SINGLE);
		ccField.setLayoutData(gridRight);
		// bcc
		Label bcc = new Label(headers, SWT.None);
		bcc.setText("Bcc: ");
		bcc.setLayoutData(gridLeft);
		bccField = new Text(headers, SWT.SINGLE);
		bccField.setLayoutData(gridRight);
		// subject
		Label subj = new Label(headers, SWT.None);
		subj.setText("Subject: ");
		subj.setLayoutData(gridLeft);
		subjectField = new Text(headers, SWT.SINGLE);
		subjectField.setLayoutData(gridRight);

		Group text = new Group(allView, SWT.SHADOW_ETCHED_IN);
		text.setLayout(layout);

		// content field
		contentField = new StyledText(text, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL);
		contentField.setLayoutData(gd1);

		// keyword field
		keywordsField = new StyledText(text, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL);
		keywordsField.setText("Related classes: " + keywords);
		keywordsField.setEditable(false);
		keywordsField.setLayoutData(gridRight);
		keywordsField.setBackground(new Color(parent.getDisplay(), new RGB(255, 255, 204)));

		GridData gdk = new GridData(SWT.DEFAULT, 2 * keywordsField.getLineHeight());
		gdk.horizontalAlignment = GridData.FILL;
		gdk.grabExcessHorizontalSpace = true;
		keywordsField.setLayoutData(gdk);

		GridData gd2 = new GridData();
		gd2.horizontalAlignment = GridData.FILL;
		gd2.grabExcessHorizontalSpace = true;
		gd2.verticalAlignment = GridData.FILL;
		gd2.grabExcessVerticalSpace = true;
		text.setLayoutData(gd2);

		/*
		 * Listeners for buttons
		 */
		sendButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// Collect the data
				String from = fromField.getText();
				String to = toField.getText();
				String cc = ccField.getText();
				String bcc = bccField.getText();
				String subject = subjectField.getText();
				String text = contentField.getText();
				String keys = keywordsField.getText();

				String bodyContent = text + "\n\n\n(Added by REmail)\n" + keys;

				if (EmailChecker.checkFromToParameters(from, to)) {
					EmailSender sender;
					if (EmailChecker.checkCcAndBcc(cc, bcc)) {
						// send email
						sender = new EmailSender(from, to, cc, bcc, subject, bodyContent);

					} else {
						// send email
						sender = new EmailSender(from, to, subject, bodyContent);
					}
					if (attachList != null && (attachList.getItemCount() != 0)) {
						sender.setAttachments(attachList.getItems());
					}
					sender.send();
				} else {
					// display error message
				}
			}
		});

		/*attachButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(new Shell(), SWT.OPEN);
				fd.setText("Add Attachment");
				String selected = fd.open();
				System.out.println(selected);
				attachList.add(selected);
			}
		});*/
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
