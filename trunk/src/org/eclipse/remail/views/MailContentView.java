package org.eclipse.remail.views;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.remail.Mail;
import org.eclipse.remail.util.ContentDecorator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.part.ViewPart;

/**
 * Implements the view showing the content of selected email.
 * 
 * @author vita
 */
public class MailContentView extends ViewPart {
	// not used, can easily be switched if by chance the MOZILLA backend is not
	// available on the target platform
	// public static TextViewer textViewer;

	public static Browser browser; // currently used - shows email as html

	public Image emptyStar;
	public Image fullStar;
	
	public int userRating=0;

	@Override
	public void createPartControl(Composite parent) {
		
	

		URL url1, url2;
		File file1 = null, file2 = null;
		try {
			url1 = new URL("platform:/plugin/org.eclipse.remail/icons/empty-star.png");
			file1 = new File(FileLocator.resolve(url1).toURI());
			url2 = new URL("platform:/plugin/org.eclipse.remail/icons/full-star.png");
			file2 = new File(FileLocator.resolve(url2).toURI());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		emptyStar = new Image(parent.getDisplay(), file1.toString());
		fullStar = new Image(parent.getDisplay(), file2.toString());

		Composite page = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		page.setLayout(layout);
		
		browser = new Browser(page, SWT.WEBKIT);

		Composite buttonContainer = new Composite(page, SWT.NONE);
		GridLayout layout2 = new GridLayout(3, false);
		buttonContainer.setLayout(layout2);
		buttonContainer.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
		// two button
		Group button1 = new Group(buttonContainer, SWT.NONE);
		button1.setText("Global rating");
		final Button buttons[]= new Button[5];
		for (int i = 0; i < 5; i++) {
			buttons[i] = new Button(button1, SWT.TOGGLE);
			buttons[i].setImage(fullStar);
			if(i==4)
				buttons[i].setImage(emptyStar);
			buttons[i].setEnabled(false);
		}
		
		button1.setLayout(new GridLayout(5, true));
		Group button2 = new Group(buttonContainer, SWT.NONE);
		button2.setText("Your rating");
		for (int i = 0; i < 5; i++) {
			final int rating=i+1;
			final Button button = new Button(button2, SWT.TOGGLE);
			button.setImage(emptyStar);
			if(i==0){
				button.setImage(fullStar);
				button.setSelection(true);
			}
			button.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					
					if(button.getImage().equals(fullStar))
					{
						button.setImage(emptyStar);
						userRating=rating-1;
					}
					else{
						button.setImage(fullStar);
						userRating=rating;
					}
					System.out.println(userRating);
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
		}
		button2.setLayout(new GridLayout(5, true));

		Button sendRating = new Button(buttonContainer, SWT.PUSH);
		sendRating.setText("Send Rating");
		
		
		GridData browsLayout = new GridData();
		browsLayout.horizontalAlignment = GridData.FILL;
		browsLayout.grabExcessHorizontalSpace = true;
		browsLayout.verticalAlignment = GridData.FILL;
		browsLayout.grabExcessVerticalSpace = true;
		browser.setLayoutData(browsLayout);

	}

	public void setMail(Mail mail) {
		String text = "";

		// comment the following lines to use the Mozilla browser
		// text = mail.getText();
		// text = text.replaceAll("\\<br/>","\n");
		// text = text.replaceAll("\\<.*?>","");
		// text = StringEscapeUtils.unescapeHtml(text);
		// Document document = new Document(text);
		// textViewer.setDocument(document);

		// uncomment the following to use the Mozilla browser
		ContentDecorator cd = new ContentDecorator(mail);
		cd.highLightPreviousMessages();
		cd.makeHTML();
		cd.insertHeader();
		text = cd.getText();
		browser.setText(text);
		
		System.err.println(mail.getId());

	}

	@Override
	public void setFocus() {
	}

}