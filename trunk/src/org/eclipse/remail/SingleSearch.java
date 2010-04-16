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
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.remail.modules.MailSearch;
import org.eclipse.remail.modules.PostgreSearch;
import org.eclipse.remail.modules.MboxSearch;
import org.eclipse.remail.modules.MboxCore;
import org.eclipse.remail.modules.ProjectSearch;
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
		System.out.println(firstElement.getClass().getName());
		if (firstElement.getClass().getName().contains("CompilationUnit"))
		{
			ICompilationUnit cu = (ICompilationUnit) firstElement;
			IResource res = cu.getResource();
			String name = res.getName();
			IPath fullPath = res.getProjectRelativePath();
			Search search = new Search();
			LinkedList<Mail> mailList = search.Execute(name, fullPath
					.toString(), false);
			Search.updateMailView(mailList);
		} else if (firstElement.getClass().getName().contains("JavaProject"))
		{
			System.out.println("JP");
			IJavaProject javaProject = (IJavaProject) firstElement;
			this.projectSearch(javaProject);

		} else if (firstElement.getClass().getName().contains("PackageFragment"))
		{
			System.out.println("PF");
			IPackageFragment packageFragment = (IPackageFragment) firstElement;
			this.packageSearch(packageFragment);

		} else
		{
			MessageDialog.openInformation(HandlerUtil.getActiveShell(event),
					"Information", "Selected object is not a class");
		}
		return null;
	}


	private void packageSearch(IPackageFragment packageFragment)
	{
		ICompilationUnit[] compilationUnits = null;
		try
		{
			compilationUnits = packageFragment.getCompilationUnits();
		} catch (JavaModelException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LinkedList<ICompilationUnit> compList = new LinkedList<ICompilationUnit>();
		for (ICompilationUnit cu : compilationUnits)
		{
			compList.add(cu);
		}
		Thread thr = new Thread(new ProjectSearch(packageFragment.getResource(), compList));
		thr.start();
	}


	public void projectSearch(IJavaProject javaProject)
	{
		IPackageFragment[] packageFragments = null;
		try
		{
			LinkedList<ICompilationUnit> compList = new LinkedList<ICompilationUnit>();
			packageFragments = javaProject.getPackageFragments();
			for (IPackageFragment pf : packageFragments)
			{
				// IResource pfres = pf.getResource();
				// System.out.println(pfres.getName());
				ICompilationUnit[] compilationUnits = pf.getCompilationUnits();
				for (ICompilationUnit cu : compilationUnits)
				{
					// IResource cures = cu.getResource();
					// String name = cures.getName();
					// System.out.println(name);
					compList.add(cu);
				}
			}
			Thread thr = new Thread(new ProjectSearch(javaProject.getResource(), compList));
			thr.start();
		} catch (JavaModelException e)
		{
			e.printStackTrace();

		}
	}

}
