package org.eclipse.remail.javascriptFunctions;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.remail.util.LocalMailListSearchOnDate;
import org.eclipse.remail.views.MailView;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

/**
 * This is the class responsible of take care of the javascript function call
 * for the function "barSelected"
 * 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 * 
 */
public class BarSelection extends BrowserFunction {

	private static String FUNCTION_NAME = "barSelected";

	public BarSelection(Browser browser, String name) {
		super(browser, name);
	}

	/**
	 * Search in the list of mails something corresponding to the data passed as
	 * argument.
	 * It build the regular expession as "<month> .* <year>" 
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
		final String regex= month+" .* "+year;
		
//		System.err.println(month+" "+year);
		
		//do the search
		Runnable thread = new Runnable() {
			
			@Override
			public void run() {
				boolean set = false;
				TreeViewer tree=null;
				while (!set) {
					tree = MailView.getViewer();
					if(tree!=null)
						set=true;
					else{
						try {
							System.err.println("sleeping");
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				LocalMailListSearchOnDate search = new LocalMailListSearchOnDate(tree, regex);
				search.search();
			}
		};
		thread.run();
		
		return null;
	}

	public static String getFUNCTION_NAME() {
		return FUNCTION_NAME;
	}
}
