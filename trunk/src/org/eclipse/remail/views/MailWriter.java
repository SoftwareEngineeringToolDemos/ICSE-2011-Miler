package org.eclipse.remail.views;

import java.util.List;

import org.apache.commons.collections.functors.IfClosure;
import org.eclipse.remail.emails.EmailChecker;
import org.eclipse.remail.emails.EmailSender;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

/**
 * Class used to provide to the user a nice interface for writing e-mails
 * 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 * 
 */
public class MailWriter extends ViewPart {

	// Buttons
	private Button sendButton;
	private Button attachButton;

	// Text fields
	private Text fromField;
	private Text toField;
	private Text ccField;
	private Text bccField;
	private Text subjectField;
	private StyledText contentField;
	private StyledText keywordsField;

	// keyword list
	private String keywords;

	private CoolBar buttonBar;

	/**
	 * Empty constructor. Should never be used explicitly
	 */
	public MailWriter() {
		super();
		keywords = "";
	}

	public MailWriter(List<String> keywordList) {
		super();
		keywords = "";
		for (String s : keywordList)
			keywords += ", " + s;
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
		buttonBar = new CoolBar(allView, SWT.HORIZONTAL);
		CoolItem sendItem = new CoolItem(buttonBar, SWT.NONE);
		sendButton = new Button(buttonBar, SWT.PUSH | SWT.FLAT);
		sendButton.setText("Send");
		sendButton.pack();
		Point size = sendButton.getSize();
		sendItem.setControl(sendButton);
		sendItem.setSize(size.x * 2, size.y);
		CoolItem attachItem = new CoolItem(buttonBar, SWT.NONE);
		attachButton = new Button(buttonBar, SWT.PUSH | SWT.FLAT);
		attachButton.setText("Attach");
		attachButton.pack();
		size = attachButton.getSize();
		attachItem.setControl(attachButton);
		attachItem.setSize(size.x * 2, size.y);

		// style for the headers
		Group headers = new Group(allView, SWT.SHADOW_ETCHED_IN);
		GridLayout headersLayout = new GridLayout(2, false);
		headers.setLayout(headersLayout);
		GridData gridLeft = new GridData();
		gridLeft.horizontalAlignment = GridData.BEGINNING;
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
		fromField = new Text(headers, SWT.SINGLE);
		fromField.setLayoutData(gridRight);
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
		keywordsField.setText("Keywords: " + keywords);
		keywordsField.setEditable(false);
		keywordsField.setLayoutData(gridRight);
		keywordsField.setBackground(new Color(parent.getDisplay(), new RGB(255, 255, 204)));

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
					// send email
					EmailSender sender = new EmailSender(from, to, subject, bodyContent);
					sender.send();
				} else {
					// display error message
				}
			}
		});
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
