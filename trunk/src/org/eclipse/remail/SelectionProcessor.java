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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.remail.modules.MailSearch;
import org.eclipse.remail.modules.PostgreSearch;
import org.eclipse.remail.modules.MboxSearch;
import org.eclipse.remail.modules.MboxCore;
import org.eclipse.remail.preferences.PreferenceConstants;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPartSite;
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
public class SelectionProcessor extends AbstractHandler
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
		this.launchSearch(selection);
		return null;
	}

	private void launchSearch(IStructuredSelection selection)
	{
		LinkedList<ICompilationUnit> compList = new LinkedList<ICompilationUnit>();
		for (Object sel : selection.toList())
		{

			if (sel.getClass().getName().contains("CompilationUnit"))
			{
				ICompilationUnit cu = (ICompilationUnit) sel;
				compList.add(cu);
			} else if (sel.getClass().getName().contains("JavaProject"))
			{
				IJavaProject javaProject = (IJavaProject) sel;
				compList = this.projectSearch(javaProject);
				break;
			} else if (sel.getClass().getName().contains("PackageFragment"))
			{
				IPackageFragment packageFragment = (IPackageFragment) sel;
				this.packageSearch(packageFragment, compList);

			} else
			{
				// MessageDialog.openInformation(
				// HandlerUtil.getActiveShell(event), "Information",
				// "Selected object is not a class");
			}
		}
		IWorkbenchPartSite site = Activator.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActivePart()
				.getSite();
		IViewSite vsite = (IViewSite) site;
		IActionBars bars = vsite.getActionBars();
		IStatusLineManager statusLine = bars.getStatusLineManager();
		IProgressMonitor pm = statusLine.getProgressMonitor();
		Thread thr = new Thread(new IndexSearch(compList, pm));
		thr.start();
	}

	private void packageSearch(IPackageFragment packageFragment,
			LinkedList<ICompilationUnit> compList)
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
		for (ICompilationUnit cu : compilationUnits)
		{
			compList.add(cu);
		}
	}

	public LinkedList<ICompilationUnit> projectSearch(IJavaProject javaProject)
	{
		IPackageFragment[] packageFragments = null;
		LinkedList<ICompilationUnit> compList = new LinkedList<ICompilationUnit>();
		try
		{
			packageFragments = javaProject.getPackageFragments();
			for (IPackageFragment pf : packageFragments)
			{
				ICompilationUnit[] compilationUnits = pf.getCompilationUnits();
				for (ICompilationUnit cu : compilationUnits)
				{
					compList.add(cu);
				}
			}
		} catch (JavaModelException e)
		{
			e.printStackTrace();

		}
		return compList;
	}

}
