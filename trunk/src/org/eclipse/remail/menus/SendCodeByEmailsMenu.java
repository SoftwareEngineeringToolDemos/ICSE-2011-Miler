package org.eclipse.remail.menus;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;


/**
 * Class used to add a code snippet to the context of the mail.
 * Is the class responsible for the behavior of the command "Send code by Email" in the
 * pop-up menu in the eclipse Editor.
 * 
 * It creates a MailWriter class and set the keywords relatives to the active class in the editor
 * and add the code to the email body.
 * 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 *
 */
public class SendCodeByEmailsMenu extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		System.out.println("Send code by mail");
		System.out.println(event.toString());
		
		
		return null;
	}
	
}
