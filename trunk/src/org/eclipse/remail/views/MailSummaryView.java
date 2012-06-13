package org.eclipse.remail.views;

import java.io.IOException;

import org.eclipse.remail.Mail;
import org.eclipse.remail.summary.Summary;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class MailSummaryView extends ViewPart {

	private static StyledText textArea;
	private static MaxentTagger tagger;
	private static Summary summarizer;
	private static Button shorten;

	@Override
	public void createPartControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NONE);
		GridLayout rl = new GridLayout(1, false);
		container.setLayout(rl);
		GridData grid = new GridData();
		grid.grabExcessVerticalSpace=true;
		grid.grabExcessHorizontalSpace=true;
		grid.horizontalAlignment=GridData.FILL;
		grid.verticalAlignment=GridData.FILL;
		Composite big = new Composite(container, SWT.NONE);
		big.setLayout(new GridLayout());
		big.setLayoutData(grid);
		GridData gd1 = new GridData();
		gd1.horizontalAlignment = GridData.FILL;
		gd1.grabExcessHorizontalSpace = true;
		gd1.verticalAlignment = GridData.FILL;
		gd1.grabExcessVerticalSpace = true;
		textArea = new StyledText(big, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		textArea.setLayoutData(gd1);
		try {
			tagger = new MaxentTagger("taggers/english-bidirectional-distsim.tagger");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		// button to visualize the short version of the summary
		shorten = new Button(container, SWT.PUSH);
		shorten.setText("Shorten");
		shorten.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shorten.setEnabled(false);
				textArea.setText(summarizer.getShortSummary());
			}
		});
	}

	public void setMailSummary(Mail mail){
		shorten.setEnabled(true);
		summarizer = new Summary(mail, tagger);
		textArea.setText(summarizer.getSummary());
	}



	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
