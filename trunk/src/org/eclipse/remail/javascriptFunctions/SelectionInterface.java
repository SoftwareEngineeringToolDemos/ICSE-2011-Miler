package org.eclipse.remail.javascriptFunctions;

import java.util.LinkedList;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.remail.Mail;
import org.eclipse.remail.Search;
import org.eclipse.remail.util.LocalMailListSearchOnDate;
import org.eclipse.remail.views.MailView;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

/**
 * An interface specifying the behavior of a selection done inside the SWT
 * browser
 * 
 * Actually its an abstract class, since interfaces in java cannot have method's
 * body
 * 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 * 
 */
public abstract class SelectionInterface extends BrowserFunction {

	String classname;
	String path;

	public SelectionInterface(Browser browser, String name) {
		super(browser, name);
	}

	public static String getFUNCTION_NAME() {
		return "Static function";
	}

	/**
	 * Search in the list of mails something corresponding to the data passed as
	 * argument. It build the regular expession as "<month> .* <year>"
	 * 
	 * @param arguments
	 *            is the date. Where arguments[0] is the month and arguments[1]
	 *            is the year
	 */
	public Object function(Object[] arguments) {
		// arguments[0] is the month, we need only 3 chars because usually month
		// in email are indicated with 3 chars!
		String month = ((String) arguments[0]).substring(0, 3);
		// arguments[1] is the year
		String year = (String) arguments[1];
		final String regex = month + " .* " + year;

		// System.err.println("'"+month+" "+year+"'");
		// System.err.println("'"+classname+" "+path+"'");

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// updates the view with all the relative mail to the class
				Search search = new Search();
				Search.updateMailView(new LinkedList<Mail>());
				LinkedList<Mail> mailList = search.Execute(classname, path, true);
				if (mailList == null)
					Search.updateMailView(new LinkedList<Mail>());
				else {
					Search.updateMailView(mailList);
					TreeViewer tree = MailView.getViewer();
					tree.setInput(mailList);
				}
			}
		});
		thread.run();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// do the search on local mails
		TreeViewer tree = MailView.getViewer();
		LocalMailListSearchOnDate searchLocal = new LocalMailListSearchOnDate(tree, regex);
		searchLocal.search();

		return null;
	}

}
