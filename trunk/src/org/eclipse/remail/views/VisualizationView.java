package org.eclipse.remail.views;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * This class is used to have a view some visualization
 * 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 *
 */
public class VisualizationView extends ViewPart {

	public static Browser browser;
	
	@Override
	public void createPartControl(Composite parent) {
		
		File file = new File("/home/lorenzobaracchi/Documents/University/UROP-2011/javascript/test.html");
		
		browser = new Browser(parent, SWT.WEBKIT);
		//browser.setUrl("http://mbostock.github.com/d3/ex/force.html");
		//browser.setUrl("http://www.lorenzobaracchi.net78.net/");
		
		new CustomFunction(browser, "myJavaFunction");
		
		System.out.println(file.toURI().toString());
		browser.setUrl("file:///home/lorenzobaracchi/Documents/University/UROP-2011/javascripts/test.html");
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	class CustomFunction extends BrowserFunction {
	 
        CustomFunction (Browser browser, String name) {
            super (browser, name);
        }
        
        public Object function(Object[] arguments){
        	System.out.println("You have pressed a button \n"+ arguments[0]);
        	return null;
        }
	}

}
