package org.eclipse.remail.views;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedHashSet;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.remail.Activator;
import org.eclipse.remail.couchdb.helper.CouchDBMethodName;
import org.eclipse.remail.couchdb.util.CouchDBCreator;
import org.eclipse.remail.couchdb.util.CouchDBSearch;
import org.eclipse.remail.daemons.ChangeViewDaemon;
import org.eclipse.remail.javascriptFunctions.BarSelection;
import org.eclipse.remail.preferences.PreferenceConstants;
import org.eclipse.remail.properties.MailingList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

/**
 * This class is used to have a view some visualization
 * 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 * 
 */
public class VisualizationView extends ViewPart {

	public static Browser browser;
	public static final String ECLIPSE_HOME = System.getProperty("eclipse.home.location")
			.substring(0, System.getProperty("eclipse.home.location").length() - 1)
			.replace("file:", "");

	public static final String SCRIPT_NAME = "plotChartForClass";
	public static final String HTML_PAGE_LOCATION = "platform:/plugin/org.eclipse.remail/visualization/chart.html";

	private BarSelection barSelectionJavaScript;

	@Override
	public void createPartControl(Composite parent) {

		browser = new Browser(parent, SWT.WEBKIT);

		new CustomFunction(browser, "myJavaFunction");
		// get the name of the class to search
		String classname = getSite().getWorkbenchWindow().getActivePage().getActiveEditor()
				.getTitle();
		// get the path of the class to search
		String path = ChangeViewDaemon.getPath(getSite().getWorkbenchWindow().getActivePage()
				.getActiveEditor().getEditorInput().getPersistable().toString());
		barSelectionJavaScript = new BarSelection(browser, BarSelection.getFUNCTION_NAME(),
				classname, path);

		setBrowserUrl();
		/**
		 * execute a javascript script when browser have finished to load the
		 * page
		 */
		browser.addProgressListener(new ProgressListener() {

			@Override
			public void completed(ProgressEvent event) {
				// System.err.println("Reloading");
				String fun;
				try {
					fun = getJavascriptFunctionToCall();
					if (fun != null)
						browser.execute(fun);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void changed(ProgressEvent event) {
				// TODO Auto-generated method stub

			}
		});

		// Add a listener which updates the browser when the selection changes
		getSite().getWorkbenchWindow().getSelectionService()
				.addSelectionListener(new ISelectionListener() {

					@Override
					public void selectionChanged(IWorkbenchPart part, ISelection selection) {
						setBrowserUrl();
					}
				});

	}

	/**
	 * Set the url to open
	 */
	protected void setBrowserUrl() {
		try {
			URL url = new URL(HTML_PAGE_LOCATION);
			File file = new File(FileLocator.resolve(url).toURI());
			String path = file.getAbsolutePath();
			browser.setUrl("file://" + path);
			// get the name of the class to search
			String classname = getSite().getWorkbenchWindow().getActivePage().getActiveEditor()
					.getTitle();
			// get the path of the class to search
			String pathClass = ChangeViewDaemon
					.getPath(getSite().getWorkbenchWindow().getActivePage().getActiveEditor()
							.getEditorInput().getPersistable().toString());
			barSelectionJavaScript = new BarSelection(browser, BarSelection.getFUNCTION_NAME(),
					classname, pathClass);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setFocus() {

	}

	/**
	 * A class representing a function which will be called from javascript
	 */
	class CustomFunction extends BrowserFunction {

		CustomFunction(Browser browser, String name) {
			super(browser, name);
		}

		public Object function(Object[] arguments) {
			System.out.println("You have pressed a button \n" + arguments[0]);
			return null;
		}
	}

	/**
	 * Return a string representing the javascript function call to make the
	 * plot
	 * 
	 * @return a string like plotChartForClass('<classname>')
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 */
	public String getJavascriptFunctionToCall() throws MalformedURLException, URISyntaxException,
			IOException {
		try {
			// get the name of the class to search
			String classname = getSite().getWorkbenchWindow().getActivePage().getActiveEditor()
					.getTitle();
			// get the server
			String serv = Activator.getHost();
			// get the port
			String port = Activator.getPort();
			// get the database
			CouchDBSearch search = new CouchDBSearch();
			String p = getSite().getWorkbenchWindow().getActivePage().getActiveEditor()
					.getTitleToolTip();
			p = p.substring(p.indexOf("/") + 1);
			LinkedHashSet<MailingList> maillist = search.checkClassBelongsProject(p);
			String listOfdbNames = "[";
			boolean first = true;
			for (MailingList ml : maillist) {
				String dbname = ml.getLocation().replace(".", "_");
				dbname = dbname.replace("@", "-");
				if (!dbname.startsWith(CouchDBCreator.PREFIX))
					dbname = CouchDBCreator.PREFIX + dbname;
				if (first)
					listOfdbNames += "'" + dbname + "'";
				else
					listOfdbNames += ", '" + dbname + "'";
			}
			listOfdbNames += "]";
			// get the searchMethod
			String method = Activator.getDefault().getPreferenceStore()
					.getString(PreferenceConstants.P_METHOD);
			String searchmethod = "";
			if (method.contains("searchSensitive"))
				searchmethod = CouchDBMethodName.CASE_SENSITIVE.getName();
			else if (method.contains("searchInsensitive"))
				searchmethod = CouchDBMethodName.CASE_INSENSITIVE.getName();
			else if (method.contains("searchStrict"))
				searchmethod = CouchDBMethodName.SEARCH_STRICT.getName();
			else if (method.contains("searchLoose"))
				searchmethod = CouchDBMethodName.SEARCH_LOSE.getName();
			else if (method.contains("searchDict"))
				searchmethod = CouchDBMethodName.SEARCH_DICT.getName();
			else if (method.contains("searchCamel"))
				searchmethod = CouchDBMethodName.SEARCH_CAMEL.getName();
			// System.err.println(classname);
			classname = classname.split("\\.")[0];
			String s = SCRIPT_NAME + "('" + classname + "'," + " '" + serv + "'," + " '" + port
					+ "'," + " " + listOfdbNames + "," + " '" + searchmethod + "')";
			// System.err.println(s);
			return s;
		} catch (NullPointerException e) {
			// Eclipse is not jet ready
			return null;
		}
	}
}
