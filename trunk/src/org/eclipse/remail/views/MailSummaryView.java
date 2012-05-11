package org.eclipse.remail.views;

import java.io.IOException;

import org.eclipse.remail.Mail;
import org.eclipse.remail.summary.Summary;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class MailSummaryView extends ViewPart {
	
	private static StyledText textArea;
	private static MaxentTagger tagger;

	@Override
	public void createPartControl(Composite parent) {
		
		textArea = new StyledText(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		try {
			tagger = new MaxentTagger("taggers/english-bidirectional-distsim.tagger");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	public void setMail(Mail mail){
		//System.err.println("NEW SUMMARY");
		Summary sm = new Summary(mail, tagger);
		//System.err.println("FATTO NEW SUMMARY");
		textArea.setText(sm.getSummary());
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
