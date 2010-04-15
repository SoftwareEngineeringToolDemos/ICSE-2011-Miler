package org.eclipse.remail;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.remail.modules.MailSearch;
import org.eclipse.remail.modules.PostgreSearch;
import org.eclipse.remail.modules.MboxSearch;
import org.eclipse.remail.modules.MboxCore;
import org.eclipse.remail.preferences.PreferenceConstants;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Main class to implement search. It is registered as handler for the package
 * explorer menu extension point. For the actual search in the DB, the
 * PosgreSearch class methods are used.
 * 
 * @author vita
 * 
 */
public class SingleSearch extends AbstractHandler
{
	
	IPreferenceStore store;

	/**
	 * Method to be run automatically after selecting search in the menu. Gets
	 * the selection and initiates chosen search method.
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		store = Activator.getDefault().getPreferenceStore();
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getActiveMenuSelection(event);
		Object firstElement = selection.getFirstElement();
		if (!firstElement.getClass().getName().contains("CompilationUnit"))
		{
			MessageDialog.openInformation(HandlerUtil.getActiveShell(event),
					"Information", "Selected object is not a class");
		} else
		{
			ICompilationUnit cu = (ICompilationUnit) firstElement;
			IResource res = cu.getResource();
			String name = res.getName();
			IPath fullPath = res.getProjectRelativePath();
			Search search = new Search();
			LinkedList<Mail> mailList = search.Execute(name, fullPath.toString(), false);
			Search.updateMailView(mailList);
		}
		return null;
	}

}
