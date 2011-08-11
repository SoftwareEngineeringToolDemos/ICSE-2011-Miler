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

/**
 * Is the class responsible for the behavior of the command "Send Email" in the
 * pop-up menu in the eclipse Package Explorer.
 * 
 * It creates a MailWriter class and set the keywords relatives to the classes
 * selected in the Package Explorer.
 * 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 * 
 */
public class SendEmailsMenu extends AbstractHandler {

	private static final String ECLIPSE_PACKAGE_IDENTIFIER = "org.eclipse.jdt.internal.core.PackageFragment";
	private static final String ECLIPSE_CLASS_IDENTIFIER = "org.eclipse.jdt.internal.core.CompilationUnit";

	ArrayList<IPackageFragment> packages;
	ArrayList<ICompilationUnit> classes;

	ArrayList<String> packageNames;
	ArrayList<String> classNames;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		System.out.println("Send email pressed");

		packages = new ArrayList<IPackageFragment>();
		classes = new ArrayList<ICompilationUnit>();

		/*
		 * Get the classes and packages
		 */
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getActiveMenuSelection(event);
		for (Object sel : selection.toList()) {
			String name = sel.getClass().getName();
			// if its a class -> add to classes
			if (name.trim().equals(ECLIPSE_CLASS_IDENTIFIER)) {
				classes.add((ICompilationUnit) sel);
			}
			// if its a package -> add to packages
			if (name.trim().equals(ECLIPSE_PACKAGE_IDENTIFIER)) {
				packages.add((IPackageFragment) sel);
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
		MailWriter mw = (MailWriter) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().findView("org.eclipse.emailrecommender.MailWriter");
		mw.setKeywords(list);

		return null;
	}

	/**
	 * Extract the complete class name from the classes list and stores them in
	 * the list classNames
	 */
	private void extractNamesClasses() {
		classNames = new ArrayList<String>();
		for (ICompilationUnit u : classes) {
			String s = u.getParent().getElementName() + "." + u.getElementName();
			classNames.add(s);
		}
	}

	/**
	 * Extract the package name from the packages list and stores them in the
	 * list packagesNames
	 */
	private void extracNamesPackages() {
		packageNames = new ArrayList<String>();
		for (IPackageFragment p : packages) {
			packageNames.add(p.getElementName());
		}
	}

}
