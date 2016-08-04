package org.eclipse.remail.javascriptFunctions;

import org.eclipse.swt.browser.Browser;

/**
 * This is the class responsible of take care of the javascript function call
 * for the function "barSelected"
 * 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 * 
 */
public class BarSelection extends SelectionInterface {

	private static String FUNCTION_NAME = "barSelected";

	public BarSelection(Browser browser, String name, String classname, String path) {
		super(browser, name);
		this.classname = classname;
		this.path = path;
	}


	public static String getFUNCTION_NAME() {
		return FUNCTION_NAME;
	}
}
