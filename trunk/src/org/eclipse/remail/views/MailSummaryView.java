package org.eclipse.remail.views;

import org.eclipse.remail.Mail;
import org.eclipse.remail.summary.Summary;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class MailSummaryView extends ViewPart {
	
	private static StyledText textArea;

	@Override
	public void createPartControl(Composite parent) {
		
		textArea = new StyledText(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);

	}
	
	public void setMail(Mail mail){
		Summary sm = new Summary(mail);
		textArea.setText(sm.getSummary());
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
