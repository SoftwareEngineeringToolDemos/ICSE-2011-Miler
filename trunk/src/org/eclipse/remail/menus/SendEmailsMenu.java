package org.eclipse.remail.menus;

import java.util.ArrayList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.remail.views.MailWriter;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class SendEmailsMenu extends AbstractHandler {
	
	private static final String ECLIPSE_PACKAGE_IDENTIFIER="org.eclipse.jdt.internal.core.PackageFragment";
	private static final String ECLIPSE_CLASS_IDENTIFIER="org.eclipse.jdt.internal.core.CompilationUnit";
	
	ArrayList<IPackageFragment> packages;
	ArrayList<ICompilationUnit> classes;
	
	ArrayList<String> packageNames;
	ArrayList<String> classNames;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		System.out.println("Send email pressed");
		
		packages=new ArrayList<IPackageFragment>();
		classes=new ArrayList<ICompilationUnit>();

		/*
		 * Get the classes and packages
		 */
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getActiveMenuSelection(event);
		for (Object sel : selection.toList()){
			String name = sel.getClass().getName();
			System.out.println(name);
			//if its a class -> add to classes 
			if(name.trim().equals(ECLIPSE_CLASS_IDENTIFIER)){
				classes.add((ICompilationUnit)sel);
			}
			//if its a package -> add to packages
			if(name.trim().equals(ECLIPSE_PACKAGE_IDENTIFIER)){
				packages.add((IPackageFragment)sel);
			}
		}
		
		/*
		 * Extract names from packages and classes
		 */
		extracNamesPackages();
		extractNamesClasses();
		
		/*
		 * Open the MailWriter and set the keywords 
		 */
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
			.showView("org.eclipse.emailrecommender.MailWriter");
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(packageNames);
		list.addAll(classNames);
		MailWriter mw =(MailWriter) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView("org.eclipse.emailrecommender.MailWriter");
		mw.setKeywords(list);
		
		return null;
	}

	private void extractNamesClasses() {
		classNames=new ArrayList<String>();
		for(ICompilationUnit u : classes){	
			String s=u.getParent().getElementName()+"."+u.getElementName();
			//System.out.println(s);
			classNames.add(s);
		}
	}

	private void extracNamesPackages() {
		packageNames=new ArrayList<String>();
		for(IPackageFragment p : packages){
			//System.out.println(p.getElementName());
			packageNames.add(p.getElementName());
		}
	}

}
