package org.eclipse.remail;

import java.sql.SQLException;

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.remail.modules.PostgreCore;
import org.eclipse.remail.preferences.PreferenceConstants;
import org.eclipse.remail.util.ContentDecorator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite; //import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.*; //import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.swt.browser.*;

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

	@Override
	public void createPartControl(Composite parent) {
		browser = new Browser(parent, SWT.MOZILLA);
		// uncomment to use the jFace text viewer instead of the mozilla browser

		// textViewer = new TextViewer(parent, SWT.MULTI | SWT.V_SCROLL |
		// SWT.WRAP);
		// textViewer.setEditable(false);

	}

	public static void setMail(Mail mail) {
		String text = "";
		if (Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_SOURCE) == "postgre") {
			PostgreCore search = new PostgreCore(Activator.connString,
					Activator.login, Activator.password);
			try {
				text = search.getMailTextFromDB(mail.getId());
			} catch (SQLException e) {
				MessageDialog.openError(null, "Error", "SQL error: "
						+ e.getMessage());
				e.printStackTrace();
				return;
			}
			mail.setText(text);
		} 
		
		ContentDecorator cd = new ContentDecorator(mail);
		cd.highLightPreviousMessages();
		cd.makeHTML();
		cd.insertHeader();		
		text = cd.getText();
		
		browser.setText(text);

	}

	@Override
	public void setFocus() {
	}

}
