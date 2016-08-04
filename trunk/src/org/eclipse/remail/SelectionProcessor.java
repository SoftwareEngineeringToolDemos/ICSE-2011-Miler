package org.eclipse.remail;

import java.util.LinkedList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * This class is listed inside plugin.xml as a handler for the REmail
 * search command, which serves as an initiator of search and is shown in the context (right
 * click) menu of the Package Explorer. Since user can select multiple classes, packages, or the
 * project itself, the task of SelectionProcessor is to produce a list of the actual classes
 * o be submitted to the search. SelectionProcessor then submits the list to the IndexSearch to
 * continue the work.
 * 
 * @author V. Humpa
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

	/**
	 * Sorts out the given selection and launches a new tread controlled by
	 * IndexSearch instance
	 * @param selection
	 */
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

	/**
	 * Retrieves the list of classes to be searched against from a package
	 * @param packageFragment
	 * @param compList
	 */
	private void packageSearch(IPackageFragment packageFragment,
			LinkedList<ICompilationUnit> compList)
	{
		ICompilationUnit[] compilationUnits = null;
		try
		{
			compilationUnits = packageFragment.getCompilationUnits();
		} catch (JavaModelException e)
		{
			e.printStackTrace();
		}
		for (ICompilationUnit cu : compilationUnits)
		{
			compList.add(cu);
		}
	}
	
	/**
	 * Retrieves the list of classes to be searched against from an entire project
	 * @param javaProject
	 * @return
	 */
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
