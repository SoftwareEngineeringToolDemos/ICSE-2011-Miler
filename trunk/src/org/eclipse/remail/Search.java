package org.eclipse.remail;

import java.util.LinkedList;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.remail.modules.MailSearch;
import org.eclipse.remail.modules.MboxSearch;
import org.eclipse.remail.modules.PostgreSearch;
import org.eclipse.remail.preferences.PreferenceConstants;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class Search
{
	public MailSearch search = null;
	IPreferenceStore store;

	public Search()
	{
		store = Activator.getDefault().getPreferenceStore();
	}

	public LinkedList<Mail> Execute(String name, String path, Boolean hidden)
	{
		LinkedList<Mail> mailList = null;
		name = name.split("\\.")[0]; // gets the filename without the
		// extension
		try
		{
			if (store.getString(PreferenceConstants.P_SOURCE) == "postgre")
				search = (PostgreSearch) new PostgreSearch("jdbc:postgresql://"
						+ store.getString(PreferenceConstants.P_POSTGRE_SERVER)
						+ ":"
						+ store.getString(PreferenceConstants.P_POSTGRE_PORT)
						+ "/"
						+ store.getString(PreferenceConstants.P_POSTGRE_DB),
						store.getString(PreferenceConstants.P_POSTGRE_LOGIN),
						store.getString(PreferenceConstants.P_POSTGRE_PASSWORD));
			else if (store.getString(PreferenceConstants.P_SOURCE) == "mbox")
				search = (MboxSearch) new MboxSearch();
			if (!hidden)
				try
				{
					PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage().showView(
									"org.eclipse.emailrecommender.MailView");
					PlatformUI
							.getWorkbench()
							.getActiveWorkbenchWindow()
							.getActivePage()
							.showView(
									"org.eclipse.emailrecommender.MailContentView");
				} catch (PartInitException e)
				{
					MessageDialog.openError(null, "Error",
							"Error in showing the view: " + e.getMessage());
					e.printStackTrace();
				}
			String method = store.getString(PreferenceConstants.P_METHOD);
			
			if (method.contains("searchSensitive"))
				mailList = search.caseSensitiveSearch(name);
			else if (method.contains("searchInsensitive"))
				mailList = search.caseInsensitiveSearch(name);
			else if (method.contains("searchStrict"))
				mailList = search.strictRegexpSearch(path, name);
			else if (method.contains("searchLoose"))
				mailList = search.looseRegexpSearch(path, name);
			else if (method.contains("searchDict"))
				mailList = search.dictionarySearch(path, name);
			else if (method.contains("searchCamel"))
				mailList = search.camelCaseSearch(path, name);
		} catch (Exception e)
		{
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
	public static IViewPart getView(String id)
	{
		IViewReference viewReferences[] = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getViewReferences();
		for (int i = 0; i < viewReferences.length; i++)
		{
			if (id.equals(viewReferences[i].getId()))
			{
				return viewReferences[i].getView(false);
			}
		}
		return null;
	}

	/**
	 * Convenience method which sets the input of the MailView view
	 * 
	 * @param mailList
	 */
	public static void updateMailView(LinkedList<Mail> mailList)
	{
		/*
		 * if (mailList.size() == 0) MessageDialog.openInformation(null,
		 * "Sorry", "No e-mails found."); else
		 * MessageDialog.openInformation(null, "Result", mailList.size() +
		 * " e-mails found.");
		 */
		MailView.getViewer().setInput(mailList);
	}
}
