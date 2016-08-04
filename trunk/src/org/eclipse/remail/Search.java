package org.eclipse.remail;

import java.util.Calendar;
import java.util.LinkedList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.remail.couchdb.util.CouchDBSearch;
import org.eclipse.remail.modules.MailSearch;
import org.eclipse.remail.modules.MboxSearch;
import org.eclipse.remail.modules.PostgreSearch;
import org.eclipse.remail.preferences.PreferenceConstants;
import org.eclipse.remail.views.MailView;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * Class through which the search is organized. Execute() method utilizes one of
 * the search modules from the modules package to provide search with chosen
 * source of e-mails. This is in context of searching for a single compilation
 * unit only. Index search is responsible in instantiating this class separately
 * for each compilation unit in question.
 * 
 * @author V. Humpa
 * 
 */
public class Search {
	public MailSearch search = null;
	IPreferenceStore store;

	public Search() {
		store = Activator.getDefault().getPreferenceStore();
	}

	/**
	 * Utilizes one of the search modules from the modules package to provide
	 * search on chosen source of e-mails and in fact runs the search.
	 * 
	 * @param name
	 * @param path
	 * @param hidden
	 * @return filtered list
	 */
	public LinkedList<Mail> Execute(String name, String path, Boolean hidden) {
		LinkedList<Mail> mailList = null;
		name = name.split("\\.")[0]; // gets the filename without the extension
		try {
			if (store.getString(PreferenceConstants.P_SOURCE) == "postgre")
				search = (PostgreSearch) new PostgreSearch("jdbc:postgresql://"
						+ store.getString(PreferenceConstants.P_POSTGRE_SERVER) + ":"
						+ store.getString(PreferenceConstants.P_POSTGRE_PORT) + "/"
						+ store.getString(PreferenceConstants.P_POSTGRE_DB),
						store.getString(PreferenceConstants.P_POSTGRE_LOGIN),
						store.getString(PreferenceConstants.P_POSTGRE_PASSWORD));
			else if (store.getString(PreferenceConstants.P_SOURCE) == "mbox")
				search = (MboxSearch) new MboxSearch();
			else if (store.getString(PreferenceConstants.P_SOURCE).equals("couchdb"))
				search = (CouchDBSearch) new CouchDBSearch();
			if (!hidden)
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
							.showView("org.eclipse.emailrecommender.MailView");
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
							.showView("org.eclipse.emailrecommender.MailContentView");
				} catch (PartInitException e) {
					MessageDialog.openError(null, "Error",
							"Error in showing the view: " + e.getMessage());
					e.printStackTrace();
				}
			String method = store.getString(PreferenceConstants.P_METHOD);
//			System.out.println(method+" "+store.getString(PreferenceConstants.P_SOURCE));
			Calendar now = Calendar.getInstance();
			Long startExecution = now.getTimeInMillis();

			if (method.contains("searchSensitive"))
				mailList = search.caseSensitiveSearch(path, name);
			else if (method.contains("searchInsensitive"))
				mailList = search.caseInsensitiveSearch(path, name);
			else if (method.contains("searchStrict"))
				mailList = search.strictRegexpSearch(path, name);
			else if (method.contains("searchLoose"))
				mailList = search.looseRegexpSearch(path, name);
			else if (method.contains("searchDict"))
				mailList = search.dictionarySearch(path, name);
			else if (method.contains("searchCamel"))
				mailList = search.camelCaseSearch(path, name);

			now = Calendar.getInstance();
			Long endExecution = now.getTimeInMillis();
			System.out
					.println("start time: " + startExecution + "\n" + "end time: " + endExecution);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mailList;
	}

	/**
	 * Returns the shown view instance with given id. Currently, the method is
	 * not used.
	 * 
	 * @param id
	 *            - eg. "org.eclipse.emailrecommender.MailContentView"
	 * @return - the view instance. Null, if the view is not currently opened.
	 */
	public static IViewPart getView(String id) {
		IViewReference viewReferences[] = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().getViewReferences();
		for (int i = 0; i < viewReferences.length; i++) {
			if (id.equals(viewReferences[i].getId())) {
				return viewReferences[i].getView(false);
			}
		}
		return null;
	}

	/**
	 * Gives true, if string "what" matches one of the filters specified in
	 * preferences.
	 * 
	 * @param store
	 * @param what
	 * @param where
	 *            - If the value is 1, subject filters are used, if 2, author
	 *            filters are used.
	 * @return
	 */
	private static Boolean matchAgainstFilters(IPreferenceStore store, String what, int where) {
		String filter = null;
		if (where == 1)
			filter = store.getString(PreferenceConstants.P_FILTER_SUBJECT);
		else if (where == 2)
			filter = store.getString(PreferenceConstants.P_FILTER_AUTHOR);
		String[] filters = filter.split(";");
		for (String fltr : filters) {
			if (!fltr.matches(""))
				if (what.contains(fltr))
					return true;
		}
		return false;
	}

	/**
	 * Submits the list of Mail objects to the currently active filters and
	 * returns new list that have been filtered accordingly
	 * 
	 * @param mailList
	 * @return
	 */
	public static LinkedList<Mail> applyMessageFilters(LinkedList<Mail> mailList) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		if (store.getString(PreferenceConstants.P_FILTER_SUBJECT).matches("")
				&& store.getString(PreferenceConstants.P_FILTER_AUTHOR).matches(""))
			return mailList;
		LinkedList<Mail> filteredMailList = new LinkedList<Mail>();
		for (Mail mail : mailList) {
			Boolean putInList = true;
			Boolean hit = matchAgainstFilters(store, mail.getSubject(), 1);
			if (store.getString(PreferenceConstants.P_FILTER_SUBJECT_EXCLUDE).contains("excluded")) {
				if (hit)
					putInList = false;
			} else {
				if (hit)
					putInList = true;
				else
					putInList = false;
			}
			hit = matchAgainstFilters(store, mail.getAuthor(), 2);
			if (store.getString(PreferenceConstants.P_FILTER_AUTHOR_EXCLUDE).contains("excluded")) {
				if (hit)
					putInList = false;
			} else {
				if (hit)
					putInList = true;
				else
					putInList = false;
			}
			if (putInList)
				filteredMailList.add(mail);
		}
		return filteredMailList;
	}

	/**
	 * Convenience method which sets the input of the MailView view
	 * 
	 * @param mailList
	 */
	public static void updateMailView(final LinkedList<Mail> mailList) {
		boolean set = false;

		while (!set) {
			final TreeViewer tree = MailView.getViewer();
			if (tree != null) {
				set = true;
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						tree.setInput(mailList);
					}
				});
				break;
			} else {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
