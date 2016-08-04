package org.eclipse.remail;

import java.io.FileOutputStream;
import java.io.PrintStream;
//import java.sql.Connection;
//import java.sql.Statement;
import java.util.LinkedList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.remail.util.CacheCouchDB;
import org.eclipse.swt.widgets.Display;

/**
 * Based on the given list of classes to search, IndexSearch commits the linking
 * and subsequently indexing process on all of them. It depends on the remail.modules.Search
 * class. IndexSearch needs to manipulate UI to inform the user about the progress of the
 * search, which is the reason why it has been included outside the non-UI modules package.
 * Inside IndexSearch, we use the Eclipse status bar progressMonitor extension to work with
 * the progress bar that notifies users about proceedings of the search.
 * 
 * @author V. Humpa
 */
public class IndexSearch implements Runnable
{
	LinkedList<ICompilationUnit> compList;
	IProject project;
	FileOutputStream out; // declare a file output object
	PrintStream p; // declare a print stream object
//	Connection conn;
	String projectName;
//	Statement stat;
	IProgressMonitor progressMonitor;

	public IndexSearch(LinkedList<ICompilationUnit> compList,
			IProgressMonitor pm)
	{
		this.project = compList.get(0).getResource().getProject();
		this.compList = compList;
		this.projectName = project.getName();
		this.progressMonitor = pm;
	}

	private void searchAll() throws Exception
	{
		try
		{
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					progressMonitor.beginTask("REmail search...", compList
							.size());
				}
			});
			for (ICompilationUnit cu : compList)
			{
				this.searchCompilationUnit(cu);
			}
		} finally
		{
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					progressMonitor.done();
				}
			});
//			conn.close();

			Activator
					.getDefault()
					.getWorkbench()
					.getDecoratorManager()
					.setEnabled(
							"org.eclipse.remail.decorators.REmailLightweightDecorator",
							false);
			Activator
					.getDefault()
					.getWorkbench()
					.getDecoratorManager()
					.setEnabled(
							"org.eclipse.remail.decorators.REmailLightweightDecorator",
							true);
		}
	}

	private void searchCompilationUnit(ICompilationUnit cu)
			throws InterruptedException
	{
		IResource res = cu.getResource();
		String name = res.getName();
		System.out.println(name);
		IPath fullPath = res.getProjectRelativePath();
		System.out.println("Starting search!");
		Search search = new Search();
		LinkedList<Mail> mailList = search.Execute(name, fullPath.toString(),
				true);
		System.out.println("---" + mailList.size() + "---");
		CacheCouchDB.addClass(name);
		
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				progressMonitor.worked(1);
			}
		});
	}

	@Override
	public void run()
	{
		try
		{
			this.searchAll();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
