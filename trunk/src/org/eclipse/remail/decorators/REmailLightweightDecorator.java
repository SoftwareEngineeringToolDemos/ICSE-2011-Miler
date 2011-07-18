package org.eclipse.remail.decorators;

import java.util.LinkedList;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.remail.Activator;
import org.eclipse.remail.Mail;
import org.eclipse.remail.couchdb.util.CouchDBSearch;
import org.eclipse.remail.modules.MailSearch;
import org.eclipse.remail.preferences.PreferenceConstants;
import org.eclipse.remail.util.CacheCouchDB;


/**
 * Class implements creation of package explorer decoration, taking care of the class results numbering
 * @author V. Humpa
 *
 */
public class REmailLightweightDecorator implements ILightweightLabelDecorator
{

	/**
	 * Provides packages explorer decoration. Check RCP manual for
	 * rationale
	 */
	@Override
	public void decorate(Object resource, IDecoration decoration)
	{
		//Comment the following to use the old-style decorator
		IResource res = (IResource) resource;
		if (JavaCore.create(res) instanceof ICompilationUnit){
			//its the decorator for one class
			String name = res.getName();
			name = name.split("\\.")[0];
			if (CacheCouchDB.containsClass(name))
				decoration.addSuffix(" (" + this.getMailList(res).size() + ")");
			else
				decoration.addSuffix(" (not searched) ");
			
		} else if (JavaCore.create(res) instanceof IPackageFragment) {
			//its the decorator for a package
			IPackageFragment pf = (IPackageFragment) JavaCore.create(res);
			ICompilationUnit[] compilationUnits = null;
			LinkedList<Mail> packageMails= new LinkedList<Mail>();
			try
			{
				compilationUnits = pf.getCompilationUnits();
			} catch (JavaModelException e)
			{
				e.printStackTrace();
			}
			boolean all=true;
			int numMail=0;
			for (ICompilationUnit cu : compilationUnits)
			{
				String name = cu.getResource().getName();
				name = name.split("\\.")[0];
				if (!CacheCouchDB.containsClass(name)){
					all=false;
//					System.out.println(pf.getPath().toString()+" not found "+name);
				}
				else{
					//get the mail for a class
					LinkedList<Mail> classList = this.getMailList(cu.getResource());
					//add to the package's list
					packageMails=Mail.mergeSortMailLists(packageMails, classList);
				}
			}
//			System.out.println(pf.getPath().toString()+" "+all+" numMail:"+numMail+" numClass:"+compilationUnits.length);
			numMail=packageMails.size();
			if(all)
				decoration.addSuffix(" ("+numMail+")");
			else
				decoration.addSuffix(" not complete ("+numMail+")");
				
		}
		//Uncomment to to use the old-style decorator
//		IResource res = (IResource) resource;
//		LinkedList<Mail> mailList = new LinkedList<Mail>();
//		if (JavaCore.create(res) instanceof ICompilationUnit)
//		{
//			if((mailList = this.getMailList(res)) != null)
//				decoration.addSuffix(" (" + this.getMailList(res).size() + ")");	
//		}
//		else if (JavaCore.create(res) instanceof IPackageFragment)
//		{
//			IPackageFragment pf = (IPackageFragment) JavaCore.create(res);
//			ICompilationUnit[] compilationUnits = null;
//			try
//			{
//				compilationUnits = pf.getCompilationUnits();
//			} catch (JavaModelException e)
//			{
//				e.printStackTrace();
//			}
//			for (ICompilationUnit m: compilationUnits)
//			{
//				mailList = Mail.mergeMailLists(mailList, this.getMailList(cu.getResource()));
//			}
//			if (mailList != null && compilationUnits.length > 0)
//				decoration.addSuffix(" (" + mailList.size() + ")");	
//		}
	}

	/**
	 * Returns a list of Mails associated with particular kind of resource
	 * @param res
	 * @return
	 */
	private LinkedList<Mail> getMailList(IResource res)
	{
		String name = res.getName();
		name = name.split("\\.")[0];
//		IPath path = res.getProjectRelativePath(); //should take the path
		LinkedList<Mail> mailList = new LinkedList<Mail>();
		MailSearch search=null;
		IPreferenceStore store= Activator.getDefault().getPreferenceStore();
		//check the preference to see what method to use
		if (store.getString(PreferenceConstants.P_SOURCE).equals("couchdb"))
			search = (CouchDBSearch) new CouchDBSearch();
		
		//check the method to use for the search
		String method = store.getString(PreferenceConstants.P_METHOD);		
		if (method.contains("searchSensitive"))
			mailList = search.caseSensitiveSearch(name);
		else if (method.contains("searchInsensitive"))
			mailList = search.caseInsensitiveSearch(name);
//		else if (method.contains("searchStrict"))
//			mailList = search.strictRegexpSearch(path, name);
//		else if (method.contains("searchLoose"))
//			mailList = search.looseRegexpSearch(path, name);
//		else if (method.contains("searchDict"))
//			mailList = search.dictionarySearch(path, name);
//		else if (method.contains("searchCamel"))
//			mailList = search.camelCaseSearch(path, name);
		
		/*
		 * Uncomment the following lines to use the SQLite cache.
		 */
//		String path = res.getProjectRelativePath().toString();
//		try
//		{
//			Class.forName("org.sqlite.JDBC");
//		} catch (ClassNotFoundException e)
//		{
//			e.printStackTrace();
//		}
//		Connection conn;
//		try
//		{
//			conn = DriverManager.getConnection("jdbc:sqlite:"
//					+ res.getProject().getLocation().toString()
//					+ File.separator + "remail.db");
//			Statement stat = conn.createStatement();
//			stat.executeUpdate("create table if not exists emails (permalink, subject, date, author, threadlink, text, visible);");
//			stat.executeUpdate("create table if not exists classes (id INTEGER PRIMARY KEY AUTOINCREMENT, name, path);");
//			stat.executeUpdate("create table if not exists hits (id INTEGER, permalink);");
//			name = name.split("\\.")[0];
//			conn.close();
//			SQLiteMailListConstructor mailListConstructor = new SQLiteMailListConstructor(res);
//			try
//			{
//				mailList = mailListConstructor.getResultMailList();
//			} catch (ClassNotFoundException e)
//			{
//				e.printStackTrace();
//			}
//		} catch (SQLException e)
//		{
//			e.printStackTrace();
//		}

//		System.out.println("size "+mailList.size());
		return mailList;
	}

	@Override
	public void addListener(ILabelProviderListener arg0)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object arg0, String arg1)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener arg0)
	{
		// TODO Auto-generated method stub

	}

}
